package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.AbstractJPAQuery;
import com.querydsl.jpa.impl.JPAProvider;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BlazeJPAQuery<T> extends AbstractJPAQuery<T, BlazeJPAQuery<T>> {

    protected final CriteriaBuilderFactory criteriaBuilderFactory;

    private boolean cachable = false;

    public BlazeJPAQuery(CriteriaBuilderFactory criteriaBuilderFactory) {
        super(null, JPQLNextTemplates.DEFAULT, new DefaultQueryMetadata());
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public BlazeJPAQuery(EntityManager em, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, JPQLNextTemplates.DEFAULT, new DefaultQueryMetadata());
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public BlazeJPAQuery(EntityManager em, QueryMetadata metadata, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, JPQLNextTemplates.DEFAULT, metadata);
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public BlazeJPAQuery(EntityManager em, JPQLTemplates templates, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, templates, new DefaultQueryMetadata());
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public BlazeJPAQuery(EntityManager em, JPQLTemplates templates, QueryMetadata metadata, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, templates, metadata);
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public <X> BlazeJPAQuery<T> fromValues(EntityPath<X> path, Collection<X> elements) {
        this.queryMixin.from(new ValuesExpression<>(path, elements, false));
        return this;
    }

    public <X> BlazeJPAQuery<T> fromIdentifiableValues(EntityPath<X> path, Collection<X> elements) {
        this.queryMixin.from(new ValuesExpression<>(path, elements, true));
        return this;
    }

    public WithBuilder<BlazeJPAQuery<T>> with(EntityPath<?> alias, Path<?>... columns) {
        Expression<Object> columnsCombined = ExpressionUtils.list(Object.class, columns);
        Expression<?> aliasCombined = Expressions.operation(alias.getType(), JPQLNextOps.WITH_COLUMNS, alias, columnsCombined);
        return new WithBuilder<>(queryMixin, aliasCombined);
    }

    public WithBuilder<BlazeJPAQuery<T>> withRecursive(EntityPath<?> alias, Path<?>... columns) {
        Expression<Object> columnsCombined = ExpressionUtils.list(Object.class, columns);
        Expression<?> aliasCombined = Expressions.operation(alias.getType(), JPQLNextOps.WITH_RECURSIVE_COLUMNS, alias, columnsCombined);
        return new WithBuilder<>(queryMixin, aliasCombined);
    }

    @Override
    public Query createQuery() {
        return createQuery(getMetadata().getModifiers(), false);
    }

    // TODO @Override
    protected Query createQuery(@Nullable QueryModifiers modifiers, boolean forCount) {
        CriteriaBuilder<T> criteriaBuilder = getCriteriaBuilder(modifiers);

        if (forCount) {
            return criteriaBuilder.getCountQuery();
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

    public PagedList<T> fetchPage(int firstResult, int maxResults) {
        return getCriteriaBuilder(getMetadata().getModifiers())
                .page(firstResult, maxResults)
                .getResultList();
    }

    public PagedList<T> fetchPage(KeysetPage keysetPage, int firstResult, int maxResults) {
        return getCriteriaBuilder(getMetadata().getModifiers())
                .page(keysetPage, firstResult, maxResults)
                .getResultList();
    }

    protected CriteriaBuilder<T> getCriteriaBuilder(@Nullable QueryModifiers modifiers) {
        BlazeCriteriaVisitor<T> blazeCriteriaVisitor = new BlazeCriteriaVisitor<>(criteriaBuilderFactory, entityManager, getTemplates());
        blazeCriteriaVisitor.serialize(getMetadata(), false, null);
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

        return criteriaBuilder;
    }


    @Override
    public BlazeJPAQuery<T> clone(EntityManager entityManager, JPQLTemplates templates) {
        BlazeJPAQuery<T> q = new BlazeJPAQuery<T>(entityManager, templates, getMetadata().clone(), criteriaBuilderFactory);
        q.clone(this);
        q.cachable = cachable;
        return q;
    }

    @Override
    public BlazeJPAQuery<T> clone(EntityManager entityManager) {
        return clone(entityManager, JPAProvider.getTemplates(entityManager));
    }

    @Override
    public <U> BlazeJPAQuery<U> select(Expression<U> expr) {
        queryMixin.setProjection(expr);
        @SuppressWarnings("unchecked") // This is the new type
                BlazeJPAQuery<U> newType = (BlazeJPAQuery<U>) this;
        return newType;
    }

    @Override
    public BlazeJPAQuery<Tuple> select(Expression<?>... exprs) {
        queryMixin.setProjection(exprs);
        @SuppressWarnings("unchecked") // This is the new type
                BlazeJPAQuery<Tuple> newType = (BlazeJPAQuery<Tuple>) this;
        return newType;
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
                return new QueryResults<T>(list, modifiers, total);
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
            List<Object> rv = new ArrayList<Object>(results.size());
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
    public <P> BlazeJPAQuery<T> fullJoin(CollectionExpression<?,P> target) {
        return queryMixin.fullJoin(target);
    }

    public <P> BlazeJPAQuery<T> fullJoin(CollectionExpression<?,P>target, Path<P> alias) {
        return queryMixin.fullJoin(target, alias);
    }

    public <P> BlazeJPAQuery<T>  fullJoin(EntityPath<P> target) {
        return queryMixin.fullJoin(target);
    }

    public <P> BlazeJPAQuery<T>  fullJoin(EntityPath<P> target, Path<P> alias) {
        return queryMixin.fullJoin(target, alias);
    }

    public <P> BlazeJPAQuery<T>  fullJoin(MapExpression<?,P> target) {
        return queryMixin.fullJoin(target);
    }

    public <P> BlazeJPAQuery<T>  fullJoin(MapExpression<?,P> target, Path<P> alias) {
        return queryMixin.fullJoin(target, alias);
    }

    // End full joins

    public void setCacheable(boolean cacheable) {
        this.cachable = cacheable;
    }

}
