package com.pallasathenagroup.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLOps;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

public class JPQLNextExpressions {

    private static final Map<ChronoUnit, Operator> DATE_ADD_OPS
            = new EnumMap<ChronoUnit, Operator>(ChronoUnit.class);
    private static final Map<ChronoUnit, Operator> DATE_DIFF_OPS
                    = new EnumMap<ChronoUnit, Operator>(ChronoUnit.class);
    private static final Map<ChronoUnit, Operator> DATE_TRUNC_OPS
                            = new EnumMap<ChronoUnit, Operator>(ChronoUnit.class);

    static {
        JPQLNextExpressions.DATE_ADD_OPS.put(ChronoUnit.YEARS, Ops.DateTimeOps.ADD_YEARS);
        JPQLNextExpressions.DATE_ADD_OPS.put(ChronoUnit.MONTHS, Ops.DateTimeOps.ADD_MONTHS);
        JPQLNextExpressions.DATE_ADD_OPS.put(ChronoUnit.WEEKS, Ops.DateTimeOps.ADD_WEEKS);
        JPQLNextExpressions.DATE_ADD_OPS.put(ChronoUnit.DAYS, Ops.DateTimeOps.ADD_DAYS);
        JPQLNextExpressions.DATE_ADD_OPS.put(ChronoUnit.HOURS, Ops.DateTimeOps.ADD_HOURS);
        JPQLNextExpressions.DATE_ADD_OPS.put(ChronoUnit.MINUTES, Ops.DateTimeOps.ADD_MINUTES);
        JPQLNextExpressions.DATE_ADD_OPS.put(ChronoUnit.SECONDS, Ops.DateTimeOps.ADD_SECONDS);
        JPQLNextExpressions.DATE_ADD_OPS.put(ChronoUnit.MILLIS, null); // TODO

        JPQLNextExpressions.DATE_DIFF_OPS.put(ChronoUnit.YEARS, Ops.DateTimeOps.DIFF_YEARS);
        JPQLNextExpressions.DATE_DIFF_OPS.put(ChronoUnit.MONTHS, Ops.DateTimeOps.DIFF_MONTHS);
        JPQLNextExpressions.DATE_DIFF_OPS.put(ChronoUnit.WEEKS, Ops.DateTimeOps.DIFF_WEEKS);
        JPQLNextExpressions.DATE_DIFF_OPS.put(ChronoUnit.DAYS, Ops.DateTimeOps.DIFF_DAYS);
        JPQLNextExpressions.DATE_DIFF_OPS.put(ChronoUnit.HOURS, Ops.DateTimeOps.DIFF_HOURS);
        JPQLNextExpressions.DATE_DIFF_OPS.put(ChronoUnit.MINUTES, Ops.DateTimeOps.DIFF_MINUTES);
        JPQLNextExpressions.DATE_DIFF_OPS.put(ChronoUnit.SECONDS, Ops.DateTimeOps.DIFF_SECONDS);
        JPQLNextExpressions.DATE_DIFF_OPS.put(ChronoUnit.MILLIS, null); // TODO

        JPQLNextExpressions.DATE_TRUNC_OPS.put(ChronoUnit.YEARS, Ops.DateTimeOps.TRUNC_YEAR);
        JPQLNextExpressions.DATE_TRUNC_OPS.put(ChronoUnit.MONTHS, Ops.DateTimeOps.TRUNC_MONTH);
        JPQLNextExpressions.DATE_TRUNC_OPS.put(ChronoUnit.WEEKS, Ops.DateTimeOps.TRUNC_WEEK);
        JPQLNextExpressions.DATE_TRUNC_OPS.put(ChronoUnit.DAYS, Ops.DateTimeOps.TRUNC_DAY);
        JPQLNextExpressions.DATE_TRUNC_OPS.put(ChronoUnit.HOURS, Ops.DateTimeOps.TRUNC_HOUR);
        JPQLNextExpressions.DATE_TRUNC_OPS.put(ChronoUnit.MINUTES, Ops.DateTimeOps.TRUNC_MINUTE);
        JPQLNextExpressions.DATE_TRUNC_OPS.put(ChronoUnit.SECONDS, Ops.DateTimeOps.TRUNC_SECOND);
    }

    /**
     * Create a new detached JPQLQuery instance with the given projection
     *
     * @param expr projection
     * @param <T>
     * @return select(expr)
     * @see JPAExpressions#select(Expression)
     */
    public static <T> BlazeJPAQuery<T> select(Expression<T> expr) {
        return new BlazeJPAQuery<>().select(expr);
    }

    /**
     * Create a new detached JPQLQuery instance with the given projection
     *
     * @param exprs projection
     * @return select(exprs)
     * @see JPAExpressions#select(Expression[])
     */
    public static BlazeJPAQuery<Tuple> select(Expression<?>... exprs) {
        return new BlazeJPAQuery<Void>().select(exprs);
    }


    /**
     * Create a new detached JPQLQuery instance with the given projection
     *
     * @param expr projection
     * @param <T>
     * @return select(distinct expr)
     * @see com.querydsl.jpa.JPAExpressions#selectDistinct(Expression)
     */
    public static <T> BlazeJPAQuery<T> selectDistinct(Expression<T> expr) {
        return new BlazeJPAQuery<Void>().select(expr).distinct();
    }

    /**
     * Create a new detached JPQLQuery instance with the given projection
     *
     * @param exprs projection
     * @return select(distinct expr)
     * @see com.querydsl.jpa.JPAExpressions#selectDistinct(Expression[])
     */
    public static BlazeJPAQuery<Tuple> selectDistinct(Expression<?>... exprs) {
        return new BlazeJPAQuery<Void>().select(exprs).distinct();
    }

    /**
     * Create a new detached JPQLQuery instance with the projection zero
     *
     * @return select(0)
     * @see JPAExpressions#selectZero()
     */
    public static BlazeJPAQuery<Integer> selectZero() {
        return select(Expressions.ZERO);
    }

    /**
     * Create a new detached JPQLQuery instance with the projection one
     *
     * @return select(1)
     * @see JPAExpressions#selectOne()
     */
    public static BlazeJPAQuery<Integer> selectOne() {
        return select(Expressions.ONE);
    }

    /**
     * Create a new detached JPQLQuery instance with the given projection
     *
     * @param expr projection and source
     * @param <T>
     * @return select(expr).from(expr)
     */
    public static <T> BlazeJPAQuery<T> selectFrom(EntityPath<T> expr) {
        return select(expr).from(expr);
    }


    /**
     * Create a avg(col) expression
     *
     * @param col collection
     * @return avg(col)
     * @see JPAExpressions#avg(CollectionExpression)
     */
    public static <A extends Comparable<? super A>> ComparableExpression<A> avg(CollectionExpression<?,A> col) {
        return Expressions.comparableOperation((Class) col.getParameter(0), Ops.QuantOps.AVG_IN_COL, (Expression<?>) col);
    }

    /**
     * Create a max(col) expression
     *
     * @param left collection
     * @return max(col)
     * @see JPAExpressions#max(CollectionExpression)
     */
    public static <A extends Comparable<? super A>> ComparableExpression<A> max(CollectionExpression<?,A> left) {
        return Expressions.comparableOperation((Class) left.getParameter(0), Ops.QuantOps.MAX_IN_COL, (Expression<?>) left);
    }

    /**
     * Create a min(col) expression
     *
     * @param left collection
     * @return min(col)
     * @see JPAExpressions#min(CollectionExpression)
     */
    public static <A extends Comparable<? super A>> ComparableExpression<A> min(CollectionExpression<?,A> left) {
        return Expressions.comparableOperation((Class) left.getParameter(0), Ops.QuantOps.MIN_IN_COL, (Expression<?>) left);
    }

    /**
     * Create a type(path) expression
     *
     * @param path entity
     * @return type(path)
     * @see JPAExpressions#type(EntityPath)
     */
    public static StringExpression type(EntityPath<?> path) {
        return Expressions.stringOperation(JPQLOps.TYPE, path);
    }

    /**
     * Create a dateadd(unit, date, amount) expression
     *
     * @param unit date part
     * @param date date
     * @param amount amount
     * @return converted date
     * @see com.querydsl.sql.SQLExpressions#dateadd(com.querydsl.sql.DatePart, DateExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateTimeExpression<D> dateadd(ChronoUnit unit, DateTimeExpression<D> date, int amount) {
        return Expressions.dateTimeOperation(date.getType(), DATE_ADD_OPS.get(unit), date, ConstantImpl.create(amount));
    }

    /**
     * Create a dateadd(unit, date, amount) expression
     *
     * @param unit date part
     * @param date date
     * @param amount amount
     * @return converted date
     * @see com.querydsl.sql.SQLExpressions#dateadd(com.querydsl.sql.DatePart, DateExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateExpression<D> dateadd(ChronoUnit unit, DateExpression<D> date, int amount) {
        return Expressions.dateOperation(date.getType(), DATE_ADD_OPS.get(unit), date, ConstantImpl.create(amount));
    }

    /**
     * Get a datediff(unit, start, end) expression
     *
     * @param unit date part
     * @param start start
     * @param end end
     * @return difference in units
     * @see com.querydsl.sql.SQLExpressions#datediff(com.querydsl.sql.DatePart, Comparable, DateExpression)
     * @since 1.0
     */
    public static <D extends Comparable<?>> NumberExpression<Integer> datediff(ChronoUnit unit,
                                                                               DateExpression<D> start, DateExpression<D> end) {
        return Expressions.numberOperation(Integer.class, DATE_DIFF_OPS.get(unit), start, end);
    }

    /**
     * Get a datediff(unit, start, end) expression
     *
     * @param unit date part
     * @param start start
     * @param end end
     * @return difference in units
     * @see com.querydsl.sql.SQLExpressions#datediff(com.querydsl.sql.DatePart, Comparable, DateExpression)
     * @since 1.0
     */
    public static <D extends Comparable<?>> NumberExpression<Integer> datediff(ChronoUnit unit,
                                                                            D start, DateExpression<D> end) {
        return Expressions.numberOperation(Integer.class, DATE_DIFF_OPS.get(unit), ConstantImpl.create(start), end);
    }

    /**
     * Get a datediff(unit, start, end) expression
     *
     * @param unit date part
     * @param start start
     * @param end end
     * @return difference in units
     * @see com.querydsl.sql.SQLExpressions#datediff(com.querydsl.sql.DatePart, Comparable, DateExpression)
     * @since 1.0
     */
    public static <D extends Comparable<?>> NumberExpression<Integer> datediff(ChronoUnit unit,
                                                                            DateExpression<D> start, D end) {
        return Expressions.numberOperation(Integer.class, DATE_DIFF_OPS.get(unit), start, ConstantImpl.create(end));
    }

    /**
     * Get a datediff(unit, start, end) expression
     *
     * @param unit date part
     * @param start start
     * @param end end
     * @return difference in units
     * @see com.querydsl.sql.SQLExpressions#datediff(com.querydsl.sql.DatePart, Comparable, DateTimeExpression)
     * @since 1.0
     */
    public static <D extends Comparable<?>> NumberExpression<Integer> datediff(ChronoUnit unit,
                                                                            DateTimeExpression<D> start, DateTimeExpression<D> end) {
        return Expressions.numberOperation(Integer.class, DATE_DIFF_OPS.get(unit), start, end);
    }

    /**
     * Get a datediff(unit, start, end) expression
     *
     * @param unit date part
     * @param start start
     * @param end end
     * @return difference in units
     * @see com.querydsl.sql.SQLExpressions#datediff(com.querydsl.sql.DatePart, Comparable, DateTimeExpression)
     * @since 1.0
     */
    public static <D extends Comparable<?>> NumberExpression<Integer> datediff(ChronoUnit unit,
                                                                            D start, DateTimeExpression<D> end) {
        return Expressions.numberOperation(Integer.class, DATE_DIFF_OPS.get(unit), ConstantImpl.create(start), end);
    }

    /**
     * Get a datediff(unit, start, end) expression
     *
     * @param unit date part
     * @param start start
     * @param end end
     * @return difference in units
     * @see com.querydsl.sql.SQLExpressions#datediff(com.querydsl.sql.DatePart, Comparable, DateTimeExpression)
     * @since 1.0
     */
    public static <D extends Comparable<?>> NumberExpression<Integer> datediff(ChronoUnit unit,
                                                                            DateTimeExpression<D> start, D end) {
        return Expressions.numberOperation(Integer.class, DATE_DIFF_OPS.get(unit), start, ConstantImpl.create(end));
    }

    /**
     * Truncate the given date expression
     *
     * @param unit date part to truncate to
     * @param expr truncated date
     * @see com.querydsl.sql.SQLExpressions#datetrunc(com.querydsl.sql.DatePart, DateExpression)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateExpression<D> datetrunc(ChronoUnit unit, DateExpression<D> expr) {
        return Expressions.dateOperation(expr.getType(), DATE_TRUNC_OPS.get(unit), expr);
    }

    /**
     * Truncate the given datetime expression
     *
     * @param unit com.querydsl.sql.DatePart to truncate to
     * @param expr truncated datetime
     * @see com.querydsl.sql.SQLExpressions#datetrunc(com.querydsl.sql.DatePart, DateTimeExpression)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateTimeExpression<D> datetrunc(ChronoUnit unit, DateTimeExpression<D> expr) {
        return Expressions.dateTimeOperation(expr.getType(), DATE_TRUNC_OPS.get(unit), expr);
    }

    /**
     * Add the given amount of years to the date
     *
     * @param date datetime
     * @param years years to add
     * @return converted datetime
     * @see com.querydsl.sql.SQLExpressions#addYears(DateExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateTimeExpression<D> addYears(DateTimeExpression<D> date, int years) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_YEARS, date, ConstantImpl.create(years));
    }

    /**
     * Add the given amount of months to the date
     *
     * @param date datetime
     * @param months months to add
     * @return converted datetime
     * @see com.querydsl.sql.SQLExpressions#addYears(DateTimeExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateTimeExpression<D> addMonths(DateTimeExpression<D> date, int months) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_MONTHS, date, ConstantImpl.create(months));
    }

    /**
     * Add the given amount of weeks to the date
     *
     * @param date datetime
     * @param weeks weeks to add
     * @return converted date
     * @see com.querydsl.sql.SQLExpressions#addWeeks(DateTimeExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateTimeExpression<D> addWeeks(DateTimeExpression<D> date, int weeks) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_WEEKS, date, ConstantImpl.create(weeks));
    }

    /**
     * Add the given amount of days to the date
     *
     * @param date datetime
     * @param days days to add
     * @return converted datetime
     * @see com.querydsl.sql.SQLExpressions#addDays(DateTimeExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateTimeExpression<D> addDays(DateTimeExpression<D> date, int days) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_DAYS, date, ConstantImpl.create(days));
    }

    /**
     * Add the given amount of hours to the date
     *
     * @param date datetime
     * @param hours hours to add
     * @return converted datetime
     * @see com.querydsl.sql.SQLExpressions#addHours(DateTimeExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateTimeExpression<D> addHours(DateTimeExpression<D> date, int hours) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_HOURS, date, ConstantImpl.create(hours));
    }

    /**
     * Add the given amount of minutes to the date
     *
     * @param date datetime
     * @param minutes minutes to add
     * @return converted datetime
     * @see com.querydsl.sql.SQLExpressions#addMinutes(DateTimeExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateTimeExpression<D> addMinutes(DateTimeExpression<D> date, int minutes) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_MINUTES, date, ConstantImpl.create(minutes));
    }

    /**
     * Add the given amount of seconds to the date
     *
     * @param date datetime
     * @param seconds seconds to add
     * @return converted datetime
     * @see com.querydsl.sql.SQLExpressions#addSeconds(DateTimeExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateTimeExpression<D> addSeconds(DateTimeExpression<D> date, int seconds) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_SECONDS, date, ConstantImpl.create(seconds));
    }

    /**
     * Add the given amount of years to the date
     *
     * @param date date
     * @param years years to add
     * @return converted date
     * @see com.querydsl.sql.SQLExpressions#addYears(DateExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateExpression<D> addYears(DateExpression<D> date, int years) {
        return Expressions.dateOperation(date.getType(), Ops.DateTimeOps.ADD_YEARS, date, ConstantImpl.create(years));
    }

    /**
     * Add the given amount of months to the date
     *
     * @param date date
     * @param months months to add
     * @return converted date
     * @see com.querydsl.sql.SQLExpressions#addMonths(DateExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateExpression<D> addMonths(DateExpression<D> date, int months) {
        return Expressions.dateOperation(date.getType(), Ops.DateTimeOps.ADD_MONTHS, date, ConstantImpl.create(months));
    }

    /**
     * Add the given amount of weeks to the date
     *
     * @param date date
     * @param weeks weeks to add
     * @return converted date
     * @see com.querydsl.sql.SQLExpressions#addWeeks(DateExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateExpression<D> addWeeks(DateExpression<D> date, int weeks) {
        return Expressions.dateOperation(date.getType(), Ops.DateTimeOps.ADD_WEEKS, date, ConstantImpl.create(weeks));
    }

    /**
     * Add the given amount of days to the date
     *
     * @param date date
     * @param days days to add
     * @return converted date
     * @see com.querydsl.sql.SQLExpressions#addDays(DateExpression, int)
     * @since 1.0
     */
    public static <D extends Comparable<?>> DateExpression<D> addDays(DateExpression<D> date, int days) {
        return Expressions.dateOperation(date.getType(), Ops.DateTimeOps.ADD_DAYS, date, ConstantImpl.create(days));
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return all(expr)
     * @since 1.0
     */
    public static WindowOver<Boolean> all(Expression<Boolean> expr) {
        return new WindowOver<Boolean>(expr.getType(), Ops.AggOps.BOOLEAN_ALL, expr);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return any(expr)
     * @since 1.0
     */
    public static WindowOver<Boolean> any(Expression<Boolean> expr) {
        return new WindowOver<Boolean>(expr.getType(), Ops.AggOps.BOOLEAN_ANY, expr);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return sum(expr)
     * @since 1.0
     */
    public static <T extends Number> WindowOver<T> sum(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), Ops.AggOps.SUM_AGG, expr);
    }

    /**
     * Start a window function expression
     *
     * @return count()
     * @since 1.0
     */
    public static WindowOver<Long> count() {
        return new WindowOver<Long>(Long.class, Ops.AggOps.COUNT_ALL_AGG);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return count(expr)
     * @since 1.0
     */
    public static WindowOver<Long> count(Expression<?> expr) {
        return new WindowOver<Long>(Long.class, Ops.AggOps.COUNT_AGG, expr);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return count(distinct expr)
     * @since 1.0
     */
    public static WindowOver<Long> countDistinct(Expression<?> expr) {
        return new WindowOver<Long>(Long.class, Ops.AggOps.COUNT_DISTINCT_AGG, expr);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return avg(expr)
     * @since 1.0
     */
    public static <T extends Number> WindowOver<T> avg(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), Ops.AggOps.AVG_AGG, expr);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return min(expr)
     * @since 1.0
     */
    public static <T extends Comparable> WindowOver<T> min(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), Ops.AggOps.MIN_AGG, expr);
    }

    /**
     * Start a window function expression
     *
     * @param expr expression
     * @return max(expr)
     * @since 1.0
     */
    public static <T extends Comparable> WindowOver<T> max(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), Ops.AggOps.MAX_AGG, expr);
    }

    /**
     * expr evaluated at the row that is one row after the current row within the partition;
     *
     * @param expr expression
     * @return lead(expr)
     * @since 1.0
     */
    public static <T> WindowOver<T> lead(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), JPQLNextOps.LEAD, expr);
    }

    /**
     * expr evaluated at the row that is one row before the current row within the partition
     *
     * @param expr expression
     * @return lag(expr)
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number & Comparable> WindowOver<T> ntile(T num) {
        return new WindowOver<T>((Class<T>) num.getClass(), JPQLNextOps.NTILE, ConstantImpl.create(num));
    }

    /**
     * rank of the current row with gaps; same as row_number of its first peer
     *
     * @return rank()
     * @since 1.0
     */
    public static WindowOver<Long> rank() {
        return new WindowOver<Long>(Long.class, JPQLNextOps.RANK);
    }

    /**
     * rank of the current row without gaps; this function counts peer groups
     *
     * @return dense_rank()
     * @since 1.0
     */
    public static WindowOver<Long> denseRank() {
        return new WindowOver<Long>(Long.class, JPQLNextOps.DENSE_RANK);
    }

    /**
     * As an analytic function, for a row r, PERCENT_RANK calculates the rank of r minus 1, divided by
     * 1 less than the number of rows being evaluated (the entire query result set or a partition).
     *
     * @return percent_rank()
     * @since 1.0
     */
    public static WindowOver<Double> percentRank() {
        return new WindowOver<Double>(Double.class, JPQLNextOps.PERCENT_RANK);
    }

    /**
     * CUME_DIST calculates the cumulative distribution of a value in a group of values.
     *
     * @return cume_dist()
     * @since 1.0
     */
    public static WindowOver<Double> cumeDist() {
        return new WindowOver<Double>(Double.class, JPQLNextOps.CUME_DIST);
    }

    /**
     * number of the current row within its partition, counting from 1
     *
     * @return row_number()
     * @since 1.0
     */
    public static WindowOver<Long> rowNumber() {
        return new WindowOver<Long>(Long.class, JPQLNextOps.ROW_NUMBER);
    }

    /**
     * returns value evaluated at the row that is the first row of the window frame
     *
     * @param expr argument
     * @return first_value(expr)
     * @since 1.0
     */
    public static <T> WindowOver<T> firstValue(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), JPQLNextOps.FIRST_VALUE, expr);
    }

    /**
     * returns value evaluated at the row that is the last row of the window frame
     *
     * @param expr argument
     * @return last_value(expr)
     * @since 1.0
     */
    public static <T> WindowOver<T> lastValue(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), JPQLNextOps.LAST_VALUE, expr);
    }

    public static <T> Expression<T> cast(Class<T> result, Expression<?> expression) {
        if (Boolean.class.equals(result) || boolean.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_BOOLEAN, expression);
        } else if (Byte.class.equals(result) || byte.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_BYTE, expression);
        } else if (Short.class.equals(result) || short.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_SHORT, expression);
        } else if (Long.class.equals(result) || long.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_LONG, expression);
        } else if (Integer.class.equals(result) || int.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_INTEGER, expression);
        } else if (Float.class.equals(result) || float.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_FLOAT, expression);
        } else if (Double.class.equals(result) || double.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_DOUBLE, expression);
        } else if (Character.class.equals(result) || char.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_CHARACTER, expression);
        } else if (String.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_STRING, expression);
        } else if (BigInteger.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_BIGINTEGER, expression);
        } else if (BigDecimal.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_BIGDECIMAL, expression);
        } else if (Calendar.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_CALENDAR, expression);
        } else if (Timestamp.class.isAssignableFrom(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_TIMESTAMP, expression);
        } else if (Time.class.isAssignableFrom(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_TIME, expression);
        } else if (Date.class.isAssignableFrom(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.CAST_DATE, expression);
        } else {
            throw new IllegalArgumentException("No cast operation for " + result.getName());
        }
    }

    public static <T> Expression<T> treat(Class<T> result, Expression<?> expression) {
        if (Boolean.class.equals(result) || boolean.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_BOOLEAN, expression);
        } else if (Byte.class.equals(result) || byte.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_BYTE, expression);
        } else if (Short.class.equals(result) || short.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_SHORT, expression);
        } else if (Long.class.equals(result) || long.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_LONG, expression);
        } else if (Integer.class.equals(result) || int.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_INTEGER, expression);
        } else if (Float.class.equals(result) || float.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_FLOAT, expression);
        } else if (Double.class.equals(result) || double.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_DOUBLE, expression);
        } else if (Character.class.equals(result) || char.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_CHARACTER, expression);
        } else if (String.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_STRING, expression);
        } else if (BigInteger.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_BIGINTEGER, expression);
        } else if (BigDecimal.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_BIGDECIMAL, expression);
        } else if (Calendar.class.equals(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_CALENDAR, expression);
        } else if (Timestamp.class.isAssignableFrom(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_TIMESTAMP, expression);
        } else if (Time.class.isAssignableFrom(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_TIME, expression);
        } else if (Date.class.isAssignableFrom(result)) {
            return Expressions.simpleOperation(result, JPQLNextOps.TREAT_DATE, expression);
        } else {
            throw new IllegalArgumentException("No cast operation for " + result.getName());
        }
    }
}
