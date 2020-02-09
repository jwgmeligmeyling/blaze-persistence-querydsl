package com.pallasathenagroup.querydsl.api;


public interface BaseQueryBuilder<T, X extends BaseQueryBuilder<T, X>> extends CommonQueryBuilder<X>, FromBuilder<X>, KeysetQueryBuilder<X>, WhereBuilder<X>, OrderByBuilder<X>, SelectBuilder<X, T>, CorrelationQueryBuilder<X>, WindowContainerBuilder<X> {

    /**
     * Returns the result type of this query.
     *
     * @return The result type of this query
     */
    public Class<T> getResultType();

}