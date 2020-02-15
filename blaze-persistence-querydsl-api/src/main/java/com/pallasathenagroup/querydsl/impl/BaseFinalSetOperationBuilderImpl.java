package com.pallasathenagroup.querydsl.impl;


import com.pallasathenagroup.querydsl.api.BaseFinalSetOperationBuilder;
import com.pallasathenagroup.querydsl.api.BaseOngoingFinalSetOperationBuilder;
import com.pallasathenagroup.querydsl.api.CommonQueryBuilder;

public abstract class BaseFinalSetOperationBuilderImpl<T, X extends BaseFinalSetOperationBuilder<T, X> & CommonQueryBuilder<X>> extends
        AbstractCommonQueryBuilder<X>
        implements BaseFinalSetOperationBuilder<T, X>, BaseOngoingFinalSetOperationBuilder<T, X> {
}
