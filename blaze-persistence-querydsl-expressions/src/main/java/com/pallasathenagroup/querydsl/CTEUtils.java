package com.pallasathenagroup.querydsl;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CTEUtils {

    public static <T> Operation<T> bind(Path<? super T> path, Expression<? extends T> expression) {
        return ExpressionUtils.operation(expression.getType(), JPQLNextOps.BIND, expression, path);
    }

    public static class Binds<X> implements FactoryExpression<X> {

        private List<Operation<?>> args = new ArrayList<>();

        public <T> Binds<X> bind(Path<? super T> path, Expression<? extends T> expression) {
            args.add(CTEUtils.bind(path, expression));
            return this;
        }

        @Override
        public List<Expression<?>> getArgs() {
            return Collections.unmodifiableList(args);
        }

        @Nullable
        @Override
        public X newInstance(Object... objects) {
            throw new IllegalStateException("Instances may not be created for CTE");
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


}
