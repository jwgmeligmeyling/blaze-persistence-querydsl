package com.pallasathenagroup.querydsl;

import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.JPQLQuery;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public interface ExtendedJPAQuery<T, Q extends ExtendedJPAQuery<T, Q>> extends JPQLQuery<T> {

    <X> Q with(Path<X> alias, SubQueryExpression<X> o);

    <X> Q withRecursive(Path<X> alias, SubQueryExpression<X> o);

    WithBuilder<Q> with(EntityPath<?> alias, Path<?>... columns);

    WithBuilder<Q> withRecursive(EntityPath<?> alias, Path<?>... columns);

    <X> Q fromValues(EntityPath<X> path, Collection<X> elements);

    <X> Q fromIdentifiableValues(EntityPath<X> path, Collection<X> elements);

    <P> Q fullJoin(CollectionExpression<?,P> target);

    <P> Q fullJoin(CollectionExpression<?,P>target, Path<P> alias);

    <P> Q fullJoin(EntityPath<P> target);

    <P> Q fullJoin(EntityPath<P> target, Path<P> alias);

    <P> Q fullJoin(MapExpression<?,P> target);

    <P> Q fullJoin(MapExpression<?,P> target, Path<P> alias);

    <X> Q from(SubQueryExpression<X> subQueryExpression, Path<X> alias);

    /**
     * Adds a left join to the given target
     *
     * @param o subquery
     * @param alias alias
     * @return the current object
     */
    <X> Q leftJoin(SubQueryExpression<X> o, Path<X> alias);

    /**
     * Adds a right join to the given target
     *
     * @param o subquery
     * @param alias alias
     * @return the current object
     */
    <X> Q rightJoin(SubQueryExpression<X> o, Path<X> alias);

    /**
     * Adds a full join to the given target
     *
     * @param o subquery
     * @param alias alias
     * @return the current object
     */
    <X> Q fullJoin(SubQueryExpression<X> o, Path<X> alias);

    /**
     * Adds a inner join to the given target
     *
     * @param o subquery
     * @param alias alias
     * @return the current object
     */
    <X> Q innerJoin(SubQueryExpression<X> o, Path<X> alias);

    /**
     * Creates an union expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    <RT> SetExpression<RT> union(List<SubQueryExpression<RT>> sq);


    /**
     * Creates an union expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    <RT> SetExpression<RT> unionAll(List<SubQueryExpression<RT>> sq);
    /**
     * Creates an intersect expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    <RT> SetExpression<RT> intersect(List<SubQueryExpression<RT>> sq);


    /**
     * Creates an intersect expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    <RT> SetExpression<RT> intersectAll(List<SubQueryExpression<RT>> sq);

    /**
     * Creates an except expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    <RT> SetExpression<RT> except(List<SubQueryExpression<RT>> sq);

    /**
     * Creates an except expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    <RT> SetExpression<RT> exceptAll(List<SubQueryExpression<RT>> sq);

    /**
     * Mark the last join as a lateral join
     * @return this query
     */
    Q lateral();


    // Covariant Overrides

    @Override
    Q from(EntityPath<?>... sources);

    @Override
    <P> Q from(CollectionExpression<?,P> target, Path<P> alias);

    @Override
    <P> Q innerJoin(EntityPath<P> target);

    @Override
    <P> Q innerJoin(EntityPath<P> target, Path<P> alias);

    @Override
    <P> Q innerJoin(CollectionExpression<?, P> target);

    @Override
    <P> Q innerJoin(CollectionExpression<?,P> target, Path<P> alias);

    @Override
    <P> Q innerJoin(MapExpression<?, P> target);

    @Override
    <P> Q innerJoin(MapExpression<?, P> target, Path<P> alias);

    @Override
    <P> Q join(EntityPath<P> target);

    @Override
    <P> Q join(EntityPath<P> target, Path<P> alias);

    @Override
    <P> Q join(CollectionExpression<?,P> target);

    @Override
    <P> Q join(CollectionExpression<?,P> target, Path<P> alias);

    @Override
    <P> Q join(MapExpression<?, P> target);

    @Override
    <P> Q join(MapExpression<?, P> target, Path<P> alias);

    @Override
    <P> Q leftJoin(EntityPath<P> target);

    @Override
    <P> Q leftJoin(EntityPath<P> target, Path<P> alias);

    @Override
    <P> Q leftJoin(CollectionExpression<?,P> target);

    @Override
    <P> Q leftJoin(CollectionExpression<?,P> target, Path<P> alias);

    @Override
    <P> Q leftJoin(MapExpression<?, P> target);

    @Override
    <P> Q leftJoin(MapExpression<?, P> target, Path<P> alias);

    @Override
    <P> Q rightJoin(EntityPath<P> target);

    @Override
    <P> Q rightJoin(EntityPath<P> target, Path<P> alias);

    @Override
    <P> Q rightJoin(CollectionExpression<?,P> target);

    @Override
    <P> Q rightJoin(CollectionExpression<?,P> target, Path<P> alias);

    @Override
    <P> Q rightJoin(MapExpression<?, P> target);

    @Override
    <P> Q rightJoin(MapExpression<?, P> target, Path<P> alias);

    @Override
    Q on(Predicate... condition);

    @Override
    Q fetchJoin();

    @Override
    Q fetchAll();

    @Override
    Q groupBy(Expression<?>... expressions);

    @Override
    Q having(Predicate... predicates);

    @Override
    Q limit(long l);

    @Override
    Q offset(long l);

    @Override
    Q restrict(QueryModifiers queryModifiers);

    @Override
    Q orderBy(OrderSpecifier<?>... orderSpecifiers);

    @Override
    <T> Q set(ParamExpression<T> paramExpression, T t);

    @Override
    Q distinct();

    @Override
    Q where(Predicate... predicates);
}
