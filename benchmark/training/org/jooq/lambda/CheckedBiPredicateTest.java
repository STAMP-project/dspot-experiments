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


import java.util.function.BiPredicate;
import java.util.function.Consumer;
import org.jooq.lambda.fi.util.function.CheckedBiPredicate;
import org.junit.Test;


/**
 *
 *
 * @author Lukas Eder
 */
public class CheckedBiPredicateTest {
    @Test
    public void testCheckedBiPredicate() {
        final CheckedBiPredicate<Object, Object> biPredicate = ( t, u) -> {
            throw new Exception(((t + ":") + u));
        };
        BiPredicate<Object, Object> p1 = Unchecked.biPredicate(biPredicate);
        BiPredicate<Object, Object> p2 = CheckedBiPredicate.unchecked(biPredicate);
        BiPredicate<Object, Object> p3 = Sneaky.biPredicate(biPredicate);
        BiPredicate<Object, Object> p4 = CheckedBiPredicate.sneaky(biPredicate);
        assertBiPredicate(p1, UncheckedException.class);
        assertBiPredicate(p2, UncheckedException.class);
        assertBiPredicate(p3, Exception.class);
        assertBiPredicate(p4, Exception.class);
    }

    @Test
    public void testCheckedBiPredicateWithCustomHandler() {
        final CheckedBiPredicate<Object, Object> biPredicate = ( t, u) -> {
            throw new Exception(((t + ":") + u));
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        BiPredicate<Object, Object> test = Unchecked.biPredicate(biPredicate, handler);
        BiPredicate<Object, Object> alias = CheckedBiPredicate.unchecked(biPredicate, handler);
        assertBiPredicate(test, IllegalStateException.class);
        assertBiPredicate(alias, IllegalStateException.class);
    }
}

