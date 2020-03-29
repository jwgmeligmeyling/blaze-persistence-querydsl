package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;
import com.querydsl.core.Fetchable;
import com.querydsl.core.NonUniqueResultException;

import java.util.Optional;

public interface ExtendedFetchable<T> extends Fetchable<T>  {

    PagedList<T> fetchPage(int firstResult, int maxResults);

    PagedList<T> fetchPage(KeysetPage keysetPage, int firstResult, int maxResults);

    default Optional<T> fetchOneOptional() throws NonUniqueResultException {
        return Optional.ofNullable(fetchOne());
    }

    default Optional<T> fetchFirstOptional() {
        return Optional.ofNullable(fetchFirst());
    }

    String getQueryString();

}
