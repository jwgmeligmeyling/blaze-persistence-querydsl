package com.pallasathenagroup.querydsl.impl;

import com.blazebit.persistence.LimitBuilder;
import com.pallasathenagroup.querydsl.api.BaseOngoingFinalSetOperationBuilder;
import com.pallasathenagroup.querydsl.api.OrderByBuilder;

public abstract class BaseFinalSetOperationBuilderImpl<X, Y extends LimitBuilder<Y> & OrderByBuilder<Y>, T> extends
        AbstractCommonQueryBuilder<T>
        implements BaseOngoingFinalSetOperationBuilder<X, Y> {
}