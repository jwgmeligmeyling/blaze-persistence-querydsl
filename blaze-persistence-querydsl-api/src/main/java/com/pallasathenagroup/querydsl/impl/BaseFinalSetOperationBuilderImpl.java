package com.pallasathenagroup.querydsl.impl;


import com.pallasathenagroup.querydsl.api.BaseFinalSetOperationBuilder;
import com.pallasathenagroup.querydsl.api.BaseOngoingFinalSetOperationBuilder;

public abstract class BaseFinalSetOperationBuilderImpl<T, X extends BaseFinalSetOperationBuilder<T, X>> extends
        AbstractCommonQueryBuilder<X>
        implements BaseFinalSetOperationBuilder<T, X>, BaseOngoingFinalSetOperationBuilder<T, X> {
}
