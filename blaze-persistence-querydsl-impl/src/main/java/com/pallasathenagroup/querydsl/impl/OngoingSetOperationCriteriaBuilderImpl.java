package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.AbstractBlazeJPAQuery;
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
import java.util.function.Function;

public class OngoingSetOperationCriteriaBuilderImpl<X, Y, T> extends
        AbstractCriteriaBuilder<T, OngoingSetOperationCriteriaBuilder<X, Y, T>>
        implements OngoingSetOperationCriteriaBuilder<X, Y, T> {

    private final Function<SubQueryExpression<T>, OngoingFinalSetOperationCriteriaBuilder<Y>> finalizer;
    protected final JPQLNextOps operation;
    protected final SubQueryExpression<T> lhs;
    protected final boolean leftNested;

    public OngoingSetOperationCriteriaBuilderImpl(BlazeJPAQuery<T> blazeJPAQuery, Function<SubQueryExpression<T>, OngoingFinalSetOperationCriteriaBuilder<Y>> finalizer, JPQLNextOps operation, SubQueryExpression<T> lhs, boolean leftNested) {
        super(blazeJPAQuery);
        this.operation = operation;
        this.finalizer = finalizer;
        this.lhs = lhs;
        this.leftNested = leftNested;
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
        return subQuery.setOperation(operation, Collections.unmodifiableList(Arrays.asList(args)));
    }


    @Override
    public OngoingFinalSetOperationCriteriaBuilder<Y> endSetWith() {
        if (blazeJPAQuery.getMetadata().getJoins().isEmpty()) {
            return new OngoingFinalSetOperationCriteriaBuilderImpl<Y, T>(null) {
                @Override
                public Y endSet() {
                    return finalizer.apply(lhs).endSet();
                }
            };
        }
        SetExpression<T> setOperation = getSetOperation();
        return new OngoingFinalSetOperationCriteriaBuilderImpl<Y, T>(setOperation) {
            @Override
            public Y endSet() {
                return finalizer.apply(setOperation).endSet();
            }
        };
    }

    @Override
    public Y endSet() {
        return endSetWith().endSet();
    }

    private OngoingFinalSetOperationCriteriaBuilder<Y> endWith(SubQueryExpression<T> subQueryExpression) {
        return finalizer.apply(subQueryExpression);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> union() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, leftNested ?  JPQLNextOps.LEFT_NESTED_SET_UNION : JPQLNextOps.SET_UNION, getSetOperation(), false);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> unionAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, leftNested ?  JPQLNextOps.LEFT_NESTED_SET_UNION_ALL : JPQLNextOps.SET_UNION_ALL, getSetOperation(), false);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> intersect() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, leftNested ?  JPQLNextOps.LEFT_NESTED_SET_INTERSECT : JPQLNextOps.SET_INTERSECT, getSetOperation(), false);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> intersectAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, leftNested ?  JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL : JPQLNextOps.SET_INTERSECT_ALL, getSetOperation(), false);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> except() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, leftNested ?  JPQLNextOps.LEFT_NESTED_SET_EXCEPT : JPQLNextOps.SET_EXCEPT, getSetOperation(), false);
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> exceptAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, leftNested ?  JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL : JPQLNextOps.SET_EXCEPT_ALL, getSetOperation(), false);
    }

    public OngoingFinalSetOperationCriteriaBuilder<MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>> endWith(SubQueryExpression<T> subQueryExpression, JPQLNextOps setOperation) {
        boolean builderResultNotEmpty = blazeJPAQuery.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();
        boolean subBuilderResultNotEmpty = subQueryExpression.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();

        SetExpression<T> middleOngoingSetResult;

        if (builderResultNotEmpty) {
            middleOngoingSetResult = getSetOperation(this.operation, this.lhs, blazeJPAQuery);
            if (subBuilderResultNotEmpty) {
                middleOngoingSetResult = getSetOperation(setOperation, middleOngoingSetResult, subQueryExpression);
            }
        } else if (subBuilderResultNotEmpty) {
            middleOngoingSetResult = getSetOperation(setOperation, this.lhs, subQueryExpression);
        } else {
            return new OngoingFinalSetOperationCriteriaBuilderImpl<MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T>(null) {
                @Override
                public MiddleOngoingSetOperationCriteriaBuilder<X, Y, T> endSet() {
                    return OngoingSetOperationCriteriaBuilderImpl.this;
                }
            };
        }


        return new OngoingFinalSetOperationCriteriaBuilderImpl<MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T>(middleOngoingSetResult) {
            @Override
            public MiddleOngoingSetOperationCriteriaBuilder<X, Y, T> endSet() {
                return  new OngoingSetOperationCriteriaBuilderImpl<>(OngoingSetOperationCriteriaBuilderImpl.this.blazeJPAQuery.createSubQuery(),
                        finalizer, OngoingSetOperationCriteriaBuilderImpl.this.operation, this.blazeJPAQuery, leftNested);
            }
        };
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startUnion() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ?  JPQLNextOps.LEFT_NESTED_SET_UNION : JPQLNextOps.SET_UNION, this::endWith, false);
    }


    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startUnionAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ?  JPQLNextOps.LEFT_NESTED_SET_UNION_ALL : JPQLNextOps.SET_UNION_ALL, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startIntersect() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ?  JPQLNextOps.LEFT_NESTED_SET_INTERSECT : JPQLNextOps.SET_INTERSECT, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startIntersectAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ?  JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL : JPQLNextOps.SET_INTERSECT_ALL, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startExcept() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ?  JPQLNextOps.LEFT_NESTED_SET_EXCEPT : JPQLNextOps.SET_EXCEPT, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T> startExceptAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ?  JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL : JPQLNextOps.SET_EXCEPT_ALL, this::endWith, false);
    }
}
