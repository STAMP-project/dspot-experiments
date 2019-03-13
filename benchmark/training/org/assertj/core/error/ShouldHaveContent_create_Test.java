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


import java.nio.charset.Charset;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.description.TextDescription;
import org.assertj.core.util.Lists;
import org.assertj.core.util.diff.Delta;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class ShouldHaveContent_create_Test {
    private static final String DIFF = "diff !";

    @Test
    public void should_create_error_message() {
        final FakeFile file = new FakeFile("xyz");
        @SuppressWarnings("unchecked")
        Delta<String> delta = Mockito.mock(Delta.class);
        Mockito.when(delta.toString()).thenReturn(ShouldHaveContent_create_Test.DIFF);
        List<Delta<String>> diffs = Lists.newArrayList(delta);
        ErrorMessageFactory factory = ShouldHaveContent.shouldHaveContent(file, Charset.defaultCharset(), diffs);
        String message = factory.create(new TextDescription("Test"), CONFIGURATION_PROVIDER.representation());
        Assertions.assertThat(message).isEqualTo(String.format((("[Test] %n" + (("File:%n" + "  <xyz>%n") + "read with charset <%s> does not have the expected content:%n%n")) + (ShouldHaveContent_create_Test.DIFF)), Charset.defaultCharset().name()));
    }
}

