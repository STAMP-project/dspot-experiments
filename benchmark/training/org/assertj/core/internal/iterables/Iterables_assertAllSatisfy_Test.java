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
package org.assertj.core.internal.iterables;


import java.util.List;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ElementsShouldSatisfy;
import org.assertj.core.error.ElementsShouldSatisfy.UnsatisfiedRequirement;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Iterables_assertAllSatisfy_Test extends IterablesBaseTest {
    private List<String> actual = Lists.newArrayList("Luke", "Leia", "Yoda");

    @Test
    public void should_satisfy_single_requirement() {
        iterables.assertAllSatisfy(TestData.someInfo(), actual, ( s) -> assertThat(s.length()).isEqualTo(4));
    }

    @Test
    public void should_satisfy_multiple_requirements() {
        iterables.assertAllSatisfy(TestData.someInfo(), actual, ( s) -> {
            assertThat(s.length()).isEqualTo(4);
            assertThat(s).doesNotContain("V");
        });
    }

    @Test
    public void should_fail_according_to_requirements() {
        // GIVEN
        Consumer<String> restrictions = ( s) -> {
            Assertions.assertThat(s.length()).isEqualTo(4);
            Assertions.assertThat(s).startsWith("L");
        };
        // WHEN
        Throwable error = Assertions.catchThrowable(() -> iterables.assertAllSatisfy(someInfo(), actual, restrictions));
        // THEN
        Assertions.assertThat(error).isNotNull();
        List<UnsatisfiedRequirement> errors = Lists.list(new UnsatisfiedRequirement("Yoda", String.format(("%n" + ((("Expecting:%n" + " <\"Yoda\">%n") + "to start with:%n") + " <\"L\">%n")))));
        Mockito.verify(failures).failure(info, ElementsShouldSatisfy.elementsShouldSatisfy(actual, errors, TestData.someInfo()));
    }

    @Test
    public void should_fail_if_consumer_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(actual).allSatisfy(null)).withMessage("The Consumer<T> expressing the assertions requirements must not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            actual = null;
            assertThat(actual).allSatisfy(null);
        }).withMessage(FailureMessages.actualIsNull());
    }
}

