package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.JoinType;
import com.blazebit.persistence.MultipleSubqueryInitiator;
import com.blazebit.persistence.ObjectBuilder;
import com.blazebit.persistence.SelectBuilder;
import com.blazebit.persistence.SubqueryBuilder;
import com.blazebit.persistence.SubqueryInitiator;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.JPAQueryMixin;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.JPQLTemplates;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
                    criteriaBuilder.fromIdentifiableValues((Class) valuesExpression.getType(), valuesExpression.getMetadata().getName(), valuesExpression.getElements());
                } else {
                    criteriaBuilder.fromValues((Class) valuesExpression.getType(), valuesExpression.getMetadata().getName(), valuesExpression.getElements());
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
                        criteriaBuilder.from(entityPath.getType(), alias);
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
                            criteriaBuilder.joinDefault(renderExpression(entityPath), alias, joinType, fetch);
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

        if (metadata.isDistinct()) {
            criteriaBuilder.distinct();
        }

        if (select instanceof FactoryExpression<?>) {
            FactoryExpression<T> factoryExpression = (FactoryExpression<T>) select;
            criteriaBuilder.selectNew(new FactoryExpressionObjectBuilder(factoryExpression));
        } else {
            renderSingleSelect(select, criteriaBuilder);
        }

        if (metadata.getWhere() != null) {
            String expression = renderExpression(metadata.getWhere());
            Map<QueryMetadata, String> subQueryToLabel = takeSubQueryToLabelMap();
            if (subQueryToLabel.isEmpty()) {
                criteriaBuilder.setWhereExpression(expression);
            } else {
                MultipleSubqueryInitiator<CriteriaBuilder<T>> subqueryInitiator = criteriaBuilder.setWhereExpressionSubqueries(expression);
                for (Map.Entry<QueryMetadata, String> entry : subQueryToLabel.entrySet()) {
                    pushSubqueryInitiator(subqueryInitiator.with(entry.getValue()));
                    serializeSubQuery(entry.getKey());
                    popSubqueryInitiator();
                }
                subqueryInitiator.end();
            }


        }

        for (Expression<?> groupByExpression : metadata.getGroupBy()) {
            criteriaBuilder.groupBy(renderExpression(groupByExpression));
        }

        if (metadata.getHaving() != null) {
            criteriaBuilder.setHavingExpression(renderExpression(metadata.getHaving()));
        }

        for (OrderSpecifier<?> orderSpecifier : metadata.getOrderBy()) {
            renderOrderSpecifier(orderSpecifier);
        }

        for (Map.Entry<ParamExpression<?>, Object> entry : metadata.getParams().entrySet()) {
            criteriaBuilder.setParameter(entry.getKey().getName(), entry.getValue());
        }

        for (Map.Entry<Object, String> entry : constantToLabel.entrySet()) {
            criteriaBuilder.setParameter(entry.getValue(), entry.getKey());
        }

        if (! metadata.getFlags().isEmpty()) {
            logger.warning("Ignoring flags for query");
        }

        if (modifiers != null) {
            if (modifiers.getLimitAsInteger() != null) {
                criteriaBuilder.setMaxResults(modifiers.getLimitAsInteger());
            }
            if (modifiers.getOffsetAsInteger() != null) {
                criteriaBuilder.setFirstResult(modifiers.getOffsetAsInteger());
            }
        }

    }

    private void serializeSubQuery(QueryMetadata metadata) {
        SubqueryInitiator<?> subqueryInitiator = getSubqueryInitiator();
        SubqueryBuilder<?> criteriaBuilder = null;

        QueryModifiers modifiers = metadata.getModifiers();
        Expression<?> select = metadata.getProjection();

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
                    criteriaBuilder = subqueryInitiator.fromIdentifiableValues((Class) valuesExpression.getType(), valuesExpression.getMetadata().getName(), valuesExpression.getElements());
                } else {
                    criteriaBuilder = subqueryInitiator.fromValues((Class) valuesExpression.getType(), valuesExpression.getMetadata().getName(), valuesExpression.getElements());
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
                            criteriaBuilder.joinDefault(renderExpression(entityPath), alias, joinType);
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

        if (metadata.isDistinct()) {
            criteriaBuilder.distinct();
        }

        if (select instanceof FactoryExpression<?>) {
            FactoryExpression<T> factoryExpression = (FactoryExpression<T>) select;
            for (Expression<?> arg : factoryExpression.getArgs()) {
                renderSingleSelect(arg, criteriaBuilder);
            }
        } else {
            renderSingleSelect(select, criteriaBuilder);
        }

        if (metadata.getWhere() != null) {
            String expression = renderExpression(metadata.getWhere());
            Map<QueryMetadata, String> subQueryToLabel = takeSubQueryToLabelMap();
            if (subQueryToLabel.isEmpty()) {
                criteriaBuilder.setWhereExpression(expression);
            } else {
                MultipleSubqueryInitiator<?> subqueryInitiator2 = criteriaBuilder.setWhereExpressionSubqueries(expression);
                for (Map.Entry<QueryMetadata, String> entry : subQueryToLabel.entrySet()) {
                    pushSubqueryInitiator(subqueryInitiator2.with(entry.getValue()));
                    serializeSubQuery(entry.getKey());
                    popSubqueryInitiator();
                }
                subqueryInitiator2.end();
            }
        }

        for (Expression<?> groupByExpression : metadata.getGroupBy()) {
            criteriaBuilder.groupBy(renderExpression(groupByExpression));
        }

        if (metadata.getHaving() != null) {
            criteriaBuilder.setHavingExpression(renderExpression(metadata.getHaving()));
        }

        for (OrderSpecifier<?> orderSpecifier : metadata.getOrderBy()) {
            renderOrderSpecifier(orderSpecifier);
        }

        for (Map.Entry<ParamExpression<?>, Object> entry : metadata.getParams().entrySet()) {
            criteriaBuilder.setParameter(entry.getKey().getName(), entry.getValue());
        }

        for (Map.Entry<Object, String> entry : constantToLabel.entrySet()) {
            criteriaBuilder.setParameter(entry.getValue(), entry.getKey());
        }

        if (! metadata.getFlags().isEmpty()) {
            logger.warning("Ignoring flags for query");
        }

        if (modifiers != null) {
            if (modifiers.getLimitAsInteger() != null) {
                criteriaBuilder.setMaxResults(modifiers.getLimitAsInteger());
            }
            if (modifiers.getOffsetAsInteger() != null) {
                criteriaBuilder.setFirstResult(modifiers.getOffsetAsInteger());
            }
        }

        criteriaBuilder.end();
    }

    private JoinType getJoinType(JoinExpression joinExpression) {
        switch (joinExpression.getType()) {
            case DEFAULT:
                throw new IllegalArgumentException("DEFAULT Join has no equivalent JoinType");
            case INNERJOIN:
            case JOIN:
                return JoinType.INNER;
            case LEFTJOIN:
                return JoinType.LEFT;
            case RIGHTJOIN:
                return JoinType.RIGHT;
            case FULLJOIN:
                return JoinType.FULL;
        }
        throw new IllegalStateException();
    }

    private void renderOrderSpecifier(OrderSpecifier<?> orderSpecifier) {
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
