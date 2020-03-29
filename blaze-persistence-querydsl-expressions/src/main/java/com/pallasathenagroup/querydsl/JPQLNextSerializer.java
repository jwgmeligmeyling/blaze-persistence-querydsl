package com.pallasathenagroup.querydsl;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.support.SerializerBase;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.JPQLTemplates;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.lang.reflect.Field;

public class JPQLNextSerializer extends JPQLSerializer {

    private final StringBuilder builder;

    public JPQLNextSerializer() {
        this(JPQLNextTemplates.DEFAULT);
    }

    public JPQLNextSerializer(JPQLTemplates templates) {
        super(templates);
        this.builder = getStringBuilder(this);
    }

    public JPQLNextSerializer(JPQLTemplates templates, EntityManager em) {
        super(templates, em);
        this.builder = getStringBuilder(this);
    }

    @Override
    public void serialize(QueryMetadata metadata, boolean forCountRow, @Nullable String projection) {
        SetOperationFlag setOperationFlag = SetOperationFlag.getSetOperationFlag(metadata);
        if (setOperationFlag != null) {
            setOperationFlag.getFlag().accept(this, null);
        } else {
            super.serialize(metadata, forCountRow, projection);
        }
    }

    @Override
    public Void visit(SubQueryExpression<?> query, Void context) {
        serialize(query.getMetadata(), false, null);
        return null;
    }

    public void clearBuffer() {
        builder.setLength(0);
    }

    public String takeBuffer() {
        String res = builder.toString();
        clearBuffer();
        return res;
    }

    private static StringBuilder getStringBuilder(JPQLNextSerializer serializer) {
        try {
            Field builderField = SerializerBase.class.getDeclaredField("builder");
            builderField.setAccessible(true);
            return (StringBuilder) builderField.get(serializer);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
