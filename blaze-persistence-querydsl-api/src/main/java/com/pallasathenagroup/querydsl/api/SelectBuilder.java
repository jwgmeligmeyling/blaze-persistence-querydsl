package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.Expression;

public interface SelectBuilder<X extends SelectBuilder<X, T>, T> {
    <U> X select(Expression<T> expression);
}
