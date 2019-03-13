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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class GreaterLessPredicateTest {
    @Test
    public void negate_whenEqualsTrueAndLessTrue_thenReturnNewInstanceWithEqualsFalseAndLessFalse() {
        String attribute = "attribute";
        Comparable value = 1;
        GreaterLessPredicate original = new GreaterLessPredicate(attribute, value, true, true);
        GreaterLessPredicate negate = ((GreaterLessPredicate) (original.negate()));
        Assert.assertThat(negate, CoreMatchers.not(CoreMatchers.sameInstance(original)));
        Assert.assertThat(negate.attributeName, CoreMatchers.equalTo(attribute));
        Assert.assertThat(negate.equal, CoreMatchers.is(false));
        Assert.assertThat(negate.less, CoreMatchers.is(false));
    }

    @Test
    public void negate_whenEqualsFalseAndLessFalse_thenReturnNewInstanceWithEqualsTrueAndLessTrue() {
        String attribute = "attribute";
        Comparable value = 1;
        GreaterLessPredicate original = new GreaterLessPredicate(attribute, value, false, false);
        GreaterLessPredicate negate = ((GreaterLessPredicate) (original.negate()));
        Assert.assertThat(negate, CoreMatchers.not(CoreMatchers.sameInstance(original)));
        Assert.assertThat(negate.attributeName, CoreMatchers.equalTo(attribute));
        Assert.assertThat(negate.equal, CoreMatchers.is(true));
        Assert.assertThat(negate.less, CoreMatchers.is(true));
    }

    @Test
    public void negate_whenEqualsTrueAndLessFalse_thenReturnNewInstanceWithEqualsFalseAndLessTrue() {
        String attribute = "attribute";
        Comparable value = 1;
        GreaterLessPredicate original = new GreaterLessPredicate(attribute, value, true, false);
        GreaterLessPredicate negate = ((GreaterLessPredicate) (original.negate()));
        Assert.assertThat(negate, CoreMatchers.not(CoreMatchers.sameInstance(original)));
        Assert.assertThat(negate.attributeName, CoreMatchers.equalTo(attribute));
        Assert.assertThat(negate.equal, CoreMatchers.is(false));
        Assert.assertThat(negate.less, CoreMatchers.is(true));
    }

    @Test
    public void negate_whenEqualsFalseAndLessTrue_thenReturnNewInstanceWithEqualsTrueAndLessFalse() {
        String attribute = "attribute";
        Comparable value = 1;
        GreaterLessPredicate original = new GreaterLessPredicate(attribute, value, false, true);
        GreaterLessPredicate negate = ((GreaterLessPredicate) (original.negate()));
        Assert.assertThat(negate, CoreMatchers.not(CoreMatchers.sameInstance(original)));
        Assert.assertThat(negate.attributeName, CoreMatchers.equalTo(attribute));
        Assert.assertThat(negate.equal, CoreMatchers.is(true));
        Assert.assertThat(negate.less, CoreMatchers.is(false));
    }

    // should be fixed in 4.0; See: https://github.com/hazelcast/hazelcast/issues/6188
    @Test
    @SuppressWarnings("unchecked")
    public void equal_zeroMinusZero() {
        final boolean equal = true;
        final boolean less = false;
        // in GreaterLessPredicate predicate
        Assert.assertFalse(new GreaterLessPredicate("this", 0.0, equal, less).apply(PredicateTestUtils.entry((-0.0))));
        Assert.assertFalse(new GreaterLessPredicate("this", 0.0, equal, less).apply(PredicateTestUtils.entry((-0.0))));
        Assert.assertFalse(new GreaterLessPredicate("this", 0.0, equal, less).apply(PredicateTestUtils.entry((-0.0))));
        Assert.assertFalse(new GreaterLessPredicate("this", 0.0, equal, less).apply(PredicateTestUtils.entry((-0.0))));
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
        final boolean equal = true;
        final boolean less = false;
        // in GreaterLessPredicate
        Assert.assertTrue(new GreaterLessPredicate("this", Double.NaN, equal, less).apply(PredicateTestUtils.entry(Double.NaN)));
        Assert.assertTrue(new GreaterLessPredicate("this", Float.NaN, equal, less).apply(PredicateTestUtils.entry(Float.NaN)));
        Assert.assertTrue(new GreaterLessPredicate("this", Double.NaN, equal, less).apply(PredicateTestUtils.entry((-(Double.NaN)))));
        Assert.assertTrue(new GreaterLessPredicate("this", Float.NaN, equal, less).apply(PredicateTestUtils.entry((-(Float.NaN)))));
        Assert.assertTrue(new GreaterLessPredicate("this", Double.NaN, equal, less).apply(PredicateTestUtils.entry((-(Float.NaN)))));
        Assert.assertTrue(new GreaterLessPredicate("this", Float.NaN, equal, less).apply(PredicateTestUtils.entry((-(Double.NaN)))));
        // whereas in Java
        Assert.assertFalse(((Double.NaN) == (Double.NaN)));
        Assert.assertFalse(((Float.NaN) == (Float.NaN)));
        Assert.assertFalse(((Double.NaN) == (-(Double.NaN))));
        Assert.assertFalse(((Float.NaN) == (-(Float.NaN))));
        Assert.assertFalse(((Double.NaN) == (-(Float.NaN))));
        Assert.assertFalse(((Float.NaN) == (-(Double.NaN))));
    }

    // should be fixed in 4.0; See: https://github.com/hazelcast/hazelcast/issues/6188
    @Test
    @SuppressWarnings("unchecked")
    public void greaterThan() {
        final boolean equal = true;
        // in GreaterLessPredicate
        Assert.assertTrue(new GreaterLessPredicate("this", 100.0, equal, false).apply(PredicateTestUtils.entry(Double.NaN)));
        Assert.assertTrue(new GreaterLessPredicate("this", 100.0, equal, false).apply(PredicateTestUtils.entry(Double.NaN)));
        Assert.assertTrue(new GreaterLessPredicate("this", 100.0, equal, false).apply(PredicateTestUtils.entry(Float.NaN)));
        Assert.assertTrue(new GreaterLessPredicate("this", 100.0, equal, false).apply(PredicateTestUtils.entry(Float.NaN)));
        Assert.assertTrue(new GreaterLessPredicate("this", 100.0, equal, false).apply(PredicateTestUtils.entry((-(Double.NaN)))));
        Assert.assertTrue(new GreaterLessPredicate("this", 100.0, equal, false).apply(PredicateTestUtils.entry((-(Double.NaN)))));
        Assert.assertTrue(new GreaterLessPredicate("this", 100.0, equal, false).apply(PredicateTestUtils.entry((-(Float.NaN)))));
        Assert.assertTrue(new GreaterLessPredicate("this", 100.0, equal, false).apply(PredicateTestUtils.entry((-(Float.NaN)))));
        Assert.assertFalse(new GreaterLessPredicate("this", (-100.0), equal, true).apply(PredicateTestUtils.entry((-(Double.NaN)))));
        Assert.assertFalse(new GreaterLessPredicate("this", (-100.0), equal, true).apply(PredicateTestUtils.entry((-(Double.NaN)))));
        Assert.assertFalse(new GreaterLessPredicate("this", (-100.0), equal, true).apply(PredicateTestUtils.entry((-(Float.NaN)))));
        Assert.assertFalse(new GreaterLessPredicate("this", (-100.0), equal, true).apply(PredicateTestUtils.entry((-(Float.NaN)))));
        // whereas in Java
        Assert.assertFalse(((Double.NaN) > 100.0));
        Assert.assertFalse(((Double.NaN) > 100.0));
        Assert.assertFalse(((Float.NaN) > 100.0));
        Assert.assertFalse(((Float.NaN) > 100.0));
        Assert.assertFalse(((-(Double.NaN)) > 100.0));
        Assert.assertFalse(((-(Double.NaN)) > 100.0));
        Assert.assertFalse(((-(Float.NaN)) > 100.0));
        Assert.assertFalse(((-(Float.NaN)) > 100.0));
        Assert.assertFalse(((-(Double.NaN)) < (-100.0)));
        Assert.assertFalse(((-(Double.NaN)) < (-100.0)));
        Assert.assertFalse(((-(Float.NaN)) < (-100.0)));
        Assert.assertFalse(((-(Float.NaN)) < (-100.0)));
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier.forClass(GreaterLessPredicate.class).suppress(NONFINAL_FIELDS, STRICT_INHERITANCE).withRedefinedSuperclass().allFieldsShouldBeUsed().verify();
    }
}

