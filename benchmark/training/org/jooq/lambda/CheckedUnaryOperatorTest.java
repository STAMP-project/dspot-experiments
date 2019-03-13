/**
 * Copyright (c), Data Geekery GmbH, contact@datageekery.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jooq.lambda;


import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;
import org.jooq.lambda.fi.util.function.CheckedDoubleUnaryOperator;
import org.jooq.lambda.fi.util.function.CheckedIntUnaryOperator;
import org.jooq.lambda.fi.util.function.CheckedLongUnaryOperator;
import org.jooq.lambda.fi.util.function.CheckedUnaryOperator;
import org.junit.Test;


/**
 *
 *
 * @author Lukas Eder
 */
public class CheckedUnaryOperatorTest {
    @Test
    public void testCheckedUnaryOperator() {
        final CheckedUnaryOperator<Object> unaryOperator = ( t) -> {
            throw new Exception(("" + t));
        };
        UnaryOperator<Object> test = Unchecked.unaryOperator(unaryOperator);
        UnaryOperator<Object> alias = CheckedUnaryOperator.unchecked(unaryOperator);
        assertUnaryOperator(test, UncheckedException.class);
        assertUnaryOperator(alias, UncheckedException.class);
    }

    @Test
    public void testCheckedUnaryOperatorWithCustomHandler() {
        final CheckedUnaryOperator<Object> unaryOperator = ( t) -> {
            throw new Exception(("" + t));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        UnaryOperator<Object> test = Unchecked.unaryOperator(unaryOperator, handler);
        UnaryOperator<Object> alias = CheckedUnaryOperator.unchecked(unaryOperator, handler);
        assertUnaryOperator(test, IllegalStateException.class);
        assertUnaryOperator(alias, IllegalStateException.class);
    }

    @Test
    public void testCheckedIntUnaryOperator() {
        final CheckedIntUnaryOperator intUnaryOperator = ( i) -> {
            throw new Exception(("" + i));
        };
        IntUnaryOperator test = Unchecked.intUnaryOperator(intUnaryOperator);
        IntUnaryOperator alias = CheckedIntUnaryOperator.unchecked(intUnaryOperator);
        assertIntUnaryOperator(test, UncheckedException.class);
        assertIntUnaryOperator(alias, UncheckedException.class);
    }

    @Test
    public void testCheckedIntUnaryOperatorWithCustomHandler() {
        final CheckedIntUnaryOperator intUnaryOperator = ( i) -> {
            throw new Exception(("" + i));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        IntUnaryOperator test = Unchecked.intUnaryOperator(intUnaryOperator, handler);
        IntUnaryOperator alias = CheckedIntUnaryOperator.unchecked(intUnaryOperator, handler);
        assertIntUnaryOperator(test, IllegalStateException.class);
        assertIntUnaryOperator(alias, IllegalStateException.class);
    }

    @Test
    public void testCheckedLongUnaryOperator() {
        final CheckedLongUnaryOperator longUnaryOperator = ( l) -> {
            throw new Exception(("" + l));
        };
        LongUnaryOperator test = Unchecked.longUnaryOperator(longUnaryOperator);
        LongUnaryOperator alias = CheckedLongUnaryOperator.unchecked(longUnaryOperator);
        assertLongUnaryOperator(test, UncheckedException.class);
        assertLongUnaryOperator(alias, UncheckedException.class);
    }

    @Test
    public void testCheckedLongUnaryOperatorWithCustomHandler() {
        final CheckedLongUnaryOperator longUnaryOperator = ( l) -> {
            throw new Exception(("" + l));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        LongUnaryOperator test = Unchecked.longUnaryOperator(longUnaryOperator, handler);
        LongUnaryOperator alias = CheckedLongUnaryOperator.unchecked(longUnaryOperator, handler);
        assertLongUnaryOperator(test, IllegalStateException.class);
        assertLongUnaryOperator(alias, IllegalStateException.class);
    }

    @Test
    public void testCheckedDoubleUnaryOperator() {
        final CheckedDoubleUnaryOperator doubleUnaryOperator = ( d) -> {
            throw new Exception(("" + d));
        };
        DoubleUnaryOperator test = Unchecked.doubleUnaryOperator(doubleUnaryOperator);
        DoubleUnaryOperator alias = CheckedDoubleUnaryOperator.unchecked(doubleUnaryOperator);
        assertDoubleUnaryOperator(test, UncheckedException.class);
        assertDoubleUnaryOperator(alias, UncheckedException.class);
    }

    @Test
    public void testCheckedDoubleUnaryOperatorWithCustomHandler() {
        final CheckedDoubleUnaryOperator doubleUnaryOperator = ( d) -> {
            throw new Exception(("" + d));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        DoubleUnaryOperator test = Unchecked.doubleUnaryOperator(doubleUnaryOperator, handler);
        DoubleUnaryOperator alias = CheckedDoubleUnaryOperator.unchecked(doubleUnaryOperator, handler);
        assertDoubleUnaryOperator(test, IllegalStateException.class);
        assertDoubleUnaryOperator(alias, IllegalStateException.class);
    }
}

