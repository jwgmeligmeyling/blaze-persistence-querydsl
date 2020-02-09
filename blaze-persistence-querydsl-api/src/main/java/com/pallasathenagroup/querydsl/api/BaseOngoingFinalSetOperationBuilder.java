package com.pallasathenagroup.querydsl.api;

public interface BaseOngoingFinalSetOperationBuilder<T, X extends BaseFinalSetOperationBuilder<T, X>> extends BaseFinalSetOperationBuilder<T, X> {

    /**
     * Ends the set operation and returns the parent builder.
     *
     * @return The parent builder
     */
    public T endSet();
}
