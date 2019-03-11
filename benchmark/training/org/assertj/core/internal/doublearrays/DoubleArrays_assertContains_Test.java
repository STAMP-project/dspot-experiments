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


import org.assertj.core.internal.Arrays;
import org.assertj.core.internal.DoubleArraysBaseTest;
import org.assertj.core.test.DoubleArrays;
import org.assertj.core.test.TestData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link DoubleArrays#assertContains(AssertionInfo, double[], double[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class DoubleArrays_assertContains_Test extends DoubleArraysBaseTest {
    private Arrays internalArrays;

    @Test
    public void should_delegate_to_internal_Arrays() {
        arrays.assertContains(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, 8.0, 10.0));
        Mockito.verify(internalArrays).assertContains(TestData.someInfo(), failures, actual, DoubleArrays.arrayOf(6.0, 8.0, 10.0));
    }
}

