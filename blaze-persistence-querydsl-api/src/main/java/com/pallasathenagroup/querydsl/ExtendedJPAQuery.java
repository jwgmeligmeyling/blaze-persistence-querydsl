package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.AbstractJPAQuery;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

interface ExtendedJPAQuery<T, Q extends ExtendedJPAQuery<T, Q>> extends JPQLQuery<T> {

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

    class BindBuilder<T, Q extends ExtendedJPAQuery<T, Q>>
            extends AbstractBlazeJPAQuery<Tuple, BindBuilder<T, Q>> {

        private final Q mainQuery;
        private final BlazeJPAQuery<Tuple> subQuery;
        private final EntityPath<?> alias;
        private final boolean recursive;
        private final List<Bind<?>> binds = new ArrayList<>();

        public BindBuilder(Q mainQuery, EntityPath<?> alias, boolean recursive) {
            super(null);
            this.mainQuery = mainQuery;
            this.subQuery = new BlazeJPAQuery<>(null);
            this.alias = alias;
            this.recursive = recursive;
        }

        public <A> SelectBuilder<A> bind(Path<A> path) {
            return new SelectBuilder<>(path);
        }

        public Q end() {
            Path<?>[] paths = binds.stream().map(Bind::getPath).toArray(Path[]::new);
            return (recursive ? mainQuery.withRecursive(alias, paths) : mainQuery.with(alias, paths)).as(this);
        }

        @Override
        public BindBuilder<T, Q> clone(EntityManager entityManager) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BindBuilder<T, Q> clone(EntityManager entityManager, JPQLTemplates templates) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <U> JPQLQuery<U> select(Expression<U> expr) {
            throw new UnsupportedOperationException();
        }

        @Override
        public JPQLQuery<com.querydsl.core.Tuple> select(Expression<?>... exprs) {
            throw new UnsupportedOperationException();
        }

        public class SelectBuilder<A> {

            private final Path<A> path;

            public SelectBuilder(Path<A> path) {
                this.path = path;
            }

            public BindBuilder<T, Q> select(Expression<A> expression) {
                binds.add(new Bind<>(path, expression));
                queryMixin.setProjection(binds.stream().map(Bind::getExpression).toArray(Expression[]::new));
                return BindBuilder.this;
            }

        }

    }


    class Bind<T> {
        private final Path<T> path;
        private final Expression<T> expression;

        public Bind(Path<T> path, Expression<T> expression) {
            this.path = path;
            this.expression = expression;
        }

        public Path<T> getPath() {
            return path;
        }

        public Expression<T> getExpression() {
            return expression;
        }
    }

}
