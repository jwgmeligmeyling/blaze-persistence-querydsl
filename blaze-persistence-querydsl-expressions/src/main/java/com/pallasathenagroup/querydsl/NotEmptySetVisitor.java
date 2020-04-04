package com.pallasathenagroup.querydsl;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.SubQueryExpression;

/**
 * Visitor implementation that checks if a query is empty (i.e. has no default joins).
 * Empty queries are removed from set operations, as they cannot be represented in SQL.
 *
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public class NotEmptySetVisitor extends DefaultVisitorImpl<Boolean, Void> {

    public static final NotEmptySetVisitor INSTANCE = new NotEmptySetVisitor();

    @Override
    public Boolean visit(Operation<?> operation, Void aVoid) {
        return operation.getArg(0).accept(this, aVoid);
    }

    @Override
    public Boolean visit(SubQueryExpression<?> subQueryExpression, Void aVoid) {
        return subQueryExpression.getMetadata().getFlags().stream()
                .anyMatch(flag -> flag.getPosition().equals(QueryFlag.Position.START_OVERRIDE) &&
                        flag.getFlag().accept(NotEmptySetVisitor.this, aVoid)) ||
                !subQueryExpression.getMetadata().getJoins().isEmpty();
    }
}
