package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;

public final class WindowExpressions {

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return all(expr)
     */
    public static WindowOver<Boolean> all(Expression<Boolean> expr) {
        return new WindowOver<Boolean>(expr.getType(), Ops.AggOps.BOOLEAN_ALL, expr);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return any(expr)
     */
    public static WindowOver<Boolean> any(Expression<Boolean> expr) {
        return new WindowOver<Boolean>(expr.getType(), Ops.AggOps.BOOLEAN_ANY, expr);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return sum(expr)
     */
    public static <T extends Number> WindowOver<T> sum(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), Ops.AggOps.SUM_AGG, expr);
    }

    /**
     * Start a window function expression
     *
     * @return count()
     */
    public static WindowOver<Long> count() {
        return new WindowOver<Long>(Long.class, Ops.AggOps.COUNT_ALL_AGG);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return count(expr)
     */
    public static WindowOver<Long> count(Expression<?> expr) {
        return new WindowOver<Long>(Long.class, Ops.AggOps.COUNT_AGG, expr);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return count(distinct expr)
     */
    public static WindowOver<Long> countDistinct(Expression<?> expr) {
        return new WindowOver<Long>(Long.class, Ops.AggOps.COUNT_DISTINCT_AGG, expr);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return avg(expr)
     */
    public static <T extends Number> WindowOver<T> avg(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), Ops.AggOps.AVG_AGG, expr);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return min(expr)
     */
    public static <T extends Comparable> WindowOver<T> min(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), Ops.AggOps.MIN_AGG, expr);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return max(expr)
     */
    public static <T extends Comparable> WindowOver<T> max(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), Ops.AggOps.MAX_AGG, expr);
    }

    /**
     * expr evaluated at the row that is one row after the current row within the partition;
     *
     * @param expr expression
     * @return lead(expr)
     */
    public static <T> WindowOver<T> lead(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), JPQLNextOps.LEAD, expr);
    }

    /**
     * expr evaluated at the row that is one row before the current row within the partition
     *
     * @param expr expression
     * @return lag(expr)
     */
    public static <T> WindowOver<T> lag(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), JPQLNextOps.LAG, expr);
    }

    /**
     * NTH_VALUE returns the expr value of the nth row in the window defined by the analytic clause.
     * The returned value has the data type of the expr.
     *
     * @param expr measure expression
     * @param n one based row index
     * @return nth_value(expr, n)
     */
    public static <T> WindowOver<T> nthValue(Expression<T> expr, Number n) {
        return nthValue(expr, ConstantImpl.create(n));
    }

    /**
     * NTH_VALUE returns the expr value of the nth row in the window defined by the analytic clause.
     * The returned value has the data type of the expr
     *
     * @param expr measure expression
     * @param n one based row index
     * @return nth_value(expr, n)
     */
    public static <T> WindowOver<T> nthValue(Expression<T> expr, Expression<? extends Number> n) {
        return new WindowOver<T>(expr.getType(), JPQLNextOps.NTH_VALUE, expr, n);
    }

    /**
     * divides an ordered data set into a number of buckets indicated by expr and assigns the
     * appropriate bucket number to each row
     *
     * @param num bucket size
     * @return ntile(num)
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number & Comparable> WindowOver<T> ntile(T num) {
        return new WindowOver<T>((Class<T>) num.getClass(), JPQLNextOps.NTILE, ConstantImpl.create(num));
    }

    /**
     * rank of the current row with gaps; same as row_number of its first peer
     *
     * @return rank()
     */
    public static WindowOver<Long> rank() {
        return new WindowOver<Long>(Long.class, JPQLNextOps.RANK);
    }

    /**
     * rank of the current row without gaps; this function counts peer groups
     *
     * @return dense_rank()
     */
    public static WindowOver<Long> denseRank() {
        return new WindowOver<Long>(Long.class, JPQLNextOps.DENSE_RANK);
    }

    /**
     * As an analytic function, for a row r, PERCENT_RANK calculates the rank of r minus 1, divided by
     * 1 less than the number of rows being evaluated (the entire query result set or a partition).
     *
     * @return percent_rank()
     */
    public static WindowOver<Double> percentRank() {
        return new WindowOver<Double>(Double.class, JPQLNextOps.PERCENT_RANK);
    }


    /**
     * CUME_DIST calculates the cumulative distribution of a value in a group of values.
     *
     * @return cume_dist()
     */
    public static WindowOver<Double> cumeDist() {
        return new WindowOver<Double>(Double.class, JPQLNextOps.CUME_DIST);
    }


    /**
     * number of the current row within its partition, counting from 1
     *
     * @return row_number()
     */
    public static WindowOver<Long> rowNumber() {
        return new WindowOver<Long>(Long.class, JPQLNextOps.ROW_NUMBER);
    }

    /**
     * returns value evaluated at the row that is the first row of the window frame
     *
     * @param expr argument
     * @return first_value(expr)
     */
    public static <T> WindowOver<T> firstValue(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), JPQLNextOps.FIRST_VALUE, expr);
    }

    /**
     * returns value evaluated at the row that is the last row of the window frame
     *
     * @param expr argument
     * @return last_value(expr)
     */
    public static <T> WindowOver<T> lastValue(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), JPQLNextOps.LAST_VALUE, expr);
    }


}
