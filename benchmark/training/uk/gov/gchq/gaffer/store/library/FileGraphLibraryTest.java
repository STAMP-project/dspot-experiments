/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.store.library;


import org.junit.Assert;
import org.junit.Test;


public class FileGraphLibraryTest extends AbstractGraphLibraryTest {
    private static final String TEST_FILE_PATH = "src/test/resources/graphLibrary";

    private static final String TEST_INVALID_FINAL_PATH = "inv@lidP@th";

    @Test
    public void shouldThrowExceptionWithInvalidPath() {
        // When / Then
        try {
            new FileGraphLibrary(FileGraphLibraryTest.TEST_INVALID_FINAL_PATH);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }
}

