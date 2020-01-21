package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;
import com.querydsl.jpa.JPQLQuery;

import java.util.Collection;

interface ExtendedJPAQuery<T, Q extends ExtendedJPAQuery<T, Q>> extends JPQLQuery<T> {

    WithBuilder<Q> with(EntityPath<?> alias, Path<?>... columns);

    WithBuilder<Q> withRecursive(EntityPath<?> alias, Path<?>... columns);

    <X> Q fromValues(EntityPath<X> path, Collection<X> elements);

    <X> Q fromIdentifiableValues(EntityPath<X> path, Collection<X> elements);

    <P> BlazeJPAQuery<T> fullJoin(CollectionExpression<?,P> target);

    <P> BlazeJPAQuery<T> fullJoin(CollectionExpression<?,P>target, Path<P> alias);

    <P> BlazeJPAQuery<T> fullJoin(EntityPath<P> target);

    <P> BlazeJPAQuery<T> fullJoin(EntityPath<P> target, Path<P> alias);

    <P> BlazeJPAQuery<T> fullJoin(MapExpression<?,P> target);

    <P> BlazeJPAQuery<T> fullJoin(MapExpression<?,P> target, Path<P> alias);

}
