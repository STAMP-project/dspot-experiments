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
 * Tests for {@link ShouldHaveParent#shouldHaveParent(File, File)} and {@link ShouldHaveParent#shouldHaveParent(Path, Path)}
 *
 * @author Jean-Christophe Gay
 * @author Francis Galiegue
 */
public class ShouldHaveParent_create_Test {
    private final File expectedFileParent = new FakeFile("expected.parent");

    private final Path expectedPathParent = Mockito.mock(Path.class);

    private TestDescription description;

    private Representation representation;

    private ErrorMessageFactory factory;

    private String actualMessage;

    private String expectedMessage;

    @Test
    public void should_create_error_message_when_file_has_no_parent() {
        final File actual = Mockito.spy(new FakeFile("actual"));
        Mockito.when(actual.getParentFile()).thenReturn(null);
        factory = ShouldHaveParent.shouldHaveParent(actual, expectedFileParent);
        actualMessage = factory.create(description, representation);
        expectedMessage = String.format(("[Test] " + (ShouldHaveParent.FILE_NO_PARENT)), actual, expectedFileParent);
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void should_create_error_message_when_file_does_not_have_expected_parent() {
        final File actual = Mockito.spy(new FakeFile("actual"));
        final FakeFile actualParent = new FakeFile("not.expected.parent");
        Mockito.when(actual.getParentFile()).thenReturn(actualParent);
        factory = ShouldHaveParent.shouldHaveParent(actual, expectedFileParent);
        actualMessage = factory.create(description, representation);
        expectedMessage = String.format(("[Test] " + (ShouldHaveParent.FILE_NOT_EXPECTED_PARENT)), actual, expectedFileParent, actualParent);
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void should_create_error_message_when_path_has_no_parent() {
        final Path actual = Mockito.mock(Path.class);
        factory = ShouldHaveParent.shouldHaveParent(actual, expectedPathParent);
        actualMessage = factory.create(description, representation);
        expectedMessage = String.format(("[Test] " + (ShouldHaveParent.PATH_NO_PARENT)), actual, expectedPathParent);
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void should_create_error_message_when_path_does_not_have_expected_parent() {
        final Path actual = Mockito.mock(Path.class);
        final Path actualParent = Mockito.mock(Path.class);
        factory = ShouldHaveParent.shouldHaveParent(actual, actualParent, expectedPathParent);
        actualMessage = factory.create(description, representation);
        expectedMessage = String.format(("[Test] " + (ShouldHaveParent.PATH_NOT_EXPECTED_PARENT)), actual, expectedPathParent, actualParent);
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}

