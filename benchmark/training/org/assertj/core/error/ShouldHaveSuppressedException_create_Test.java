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
import org.assertj.core.description.TextDescription;
import org.junit.jupiter.api.Test;


public class ShouldHaveSuppressedException_create_Test {
    @Test
    public void should_create_error_message() {
        Throwable actual = new Throwable();
        actual.addSuppressed(new IllegalArgumentException("invalid arg"));
        actual.addSuppressed(new NullPointerException("null arg"));
        ErrorMessageFactory factory = ShouldHaveSuppressedException.shouldHaveSuppressedException(actual, new IllegalArgumentException("foo"));
        String message = factory.create(new TextDescription("Test"), CONFIGURATION_PROVIDER.representation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + (((((("Expecting:%n" + "  <java.lang.Throwable>%n") + "to have a suppressed exception with the following type and message:%n") + "  <\"java.lang.IllegalArgumentException\"> / <\"foo\">%n") + "but could not find any in actual's suppressed exceptions:%n") + "  <[java.lang.IllegalArgumentException: invalid arg,%n") + "    java.lang.NullPointerException: null arg]>."))));
    }
}

