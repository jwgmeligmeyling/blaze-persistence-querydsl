package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.BlazeJPAQuery;
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
        return null;
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> unionAll() {
        return null;
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersect() {
        return null;
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersectAll() {
        return null;
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> except() {
        return null;
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> exceptAll() {
        return null;
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
