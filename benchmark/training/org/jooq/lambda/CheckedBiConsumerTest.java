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


import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import org.jooq.lambda.fi.util.function.CheckedBiConsumer;
import org.jooq.lambda.fi.util.function.CheckedObjDoubleConsumer;
import org.jooq.lambda.fi.util.function.CheckedObjIntConsumer;
import org.jooq.lambda.fi.util.function.CheckedObjLongConsumer;
import org.junit.Test;


/**
 *
 *
 * @author Lukas Eder
 */
public class CheckedBiConsumerTest {
    @Test
    public void testCheckedBiConsumer() {
        final CheckedBiConsumer<Object, Object> biConsumer = ( o1, o2) -> {
            throw new Exception(((o1 + ":") + o2));
        };
        BiConsumer<Object, Object> c1 = Unchecked.biConsumer(biConsumer);
        BiConsumer<Object, Object> c2 = CheckedBiConsumer.unchecked(biConsumer);
        BiConsumer<Object, Object> c3 = Sneaky.biConsumer(biConsumer);
        BiConsumer<Object, Object> c4 = CheckedBiConsumer.sneaky(biConsumer);
        assertBiConsumer(c1, UncheckedException.class);
        assertBiConsumer(c2, UncheckedException.class);
        assertBiConsumer(c3, Exception.class);
        assertBiConsumer(c4, Exception.class);
    }

    @Test
    public void testCheckedBiConsumerWithCustomHandler() {
        final CheckedBiConsumer<Object, Object> biConsumer = ( o1, o2) -> {
            throw new Exception(((o1 + ":") + o2));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        BiConsumer<Object, Object> c1 = Unchecked.biConsumer(biConsumer, handler);
        BiConsumer<Object, Object> c2 = CheckedBiConsumer.unchecked(biConsumer, handler);
        assertBiConsumer(c1, IllegalStateException.class);
        assertBiConsumer(c2, IllegalStateException.class);
    }

    @Test
    public void testCheckedObjIntConsumer() {
        final CheckedObjIntConsumer<Object> objIntConsumer = ( o1, o2) -> {
            throw new Exception(((o1 + ":") + o2));
        };
        ObjIntConsumer<Object> c1 = Unchecked.objIntConsumer(objIntConsumer);
        ObjIntConsumer<Object> c2 = CheckedObjIntConsumer.unchecked(objIntConsumer);
        ObjIntConsumer<Object> c3 = Sneaky.objIntConsumer(objIntConsumer);
        ObjIntConsumer<Object> c4 = CheckedObjIntConsumer.sneaky(objIntConsumer);
        assertObjIntConsumer(c1, UncheckedException.class);
        assertObjIntConsumer(c2, UncheckedException.class);
        assertObjIntConsumer(c3, Exception.class);
        assertObjIntConsumer(c4, Exception.class);
    }

    @Test
    public void testCheckedObjIntConsumerWithCustomHandler() {
        final CheckedObjIntConsumer<Object> objIntConsumer = ( o1, o2) -> {
            throw new Exception(((o1 + ":") + o2));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        ObjIntConsumer<Object> test = Unchecked.objIntConsumer(objIntConsumer, handler);
        ObjIntConsumer<Object> alias = CheckedObjIntConsumer.unchecked(objIntConsumer, handler);
        assertObjIntConsumer(test, IllegalStateException.class);
        assertObjIntConsumer(alias, IllegalStateException.class);
    }

    @Test
    public void testCheckedObjLongConsumer() {
        final CheckedObjLongConsumer<Object> objLongConsumer = ( o1, o2) -> {
            throw new Exception(((o1 + ":") + o2));
        };
        ObjLongConsumer<Object> c1 = Unchecked.objLongConsumer(objLongConsumer);
        ObjLongConsumer<Object> c2 = CheckedObjLongConsumer.unchecked(objLongConsumer);
        ObjLongConsumer<Object> c3 = Sneaky.objLongConsumer(objLongConsumer);
        ObjLongConsumer<Object> c4 = CheckedObjLongConsumer.sneaky(objLongConsumer);
        assertObjLongConsumer(c1, UncheckedException.class);
        assertObjLongConsumer(c2, UncheckedException.class);
        assertObjLongConsumer(c3, Exception.class);
        assertObjLongConsumer(c4, Exception.class);
    }

    @Test
    public void testCheckedObjLongConsumerWithCustomHandler() {
        final CheckedObjLongConsumer<Object> objLongConsumer = ( o1, o2) -> {
            throw new Exception(((o1 + ":") + o2));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        ObjLongConsumer<Object> test = Unchecked.objLongConsumer(objLongConsumer, handler);
        ObjLongConsumer<Object> alias = CheckedObjLongConsumer.unchecked(objLongConsumer, handler);
        assertObjLongConsumer(test, IllegalStateException.class);
        assertObjLongConsumer(alias, IllegalStateException.class);
    }

    @Test
    public void testCheckedObjDoubleConsumer() {
        final CheckedObjDoubleConsumer<Object> objDoubleConsumer = ( o1, o2) -> {
            throw new Exception(((o1 + ":") + o2));
        };
        ObjDoubleConsumer<Object> c1 = Unchecked.objDoubleConsumer(objDoubleConsumer);
        ObjDoubleConsumer<Object> c2 = CheckedObjDoubleConsumer.unchecked(objDoubleConsumer);
        ObjDoubleConsumer<Object> c3 = Sneaky.objDoubleConsumer(objDoubleConsumer);
        ObjDoubleConsumer<Object> c4 = CheckedObjDoubleConsumer.sneaky(objDoubleConsumer);
        assertObjDoubleConsumer(c1, UncheckedException.class);
        assertObjDoubleConsumer(c2, UncheckedException.class);
        assertObjDoubleConsumer(c3, Exception.class);
        assertObjDoubleConsumer(c4, Exception.class);
    }

    @Test
    public void testCheckedObjDoubleConsumerWithCustomHandler() {
        final CheckedObjDoubleConsumer<Object> objDoubleConsumer = ( o1, o2) -> {
            throw new Exception(((o1 + ":") + o2));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        ObjDoubleConsumer<Object> test = Unchecked.objDoubleConsumer(objDoubleConsumer, handler);
        ObjDoubleConsumer<Object> alias = CheckedObjDoubleConsumer.unchecked(objDoubleConsumer, handler);
        assertObjDoubleConsumer(test, IllegalStateException.class);
        assertObjDoubleConsumer(alias, IllegalStateException.class);
    }
}

