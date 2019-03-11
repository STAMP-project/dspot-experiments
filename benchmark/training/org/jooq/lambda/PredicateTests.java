/**
 * Copyright (c), Data Geekery GmbH, contact@datageekery.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jooq.lambda;


import java.util.function.Predicate;
import org.jooq.lambda.function.Functions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukas Eder
 */
public class PredicateTests {
    @Test
    public void testPredicates() {
        Predicate<Integer> even = ( i) -> (i % 2) == 0;
        Predicate<Integer> threes = ( i) -> (i % 3) == 0;
        Assert.assertTrue(even.test(0));
        Assert.assertFalse(even.test(1));
        Assert.assertFalse(Functions.not(even).test(0));
        Assert.assertTrue(Functions.not(even).test(1));
        Assert.assertTrue(Functions.and(even, threes).test(0));
        Assert.assertFalse(Functions.and(even, threes).test(1));
        Assert.assertFalse(Functions.and(even, threes).test(2));
        Assert.assertFalse(Functions.and(even, threes).test(3));
        Assert.assertFalse(Functions.and(even, threes).test(4));
        Assert.assertFalse(Functions.and(even, threes).test(5));
        Assert.assertTrue(Functions.and(even, threes).test(6));
        Assert.assertTrue(Functions.or(even, threes).test(0));
        Assert.assertFalse(Functions.or(even, threes).test(1));
        Assert.assertTrue(Functions.or(even, threes).test(2));
        Assert.assertTrue(Functions.or(even, threes).test(3));
        Assert.assertTrue(Functions.or(even, threes).test(4));
        Assert.assertFalse(Functions.or(even, threes).test(5));
        Assert.assertTrue(Functions.or(even, threes).test(6));
    }
}

