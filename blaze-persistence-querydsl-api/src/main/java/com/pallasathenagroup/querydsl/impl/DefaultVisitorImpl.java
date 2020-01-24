package com.pallasathenagroup.querydsl.impl;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;

public class DefaultVisitorImpl<T,V> implements Visitor<T,V> {
    @Override
    public T visit(Constant<?> constant, V v) {
        return null;
    }

    @Override
    public T visit(FactoryExpression<?> factoryExpression, V v) {
        return null;
    }

    @Override
    public T visit(Operation<?> operation, V v) {
        return null;
    }

    @Override
    public T visit(ParamExpression<?> paramExpression, V v) {
        return null;
    }

    @Override
    public T visit(Path<?> path, V v) {
        return null;
    }

    @Override
    public T visit(SubQueryExpression<?> subQueryExpression, V v) {
        return null;
    }

    @Override
    public T visit(TemplateExpression<?> templateExpression, V v) {
        return null;
    }
}
