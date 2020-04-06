package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.SubQueryExpression;

import java.util.Arrays;

/**
 * Utility methods for generating set operations.
 *
 * Analog to {@code com.querydsl.sql.UnionUtils}.
 */
public final class SetUtils {

    /**
     * Create a set operation
     *
     * @param setOperation Set operation to use
     * @param wrapSets Whether or not to wrap set left nested operation expressions.
     *    In most cases, expressions are assumed in CNF with explicit precedence.
     *    In some cases however, you want the operator to over precedence, i.e.
     *    in a chain of operations that are left folded
     * @param expressions Operands for the set operation
     * @param <T> Set operation result type
     * @return The set operation
     */
    @SafeVarargs
    public static <T> Expression<T> setOperation(JPQLNextOps setOperation, boolean wrapSets, Expression<T>... expressions) {
        return Arrays.stream(expressions)
                .filter(expression -> expression.accept(NotEmptySetVisitor.INSTANCE, null))
                .reduce((a, b) -> a.accept(new ExpressionVoidDefaultVisitorImpl<>(setOperation, wrapSets, a, b), null)).get();
    }

    private static JPQLNextOps getLeftNestedSetOperation(JPQLNextOps setOperation) {
        JPQLNextOps leftNestedOperation = null;
        switch (setOperation) {
            case SET_UNION:
                leftNestedOperation = JPQLNextOps.LEFT_NESTED_SET_UNION;
                break;
            case SET_UNION_ALL:
                leftNestedOperation = JPQLNextOps.LEFT_NESTED_SET_UNION_ALL;
                break;
            case SET_INTERSECT:
                leftNestedOperation = JPQLNextOps.LEFT_NESTED_SET_INTERSECT;
                break;
            case SET_INTERSECT_ALL:
                leftNestedOperation = JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL;
                break;
            case SET_EXCEPT:
                leftNestedOperation = JPQLNextOps.LEFT_NESTED_SET_EXCEPT;
                break;
            case SET_EXCEPT_ALL:
                leftNestedOperation = JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL;
                break;
            default:
                leftNestedOperation = setOperation;
        }
        return leftNestedOperation;
    }

    @SafeVarargs
    public static <T> Expression<T> union(Expression<T>... expressions) {
        return setOperation(JPQLNextOps.SET_UNION, true, expressions);
    }

    @SafeVarargs
    public static <T> Expression<T> unionAll(Expression<T>... expressions) {
        return setOperation(JPQLNextOps.SET_UNION_ALL, true, expressions);
    }

    @SafeVarargs
    public static <T> Expression<T> intersect(Expression<T>... expressions) {
        return setOperation(JPQLNextOps.SET_INTERSECT, true, expressions);
    }

    @SafeVarargs
    public static <T> Expression<T> intersectAll(Expression<T>... expressions) {
        return setOperation(JPQLNextOps.SET_INTERSECT_ALL, true, expressions);
    }

    @SafeVarargs
    public static <T> Expression<T> except(Expression<T>... expressions) {
        return setOperation(JPQLNextOps.SET_EXCEPT, true, expressions);
    }

    @SafeVarargs
    public static <T> Expression<T> exceptAll(Expression<T>... expressions) {
        return setOperation(JPQLNextOps.SET_EXCEPT_ALL, true, expressions);
    }

    private static class ExpressionVoidDefaultVisitorImpl<T> extends DefaultVisitorImpl<Expression<T>, Void> {
        private final Expression<T> a;
        private final boolean wrapSets;
        private final JPQLNextOps setOperation, leftNestedOperation;
        private final Expression<T> b;

        public ExpressionVoidDefaultVisitorImpl(JPQLNextOps setOperation, boolean wrapSets, Expression<T> a, Expression<T> b) {
            this.a = a;
            this.wrapSets = wrapSets;
            this.leftNestedOperation = getLeftNestedSetOperation(setOperation);
            this.setOperation = setOperation;
            this.b = b;
        }

        @Override
        public Expression<T> visit(Operation<?> rv, Void rhs) {
            return ExpressionUtils.operation(a.getType(), wrapSets ? leftNestedOperation : setOperation, a, b);
        }

        @Override
        public Expression<T> visit(SubQueryExpression<?> subQueryExpression, Void rhs) {
            SetOperationFlag setOperationFlag = SetOperationFlag.getSetOperationFlag(subQueryExpression.getMetadata());
            boolean nestedSet = setOperationFlag != null;
            return ExpressionUtils.operation(a.getType(), nestedSet && wrapSets ? leftNestedOperation : setOperation, a, b);
        }
    }
}
