package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;

import java.util.Collection;

public interface FromBuilder<Q extends FromBuilder<Q>> {

    <X> Q fromValues(EntityPath<X> path, Collection<X> elements);

    <X> Q fromIdentifiableValues(EntityPath<X> path, Collection<X> elements);

    <P> Q fullJoin(CollectionExpression<?, P> target);

    <P> Q fullJoin(CollectionExpression<?, P> target, Path<P> alias);

    <P> Q fullJoin(EntityPath<P> target);

    <P> Q fullJoin(EntityPath<P> target, Path<P> alias);

    <P> Q fullJoin(MapExpression<?, P> target);

    <P> Q fullJoin(MapExpression<?, P> target, Path<P> alias);

    <X> Q from(SubQueryExpression<X> subQueryExpression, Path<X> alias);

    /**
     * Adds a left join to the given target
     *
     * @param o     subquery
     * @param alias alias
     * @return the current object
     */
    <X> Q leftJoin(SubQueryExpression<X> o, Path<X> alias);

    /**
     * Adds a right join to the given target
     *
     * @param o     subquery
     * @param alias alias
     * @return the current object
     */
    <X> Q rightJoin(SubQueryExpression<X> o, Path<X> alias);

    /**
     * Adds a full join to the given target
     *
     * @param o     subquery
     * @param alias alias
     * @return the current object
     */
    <X> Q fullJoin(SubQueryExpression<X> o, Path<X> alias);

    /**
     * Adds a inner join to the given target
     *
     * @param o     subquery
     * @param alias alias
     * @return the current object
     */
    <X> Q innerJoin(SubQueryExpression<X> o, Path<X> alias);

    /**
     * Mark the last join as a lateral join
     *
     * @return this query
     */
    Q lateral();


    Q from(EntityPath<?>... sources);


    <P> Q from(CollectionExpression<?, P> target, Path<P> alias);


    <P> Q innerJoin(EntityPath<P> target);


    <P> Q innerJoin(EntityPath<P> target, Path<P> alias);


    <P> Q innerJoin(CollectionExpression<?, P> target);


    <P> Q innerJoin(CollectionExpression<?, P> target, Path<P> alias);


    <P> Q innerJoin(MapExpression<?, P> target);


    <P> Q innerJoin(MapExpression<?, P> target, Path<P> alias);


    <P> Q join(EntityPath<P> target);


    <P> Q join(EntityPath<P> target, Path<P> alias);


    <P> Q join(CollectionExpression<?, P> target);


    <P> Q join(CollectionExpression<?, P> target, Path<P> alias);


    <P> Q join(MapExpression<?, P> target);


    <P> Q join(MapExpression<?, P> target, Path<P> alias);


    <P> Q leftJoin(EntityPath<P> target);


    <P> Q leftJoin(EntityPath<P> target, Path<P> alias);


    <P> Q leftJoin(CollectionExpression<?, P> target);


    <P> Q leftJoin(CollectionExpression<?, P> target, Path<P> alias);


    <P> Q leftJoin(MapExpression<?, P> target);


    <P> Q leftJoin(MapExpression<?, P> target, Path<P> alias);


    <P> Q rightJoin(EntityPath<P> target);


    <P> Q rightJoin(EntityPath<P> target, Path<P> alias);


    <P> Q rightJoin(CollectionExpression<?, P> target);


    <P> Q rightJoin(CollectionExpression<?, P> target, Path<P> alias);


    <P> Q rightJoin(MapExpression<?, P> target);


    <P> Q rightJoin(MapExpression<?, P> target, Path<P> alias);


    Q on(Predicate... condition);


    Q fetchJoin();


    Q fetchAll();

}
