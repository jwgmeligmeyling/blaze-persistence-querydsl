package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.ParamExpression;

/**
 * A base interface for builders that can hold parameters.
 *
 * @param <Q> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface ParameterHolder<Q extends ParameterHolder<Q>> {

    /**
     * Set the given parameter to the given value
     *
     * @param <U> parameter value type
     * @param paramExpression param
     * @param u binding
     * @return the current object
     */
    <U> Q set(ParamExpression<U> paramExpression, U u);
}
