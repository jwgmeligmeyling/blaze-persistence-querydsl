package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.parser.util.TypeConverter;
import com.blazebit.persistence.parser.util.TypeUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.jpa.DefaultQueryHandler;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.QueryHandler;

/**
 * {@code JPQLNextTemplates} extends {@link JPQLTemplates} to provide operator patterns for JPQL Next
 * serialization
 *
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public class JPQLNextTemplates extends JPQLTemplates {

    public static final JPQLNextTemplates DEFAULT = new JPQLNextTemplates();

    public JPQLNextTemplates() {
        this(DEFAULT_ESCAPE, DefaultQueryHandler.DEFAULT);
    }

    public JPQLNextTemplates(char escape) {
        this(escape, DefaultQueryHandler.DEFAULT);
    }

    public JPQLNextTemplates(char escape, QueryHandler queryHandler) {
        super(escape, queryHandler);

        add(Ops.DateTimeOps.MILLISECOND, "millisecond(0)");
        add(Ops.DateTimeOps.SECOND, "second({0})");
        add(Ops.DateTimeOps.MINUTE, "minute({0})");
        add(Ops.DateTimeOps.HOUR, "hour({0})");
        add(Ops.DateTimeOps.DAY_OF_MONTH, "day({0})");
        add(Ops.DateTimeOps.DAY_OF_WEEK, "dayofweek({0})");
        add(Ops.DateTimeOps.DAY_OF_YEAR, "dayofyear({0})");
        add(Ops.DateTimeOps.MONTH, "month({0})");
        add(Ops.DateTimeOps.YEAR, "year({0})");
        add(Ops.DateTimeOps.WEEK, "iso_week({0})");

        add(Ops.DateTimeOps.ADD_DAYS, "add_day({0}, {1})");
        add(Ops.DateTimeOps.ADD_HOURS, "add_hour({0}, {1})");
        add(Ops.DateTimeOps.ADD_MINUTES, "add_minute({0}, {1})");
        add(Ops.DateTimeOps.ADD_MONTHS, "add_month({0}, {1})");
        add(Ops.DateTimeOps.ADD_SECONDS, "add_second({0}, {1})");
        add(Ops.DateTimeOps.ADD_WEEKS, "add_week({0}, {1})");
        add(Ops.DateTimeOps.ADD_YEARS, "add_year({0}, {1})");

        add(Ops.DateTimeOps.DIFF_DAYS, "day_diff({0}, {1})");
        add(Ops.DateTimeOps.DIFF_HOURS, "hour_diff({0}, {1})");
        add(Ops.DateTimeOps.DIFF_MINUTES, "minute_diff({0}, {1})");
        add(Ops.DateTimeOps.DIFF_MONTHS, "month_diff({0}, {1})");
        add(Ops.DateTimeOps.DIFF_SECONDS, "second_diff({0}, {1})");
        add(Ops.DateTimeOps.DIFF_WEEKS, "week_diff({0}, {1})");
        add(Ops.DateTimeOps.DIFF_YEARS, "year_diff({0}, {1})");

        add(Ops.DateTimeOps.TRUNC_DAY, "trunc_day({0})");
        add(Ops.DateTimeOps.TRUNC_HOUR, "trunc_hour({0})");
        add(Ops.DateTimeOps.TRUNC_MINUTE, "trunc_minute({0})");
        add(Ops.DateTimeOps.TRUNC_MONTH, "trunc_month({0})");
        add(Ops.DateTimeOps.TRUNC_SECOND, "trunc_second({0})");
        add(Ops.DateTimeOps.TRUNC_WEEK, "trunc_week({0})");
        add(Ops.DateTimeOps.TRUNC_YEAR, "trunc_year({0})");

        add(Ops.AggOps.BOOLEAN_ALL, "AND_AGG({0})");
        add(Ops.AggOps.BOOLEAN_ANY, "OR_AGG({0})");

        add(JPQLNextOps.CUME_DIST, "cume_dist()");
        add(JPQLNextOps.DENSE_RANK, "dense_rank()");
        add(JPQLNextOps.FIRST_VALUE, "first_value({0})");
        add(JPQLNextOps.LAG, "lag({0})");
        add(JPQLNextOps.LAST_VALUE, "last_value({0})");
        add(JPQLNextOps.LEAD, "lead({0})");
        add(JPQLNextOps.NTH_VALUE, "nth_value({0}, {1})");
        add(JPQLNextOps.NTILE, "ntile({0})");
        add(JPQLNextOps.PERCENT_RANK, "PERCENT_RANK({0})");
        add(JPQLNextOps.RANK, "rank()");
        add(JPQLNextOps.GROUP_CONCAT, "GROUP_CONCAT({0})");
        add(JPQLNextOps.WINDOW_GROUP_CONCAT, "WINDOW_GROUP_CONCAT({0})");
        add(JPQLNextOps.ROW_NUMBER, "row_number()");

        add(JPQLNextOps.SET_UNION, "{0} UNION {1}", Precedence.OR + 1);
        add(JPQLNextOps.SET_UNION_ALL, "{0} UNION ALL {1}", Precedence.OR + 1);
        add(JPQLNextOps.SET_INTERSECT, "{0} INTERSECT {1}", Precedence.OR + 2);
        add(JPQLNextOps.SET_INTERSECT_ALL, "{0} INTERSECT ALL {1}", Precedence.OR + 2);
        add(JPQLNextOps.SET_EXCEPT, "{0} EXCEPT {1}", Precedence.OR + 1);
        add(JPQLNextOps.SET_EXCEPT_ALL, "{0} EXCEPT ALL {1}", Precedence.OR + 1);

        add(JPQLNextOps.LEFT_NESTED_SET_UNION, "({0}) UNION {1}", Precedence.OR + 1);
        add(JPQLNextOps.LEFT_NESTED_SET_UNION_ALL, "({0}) UNION ALL {1}", Precedence.OR + 1);
        add(JPQLNextOps.LEFT_NESTED_SET_INTERSECT, "({0}) INTERSECT {1}", Precedence.OR + 2);
        add(JPQLNextOps.LEFT_NESTED_SET_INTERSECT_ALL, "({0}) INTERSECT ALL {1}", Precedence.OR + 2);
        add(JPQLNextOps.LEFT_NESTED_SET_EXCEPT, "({0}) EXCEPT {1}", Precedence.OR + 1);
        add(JPQLNextOps.LEFT_NESTED_SET_EXCEPT_ALL, "({0}) EXCEPT ALL {1}", Precedence.OR + 1);

        add(JPQLNextOps.CAST_BOOLEAN, "CAST_BOOLEAN({0})");
        add(JPQLNextOps.CAST_BYTE, "CAST_BYTE({0})");
        add(JPQLNextOps.CAST_SHORT, "CAST_SHORT({0})");
        add(JPQLNextOps.CAST_LONG, "CAST_LONG({0})");
        add(JPQLNextOps.CAST_INTEGER, "CAST_INTEGER({0})");
        add(JPQLNextOps.CAST_FLOAT, "CAST_FLOAT({0})");
        add(JPQLNextOps.CAST_DOUBLE, "CAST_DOUBLE({0})");
        add(JPQLNextOps.CAST_CHARACTER, "CAST_CHARACTER({0})");
        add(JPQLNextOps.CAST_STRING, "CAST_STRING({0})");
        add(JPQLNextOps.CAST_BIGINTEGER, "CAST_BIGINTEGER({0})");
        add(JPQLNextOps.CAST_BIGDECIMAL, "CAST_BIGDECIMAL({0})");
        add(JPQLNextOps.CAST_TIME, "CAST_TIME({0})");
        add(JPQLNextOps.CAST_DATE, "CAST_BOOLEAN({0})");
        add(JPQLNextOps.CAST_TIMESTAMP, "CAST_TIMESTAMP({0})");
        add(JPQLNextOps.CAST_CALENDAR, "CAST_CALENDAR({0})");

        add(JPQLNextOps.TREAT_BOOLEAN, "TREAT_BOOLEAN({0})");
        add(JPQLNextOps.TREAT_BYTE, "TREAT_BYTE({0})");
        add(JPQLNextOps.TREAT_SHORT, "TREAT_SHORT({0})");
        add(JPQLNextOps.TREAT_LONG, "TREAT_LONG({0})");
        add(JPQLNextOps.TREAT_INTEGER, "TREAT_INTEGER({0})");
        add(JPQLNextOps.TREAT_FLOAT, "TREAT_FLOAT({0})");
        add(JPQLNextOps.TREAT_DOUBLE, "TREAT_DOUBLE({0})");
        add(JPQLNextOps.TREAT_CHARACTER, "TREAT_CHARACTER({0})");
        add(JPQLNextOps.TREAT_STRING, "TREAT_STRING({0})");
        add(JPQLNextOps.TREAT_BIGINTEGER, "TREAT_BIGINTEGER({0})");
        add(JPQLNextOps.TREAT_BIGDECIMAL, "TREAT_BIGDECIMAL({0})");
        add(JPQLNextOps.TREAT_TIME, "TREAT_TIME({0})");
        add(JPQLNextOps.TREAT_DATE, "TREAT_BOOLEAN({0})");
        add(JPQLNextOps.TREAT_TIMESTAMP, "TREAT_TIMESTAMP({0})");
        add(JPQLNextOps.TREAT_CALENDAR, "TREAT_CALENDAR({0})");


        add(JPQLNextOps.LEAST, "LEAST({0}, {1})");
        add(JPQLNextOps.GREATEST, "GREATEST({0}, {1})");
        add(JPQLNextOps.REPEAT, "REPEAT({0}, {1})");

        add(JPQLNextOps.BIND, "{0}");

        add(Ops.XOR, "({0} AND NOT {1} OR (NOT {0} AND {1}))");

    }

    @Override
    public String asLiteral(Object constant) {
        TypeConverter converter = TypeUtils.getConverter(constant.getClass());
        if (converter != null) {
            return converter.toString(constant);
        }
        return super.asLiteral(constant);
    }

}
