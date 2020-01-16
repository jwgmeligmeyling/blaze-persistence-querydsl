package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.Ops;
import com.querydsl.jpa.DefaultQueryHandler;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.QueryHandler;

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
    }
}
