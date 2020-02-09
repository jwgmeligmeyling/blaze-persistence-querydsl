package com.pallasathenagroup.querydsl.api;

import javax.persistence.TypedQuery;
import java.util.List;

public interface Queryable<T, X extends Queryable<T, X>> {

    /**
     * Returns the query string for the built query.
     *
     * @return The query string
     */
    public String getQueryString();

    /**
     * Returns the JPA typed query for the built query.
     * The returned query is already parameterized with all known parameters.
     *
     * @return The typed query for the built query
     */
    public TypedQuery<T> getQuery();

    /**
     * Execute the query and return the result as a type List.
     *
     * @return The list of the results
     */
    public List<T> getResultList();

    /**
     * Execute the query expecting a single result.
     *
     * @return The single result
     */
    public T getSingleResult();

}
