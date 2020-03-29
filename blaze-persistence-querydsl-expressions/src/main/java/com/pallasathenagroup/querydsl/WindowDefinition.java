package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.parser.expression.WindowFrameMode;
import com.blazebit.persistence.parser.expression.WindowFramePositionType;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.MutableExpressionBase;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.blazebit.persistence.parser.expression.WindowFrameMode.GROUPS;
import static com.blazebit.persistence.parser.expression.WindowFrameMode.RANGE;
import static com.blazebit.persistence.parser.expression.WindowFrameMode.ROWS;

public class WindowDefinition<Q extends WindowDefinition<Q, ?>, T> extends MutableExpressionBase<T> {

    private static final String ORDER_BY = "order by ";

    private static final String PARTITION_BY = "partition by ";

    private static final long serialVersionUID = -4130672293308756779L;

    private final List<OrderSpecifier<?>> orderBy = new ArrayList<OrderSpecifier<?>>();

    private final List<Expression<?>> partitionBy = new ArrayList<Expression<?>>();

    @Nullable
    private transient volatile SimpleExpression<T> value;

    private String baseWindowName;
    private WindowFrameMode frameMode;
    private WindowFramePositionType frameStartType;
    private Expression<?> frameStartExpression;
    private WindowFramePositionType frameEndType;
    private Expression<?> frameEndExpression;

    public List<OrderSpecifier<?>> getOrderBy() {
        return orderBy;
    }

    public List<Expression<?>> getPartitionBy() {
        return partitionBy;
    }


    public WindowDefinition(Class<? extends T> clasz) {
        super(clasz);
    }

    public WindowDefinition(Class<? extends T> clasz, String baseWindowName) {
        this(clasz);
        this.baseWindowName = baseWindowName;
    }

    @Nullable
    @Override
    public Object accept(Visitor v, @Nullable Object context) {
        return getValue().accept(v, context);
    }

    public Expression<T> getValue() {
        if (value == null) {
            int size = 0;
            ImmutableList.Builder<Expression<?>> args = ImmutableList.builder();
            StringBuilder builder = new StringBuilder();

            if (baseWindowName != null) {
                builder.append(baseWindowName).append(" ");
            }

            if (!partitionBy.isEmpty()) {
                builder.append(PARTITION_BY);
                boolean first = true;


                for (Expression<?> expr : partitionBy) {
                    if (!first) {
                        builder.append(", ");
                    }
                    builder.append("{").append(size).append("}");
                    args.add(expr);
                    size++;
                    first = false;
                }

            }
            if (!orderBy.isEmpty()) {
                if (!partitionBy.isEmpty()) {
                    builder.append(" ");
                }
                builder.append(ORDER_BY);
                builder.append("{").append(size).append("}");
                args.add(ExpressionUtils.orderBy(orderBy));
                size++;
            }


            if (frameStartType != null) {
                if (frameEndType != null) {
                    builder.append(" between ");

                    switch (frameStartType) {
                        case UNBOUNDED_PRECEDING:
                        case CURRENT_ROW:
                        case UNBOUNDED_FOLLOWING:
                            builder.append(frameStartType.toString().replace("_", " ").toLowerCase());
                            break;
                        case BOUNDED_PRECEDING:
                            builder.append("preceding {").append(size).append("}");
                            args.add(frameStartExpression);
                            size++;
                            break;
                        case BOUNDED_FOLLOWING:
                            builder.append("following {").append(size).append("}");
                            args.add(frameStartExpression);

                            size++;
                            break;
                    }

                    builder.append(" and ");


                    switch (frameEndType) {
                        case UNBOUNDED_PRECEDING:
                        case CURRENT_ROW:
                        case UNBOUNDED_FOLLOWING:
                            builder.append(frameEndType.toString().replace("_", " ").toLowerCase());
                            break;
                        case BOUNDED_PRECEDING:
                            builder.append("preceding {").append(size).append("}");
                            args.add(frameEndExpression);
                            size++;
                            break;
                        case BOUNDED_FOLLOWING:
                            builder.append("following {").append(size).append("}");
                            args.add(frameEndExpression);
                            size++;
                            break;
                    }
                } else {
                    switch (frameStartType) {
                        case UNBOUNDED_PRECEDING:
                        case CURRENT_ROW:
                        case UNBOUNDED_FOLLOWING:
                            builder.append(frameStartType.toString().replace("_", " ").toLowerCase());
                            break;
                        case BOUNDED_PRECEDING:
                            builder.append("preceding {").append(size).append("}");
                            args.add(frameStartExpression);
                            size++;
                            break;
                        case BOUNDED_FOLLOWING:
                            builder.append("following {").append(size).append("}");
                            args.add(frameStartExpression);

                            size++;
                            break;
                    }
                }
            }

            value = Expressions.template(getType(), builder.toString(), (List<Expression<?>>) args.build());
        }
        return value;
    }

    public Q orderBy(ComparableExpressionBase<?> orderBy) {
        value = null;
        this.orderBy.add(orderBy.asc());
        return (Q) this;
    }

    public Q orderBy(ComparableExpressionBase<?>... orderBy) {
        value = null;
        for (ComparableExpressionBase<?> e : orderBy) {
            this.orderBy.add(e.asc());
        }
        return (Q) this;
    }

    public Q  orderBy(OrderSpecifier<?> orderBy) {
        value = null;
        this.orderBy.add(orderBy);
        return (Q) this;
    }

    public Q orderBy(OrderSpecifier<?>... orderBy) {
        value = null;
        Collections.addAll(this.orderBy, orderBy);
        return (Q) this;
    }

    public Q partitionBy(Expression<?> partitionBy) {
        value = null;
        this.partitionBy.add(partitionBy);
        return (Q) this;
    }

    public Q partitionBy(Expression<?>... partitionBy) {
        value = null;
        Collections.addAll(this.partitionBy, partitionBy);
        return (Q) this;
    }

    Q withFrame(WindowFrameMode frame, WindowFramePositionType frameStartType, Expression<?> frameStartExpression, WindowFramePositionType frameEndType, Expression<?> frameEndExpression) {
        this.frameMode = frame;
        this.frameStartType = frameStartType;
        this.frameStartExpression = frameStartExpression;
        this.frameEndType = frameEndType;
        this.frameEndExpression = frameEndExpression;
        return (Q) this;
    }

    public String getBaseWindowName() {
        return baseWindowName;
    }

    public WindowFrameMode getFrameMode() {
        return frameMode;
    }

    public WindowFramePositionType getFrameStartType() {
        return frameStartType;
    }

    public Expression<?> getFrameStartExpression() {
        return frameStartExpression;
    }

    public WindowFramePositionType getFrameEndType() {
        return frameEndType;
    }

    public Expression<?> getFrameEndExpression() {
        return frameEndExpression;
    }

    public WindowRows<Q> rows() {
        value = null;
        int offset = orderBy.size() + partitionBy.size() + 1;
        return new WindowRows<Q>((Q) this, ROWS);
    }

    public WindowRows<Q> range() {
        value = null;
        int offset = orderBy.size() + partitionBy.size() + 1;
        return new WindowRows<Q>((Q) this, RANGE);
    }

    public WindowRows<Q> groups() {
        value = null;
        int offset = orderBy.size() + partitionBy.size() + 1;
        return new WindowRows<Q>((Q) this, GROUPS);
    }

}
