package com.pallasathenagroup.querydsl;

import com.querydsl.core.Fetchable;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.SubQueryExpression;

import javax.annotation.Nonnegative;

public interface SetExpression<RT> extends SubQueryExpression<RT>, Fetchable<RT>, ExtendedFetchable<RT> {


    /**
     * Set the limit / max results for the query results
     *
     * @param limit max rows
     * @return the current object
     */
    SetExpression<RT> limit(@Nonnegative long limit);

    /**
     * Set the offset for the query results
     *
     * @param offset row offset
     * @return the current object
     */
    SetExpression<RT> offset(@Nonnegative long offset);


    /**
     * Define the ordering of the query results
     *
     * @param o order
     * @return the current object
     */
    SetExpression<RT> orderBy(OrderSpecifier<?>... o);

}
