package com.pallasathenagroup.querydsl.experimental;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;

public class Bind<T> {
    private final Path<T> path;
    private final Expression<T> expression;

    public Bind(Path<T> path, Expression<T> expression) {
        this.path = path;
        this.expression = expression;
    }

    public Path<T> getPath() {
        return path;
    }

    public Expression<T> getExpression() {
        return expression;
    }
}
