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


import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import org.jooq.lambda.fi.util.function.CheckedBinaryOperator;
import org.jooq.lambda.fi.util.function.CheckedDoubleBinaryOperator;
import org.jooq.lambda.fi.util.function.CheckedIntBinaryOperator;
import org.jooq.lambda.fi.util.function.CheckedLongBinaryOperator;
import org.junit.Test;


/**
 *
 *
 * @author Lukas Eder
 */
public class CheckedBinaryOperatorTest {
    @Test
    public void testCheckedBinaryOperator() {
        final CheckedBinaryOperator<Object> binaryOperator = ( t1, t2) -> {
            throw new Exception(((t1 + ":") + t2));
        };
        BinaryOperator<Object> o1 = Unchecked.binaryOperator(binaryOperator);
        BinaryOperator<Object> o2 = CheckedBinaryOperator.unchecked(binaryOperator);
        BinaryOperator<Object> o3 = Sneaky.binaryOperator(binaryOperator);
        BinaryOperator<Object> o4 = CheckedBinaryOperator.sneaky(binaryOperator);
        assertBinaryOperator(o1, UncheckedException.class);
        assertBinaryOperator(o2, UncheckedException.class);
        assertBinaryOperator(o3, Exception.class);
        assertBinaryOperator(o4, Exception.class);
    }

    @Test
    public void testCheckedBinaryOperatorWithCustomHandler() {
        final CheckedBinaryOperator<Object> binaryOperator = ( t1, t2) -> {
            throw new Exception(((t1 + ":") + t2));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        BinaryOperator<Object> test = Unchecked.binaryOperator(binaryOperator, handler);
        BinaryOperator<Object> alias = CheckedBinaryOperator.unchecked(binaryOperator, handler);
        assertBinaryOperator(test, IllegalStateException.class);
        assertBinaryOperator(alias, IllegalStateException.class);
    }

    @Test
    public void testCheckedIntBinaryOperator() {
        final CheckedIntBinaryOperator intBinaryOperator = ( i1, i2) -> {
            throw new Exception(((i1 + ":") + i2));
        };
        IntBinaryOperator o1 = Unchecked.intBinaryOperator(intBinaryOperator);
        IntBinaryOperator o2 = CheckedIntBinaryOperator.unchecked(intBinaryOperator);
        IntBinaryOperator o3 = Sneaky.intBinaryOperator(intBinaryOperator);
        IntBinaryOperator o4 = CheckedIntBinaryOperator.sneaky(intBinaryOperator);
        assertIntBinaryOperator(o1, UncheckedException.class);
        assertIntBinaryOperator(o2, UncheckedException.class);
        assertIntBinaryOperator(o3, Exception.class);
        assertIntBinaryOperator(o4, Exception.class);
    }

    @Test
    public void testCheckedIntBinaryOperatorWithCustomHandler() {
        final CheckedIntBinaryOperator intBinaryOperator = ( i1, i2) -> {
            throw new Exception(((i1 + ":") + i2));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        IntBinaryOperator test = Unchecked.intBinaryOperator(intBinaryOperator, handler);
        IntBinaryOperator alias = CheckedIntBinaryOperator.unchecked(intBinaryOperator, handler);
        assertIntBinaryOperator(test, IllegalStateException.class);
        assertIntBinaryOperator(alias, IllegalStateException.class);
    }

    @Test
    public void testCheckedLongBinaryOperator() {
        final CheckedLongBinaryOperator longBinaryOperator = ( l1, l2) -> {
            throw new Exception(((l1 + ":") + l2));
        };
        LongBinaryOperator o1 = Unchecked.longBinaryOperator(longBinaryOperator);
        LongBinaryOperator o2 = CheckedLongBinaryOperator.unchecked(longBinaryOperator);
        LongBinaryOperator o3 = Sneaky.longBinaryOperator(longBinaryOperator);
        LongBinaryOperator o4 = CheckedLongBinaryOperator.sneaky(longBinaryOperator);
        assertLongBinaryOperator(o1, UncheckedException.class);
        assertLongBinaryOperator(o2, UncheckedException.class);
        assertLongBinaryOperator(o3, Exception.class);
        assertLongBinaryOperator(o4, Exception.class);
    }

    @Test
    public void testCheckedLongBinaryOperatorWithCustomHandler() {
        final CheckedLongBinaryOperator longBinaryOperator = ( l1, l2) -> {
            throw new Exception(((l1 + ":") + l2));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        LongBinaryOperator test = Unchecked.longBinaryOperator(longBinaryOperator, handler);
        LongBinaryOperator alias = CheckedLongBinaryOperator.unchecked(longBinaryOperator, handler);
        assertLongBinaryOperator(test, IllegalStateException.class);
        assertLongBinaryOperator(alias, IllegalStateException.class);
    }

    @Test
    public void testCheckedDoubleBinaryOperator() {
        final CheckedDoubleBinaryOperator doubleBinaryOperator = ( d1, d2) -> {
            throw new Exception(((d1 + ":") + d2));
        };
        DoubleBinaryOperator o1 = Unchecked.doubleBinaryOperator(doubleBinaryOperator);
        DoubleBinaryOperator o2 = CheckedDoubleBinaryOperator.unchecked(doubleBinaryOperator);
        DoubleBinaryOperator o3 = Sneaky.doubleBinaryOperator(doubleBinaryOperator);
        DoubleBinaryOperator o4 = CheckedDoubleBinaryOperator.sneaky(doubleBinaryOperator);
        assertDoubleBinaryOperator(o1, UncheckedException.class);
        assertDoubleBinaryOperator(o2, UncheckedException.class);
        assertDoubleBinaryOperator(o3, Exception.class);
        assertDoubleBinaryOperator(o4, Exception.class);
    }

    @Test
    public void testCheckedDoubleBinaryOperatorWithCustomHandler() {
        final CheckedDoubleBinaryOperator doubleBinaryOperator = ( d1, d2) -> {
            throw new Exception(((d1 + ":") + d2));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        DoubleBinaryOperator test = Unchecked.doubleBinaryOperator(doubleBinaryOperator, handler);
        DoubleBinaryOperator alias = CheckedDoubleBinaryOperator.unchecked(doubleBinaryOperator, handler);
        assertDoubleBinaryOperator(test, IllegalStateException.class);
        assertDoubleBinaryOperator(alias, IllegalStateException.class);
    }
}

