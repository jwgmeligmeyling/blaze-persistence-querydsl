package com.pallasathenagroup.querydsl.api;

public interface BaseOngoingSetOperationBuilder<X, Y, Z extends StartOngoingSetOperationBuilder<?, ?, ?>> extends SetOperationBuilder<X, Z> {

    /**
     * Ends the current set operation scope and switches back to the parent query.
     *
     * @return The parent query builder
     */
    public Y endSet();
}
