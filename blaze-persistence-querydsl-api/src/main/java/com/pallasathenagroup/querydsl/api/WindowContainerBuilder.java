package com.pallasathenagroup.querydsl.api;

import com.pallasathenagroup.querydsl.NamedWindow;

/**
 * A base interface for builders that support adding named windows for analytics functions.
 *
 * @param <X> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface WindowContainerBuilder<X extends WindowContainerBuilder<X>> {

    /**
     * Add a named window to this query.
     *
     * @param window The window definition to add
     * @return this query
     */
    X window(NamedWindow window);

}
