package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.ParamExpression;

public interface ParameterHolder<Q extends ParameterHolder<Q>> {
    <U> Q set(ParamExpression<U> paramExpression, U u);
}
