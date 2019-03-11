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
package org.assertj.core.internal.floatarrays;


import org.assertj.core.internal.Arrays;
import org.assertj.core.internal.FloatArraysBaseTest;
import org.assertj.core.test.FloatArrays;
import org.assertj.core.test.TestData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link FloatArrays#assertContains(AssertionInfo, float[], float[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class FloatArrays_assertContains_Test extends FloatArraysBaseTest {
    private Arrays internalArrays;

    @Test
    public void should_delegate_to_internal_Arrays() {
        arrays.assertContains(TestData.someInfo(), actual, FloatArrays.arrayOf(6.0F, 8.0F, 10.0F));
        Mockito.verify(internalArrays).assertContains(TestData.someInfo(), failures, actual, FloatArrays.arrayOf(6.0F, 8.0F, 10.0F));
    }
}

