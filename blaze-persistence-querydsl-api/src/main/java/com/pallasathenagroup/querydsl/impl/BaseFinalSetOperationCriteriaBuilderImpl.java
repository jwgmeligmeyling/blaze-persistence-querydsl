package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.api.BaseFinalSetOperationBuilder;
import com.pallasathenagroup.querydsl.api.BaseOngoingFinalSetOperationBuilder;
import com.pallasathenagroup.querydsl.api.CommonQueryBuilder;

public abstract class BaseFinalSetOperationCriteriaBuilderImpl<T, X extends BaseFinalSetOperationBuilder<T, X> & CommonQueryBuilder<X>>
        extends BaseFinalSetOperationBuilderImpl<T, X>
        implements BaseOngoingFinalSetOperationBuilder<T, X> {
}
