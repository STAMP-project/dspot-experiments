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
import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.TestDescription;
import org.assertj.core.presentation.Representation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class ShouldBeDirectory_create_Test {
    private TestDescription description;

    private Representation representation;

    private ErrorMessageFactory factory;

    private String message;

    private String expectedMessage;

    @Test
    public void should_create_error_message_for_Path() {
        final Path path = Mockito.mock(Path.class);
        factory = ShouldBeDirectory.shouldBeDirectory(path);
        message = factory.create(description, representation);
        expectedMessage = String.format(("[Test] " + (ShouldBeDirectory.PATH_SHOULD_BE_DIRECTORY)), path);
        Assertions.assertThat(message).isEqualTo(expectedMessage);
    }

    @Test
    public void should_create_error_message_for_File() {
        final File file = new FakeFile("xyz");
        factory = ShouldBeDirectory.shouldBeDirectory(file);
        message = factory.create(description, representation);
        expectedMessage = String.format(("[Test] " + (ShouldBeDirectory.FILE_SHOULD_BE_DIRECTORY)), file);
        Assertions.assertThat(message).isEqualTo(expectedMessage);
    }
}

