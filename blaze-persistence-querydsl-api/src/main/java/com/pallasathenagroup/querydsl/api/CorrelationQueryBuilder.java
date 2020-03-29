package com.pallasathenagroup.querydsl.api;

/**
 * An interface for correlation query builders.
 *
 * @param <X> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface CorrelationQueryBuilder<X extends CorrelationQueryBuilder<X>> extends FromBuilder<X>, ParameterHolder<X> {
}
