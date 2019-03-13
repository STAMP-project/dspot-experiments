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
package org.assertj.core.api.string_;


import java.util.IllegalFormatException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.StringAssertBaseTest;
import org.assertj.core.util.AssertionsUtil;
import org.junit.Test;


public class StringAssert_isEqualTo_Test extends StringAssertBaseTest {
    @Test
    public void should_throw_IllegalFormatException_when_given_an_invalid_format() {
        // GIVEN
        String template = "%s %s";
        // WHEN
        Throwable exception = Assertions.catchThrowable(() -> assertThat("foo").isEqualTo(template, 1));
        // THEN
        Assertions.assertThat(exception).isInstanceOf(IllegalFormatException.class);
    }

    @Test
    public void should_throw_NullPointerException_when_given_a_null_template() {
        // GIVEN
        String template = null;
        // WHEN
        Throwable exception = Assertions.catchThrowable(() -> assertThat("foo").isEqualTo(template, 1));
        // THEN
        Assertions.assertThat(exception).isInstanceOf(NullPointerException.class).hasMessageContaining("The expectedStringTemplate must not be null");
    }

    @Test
    public void should_fail_if_actual_is_null_since_template_cant_be_null() {
        // GIVEN
        String actual = null;
        // THEN
        AssertionsUtil.expectAssertionError(() -> assertThat(actual).isEqualTo("%s", "abc"));
    }
}

