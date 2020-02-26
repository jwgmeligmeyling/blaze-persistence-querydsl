package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.SetExpressionImpl;
import com.pallasathenagroup.querydsl.api.CriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCriteriaBuilder;
import com.querydsl.core.types.SubQueryExpression;

public class CriteriaBuilderImpl<T> extends AbstractFullQueryBuilder<T, CriteriaBuilderImpl<T>> implements CriteriaBuilder<T, CriteriaBuilderImpl<T>> {

    public CriteriaBuilderImpl(BlazeJPAQuery<T> blazeJPAQuery) {
        super(blazeJPAQuery);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> union() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_UNION, blazeJPAQuery, blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> unionAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_UNION_ALL, blazeJPAQuery, blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersect() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_INTERSECT, blazeJPAQuery, blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersectAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_INTERSECT_ALL, blazeJPAQuery, blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> except() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_EXCEPT, blazeJPAQuery, blazeJPAQuery.createSubQuery());
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> exceptAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_EXCEPT_ALL, blazeJPAQuery, blazeJPAQuery.createSubQuery());
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

    public OngoingFinalSetOperationCriteriaBuilder<LeafOngoingFinalSetOperationCriteriaBuilder<T>> endWith(SubQueryExpression<T> subQueryExpression, JPQLNextOps setOperation) {
        SetExpression<T> setOperation1 = getSetOperation(setOperation, blazeJPAQuery, subQueryExpression);
        return new OngoingFinalSetOperationCriteriaBuilderImpl<LeafOngoingFinalSetOperationCriteriaBuilder<T>, T>(setOperation1) {
            @Override
            public LeafOngoingFinalSetOperationCriteriaBuilder<T> endSet() {
                return new LeafOngoingSetOperationCriteriaBuilderImpl<T>(setOperation, setOperation1, CriteriaBuilderImpl.this.blazeJPAQuery.createSubQuery());
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

    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startSet() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_UNION, (tSubQueryExpression, jpqlNextOps) -> {
            return new OngoingFinalSetOperationCriteriaBuilderImpl<LeafOngoingFinalSetOperationCriteriaBuilder<T>, T>((SetExpression<T>) tSubQueryExpression) {
                @Override
                public LeafOngoingFinalSetOperationCriteriaBuilder<T> endSet() {
                    return new LeafOngoingSetOperationCriteriaBuilderImpl<T>(JPQLNextOps.SET_UNION, tSubQueryExpression, CriteriaBuilderImpl.this.blazeJPAQuery.createSubQuery());
                }
            };
        });
    }
}
