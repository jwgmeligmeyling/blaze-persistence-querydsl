package com.pallasathenagroup.querydsl.api;

import com.blazebit.persistence.Queryable;
import com.querydsl.core.types.SubQueryExpression;

public interface FinalSetOperationCriteriaBuilder<T> extends
        CommonQueryBuilder<FinalSetOperationCriteriaBuilder<T>>, Queryable<T, FinalSetOperationCriteriaBuilder<T>>, BaseFinalSetOperationBuilder<T, FinalSetOperationCriteriaBuilder<T>>, SubQueryExpression<T> {
}
