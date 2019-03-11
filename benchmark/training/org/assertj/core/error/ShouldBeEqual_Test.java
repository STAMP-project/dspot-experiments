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
package org.assertj.core.error;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;


public class ShouldBeEqual_Test {
    @Test
    public void should_display_comparison_strategy_in_error_message() {
        // GIVEN
        String actual = "Luke";
        String expected = "Yoda";
        ThrowingCallable code = () -> assertThat(actual).as("Jedi").usingComparator(CaseInsensitiveStringComparator.instance).isEqualTo(expected);
        // WHEN
        AssertionFailedError error = Assertions.catchThrowableOfType(code, AssertionFailedError.class);
        // THEN
        Assertions.assertThat(error.getActual().getValue()).isSameAs(actual);
        Assertions.assertThat(error.getExpected().getValue()).isSameAs(expected);
        Assertions.assertThat(error).hasMessage(String.format(("[Jedi] %nExpecting:%n" + ((((" <\"Luke\">%n" + "to be equal to:%n") + " <\"Yoda\">%n") + "when comparing values using CaseInsensitiveStringComparator%n") + "but was not."))));
    }
}

