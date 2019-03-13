/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.internal.doublearrays;


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeNullOrEmpty;
import org.assertj.core.internal.DoubleArraysBaseTest;
import org.assertj.core.test.DoubleArrays;
import org.assertj.core.test.TestData;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link DoubleArrays#assertNullOrEmpty(AssertionInfo, double[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class DoubleArrays_assertNullOrEmpty_Test extends DoubleArraysBaseTest {
    @Test
    public void should_fail_if_array_is_not_null_and_is_not_empty() {
        double[] actual = new double[]{ 6.0, 8.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertNullOrEmpty(someInfo(), actual)).withMessage(ShouldBeNullOrEmpty.shouldBeNullOrEmpty(actual).create());
    }

    @Test
    public void should_pass_if_array_is_null() {
        arrays.assertNullOrEmpty(TestData.someInfo(), null);
    }

    @Test
    public void should_pass_if_array_is_empty() {
        arrays.assertNullOrEmpty(TestData.someInfo(), DoubleArrays.emptyArray());
    }
}

