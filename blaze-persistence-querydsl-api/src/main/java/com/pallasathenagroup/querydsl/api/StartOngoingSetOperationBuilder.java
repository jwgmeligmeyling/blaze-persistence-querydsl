package com.pallasathenagroup.querydsl.api;


public interface StartOngoingSetOperationBuilder<X, Y, Z extends StartOngoingSetOperationBuilder<?, ?, ?>>
        extends SetOperationBuilder<X, Z>
{

    /**
     * Starts a nested set operation builder.
     * Doing this is like starting a nested query that will be connected via a set operation.
     *
     * @return The set operation builder
     * @since 1.2.0
     */
    public Z startSet();
}
