package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.AbstractBlazeJPAQuery;
import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.SetExpressionImpl;
import com.pallasathenagroup.querydsl.api.MiddleOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCriteriaBuilder;
import com.querydsl.core.types.SubQueryExpression;

import java.util.function.Function;

public class OngoingSetOperationCriteriaBuilderImpl<T, Z> extends
        AbstractCriteriaBuilder<T, OngoingSetOperationCriteriaBuilder<T, Z>>
        implements OngoingSetOperationCriteriaBuilder<T, Z> {

    private final Function<SubQueryExpression<T>, Z> finalizer;
    protected final JPQLNextOps operation;
    protected final SubQueryExpression<T> lhs;

    public OngoingSetOperationCriteriaBuilderImpl(BlazeJPAQuery<T> blazeJPAQuery, Function<SubQueryExpression<T>, Z> finalizer, JPQLNextOps operation, SubQueryExpression<T> lhs) {
        super(blazeJPAQuery);
        this.operation = operation;
        this.finalizer = finalizer;
        this.lhs = lhs;
    }

    public SetExpression<T> getSetOperation() {
        boolean builderResultNotEmpty = blazeJPAQuery.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();
        if (builderResultNotEmpty) {
            return getSetOperation(operation, lhs, blazeJPAQuery);
        }
        if (lhs instanceof SetExpression) {
            return (SetExpression<T>) lhs;
        }
        else {
            return new SetExpressionImpl((AbstractBlazeJPAQuery) lhs);
        }
    }


    public SetExpression<T> getSetOperation(JPQLNextOps operation, SubQueryExpression<T>... args) {
        BlazeJPAQuery<Object> subQuery = blazeJPAQuery.createSubQuery();
        switch (operation) {
            case SET_UNION:
                return subQuery.union(args);
            case SET_UNION_ALL:
                return subQuery.unionAll(args);
            case SET_INTERSECT:
                return subQuery.intersect(args);
            case SET_INTERSECT_ALL:
                return subQuery.intersectAll(args);
            case SET_EXCEPT:
                return subQuery.except(args);
            case SET_EXCEPT_ALL:
                return subQuery.exceptAll(args);
            default:
                throw new UnsupportedOperationException();
        }
    }


    @Override
    public OngoingFinalSetOperationCriteriaBuilder<Z> endSetWith() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Z endSet() {
        if (blazeJPAQuery.getMetadata().getJoins().isEmpty()) {
            return finalizer.apply(lhs);
        }
        SetExpression<T> setOperation = getSetOperation();
        return finalizer.apply(setOperation);
    }


    public Z endWith(SubQueryExpression<T> subQueryExpression) {
        return finalizer.apply(subQueryExpression);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<T, Z> union() {
        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_UNION, getSetOperation());
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<T, Z> unionAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_UNION_ALL, getSetOperation());
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<T, Z> intersect() {
        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_INTERSECT, getSetOperation());
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<T, Z> intersectAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_INTERSECT_ALL, getSetOperation());
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<T, Z> except() {
        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_EXCEPT, getSetOperation());
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<T, Z> exceptAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_EXCEPT_ALL, getSetOperation());
    }

    public MiddleOngoingSetOperationCriteriaBuilder<T, Z> endWith(SubQueryExpression<T> subQueryExpression, JPQLNextOps setOperation) {
        SubQueryExpression<T> middleOngoingSetResult = this.lhs;
        boolean builderResultNotEmpty = blazeJPAQuery.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();

        if (builderResultNotEmpty) {
            middleOngoingSetResult = getSetOperation(this.operation, middleOngoingSetResult, blazeJPAQuery);
        }

        boolean subBuilderResultNotEmpty = subQueryExpression.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();

        if (subBuilderResultNotEmpty) {
            middleOngoingSetResult = getSetOperation(setOperation, middleOngoingSetResult, subQueryExpression);
        }

        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), finalizer, this.operation, middleOngoingSetResult);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, MiddleOngoingSetOperationCriteriaBuilder<T, Z>> startUnion() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_UNION, this::endWith);
    }


    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, MiddleOngoingSetOperationCriteriaBuilder<T, Z>> startUnionAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_UNION_ALL, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, MiddleOngoingSetOperationCriteriaBuilder<T, Z>> startIntersect() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_INTERSECT, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, MiddleOngoingSetOperationCriteriaBuilder<T, Z>> startIntersectAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_INTERSECT_ALL, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, MiddleOngoingSetOperationCriteriaBuilder<T, Z>> startExcept() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_EXCEPT, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, MiddleOngoingSetOperationCriteriaBuilder<T, Z>> startExceptAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_EXCEPT_ALL, this::endWith);
    }
}
