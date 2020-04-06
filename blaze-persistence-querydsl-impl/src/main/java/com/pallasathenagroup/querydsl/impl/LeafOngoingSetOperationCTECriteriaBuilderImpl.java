package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.AbstractBlazeJPAQuery;
import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.NotEmptySetVisitor;
import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.SetExpressionImpl;
import com.pallasathenagroup.querydsl.api.FinalSetOperationCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingFinalSetOperationCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingSetOperationCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.SelectBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCTECriteriaBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

public class LeafOngoingSetOperationCTECriteriaBuilderImpl<T, Q> extends AbstractCTECriteriaBuilder<T, LeafOngoingSetOperationCTECriteriaBuilder<Q>> implements LeafOngoingSetOperationCTECriteriaBuilder<Q>, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q> {

    protected final JPQLNextOps operation;
    protected final SubQueryExpression<T> lhs;
    protected final boolean leftNested;
    private final Function<SubQueryExpression<T>, Q> finalizer;


    public LeafOngoingSetOperationCTECriteriaBuilderImpl(
            BlazeJPAQuery<T> blazeJPAQuery, JPQLNextOps operation, SubQueryExpression<T> lhs, boolean leftNested, Function<SubQueryExpression<T>, Q> finalizer) {
        super(blazeJPAQuery);
        this.operation = operation;
        this.lhs = lhs;
        this.leftNested = leftNested;
        this.finalizer = finalizer;
    }

    public SetExpression<T> getSetOperation() {
        boolean builderResultNotEmpty = blazeJPAQuery.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();
        if (builderResultNotEmpty) {
            BlazeJPAQuery<Object> subQuery = blazeJPAQuery.createSubQuery();
            return subQuery.setOperation(operation, false, Collections.unmodifiableList(Arrays.asList(lhs, blazeJPAQuery)));
        }
        if (lhs instanceof SetExpression) {
            return (SetExpression<T>) lhs;
        }
        else {
            return new SetExpressionImpl((AbstractBlazeJPAQuery) lhs);
        }
    }

    @Override
    public FinalSetOperationCTECriteriaBuilder<Q> endSet() {
        SetExpression<T> setOperation = getSetOperation();
         return new FinalSetOperationCTECriteriaBuilderImpl<>(setOperation, finalizer);
    }

    @Override
    public <U> SelectBuilder<LeafOngoingSetOperationCTECriteriaBuilder<Q>, U> bind(Path<U> path) {
        return new SelectBuilder<LeafOngoingSetOperationCTECriteriaBuilder<Q>, U>() {
            @Override
            public LeafOngoingSetOperationCTECriteriaBuilder<Q> select(Expression<? extends U> expression) {
                blazeJPAQuery.bind(path, expression);
                return LeafOngoingSetOperationCTECriteriaBuilderImpl.this;
            }
        };
    }

    @Override
    public LeafOngoingSetOperationCTECriteriaBuilder<Q> union() {
        return new LeafOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION : JPQLNextOps.SET_UNION, blazeJPAQuery, false, finalizer);
    }

    @Override
    public LeafOngoingSetOperationCTECriteriaBuilder<Q> unionAll() {
        return new LeafOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION_ALL : JPQLNextOps.SET_UNION_ALL, blazeJPAQuery, false, finalizer);
    }

    @Override
    public LeafOngoingSetOperationCTECriteriaBuilder<Q> intersect() {
        return new LeafOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT : JPQLNextOps.SET_INTERSECT, blazeJPAQuery, false, finalizer);
    }

    @Override
    public LeafOngoingSetOperationCTECriteriaBuilder<Q> intersectAll() {
        return new LeafOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL : JPQLNextOps.SET_INTERSECT_ALL, blazeJPAQuery, false, finalizer);
    }

    @Override
    public LeafOngoingSetOperationCTECriteriaBuilder<Q> except() {
        return new LeafOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT : JPQLNextOps.SET_EXCEPT, blazeJPAQuery, false, finalizer);
    }

    @Override
    public LeafOngoingSetOperationCTECriteriaBuilder<Q> exceptAll() {
        return new LeafOngoingSetOperationCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL : JPQLNextOps.SET_EXCEPT_ALL, blazeJPAQuery, false, finalizer);
    }

    public OngoingFinalSetOperationCTECriteriaBuilder<LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>> endWith(SubQueryExpression<T> subQueryExpression, JPQLNextOps setOperation) {
        boolean subBuilderResultNotEmpty = subQueryExpression.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();

        if (! subBuilderResultNotEmpty) {
            return new OngoingFinalSetOperationCTECriteriaBuilderImpl<LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>, T>(null) {

                @Override
                public LeafOngoingFinalSetOperationCTECriteriaBuilder<Q> endSet() {
                    return LeafOngoingSetOperationCTECriteriaBuilderImpl.this;
                }

            };
        }

        SubQueryExpression<T> lhs = this.lhs;

        boolean builderResultNotEmpty = blazeJPAQuery.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();
        if (builderResultNotEmpty) {
            lhs = getSetOperation();
        }

        BlazeJPAQuery<T> subQuery = blazeJPAQuery.createSubQuery();
        SetExpression<T> union = subQuery.setOperation(setOperation, false, Collections.unmodifiableList(Arrays.asList(lhs, subQueryExpression)));

        return new OngoingFinalSetOperationCTECriteriaBuilderImpl<LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>, T>(union) {

            @Override
            public LeafOngoingFinalSetOperationCTECriteriaBuilder<Q> endSet() {
                return new LeafOngoingSetOperationCTECriteriaBuilderImpl<T, Q>(
                        LeafOngoingSetOperationCTECriteriaBuilderImpl.this.blazeJPAQuery.createSubQuery(),
                        LeafOngoingSetOperationCTECriteriaBuilderImpl.this.operation,
                        subQuery,
                        LeafOngoingSetOperationCTECriteriaBuilderImpl.this.leftNested,
                        finalizer::apply
                );
            }

        };
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<Q, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>> startUnion() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, Q, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>>(
                blazeJPAQuery.createSubQuery(),
                leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION : JPQLNextOps.SET_UNION,
                this::endWith,
                false);
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<Q, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>> startUnionAll() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, Q, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>>(
                blazeJPAQuery.createSubQuery(),
                leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION_ALL : JPQLNextOps.SET_UNION_ALL,
                this::endWith,
                false);
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<Q, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>> startIntersect() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, Q, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>>(
                blazeJPAQuery.createSubQuery(),
                leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT : JPQLNextOps.SET_INTERSECT,
                this::endWith,
                false);
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<Q, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>> startIntersectAll() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, Q, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>>(
                blazeJPAQuery.createSubQuery(),
                leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL : JPQLNextOps.SET_INTERSECT_ALL,
                this::endWith,
                false);
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<Q, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>> startExcept() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, Q, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>>(
                blazeJPAQuery.createSubQuery(),
                leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT : JPQLNextOps.SET_EXCEPT,
                this::endWith,
                false);
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<Q, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>> startExceptAll() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, Q, LeafOngoingFinalSetOperationCTECriteriaBuilder<Q>>(
                blazeJPAQuery.createSubQuery(),
                leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL : JPQLNextOps.SET_EXCEPT_ALL,
                this::endWith,
                false);
    }

}
