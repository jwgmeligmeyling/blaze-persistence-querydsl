/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.parser.expression.WindowFrameMode;
import com.blazebit.persistence.parser.expression.WindowFramePositionType;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;

/**
 * {@code WindowRows} provides the building of the rows/range part of the window function expression
 *
 * @param <Def> Builder type
 *
 * @author tiwe
 */
public class WindowRows<Def extends WindowDefinition<Def, ?>> {

    private final WindowFrameMode frameMode;
    private WindowFramePositionType frameStartType;
    private Expression<?> frameStartExpression;
    private WindowFramePositionType frameEndType;
    private Expression<?> frameEndExpression;

    /**
     * Intermediate step
     */
    public class Between {

        public BetweenAnd unboundedPreceding() {
            frameStartType = WindowFramePositionType.UNBOUNDED_PRECEDING;
            return new BetweenAnd();
        }

        public BetweenAnd currentRow() {
            frameStartType = WindowFramePositionType.CURRENT_ROW;
            return new BetweenAnd();
        }

        public BetweenAnd preceding(Expression<Integer> expr) {
            frameStartType = WindowFramePositionType.BOUNDED_PRECEDING;
            frameStartExpression = expr;
            return new BetweenAnd();
        }

        public BetweenAnd preceding(int i) {
            return preceding(ConstantImpl.create(i));
        }

        public BetweenAnd following(Expression<Integer> expr) {
            frameStartType = WindowFramePositionType.BOUNDED_FOLLOWING;
            frameStartExpression = expr;
            return new BetweenAnd();
        }

        public BetweenAnd following(int i) {
            return following(ConstantImpl.create(i));
        }
    }

    /**
     * Intermediate step
     */
    public class BetweenAnd {

        public Def unboundedFollowing() {
            frameEndType = WindowFramePositionType.UNBOUNDED_FOLLOWING;
            return rv.withFrame(frameMode, frameStartType, frameStartExpression, frameEndType, frameEndExpression);
        }

        public Def currentRow() {
            frameEndType = WindowFramePositionType.CURRENT_ROW;
            return rv.withFrame(frameMode, frameStartType, frameStartExpression, frameEndType, frameEndExpression);
        }

        public Def preceding(Expression<Integer> expr) {
            frameEndType = WindowFramePositionType.BOUNDED_PRECEDING;
            frameEndExpression = expr;
            return rv.withFrame(frameMode, frameStartType, frameStartExpression, frameEndType, frameEndExpression);
        }

        public Def preceding(int i) {
            return preceding(ConstantImpl.create(i));
        }

        public Def following(Expression<Integer> expr) {
            frameEndType = WindowFramePositionType.BOUNDED_FOLLOWING;
            frameEndExpression = expr;
            return rv.withFrame(frameMode, frameStartType, frameStartExpression, frameEndType, frameEndExpression);
        }

        public Def following(int i) {
            return following(ConstantImpl.create(i));
        }
    }

    private final Def rv;

    public WindowRows(Def windowFunction, WindowFrameMode frameMode) {
        this.rv = windowFunction;
        this.frameMode = frameMode;
    }

    public Between between() {
        return new Between();
    }

    public Def unboundedPreceding() {
        frameStartType = WindowFramePositionType.UNBOUNDED_PRECEDING;
        return rv.withFrame(frameMode, frameStartType, frameStartExpression, frameEndType, frameEndExpression);
    }

    public Def currentRow() {
        frameStartType = WindowFramePositionType.CURRENT_ROW;
        return rv.withFrame(frameMode, frameStartType, frameStartExpression, frameEndType, frameEndExpression);
    }

    public Def preceding(Expression<Integer> expr) {
        frameStartType = WindowFramePositionType.BOUNDED_PRECEDING;
        frameStartExpression = expr;
        return rv.withFrame(frameMode, frameStartType, frameStartExpression, frameEndType, frameEndExpression);
    }

    public Def preceding(int i) {
        return preceding(ConstantImpl.create(i));
    }

}
