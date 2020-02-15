package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.api.FinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCriteriaBuilder;
import com.querydsl.core.types.SubQueryExpression;

public class LeafOngoingSetOperationCriteriaBuilderImpl<T, X extends LeafOngoingSetOperationCriteriaBuilderImpl<T, X>>
        extends AbstractCriteriaBuilder<T, LeafOngoingSetOperationCriteriaBuilder<T>>
        implements LeafOngoingSetOperationCriteriaBuilder<T>
        , LeafOngoingFinalSetOperationCriteriaBuilder<T>
{

    protected final JPQLNextOps operation;
    protected final SubQueryExpression<T> lhs;

    public LeafOngoingSetOperationCriteriaBuilderImpl(JPQLNextOps operation, SubQueryExpression<T> lhs, BlazeJPAQuery<T> blazeJPAQuery) {
        super(blazeJPAQuery);
        this.operation = operation;
        this.lhs = lhs;
    }

    @Override
    public FinalSetOperationCriteriaBuilder<T> endSet() {
        return new FinalSetOperationCriteriaBuilderImpl<>(getSetOperation());
    }

    public SetExpression<T> getSetOperation() {
        BlazeJPAQuery<Object> subQuery = blazeJPAQuery.createSubQuery();
        switch (operation) {
            case SET_UNION:
                return subQuery.union(lhs, blazeJPAQuery);
            case SET_UNION_ALL:
                return subQuery.unionAll(lhs, blazeJPAQuery);
            case SET_INTERSECT:
                return subQuery.intersect(lhs, blazeJPAQuery);
            case SET_INTERSECT_ALL:
                return subQuery.intersectAll(lhs, blazeJPAQuery);
            case SET_EXCEPT:
                return subQuery.except(lhs, blazeJPAQuery);
            case SET_EXCEPT_ALL:
                return subQuery.exceptAll(lhs, blazeJPAQuery);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> union() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_UNION, getSetOperation(), blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> unionAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_UNION_ALL, getSetOperation(), blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersect() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_INTERSECT, getSetOperation(), blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersectAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_INTERSECT_ALL, getSetOperation(), blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> except() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_EXCEPT, getSetOperation(), blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> exceptAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_EXCEPT_ALL, getSetOperation(), blazeJPAQuery.createSubQuery());
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
