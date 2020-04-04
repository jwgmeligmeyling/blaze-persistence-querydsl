package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;

import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.Map;

/**
 * Utility methods for dealing with date fields
 *
 * @since 1.0
 * @author Jan-Willem Gmelig Meyling
 */
public final class DateExpressions {

    private static final Map<ChronoUnit, Operator> DATE_ADD_OPS
            = new EnumMap<ChronoUnit, Operator>(ChronoUnit.class);

    private static final Map<ChronoUnit, Operator> DATE_DIFF_OPS
            = new EnumMap<ChronoUnit, Operator>(ChronoUnit.class);

    private static final Map<ChronoUnit, Operator> DATE_TRUNC_OPS
            = new EnumMap<ChronoUnit, Operator>(ChronoUnit.class);

    static {
        DATE_ADD_OPS.put(ChronoUnit.YEARS, Ops.DateTimeOps.ADD_YEARS);
        DATE_ADD_OPS.put(ChronoUnit.MONTHS, Ops.DateTimeOps.ADD_MONTHS);
        DATE_ADD_OPS.put(ChronoUnit.WEEKS, Ops.DateTimeOps.ADD_WEEKS);
        DATE_ADD_OPS.put(ChronoUnit.DAYS, Ops.DateTimeOps.ADD_DAYS);
        DATE_ADD_OPS.put(ChronoUnit.HOURS, Ops.DateTimeOps.ADD_HOURS);
        DATE_ADD_OPS.put(ChronoUnit.MINUTES, Ops.DateTimeOps.ADD_MINUTES);
        DATE_ADD_OPS.put(ChronoUnit.SECONDS, Ops.DateTimeOps.ADD_SECONDS);
        DATE_ADD_OPS.put(ChronoUnit.MILLIS, null); // TODO

        DATE_DIFF_OPS.put(ChronoUnit.YEARS, Ops.DateTimeOps.DIFF_YEARS);
        DATE_DIFF_OPS.put(ChronoUnit.MONTHS, Ops.DateTimeOps.DIFF_MONTHS);
        DATE_DIFF_OPS.put(ChronoUnit.WEEKS, Ops.DateTimeOps.DIFF_WEEKS);
        DATE_DIFF_OPS.put(ChronoUnit.DAYS, Ops.DateTimeOps.DIFF_DAYS);
        DATE_DIFF_OPS.put(ChronoUnit.HOURS, Ops.DateTimeOps.DIFF_HOURS);
        DATE_DIFF_OPS.put(ChronoUnit.MINUTES, Ops.DateTimeOps.DIFF_MINUTES);
        DATE_DIFF_OPS.put(ChronoUnit.SECONDS, Ops.DateTimeOps.DIFF_SECONDS);
        DATE_DIFF_OPS.put(ChronoUnit.MILLIS, null); // TODO

        DATE_TRUNC_OPS.put(ChronoUnit.YEARS, Ops.DateTimeOps.TRUNC_YEAR);
        DATE_TRUNC_OPS.put(ChronoUnit.MONTHS, Ops.DateTimeOps.TRUNC_MONTH);
        DATE_TRUNC_OPS.put(ChronoUnit.WEEKS, Ops.DateTimeOps.TRUNC_WEEK);
        DATE_TRUNC_OPS.put(ChronoUnit.DAYS, Ops.DateTimeOps.TRUNC_DAY);
        DATE_TRUNC_OPS.put(ChronoUnit.HOURS, Ops.DateTimeOps.TRUNC_HOUR);
        DATE_TRUNC_OPS.put(ChronoUnit.MINUTES, Ops.DateTimeOps.TRUNC_MINUTE);
        DATE_TRUNC_OPS.put(ChronoUnit.SECONDS, Ops.DateTimeOps.TRUNC_SECOND);
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
    
}
