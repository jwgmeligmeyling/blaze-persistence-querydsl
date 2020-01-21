package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;
import com.querydsl.core.Fetchable;

public interface ExtendedFetchable<T> extends Fetchable<T>  {

    PagedList<T> fetchPage(int firstResult, int maxResults);

    PagedList<T> fetchPage(KeysetPage keysetPage, int firstResult, int maxResults);
}
