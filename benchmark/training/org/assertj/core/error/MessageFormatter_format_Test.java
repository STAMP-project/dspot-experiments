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
import org.assertj.core.description.Description;
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link MessageFormatter#format(Description, String, Object...)}</code>.
 *
 * @author Alex Ruiz
 */
public class MessageFormatter_format_Test {
    private DescriptionFormatter descriptionFormatter;

    private MessageFormatter messageFormatter;

    @Test
    public void should_throw_error_if_format_string_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> messageFormatter.format(null, null, null));
    }

    @Test
    public void should_throw_error_if_args_array_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Object[] args = null;
            messageFormatter.format(null, null, "", args);
        });
    }

    @Test
    public void should_format_message() {
        Description description = new TextDescription("Test");
        String s = messageFormatter.format(description, StandardRepresentation.STANDARD_REPRESENTATION, "Hello %s", "World");
        Assertions.assertThat(s).isEqualTo("[Test] Hello \"World\"");
        Mockito.verify(descriptionFormatter).format(description);
    }
}

