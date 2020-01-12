package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.JoinType;
import com.blazebit.persistence.ObjectBuilder;
import com.blazebit.persistence.SelectBuilder;
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
import com.querydsl.jpa.JPAQueryMixin;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.JPQLTemplates;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BlazeCriteriaVisitor<T> {

    private static final Logger logger = Logger.getLogger(BlazeCriteriaVisitor.class.getName());

    private CriteriaBuilder<T> criteriaBuilder;
    private CriteriaBuilderFactory criteriaBuilderFactory;
    private EntityManager entityManager;
    private JPQLTemplates templates;
    private Map<Object, String> constantToLabel = new IdentityHashMap<>();

    public BlazeCriteriaVisitor(CriteriaBuilderFactory criteriaBuilderFactory, EntityManager entityManager, JPQLTemplates templates) {
        this.criteriaBuilderFactory = criteriaBuilderFactory;
        this.entityManager = entityManager;
        this.templates = templates;
    }

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

            if (target instanceof Path<?>) {
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
            criteriaBuilder.setWhereExpression(renderExpression(metadata.getWhere()));
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

    private String renderExpression(Expression<?> select) {
        JPQLSerializer jpqlSerializer = new JPQLSerializer(templates) {
            @Override
            public void visitConstant(Object constant) {
                // TODO Handle in case operations
                boolean wrap = templates.wrapConstant(constant);
                if (wrap) append("(");
                append(":");
                append(constantToLabel.computeIfAbsent(constant, o -> "param_" + (constantToLabel.size() + 1)));
                if (wrap) append(")");
            }
        };
        select.accept(jpqlSerializer, null);
        return jpqlSerializer.toString();
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
