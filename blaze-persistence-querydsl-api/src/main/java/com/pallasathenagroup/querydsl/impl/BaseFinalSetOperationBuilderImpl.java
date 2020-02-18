package com.pallasathenagroup.querydsl.impl;


import com.pallasathenagroup.querydsl.api.BaseFinalSetOperationBuilder;
import com.pallasathenagroup.querydsl.api.BaseOngoingFinalSetOperationBuilder;

public abstract class BaseFinalSetOperationBuilderImpl<X, Y extends BaseFinalSetOperationBuilder<X, Y>, T> extends
        AbstractCommonQueryBuilder<T>
        implements BaseFinalSetOperationBuilder<X, Y>, BaseOngoingFinalSetOperationBuilder<X, Y> {
}