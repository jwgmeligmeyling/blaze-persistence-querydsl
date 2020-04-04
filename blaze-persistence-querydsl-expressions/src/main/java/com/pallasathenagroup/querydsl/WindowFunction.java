/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pallasathenagroup.querydsl;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * {@code WindowFunction} is a builder for window function expressions.
 * Analog to {@link com.querydsl.sql.WindowFunction}.
 *
 * @param <A> expression type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public class WindowFunction<A> extends WindowDefinition<WindowFunction<A>, A> {

    private final Expression<A> target;

    @Nullable
    private transient volatile SimpleExpression<A> value;

    /**
     * Create a new {@code WindowFunction} that wraps an {@code Expression}.
     *
     * @param expr the expression
     * @since 1.0
     */
    public WindowFunction(Expression<A> expr) {
        super(expr.getType());
        this.target = expr;
    }


    /**
     * Create a new {@code WindowFunction} that wraps an {@code Expression}.
     *
     * @param expr the expression
     * @param baseWindowName the base window name
     * @since 1.0
     */
    public WindowFunction(Expression<A> expr, String baseWindowName) {
        super(expr.getType(), baseWindowName);
        this.target = expr;
    }

    @Override
    public SimpleExpression<A> getValue() {
        if (value == null) {
            value = Expressions.template(target.getType(), "{0} over ({1})", ImmutableList.of(target, super.getValue()));
        }
        return value;
    }

    /**
     * Create an alias for the expression.
     *
     * @param alias The alias
     * @return alias expression
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public SimpleExpression<A> as(Expression<A> alias) {
        return Expressions.operation(getType(), Ops.ALIAS, this, alias);
    }

    /**
     * Create an alias for the expression.
     *
     * @param alias The alias
     * @return alias expression
     * @since 1.0
     */
    public SimpleExpression<A> as(String alias) {
        return Expressions.operation(getType(), Ops.ALIAS, this, ExpressionUtils.path(getType(), alias));
    }

    /**
     * Create a {@code this == right} expression
     *
     * @param expr rhs of the comparison
     * @return this == right
     * @since 1.0
     */
    public BooleanExpression eq(Expression<A> expr) {
        return getValue().eq(expr);
    }

    /**
     * Create a {@code this == right} expression
     *
     * @param arg rhs of the comparison
     * @return this == right
     * @since 1.0
     */
    public BooleanExpression eq(A arg) {
        return getValue().eq(arg);
    }

    /**
     * Create a {@code this <> right} expression
     *
     * @param expr rhs of the comparison
     * @return this != right
     * @since 1.0
     */
    public BooleanExpression ne(Expression<A> expr) {
        return getValue().ne(expr);
    }

    /**
     * Create a {@code this <> right} expression
     *
     * @param arg rhs of the comparison
     * @return this != right
     * @since 1.0
     */
    public BooleanExpression ne(A arg) {
        return getValue().ne(arg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WindowFunction<?> that = (WindowFunction<?>) o;
        return Objects.equals(target, that.target) && super.equals(o);
    }

}
