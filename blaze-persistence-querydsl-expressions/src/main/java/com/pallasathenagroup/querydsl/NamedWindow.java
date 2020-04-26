package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;

import java.util.Objects;

/**
 * A named window. Combines a {@link WindowDefinition} and an alias.
 *
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public class NamedWindow extends WindowDefinition<NamedWindow, Void> {

    private final String alias;

    /**
     * Create a new named window.
     *
     * @param alias Alias for the window
     * @since 1.0
     */
    public NamedWindow(String alias) {
        super(Void.class);
        this.alias = alias;
    }

    /**
     * Get the alias for the window
     *
     * @return the alias
     * @since 1.0
     */
    public String getAlias() {
        return alias;
    }

    @Override
    public Expression<Void> getValue() {
        return Expressions.template(super.getType(), alias);
    }

    public Expression<Void> getWindowDefinition() {
        return Expressions.operation(getType(), JPQLNextOps.WINDOW_NAME, Expressions.constant(alias), super.getValue());
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
