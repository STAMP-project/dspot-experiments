/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.query.impl.predicates;


import Warning.NONFINAL_FIELDS;
import Warning.STRICT_INHERITANCE;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InPredicateTest {
    // should be fixed in 4.0; See: https://github.com/hazelcast/hazelcast/issues/6188
    @Test
    @SuppressWarnings("unchecked")
    public void equal_zeroMinusZero() {
        // in BetweenPredicate
        Assert.assertFalse(new InPredicate("this", 0.0).apply(PredicateTestUtils.entry((-0.0))));
        Assert.assertFalse(new InPredicate("this", 0.0).apply(PredicateTestUtils.entry((-0.0))));
        Assert.assertFalse(new InPredicate("this", 0.0).apply(PredicateTestUtils.entry((-0.0))));
        Assert.assertFalse(new InPredicate("this", 0.0).apply(PredicateTestUtils.entry((-0.0))));
        // whereas in Java
        Assert.assertTrue((0.0 == (-0.0)));
        Assert.assertTrue((0.0 == (-0.0)));
        Assert.assertTrue((0.0 == (-0.0)));
        Assert.assertTrue((0.0 == (-0.0)));
    }

    // should be fixed in 4.0; See: https://github.com/hazelcast/hazelcast/issues/6188
    @Test
    @SuppressWarnings("unchecked")
    public void equal_NaN() {
        // in BetweenPredicate
        Assert.assertTrue(new InPredicate("this", Double.NaN).apply(PredicateTestUtils.entry(Double.NaN)));
        Assert.assertTrue(new InPredicate("this", Float.NaN).apply(PredicateTestUtils.entry(Float.NaN)));
        Assert.assertTrue(new InPredicate("this", Double.NaN).apply(PredicateTestUtils.entry((-(Double.NaN)))));
        Assert.assertTrue(new InPredicate("this", Float.NaN).apply(PredicateTestUtils.entry((-(Float.NaN)))));
        Assert.assertTrue(new InPredicate("this", Double.NaN).apply(PredicateTestUtils.entry((-(Float.NaN)))));
        Assert.assertTrue(new InPredicate("this", Float.NaN).apply(PredicateTestUtils.entry((-(Double.NaN)))));
        // whereas in Java
        Assert.assertFalse(((Double.NaN) == (Double.NaN)));
        Assert.assertFalse(((Float.NaN) == (Float.NaN)));
        Assert.assertFalse(((Double.NaN) == (-(Double.NaN))));
        Assert.assertFalse(((Float.NaN) == (-(Float.NaN))));
        Assert.assertFalse(((Double.NaN) == (-(Float.NaN))));
        Assert.assertFalse(((Float.NaN) == (-(Double.NaN))));
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier.forClass(InPredicate.class).suppress(NONFINAL_FIELDS, STRICT_INHERITANCE).withRedefinedSuperclass().allFieldsShouldBeUsed().verify();
    }
}

