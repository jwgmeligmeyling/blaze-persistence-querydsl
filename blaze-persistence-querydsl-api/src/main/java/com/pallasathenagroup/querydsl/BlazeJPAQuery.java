package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQuery;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class BlazeJPAQuery<T> extends JPAQuery<T> {

    protected final CriteriaBuilderFactory criteriaBuilderFactory;

    public BlazeJPAQuery(CriteriaBuilderFactory criteriaBuilderFactory) {
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public BlazeJPAQuery(EntityManager em, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em);
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public BlazeJPAQuery(EntityManager em, QueryMetadata metadata, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, metadata);
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public BlazeJPAQuery(EntityManager em, JPQLTemplates templates, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, templates);
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    public BlazeJPAQuery(EntityManager em, JPQLTemplates templates, QueryMetadata metadata, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, templates, metadata);
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    @Override
    public Query createQuery() {
        return createQuery(getMetadata().getModifiers(), false);
    }

    // TODO @Override
    protected Query createQuery(@Nullable QueryModifiers modifiers, boolean forCount) {
        BlazeCriteriaVisitor<Tuple> blazeCriteriaVisitor = new BlazeCriteriaVisitor<>(criteriaBuilderFactory, entityManager, getTemplates());
        blazeCriteriaVisitor.serialize(getMetadata(), false, null);
        CriteriaBuilder<Tuple> criteriaBuilder = blazeCriteriaVisitor.getCriteriaBuilder();

        if (modifiers != null) {
            if (modifiers.getLimitAsInteger() != null) {
                criteriaBuilder.setMaxResults(modifiers.getLimitAsInteger());
            }
            if (modifiers.getOffsetAsInteger() != null) {
                criteriaBuilder.setFirstResult(modifiers.getOffsetAsInteger());
            }
        }

        System.out.println(criteriaBuilder.getQueryString());

        if (forCount) {
            return criteriaBuilder.getCountQuery();
        }

        return criteriaBuilder.getQuery();
    }

    // Covariant overrides

    @Override
    public BlazeJPAQuery<T> from(EntityPath<?> arg) {
        return (BlazeJPAQuery<T>) super.from(arg);
    }

    @Override
    public BlazeJPAQuery<T> from(EntityPath<?>... args) {
        return (BlazeJPAQuery<T>) super.from(args);
    }

    @Override
    public <P> BlazeJPAQuery<T> from(CollectionExpression<?, P> target, Path<P> alias) {
        return (BlazeJPAQuery<T>) super.from(target, alias);
    }

    @Override
    public <U> BlazeJPAQuery<U> select(Expression<U> expr) {
        return (BlazeJPAQuery<U>) super.select(expr);
    }

    @Override
    public BlazeJPAQuery<Tuple> select(Expression<?>... exprs) {
        return (BlazeJPAQuery<Tuple>) super.select(exprs);
    }

    @Override
    public BlazeJPAQuery<T> where(Predicate o) {
        return (BlazeJPAQuery<T>) super.where(o);
    }

    @Override
    public BlazeJPAQuery<T> where(Predicate... o) {
        return (BlazeJPAQuery<T> ) super.where(o);
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
}
