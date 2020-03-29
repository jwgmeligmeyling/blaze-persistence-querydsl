package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.NotEmptySetVisitor;
import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.SetExpressionImpl;
import com.pallasathenagroup.querydsl.api.MiddleOngoingSetOperationCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingSetOperationCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.SelectBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCTECriteriaBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiFunction;

public class StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, Y> extends AbstractCTECriteriaBuilder<T, StartOngoingSetOperationCTECriteriaBuilder<X, Y>> implements StartOngoingSetOperationCTECriteriaBuilder<X, Y> {

    protected final JPQLNextOps operation;
    private final BiFunction<SubQueryExpression<T>, JPQLNextOps, OngoingFinalSetOperationCTECriteriaBuilder<Y>> finalizer;
    private final boolean leftNested;

    public StartOngoingSetOperationCTECriteriaBuilderImpl(BlazeJPAQuery<T> blazeJPAQuery, JPQLNextOps operation,
                                                          BiFunction<SubQueryExpression<T>, JPQLNextOps, OngoingFinalSetOperationCTECriteriaBuilder<Y>> finalizer,
                                                          boolean leftNested) {
        super(blazeJPAQuery);
        this.operation = operation;
        this.finalizer = finalizer;
        this.leftNested = leftNested;
    }

    @Override
    public OngoingFinalSetOperationCTECriteriaBuilder<Y> endSetWith() {
        return finalizer.apply(blazeJPAQuery, operation);
    }

    @Override
    public Y endSet() {
        return endSetWith().endSet();
    }

    @Override
    public <U> SelectBuilder<StartOngoingSetOperationCTECriteriaBuilder<X, Y>, U> bind(Path<U> path) {
        return new SelectBuilder<StartOngoingSetOperationCTECriteriaBuilder<X, Y>, U>() {
            @Override
            public StartOngoingSetOperationCTECriteriaBuilder<X, Y> select(Expression<? extends U> expression) {
                blazeJPAQuery.bind(path, expression);
                return StartOngoingSetOperationCTECriteriaBuilderImpl.this;
            }
        };
    }

    public SetExpression<T> getSetOperation(JPQLNextOps operation, SubQueryExpression<T>... args) {
        BlazeJPAQuery<Object> subQuery = blazeJPAQuery.createSubQuery();
        return subQuery.setOperation(operation, Collections.unmodifiableList(Arrays.asList(args)));
    }


    public OngoingFinalSetOperationCTECriteriaBuilder<Y> endWithA(SubQueryExpression<T> subQueryExpression) {
        return finalizer.apply(subQueryExpression, operation);
    }

    public OngoingFinalSetOperationCTECriteriaBuilder<MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> endWith(SubQueryExpression<T> subQueryExpression, JPQLNextOps setOperation) {
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

        return new OngoingFinalSetOperationCTECriteriaBuilderImpl<MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>, T>(middleOngoingSetResult) {

            @Override
            public MiddleOngoingSetOperationCTECriteriaBuilder<X, Y> endSet() {
                return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X ,Y>(
                    StartOngoingSetOperationCTECriteriaBuilderImpl.this.blazeJPAQuery.createSubQuery(),
                        StartOngoingSetOperationCTECriteriaBuilderImpl.this.operation,
                        finalizer::apply,
                        leftNested
                );
            }

        };
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startSet() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), null, this::endWith, true);
    }

    @Override
    public OngoingSetOperationCTECriteriaBuilder<X, Y> union() {
        return new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                blazeJPAQuery.createSubQuery(),
                this::endWithA,
                leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION : JPQLNextOps.SET_UNION,
                blazeJPAQuery,
                false
        );
    }

    @Override
    public OngoingSetOperationCTECriteriaBuilder<X, Y> unionAll() {
        return new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                blazeJPAQuery.createSubQuery(),
                this::endWithA,
                leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION_ALL : JPQLNextOps.SET_UNION_ALL,
                blazeJPAQuery,
                false
        );
    }

    @Override
    public OngoingSetOperationCTECriteriaBuilder<X, Y> intersect() {
        return new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                blazeJPAQuery.createSubQuery(),
                this::endWithA,
                leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT : JPQLNextOps.SET_INTERSECT,
                blazeJPAQuery,
                false
        );
    }

    @Override
    public OngoingSetOperationCTECriteriaBuilder<X, Y> intersectAll() {
        return new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                blazeJPAQuery.createSubQuery(),
                this::endWithA,
                leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL : JPQLNextOps.SET_INTERSECT_ALL,
                blazeJPAQuery,
                false
        );
    }

    @Override
    public OngoingSetOperationCTECriteriaBuilder<X, Y> except() {
        return new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                blazeJPAQuery.createSubQuery(),
                this::endWithA,
                leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT : JPQLNextOps.SET_EXCEPT,
                blazeJPAQuery,
                false
        );
    }

    @Override
    public OngoingSetOperationCTECriteriaBuilder<X, Y> exceptAll() {
        return new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                blazeJPAQuery.createSubQuery(),
                this::endWithA,
                leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL: JPQLNextOps.SET_EXCEPT_ALL,
                blazeJPAQuery,
                false
        );
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startUnion() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION : JPQLNextOps.SET_UNION, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startUnionAll() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION_ALL : JPQLNextOps.SET_UNION_ALL, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startIntersect() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT : JPQLNextOps.SET_INTERSECT, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startIntersectAll() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL : JPQLNextOps.SET_INTERSECT_ALL, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startExcept() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT : JPQLNextOps.SET_EXCEPT, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startExceptAll() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL : JPQLNextOps.SET_EXCEPT_ALL, this::endWith, false);
    }
}
