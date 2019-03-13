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


import java.io.File;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.TestDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.util.Throwables;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link ShouldBeInstance#create(org.assertj.core.description.Description, org.assertj.core.presentation.Representation)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class ShouldBeInstance_create_Test {
    private ErrorMessageFactory factory;

    @Test
    public void should_create_error_message() {
        String message = factory.create(new TestDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + ((((("Expecting:%n" + "  <\"Yoda\">%n") + "to be an instance of:%n") + "  <java.io.File>%n") + "but was instance of:%n") + "  <java.lang.String>"))));
    }

    @Test
    public void should_create_error_message_with_stack_trace_for_throwable() {
        IllegalArgumentException throwable = new IllegalArgumentException("Not a file");
        String message = ShouldBeInstance.shouldBeInstance(throwable, File.class).create();
        Assertions.assertThat(message).isEqualTo(String.format(("%nExpecting:%n" + (((("  <java.lang.IllegalArgumentException: Not a file>%n" + "to be an instance of:%n") + "  <java.io.File>%n") + "but was:%n") + "  <\"%s\">")), Throwables.getStackTrace(throwable)));
    }

    @Test
    public void should_create_shouldBeInstanceButWasNull_error_message() {
        factory = ShouldBeInstance.shouldBeInstanceButWasNull("other", File.class);
        String message = factory.create(new TestDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format(("[Test] %n" + (((("Expecting object:%n" + "  \"other\"%n") + "to be an instance of:%n") + "  <java.io.File>%n") + "but was null"))));
    }
}

