package com.pallasathenagroup.querydsl.api;

public interface SetOperationBuilder<X, Y extends StartOngoingSetOperationBuilder<?, ?, ?>> {

    /**
     * Connects this query with the union operator with query following after this call.
     *
     * @return The query builder that should be connected via union
     */
    public X union();

    /**
     * Connects this query with the union all operator with query following after this call.
     *
     * @return The query builder that should be connected via union
     */
    public X unionAll();

    /**
     * Connects this query with the intersect operator with query following after this call.
     *
     * @return The query builder that should be connected via union
     */
    public X intersect();

    /**
     * Connects this query with the intersect all operator with query following after this call.
     *
     * @return The query builder that should be connected via union
     */
    public X intersectAll();

    /**
     * Connects this query with the except operator with query following after this call.
     *
     * @return The query builder that should be connected via union
     */
    public X except();

    /**
     * Connects this query with the except all operator with query following after this call.
     *
     * @return The query builder that should be connected via union
     */
    public X exceptAll();

    /* Subquery variants */

    /**
     * Connects this query with the union operator with subquery following after this call.
     *
     * @return The query builder that should be connected via union
     */
    public Y startUnion();

    /**
     * Connects this query with the union all operator with subquery following after this call.
     *
     * @return The query builder that should be connected via union
     */
    public Y startUnionAll();

    /**
     * Connects this query with the intersect operator with subquery following after this call.
     *
     * @return The query builder that should be connected via union
     */
    public Y startIntersect();

    /**
     * Connects this query with the intersect all operator with subquery following after this call.
     *
     * @return The query builder that should be connected via union
     */
    public Y startIntersectAll();

    /**
     * Connects this query with the except operator with subquery following after this call.
     *
     * @return The query builder that should be connected via union
     */
    public Y startExcept();

    /**
     * Connects this query with the except all operator with subquery following after this call.
     *
     * @return The query builder that should be connected via union
     */
    public Y startExceptAll();
}
