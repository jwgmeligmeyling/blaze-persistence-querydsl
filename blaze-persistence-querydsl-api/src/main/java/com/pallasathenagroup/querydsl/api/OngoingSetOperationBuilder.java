package com.pallasathenagroup.querydsl.api;

public interface OngoingSetOperationBuilder<X, Y, Z extends StartOngoingSetOperationBuilder<?, ?, ?>> extends BaseOngoingSetOperationBuilder<X, Y, Z> {

    /**
     * Finishes the current set operation builder and returns a final builder for ordering and limiting.
     *
     * @return The final builder for ordering and limiting
     */
    public BaseOngoingFinalSetOperationBuilder<Y, ?> endSetWith();
}
