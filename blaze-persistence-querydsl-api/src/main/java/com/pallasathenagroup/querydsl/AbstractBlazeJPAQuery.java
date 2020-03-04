package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.FullQueryBuilder;
import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.Queryable;
import com.pallasathenagroup.querydsl.impl.BlazeCriteriaVisitor;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.AbstractJPAQuery;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class AbstractBlazeJPAQuery<T, Q extends AbstractBlazeJPAQuery<T, Q>> extends AbstractJPAQuery<T, Q> implements ExtendedJPAQuery<T, Q>, ExtendedFetchable<T> {

    public static final JoinFlag LATERAL = new JoinFlag("LATERAL", JoinFlag.Position.BEFORE_TARGET);

    protected final CriteriaBuilderFactory criteriaBuilderFactory;

    protected boolean cachable = false;

    public AbstractBlazeJPAQuery(CriteriaBuilderFactory criteriaBuilderFactory) {
        super(null, JPQLNextTemplates.DEFAULT, new DefaultQueryMetadata());
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public AbstractBlazeJPAQuery(EntityManager em, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, JPQLNextTemplates.DEFAULT, new DefaultQueryMetadata());
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    @Override
    protected BlazeCriteriaVisitor<T> createSerializer() {
        return new BlazeCriteriaVisitor<T>(criteriaBuilderFactory, entityManager, getTemplates());
    }

    public AbstractBlazeJPAQuery(EntityManager em, QueryMetadata metadata, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, JPQLNextTemplates.DEFAULT, metadata);
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public AbstractBlazeJPAQuery(EntityManager em, JPQLTemplates templates, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, templates, new DefaultQueryMetadata());
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public AbstractBlazeJPAQuery(EntityManager em, JPQLTemplates templates, QueryMetadata metadata, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, templates, metadata);
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    @Override
    public <X> Q fromValues(EntityPath<X> path, Collection<X> elements) {
        return this.queryMixin.from(new ValuesExpression<>(path, elements, false));
    }

    @Override
    public <X> Q fromIdentifiableValues(EntityPath<X> path, Collection<X> elements) {
        return this.queryMixin.from(new ValuesExpression<>(path, elements, true));
    }

    @Override
    public <X> Q fromValues(Path<X> path, Path<X> alias, Collection<X> elements) {
        return this.queryMixin.from(new ValuesExpression<>(path, alias, elements, false));
    }

    @Override
    public <X> Q fromIdentifiableValues(Path<X> path, Path<X> alias, Collection<X> elements) {
        return this.queryMixin.from(new ValuesExpression<>(path, alias, elements, true));
    }

    @Override
    public WithBuilder<Q> with(EntityPath<?> alias, Path<?>... columns) {
        Expression<Object> columnsCombined = ExpressionUtils.list(Object.class, columns);
        Expression<?> aliasCombined = Expressions.operation(alias.getType(), JPQLNextOps.WITH_COLUMNS, alias, columnsCombined);
        return new WithBuilder<>(queryMixin, aliasCombined);
    }

    @Override
    public <X> Q with(Path<X> alias, SubQueryExpression<X> o) {
        Expression<?> expr = ExpressionUtils.operation(alias.getType(), JPQLNextOps.WITH_ALIAS, alias, o);
        return queryMixin.addFlag(new QueryFlag(QueryFlag.Position.WITH, expr));
    }

    @Override
    public <X> Q withRecursive(Path<X> alias, SubQueryExpression<X> o) {
        // TODO recursive
        Expression<?> expr = ExpressionUtils.operation(alias.getType(), JPQLNextOps.WITH_ALIAS, alias, o);
        return queryMixin.addFlag(new QueryFlag(QueryFlag.Position.WITH, expr));
    }

    @Override
    public WithBuilder<Q> withRecursive(EntityPath<?> alias, Path<?>... columns) {
        Expression<Object> columnsCombined = ExpressionUtils.list(Object.class, columns);
        Expression<?> aliasCombined = Expressions.operation(alias.getType(), JPQLNextOps.WITH_RECURSIVE_COLUMNS, alias, columnsCombined);
        return new WithBuilder<>(queryMixin, aliasCombined);
    }

    @Override
    public Query createQuery() {
        return createQuery(getMetadata().getModifiers(), false);
    }

    // TODO @Override
    @SuppressWarnings("unchecked")
    protected Query createQuery(@Nullable QueryModifiers modifiers, boolean forCount) {
        Queryable<T, ?> criteriaBuilder = getQueryable(modifiers);

        if (forCount) {
            return ((FullQueryBuilder<T, ?>) criteriaBuilder).getCountQuery();
        }

        TypedQuery<T> query = criteriaBuilder.getQuery();

        if (lockMode != null) {
            query.setLockMode(lockMode);
        }
        if (flushMode != null) {
            query.setFlushMode(flushMode);
        }

        for (Map.Entry<String, Object> entry : hints.entries()) {
            query.setHint(entry.getKey(), entry.getValue());
        }

        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PagedList<T> fetchPage(int firstResult, int maxResults) {
        return ((FullQueryBuilder<T,?>) getQueryable(getMetadata().getModifiers()))
                .page(firstResult, maxResults)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public PagedList<T> fetchPage(KeysetPage keysetPage, int firstResult, int maxResults) {
        return ((FullQueryBuilder<T,?>) getQueryable(getMetadata().getModifiers()))
                .page(keysetPage, firstResult, maxResults)
                .getResultList();
    }

    @Override
    protected BlazeCriteriaVisitor<T> serialize(boolean forCountRow, boolean validate) {
        if (validate) {
            if (queryMixin.getMetadata().getJoins().isEmpty()) {
                throw new IllegalArgumentException("No sources given");
            }
        }
        BlazeCriteriaVisitor<T> blazeCriteriaVisitor = createSerializer();
        blazeCriteriaVisitor.serialize(this);
        return blazeCriteriaVisitor;
    }

    public String getQueryString() {
        return getQueryable(null).getQueryString();
    }

    protected Queryable<T, ?> getQueryable(@Nullable QueryModifiers modifiers) {
        BlazeCriteriaVisitor<T> blazeCriteriaVisitor = serialize(false, false);
        CriteriaBuilder<T> criteriaBuilder = blazeCriteriaVisitor.getCriteriaBuilder();

        if (modifiers != null) {
            if (modifiers.getLimitAsInteger() != null) {
                criteriaBuilder.setMaxResults(modifiers.getLimitAsInteger());
            }
            if (modifiers.getOffsetAsInteger() != null) {
                criteriaBuilder.setFirstResult(modifiers.getOffsetAsInteger());
            }
        }

        for (Map.Entry<String, Object> entry : hints.entries()) {
            if (entry.getValue() instanceof String) {
                criteriaBuilder.setProperty(entry.getKey(), (String) entry.getValue());
            }
        }

        if (cachable) {
            criteriaBuilder.setCacheable(true);
        }

        return blazeCriteriaVisitor.getQueryable();
    }

    @Override
    protected void clone(Q query) {
        super.clone(query);
        this.cachable = query.cachable;
    }

    // Work around private access to query(...)

    @Override
    public long fetchCount() {
        try {
            Query query = createQuery(null, true);
            return (Long) query.getSingleResult();
        } finally {
            reset();
        }
    }

    @Override
    public QueryResults<T> fetchResults() {
        try {
            Query countQuery = createQuery(null, true);
            long total = (Long) countQuery.getSingleResult();
            if (total > 0) {
                QueryModifiers modifiers = getMetadata().getModifiers();
                Query query = createQuery(modifiers, false);
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) getResultList(query);
                return new QueryResults<>(list, modifiers, total);
            } else {
                return QueryResults.emptyResults();
            }
        } finally {
            reset();
        }

    }

    private List<?> getResultList(Query query) {
        // TODO : use lazy fetch here?
        if (projection != null) {
            List<?> results = query.getResultList();
            List<Object> rv = new ArrayList<>(results.size());
            for (Object o : results) {
                if (o != null) {
                    if (!o.getClass().isArray()) {
                        o = new Object[]{o};
                    }
                    rv.add(projection.newInstance((Object[]) o));
                } else {
                    rv.add(null);
                }
            }
            return rv;
        } else {
            return query.getResultList();
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    @Override
    public T fetchOne() throws NonUniqueResultException {
        try {
            Query query = createQuery(getMetadata().getModifiers(), false);
            return (T) getSingleResult(query);
        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (javax.persistence.NonUniqueResultException e) {
            throw new NonUniqueResultException(e);
        } finally {
            reset();
        }
    }

    @Nullable
    private Object getSingleResult(Query query) {
        if (projection != null) {
            Object result = query.getSingleResult();
            if (result != null) {
                if (!result.getClass().isArray()) {
                    result = new Object[]{result};
                }
                return projection.newInstance((Object[]) result);
            } else {
                return null;
            }
        } else {
            return query.getSingleResult();
        }
    }

    // End workaround

    // Full joins
    @Override
    public <P> Q fullJoin(CollectionExpression<?,P> target) {
        return queryMixin.fullJoin(target);
    }

    @Override
    public <P> Q fullJoin(CollectionExpression<?,P>target, Path<P> alias) {
        return queryMixin.fullJoin(target, alias);
    }

    @Override
    public <P> Q fullJoin(EntityPath<P> target) {
        return queryMixin.fullJoin(target);
    }

    @Override
    public <P> Q fullJoin(EntityPath<P> target, Path<P> alias) {
        return queryMixin.fullJoin(target, alias);
    }

    @Override
    public <P> Q fullJoin(MapExpression<?,P> target) {
        return queryMixin.fullJoin(target);
    }

    @Override
    public <P> Q fullJoin(MapExpression<?,P> target, Path<P> alias) {
        return queryMixin.fullJoin(target, alias);
    }

    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public <X> Q from(SubQueryExpression<X> subQueryExpression, Path<X> alias) {
        return (Q) queryMixin.from(ExpressionUtils.as((Expression) subQueryExpression, alias));
    }

    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public <X> Q leftJoin(SubQueryExpression<X> o, Path<X> alias) {
        return (Q) queryMixin.leftJoin((Expression) o, alias);
    }

    @Override
    public Q lateral() {
        return queryMixin.addJoinFlag(LATERAL);
    }

    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public <X> Q rightJoin(SubQueryExpression<X> o, Path<X> alias) {
        return (Q) queryMixin.rightJoin((Expression) o, alias);
    }

    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public <X> Q fullJoin(SubQueryExpression<X> o, Path<X> alias) {
        return (Q) queryMixin.fullJoin((Expression) o, alias);
    }

    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public <X> Q innerJoin(SubQueryExpression<X> o, Path<X> alias) {
        return (Q) queryMixin.innerJoin((Expression) o, alias);
    }

    // End full joins

    public Q setCacheable(boolean cacheable) {
        this.cachable = cacheable;
        return queryMixin.getSelf();
    }

    @Override
    public String toString() {
        return getQueryable(null).getQueryString();
    }

    // Union stuff

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <RT> SetExpression<RT> setOperation(JPQLNextOps operator, List<SubQueryExpression<RT>> sq) {
        queryMixin.setProjection(sq.get(0).getMetadata().getProjection());
        if (!queryMixin.getMetadata().getJoins().isEmpty()) {
            throw new IllegalArgumentException("Don't mix union and from");
        }

        this.queryMixin.addFlag(new SetOperationFlag(SetUtils.setOperation(operator, sq.toArray(new Expression[0]))));
        return new SetExpressionImpl(this);
    }

    @Override
    public <RT> SetExpression<RT> union(List<SubQueryExpression<RT>> sq) {
        return setOperation(JPQLNextOps.SET_UNION, sq);
    }

    @Override
    public <RT> SetExpression<RT> unionAll(List<SubQueryExpression<RT>> sq) {
        return setOperation(JPQLNextOps.SET_UNION_ALL, sq);
    }

    @Override
    public <RT> SetExpression<RT> intersect(List<SubQueryExpression<RT>> sq) {
        return setOperation(JPQLNextOps.SET_INTERSECT, sq);
    }

    @Override
    public <RT> SetExpression<RT> intersectAll(List<SubQueryExpression<RT>> sq) {
        return setOperation(JPQLNextOps.SET_INTERSECT_ALL, sq);
    }

    @Override
    public <RT> SetExpression<RT> except(List<SubQueryExpression<RT>> sq) {
        return setOperation(JPQLNextOps.SET_EXCEPT, sq);
    }

    @Override
    public <RT> SetExpression<RT> exceptAll(List<SubQueryExpression<RT>> sq) {
        return setOperation(JPQLNextOps.SET_EXCEPT_ALL, sq);
    }

    /**
     * Creates an union expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    @SafeVarargs
    public final <RT> SetExpression<RT> union(SubQueryExpression<RT>... sq) {
        return union(Arrays.asList(sq));

    }

    /**
     * Creates an union expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    @SafeVarargs
    public final <RT> SetExpression<RT> unionAll(SubQueryExpression<RT>... sq) {
        return unionAll(Arrays.asList(sq));
    }

    /**
     * Creates an intersect expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    @SafeVarargs
    public final <RT> SetExpression<RT> intersect(SubQueryExpression<RT>... sq) {
        return intersect(Arrays.asList(sq));
    }

    /**
     * Creates an intersect expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    @SafeVarargs
    public final <RT> SetExpression<RT> intersectAll(SubQueryExpression<RT>... sq) {
        return intersectAll(Arrays.asList(sq));
    }

    /**
     * Creates an except expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    @SafeVarargs
    public final <RT> SetExpression<RT> except(SubQueryExpression<RT>... sq) {
        return except(Arrays.asList(sq));
    }

    /**
     * Creates an except expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    @SafeVarargs
    public final <RT> SetExpression<RT> exceptAll(SubQueryExpression<RT>... sq) {
        return exceptAll(Arrays.asList(sq));
    }

    private CTEUtils.Binds<T> binds = new CTEUtils.Binds<>();

    /**
     * Bind a CTE attribute to a select expression.
     *
     * @param path Attribute path
     * @param expression Expression to bind the path to
     * @param <U> Attribute type
     * @return this query
     * @deprecated Fluent API proof of concept
     */
    @Deprecated
    public <U> Q bind(Path<? super U> path, Expression<? extends U> expression) {
        select(binds.bind(path, expression));
        return queryMixin.getSelf();
    }

    @Override
    public Q window(NamedWindow namedWindow) {
        queryMixin.addFlag(new QueryFlag(QueryFlag.Position.AFTER_HAVING, namedWindow));
        return queryMixin.getSelf();
    }

}
