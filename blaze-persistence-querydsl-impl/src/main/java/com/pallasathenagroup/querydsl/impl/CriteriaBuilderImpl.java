package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.JPQLNextOps;
import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.api.CriteriaBuilder;
import com.pallasathenagroup.querydsl.api.FullSelectCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCriteriaBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;

import java.util.Arrays;
import java.util.Collections;

public class CriteriaBuilderImpl<T> extends AbstractFullQueryBuilder<T, CriteriaBuilder<T>> implements CriteriaBuilder<T> {

    public CriteriaBuilderImpl(BlazeJPAQuery<T> blazeJPAQuery) {
        super(blazeJPAQuery);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> union() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_UNION, blazeJPAQuery, blazeJPAQuery.createSubQuery(), false);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> unionAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_UNION_ALL, blazeJPAQuery, blazeJPAQuery.createSubQuery(), false);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersect() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_INTERSECT, blazeJPAQuery, blazeJPAQuery.createSubQuery(), false);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> intersectAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_INTERSECT_ALL, blazeJPAQuery, blazeJPAQuery.createSubQuery(), false);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> except() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_EXCEPT, blazeJPAQuery, blazeJPAQuery.createSubQuery(), false);
    }

    @Override
    public LeafOngoingSetOperationCriteriaBuilder<T> exceptAll() {
        return new LeafOngoingSetOperationCriteriaBuilderImpl<>(JPQLNextOps.SET_EXCEPT_ALL, blazeJPAQuery, blazeJPAQuery.createSubQuery(), false);
    }

    public SetExpression<T> getSetOperation(JPQLNextOps operation, SubQueryExpression<T>... args) {
        BlazeJPAQuery<Object> subQuery = blazeJPAQuery.createSubQuery();
        return subQuery.setOperation(operation, false, Collections.unmodifiableList(Arrays.asList(args)));
    }

    public OngoingFinalSetOperationCriteriaBuilder<LeafOngoingFinalSetOperationCriteriaBuilder<T>> endWith(SubQueryExpression<T> subQueryExpression, JPQLNextOps setOperation) {
        SetExpression<T> setOperation1 = getSetOperation(setOperation, blazeJPAQuery, subQueryExpression);
        return new OngoingFinalSetOperationCriteriaBuilderImpl<LeafOngoingFinalSetOperationCriteriaBuilder<T>, T>(setOperation1) {
            @Override
            public LeafOngoingFinalSetOperationCriteriaBuilder<T> endSet() {
                return new LeafOngoingSetOperationCriteriaBuilderImpl<T>(setOperation, setOperation1, CriteriaBuilderImpl.this.blazeJPAQuery.createSubQuery(), false);
            }
        };
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startUnion() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_UNION, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startUnionAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_UNION_ALL, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startIntersect() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_INTERSECT, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startIntersectAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_INTERSECT_ALL, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startExcept() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_EXCEPT, this::endWith, false);
    }

    @Override
    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startExceptAll() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), JPQLNextOps.SET_EXCEPT_ALL, this::endWith, false);
    }

    public StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startSet() {
        return new StartOngoingSetOperationCriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), null, (tSubQueryExpression, jpqlNextOps) -> {
            return new OngoingFinalSetOperationCriteriaBuilderImpl<LeafOngoingFinalSetOperationCriteriaBuilder<T>, T>((SetExpression<T>) tSubQueryExpression) {
                @Override
                public LeafOngoingFinalSetOperationCriteriaBuilder<T> endSet() {
                    return new LeafOngoingSetOperationCriteriaBuilderImpl<T>(null, tSubQueryExpression, CriteriaBuilderImpl.this.blazeJPAQuery.createSubQuery(), true);
                }
            };
        }, true);
    }

    @Override
    public <U> FullSelectCTECriteriaBuilder<CriteriaBuilder<T>, U> with(EntityPath<U> entityPath) {
        return new FullSelectCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), subQueryExpression -> {
            blazeJPAQuery.with((Path) entityPath, subQueryExpression);
            return CriteriaBuilderImpl.this;
        });
    }

    @Override
    public <U> FullSelectCTECriteriaBuilder<CriteriaBuilder<T>, U> withRecursive(EntityPath<U> entityPath) {
        return new FullSelectCTECriteriaBuilderImpl<>(blazeJPAQuery.createSubQuery(), subQueryExpression -> {
            blazeJPAQuery.withRecursive((Path) entityPath, subQueryExpression);
            return CriteriaBuilderImpl.this;
        });
    }

}
