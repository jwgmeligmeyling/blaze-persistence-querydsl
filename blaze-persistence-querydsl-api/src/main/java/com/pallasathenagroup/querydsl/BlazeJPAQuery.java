package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.JPQLTemplates;

import javax.persistence.EntityManager;

@SuppressWarnings("unused")
public class BlazeJPAQuery<T> extends AbstractBlazeJPAQuery<T, BlazeJPAQuery<T>> implements ExtendedJPAQuery<T, BlazeJPAQuery<T>>, ExtendedFetchable<T> {

    public static final JoinFlag LATERAL = new JoinFlag("LATERAL", JoinFlag.Position.BEFORE_TARGET);

    public BlazeJPAQuery() {
        this(null);
    }

    public BlazeJPAQuery(CriteriaBuilderFactory criteriaBuilderFactory) {
        super(criteriaBuilderFactory);
    }

    public BlazeJPAQuery(EntityManager em, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, criteriaBuilderFactory);
    }

    public BlazeJPAQuery(EntityManager em, QueryMetadata metadata, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, metadata, criteriaBuilderFactory);
    }

    public BlazeJPAQuery(EntityManager em, JPQLTemplates templates, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, templates, criteriaBuilderFactory);
    }

    public BlazeJPAQuery(EntityManager em, JPQLTemplates templates, QueryMetadata metadata, CriteriaBuilderFactory criteriaBuilderFactory) {
        super(em, templates, metadata, criteriaBuilderFactory);
    }

    @Override
    public BlazeJPAQuery<T> clone(EntityManager entityManager, JPQLTemplates templates) {
        BlazeJPAQuery<T> q = new BlazeJPAQuery<T>(entityManager, templates, getMetadata().clone(), criteriaBuilderFactory);
        q.clone(this);
        return q;
    }

    @Override
    public BlazeJPAQuery<T> clone(EntityManager entityManager) {
        return clone(entityManager, getTemplates());
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

    @Override
    public BlazeJPAQuery<T> union() {
        BlazeJPAQuery<T> query = new BlazeJPAQuery<>(entityManager, getTemplates(), getMetadata(), criteriaBuilderFactory);
        query.cachable = this.cachable;
        query.union = SetUtils.union(this, query);
        return query;
    }

}
