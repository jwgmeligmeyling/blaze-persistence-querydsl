package com.pallasathenagroup.querydsl;

import com.querydsl.core.Fetchable;
import com.querydsl.core.types.SubQueryExpression;

public interface Union<RT> extends SubQueryExpression<RT>, Fetchable<RT>, ExtendedFetchable<RT> {
}
