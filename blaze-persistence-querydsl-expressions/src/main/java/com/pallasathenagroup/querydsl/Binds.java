package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@code FactoryExpression} for representing CTE bindings.
 *
 * @param <X> CTE expression result type
 * @author Jan-Willem Gmelig Meyling
 */
public class Binds<X> implements FactoryExpression<X> {

    private List<Operation<?>> args = new ArrayList<>();

    public <T> Binds<X> bind(Path<? super T> path, Expression<? extends T> expression) {
        args.add(JPQLNextExpressions.bind(path, expression));
        return this;
    }

    @Override
    public List<Expression<?>> getArgs() {
        return Collections.<Expression<?>> unmodifiableList(args);
    }

    @Nullable
    @Override
    public X newInstance(Object... objects) {
        throw new IllegalStateException("Instances may not be created for a CTE binds projection");
    }

    @Nullable
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, @Nullable C c) {
        return visitor.visit(this, c);
    }

    @Override
    public Class<? extends X> getType() {
        return (Class) ((Path) args.get(0).getArg(1)).getRoot().getType();
    }
}
