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

public class StartOngoingSetOperationCriteriaBuilderImpl<T, Z> extends
        AbstractCriteriaBuilder<T, StartOngoingSetOperationCriteriaBuilder<T, Z>>
        implements StartOngoingSetOperationCriteriaBuilder<T, Z>  {


    protected final JPQLNextOps operation;
    private final BiFunction<SubQueryExpression<T>, JPQLNextOps, Z> finalizer;

    public StartOngoingSetOperationCriteriaBuilderImpl(
            BlazeJPAQuery<T> blazeJPAQuery, JPQLNextOps operation,
            BiFunction<SubQueryExpression<T>, JPQLNextOps, Z> finalizer) {
        super(blazeJPAQuery);
        this.operation = operation;
        this.finalizer = finalizer;
    }

    @Override
    public OngoingFinalSetOperationCriteriaBuilder<Z> endSetWith() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Z endSet() {
        return finalizer.apply(blazeJPAQuery, operation);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, MiddleOngoingSetOperationCriteriaBuilder<T, Z>> startSet() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this.operation, this::endWith);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<T, Z> union() {
        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_UNION, blazeJPAQuery);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<T, Z> unionAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_UNION_ALL, blazeJPAQuery);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<T, Z> intersect() {
        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_INTERSECT, blazeJPAQuery);
    }

    public Z endWith(SubQueryExpression<T> subQueryExpression) {
        return finalizer.apply(subQueryExpression, operation);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<T, Z> intersectAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_INTERSECT_ALL, blazeJPAQuery);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<T, Z> except() {
        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_EXCEPT, blazeJPAQuery);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<T, Z> exceptAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_EXCEPT_ALL, blazeJPAQuery);
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

    public MiddleOngoingSetOperationCriteriaBuilder<T, Z> endWith(SubQueryExpression<T> subQueryExpression, JPQLNextOps setOperation) {
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

        return new OngoingSetOperationCriteriaBuilderImpl<T, Z>(blazeJPAQuery.createSubQuery(),
                s -> finalizer.apply(s, this.operation), this.operation, middleOngoingSetResult);
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
