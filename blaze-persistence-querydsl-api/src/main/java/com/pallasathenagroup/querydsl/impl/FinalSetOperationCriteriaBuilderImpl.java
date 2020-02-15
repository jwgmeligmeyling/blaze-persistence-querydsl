package com.pallasathenagroup.querydsl.impl;


import com.pallasathenagroup.querydsl.api.FinalSetOperationCriteriaBuilder;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;

import javax.persistence.TypedQuery;
import java.util.List;

public class FinalSetOperationCriteriaBuilderImpl<T> extends BaseFinalSetOperationCriteriaBuilderImpl<T, FinalSetOperationCriteriaBuilder<T>> implements FinalSetOperationCriteriaBuilder<T>  {

    public FinalSetOperationCriteriaBuilderImpl() {
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public TypedQuery<T> getQuery() {
        return null;
    }

    @Override
    public List<T> getResultList() {
        return null;
    }

    @Override
    public T getSingleResult() {
        return null;
    }

    @Override
    public T endSet() {
        return null;
    }

    @Override
    public FinalSetOperationCriteriaBuilderImpl<T> limit(long l) {
        return null;
    }

    @Override
    public FinalSetOperationCriteriaBuilderImpl<T> offset(long l) {
        return null;
    }

    @Override
    public FinalSetOperationCriteriaBuilderImpl<T> restrict(QueryModifiers queryModifiers) {
        return null;
    }

    @Override
    public FinalSetOperationCriteriaBuilderImpl<T> orderBy(OrderSpecifier<?>... orderSpecifiers) {
        return null;
    }

    @Override
    public <U> FinalSetOperationCriteriaBuilderImpl<T> set(ParamExpression<U> paramExpression, U u) {
        return null;
    }
}
