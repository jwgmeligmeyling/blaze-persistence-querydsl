package com.pallasathenagroup.querydsl.api;

import com.blazebit.persistence.Queryable;

public interface FinalSetOperationCriteriaBuilder<T> extends
        CommonQueryBuilder<FinalSetOperationCriteriaBuilder<T>>, Queryable<T, FinalSetOperationCriteriaBuilder<T>>, BaseFinalSetOperationBuilder<T, FinalSetOperationCriteriaBuilder<T>> {
}
