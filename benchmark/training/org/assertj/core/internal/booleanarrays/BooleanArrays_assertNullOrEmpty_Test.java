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
package org.assertj.core.internal.booleanarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.error.ShouldBeNullOrEmpty;
import org.assertj.core.internal.BooleanArraysBaseTest;
import org.assertj.core.test.BooleanArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link BooleanArrays#assertNullOrEmpty(AssertionInfo, boolean[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class BooleanArrays_assertNullOrEmpty_Test extends BooleanArraysBaseTest {
    @Test
    public void should_fail_if_array_is_not_null_and_is_not_empty() {
        AssertionInfo info = TestData.someInfo();
        boolean[] actual = new boolean[]{ true, false };
        try {
            arrays.assertNullOrEmpty(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeNullOrEmpty.shouldBeNullOrEmpty(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_array_is_null() {
        arrays.assertNullOrEmpty(TestData.someInfo(), null);
    }

    @Test
    public void should_pass_if_array_is_empty() {
        arrays.assertNullOrEmpty(TestData.someInfo(), BooleanArrays.emptyArray());
    }
}

