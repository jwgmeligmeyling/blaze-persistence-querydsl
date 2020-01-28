package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.FullQueryBuilder;
import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.Queryable;
import com.pallasathenagroup.querydsl.impl.BlazeCriteriaVisitor;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.NonUniqueResultException;
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
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.AbstractJPAQuery;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class AbstractBlazeJPAQuery<T, Q extends AbstractBlazeJPAQuery<T, Q>> extends AbstractJPAQuery<T, Q> implements ExtendedJPAQuery<T, Q>, ExtendedFetchable<T> {

    protected final CriteriaBuilderFactory criteriaBuilderFactory;

    @Nullable
    protected Expression<?> union;

    protected boolean cachable = false;

    public AbstractBlazeJPAQuery(CriteriaBuilderFactory criteriaBuilderFactory) {
        super(null, JPQLNextTemplates.DEFAULT, new DefaultQueryMetadata());
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public AbstractBlazeJPAQuery(EntityManager em, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, JPQLNextTemplates.DEFAULT, new DefaultQueryMetadata());
        this.criteriaBuilderFactory = criteriaBuilderFactory;
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
    public <R,C> R accept(Visitor<R,C> v, @Nullable C context) {
        if (union != null) {
            return union.accept(v, context);
        } else {
            return super.accept(v, context);
        }
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
    public WithBuilder<Q> with(EntityPath<?> alias, Path<?>... columns) {
        Expression<Object> columnsCombined = ExpressionUtils.list(Object.class, columns);
        Expression<?> aliasCombined = Expressions.operation(alias.getType(), JPQLNextOps.WITH_COLUMNS, alias, columnsCombined);
        return new WithBuilder<>(queryMixin, aliasCombined);
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
        Queryable<T, ?> criteriaBuilder = getCriteriaBuilder(modifiers);

        if (forCount) {
            return ((FullQueryBuilder<T, ?>) criteriaBuilder).getCountQuery();
        }

        String queryString = criteriaBuilder.getQueryString();
        System.out.println(queryString);
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
        return ((FullQueryBuilder<T,?>) getCriteriaBuilder(getMetadata().getModifiers()))
                .page(firstResult, maxResults)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public PagedList<T> fetchPage(KeysetPage keysetPage, int firstResult, int maxResults) {
        return ((FullQueryBuilder<T,?>) getCriteriaBuilder(getMetadata().getModifiers()))
                .page(keysetPage, firstResult, maxResults)
                .getResultList();
    }

    protected Queryable<T, ?> getCriteriaBuilder(@Nullable QueryModifiers modifiers) {
        BlazeCriteriaVisitor<T> blazeCriteriaVisitor = new BlazeCriteriaVisitor<>(criteriaBuilderFactory, entityManager, getTemplates());
        blazeCriteriaVisitor.serialize(this);
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
        this.union = query.union;
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

    // End full joins

    public Q setCacheable(boolean cacheable) {
        this.cachable = cacheable;
        return queryMixin.getSelf();
    }

    // Union stuff

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <RT> SetOperation<RT> setOperation(JPQLNextOps operator, List<SubQueryExpression<RT>> sq) {
        queryMixin.setProjection(sq.get(0).getMetadata().getProjection());
        if (!queryMixin.getMetadata().getJoins().isEmpty()) {
            throw new IllegalArgumentException("Don't mix union and from");
        }
        this.union = SetUtils.setOperation(operator, sq.toArray(new Expression[0]));
        return new SetOperationImpl(this);
    }

    @Override
    public <RT> SetOperation<RT> union(List<SubQueryExpression<RT>> sq) {
        return setOperation(JPQLNextOps.SET_UNION, sq);
    }

    @Override
    public <RT> SetOperation<RT> unionAll(List<SubQueryExpression<RT>> sq) {
        return setOperation(JPQLNextOps.SET_UNION_ALL, sq);
    }

    @Override
    public <RT> SetOperation<RT> intersect(List<SubQueryExpression<RT>> sq) {
        return setOperation(JPQLNextOps.SET_INTERSECT, sq);
    }

    @Override
    public <RT> SetOperation<RT> intersectAll(List<SubQueryExpression<RT>> sq) {
        return setOperation(JPQLNextOps.SET_INTERSECT_ALL, sq);
    }

    @Override
    public <RT> SetOperation<RT> except(List<SubQueryExpression<RT>> sq) {
        return setOperation(JPQLNextOps.SET_EXCEPT, sq);
    }

    @Override
    public <RT> SetOperation<RT> exceptAll(List<SubQueryExpression<RT>> sq) {
        return setOperation(JPQLNextOps.SET_EXCEPT_ALL, sq);
    }

}
