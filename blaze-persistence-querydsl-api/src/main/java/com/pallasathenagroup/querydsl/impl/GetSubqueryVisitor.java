package com.pallasathenagroup.querydsl.impl;

import com.querydsl.core.types.Operation;
import com.querydsl.core.types.SubQueryExpression;

public class GetSubqueryVisitor extends DefaultVisitorImpl<SubQueryExpression<?>, Void> {

    public static final GetSubqueryVisitor INSTANCE = new GetSubqueryVisitor();

    @Override
    public SubQueryExpression<?> visit(SubQueryExpression<?> subQueryExpression, Void aVoid) {
        return subQueryExpression;
    }
}
