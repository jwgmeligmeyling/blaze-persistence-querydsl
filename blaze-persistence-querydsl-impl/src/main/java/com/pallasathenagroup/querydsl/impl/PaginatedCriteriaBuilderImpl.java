package com.pallasathenagroup.querydsl.impl;

import com.blazebit.persistence.PagedList;
import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.api.PaginatedCriteriaBuilder;

public class PaginatedCriteriaBuilderImpl<T>
        extends AbstractFullQueryBuilder<T, PaginatedCriteriaBuilder<T>>
        implements PaginatedCriteriaBuilder<T> {

    private int firstResult, maxResults;

    public PaginatedCriteriaBuilderImpl(BlazeJPAQuery<T> blazeJPAQuery, int firstResult, int maxResults) {
        super(blazeJPAQuery);
        this.firstResult = firstResult;
        this.maxResults = maxResults;
    }

    @Override
    public PaginatedCriteriaBuilder<T> page(int firstResult, int maxResults) {
        this.firstResult = firstResult;
        this.maxResults = maxResults;
        return this;
    }

    @Override
    public PagedList<T> getResultList() {
        return blazeJPAQuery.fetchPage(firstResult, maxResults);
    }

}
