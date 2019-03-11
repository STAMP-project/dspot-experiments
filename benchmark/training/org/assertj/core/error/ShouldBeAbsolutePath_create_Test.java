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


import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.TestDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ShouldBeAbsolutePath#create(Description, org.assertj.core.presentation.Representation)}</code>.
 *
 * @author Yvonne Wang
 */
public class ShouldBeAbsolutePath_create_Test {
    private TestDescription description;

    private StandardRepresentation representation;

    private ErrorMessageFactory factory;

    private String actualMessage;

    @Test
    public void should_create_error_message_for_File_Object() {
        final FakeFile file = new FakeFile("xyz");
        factory = ShouldBeAbsolutePath.shouldBeAbsolutePath(file);
        actualMessage = factory.create(description, representation);
        Assertions.assertThat(actualMessage).isEqualTo(String.format(("[Test] " + (ShouldBeAbsolutePath.SHOULD_BE_ABSOLUTE_PATH)), file));
    }

    @Test
    public void should_create_error_message_for_Path_object() {
        final Path path = Mockito.mock(Path.class);
        factory = ShouldBeAbsolutePath.shouldBeAbsolutePath(path);
        actualMessage = factory.create(description, representation);
        Assertions.assertThat(actualMessage).isEqualTo(String.format(("[Test] " + (ShouldBeAbsolutePath.SHOULD_BE_ABSOLUTE_PATH)), path));
    }
}

