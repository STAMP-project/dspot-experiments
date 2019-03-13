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
import java.io.InputStream;
import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.DigestDiff;
import org.assertj.core.internal.TestDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class ShouldHaveDigest_create_Test {
    private static final TestDescription TEST_DESCRIPTION = new TestDescription("TEST");

    private DigestDiff diff;

    @Test
    public void should_create_error_message_with_File() {
        // GIVEN
        File actual = new FakeFile("actual.png");
        // WHEN
        String message = ShouldHaveDigest.shouldHaveDigest(actual, diff).create(ShouldHaveDigest_create_Test.TEST_DESCRIPTION, StandardRepresentation.STANDARD_REPRESENTATION);
        // THEN
        Assertions.assertThat(message).isEqualTo(String.format((((((((((("[TEST] %n" + "Expecting File ") + actual) + " MD5 digest to be:%n") + "  <\"") + (diff.getExpected())) + "\">%n") + "but was:%n") + "  <\"") + (diff.getActual())) + "\">")));
    }

    @Test
    public void should_create_error_message_with_Path() {
        // GIVEN
        Path actual = Mockito.mock(Path.class);
        // WHEN
        String message = ShouldHaveDigest.shouldHaveDigest(actual, diff).create(ShouldHaveDigest_create_Test.TEST_DESCRIPTION, StandardRepresentation.STANDARD_REPRESENTATION);
        // THEN
        Assertions.assertThat(message).isEqualTo(String.format((((((((((("[TEST] %n" + "Expecting Path ") + actual) + " MD5 digest to be:%n") + "  <\"") + (diff.getExpected())) + "\">%n") + "but was:%n") + "  <\"") + (diff.getActual())) + "\">")));
    }

    @Test
    public void should_create_error_message_with_InputStream() {
        // GIVEN
        InputStream actual = Mockito.mock(InputStream.class);
        // WHEN
        String message = ShouldHaveDigest.shouldHaveDigest(actual, diff).create(ShouldHaveDigest_create_Test.TEST_DESCRIPTION, StandardRepresentation.STANDARD_REPRESENTATION);
        // THEN
        Assertions.assertThat(message).isEqualTo(String.format((((((((((("[TEST] %n" + "Expecting InputStream ") + actual) + " MD5 digest to be:%n") + "  <\"") + (diff.getExpected())) + "\">%n") + "but was:%n") + "  <\"") + (diff.getActual())) + "\">")));
    }
}

