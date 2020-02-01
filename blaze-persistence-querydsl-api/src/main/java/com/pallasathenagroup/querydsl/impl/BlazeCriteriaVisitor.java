package com.pallasathenagroup.querydsl.impl;

import com.blazebit.persistence.BaseOngoingSetOperationBuilder;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.DistinctBuilder;
import com.blazebit.persistence.FinalSetOperationCTECriteriaBuilder;
import com.blazebit.persistence.FinalSetOperationCriteriaBuilder;
import com.blazebit.persistence.FinalSetOperationSubqueryBuilder;
import com.blazebit.persistence.From;
import com.blazebit.persistence.FromBaseBuilder;
import com.blazebit.persistence.FromBuilder;
import com.blazebit.persistence.FullQueryBuilder;
import com.blazebit.persistence.FullSelectCTECriteriaBuilder;
import com.blazebit.persistence.GroupByBuilder;
import com.blazebit.persistence.HavingBuilder;
import com.blazebit.persistence.JoinOnBuilder;
import com.blazebit.persistence.JoinType;
import com.blazebit.persistence.LimitBuilder;
import com.blazebit.persistence.MultipleSubqueryInitiator;
import com.blazebit.persistence.ObjectBuilder;
import com.blazebit.persistence.OrderByBuilder;
import com.blazebit.persistence.ParameterHolder;
import com.blazebit.persistence.Queryable;
import com.blazebit.persistence.SelectBaseCTECriteriaBuilder;
import com.blazebit.persistence.SelectBuilder;
import com.blazebit.persistence.SelectCTECriteriaBuilder;
import com.blazebit.persistence.SelectRecursiveCTECriteriaBuilder;
import com.blazebit.persistence.SetOperationBuilder;
import com.blazebit.persistence.SubqueryBuilder;
import com.blazebit.persistence.SubqueryInitiator;
import com.blazebit.persistence.WhereBuilder;
import com.google.common.collect.ImmutableList;
import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.SetOperationImpl;
import com.pallasathenagroup.querydsl.ValuesExpression;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryFlag.Position;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;
import com.querydsl.jpa.JPAQueryMixin;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.JPQLTemplates;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

import static com.pallasathenagroup.querydsl.JPQLNextOps.SET_UNION;
import static com.pallasathenagroup.querydsl.JPQLNextOps.WITH_RECURSIVE_COLUMNS;

public class BlazeCriteriaVisitor<T> extends JPQLSerializer {

    private static final Logger logger = Logger.getLogger(BlazeCriteriaVisitor.class.getName());

    private CriteriaBuilder<T> criteriaBuilder;
    private Queryable<T, ?> queryable;
    private CriteriaBuilderFactory criteriaBuilderFactory;
    private EntityManager entityManager;
    private Map<Object, String> constantToLabel = new IdentityHashMap<>();
    private Map<Expression<?>, String> subQueryToLabel = new IdentityHashMap<>();
    private JPQLTemplates templates;

    public BlazeCriteriaVisitor(CriteriaBuilderFactory criteriaBuilderFactory, EntityManager entityManager, JPQLTemplates templates) {
        super(templates);
        this.criteriaBuilderFactory = criteriaBuilderFactory;
        this.entityManager = entityManager;
        this.templates = templates;
    }

    @Override
    public void serialize(QueryMetadata metadata, boolean forCountRow, @Nullable String projection) {
        QueryModifiers modifiers = metadata.getModifiers();
        Expression<?> select = metadata.getProjection();
        Class<T> type = (Class<T>) select.getType();
        this.criteriaBuilder = criteriaBuilderFactory.create(entityManager, type);


        renderCTEs(metadata);
        renderJoins(metadata, criteriaBuilder);
        renderDistinct(metadata, criteriaBuilder);
        renderWhere(metadata, criteriaBuilder);
        renderGroupBy(metadata, criteriaBuilder);
        renderHaving(metadata, criteriaBuilder);
        renderOrderBy(metadata, criteriaBuilder);
        renderParameters(metadata, criteriaBuilder);
        renderConstants(criteriaBuilder);
        renderModifiers(modifiers, criteriaBuilder);

        if (select instanceof FactoryExpression<?>) {
            FactoryExpression<T> factoryExpression = (FactoryExpression<T>) select;
            criteriaBuilder.selectNew(new FactoryExpressionObjectBuilder(factoryExpression));
        } else {
            renderSingleSelect(select, criteriaBuilder);
        }

    }

    public void serialize(Expression<?> expression) {
        Class<T> type = (Class<T>) expression.getType();
        this.criteriaBuilder = criteriaBuilderFactory.create(entityManager, type);
        this.queryable = (Queryable<T, ?>) serializeSubQuery(this.criteriaBuilder, expression);
    }

    public Object serializeSubQuery(Object criteriaBuilder, Expression<?> expression) {
        Object result = expression.accept(new Visitor<Object, Object>() {
            @Override
            public Object visit(Constant<?> constant, Object criteriaBuilder) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Object visit(FactoryExpression<?> factoryExpression, Object criteriaBuilder) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Object visit(Operation<?> setOperation, Object criteriaBuilder) {
                criteriaBuilder = setOperation.getArg(0).accept(this, criteriaBuilder);
                SetOperationBuilder<?,?> setOperationBuilder = (SetOperationBuilder<?,?>) criteriaBuilder;
                Object setBuilder = null;
                boolean isNestedSet = setOperation.getArg(1) instanceof Operation<?> || setOperation.getArg(1) instanceof SetOperationImpl;
                switch ((JPQLNextOps) setOperation.getOperator()) {
                    case SET_UNION:
                        setBuilder = isNestedSet ?
                                setOperationBuilder.startUnion() : setOperationBuilder.union();
                        break;
                    case SET_UNION_ALL:
                        setBuilder = isNestedSet ?
                                setOperationBuilder.startUnionAll() : setOperationBuilder.unionAll();
                        break;
                    case SET_EXCEPT:
                        setBuilder = isNestedSet ?
                                setOperationBuilder.startExcept() : setOperationBuilder.except();
                        break;
                    case SET_EXCEPT_ALL:
                        setBuilder = isNestedSet ?
                                setOperationBuilder.startExceptAll() : setOperationBuilder.exceptAll();
                        break;
                    case SET_INTERSECT:
                        setBuilder = isNestedSet ?
                                setOperationBuilder.startIntersect() : setOperationBuilder.intersect();
                        break;
                    case SET_INTERSECT_ALL:
                        setBuilder = isNestedSet ?
                                setOperationBuilder.startIntersectAll() : setOperationBuilder.intersectAll();
                        break;
                }
                setBuilder = setOperation.getArg(1).accept(this, setBuilder);
                if (isNestedSet) return ((BaseOngoingSetOperationBuilder<?, ?, ?>) setBuilder).endSet();
                return setBuilder;
            }

            @Override
            public Object visit(ParamExpression<?> paramExpression, Object criteriaBuilder) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Object visit(Path<?> path, Object criteriaBuilder) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Object visit(SubQueryExpression<?> subQuery, Object criteriaBuilder) {
                QueryMetadata subQueryMetadata = subQuery.getMetadata();

                renderCTEs(subQueryMetadata);

                criteriaBuilder = renderJoins(subQueryMetadata, (FromBaseBuilder) criteriaBuilder);
                renderDistinct(subQueryMetadata, (DistinctBuilder<?>) criteriaBuilder);
                renderWhere(subQueryMetadata, (WhereBuilder<?>) criteriaBuilder);
                renderGroupBy(subQueryMetadata, (GroupByBuilder<?>) criteriaBuilder);
                renderHaving(subQueryMetadata, (HavingBuilder<?>) criteriaBuilder);
                renderOrderBy(subQueryMetadata, (OrderByBuilder<?>) criteriaBuilder);
                renderParameters(subQueryMetadata, (ParameterHolder<?>) criteriaBuilder);
                renderConstants((ParameterHolder<?>) criteriaBuilder);
                renderModifiers(subQueryMetadata.getModifiers(), (LimitBuilder<?>) criteriaBuilder);

                Expression<?> select = subQueryMetadata.getProjection();
                if (select instanceof FactoryExpression<?> && criteriaBuilder instanceof FullQueryBuilder<?, ?>) {
                    FactoryExpression<T> factoryExpression = (FactoryExpression<T>) select;
                    FullQueryBuilder<?, ?> fullQueryBuilder = (FullQueryBuilder<?, ?>) criteriaBuilder;
                    fullQueryBuilder.selectNew(new FactoryExpressionObjectBuilder(factoryExpression));

                } else {
                    List<? extends Expression<?>> projection = expandProjection(subQueryMetadata.getProjection());

                    if (criteriaBuilder instanceof SelectBaseCTECriteriaBuilder) {
                        for (int i = 0; cteAliases != null && i < cteAliases.size(); i++) {
                            Path<?> alias = cteAliases.get(i);
                            String aliasString = relativePathString(cteEntityPath, alias);
                            Expression<?> projExpression = projection.get(i);

                            SelectBuilder<?> bindBuilder = ((SelectBaseCTECriteriaBuilder<?>) criteriaBuilder).bind(aliasString);
                            setExpressionSubqueries(projExpression, bindBuilder::select, bindBuilder::selectSubqueries);
                        }
                        cteAliases = null;
                    } else {
                        for (Expression<?> selection : projection) {
                            renderSingleSelect(selection, (SelectBuilder<?>) criteriaBuilder);
                        }
                    }
                }

                return criteriaBuilder;
            }

            @Override
            public Object visit(TemplateExpression<?> templateExpression, Object criteriaBuilder) {
                throw new UnsupportedOperationException();
            }
        }, criteriaBuilder);

        if (result instanceof BaseOngoingSetOperationBuilder) {
            result = ((BaseOngoingSetOperationBuilder<?, ?, ?>) result).endSet();
        }

        if ((result instanceof FinalSetOperationCriteriaBuilder ||
            result instanceof FinalSetOperationCTECriteriaBuilder ||
            result instanceof FinalSetOperationSubqueryBuilder) &&
            expression instanceof SubQueryExpression<?>) {
            QueryMetadata metadata = ((SubQueryExpression<?>) expression).getMetadata();
            renderOrderBy(metadata, (OrderByBuilder<?>) result);
            renderModifiers(metadata.getModifiers(), (LimitBuilder<?>) result);
        }

        return result;
    }


    private void renderCTEs(QueryMetadata subQueryMetadata) {
        for (QueryFlag queryFlag : subQueryMetadata.getFlags()) {
            Expression<?> flag = queryFlag.getFlag();
            Position position = queryFlag.getPosition();
            switch (position) {
                case WITH:
                    flag.accept(BlazeCriteriaVisitor.this, null);
                    break;
            }
        }
    }

    private void renderModifiers(QueryModifiers modifiers, LimitBuilder<?> criteriaBuilder) {
        if (modifiers != null) {
            if (modifiers.getLimitAsInteger() != null) {
                criteriaBuilder.setMaxResults(modifiers.getLimitAsInteger());
            }
            if (modifiers.getOffsetAsInteger() != null) {
                criteriaBuilder.setFirstResult(modifiers.getOffsetAsInteger());
            }
        }
    }

    private void renderConstants(ParameterHolder<?> criteriaBuilder) {
        for (Map.Entry<Object, String> entry : constantToLabel.entrySet()) {
            criteriaBuilder.setParameter(entry.getValue(), entry.getKey());
        }
    }

    private void renderParameters(QueryMetadata metadata, ParameterHolder<?> criteriaBuilder) {
        for (Map.Entry<ParamExpression<?>, Object> entry : metadata.getParams().entrySet()) {
            criteriaBuilder.setParameter(entry.getKey().getName(), entry.getValue());
        }
    }

    private void renderOrderBy(QueryMetadata metadata, OrderByBuilder<?> criteriaBuilder) {
        for (OrderSpecifier<?> orderSpecifier : metadata.getOrderBy()) {
            renderOrderSpecifier(orderSpecifier, criteriaBuilder);
        }
    }

    private void renderHaving(QueryMetadata metadata, HavingBuilder<?> criteriaBuilder) {
        if (metadata.getHaving() != null) {
            setExpressionSubqueries(metadata.getHaving(), criteriaBuilder::havingExpression, criteriaBuilder::havingExpressionSubqueries);
        }
    }

    private void renderGroupBy(QueryMetadata metadata, GroupByBuilder<?> criteriaBuilder) {
        for (Expression<?> groupByExpression : metadata.getGroupBy()) {
            criteriaBuilder.groupBy(renderExpression(groupByExpression));
        }
    }

    private void renderWhere(QueryMetadata metadata, WhereBuilder<?> criteriaBuilder) {
        if (metadata.getWhere() != null) {
            setExpressionSubqueries(metadata.getWhere(), criteriaBuilder::whereExpression, criteriaBuilder::whereExpressionSubqueries);
        }
    }

    private void renderDistinct(QueryMetadata metadata, DistinctBuilder<?> criteriaBuilder) {
        if (metadata.isDistinct()) {
            criteriaBuilder.distinct();
        }
    }

    private <X extends FromBuilder<X>> X renderJoins(QueryMetadata metadata, FromBaseBuilder<X> fromBuilder) {
        X criteriaBuilder = null;
        for (JoinExpression joinExpression : metadata.getJoins()) {
            boolean fetch = joinExpression.hasFlag(JPAQueryMixin.FETCH);
            boolean hasCondition = joinExpression.getCondition() != null;
            Expression<?> target = joinExpression.getTarget();
            String alias = null;

            if (target instanceof Operation<?>) {
                Operation<?> operation = (Operation<?>) target;
                if (operation.getOperator() == Ops.ALIAS) {
                    target = operation.getArg(0);
                    alias = ((Path<?>) operation.getArg(1)).getMetadata().getName();
                }
            }

            if (target instanceof ValuesExpression<?>) {
                ValuesExpression<?> valuesExpression = (ValuesExpression<?>) target;
                Class type = valuesExpression.getType();
                String name = valuesExpression.getMetadata().getName();
                Collection<?> elements = valuesExpression.getElements();
                if ( valuesExpression.isIdentifiable() ) {
                    criteriaBuilder = (X) fromBuilder.fromIdentifiableValues(type, name, elements);
                } else {
                    criteriaBuilder = (X) fromBuilder.fromValues(type, name, elements);
                }
            }
            else if (target instanceof Path<?>) {
                Path<?> entityPath = (Path<?>) target;
                if (alias == null) {
                    alias = entityPath.getMetadata().getName();
                }
                boolean entityJoin = entityPath.getMetadata().isRoot();

                switch (joinExpression.getType()) {
                    case DEFAULT:
                        if (fromBuilder instanceof FromBuilder) {
                            criteriaBuilder = (X) fromBuilder;
                            From from = criteriaBuilder.getFrom(alias);
                            if (from != null) {
                                if (from.getJavaType().equals(entityPath.getType())) {
                                    break;
                                }
                            }
                        }
                        criteriaBuilder = fromBuilder.from(entityPath.getType(), alias);
                        break;
                    default:
                        JoinType joinType = getJoinType(joinExpression);

                        if (hasCondition && fetch) {
                            logger.warning("Fetch is ignored due to on-clause");
                        }

                        if (entityJoin) {
                            if (!hasCondition) {
                                throw new IllegalStateException("No on-clause for entity join!");
                            }
                            JoinOnBuilder<X> xJoinOnBuilder = criteriaBuilder.joinOn(entityPath.getType(), alias, joinType);
                            setExpressionSubqueries(joinExpression.getCondition(), xJoinOnBuilder::setOnExpression, xJoinOnBuilder::setOnExpressionSubqueries);
                        } else if (!hasCondition) {
                            if (fetch) {
                                ((FullQueryBuilder<?, ?>) criteriaBuilder).joinDefault(renderExpression(entityPath), alias, joinType, fetch);
                            } else {
                                criteriaBuilder.joinDefault(renderExpression(entityPath), alias, joinType);
                            }
                        } else {
                            JoinOnBuilder<X> xJoinOnBuilder = criteriaBuilder.joinOn(renderExpression(entityPath), alias, joinType);
                            setExpressionSubqueries(joinExpression.getCondition(), xJoinOnBuilder::setOnExpression, xJoinOnBuilder::setOnExpressionSubqueries);
                        }

                        break;
                }

            } else if (target instanceof SubQueryExpression)  {
                switch (joinExpression.getType()) {
                    case DEFAULT:
                        FullSelectCTECriteriaBuilder<X> xFullSelectCTECriteriaBuilder = fromBuilder.fromEntitySubquery(target.getType(), alias);
                        FullSelectCTECriteriaBuilder<X> xFullSelectCTECriteriaBuilder1 = (FullSelectCTECriteriaBuilder<X>) serializeSubQuery(xFullSelectCTECriteriaBuilder, target);
                        criteriaBuilder = xFullSelectCTECriteriaBuilder1.end();
                        break;
                    default:
                        JoinType joinType = getJoinType(joinExpression);

                        boolean isLateral = joinExpression.hasFlag(BlazeJPAQuery.LATERAL);

                        if (fetch) {
                            logger.warning("Fetch is ignored due to subquery entity join!");
                        }

                        if (isLateral) {
                            if (hasCondition) {
                                FullSelectCTECriteriaBuilder<JoinOnBuilder<X>> joinOnBuilderFullSelectCTECriteriaBuilder = criteriaBuilder.joinLateralOnEntitySubquery(target.getType(), alias, alias, joinType);
                                JoinOnBuilder<X> xJoinOnBuilder = ((FullSelectCTECriteriaBuilder<JoinOnBuilder<X>>) serializeSubQuery(joinOnBuilderFullSelectCTECriteriaBuilder, target)).end();
                                setExpressionSubqueries(joinExpression.getCondition(), xJoinOnBuilder::setOnExpression, xJoinOnBuilder::setOnExpressionSubqueries);
                                criteriaBuilder = xJoinOnBuilder.end();
                            } else {
                                FullSelectCTECriteriaBuilder<X> xFullSelectCTECriteriaBuilder2 = criteriaBuilder.joinLateralEntitySubquery(target.getType(), alias, alias, joinType);
                                criteriaBuilder = ((FullSelectCTECriteriaBuilder<X>) serializeSubQuery(xFullSelectCTECriteriaBuilder2, target)).end();
                            }
                        } else {

                            if (!hasCondition) {
                                throw new IllegalStateException("No on-clause for subquery entity join!");
                            }

                            FullSelectCTECriteriaBuilder<JoinOnBuilder<X>> joinOnBuilderFullSelectCTECriteriaBuilder = criteriaBuilder.joinOnEntitySubquery(target.getType(), alias, joinType);
                            JoinOnBuilder<X> xJoinOnBuilder = ((FullSelectCTECriteriaBuilder<JoinOnBuilder<X>>) serializeSubQuery(joinOnBuilderFullSelectCTECriteriaBuilder, target)).end();
                            setExpressionSubqueries(joinExpression.getCondition(), xJoinOnBuilder::setOnExpression, xJoinOnBuilder::setOnExpressionSubqueries);
                            criteriaBuilder = xJoinOnBuilder.end();
                        }
                        break;
                }
            } else {
                // TODO Handle Treat operations
                throw new UnsupportedOperationException("Joins for " + target + " is not yet implemented");
            }
        }

        return criteriaBuilder;
    }

    private List<? extends Expression<?>> expandProjection(Expression<?> expr) {
        if (expr instanceof FactoryExpression) {
            return ((FactoryExpression<?>) expr).getArgs();
        } else {
            return ImmutableList.of(expr);
        }
    }

    private JoinType getJoinType(JoinExpression joinExpression) {
        switch (joinExpression.getType()) {
            case INNERJOIN:
            case JOIN:
                return JoinType.INNER;
            case LEFTJOIN:
                return JoinType.LEFT;
            case RIGHTJOIN:
                return JoinType.RIGHT;
            case FULLJOIN:
                return JoinType.FULL;
            default:
                throw new IllegalArgumentException("Join has no equivalent JoinType");
        }
    }

    private void renderOrderSpecifier(OrderSpecifier<?> orderSpecifier, OrderByBuilder<?> criteriaBuilder) {
        String orderExpression = renderExpression(orderSpecifier.getTarget());
        boolean ascending = orderSpecifier.isAscending();
        switch (orderSpecifier.getNullHandling()) {
            case Default:
                criteriaBuilder.orderBy(orderExpression, ascending);
                break;
            case NullsFirst:
                criteriaBuilder.orderBy(orderExpression, ascending, true);
                break;
            case NullsLast:
                criteriaBuilder.orderBy(orderExpression, ascending, false);
                break;
        }
    }

    private void renderSingleSelect(Expression<?> select, SelectBuilder<?> selectBuilder) {
        String alias = null;

        if (select instanceof Operation<?>) {
            Operation<?> operation = (Operation<?>) select;
            if (operation.getOperator() == Ops.ALIAS) {
                select = operation.getArg(0);
                alias = ((Path<?>) operation.getArg(1)).getMetadata().getName();
            }
        }

        final String finalAlias = alias;
        setExpressionSubqueries(select,
                (expression) -> finalAlias != null ? selectBuilder.select(expression, finalAlias) : selectBuilder.select(expression),
                (expression) -> finalAlias != null ? selectBuilder.selectSubqueries(expression, finalAlias) : selectBuilder.selectSubqueries(expression));
    }

    private int length = 0;

    private String takeBuffer() {
        if (getLength() == length) {
            return "";
        }
        String result = toString().substring(length);
        length = getLength();
        return result;
    }

    private String renderExpression(Expression<?> select) {
        takeBuffer();
        select.accept(this, null);
        return takeBuffer();
    }

    @Override
    public void visitConstant(Object constant) {
        // TODO Handle in case operations
        boolean wrap = templates.wrapConstant(constant);
        if (wrap) append("(");
        append(":");
        append(constantToLabel.computeIfAbsent(constant, o -> "param_" + (constantToLabel.size() + 1)));
        if (wrap) append(")");
    }

    @Override
    public Void visit(ParamExpression<?> param, Void context) {
        append(":").append(param.getName());
        return null;
    }

    @Override
    public Void visit(SubQueryExpression<?> query, Void context) {
        renderSubQueryExpression(query);
        return null;
    }

    private void renderSubQueryExpression(Expression<?> query) {
        append(subQueryToLabel.computeIfAbsent(query, o -> "generatedSubquery_" + (subQueryToLabel.size() + 1)));
    }

    private final List<SubqueryInitiator<?>> subqueryInitiatorStack = new ArrayList<SubqueryInitiator<?>>();;

    private SubqueryInitiator<?> getSubqueryInitiator() {
        return subqueryInitiatorStack.get(subqueryInitiatorStack.size() - 1);
    }

    private void pushSubqueryInitiator(SubqueryInitiator<?> subqueryInitiator) {
        subqueryInitiatorStack.add(subqueryInitiator);
    }

    private void popSubqueryInitiator() {
        subqueryInitiatorStack.remove(subqueryInitiatorStack.size() - 1);
    }

    private Map<Expression<?>, String> takeSubQueryToLabelMap() {
        Map<Expression<?>, String> subQueryToLabel = this.subQueryToLabel;
        if (subQueryToLabel.isEmpty()) {
            return Collections.emptyMap();
        }
        this.subQueryToLabel = new IdentityHashMap<>();
        return subQueryToLabel;
    }

    private EntityPath<?> cteEntityPath;
    private List<Path<?>> cteAliases;
    private boolean recursive;

    private <X> X setExpressionSubqueries(Expression<?> expression, Function<String, ? extends X> setExpression, Function<String, MultipleSubqueryInitiator<? extends X>> setExpressionSubqueries) {
        String expressionString = renderExpression(expression);
        Map<Expression<?>, String> subQueryToLabel = takeSubQueryToLabelMap();
        if (subQueryToLabel.isEmpty()) {
            return setExpression.apply(expressionString);
        } else {
            MultipleSubqueryInitiator<? extends X> subqueryInitiator = setExpressionSubqueries.apply(expressionString);
            for (Map.Entry<Expression<?>, String> entry : subQueryToLabel.entrySet()) {
                SubqueryInitiator<?> initiator = subqueryInitiator.with(entry.getValue());
                pushSubqueryInitiator(initiator);
                Object o = serializeSubQuery(initiator, entry.getKey());

                if (o instanceof SubqueryBuilder) {
                    ((SubqueryBuilder<?>) o).end();
                } else if (o instanceof FinalSetOperationSubqueryBuilder) {
                    ((FinalSetOperationSubqueryBuilder<?>) o).end();
                }

                popSubqueryInitiator();
            }
            return subqueryInitiator.end();
        }
    }

    @Override
    protected void visitOperation(Class<?> type, Operator operator, List<? extends Expression<?>> args) {
        if (operator instanceof JPQLNextOps) {
            switch ((JPQLNextOps) operator) {
                case WITH_ALIAS:
                    Expression<?> withColumns = args.get(0);
                    withColumns.accept(this, null);

                    if (recursive) {
                        Operation<?> unionOperation = args.get(1).accept(GetOperationVisitor.INSTANCE, null);
                        SubQueryExpression<?> subQuery = (SubQueryExpression<?>) unionOperation.getArg(0);
                        SelectRecursiveCTECriteriaBuilder<?> baseCriteriaBuilder =
                                (SelectRecursiveCTECriteriaBuilder<?>)
                                        serializeSubQuery(criteriaBuilder.withRecursive(cteEntityPath.getType()), subQuery);
                        SelectCTECriteriaBuilder<?> recursiveCriteriaBuilder = unionOperation.getOperator() == SET_UNION ?
                                baseCriteriaBuilder.union() : baseCriteriaBuilder.unionAll();
                        subQuery = (SubQueryExpression<?>) unionOperation.getArg(1);
                        ((SelectCTECriteriaBuilder<?>) serializeSubQuery(recursiveCriteriaBuilder, subQuery)).end();
                    } else {
                        FullSelectCTECriteriaBuilder<?> cteBuilder = criteriaBuilder.with(cteEntityPath.getType());

                        Expression<?> subQueryExpression = args.get(1);
                        Object result = serializeSubQuery(cteBuilder, subQueryExpression);

                        if (result instanceof FinalSetOperationCTECriteriaBuilder) {
                            ((FinalSetOperationCTECriteriaBuilder<?>) result).end();
                        } else if (result instanceof FullSelectCTECriteriaBuilder) {
                            ((FullSelectCTECriteriaBuilder<?>) result).end();
                        }
                    }
                    return;
                case WITH_RECURSIVE_COLUMNS:
                case WITH_COLUMNS:
                    recursive = operator == WITH_RECURSIVE_COLUMNS;
                    cteEntityPath = (EntityPath<?>) args.get(0);
                    cteAliases = args.get(1).accept(new CteAttributesVisitor(), new ArrayList<>());
                    return;
            }
        }

        super.visitOperation(type, operator, args);
    }

    @Override
    public Void visit(Operation<?> expr, Void context) {
        if (expr.getOperator() instanceof JPQLNextOps) {
            switch ((JPQLNextOps) expr.getOperator()) {
                case SET_UNION:
                case SET_UNION_ALL:
                case SET_INTERSECT:
                case SET_INTERSECT_ALL:
                case SET_EXCEPT:
                case SET_EXCEPT_ALL:
                    renderSubQueryExpression(expr);
                    return null;

            }
        }

        return super.visit(expr, context);
    }

    private static String relativePathString(Path<?> root, Path<?> path) {
        StringBuilder pathString = new StringBuilder(path.getMetadata().getName().length());
        while (path.getMetadata().getParent() != null && ! path.equals(root)) {
            if (pathString.length() > 0) pathString.insert(0, '.');
            pathString.insert(0, path.getMetadata().getName());
            path = path.getMetadata().getParent();
        }
        return pathString.toString();
    }

    private static class CteAttributesVisitor implements Visitor<List<Path<?>>, List<Path<?>>> {

        @Override
        public List<Path<?>> visit(Constant<?> expr, List<Path<?>> context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Path<?>> visit(FactoryExpression<?> expr, List<Path<?>> context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Path<?>> visit(Operation<?> operation, List<Path<?>> cteAliases) {
            switch ((Ops) (operation.getOperator())) {
                case SINGLETON:
                    operation.getArg(0).accept(this, cteAliases);
                    break;
                case LIST:
                    visit(operation.getArgs(), cteAliases);
                    break;
            }
            return cteAliases;
        }

        @Override
        public List<Path<?>> visit(ParamExpression<?> expr, List<Path<?>> context) {
            throw new UnsupportedOperationException();

        }

        @Override
        public List<Path<?>> visit(Path<?> expr, List<Path<?>> context) {
            context.add(expr);
            return context;
        }

        @Override
        public List<Path<?>> visit(SubQueryExpression<?> expr, List<Path<?>> context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Path<?>> visit(TemplateExpression<?> expr, List<Path<?>> context) {
            throw new UnsupportedOperationException();

        }

        private void visit(List<Expression<?>> exprs, List<Path<?>> context) {
            for (Expression<?> e : exprs) {
                e.accept(this, context);
            }
        }

    }

    public CriteriaBuilder<T> getCriteriaBuilder() {
        return criteriaBuilder;
    }

    public Queryable<T, ?> getQueryable() {
        return queryable;
    }

    private class FactoryExpressionObjectBuilder implements ObjectBuilder<T> {
        private final FactoryExpression<T> factoryExpression;

        public FactoryExpressionObjectBuilder(FactoryExpression<T> factoryExpression) {
            this.factoryExpression = factoryExpression;
        }

        @Override
        public <X extends SelectBuilder<X>> void applySelects(X selectBuilder) {
            for (Expression<?> arg : factoryExpression.getArgs()) {
                renderSingleSelect(arg, selectBuilder);
            }
        }

        @Override
        public T build(Object[] tuple) {
            return factoryExpression.newInstance(tuple);
        }

        @Override
        public List<T> buildList(List<T> list) {
            return list;
        }
    }
}