package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.parser.expression.WindowFrameMode;
import com.blazebit.persistence.parser.expression.WindowFramePositionType;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.MutableExpressionBase;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimpleOperation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.blazebit.persistence.parser.expression.WindowFrameMode.GROUPS;
import static com.blazebit.persistence.parser.expression.WindowFrameMode.RANGE;
import static com.blazebit.persistence.parser.expression.WindowFrameMode.ROWS;

/**
 * A base class for window definition expressions.
 *
 * @param <Q> Concrete window type
 * @param <T> Expression result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public class WindowDefinition<Q extends WindowDefinition<Q, ?>, T> extends MutableExpressionBase<T> {

    private static final String ORDER_BY = "order by ";

    private static final String PARTITION_BY = "partition by ";

    private static final long serialVersionUID = -4130672293308756779L;

    private final List<OrderSpecifier<?>> orderBy = new ArrayList<OrderSpecifier<?>>();

    private final List<Expression<?>> partitionBy = new ArrayList<Expression<?>>();

    @Nullable
    private transient volatile SimpleExpression value;

    private String baseWindowName;
    private WindowFrameMode frameMode;
    private WindowFramePositionType frameStartType;
    private Expression<?> frameStartExpression;
    private WindowFramePositionType frameEndType;
    private Expression<?> frameEndExpression;

    /**
     * Get the OrderSpecifiers
     *
     * @return order by
     * @since 1.0
     */
    public List<OrderSpecifier<?>> getOrderBy() {
        return orderBy;
    }

    /**
     * Get the partition specifiers
     *
     * @return partition by
     * @since 1.0
     */
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

    /**
     * Construct a template expression for this Window Definition.
     *
     * @return template expression
     * @since 1.0
     */
    public Expression<T> getValue() {
        if (value == null) {
            Optional<Expression> baseWindow = Optional.ofNullable(baseWindowName)
                    .map(name -> Expressions.operation(Object.class, JPQLNextOps.WINDOW_BASE, Expressions.constant(baseWindowName)));

            Optional<SimpleOperation<Object>> partitionByOperation = partitionBy.stream()
                    .reduce((a, b) -> Expressions.operation(a.getType(), Ops.LIST, a, b))
                    .map(a -> Expressions.operation(Object.class, JPQLNextOps.WINDOW_PARTITION_BY, a));

            Optional<SimpleOperation<Object>> orderByOperation = orderBy.stream()
                    .map(orderSpecifier -> (Expression) Expressions.template(
                            Object.class,
                            "{0} {1s} {2s}",
                            orderSpecifier.getTarget(),
                            orderSpecifier.getOrder(),
                            orderSpecifier.getNullHandling() != null && orderSpecifier.getNullHandling() != OrderSpecifier.NullHandling.Default ? orderSpecifier.getNullHandling() == OrderSpecifier.NullHandling.NullsFirst ? "NULLS FIRST" : "NULLS LAST" : ""
                    ))
                    .reduce((a, b) -> Expressions.operation(Object.class, Ops.LIST, a, b))
                    .map(a -> Expressions.operation(Object.class, JPQLNextOps.WINDOW_ORDER_BY, a));

            Optional<Operation> frameClauseOperation;

            if (frameMode != null) {
                Operator frameOperator;
                switch (frameMode) {
                    case RANGE:
                        frameOperator = JPQLNextOps.WINDOW_RANGE;
                        break;
                    case ROWS:
                        frameOperator = JPQLNextOps.WINDOW_ROWS;
                        break;
                    case GROUPS:
                        frameOperator = JPQLNextOps.WINDOW_GROUPS;
                        break;
                    default: throw new UnsupportedOperationException();
                }

                Operation rangeClause;
                Operator frameStartOperator = getOperatorForWindowFramePositionType(this.frameStartType);
                Operation frameStart = rangeClause = frameStartExpression != null ?
                        Expressions.operation(Object.class, frameStartOperator, frameStartExpression) :
                        Expressions.operation(Object.class, frameStartOperator);

                if (frameEndType != null) {
                    Operator frameEndOperator = getOperatorForWindowFramePositionType(this.frameEndType);
                    Operation frameEnd = frameEndExpression != null ?
                            Expressions.operation(Object.class, frameEndOperator, frameEndExpression) :
                            Expressions.operation(Object.class, frameEndOperator);

                    rangeClause = Expressions.operation(Object.class, JPQLNextOps.WINDOW_BETWEEN, frameStart, frameEnd);
                }

                frameClauseOperation = Optional.of(Expressions.operation(Object.class, frameOperator, rangeClause));
            } else {
                frameClauseOperation = Optional.empty();
            }

            List<? extends Expression> arguments = Stream.of(baseWindow, partitionByOperation, orderByOperation, frameClauseOperation)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            Operator windowOperator;

            switch (arguments.size()) {
                case 1: windowOperator = JPQLNextOps.WINDOW_DEFINITION_1; break;
                case 2: windowOperator = JPQLNextOps.WINDOW_DEFINITION_2; break;
                case 3: windowOperator = JPQLNextOps.WINDOW_DEFINITION_3; break;
                case 4: windowOperator = JPQLNextOps.WINDOW_DEFINITION_4; break;
                default: throw new UnsupportedOperationException();
            }

            value = Expressions.operation(Object.class, windowOperator, arguments.toArray(new Expression[0]));
        }
        return value;
    }

    private Operator getOperatorForWindowFramePositionType(WindowFramePositionType frameStartType) {
        Operator frameStartOperator = null;
        switch (frameStartType) {
            case UNBOUNDED_PRECEDING:
                frameStartOperator = JPQLNextOps.WINDOW_UNBOUNDED_PRECEDING;
                break;
            case BOUNDED_PRECEDING:
                frameStartOperator = JPQLNextOps.WINDOW_PRECEDING;
                break;
            case CURRENT_ROW:
                frameStartOperator = JPQLNextOps.WINDOW_CURRENT_ROW;
                break;
            case UNBOUNDED_FOLLOWING:
                frameStartOperator = JPQLNextOps.WINDOW_UNBOUNDED_FOLLOWING;
                break;
            case BOUNDED_FOLLOWING:
                frameStartOperator = JPQLNextOps.WINDOW_FOLLOWING;
                break;
        }
        return frameStartOperator;
    }

    /**
     * Add an order by clause to this window definition.
     *
     * @param orderBy order by expression
     * @return this window definition
     * @since 1.0
     */
    public Q orderBy(ComparableExpressionBase<?> orderBy) {
        value = null;
        this.orderBy.add(orderBy.asc());
        return (Q) this;
    }

    /**
     * Add an order by clause to this window definition.
     *
     * @param orderBy order by expressions
     * @return this window definition
     * @since 1.0
     */
    public Q orderBy(ComparableExpressionBase<?>... orderBy) {
        value = null;
        for (ComparableExpressionBase<?> e : orderBy) {
            this.orderBy.add(e.asc());
        }
        return (Q) this;
    }

    /**
     * Add an order by clause to this window definition.
     *
     * @param orderBy order by expression
     * @return this window definition
     * @since 1.0
     */
    public Q  orderBy(OrderSpecifier<?> orderBy) {
        value = null;
        this.orderBy.add(orderBy);
        return (Q) this;
    }

    /**
     * Add an order by clause to this window definition.
     *
     * @param orderBy order by expressions
     * @return this window definition
     * @since 1.0
     */
    public Q orderBy(OrderSpecifier<?>... orderBy) {
        value = null;
        Collections.addAll(this.orderBy, orderBy);
        return (Q) this;
    }

    /**
     * Add an partition by clause to this window definition.
     *
     * @param partitionBy partition by expression
     * @return this window definition
     * @since 1.0
     */
    public Q partitionBy(Expression<?> partitionBy) {
        value = null;
        this.partitionBy.add(partitionBy);
        return (Q) this;
    }

    /**
     * Add an partition by clause to this window definition.
     *
     * @param partitionBy partition by expressions
     * @return this window definition
     * @since 1.0
     */
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

    /**
     * Return the base window name
     *
     * @return the base window name, if null if none
     * @since 1.0
     */
    public String getBaseWindowName() {
        return baseWindowName;
    }

    /**
     * Return the frame mode
     *
     * @return the frame mode, or null if none
     * @since 1.0
     */
    public WindowFrameMode getFrameMode() {
        return frameMode;
    }

    /**
     * Return the frame start type
     *
     * @return the frame start type, or null if none
     * @since 1.0
     */
    public WindowFramePositionType getFrameStartType() {
        return frameStartType;
    }

    /**
     * Return the frame start expression
     *
     * @return the frame start expression, or null if none
     * @since 1.0
     */
    public Expression<?> getFrameStartExpression() {
        return frameStartExpression;
    }

    /**
     * Return the frame end type
     *
     * @return the frame end type, or null if none
     * @since 1.0
     */
    public WindowFramePositionType getFrameEndType() {
        return frameEndType;
    }

    /**
     * Return the frame end expression
     *
     * @return the frame end expression, or null if none
     * @since 1.0
     */
    public Expression<?> getFrameEndExpression() {
        return frameEndExpression;
    }

    /**
     * Initiate a {@code WindowRows} builder in {@code ROWS} mode.
     *
     * @return {@code ROWS} clause builder
     * @since 1.0
     */
    public WindowRows<Q> rows() {
        value = null;
        int offset = orderBy.size() + partitionBy.size() + 1;
        return new WindowRows<Q>((Q) this, ROWS);
    }

    /**
     * Initiate a {@code WindowRows} builder in {@code RANGE} mode.
     *
     * @return {@code RANGE} clause builder
     * @since 1.0
     */
    public WindowRows<Q> range() {
        value = null;
        int offset = orderBy.size() + partitionBy.size() + 1;
        return new WindowRows<Q>((Q) this, RANGE);
    }

    /**
     * Initiate a {@code WindowRows} builder in {@code GROUPS} mode.
     *
     * @return {@code GROUPS} clause builder
     * @since 1.0
     */
    public WindowRows<Q> groups() {
        value = null;
        int offset = orderBy.size() + partitionBy.size() + 1;
        return new WindowRows<Q>((Q) this, GROUPS);
    }

}
