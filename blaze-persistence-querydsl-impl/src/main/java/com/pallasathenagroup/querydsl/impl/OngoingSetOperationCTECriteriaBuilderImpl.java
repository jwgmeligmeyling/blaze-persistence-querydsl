package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.AbstractBlazeJPAQuery;
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
import java.util.function.Function;

public class OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T> extends AbstractCTECriteriaBuilder<T, OngoingSetOperationCTECriteriaBuilder<X, Y>> implements OngoingSetOperationCTECriteriaBuilder<X, Y> {

    private final Function<SubQueryExpression<T>, OngoingFinalSetOperationCTECriteriaBuilder<Y>> finalizer;
    protected final JPQLNextOps operation;
    protected final SubQueryExpression<T> lhs;
    protected final boolean leftNested;

    public OngoingSetOperationCTECriteriaBuilderImpl(BlazeJPAQuery<T> blazeJPAQuery, Function<SubQueryExpression<T>, OngoingFinalSetOperationCTECriteriaBuilder<Y>> finalizer, JPQLNextOps operation, SubQueryExpression<T> lhs, boolean leftNested) {
        super(blazeJPAQuery);
        this.finalizer = finalizer;
        this.operation = operation;
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
        return subQuery.setOperation(operation, false, Collections.unmodifiableList(Arrays.asList(args)));
    }

    @Override
    public OngoingFinalSetOperationCTECriteriaBuilder<Y> endSetWith() {
        if (blazeJPAQuery.getMetadata().getJoins().isEmpty()) {
            return new OngoingFinalSetOperationCTECriteriaBuilderImpl<Y, T>(null) {
                @Override
                public Y endSet() {
                    return finalizer.apply(lhs).endSet();
                }
            };
        }
        return new OngoingFinalSetOperationCTECriteriaBuilderImpl<Y, T>(getSetOperation()) {
            @Override
            public Y endSet() {
                return finalizer.apply(setExpression).endSet();
            }
        };
    }

    @Override
    public Y endSet() {
        return endSetWith().endSet();
    }

    @Override
    public <U> SelectBuilder<OngoingSetOperationCTECriteriaBuilder<X, Y>, U> bind(Path<U> path) {
        return new SelectBuilder<OngoingSetOperationCTECriteriaBuilder<X, Y>, U>() {
            @Override
            public OngoingSetOperationCTECriteriaBuilder<X, Y> select(Expression<? extends U> expression) {
                blazeJPAQuery.bind(path, expression);
                return OngoingSetOperationCTECriteriaBuilderImpl.this;
            }
        };
    }

    private OngoingFinalSetOperationCTECriteriaBuilder<Y> endWith(SubQueryExpression<T> subQueryExpression) {
        return finalizer.apply(subQueryExpression);
    }

    @Override
    public OngoingSetOperationCTECriteriaBuilder<X, Y> union() {
        return new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                this.blazeJPAQuery.createSubQuery(),
                this::endWith,
                leftNested ?  JPQLNextOps.LEFT_NESTED_SET_UNION : JPQLNextOps.SET_UNION,
                getSetOperation(),
                false
        );
    }

    @Override
    public OngoingSetOperationCTECriteriaBuilder<X, Y> unionAll() {
        return new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                this.blazeJPAQuery.createSubQuery(),
                this::endWith,
                leftNested ?  JPQLNextOps.LEFT_NESTED_SET_UNION_ALL : JPQLNextOps.SET_UNION_ALL,
                getSetOperation(),
                false
        );
    }

    @Override
    public OngoingSetOperationCTECriteriaBuilder<X, Y> intersect() {
        return new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                this.blazeJPAQuery.createSubQuery(),
                this::endWith,
                leftNested ?  JPQLNextOps.LEFT_NESTED_SET_INTERSECT : JPQLNextOps.SET_INTERSECT,
                getSetOperation(),
                false
        );
    }

    @Override
    public OngoingSetOperationCTECriteriaBuilder<X, Y> intersectAll() {
        return new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                this.blazeJPAQuery.createSubQuery(),
                this::endWith,
                leftNested ?  JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL : JPQLNextOps.SET_INTERSECT_ALL,
                getSetOperation(),
                false
        );
    }

    @Override
    public OngoingSetOperationCTECriteriaBuilder<X, Y> except() {
        return new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                this.blazeJPAQuery.createSubQuery(),
                this::endWith,
                leftNested ?  JPQLNextOps.LEFT_NESTED_SET_EXCEPT : JPQLNextOps.SET_EXCEPT,
                getSetOperation(),
                false
        );
    }

    @Override
    public OngoingSetOperationCTECriteriaBuilder<X, Y> exceptAll() {
        return new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                this.blazeJPAQuery.createSubQuery(),
                this::endWith,
                leftNested ?  JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL : JPQLNextOps.SET_EXCEPT_ALL,
                getSetOperation(),
                false
        );
    }

    public OngoingFinalSetOperationCTECriteriaBuilder<MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> endWith(SubQueryExpression<T> subQueryExpression, JPQLNextOps setOperation) {
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
            return new OngoingFinalSetOperationCTECriteriaBuilderImpl<MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>, T>(null) {
                @Override
                public MiddleOngoingSetOperationCTECriteriaBuilder<X, Y> endSet() {
                    return OngoingSetOperationCTECriteriaBuilderImpl.this;
                }
            };
        }

        return new OngoingFinalSetOperationCTECriteriaBuilderImpl<MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>, T>(middleOngoingSetResult) {
            @Override
            public MiddleOngoingSetOperationCTECriteriaBuilder<X, Y> endSet() {
                return  new OngoingSetOperationCTECriteriaBuilderImpl<X, Y, T>(
                        OngoingSetOperationCTECriteriaBuilderImpl.this.blazeJPAQuery.createSubQuery(),
                        finalizer,
                        OngoingSetOperationCTECriteriaBuilderImpl.this.operation,
                        this.setExpression,
                        leftNested);
            }
        };
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startUnion() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>>(
                this.blazeJPAQuery.createSubQuery(),
                leftNested ?  JPQLNextOps.LEFT_NESTED_SET_UNION : JPQLNextOps.SET_UNION,
                this::endWith,
                false
                );
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startUnionAll() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>>(
                this.blazeJPAQuery.createSubQuery(),
                leftNested ?  JPQLNextOps.LEFT_NESTED_SET_UNION_ALL : JPQLNextOps.SET_UNION_ALL,
                this::endWith,
                false
        );
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startIntersect() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>>(
                this.blazeJPAQuery.createSubQuery(),
                leftNested ?  JPQLNextOps.LEFT_NESTED_SET_INTERSECT : JPQLNextOps.SET_INTERSECT,
                this::endWith,
                false
        );
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startIntersectAll() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>>(
                this.blazeJPAQuery.createSubQuery(),
                leftNested ?  JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL : JPQLNextOps.SET_INTERSECT_ALL,
                this::endWith,
                false
        );
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startExcept() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>>(
                this.blazeJPAQuery.createSubQuery(),
                leftNested ?  JPQLNextOps.LEFT_NESTED_SET_EXCEPT : JPQLNextOps.SET_EXCEPT,
                this::endWith,
                false
        );
    }

    @Override
    public StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>> startExceptAll() {
        return new StartOngoingSetOperationCTECriteriaBuilderImpl<T, X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>>(
                this.blazeJPAQuery.createSubQuery(),
                leftNested ?  JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL : JPQLNextOps.SET_EXCEPT_ALL,
                this::endWith,
                false
        );
    }

}
