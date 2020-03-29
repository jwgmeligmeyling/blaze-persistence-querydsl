package com.pallasathenagroup.querydsl.api;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.SetOperationBuilder;
import com.pallasathenagroup.querydsl.BlazeJPAQuery;

import javax.persistence.EntityManager;

/**
 * A builder for criteria queries. This is the entry point for building queries.
 *
 * @param <T> The query result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface CriteriaBuilder<T> extends
        FullQueryBuilder<T, CriteriaBuilder<T>>,
        BaseCriteriaBuilder<T, CriteriaBuilder<T>>,
        CTEBuilder<CriteriaBuilder<T>>,
        SetOperationBuilder<
                        LeafOngoingSetOperationCriteriaBuilder<T>,
                        StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T>> {


    // TODO: Actually a workaround to simulate CriteriaBuilderFactory.startSet(...)
    StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T> startSet();

    public static <T> CriteriaBuilder<T> getCriteriaBuilder(EntityManager em, CriteriaBuilderFactory criteriaBuilderFactory) {
        try {
            // TODO Service provider
            BlazeJPAQuery<T> blazeJPAQuery = new BlazeJPAQuery<T>(em, criteriaBuilderFactory);
            return (CriteriaBuilder<T>) Class.forName("com.pallasathenagroup.querydsl.impl.CriteriaBuilderImpl").getConstructor(BlazeJPAQuery.class).newInstance(blazeJPAQuery);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
