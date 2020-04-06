package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.api.FullSelectCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingFinalSetOperationCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingSetOperationCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.SelectBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCTECriteriaBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

public class FullSelectCTECriteriaBuilderImpl<X, T> extends AbstractCTECriteriaBuilder<T, FullSelectCTECriteriaBuilder<X, T>> implements FullSelectCTECriteriaBuilder<X, T> {

    private final Function<SubQueryExpression<T>, X> finalizer;

    public FullSelectCTECriteriaBuilderImpl(BlazeJPAQuery<T> blazeJPAQuery, Function<SubQueryExpression<T>, X> finalizer) {
        super(blazeJPAQuery);
        this.finalizer = finalizer;
    }

    @Override
    public X end() {
        return finalizer.apply(blazeJPAQuery);
    }

    @Override
    public <U> SelectBuilder<FullSelectCTECriteriaBuilder<X, T>, U> bind(Path<U> path) {
        return new SelectBuilder<FullSelectCTECriteriaBuilder<X, T>, U>() {
            @Override
            public FullSelectCTECriteriaBuilder<X, T> select(Expression<? extends U> expression) {
                blazeJPAQuery.bind(path, expression);
                return FullSelectCTECriteriaBuilderImpl.this;
            }
        };
    }

    @Override
    public LeafOngoingSetOperationCTECriteriaBuilder<X> union() {
        return new LeafOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_UNION, blazeJPAQuery, false, finalizer);

    }

    @Override
    public LeafOngoingSetOperationCTECriteriaBuilder<X> unionAll() {
        return new LeafOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_UNION_ALL, blazeJPAQuery, false, finalizer);
    }

    @Override
    public LeafOngoingSetOperationCTECriteriaBuilder<X> intersect() {
        return new LeafOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_INTERSECT, blazeJPAQuery, false, finalizer);
    }

    @Override
    public LeafOngoingSetOperationCTECriteriaBuilder<X> intersectAll() {
        return new LeafOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_INTERSECT_ALL, blazeJPAQuery, false, finalizer);
    }

    @Override
    public LeafOngoingSetOperationCTECriteriaBuilder<X> except() {
        return new LeafOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_EXCEPT, blazeJPAQuery, false, finalizer);
    }

    @Override
    public LeafOngoingSetOperationCTECriteriaBuilder<X> exceptAll() {
        return new LeafOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_EXCEPT_ALL, blazeJPAQuery, false, finalizer);
    }


    public SetExpression<T> getSetOperation(JPQLNextOps operation, SubQueryExpression<T>... args) {
        BlazeJPAQuery<Object> subQuery = blazeJPAQuery.createSubQuery();
        return subQuery.setOperation(operation, false, Collections.unmodifiableList(Arrays.asList(args)));
    }

    public OngoingFinalSetOperationCTECriteriaBuilder<LeafOngoingFinalSetOperationCTECriteriaBuilder<X>> endWith(SubQueryExpression<T> subQueryExpression, JPQLNextOps setOperation) {
        SetExpression<T> setOperation1 = getSetOperation(setOperation, blazeJPAQuery, subQueryExpression);
        return new OngoingFinalSetOperationCTECriteriaBuilderImpl<LeafOngoingFinalSetOperationCTECriteriaBuilder<X>, T>(setOperation1) {
            @Override
            public LeafOngoingFinalSetOperationCTECriteriaBuilder<X> endSet() {
                return new LeafOngoingSetOperationCTECriteriaBuilderImpl<T, X>(
                        blazeJPAQuery.createSubQuery(),
                        setOperation,
                        setOperation1,
                        false,
                        finalizer::apply);
            }
        };
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>> startUnion() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>>(
                blazeJPAQuery.createSubQuery(),
                JPQLNextOps.SET_UNION,
                this::endWith,
                false
        );
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>> startUnionAll() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>>(
                blazeJPAQuery.createSubQuery(),
                JPQLNextOps.SET_UNION_ALL,
                this::endWith,
                false
        );
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>> startIntersect() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>>(
                blazeJPAQuery.createSubQuery(),
                JPQLNextOps.SET_INTERSECT,
                this::endWith,
                false
        );
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>> startIntersectAll() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>>(
                blazeJPAQuery.createSubQuery(),
                JPQLNextOps.SET_INTERSECT_ALL,
                this::endWith,
                false
        );
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>> startExcept() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>>(
                blazeJPAQuery.createSubQuery(),
                JPQLNextOps.SET_EXCEPT,
                this::endWith,
                false
        );
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>> startExceptAll() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>>(
                blazeJPAQuery.createSubQuery(),
                JPQLNextOps.SET_EXCEPT_ALL,
                this::endWith,
                false
        );
    }

}
