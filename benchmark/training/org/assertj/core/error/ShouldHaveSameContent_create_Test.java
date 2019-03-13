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


import java.io.ByteArrayInputStream;
import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.api.Test;


/**
 * Tests for
 * <code>{@link ShouldHaveSameContent#create(org.assertj.core.description.Description, org.assertj.core.presentation.Representation)}</code>
 * .
 *
 * @author Yvonne Wang
 * @author Matthieu Baechler
 */
public class ShouldHaveSameContent_create_Test {
    @Test
    public void should_create_error_message_file_even_if_content_contains_format_specifier() {
        ErrorMessageFactory factory = ShouldHaveSameContent.shouldHaveSameContent(new FakeFile("abc"), new FakeFile("xyz"), Collections.emptyList());
        String expectedErrorMessage = String.format("[Test] %nFile:%n  <abc>%nand file:%n  <xyz>%ndo not have same content:%n%n");
        Assertions.assertThat(factory.create(new TextDescription("Test"), new StandardRepresentation())).isEqualTo(expectedErrorMessage);
    }

    @Test
    public void should_create_error_message_inputstream_even_if_content_contains_format_specifier() {
        ErrorMessageFactory factory = ShouldHaveSameContent.shouldHaveSameContent(new ByteArrayInputStream(new byte[]{ 'a' }), new ByteArrayInputStream(new byte[]{ 'b' }), Collections.emptyList());
        String expectedErrorMessage = String.format("[Test] %nInputStreams do not have same content:%n%n");
        Assertions.assertThat(factory.create(new TextDescription("Test"), new StandardRepresentation())).isEqualTo(expectedErrorMessage);
    }
}

