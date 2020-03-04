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

import java.util.function.Function;

public class OngoingSetOperationCriteriaBuilderImpl<X, Y, T> extends
        AbstractCriteriaBuilder<T, OngoingSetOperationCriteriaBuilder<X, Y, T>>
        implements OngoingSetOperationCriteriaBuilder<X, Y, T> {

    private final Function<SubQueryExpression<T>, OngoingFinalSetOperationCriteriaBuilder<Y>> finalizer;
    protected final JPQLNextOps operation;
    protected final SubQueryExpression<T> lhs;

    public OngoingSetOperationCriteriaBuilderImpl(BlazeJPAQuery<T> blazeJPAQuery, Function<SubQueryExpression<T>, OngoingFinalSetOperationCriteriaBuilder<Y>> finalizer, JPQLNextOps operation, SubQueryExpression<T> lhs) {
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
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_UNION, getSetOperation());
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> unionAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_UNION_ALL, getSetOperation());
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> intersect() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_INTERSECT, getSetOperation());
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> intersectAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_INTERSECT_ALL, getSetOperation());
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> except() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_EXCEPT, getSetOperation());
    }

    @Override
    public OngoingSetOperationCriteriaBuilder<X, Y, T> exceptAll() {
        return new OngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), this::endWith, JPQLNextOps.SET_EXCEPT_ALL, getSetOperation());
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
                        finalizer, OngoingSetOperationCriteriaBuilderImpl.this.operation, this.blazeJPAQuery);
            }
        };
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
