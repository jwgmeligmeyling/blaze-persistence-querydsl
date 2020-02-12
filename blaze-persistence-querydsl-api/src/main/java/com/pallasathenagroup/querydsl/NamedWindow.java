package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;

import java.util.Objects;

public class NamedWindow extends WindowDefinition<NamedWindow, Void> {

    private final String alias;

    public NamedWindow(String alias) {
        super(Void.class);
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public Expression<Void> getValue() {
        return Expressions.template(super.getType(), "WINDOW " + alias + " AS ({0})", super.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedWindow)) return false;
        if (!super.equals(o)) return false;
        NamedWindow that = (NamedWindow) o;
        return Objects.equals(alias, that.alias) && super.equals(o);
    }

}
