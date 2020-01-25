package com.pallasathenagroup.querydsl;

import com.pallasathenagroup.querydsl.experimental.BindBuilder;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;
import com.querydsl.jpa.JPQLQuery;

import java.util.Collection;

public interface ExtendedJPAQuery<T, Q extends ExtendedJPAQuery<T, Q>> extends JPQLQuery<T> {

    WithBuilder<Q> with(EntityPath<?> alias, Path<?>... columns);

    WithBuilder<Q> withRecursive(EntityPath<?> alias, Path<?>... columns);

    default BindBuilder<T, Q> with(EntityPath<?> alias) {
        return new BindBuilder<T, Q>((Q) this, alias, false);
    }

    default BindBuilder<T, Q> withRecursive(EntityPath<?> alias) {
        return new BindBuilder<T, Q>((Q) this, alias, true);
    }

    <X> Q fromValues(EntityPath<X> path, Collection<X> elements);

    <X> Q fromIdentifiableValues(EntityPath<X> path, Collection<X> elements);

    <P> Q fullJoin(CollectionExpression<?,P> target);

    <P> Q fullJoin(CollectionExpression<?,P>target, Path<P> alias);

    <P> Q fullJoin(EntityPath<P> target);

    <P> Q fullJoin(EntityPath<P> target, Path<P> alias);

    <P> Q fullJoin(MapExpression<?,P> target);

    <P> Q fullJoin(MapExpression<?,P> target, Path<P> alias);

    Q union();


}
