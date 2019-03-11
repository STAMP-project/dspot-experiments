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
import org.assertj.core.description.TextDescription;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class ShouldBeSymbolicLink_create_Test {
    @Test
    public void should_create_error_message() {
        final Path actual = Mockito.mock(Path.class);
        String actualMessage = ShouldBeSymbolicLink.shouldBeSymbolicLink(actual).create(new TextDescription("Test"));
        Assertions.assertThat(actualMessage).isEqualTo(String.format(("[Test] " + (ShouldBeSymbolicLink.SHOULD_BE_SYMBOLIC_LINK)), actual));
    }
}

