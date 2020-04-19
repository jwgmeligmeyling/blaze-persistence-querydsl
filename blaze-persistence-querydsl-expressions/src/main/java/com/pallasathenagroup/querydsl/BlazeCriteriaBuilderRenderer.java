package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.BaseOngoingFinalSetOperationBuilder;
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
import com.blazebit.persistence.OngoingSetOperationBuilder;
import com.blazebit.persistence.OrderByBuilder;
import com.blazebit.persistence.ParameterHolder;
import com.blazebit.persistence.Queryable;
import com.blazebit.persistence.SelectBaseCTECriteriaBuilder;
import com.blazebit.persistence.SelectBuilder;
import com.blazebit.persistence.SelectCTECriteriaBuilder;
import com.blazebit.persistence.SelectRecursiveCTECriteriaBuilder;
import com.blazebit.persistence.SetOperationBuilder;
import com.blazebit.persistence.StartOngoingSetOperationBuilder;
import com.blazebit.persistence.SubqueryBuilder;
import com.blazebit.persistence.SubqueryInitiator;
import com.blazebit.persistence.WhereBuilder;
import com.blazebit.persistence.WindowBuilder;
import com.blazebit.persistence.WindowContainerBuilder;
import com.blazebit.persistence.WindowFrameBetweenBuilder;
import com.blazebit.persistence.WindowFrameBuilder;
import com.blazebit.persistence.WindowFrameExclusionBuilder;
import com.blazebit.persistence.parser.EntityMetamodel;
import com.blazebit.persistence.parser.expression.WindowFrameMode;
import com.blazebit.persistence.parser.util.JpaMetamodelUtils;
import com.blazebit.persistence.spi.ExtendedAttribute;
import com.blazebit.persistence.spi.ExtendedManagedType;
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
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.CollectionExpressionBase;
import com.querydsl.jpa.JPAQueryMixin;
import com.querydsl.jpa.JPQLTemplates;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

import static com.pallasathenagroup.querydsl.JPQLNextOps.BIND;
import static com.pallasathenagroup.querydsl.JPQLNextOps.LEFT_NESTED_SET_OPERATIONS;
import static com.pallasathenagroup.querydsl.JPQLNextOps.SET_UNION;
import static com.pallasathenagroup.querydsl.JPQLNextOps.WITH_RECURSIVE_ALIAS;
import static com.pallasathenagroup.querydsl.SetOperationFlag.getSetOperationFlag;

/**
 * A class for rendering a {@link BlazeJPAQuery} to a {@link CriteriaBuilder}
 * @param <T> Query result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public class BlazeCriteriaBuilderRenderer<T> {

    private static final Logger logger = Logger.getLogger(BlazeCriteriaBuilderRenderer.class.getName());

    private final CriteriaBuilderFactory criteriaBuilderFactory;
    private final EntityManager entityManager;
    private final JPQLNextSerializer serializer;
    private final Map<Object, String> constantToLabel = new IdentityHashMap<>();
    private Map<Expression<?>, String> subQueryToLabel = new IdentityHashMap<>();
    private CriteriaBuilder<T> criteriaBuilder;

    public BlazeCriteriaBuilderRenderer(CriteriaBuilderFactory criteriaBuilderFactory, EntityManager entityManager, JPQLTemplates templates) {
        this.serializer = new JPQLNextExpressionSerializer(templates, entityManager);
        this.criteriaBuilderFactory = criteriaBuilderFactory;
        this.entityManager = entityManager;
    }

    public Queryable<T, ?> render(Expression<?> expression) {
        this.criteriaBuilder = (CriteriaBuilder) criteriaBuilderFactory.create(entityManager, Object.class);
        return (Queryable<T, ?>) serializeSubQuery(this.criteriaBuilder, expression);
    }

    private Object serializeSubQuery(Object criteriaBuilder, Expression<?> expression) {
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
                Expression<?> lhs = setOperation.getArg(0);
                SubQueryExpression<?> lhsSubquery = lhs.accept(GetSubQueryVisitor.INSTANCE, null);
                SetOperationFlag setOperationFlag = lhsSubquery != null ? getSetOperationFlag(lhsSubquery.getMetadata()) : null;
                boolean lhsNestedSet = setOperationFlag != null && LEFT_NESTED_SET_OPERATIONS.contains(setOperation.getOperator());

                if (lhsNestedSet) {
                    if (criteriaBuilder instanceof StartOngoingSetOperationBuilder) {
                        StartOngoingSetOperationBuilder<?, ?, ?> ob = (StartOngoingSetOperationBuilder<?, ?, ?>) criteriaBuilder;
                        criteriaBuilder = ob.startSet();
                    } else if (criteriaBuilder instanceof SubqueryInitiator) {
                        SubqueryInitiator<?> subqueryInitiator = (SubqueryInitiator<?>) criteriaBuilder;
                        criteriaBuilder = subqueryInitiator.startSet();
                    } else {
                        criteriaBuilder = criteriaBuilderFactory.startSet(entityManager, Object.class);
                    }

                    criteriaBuilder = setOperationFlag.getFlag().accept(this, criteriaBuilder);

                    if (criteriaBuilder instanceof OngoingSetOperationBuilder) {
                        criteriaBuilder = ((OngoingSetOperationBuilder<?, ?, ?>) criteriaBuilder).endSetWith();
                        renderOrderBy(lhsSubquery.getMetadata(), (OrderByBuilder<?>) criteriaBuilder);
                        renderModifiers(lhsSubquery.getMetadata().getModifiers(), (LimitBuilder<?>) criteriaBuilder);
                        criteriaBuilder = ((BaseOngoingFinalSetOperationBuilder) criteriaBuilder).endSet();
                    } else {
                        throw new UnsupportedOperationException();
                    }
                } else {
                    criteriaBuilder = lhs.accept(this, criteriaBuilder);
                }

                Expression<?> rhs = setOperation.getArg(1);
                SubQueryExpression<?> rhsSubquery = rhs.accept(GetSubQueryVisitor.INSTANCE, null);
                setOperationFlag = rhsSubquery != null ? getSetOperationFlag(rhsSubquery.getMetadata()) : null;
                boolean isNestedSet = setOperationFlag != null;
                SetOperationBuilder<?,?> setOperationBuilder = (SetOperationBuilder<?,?>) criteriaBuilder;

                switch ((JPQLNextOps) setOperation.getOperator()) {
                    case SET_UNION:
                    case LEFT_NESTED_SET_UNION:
                        criteriaBuilder = isNestedSet ?
                                setOperationBuilder.startUnion() : setOperationBuilder.union();
                        break;
                    case SET_UNION_ALL:
                    case LEFT_NESTED_SET_UNION_ALL:
                        criteriaBuilder = isNestedSet ?
                                setOperationBuilder.startUnionAll() : setOperationBuilder.unionAll();
                        break;
                    case SET_EXCEPT:
                    case LEFT_NESTED_SET_EXCEPT:
                        criteriaBuilder = isNestedSet ?
                                setOperationBuilder.startExcept() : setOperationBuilder.except();
                        break;
                    case SET_EXCEPT_ALL:
                    case LEFT_NESTED_SET_EXCEPT_ALL:
                        criteriaBuilder = isNestedSet ?
                                setOperationBuilder.startExceptAll() : setOperationBuilder.exceptAll();
                        break;
                    case SET_INTERSECT:
                    case LEFT_NESTED_SET_INTERSECT:
                        criteriaBuilder = isNestedSet ?
                                setOperationBuilder.startIntersect() : setOperationBuilder.intersect();
                        break;
                    case SET_INTERSECT_ALL:
                    case LEFT_NESTED_SET_INTERSECT_ALL:
                        criteriaBuilder = isNestedSet ?
                                setOperationBuilder.startIntersectAll() : setOperationBuilder.intersectAll();
                        break;
                    default: throw new UnsupportedOperationException("No support for set operation " + setOperation.getOperator());
                }

                if (isNestedSet) {
                    criteriaBuilder = setOperationFlag.getFlag().accept(this, criteriaBuilder);

                    if (criteriaBuilder instanceof OngoingSetOperationBuilder) {
                        criteriaBuilder = ((OngoingSetOperationBuilder<?, ?, ?>) criteriaBuilder).endSetWith();
                        renderOrderBy(rhsSubquery.getMetadata(), (OrderByBuilder<?>) criteriaBuilder);
                        renderModifiers(rhsSubquery.getMetadata().getModifiers(), (LimitBuilder<?>) criteriaBuilder);
                        criteriaBuilder = ((BaseOngoingFinalSetOperationBuilder) criteriaBuilder).endSet();
                    } else {
                        throw new UnsupportedOperationException();
                    }
                } else {
                    criteriaBuilder = rhs.accept(this, criteriaBuilder);
                }

                return criteriaBuilder;
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

                Optional<QueryFlag> setOperation = subQueryMetadata.getFlags().stream().filter(flag -> flag.getPosition().equals(Position.START_OVERRIDE)).findAny();
                if (setOperation.isPresent()) {
                    return setOperation.get().getFlag().accept(this, criteriaBuilder);
                }

                renderCTEs(subQueryMetadata);

                criteriaBuilder = renderJoins(subQueryMetadata, (FromBaseBuilder) criteriaBuilder);
                criteriaBuilder = renderNamedWindows(subQueryMetadata, (WindowContainerBuilder) criteriaBuilder);
                renderDistinct(subQueryMetadata, (DistinctBuilder<?>) criteriaBuilder);
                renderWhere(subQueryMetadata, (WhereBuilder<?>) criteriaBuilder);
                renderGroupBy(subQueryMetadata, (GroupByBuilder<?>) criteriaBuilder);
                renderHaving(subQueryMetadata, (HavingBuilder<?>) criteriaBuilder);

                Expression<?> select = subQueryMetadata.getProjection();
                if (select instanceof FactoryExpression<?> && criteriaBuilder instanceof FullQueryBuilder<?, ?>) {
                    FactoryExpression<T> factoryExpression = (FactoryExpression<T>) select;
                    FullQueryBuilder<?, ?> fullQueryBuilder = (FullQueryBuilder<?, ?>) criteriaBuilder;
                    criteriaBuilder = fullQueryBuilder.selectNew(new FactoryExpressionObjectBuilder(factoryExpression));

                } else {
                    List<? extends Expression<?>> projection = expandProjection(subQueryMetadata.getProjection());

                    if (criteriaBuilder instanceof SelectBaseCTECriteriaBuilder) {
                        SelectBaseCTECriteriaBuilder<?> selectBaseCriteriaBuilder = (SelectBaseCTECriteriaBuilder<?>) criteriaBuilder;

                        boolean bindEntity = projection.size() == 1 && subQueryMetadata.getJoins().get(0).getTarget().accept(new JoinTargetAliasPathResolver(), null).equals(projection.get(0));

                        if (bindEntity) {
                            EntityMetamodel metamodel = criteriaBuilderFactory.getService(EntityMetamodel.class);
                            Path<?> pathExpression = (Path<?>) projection.get(0);

                            ExtendedManagedType<?> managedType = metamodel.getManagedType(ExtendedManagedType.class, pathExpression.getType());
                            Map<String, ? extends ExtendedAttribute<?, ?>> ownedSingularAttributes = managedType.getOwnedSingularAttributes();

                            for (Map.Entry<String, ? extends ExtendedAttribute<?,?>> ownedSingularAttribute : ownedSingularAttributes.entrySet()) {
                                String attributeName = ownedSingularAttribute.getKey();
                                ExtendedAttribute<?, ?> attribute = ownedSingularAttribute.getValue();

                                if (!JpaMetamodelUtils.isAssociation(attribute.getAttribute())) {
                                    SelectBuilder<?> bindBuilder = selectBaseCriteriaBuilder.bind(attributeName);
                                    BeanPath<?> beanPath = new BeanPath<Object>(attribute.getElementClass(), pathExpression, attributeName);
                                    setExpressionSubqueries(beanPath, bindBuilder::select, bindBuilder::selectSubqueries);
                                }
                            }
                        } else {
                            for (int i = 0; i < projection.size(); i++) {
                                Expression<?> projExpression = projection.get(i);
                                Path<?> alias = null;
                                Path<?> cteEntityPath = null;

                                if (projExpression instanceof Operation) {
                                    Operation<?> projOperation = (Operation<?>) projExpression;
                                    if (projOperation.getOperator() == BIND) {
                                        alias = (Path<?>) projOperation.getArg(1);
                                        cteEntityPath = alias.getRoot();
                                        projExpression = projOperation.getArg(0);
                                    }
                                }
                                if (alias == null && cteAliases != null) {
                                    alias = cteAliases.get(i);
                                }

                                if (alias != null) {
                                    String aliasString = relativePathString(cteEntityPath, alias);
                                    SelectBuilder<?> bindBuilder = selectBaseCriteriaBuilder.bind(aliasString);
                                    setExpressionSubqueries(projExpression, bindBuilder::select, bindBuilder::selectSubqueries);
                                }
                            }
                        }
                    } else {
                        for (Expression<?> selection : projection) {
                            renderSingleSelect(selection, (SelectBuilder<?>) criteriaBuilder);
                        }
                    }
                }

                renderOrderBy(subQueryMetadata, (OrderByBuilder<?>) criteriaBuilder);
                renderParameters(subQueryMetadata, (ParameterHolder<?>) criteriaBuilder);
                renderConstants((ParameterHolder<?>) criteriaBuilder);
                renderModifiers(subQueryMetadata.getModifiers(), (LimitBuilder<?>) criteriaBuilder);
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
                    flag.accept(serializer, null);
                    break;
            }
        }
    }

    private <T extends WindowContainerBuilder<T>> T renderNamedWindows(QueryMetadata subQueryMetadata, T windowContainerBuilder) {
        for (QueryFlag queryFlag : subQueryMetadata.getFlags()) {
            Expression<?> flag = queryFlag.getFlag();
            Position position = queryFlag.getPosition();
            switch (position) {
                case AFTER_HAVING:
                    windowContainerBuilder = renderWindowFlag(queryFlag, windowContainerBuilder);
                    break;
            }
        }
        return windowContainerBuilder;
    }

    private <T extends WindowContainerBuilder<T>> T renderWindowFlag(QueryFlag queryFlag, WindowContainerBuilder<T> windowContainerBuilder) {
        NamedWindow namedWindow = (NamedWindow) queryFlag.getFlag();
        WindowBuilder<T> window = windowContainerBuilder.window(namedWindow.getAlias());

        for (Expression<?> expression : namedWindow.getPartitionBy()) {
            window.partitionBy(renderExpression(expression));
        }

        for (OrderSpecifier<?> orderSpecifier : namedWindow.getOrderBy()) {
            renderOrderSpecifier(orderSpecifier, window);
        }

        if (namedWindow.getFrameMode() != null) {
            WindowFrameBuilder<T> windowFrameBuilder = namedWindow.getFrameMode().equals(WindowFrameMode.RANGE) ?
                    window.range() : namedWindow.getFrameMode().equals(WindowFrameMode.ROWS) ?
                    window.rows() : window.groups();

            WindowFrameExclusionBuilder<T> frameExclusionBuilder = null;

            if (namedWindow.getFrameEndType() != null) {
                WindowFrameBetweenBuilder<T> betweenBuilder = null;
                switch (namedWindow.getFrameStartType()) {
                    case UNBOUNDED_PRECEDING:
                        betweenBuilder = windowFrameBuilder.betweenUnboundedPreceding();
                        break;
                    case BOUNDED_PRECEDING:
                        betweenBuilder = windowFrameBuilder.betweenPreceding(renderExpression(namedWindow.getFrameStartExpression()));
                        break;
                    case CURRENT_ROW:
                        betweenBuilder = windowFrameBuilder.betweenCurrentRow();
                        break;
                    case BOUNDED_FOLLOWING:
                        betweenBuilder = windowFrameBuilder.betweenFollowing(renderExpression(namedWindow.getFrameStartExpression()));
                        break;
                }

                switch (namedWindow.getFrameEndType()) {
                    case BOUNDED_PRECEDING:
                        frameExclusionBuilder = betweenBuilder.andPreceding(renderExpression(namedWindow.getFrameEndExpression()));
                        break;
                    case CURRENT_ROW:
                        frameExclusionBuilder = betweenBuilder.andCurrentRow();
                        break;
                    case UNBOUNDED_FOLLOWING:
                        frameExclusionBuilder = betweenBuilder.andUnboundedFollowing();
                        break;
                    case BOUNDED_FOLLOWING:
                        frameExclusionBuilder = betweenBuilder.andFollowing(renderExpression(namedWindow.getFrameEndExpression()));
                        break;
                }
            } else {
                switch (namedWindow.getFrameStartType()) {
                    case UNBOUNDED_PRECEDING:
                        frameExclusionBuilder = windowFrameBuilder.unboundedPreceding();
                        break;
                    case BOUNDED_PRECEDING:
                        frameExclusionBuilder = windowFrameBuilder.preceding(renderExpression(namedWindow.getFrameStartExpression()));
                        break;
                    case CURRENT_ROW:
                        frameExclusionBuilder = windowFrameBuilder.currentRow();
                        break;
                }
            }

            return frameExclusionBuilder.end();
        } else {
            return window.end();
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
            try {
                criteriaBuilder.setParameter(entry.getValue(), entry.getKey());
            }
            catch (Exception e) {
                throw e;
            }
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
                Class type = valuesExpression.getRoot().getType();
                String name = valuesExpression.getAlias().getMetadata().getName();
                Collection<?> elements = valuesExpression.getElements();

                if (! valuesExpression.getMetadata().isRoot()) {
                    String attribute = relativePathString(valuesExpression.getRoot(), valuesExpression);
                    if ( valuesExpression.isIdentifiable() ) {
                        criteriaBuilder = (X) fromBuilder.fromIdentifiableValues(type, attribute, name, elements);
                    } else {
                        criteriaBuilder = (X) fromBuilder.fromValues(type, attribute, name, elements);
                    }
                } else if ( valuesExpression.isIdentifiable() ) {
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

                            // TODO find a clean way to detect pre-set FROM clauses...
                            if (from != null) {
                                if (entityPath instanceof CollectionExpressionBase<?,?> &&
                                        ((CollectionExpressionBase<?, ?>) entityPath).getElementType().equals(from.getJavaType()) ||
                                        from.getJavaType().equals(entityPath.getType())) {
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
                    case DEFAULT: {
                        FullSelectCTECriteriaBuilder<X> xFullSelectCTECriteriaBuilder = fromBuilder.fromSubquery(target.getType(), alias);
                        Object o = serializeSubQuery(xFullSelectCTECriteriaBuilder, target);
                        criteriaBuilder = o instanceof FinalSetOperationCTECriteriaBuilder ?
                                ((FinalSetOperationCTECriteriaBuilder<X>) o).end() :
                                ((FullSelectCTECriteriaBuilder<X>) o).end();
                        break;
                    }
                    default: {
                        JoinType joinType = getJoinType(joinExpression);
                        boolean isLateral = joinExpression.hasFlag(AbstractBlazeJPAQuery.LATERAL);

                        if (fetch) {
                            logger.warning("Fetch is ignored due to subquery entity join!");
                        }

                        SubQueryExpression<?> subQueryExpression = target.accept(new FirstSubqueryResolver(), null);
                        Path<?> fromPath = subQueryExpression.getMetadata().getJoins().get(0).getTarget().accept(new FirstSubqueryTargetPathResolver(), null);
                        boolean entityJoin = fromPath.getMetadata().isRoot();

                        if (hasCondition) {
                            FullSelectCTECriteriaBuilder<JoinOnBuilder<X>> joinOnBuilderFullSelectCTECriteriaBuilder;

                            if (isLateral) {
                                // TODO: Visit for SET operations
                                String subqueryAlias = subQueryExpression.getMetadata().getJoins().get(0).getTarget().accept(new JoinTargetAliasPathResolver(), null).getMetadata().getName();
                                if (entityJoin) {
                                    joinOnBuilderFullSelectCTECriteriaBuilder = criteriaBuilder.joinLateralOnSubquery(target.getType(), alias, joinType);
                                } else {
                                    joinOnBuilderFullSelectCTECriteriaBuilder = criteriaBuilder.joinLateralOnSubquery(renderExpression(fromPath), alias, subqueryAlias, joinType);
                                }
                            } else {
                                if (!entityJoin) {
                                    throw new IllegalStateException("Entity join to association");
                                }
                                joinOnBuilderFullSelectCTECriteriaBuilder = criteriaBuilder.joinOnSubquery(target.getType(), alias, joinType);
                            }

                            Object o = serializeSubQuery(joinOnBuilderFullSelectCTECriteriaBuilder, target);
                            JoinOnBuilder<X> joinOnBuilder = o instanceof FinalSetOperationCTECriteriaBuilder ?
                                    ((FinalSetOperationCTECriteriaBuilder<JoinOnBuilder<X>>) o).end() :
                                    ((FullSelectCTECriteriaBuilder<JoinOnBuilder<X>>) o).end();
                            criteriaBuilder = setExpressionSubqueries(joinExpression.getCondition(), joinOnBuilder::setOnExpression, joinOnBuilder::setOnExpressionSubqueries);
                        } else {
                            if (isLateral) {
                                FullSelectCTECriteriaBuilder<X> xFullSelectCTECriteriaBuilder;
                                String subqueryAlias = subQueryExpression.getMetadata().getJoins().get(0).getTarget().accept(new JoinTargetAliasPathResolver(), null).getMetadata().getName();

                                if (entityJoin) {
                                    xFullSelectCTECriteriaBuilder = criteriaBuilder.joinLateralSubquery(target.getType(), alias, joinType);
                                } else {
                                    xFullSelectCTECriteriaBuilder = criteriaBuilder.joinLateralSubquery(renderExpression(fromPath), alias, subqueryAlias, joinType);
                                }

                                Object o = serializeSubQuery(xFullSelectCTECriteriaBuilder, target);
                                criteriaBuilder = o instanceof FinalSetOperationCTECriteriaBuilder ?
                                        ((FinalSetOperationCTECriteriaBuilder<X>) o).end() :
                                        ((FullSelectCTECriteriaBuilder<X>) o).end();

                            } else {
                                throw new IllegalStateException("No on-clause for subquery entity join!");
                            }
                        }
                        break;
                    }
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
            return Collections.singletonList(expr);
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
            default:
                throw new IllegalArgumentException("Null handling not implemented for " + orderSpecifier.getNullHandling());
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

    private String renderExpression(Expression<?> select) {
        serializer.clearBuffer();
        select.accept(serializer, null);
        return serializer.takeBuffer();
    }

    private final List<SubqueryInitiator<?>> subqueryInitiatorStack = new ArrayList<SubqueryInitiator<?>>();;

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

    private List<Path<?>> cteAliases;

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
                    o = ((SubqueryBuilder<?>) o).end();
                } else if (o instanceof FinalSetOperationSubqueryBuilder) {
                    o = ((FinalSetOperationSubqueryBuilder<?>) o).end();
                }

                assert subqueryInitiator == o : "Expected SubqueryInitiator to return original MultipleSubqueryInitiator";
                popSubqueryInitiator();
            }
            return subqueryInitiator.end();
        }
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

    private static class CteAttributesVisitor extends DefaultVisitorImpl<List<Path<?>>, List<Path<?>>> {

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
        public List<Path<?>> visit(Path<?> expr, List<Path<?>> context) {
            context.add(expr);
            return context;
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

    private static class JoinTargetAliasPathResolver extends DefaultVisitorImpl<Path<?>, Void> {
        @Override
        public Path<?> visit(Operation<?> operation, Void aVoid) {
            return operation.getArg(1).accept(this, aVoid);
        }

        @Override
        public Path<?> visit(Path<?> path, Void aVoid) {
            return path;
        }
    }

    private static class FirstSubqueryTargetPathResolver extends DefaultVisitorImpl<Path<?>, Object> {
        @Override
        public Path<?> visit(Operation<?> operation, Object o) {
            return operation.getArg(0).accept(this, o);
        }

        @Override
        public Path<?> visit(Path<?> path, Object o) {
            return path;
        }

        @Override
        public Path<?> visit(SubQueryExpression<?> subQueryExpression, Object o) {
            return subQueryExpression.getMetadata().getJoins().get(0).getTarget().accept(this, o);
        }
    }

    private static class FirstSubqueryResolver extends DefaultVisitorImpl<SubQueryExpression<?>, Object> {
        @Override
        public SubQueryExpression<?> visit(Operation<?> operation, Object o) {
            return operation.getArg(0).accept(this, o);
        }

        @Override
        public SubQueryExpression<?> visit(SubQueryExpression<?> subQueryExpression, Object o) {
            SetOperationFlag setOperationFlag = getSetOperationFlag(subQueryExpression.getMetadata());
            if (setOperationFlag != null) {
                return setOperationFlag.getFlag().accept(this, o);
            }
            return subQueryExpression;
        }
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

    private static class GetOperationVisitor extends DefaultVisitorImpl<Operation<?>, Void> {

        public static final GetOperationVisitor INSTANCE = new GetOperationVisitor();

        @Override
        public Operation<?> visit(Operation<?> operation, Void aVoid) {
            return operation;
        }

    }

    private static class GetSubQueryVisitor extends DefaultVisitorImpl<SubQueryExpression<?>, Void> {

        public static final GetSubQueryVisitor INSTANCE = new GetSubQueryVisitor();

        @Override
        public SubQueryExpression<?> visit(SubQueryExpression<?> subQueryExpression, Void aVoid) {
            return subQueryExpression;
        }
    }

    private class JPQLNextExpressionSerializer extends JPQLNextSerializer {

        private final JPQLTemplates templates;

        public JPQLNextExpressionSerializer(JPQLTemplates templates, EntityManager entityManager) {
            super(templates, entityManager);
            this.templates = templates;
        }

        @Override
        public void visitConstant(Object constant) {
            // TODO Handle in case operations
            boolean wrap = templates.wrapConstant(constant);
            if (wrap) append("(");
            append(":");
            append(constantToLabel.computeIfAbsent(constant, o -> "param_" + constantToLabel.size()));
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
            serializer.append(subQueryToLabel.computeIfAbsent(query, o -> "generatedSubquery_" + (subQueryToLabel.size() + 1)));
        }

        @Override
        protected void visitOperation(final Class<?> type, final Operator operator, final List<? extends Expression<?>> args) {
            if (operator instanceof JPQLNextOps) {
                switch ((JPQLNextOps) operator) {
                    case WITH_RECURSIVE_ALIAS:
                    case WITH_ALIAS:
                        boolean recursive = operator == WITH_RECURSIVE_ALIAS;
                        Expression<?> withColumns = args.get(0);
                        Expression<?> subQueryExpression = args.get(1);
                        withColumns.accept(this, null);
                        Class<?> cteType = withColumns.getType();

                        if (recursive) {
                            Operation<?> unionOperation = subQueryExpression.accept(GetOperationVisitor.INSTANCE, null);
                            if (unionOperation == null) {
                                SubQueryExpression<?> setSubquery = subQueryExpression.accept(GetSubQueryVisitor.INSTANCE, null);
                                QueryFlag setFlag = getSetOperationFlag(setSubquery.getMetadata());
                                unionOperation = setFlag.getFlag().accept(GetOperationVisitor.INSTANCE, null);
                            }

                            SubQueryExpression<?> subQuery = (SubQueryExpression<?>) unionOperation.getArg(0);
                            SelectRecursiveCTECriteriaBuilder<?> baseCriteriaBuilder =
                                    (SelectRecursiveCTECriteriaBuilder<?>)
                                            serializeSubQuery(criteriaBuilder.withRecursive(cteType), subQuery);
                            SelectCTECriteriaBuilder<?> recursiveCriteriaBuilder = unionOperation.getOperator() == SET_UNION ?
                                    baseCriteriaBuilder.union() : baseCriteriaBuilder.unionAll();
                            subQuery = (SubQueryExpression<?>) unionOperation.getArg(1);
                            ((SelectCTECriteriaBuilder<?>) serializeSubQuery(recursiveCriteriaBuilder, subQuery)).end();
                        } else {
                            FullSelectCTECriteriaBuilder<?> cteBuilder = criteriaBuilder.with(cteType);

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
                        cteAliases = args.get(1).accept(new CteAttributesVisitor(), new ArrayList<>());
                        return;
                }
            }

            // JPQLSerializer calls serialize transitively,
            if (operator == Ops.EXISTS && args.get(0) instanceof SubQueryExpression) {
                append("EXISTS (");
                renderSubQueryExpression(args.get(0));
                append(")");
                return;
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
                    case LEFT_NESTED_SET_UNION:
                    case LEFT_NESTED_SET_UNION_ALL:
                    case LEFT_NESTED_SET_INTERSECT:
                    case LEFT_NESTED_SET_INTERSECT_ALL:
                    case LEFT_NESTED_SET_EXCEPT:
                    case LEFT_NESTED_SET_EXCEPT_ALL:
                        renderSubQueryExpression(expr);
                        return null;
                }
            }

            return super.visit(expr, context);
        }

    }
}
