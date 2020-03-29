package com.pallasathenagroup.querydsl.api;

/**
 * A base interface for builders that support basic query functionality.
 * This interface is shared between normal query builders and subquery builders.
 *
 * @param <X> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface CommonQueryBuilder<X extends CommonQueryBuilder<X>> extends ParameterHolder<X> {
}
