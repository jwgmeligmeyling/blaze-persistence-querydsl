package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.api.CriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCriteriaBuilder;

public class CriteriaBuilderImpl<T> extends AbstractFullQueryBuilder<T, CriteriaBuilderImpl<T>> implements CriteriaBuilder<T, CriteriaBuilderImpl<T>> {

    public CriteriaBuilderImpl(BlazeJPAQuery<T> blazeJPAQuery) {
        super(blazeJPAQuery);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> union() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_UNION, blazeJPAQuery, blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> unionAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_UNION_ALL, blazeJPAQuery, blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersect() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_INTERSECT, blazeJPAQuery, blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersectAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_INTERSECT_ALL, blazeJPAQuery, blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> except() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_EXCEPT, blazeJPAQuery, blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> exceptAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_EXCEPT_ALL, blazeJPAQuery, blazeJPAQuery.createSubQuery());
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>> startUnion() {
        return null;
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>> startUnionAll() {
        return null;
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>> startIntersect() {
        return null;
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>> startIntersectAll() {
        return null;
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>> startExcept() {
        return null;
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>> startExceptAll() {
        return null;
    }
}
