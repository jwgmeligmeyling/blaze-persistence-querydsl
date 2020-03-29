package com.pallasathenagroup.querydsl.impl;


import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.SetExpressionImpl;
import com.pallasathenagroup.querydsl.api.LeafOngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.MiddleOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCriteriaBuilder;
import com.querydsl.core.types.SubQueryExpression;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiFunction;

public class StartOngoingSetOperationCriteriaBuilderImpl<X, Y, T> extends
        AbstractCriteriaBuilder<T, StartOngoingSetOperationCriteriaBuilder<X, Y, T>>
        implements StartOngoingSetOperationCriteriaBuilder<X, Y, T>  {


    protected final JPQLNextOps operation;
    private final BiFunction<SubQueryExpression<T>, JPQLNextOps, OngoingFinalSetOperationCriteriaBuilder<Y>> finalizer;
    private final boolean leftNested;

    public StartOngoingSetOperationCriteriaBuilderImpl(
            BlazeJPAQuery<T> blazeJPAQuery, JPQLNextOps operation,
            BiFunction<SubQueryExpression<T>, JPQLNextOps, OngoingFinalSetOperationCriteriaBuilder<Y>> finalizer,
            boolean leftNested
    ) {
        super(blazeJPAQuery);
        this.operation = operation;
        this.finalizer = finalizer;
        this.leftNested = leftNested;
    }

    @Override
    public OngoingFinalSetOperationCriteriaBuilder<Y> endSetWith() {
        return finalizer.apply(blazeJPAQuery, operation);
    }

    @Override
    public Y endSet() {
        return endSetWith().endSet();
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startSet() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), null, this::endWith, true);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> union() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION : JPQLNextOps.SET_UNION, blazeJPAQuery, false);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> unionAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION_ALL : JPQLNextOps.SET_UNION_ALL, blazeJPAQuery, false);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> intersect() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT : JPQLNextOps.SET_INTERSECT, blazeJPAQuery, false);
    }

    public OngoingFinalSetOperationCriteriaBuilder<Y> endWith(SubQueryExpression<T> subQueryExpression) {
        return finalizer.apply(subQueryExpression, operation);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> intersectAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL : JPQLNextOps.SET_INTERSECT_ALL, blazeJPAQuery, false);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> except() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT : JPQLNextOps.SET_EXCEPT, blazeJPAQuery, false);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> exceptAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL : JPQLNextOps.SET_EXCEPT_ALL, blazeJPAQuery, false);
    }


    public SetExpression<T> getSetOperation(JPQLNextOps operation, SubQueryExpression<T>... args) {
        BlazeJPAQuery<Object> subQuery = blazeJPAQuery.createSubQuery();
        return subQuery.setOperation(operation, Collections.unmodifiableList(Arrays.asList(args)));
    }

    public OngoingFinalSetOperationCriteriaBuilder<MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>> endWith(SubQueryExpression<T> subQueryExpression, JPQLNextOps setOperation) {
        boolean subBuilderResultNotEmpty = subQueryExpression.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();
        boolean builderResultNotEmpty = blazeJPAQuery.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();
        SetExpression<T> middleOngoingSetResult;

        if (subBuilderResultNotEmpty) {
            if (builderResultNotEmpty) {
                middleOngoingSetResult = getSetOperation(setOperation, blazeJPAQuery, subQueryExpression);
            } else {
                if (subQueryExpression instanceof SetExpression) {
                    middleOngoingSetResult = (SetExpression) subQueryExpression;
                } else if (subQueryExpression instanceof BlazeJPAQuery) {
                    middleOngoingSetResult = new SetExpressionImpl<>((BlazeJPAQuery) subQueryExpression);
                } else {
                    throw new IllegalStateException();
                }
            }
        } else if (builderResultNotEmpty) {
            middleOngoingSetResult = new SetExpressionImpl<>(blazeJPAQuery);
        } else {
            throw new IllegalStateException();
        }

        return new OngoingFinalSetOperationCriteriaBuilderImpl<MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T>(middleOngoingSetResult) {
            @Override
            public MiddleOngoingSetOperationCriteriaBuilder<X, Y, T> endSet() {
                return  new OngoingSetOperationCriteriaBuilderImpl<>(StartOngoingSetOperationCriteriaBuilderImpl.this.blazeJPAQuery.createSubQuery(),
                        s -> finalizer.apply(s, StartOngoingSetOperationCriteriaBuilderImpl.this.operation), StartOngoingSetOperationCriteriaBuilderImpl.this.operation, this.blazeJPAQuery, leftNested);
            }
        };

    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startUnion() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION : JPQLNextOps.SET_UNION, this::endWith, false);
    }


    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startUnionAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION_ALL : JPQLNextOps.SET_UNION_ALL, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startIntersect() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT : JPQLNextOps.SET_INTERSECT, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startIntersectAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL : JPQLNextOps.SET_INTERSECT_ALL, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startExcept() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT : JPQLNextOps.SET_EXCEPT, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startExceptAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL : JPQLNextOps.SET_EXCEPT_ALL, this::endWith, false);
    }

}
