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


/**
 * Tests for {@link ShouldHaveNoParent#shouldHaveNoParent(File)} and {@link ShouldHaveNoParent#shouldHaveNoParent(Path)}
 *
 * @author Jean-Christophe Gay
 * @author Francis Galiegue
 */
public class ShouldHaveNoParent_create_Test {
    private TestDescription description;

    private Representation representation;

    private ErrorMessageFactory factory;

    private String actualMessage;

    private String expectedMessage;

    @Test
    public void should_create_error_message_when_file_has_a_parent() {
        final File file = Mockito.mock(File.class);
        final FakeFile parent = new FakeFile("unexpected.parent");
        Mockito.when(file.getParentFile()).thenReturn(parent);
        String fileAbsolutePath = "/path/to/file";
        Mockito.when(file.getAbsolutePath()).thenReturn(fileAbsolutePath);
        factory = ShouldHaveNoParent.shouldHaveNoParent(file);
        actualMessage = factory.create(description, representation);
        expectedMessage = String.format(("[Test] " + (ShouldHaveNoParent.FILE_HAS_PARENT)), fileAbsolutePath, parent);
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void should_create_error_message_when_path_has_a_parent() {
        final Path path = Mockito.mock(Path.class);
        final Path parent = Mockito.mock(Path.class);
        Mockito.when(path.getParent()).thenReturn(parent);
        factory = ShouldHaveNoParent.shouldHaveNoParent(path);
        actualMessage = factory.create(description, representation);
        expectedMessage = String.format(("[Test] " + (ShouldHaveNoParent.PATH_HAS_PARENT)), path, parent);
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}

