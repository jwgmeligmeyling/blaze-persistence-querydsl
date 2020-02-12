package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.Visitor;

import javax.annotation.Nullable;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

public class ValuesExpression<T> implements Path<T> {

    private final Path<T> entityPath;
    private final Collection<T> elements;
    private final boolean identifiable;

    public ValuesExpression(Path<T> entityPath, Collection<T> elements, boolean identifiable) {
        this.entityPath = entityPath;
        this.elements = elements;
        this.identifiable = identifiable;
    }

    @Override
    public PathMetadata getMetadata() {
        return entityPath.getMetadata();
    }

    @Override
    public Path<?> getRoot() {
        return entityPath.getRoot();
    }

    @Override
    public AnnotatedElement getAnnotatedElement() {
        return entityPath.getAnnotatedElement();
    }

    @Override
    @Nullable
    public <R, C> R accept(Visitor<R, C> v, @Nullable C context) {
        return entityPath.accept(v, context);
    }

    @Override
    public Class<? extends T> getType() {
        return entityPath.getType();
    }

    public Collection<T> getElements() {
        return elements;
    }

    public boolean isIdentifiable() {
        return identifiable;
    }

}
