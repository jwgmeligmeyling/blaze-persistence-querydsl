package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.AbstractBlazeJPAQuery;
import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.NotEmptySetVisitor;
import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.SetExpressionImpl;
import com.pallasathenagroup.querydsl.api.FinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCriteriaBuilder;
import com.querydsl.core.types.SubQueryExpression;

import java.util.Arrays;
import java.util.Collections;

public class LeafOngoingSetOperationCriteriaBuilderImpl<T>
        extends AbstractCriteriaBuilder<T, LeafOngoingSetOperationCriteriaBuilder<T>>
        implements LeafOngoingSetOperationCriteriaBuilder<T>, LeafOngoingFinalSetOperationCriteriaBuilder<T>
{

    protected final JPQLNextOps operation;
    protected final SubQueryExpression<T> lhs;
    protected final boolean leftNested;

    public LeafOngoingSetOperationCriteriaBuilderImpl(JPQLNextOps operation, SubQueryExpression<T> lhs, BlazeJPAQuery<T> blazeJPAQuery, boolean leftNested) {
        super(blazeJPAQuery);
        this.operation = operation;
        this.lhs = lhs;
        this.leftNested = leftNested;
    }

    @Override
    public FinalSetOperationCriteriaBuilder<T> endSet() {
        if (blazeJPAQuery.getMetadata().getJoins().isEmpty()) {
            SetExpression<T> setExpression;
            if (lhs instanceof SetExpression) {
                setExpression = (SetExpression<T>) lhs;
            } else {
                setExpression = new SetExpressionImpl((AbstractBlazeJPAQuery) lhs);
            }
            return new FinalSetOperationCriteriaBuilderImpl<>(setExpression);
        }
        return new FinalSetOperationCriteriaBuilderImpl<>(getSetOperation());
    }

    public SetExpression<T> getSetOperation() {
        boolean builderResultNotEmpty = blazeJPAQuery.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();
        if (builderResultNotEmpty) {
            BlazeJPAQuery<Object> subQuery = blazeJPAQuery.createSubQuery();
            return subQuery.setOperation(operation, Collections.unmodifiableList(Arrays.asList(lhs, blazeJPAQuery)));
        }
        if (lhs instanceof SetExpression) {
            return (SetExpression<T>) lhs;
        }
        else {
            return new SetExpressionImpl((AbstractBlazeJPAQuery) lhs);
        }

    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> union() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION : JPQLNextOps.SET_UNION, getSetOperation(), blazeJPAQuery.createSubQuery(), false);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> unionAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION_ALL : JPQLNextOps.SET_UNION_ALL, getSetOperation(), blazeJPAQuery.createSubQuery(), false);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersect() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT : JPQLNextOps.SET_INTERSECT, getSetOperation(), blazeJPAQuery.createSubQuery(), false);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersectAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL : JPQLNextOps.SET_INTERSECT_ALL, getSetOperation(), blazeJPAQuery.createSubQuery(), false);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> except() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT : JPQLNextOps.SET_EXCEPT, getSetOperation(), blazeJPAQuery.createSubQuery(), false);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> exceptAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL : JPQLNextOps.SET_EXCEPT_ALL, getSetOperation(), blazeJPAQuery.createSubQuery(), false);
    }

    public OngoingFinalSetOperationCriteriaBuilder<LeafOngoingFinalSetOperationCriteriaBuilder<T>> endWith(SubQueryExpression<T> subQueryExpression, JPQLNextOps setOperation) {
        boolean subBuilderResultNotEmpty = subQueryExpression.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();

        if (! subBuilderResultNotEmpty) {
            return new OngoingFinalSetOperationCriteriaBuilderImpl<LeafOngoingFinalSetOperationCriteriaBuilder<T>, T>(null) {
                @Override
                public LeafOngoingFinalSetOperationCriteriaBuilder<T> endSet() {
                    return LeafOngoingSetOperationCriteriaBuilderImpl.this;
                }
            };
        }

        SubQueryExpression<T> lhs = this.lhs;

        boolean builderResultNotEmpty = blazeJPAQuery.accept(NotEmptySetVisitor.INSTANCE, null).booleanValue();
        if (builderResultNotEmpty) {
            lhs = getSetOperation();
        }

        BlazeJPAQuery<Object> subQuery = blazeJPAQuery.createSubQuery();
        SetExpression<T> union = subQuery.setOperation(setOperation, Collections.unmodifiableList(Arrays.asList(lhs, subQueryExpression)));

        return new OngoingFinalSetOperationCriteriaBuilderImpl<LeafOngoingFinalSetOperationCriteriaBuilder<T>, T>(union) {
            @Override
            public LeafOngoingFinalSetOperationCriteriaBuilder<T> endSet() {
                return  new LeafOngoingSetOperationCriteriaBuilderImpl<T>(setOperation, union, LeafOngoingSetOperationCriteriaBuilderImpl.this.blazeJPAQuery.createSubQuery(), leftNested);
            }
        };
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startUnion() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION : JPQLNextOps.SET_UNION, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startUnionAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_UNION_ALL : JPQLNextOps.SET_UNION_ALL, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startIntersect() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT : JPQLNextOps.SET_INTERSECT, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startIntersectAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL : JPQLNextOps.SET_INTERSECT_ALL, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startExcept() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT : JPQLNextOps.SET_EXCEPT, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startExceptAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), leftNested ? JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL : JPQLNextOps.SET_EXCEPT_ALL, this::endWith, false);
    }

}
