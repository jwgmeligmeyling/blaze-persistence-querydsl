package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.AbstractBlazeJPAQuery;
import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.SetExpressionImpl;
import com.pallasathenagroup.querydsl.api.FinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.MiddleOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCriteriaBuilder;
import com.querydsl.core.types.SubQueryExpression;

import java.util.function.BiFunction;

public class LeafOngoingSetOperationCriteriaBuilderImpl<T>
        extends AbstractCriteriaBuilder<T, LeafOngoingSetOperationCriteriaBuilder<T>>
        implements LeafOngoingSetOperationCriteriaBuilder<T>, LeafOngoingFinalSetOperationCriteriaBuilder<T>
{

    protected final JPQLNextOps operation;
    protected final SubQueryExpression<T> lhs;

    public LeafOngoingSetOperationCriteriaBuilderImpl(JPQLNextOps operation, SubQueryExpression<T> lhs, BlazeJPAQuery<T> blazeJPAQuery) {
        super(blazeJPAQuery);
        this.operation = operation;
        this.lhs = lhs;
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
            switch (operation) {
                case SET_UNION:
                    return subQuery.union(lhs, blazeJPAQuery);
                case SET_UNION_ALL:
                    return subQuery.unionAll(lhs, blazeJPAQuery);
                case SET_INTERSECT:
                    return subQuery.intersect(lhs, blazeJPAQuery);
                case SET_INTERSECT_ALL:
                    return subQuery.intersectAll(lhs, blazeJPAQuery);
                case SET_EXCEPT:
                    return subQuery.except(lhs, blazeJPAQuery);
                case SET_EXCEPT_ALL:
                    return subQuery.exceptAll(lhs, blazeJPAQuery);
                default:
                    throw new UnsupportedOperationException();
            }
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
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_UNION, getSetOperation(), blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> unionAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_UNION_ALL, getSetOperation(), blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersect() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_INTERSECT, getSetOperation(), blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersectAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_INTERSECT_ALL, getSetOperation(), blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> except() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_EXCEPT, getSetOperation(), blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> exceptAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_EXCEPT_ALL, getSetOperation(), blazeJPAQuery.createSubQuery());
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
        SetExpression<T> union;
        switch (setOperation) {
            case SET_UNION:
                union = subQuery.union(lhs, subQueryExpression);
                break;
            case SET_UNION_ALL:
                union = subQuery.unionAll(lhs, subQueryExpression);
                break;
            case SET_INTERSECT:
                union = subQuery.intersect(lhs, subQueryExpression);
                break;
            case SET_INTERSECT_ALL:
                union = subQuery.intersectAll(lhs, subQueryExpression);
                break;
            case SET_EXCEPT:
                union = subQuery.except(lhs, subQueryExpression);
                break;
            case SET_EXCEPT_ALL:
                union = subQuery.exceptAll(lhs, subQueryExpression);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return new OngoingFinalSetOperationCriteriaBuilderImpl<LeafOngoingFinalSetOperationCriteriaBuilder<T>, T>(union) {
            @Override
            public LeafOngoingFinalSetOperationCriteriaBuilder<T> endSet() {
                return  new LeafOngoingSetOperationCriteriaBuilderImpl<T>(setOperation, union, LeafOngoingSetOperationCriteriaBuilderImpl.this.blazeJPAQuery.createSubQuery());
            }
        };
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startUnion() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_UNION, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startUnionAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_UNION_ALL, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startIntersect() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_INTERSECT, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startIntersectAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_INTERSECT_ALL, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startExcept() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_EXCEPT, this::endWith);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startExceptAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_EXCEPT_ALL, this::endWith);
    }
}
