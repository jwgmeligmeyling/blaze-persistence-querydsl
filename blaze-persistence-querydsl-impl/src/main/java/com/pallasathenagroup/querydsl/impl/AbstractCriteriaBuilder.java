package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.NamedWindow;
import com.pallasathenagroup.querydsl.api.BaseCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingSetOperationCriteriaBuilder;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.QTuple;
import com.querydsl.core.types.SubQueryExpression;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractCriteriaBuilder<T, Q extends BaseCriteriaBuilder<T, Q>> extends AbstractCommonQueryBuilder<Q> {

    protected final BlazeJPAQuery<T> blazeJPAQuery;
    protected final Q self = (Q) this;

    public AbstractCriteriaBuilder(BlazeJPAQuery<T> blazeJPAQuery) {
        this.blazeJPAQuery = blazeJPAQuery;
    }

    public Q distinct() {
        blazeJPAQuery.distinct();
        return self;
    }

    public Q groupBy(Expression<?>... expressions) {
        blazeJPAQuery.groupBy(expressions);
        return self;
    }

    public Q having(Predicate... predicates) {
        blazeJPAQuery.having(predicates);
        return self;
    }

    public Q setFirstResult(int i) {
        blazeJPAQuery.offset(i);
        return self;
    }

    public Q setMaxResults(int i) {
        blazeJPAQuery.limit(i);
        return self;
    }

    public int getFirstResult() {
        return blazeJPAQuery.getMetadata().getModifiers().getOffsetAsInteger();
    }

    public int getMaxResults() {
        return blazeJPAQuery.getMetadata().getModifiers().getLimitAsInteger();
    }

    public Class<T> getResultType() {
        return blazeJPAQuery.getType();
    }

    public <X> Q fromValues(EntityPath<X> path, Collection<X> elements) {
        blazeJPAQuery.fromValues(path, elements);
        return self;
    }

    public <X> Q fromIdentifiableValues(EntityPath<X> path, Collection<X> elements) {
        blazeJPAQuery.fromIdentifiableValues(path, elements);
        return self;
    }

    public <X> Q fromValues(Path<X> path, Path<X> alias, Collection<X> elements) {
        blazeJPAQuery.fromValues(path, alias, elements);
        return self;
    }

    public <X> Q fromIdentifiableValues(Path<X> path, Path<X> alias, Collection<X> elements) {
        blazeJPAQuery.fromIdentifiableValues(path, alias, elements);
        return self;
    }


    public <P> Q fullJoin(CollectionExpression<?, P> target) {
        blazeJPAQuery.fullJoin(target);
        return self;
    }

    public <P> Q fullJoin(CollectionExpression<?, P> target, Path<P> alias) {
        blazeJPAQuery.fullJoin(target, alias);
        return self;
    }

    public <P> Q fullJoin(EntityPath<P> target) {
        blazeJPAQuery.fullJoin(target);
        return self;
    }

    public <P> Q fullJoin(EntityPath<P> target, Path<P> alias) {
        blazeJPAQuery.fullJoin(target, alias);
        return self;
    }

    public <P> Q fullJoin(MapExpression<?, P> target) {
        blazeJPAQuery.fullJoin(target);
        return self;
    }

    public <P> Q fullJoin(MapExpression<?, P> target, Path<P> alias) {
        blazeJPAQuery.fullJoin(target, alias);
        return self;
    }

    public <X> Q from(SubQueryExpression<X> subQueryExpression, Path<X> alias) {
        blazeJPAQuery.from(subQueryExpression, alias);
        return self;
    }

    public <X> Q leftJoin(SubQueryExpression<X> o, Path<X> alias) {
        blazeJPAQuery.leftJoin(o, alias);
        return self;
    }

    public <X> Q rightJoin(SubQueryExpression<X> o, Path<X> alias) {
        blazeJPAQuery.rightJoin(o, alias);
        return self;
    }

    public <X> Q fullJoin(SubQueryExpression<X> o, Path<X> alias) {
        blazeJPAQuery.fullJoin(o, alias);
        return self;
    }

    public <X> Q innerJoin(SubQueryExpression<X> o, Path<X> alias) {
        blazeJPAQuery.innerJoin(o, alias);
        return self;
    }

    public Q lateral() {
        blazeJPAQuery.lateral();
        return self;
    }

    public Q from(EntityPath<?>... sources) {
        blazeJPAQuery.from(sources);
        return self;
    }

    public <P> Q from(CollectionExpression<?, P> target, Path<P> alias) {
        blazeJPAQuery.from(target, alias);
        return self;
    }

    public <P> Q innerJoin(EntityPath<P> target) {
        blazeJPAQuery.innerJoin(target);
        return self;
    }

    public <P> Q innerJoin(EntityPath<P> target, Path<P> alias) {
        blazeJPAQuery.innerJoin(target, alias);
        return self;
    }

    public <P> Q innerJoin(CollectionExpression<?, P> target) {
        blazeJPAQuery.innerJoin(target);
        return self;
    }

    public <P> Q innerJoin(CollectionExpression<?, P> target, Path<P> alias) {
        blazeJPAQuery.innerJoin(target, alias);
        return self;
    }

    public <P> Q innerJoin(MapExpression<?, P> target) {
        blazeJPAQuery.innerJoin(target);
        return self;
    }

    public <P> Q innerJoin(MapExpression<?, P> target, Path<P> alias) {
        blazeJPAQuery.innerJoin(target, alias);
        return self;
    }

    public <P> Q join(EntityPath<P> target) {
        blazeJPAQuery.join(target);
        return self;
    }

    public <P> Q join(EntityPath<P> target, Path<P> alias) {
        blazeJPAQuery.join(target, alias);
        return self;
    }

    public <P> Q join(CollectionExpression<?, P> target) {
        blazeJPAQuery.join(target);
        return self;
    }

    public <P> Q join(CollectionExpression<?, P> target, Path<P> alias) {
        blazeJPAQuery.join(target, alias);
        return self;
    }

    public <P> Q join(MapExpression<?, P> target) {
        blazeJPAQuery.join(target);
        return self;
    }

    public <P> Q join(MapExpression<?, P> target, Path<P> alias) {
        blazeJPAQuery.join(target, alias);
        return self;
    }

    public <P> Q leftJoin(EntityPath<P> target) {
        blazeJPAQuery.leftJoin(target);
        return self;
    }

    public <P> Q leftJoin(EntityPath<P> target, Path<P> alias) {
        blazeJPAQuery.leftJoin(target, alias);
        return self;
    }

    public <P> Q leftJoin(CollectionExpression<?, P> target) {
        blazeJPAQuery.leftJoin(target);
        return self;
    }

    public <P> Q leftJoin(CollectionExpression<?, P> target, Path<P> alias) {
        blazeJPAQuery.leftJoin(target, alias);
        return self;
    }

    public <P> Q leftJoin(MapExpression<?, P> target) {
        blazeJPAQuery.leftJoin(target);
        return self;
    }

    public <P> Q leftJoin(MapExpression<?, P> target, Path<P> alias) {
        blazeJPAQuery.leftJoin(target, alias);
        return self;
    }

    public <P> Q rightJoin(EntityPath<P> target) {
        blazeJPAQuery.rightJoin(target);
        return self;
    }

    public <P> Q rightJoin(EntityPath<P> target, Path<P> alias) {
        blazeJPAQuery.rightJoin(target, alias);
        return self;
    }

    public <P> Q rightJoin(CollectionExpression<?, P> target) {
        blazeJPAQuery.rightJoin(target);
        return self;
    }

    public <P> Q rightJoin(CollectionExpression<?, P> target, Path<P> alias) {
        blazeJPAQuery.rightJoin(target, alias);
        return self;
    }

    public <P> Q rightJoin(MapExpression<?, P> target) {
        blazeJPAQuery.rightJoin(target);
        return self;
    }

    public <P> Q rightJoin(MapExpression<?, P> target, Path<P> alias) {
        blazeJPAQuery.rightJoin(target, alias);
        return self;
    }

    public Q on(Predicate... condition) {
        blazeJPAQuery.on(condition);
        return self;
    }

    public Q fetchJoin() {
        blazeJPAQuery.fetchJoin();
        return self;
    }

    public Q fetchAll() {
        blazeJPAQuery.fetchAll();
        return self;
    }

    public Q window(NamedWindow window) {
        blazeJPAQuery.window(window);
        return self;
    }


    public Q orderBy(OrderSpecifier<?>... orderSpecifiers) {
        blazeJPAQuery.orderBy(orderSpecifiers);
        return self;
    }


    public <U> Q set(ParamExpression<U> paramExpression, U u) {
        blazeJPAQuery.set(paramExpression, u);
        return self;
    }


    public String getQueryString() {
        return blazeJPAQuery.getQueryString();
    }


    public TypedQuery<T> getQuery() {
        throw new UnsupportedOperationException();
    }


    public List<T> getResultList() {
        return blazeJPAQuery.fetch();
    }


    public T getSingleResult() {
        return blazeJPAQuery.fetchOne();
    }

    public Q where(Predicate... predicates) {
        blazeJPAQuery.where(predicates);
        return self;
    }

    public Q select(Expression<?> expression) {
        Expression<?> projection = blazeJPAQuery.getMetadata().getProjection();
        if (projection != null) {
            List<Expression<?>> expressions = new ArrayList<>();
            if (projection instanceof QTuple) {
                expressions.addAll(((QTuple) projection).getArgs());
            } else {
                expressions.add(projection);
            }
            expressions.add(expression);
            blazeJPAQuery.select(expressions.toArray(new Expression[0]));
        } else {
            blazeJPAQuery.select(expression);
        }
        return self;
    }
}
