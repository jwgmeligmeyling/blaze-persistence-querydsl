package com.pallasathenagroup.querydsl;

import com.pallasathenagroup.querydsl.experimental.BindBuilder;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.JPQLQuery;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public interface ExtendedJPAQuery<T, Q extends ExtendedJPAQuery<T, Q>> extends JPQLQuery<T> {

    WithBuilder<Q> with(EntityPath<?> alias, Path<?>... columns);

    WithBuilder<Q> withRecursive(EntityPath<?> alias, Path<?>... columns);

    @SuppressWarnings("unchecked")
    default BindBuilder<T, Q> with(EntityPath<?> alias) {
        return new BindBuilder<>((Q) this, alias, false);
    }

    @SuppressWarnings("unchecked")
    default BindBuilder<T, Q> withRecursive(EntityPath<?> alias) {
        return new BindBuilder<>((Q) this, alias, true);
    }

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
    <X> Q rightJoin(SubQueryExpression<X> o, Path<X> alias);
    <X> Q fullJoin(SubQueryExpression<X> o, Path<X> alias);
    <X> Q innerJoin(SubQueryExpression<X> o, Path<X> alias);

    /**
     * Creates an union expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    @SuppressWarnings("unchecked")
    default <RT> SetOperation<RT> union(SubQueryExpression<RT>... sq) {
        return union(Arrays.asList(sq));
    }
    
    /**
     * Creates an union expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    <RT> SetOperation<RT> union(List<SubQueryExpression<RT>> sq);
    
    /**
     * Creates an union expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    @SuppressWarnings({"unchecked"})
    default <RT> SetOperation<RT> unionAll(SubQueryExpression<RT>... sq) {
        return unionAll(Arrays.asList(sq));
    }
    
    /**
     * Creates an union expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    <RT> SetOperation<RT> unionAll(List<SubQueryExpression<RT>> sq);
    
    /**
     * Creates an intersect expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    @SuppressWarnings({"unchecked"})
    default <RT> SetOperation<RT> intersect(SubQueryExpression<RT>... sq) {
        return intersect(Arrays.asList(sq));
    }

    /**
     * Creates an intersect expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    <RT> SetOperation<RT> intersect(List<SubQueryExpression<RT>> sq);
    
    /**
     * Creates an intersect expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    @SuppressWarnings({"unchecked"})
    default <RT> SetOperation<RT> intersectAll(SubQueryExpression<RT>... sq) {
        return intersectAll(Arrays.asList(sq));
    }
    
    /**
     * Creates an intersect expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    <RT> SetOperation<RT> intersectAll(List<SubQueryExpression<RT>> sq);
    
    /**
     * Creates an except expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    @SuppressWarnings({"unchecked"})
    default <RT> SetOperation<RT> except(SubQueryExpression<RT>... sq) {
        return except(Arrays.asList(sq));
    }
        
    /**
     * Creates an except expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    <RT> SetOperation<RT> except(List<SubQueryExpression<RT>> sq);
    
    /**
     * Creates an except expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    @SuppressWarnings({"unchecked"})
    default <RT> SetOperation<RT> exceptAll(SubQueryExpression<RT>... sq) {
        return exceptAll(Arrays.asList(sq));
    }
    
    /**
     * Creates an except expression for the given subqueries
     *
     * @param <RT> union subquery result
     * @param sq subqueries
     * @return union
     */
    <RT> SetOperation<RT> exceptAll(List<SubQueryExpression<RT>> sq);

    Q union();


}
