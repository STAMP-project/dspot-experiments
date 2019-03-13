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
package org.assertj.core.api.file;


import java.io.File;
import java.nio.charset.Charset;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.FileAssertBaseTest;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link FileAssert#hasSameContentAs(java.io.File)}</code>.
 *
 * @author Yvonne Wang
 */
public class FileAssert_hasSameContentAs_Test extends FileAssertBaseTest {
    private static File expected;

    @Test
    public void should_use_charset_specified_by_usingCharset_to_read_actual_file_content() throws Exception {
        Charset turkishCharset = Charset.forName("windows-1254");
        File actual = createDeleteOnExitTempFileWithContent("Ger?ek", turkishCharset);
        File expected = createDeleteOnExitTempFileWithContent("Ger?ek", Charset.defaultCharset());
        Assertions.assertThat(actual).usingCharset(turkishCharset).hasSameContentAs(expected);
    }

    @Test
    public void should_allow_charset_to_be_specified_for_reading_expected_file_content() throws Exception {
        Charset turkishCharset = Charset.forName("windows-1254");
        File actual = createDeleteOnExitTempFileWithContent("Ger?ek", Charset.defaultCharset());
        File expected = createDeleteOnExitTempFileWithContent("Ger?ek", turkishCharset);
        Assertions.assertThat(actual).hasSameContentAs(expected, turkishCharset);
    }
}

