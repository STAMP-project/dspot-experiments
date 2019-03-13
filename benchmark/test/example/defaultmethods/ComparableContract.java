/**
 * Copyright 2015-2019 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */
package example.defaultmethods;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


// tag::user_guide[]
public interface ComparableContract<T extends Comparable<T>> extends Testable<T> {
    @Test
    default void returnsZeroWhenComparedToItself() {
        T value = createValue();
        Assertions.assertEquals(0, value.compareTo(value));
    }

    @Test
    default void returnsPositiveNumberWhenComparedToSmallerValue() {
        T value = createValue();
        T smallerValue = createSmallerValue();
        Assertions.assertTrue(((value.compareTo(smallerValue)) > 0));
    }

    @Test
    default void returnsNegativeNumberWhenComparedToLargerValue() {
        T value = createValue();
        T smallerValue = createSmallerValue();
        Assertions.assertTrue(((smallerValue.compareTo(value)) < 0));
    }
}

/**
 * end::user_guide[]
 */
