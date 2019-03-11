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
package org.assertj.core.internal.objects;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.error.ShouldNotBeEqual;
import org.assertj.core.internal.ObjectsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Objects#assertNotEqual(AssertionInfo, Object, Object)}</code>.
 *
 * @author Alex Ruiz
 */
public class Objects_assertNotEqual_Test extends ObjectsBaseTest {
    @Test
    public void should_pass_if_objects_are_not_equal() {
        objects.assertNotEqual(TestData.someInfo(), "Yoda", "Luke");
    }

    @Test
    public void should_fail_if_objects_are_equal() {
        AssertionInfo info = TestData.someInfo();
        try {
            objects.assertNotEqual(info, "Yoda", "Yoda");
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqual.shouldNotBeEqual("Yoda", "Yoda"));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_objects_are_not_equal_according_to_custom_comparison_strategy() {
        objectsWithCustomComparisonStrategy.assertNotEqual(TestData.someInfo(), "Yoda", "Luke");
    }

    @Test
    public void should_fail_if_objects_are_equal_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            objectsWithCustomComparisonStrategy.assertNotEqual(info, "YoDA", "Yoda");
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqual.shouldNotBeEqual("YoDA", "Yoda", customComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

