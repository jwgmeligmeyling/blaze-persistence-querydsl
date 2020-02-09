package com.pallasathenagroup.querydsl.impl;

import com.querydsl.core.types.Operation;
import com.querydsl.core.types.SubQueryExpression;

public class NotEmptySetVisitor extends DefaultVisitorImpl<Boolean, Void> {

    public static final NotEmptySetVisitor INSTANCE = new NotEmptySetVisitor();

    @Override
    public Boolean visit(Operation<?> operation, Void aVoid) {
        return operation.getArg(0).accept(this, aVoid);
    }

    @Override
    public Boolean visit(SubQueryExpression<?> subQueryExpression, Void aVoid) {
        return !subQueryExpression.getMetadata().getJoins().isEmpty();
    }
}
