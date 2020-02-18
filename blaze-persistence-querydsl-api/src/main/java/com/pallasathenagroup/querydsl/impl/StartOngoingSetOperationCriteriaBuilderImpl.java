package com.pallasathenagroup.querydsl.impl;


import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.api.MiddleOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCriteriaBuilder;
import com.querydsl.core.types.SubQueryExpression;

import java.util.function.BiFunction;

public class StartOngoingSetOperationCriteriaBuilderImpl<X, Y, T> extends
        AbstractCriteriaBuilder<T, StartOngoingSetOperationCriteriaBuilder<X, Y, T>>
        implements StartOngoingSetOperationCriteriaBuilder<X, Y, T>  {


    protected final JPQLNextOps operation;
    private final BiFunction<SubQueryExpression<T>, JPQLNextOps, Y> finalizer;

    public StartOngoingSetOperationCriteriaBuilderImpl(
            BlazeJPAQuery<T> blazeJPAQuery, JPQLNextOps operation,
            BiFunction<SubQueryExpression<T>, JPQLNextOps, Y> finalizer
    ) {
        super(blazeJPAQuery);
        this.operation = operation;
        this.finalizer = finalizer;
    }

    @Override
    public OngoingFinalSetOperationCriteriaBuilder<Y> endSetWith() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Y endSet() {
        return finalizer.apply(blazeJPAQuery, operation);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startSet() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this.operation, this::endWith);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> union() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_UNION, blazeJPAQuery);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> unionAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_UNION_ALL, blazeJPAQuery);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> intersect() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_INTERSECT, blazeJPAQuery);
    }

    public Y endWith(SubQueryExpression<T> subQueryExpression) {
        return finalizer.apply(subQueryExpression, operation);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> intersectAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_INTERSECT_ALL, blazeJPAQuery);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> except() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_EXCEPT, blazeJPAQuery);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> exceptAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_EXCEPT_ALL, blazeJPAQuery);
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

    public MiddleOngoingSetOperationCriteriaBuilder<X, Y, T> endWith(SubQueryExpression<T> subQueryExpression, JPQLNextOps setOperation) {
        boolean subBuilderResultNotEmpty = subQueryExpression.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();
        boolean builderResultNotEmpty = blazeJPAQuery.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();
        SubQueryExpression<T> middleOngoingSetResult;

        if (subBuilderResultNotEmpty) {
            middleOngoingSetResult = subQueryExpression;
            if (builderResultNotEmpty) {
                middleOngoingSetResult = getSetOperation(setOperation, blazeJPAQuery, middleOngoingSetResult);
            }
        } else if (builderResultNotEmpty) {
            middleOngoingSetResult = blazeJPAQuery;
        } else {
            return this;
        }

        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(),
                s -> finalizer.apply(s, this.operation), this.operation, middleOngoingSetResult);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startUnion() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_UNION, this::endWith);
    }


    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startUnionAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_UNION_ALL, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startIntersect() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_INTERSECT, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startIntersectAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_INTERSECT_ALL, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startExcept() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_EXCEPT, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startExceptAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_EXCEPT_ALL, this::endWith);
    }

}
