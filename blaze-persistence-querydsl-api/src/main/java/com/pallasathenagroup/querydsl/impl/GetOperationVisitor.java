package com.pallasathenagroup.querydsl.impl;

import com.querydsl.core.types.Operation;

public class GetOperationVisitor extends DefaultVisitorImpl<Operation<?>, Void> {

    public static final GetOperationVisitor INSTANCE = new GetOperationVisitor();

    @Override
    public Operation<?> visit(Operation<?> operation, Void aVoid) {
        return operation;
    }

}
