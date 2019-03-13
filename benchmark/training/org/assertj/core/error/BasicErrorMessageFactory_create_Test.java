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
import org.assertj.core.description.EmptyTextDescription;
import org.assertj.core.internal.TestDescription;
import org.assertj.core.presentation.Representation;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for
 * <code>{@link BasicErrorMessageFactory#create(Description, org.assertj.core.presentation.Representation)}</code>.
 *
 * @author Yvonne Wang
 */
public class BasicErrorMessageFactory_create_Test {
    private MessageFormatter formatter;

    private BasicErrorMessageFactory factory;

    @Test
    public void should_implement_toString() {
        Description description = new TestDescription("Test");
        Representation representation = new StandardRepresentation();
        String formattedMessage = "[Test] Hello Yoda";
        Mockito.when(formatter.format(description, representation, "Hello %s", "Yoda")).thenReturn(formattedMessage);
        Assertions.assertThat(factory.create(description, representation)).isEqualTo(formattedMessage);
    }

    @Test
    public void should_create_error_with_configured_representation() {
        Description description = new TestDescription("Test");
        String formattedMessage = "[Test] Hello Yoda";
        Mockito.when(formatter.format(ArgumentMatchers.eq(description), ArgumentMatchers.same(CONFIGURATION_PROVIDER.representation()), ArgumentMatchers.eq("Hello %s"), ArgumentMatchers.eq("Yoda"))).thenReturn(formattedMessage);
        Assertions.assertThat(factory.create(description)).isEqualTo(formattedMessage);
    }

    @Test
    public void should_create_error_with_empty_description_and_configured_representation() {
        Description description = EmptyTextDescription.emptyDescription();
        String formattedMessage = "[] Hello Yoda";
        Mockito.when(formatter.format(ArgumentMatchers.eq(description), ArgumentMatchers.same(CONFIGURATION_PROVIDER.representation()), ArgumentMatchers.eq("Hello %s"), ArgumentMatchers.eq("Yoda"))).thenReturn(formattedMessage);
        Assertions.assertThat(factory.create()).isEqualTo(formattedMessage);
    }
}

