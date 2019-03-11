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
import org.assertj.core.presentation.Representation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link ShouldExist#create(Description, Representation)}
 *
 * @author Yvonne Wang
 */
public class ShouldExist_create_Test {
    private TestDescription description;

    private Representation representation;

    private ErrorMessageFactory factory;

    private String actualMessage;

    private String expectedMessage;

    @Test
    public void should_create_error_message_for_File() {
        factory = ShouldExist.shouldExist(new FakeFile("xyz"));
        actualMessage = factory.create(description, representation);
        expectedMessage = String.format("[Test] %nExpecting file:%n  <xyz>%nto exist.");
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void should_create_error_message_for_Path_following_symbolic_links() {
        final Path actual = Mockito.mock(Path.class);
        factory = ShouldExist.shouldExist(actual);
        actualMessage = factory.create(description, representation);
        expectedMessage = String.format(("[Test] " + (ShouldExist.PATH_SHOULD_EXIST)), actual);
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void should_create_error_message_for_Path_not_following_symbolic_links() {
        final Path actual = Mockito.mock(Path.class);
        factory = ShouldExist.shouldExistNoFollowLinks(actual);
        actualMessage = factory.create(description, representation);
        expectedMessage = String.format(("[Test] " + (ShouldExist.PATH_SHOULD_EXIST_NO_FOLLOW_LINKS)), actual);
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}

