package com.pallasathenagroup.querydsl.experimental;

import com.pallasathenagroup.querydsl.AbstractBlazeJPAQuery;
import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.ExtendedJPAQuery;
import com.pallasathenagroup.querydsl.SetUtils;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLTemplates;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.List;

public class BindBuilder<T, Q extends ExtendedJPAQuery<T, Q>>
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

    @Override
    public com.pallasathenagroup.querydsl.experimental.BindBuilder<T, Q> union() {
        BindBuilder<T,Q> bindBuilder = new com.pallasathenagroup.querydsl.experimental.BindBuilder<>(mainQuery, alias, recursive);
        bindBuilder.union = SetUtils.union(this, bindBuilder);
        return bindBuilder;
    }

    public Q end() {
        Path<?>[] paths = binds.stream().map(Bind::getPath).toArray(Path[]::new);
        return (recursive ? mainQuery.withRecursive(alias, paths) : mainQuery.with(alias, paths)).as(this);
    }

    @Override
    public com.pallasathenagroup.querydsl.experimental.BindBuilder<T, Q> clone(EntityManager entityManager) {
        throw new UnsupportedOperationException();
    }

    @Override
    public com.pallasathenagroup.querydsl.experimental.BindBuilder<T, Q> clone(EntityManager entityManager, JPQLTemplates templates) {
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

        public com.pallasathenagroup.querydsl.experimental.BindBuilder<T, Q> select(Expression<A> expression) {
            binds.add(new Bind<>(path, expression));
            queryMixin.setProjection(binds.stream().map(Bind::getExpression).toArray(Expression[]::new));
            return com.pallasathenagroup.querydsl.experimental.BindBuilder.this;
        }

    }

}
