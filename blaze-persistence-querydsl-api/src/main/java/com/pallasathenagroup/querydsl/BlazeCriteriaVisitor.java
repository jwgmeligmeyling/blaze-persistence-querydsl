package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.BaseOngoingFinalSetOperationBuilder;
import com.blazebit.persistence.BaseOngoingSetOperationBuilder;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.DistinctBuilder;
import com.blazebit.persistence.FinalSetOperationCTECriteriaBuilder;
import com.blazebit.persistence.FinalSetOperationCriteriaBuilder;
import com.blazebit.persistence.FromBaseBuilder;
import com.blazebit.persistence.FromBuilder;
import com.blazebit.persistence.FullQueryBuilder;
import com.blazebit.persistence.FullSelectCTECriteriaBuilder;
import com.blazebit.persistence.GroupByBuilder;
import com.blazebit.persistence.HavingBuilder;
import com.blazebit.persistence.JoinType;
import com.blazebit.persistence.LeafOngoingFinalSetOperationCTECriteriaBuilder;
import com.blazebit.persistence.LeafOngoingSetOperationCTECriteriaBuilder;
import com.blazebit.persistence.LeafOngoingSetOperationCriteriaBuilder;
import com.blazebit.persistence.LimitBuilder;
import com.blazebit.persistence.MiddleOngoingSetOperationCTECriteriaBuilder;
import com.blazebit.persistence.MultipleSubqueryInitiator;
import com.blazebit.persistence.ObjectBuilder;
import com.blazebit.persistence.OngoingSetOperationBuilder;
import com.blazebit.persistence.OngoingSetOperationCTECriteriaBuilder;
import com.blazebit.persistence.OrderByBuilder;
import com.blazebit.persistence.ParameterHolder;
import com.blazebit.persistence.SelectBaseCTECriteriaBuilder;
import com.blazebit.persistence.SelectBuilder;
import com.blazebit.persistence.SelectCTECriteriaBuilder;
import com.blazebit.persistence.SelectRecursiveCTECriteriaBuilder;
import com.blazebit.persistence.SetOperationBuilder;
import com.blazebit.persistence.StartOngoingSetOperationBuilder;
import com.blazebit.persistence.StartOngoingSetOperationCTECriteriaBuilder;
import com.blazebit.persistence.SubqueryBuilder;
import com.blazebit.persistence.SubqueryInitiator;
import com.blazebit.persistence.WhereBuilder;
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
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;

import static com.pallasathenagroup.querydsl.JPQLNextOps.WITH_RECURSIVE_COLUMNS;

public class BlazeCriteriaVisitor<T> extends JPQLSerializer {

    private static final Logger logger = Logger.getLogger(BlazeCriteriaVisitor.class.getName());

    private CriteriaBuilder<T> criteriaBuilder;
    private CriteriaBuilderFactory criteriaBuilderFactory;
    private EntityManager entityManager;
    private Map<Object, String> constantToLabel = new IdentityHashMap<>();
    private Map<QueryMetadata, String> subQueryToLabel = new IdentityHashMap<>();
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


        for (QueryFlag queryFlag : metadata.getFlags()) {
            Expression<?> flag = queryFlag.getFlag();
            Position position = queryFlag.getPosition();
            switch (position) {
                case WITH:
                    flag.accept(this, null);
                    break;
            }
        }

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
            criteriaBuilder.setHavingExpression(renderExpression(metadata.getHaving()));
        }
    }

    private void renderGroupBy(QueryMetadata metadata, GroupByBuilder<?> criteriaBuilder) {
        for (Expression<?> groupByExpression : metadata.getGroupBy()) {
            criteriaBuilder.groupBy(renderExpression(groupByExpression));
        }
    }

    private void renderWhere(QueryMetadata metadata, WhereBuilder<?> criteriaBuilder) {
        if (metadata.getWhere() != null) {
            String expression = renderExpression(metadata.getWhere());
            Map<QueryMetadata, String> subQueryToLabel = takeSubQueryToLabelMap();
            if (subQueryToLabel.isEmpty()) {
                criteriaBuilder.setWhereExpression(expression);
            } else {
                MultipleSubqueryInitiator<?> subqueryInitiator = criteriaBuilder.setWhereExpressionSubqueries(expression);
                for (Map.Entry<QueryMetadata, String> entry : subQueryToLabel.entrySet()) {
                    pushSubqueryInitiator(subqueryInitiator.with(entry.getValue()));
                    serializeSubQuery(entry.getKey());
                    popSubqueryInitiator();
                }
                subqueryInitiator.end();
            }
        }
    }

    private void renderDistinct(QueryMetadata metadata, DistinctBuilder<?> criteriaBuilder) {
        if (metadata.isDistinct()) {
            criteriaBuilder.distinct();
        }
    }

    private <X extends FromBuilder<X>> X renderJoins(QueryMetadata metadata, FromBaseBuilder<X> subqueryInitiator) {
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
                if ( valuesExpression.isIdentifiable() ) {
                    criteriaBuilder = (X) subqueryInitiator.fromIdentifiableValues((Class) valuesExpression.getType(), valuesExpression.getMetadata().getName(), valuesExpression.getElements());
                } else {
                    criteriaBuilder = (X) subqueryInitiator.fromValues((Class) valuesExpression.getType(), valuesExpression.getMetadata().getName(), valuesExpression.getElements());
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
                        criteriaBuilder = subqueryInitiator.from(entityPath.getType(), alias);
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
                            criteriaBuilder.joinOn(entityPath.getType(), alias, joinType)
                                    .setOnExpression(renderExpression(joinExpression.getCondition()));
                        } else if (!hasCondition) {
                            if (fetch) {
                                ((FullQueryBuilder) criteriaBuilder).joinDefault(renderExpression(entityPath), alias, joinType, fetch);
                            } else {
                                criteriaBuilder.joinDefault(renderExpression(entityPath), alias, joinType);
                            }
                        } else {
                            criteriaBuilder.joinOn(renderExpression(entityPath), alias, joinType)
                                    .setOnExpression(renderExpression(joinExpression.getCondition()));
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

    private void serializeSubQuery(QueryMetadata metadata) {
        SubqueryInitiator<?> subqueryInitiator = getSubqueryInitiator();
        QueryModifiers modifiers = metadata.getModifiers();
        Expression<?> select = metadata.getProjection();

        SubqueryBuilder criteriaBuilder = renderJoins(metadata, subqueryInitiator);
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
            for (Expression<?> arg : factoryExpression.getArgs()) {
                renderSingleSelect(arg, criteriaBuilder);
            }
        } else {
            renderSingleSelect(select, criteriaBuilder);
        }

        criteriaBuilder.end();
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

        String s = renderExpression(select);
        selectBuilder.select(s, alias);
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
    public Void visit(SubQueryExpression<?> query, Void context) {
        append(subQueryToLabel.computeIfAbsent(query.getMetadata(), o -> "generatedSubquery_" + (subQueryToLabel.size() + 1)));
        return null;
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

    private Map<QueryMetadata, String> takeSubQueryToLabelMap() {
        Map<QueryMetadata, String> subQueryToLabel = this.subQueryToLabel;
        if (subQueryToLabel.isEmpty()) {
            return Collections.emptyMap();
        }
        this.subQueryToLabel = new IdentityHashMap<>();
        return subQueryToLabel;
    }

    private EntityPath<?> cteEntityPath;
    private List<Path<?>> cteAliases;
    private boolean recursive;

    private <X extends SelectBaseCTECriteriaBuilder<?> & SetOperationBuilder<?, ?>>
    Object renderWithQuery(X criteriaBuilder, Expression<?> expression) {
        if (expression instanceof Operation<?>) {
            Operation<?> setOperation = (Operation<?>) expression;
            renderWithQuery(criteriaBuilder, setOperation.getArg(0));
            Object setBuilder = null;
            switch ((JPQLNextOps) setOperation.getOperator()) {
                case SET_UNION:
                    setBuilder = setOperation.getArg(1) instanceof Operation<?> ?
                            criteriaBuilder.startUnion() : criteriaBuilder.union();
                    break;
                case SET_UNION_ALL:
                    setBuilder = setOperation.getArg(1) instanceof Operation<?> ?
                            criteriaBuilder.startUnionAll() : criteriaBuilder.unionAll();
                    break;
                case SET_EXCEPT:
                    setBuilder = setOperation.getArg(1) instanceof Operation<?> ?
                            criteriaBuilder.startExcept() : criteriaBuilder.except();
                    break;
                case SET_EXCEPT_ALL:
                    setBuilder = setOperation.getArg(1) instanceof Operation<?> ?
                            criteriaBuilder.startExceptAll() : criteriaBuilder.exceptAll();
                    break;
                case SET_INTERSECT:
                    setBuilder = setOperation.getArg(1) instanceof Operation<?> ?
                            criteriaBuilder.startIntersect() : criteriaBuilder.intersect();
                    break;
                case SET_INTERSECT_ALL:
                    setBuilder = setOperation.getArg(1) instanceof Operation<?> ?
                            criteriaBuilder.startIntersectAll() : criteriaBuilder.intersectAll();
                    break;
            }
            setBuilder = renderWithQuery((X) setBuilder, setOperation.getArg(1));
            return ((BaseOngoingSetOperationBuilder) setBuilder).endSet();
        }
        else {

            SubQueryExpression<?> subQuery = (SubQueryExpression) expression;
            QueryMetadata subQueryMetadata = subQuery.getMetadata();

            renderJoins(subQueryMetadata, (FromBaseBuilder) criteriaBuilder);
            renderDistinct(subQueryMetadata, criteriaBuilder);
            renderWhere(subQueryMetadata, criteriaBuilder);
            renderGroupBy(subQueryMetadata, criteriaBuilder);
            renderHaving(subQueryMetadata, criteriaBuilder);
            renderOrderBy(subQueryMetadata, criteriaBuilder);
            renderParameters(subQueryMetadata, criteriaBuilder);
            renderModifiers(subQueryMetadata.getModifiers(), criteriaBuilder);

            FactoryExpression<?> projection = (FactoryExpression<?>) subQueryMetadata.getProjection();

            for (int i = 0; i < cteAliases.size(); i++) {
                Path<?> alias = cteAliases.get(i);
                String aliasString = relativePathString(cteEntityPath, alias);
                Expression<?> projExpression = projection.getArgs().get(i);
                String expressionString = renderExpression(projExpression);
                criteriaBuilder.bind(aliasString).select(expressionString);
            }
        }
        return criteriaBuilder;
    }

    @Override
    protected void visitOperation(Class<?> type, Operator operator, List<? extends Expression<?>> args) {
        if (operator instanceof JPQLNextOps) {
            switch ((JPQLNextOps) operator) {
                case WITH_ALIAS:
                    Expression<?> withColumns = args.get(0);
                    withColumns.accept(this, null);


                    if (recursive) {
                        Operation<?> unionOperation = (Operation<?>) args.get(1);
                        SubQueryExpression<?> subQuery = (SubQueryExpression) unionOperation.getArg(0);
                        QueryMetadata subQueryMetadata = subQuery.getMetadata();
                        FactoryExpression<?> projection = (FactoryExpression<?>) subQueryMetadata.getProjection();

                        SelectRecursiveCTECriteriaBuilder<CriteriaBuilder<T>> baseCriteriaBuilder = criteriaBuilder.withRecursive(cteEntityPath.getType());
                        SelectCTECriteriaBuilder<CriteriaBuilder<T>> recursiveCriteriaBuilder = null;

                        renderJoins(subQueryMetadata, (FromBaseBuilder) baseCriteriaBuilder);
                        renderDistinct(subQueryMetadata, baseCriteriaBuilder);
                        renderWhere(subQueryMetadata, baseCriteriaBuilder);
                        renderGroupBy(subQueryMetadata, baseCriteriaBuilder);
                        renderHaving(subQueryMetadata, baseCriteriaBuilder);
                        renderOrderBy(subQueryMetadata, baseCriteriaBuilder);
                        renderParameters(subQueryMetadata, baseCriteriaBuilder);
                        renderModifiers(subQueryMetadata.getModifiers(), baseCriteriaBuilder);


                        for (int i = 0; i < cteAliases.size(); i++) {
                            Path<?> alias = cteAliases.get(i);
                            String aliasString = relativePathString(cteEntityPath, alias);
                            Expression<?> projExpression = projection.getArgs().get(i);
                            String expressionString = renderExpression(projExpression);
                            baseCriteriaBuilder.bind(aliasString).select(expressionString);
                        }


                        switch ((JPQLNextOps) unionOperation.getOperator()) {
                            case SET_UNION:
                                recursiveCriteriaBuilder = baseCriteriaBuilder.union();
                                break;
                            case SET_UNION_ALL:
                                recursiveCriteriaBuilder = baseCriteriaBuilder.unionAll();
                                break;
                        }

                        subQuery = (SubQueryExpression) unionOperation.getArg(1);
                        subQueryMetadata = subQuery.getMetadata();
                        projection = (FactoryExpression<?>) subQueryMetadata.getProjection();

                        renderJoins(subQueryMetadata, (FromBaseBuilder) recursiveCriteriaBuilder);
                        renderDistinct(subQueryMetadata, recursiveCriteriaBuilder);
                        renderWhere(subQueryMetadata, recursiveCriteriaBuilder);
                        renderGroupBy(subQueryMetadata, recursiveCriteriaBuilder);
                        renderHaving(subQueryMetadata, recursiveCriteriaBuilder);
                        renderOrderBy(subQueryMetadata, recursiveCriteriaBuilder);
                        renderParameters(subQueryMetadata, recursiveCriteriaBuilder);
                        renderModifiers(subQueryMetadata.getModifiers(), recursiveCriteriaBuilder);

                        for (int i = 0; i < cteAliases.size(); i++) {
                            Path<?> alias = cteAliases.get(i);
                            String aliasString = relativePathString(cteEntityPath, alias);
                            Expression<?> projExpression = projection.getArgs().get(i);
                            String expressionString = renderExpression(projExpression);
                            recursiveCriteriaBuilder.bind(aliasString).select(expressionString);
                        }

                        recursiveCriteriaBuilder.end();
                        return;
                    }


                    FullSelectCTECriteriaBuilder<?> cteBuilder = criteriaBuilder.with(cteEntityPath.getType());

                    Expression<?> subQueryExpression = args.get(1);
                    Object result = renderWithQuery(cteBuilder, subQueryExpression);

                    if (result instanceof FinalSetOperationCTECriteriaBuilder) {
                        result = ((FinalSetOperationCTECriteriaBuilder) result).end();
                    }
                    else if (result instanceof FullSelectCTECriteriaBuilder) {
                        result = ((FullSelectCTECriteriaBuilder) result).end();
                    }

//                    SubQueryExpression<?> subQuery = (SubQueryExpression) args.get(1);
//                    QueryMetadata subQueryMetadata = subQuery.getMetadata();
//
//                    renderJoins(subQueryMetadata, cteBuilder);
//                    renderDistinct(subQueryMetadata, cteBuilder);
//                    renderWhere(subQueryMetadata, cteBuilder);
//                    renderGroupBy(subQueryMetadata, cteBuilder);
//                    renderHaving(subQueryMetadata, cteBuilder);
//                    renderOrderBy(subQueryMetadata, cteBuilder);
//                    renderParameters(subQueryMetadata, cteBuilder);
//                    renderModifiers(subQueryMetadata.getModifiers(), cteBuilder);
//
//                    FactoryExpression<?> projection = (FactoryExpression<?>) subQueryMetadata.getProjection();
//
//                    for (int i = 0; i < cteAliases.size(); i++) {
//                        Path<?> alias = cteAliases.get(i);
//                        String aliasString = relativePathString(cteEntityPath, alias);
//                        Expression<?> expression = projection.getArgs().get(i);
//                        String expressionString = renderExpression(expression);
//                        cteBuilder.bind(aliasString).select(expressionString);
//                    }
//
//                    cteBuilder.end();
                    return;
                case WITH_RECURSIVE_COLUMNS:
                    recursive = true;
                case WITH_COLUMNS:
                    cteEntityPath = (EntityPath<?>) args.get(0);
                    cteAliases = args.get(1).accept(new CteAttributesVisitor(), new ArrayList<>());
                    return;
            }
        }

        super.visitOperation(type, operator, args);
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
