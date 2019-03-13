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
package org.assertj.core.api.optional;


import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.BaseTest;
import org.assertj.core.error.OptionalShouldBePresent;
import org.assertj.core.error.OptionalShouldContainInstanceOf;
import org.junit.jupiter.api.Test;


public class OptionalAssert_containsInstanceOf_Test extends BaseTest {
    @Test
    public void should_fail_if_optional_is_empty() {
        Optional<Object> actual = Optional.empty();
        Throwable thrown = Assertions.catchThrowable(() -> {
            assertThat(actual).containsInstanceOf(.class);
        });
        Assertions.assertThat(thrown).isInstanceOf(AssertionError.class).hasMessage(OptionalShouldBePresent.shouldBePresent(actual).create());
    }

    @Test
    public void should_pass_if_optional_contains_required_type() {
        Assertions.assertThat(Optional.of("something")).containsInstanceOf(String.class).containsInstanceOf(Object.class);
    }

    @Test
    public void should_pass_if_optional_contains_required_type_subclass() {
        Assertions.assertThat(Optional.of(new OptionalAssert_containsInstanceOf_Test.SubClass())).containsInstanceOf(OptionalAssert_containsInstanceOf_Test.ParentClass.class);
    }

    @Test
    public void should_fail_if_optional_contains_other_type_than_required() {
        Optional<OptionalAssert_containsInstanceOf_Test.ParentClass> actual = Optional.of(new OptionalAssert_containsInstanceOf_Test.ParentClass());
        Throwable thrown = Assertions.catchThrowable(() -> {
            assertThat(actual).containsInstanceOf(.class);
        });
        Assertions.assertThat(thrown).isInstanceOf(AssertionError.class).hasMessage(OptionalShouldContainInstanceOf.shouldContainInstanceOf(actual, OptionalAssert_containsInstanceOf_Test.OtherClass.class).create());
    }

    private static class ParentClass {}

    private static class SubClass extends OptionalAssert_containsInstanceOf_Test.ParentClass {}

    private static class OtherClass {}
}

