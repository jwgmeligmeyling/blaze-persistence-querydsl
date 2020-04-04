package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;

import java.util.Collection;

/**
 * An interface for builders that support the from clause.
 *
 * @param <Q> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface FromBuilder<Q extends FromBuilder<Q>> {

    /**
     * Select from a set of values using the {@code VALUES} clause.
     *
     * @param path Type of values
     * @param elements The elements
     * @param <X> The element type
     * @return this query
     */
    <X> Q fromValues(EntityPath<X> path, Collection<X> elements);

    /**
     * Select from a set of values using the {@code VALUES} clause.
     *
     * @param path Type of values
     * @param elements The elements
     * @param <X> The element type
     * @return this query
     */
    <X> Q fromIdentifiableValues(EntityPath<X> path, Collection<X> elements);


    /**
     * Select from a set of values using the {@code VALUES} clause.
     *
     * @param path Type of values
     * @param alias The alias from which the values can be referenced
     * @param elements The elements
     * @param <X> The element type
     * @return this query
     * @since 1.0
     */
    <X> Q fromValues(Path<X> path, Path<X> alias, Collection<X> elements);

    /**
     * Select from a set of values using the {@code VALUES} clause.
     *
     * @param path Type of values
     * @param alias The alias from which the values can be referenced
     * @param elements The elements
     * @param <X> The element type
     * @return this query
     * @since 1.0
     */
    <X> Q fromIdentifiableValues(Path<X> path, Path<X> alias, Collection<X> elements);

    /**
     * Create a full join with the given target.
     * Analog to {@link com.querydsl.sql.SQLCommonQuery#fullJoin(EntityPath)}.
     * Use {@link FetchBuilder#fetchJoin()} to add the fetchJoin parameter to this join.
     * Use {@link #lateral()} to use a lateral join for this join.
     *
     * @param <P> The join target type The type of the join target
     * @param target The join target
     * @return this query
     * @apiNote Full joins are only supported by some ORMs, like Hibernate.
     * @since 1.0
     */
    <P> Q fullJoin(CollectionExpression<?, P> target);

    /**
     * Create a full join with the given target.
     * Analog to {@link com.querydsl.sql.SQLCommonQuery#fullJoin(EntityPath)}.
     * Use {@link FetchBuilder#fetchJoin()} to add the fetchJoin parameter to this join.
     * Use {@link #lateral()} to use a lateral join for this join.
     *
     * @param <P> The join target type The type of the join target
     * @param target The join target
     * @param alias alias
     * @return the current object
     * @apiNote Full joins are only supported by some ORMs, like Hibernate.
     * @since 1.0
     */
    <P> Q fullJoin(CollectionExpression<?, P> target, Path<P> alias);

    /**
     * Create a full join with the given target.
     * Analog to {@link com.querydsl.sql.SQLCommonQuery#fullJoin(EntityPath)}.
     * Use {@link FetchBuilder#fetchJoin()} to add the fetchJoin parameter to this join.
     * Use {@link #lateral()} to use a lateral join for this join.
     *
     * @param <P> The join target type The type of the join target
     * @param target The join target
     * @return the current object
     * @apiNote Full joins are only supported by some ORMs, like Hibernate.
     * @since 1.0
     */
    <P> Q fullJoin(EntityPath<P> target);

    /**
     * Create a full join with the given target.
     * Analog to {@link com.querydsl.sql.SQLCommonQuery#fullJoin(EntityPath)}.
     * Use {@link FetchBuilder#fetchJoin()} to add the fetchJoin parameter to this join.
     * Use {@link #lateral()} to use a lateral join for this join.
     *
     * @param <P> The join target type The type of the join target
     * @param target The join target
     * @param alias The alias under which the join can be referenced
     * @return the current object
     * @apiNote Full joins are only supported by some ORMs, like Hibernate.
     * @since 1.0
     */
    <P> Q fullJoin(EntityPath<P> target, Path<P> alias);

    /**
     * Create a full join with the given target.
     * Analog to {@link com.querydsl.sql.SQLCommonQuery#fullJoin(EntityPath)}.
     * Use {@link FetchBuilder#fetchJoin()} to add the fetchJoin parameter to this join.
     * Use {@link #lateral()} to use a lateral join for this join.
     *
     * @param <P> The join target type The type of the join target
     * @param target The join target
     * @return the current object
     * @apiNote Full joins are only supported by some ORMs, like Hibernate.
     * @since 1.0
     */
    <P> Q fullJoin(MapExpression<?, P> target);

    /**
     * Create a full join with the given target.
     * Analog to {@link com.querydsl.sql.SQLCommonQuery#fullJoin(EntityPath)}.
     * Use {@link FetchBuilder#fetchJoin()} to add the fetchJoin parameter to this join.
     * Use {@link #lateral()} to use a lateral join for this join.
     *
     * @param <P> The join target type The type of the join target
     * @param target The join target
     * @param alias The alias under which the join can be referenced
     * @return the current object
     * @apiNote Full joins are only supported by some ORMs, like Hibernate.
     * @since 1.0
     */
    <P> Q fullJoin(MapExpression<?, P> target, Path<P> alias);

    /**
     * Add sources to this query
     *
     * @param subQueryExpression Subquery expression
     * @param alias The alias for the subquery from which it can be referenced in the outer query
     * @return the current object
     * @since 1.0
     */
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

    /**
     * Add sources to this query
     *
     * @param sources sources
     * @return the current object
     */
    Q from(EntityPath<?>... sources);

    /**
     * Add a query source
     *
     * @param target collection
     * @param alias alias
     * @param <P> The join target type
     * @return the current object
     */
    <P> Q from(CollectionExpression<?, P> target, Path<P> alias);

    /**
     * Create a inner join with the given target.
     * Use fetchJoin() to add the fetchJoin parameter to this join.
     *
     * @param <P> The join target type
     * @param target target
     * @return the current object
     */
    <P> Q innerJoin(EntityPath<P> target);

    /**
     * Create a inner join with the given target and alias.
     *
     * @param <P> The join target type
     * @param target target
     * @param alias alias
     * @return the current object
     */
    <P> Q innerJoin(EntityPath<P> target, Path<P> alias);

    /**
     * Create a inner join with the given target.
     * Use fetchJoin() to add the fetchJoin parameter to this join.
     *
     * @param <P> The join target type
     * @param target target
     * @return the current object
     */
    <P> Q innerJoin(CollectionExpression<?, P> target);

    /**
     * Create a inner join with the given target and alias.
     *
     * @param <P> The join target type
     * @param target target
     * @param alias alias
     * @return the current object
     */
    <P> Q innerJoin(CollectionExpression<?, P> target, Path<P> alias);

    /**
     * Create a inner join with the given target.
     * Use fetchJoin() to add the fetchJoin parameter to this join.
     *
     * @param <P> The join target type
     * @param target target
     * @return the current object
     */
    <P> Q innerJoin(MapExpression<?, P> target);

    /**
     * Create a inner join with the given target and alias.
     *
     * @param <P> The join target type
     * @param target target
     * @param alias alias
     * @return the current object
     */
    <P> Q innerJoin(MapExpression<?, P> target, Path<P> alias);

    /**
     * Create a join with the given target.
     * Use fetchJoin() to add the fetchJoin parameter to this join.
     *
     * @param <P> The join target type
     * @param target target
     * @return the current object
     */
    <P> Q join(EntityPath<P> target);

    /**
     * Create a join with the given target and alias.
     *
     * @param <P> The join target type
     * @param target target
     * @param alias alias
     * @return the current object
     */
    <P> Q join(EntityPath<P> target, Path<P> alias);

    /**
     * Create a join with the given target.
     * Use fetchJoin() to add the fetchJoin parameter to this join.
     *
     * @param <P> The join target type
     * @param target target
     * @return the current object
     */
    <P> Q join(CollectionExpression<?, P> target);

    /**
     * Create a join with the given target
     * Use fetchJoin() to add the fetchJoin parameter to this join
     *
     * @param <P> The join target type
     * @param target target
     * @param alias alias
     * @return the current object
     */
    <P> Q join(CollectionExpression<?, P> target, Path<P> alias);

    /**
     * Create a join with the given target.
     * Use fetchJoin() to add the fetchJoin parameter to this join.
     *
     * @param <P> The join target type
     * @param target target
     * @return the current object
     */
    <P> Q join(MapExpression<?, P> target);

    /**
     * Create a join with the given target and alias.
     *
     * @param <P> The join target type
     * @param target target
     * @param alias alias
     * @return the current object
     */
    <P> Q join(MapExpression<?, P> target, Path<P> alias);

    /**
     * Create a left join with the given target.
     * Use fetchJoin() to add the fetchJoin parameter to this join.
     *
     * @param <P> The join target type
     * @param target target
     * @return the current object
     */
    <P> Q leftJoin(EntityPath<P> target);

    /**
     * Create a left join with the given target and alias.
     *
     * @param <P> The join target type
     * @param target target
     * @param alias alias
     * @return the current object
     */
    <P> Q leftJoin(EntityPath<P> target, Path<P> alias);

    /**
     * Create a left join with the given target.
     * Use fetchJoin() to add the fetchJoin parameter to this join.
     *
     * @param <P> The join target type
     * @param target target
     * @return the current object
     */
    <P> Q leftJoin(CollectionExpression<?, P> target);

    /**
     * Create a left join with the given target and alias.
     *
     * @param <P> The join target type
     * @param target target
     * @param alias alias
     * @return the current object
     */
    <P> Q leftJoin(CollectionExpression<?, P> target, Path<P> alias);

    /**
     * Create a left join with the given target.
     * Use fetchJoin() to add the fetchJoin parameter to this join.
     *
     * @param <P> The join target type
     * @param target target
     * @return the current object
     */
    <P> Q leftJoin(MapExpression<?, P> target);

    /**
     * Create a left join with the given target and alias.
     *
     * @param <P> The join target type
     * @param target target
     * @param alias alias
     * @return the current object
     */
    <P> Q leftJoin(MapExpression<?, P> target, Path<P> alias);

    /**
     * Create a right join with the given target.
     * Use fetchJoin() to add the fetchJoin parameter to this join.
     *
     * @param <P> The join target type
     * @param target target
     * @return the current object
     */
    <P> Q rightJoin(EntityPath<P> target);

    /**
     * Create a right join with the given target and alias.
     *
     * @param <P> The join target type
     * @param target target
     * @param alias alias
     * @return the current object
     */
    <P> Q rightJoin(EntityPath<P> target, Path<P> alias);

    /**
     * Create a right join with the given target.
     * Use fetchJoin() to add the fetchJoin parameter to this join.
     *
     * @param <P> The join target type
     * @param target target
     * @return the current object
     */
    <P> Q rightJoin(CollectionExpression<?, P> target);

    /**
     * Create a right join with the given target and alias.
     *
     * @param <P> The join target type
     * @param target target
     * @param alias alias
     * @return the current object
     */
    <P> Q rightJoin(CollectionExpression<?, P> target, Path<P> alias);

    /**
     * Create a right join with the given target.
     * Use fetchJoin() to add the fetchJoin parameter to this join.
     *
     * @param <P> The join target type
     * @param target target
     * @return the current object
     */
    <P> Q rightJoin(MapExpression<?, P> target);

    /**
     * Create a right join with the given target and alias.
     *
     * @param <P> The join target type
     * @param target target
     * @param alias alias
     * @return the current object
     */
    <P> Q rightJoin(MapExpression<?, P> target, Path<P> alias);

    /**
     * Add join conditions to the last added join
     *
     * @param condition join conditions
     * @return the current object
     */
    Q on(Predicate... condition);

}
