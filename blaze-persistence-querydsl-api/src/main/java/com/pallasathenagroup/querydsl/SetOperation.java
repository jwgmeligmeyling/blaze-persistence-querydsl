package com.pallasathenagroup.querydsl;

import com.querydsl.core.Fetchable;
import com.querydsl.core.types.SubQueryExpression;

public interface SetOperation<RT> extends SubQueryExpression<RT>, Fetchable<RT>, ExtendedFetchable<RT> {}
