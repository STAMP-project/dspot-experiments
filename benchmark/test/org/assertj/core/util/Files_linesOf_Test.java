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
package org.assertj.core.util;


import java.io.File;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link Files#linesOf(File, Charset)} and {@link Files#linesOf(File, String)}.
 *
 * @author Mateusz Haligowski
 */
public class Files_linesOf_Test {
    private static final File SAMPLE_UNIX_FILE = new File("src/test/resources/utf8.txt");

    private static final File SAMPLE_WIN_FILE = new File("src/test/resources/utf8_win.txt");

    private static final File SAMPLE_MAC_FILE = new File("src/test/resources/utf8_mac.txt");

    private static final List<String> EXPECTED_CONTENT = Lists.newArrayList("A text file encoded in UTF-8, with diacritics:", "? ?");

    public static final String UTF_8 = "UTF-8";

    @Test
    public void should_throw_exception_when_charset_is_null() {
        Charset charset = null;
        Assertions.assertThatNullPointerException().isThrownBy(() -> linesOf(SAMPLE_UNIX_FILE, charset));
    }

    @Test
    public void should_throw_exception_if_charset_name_does_not_exist() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> linesOf(new File("test"), "Klingon"));
    }

    @Test
    public void should_throw_exception_if_file_not_found() {
        File missingFile = new File("missing.txt");
        Assertions.assertThat(missingFile).doesNotExist();
        Assertions.assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> linesOf(missingFile, Charset.defaultCharset()));
    }

    @Test
    public void should_pass_if_unix_file_is_split_into_lines() {
        Assertions.assertThat(Files.linesOf(Files_linesOf_Test.SAMPLE_UNIX_FILE, StandardCharsets.UTF_8)).isEqualTo(Files_linesOf_Test.EXPECTED_CONTENT);
    }

    @Test
    public void should_pass_if_unix_file_is_split_into_lines_using_charset() {
        Assertions.assertThat(Files.linesOf(Files_linesOf_Test.SAMPLE_UNIX_FILE, Files_linesOf_Test.UTF_8)).isEqualTo(Files_linesOf_Test.EXPECTED_CONTENT);
    }

    @Test
    public void should_pass_if_windows_file_is_split_into_lines() {
        Assertions.assertThat(Files.linesOf(Files_linesOf_Test.SAMPLE_WIN_FILE, StandardCharsets.UTF_8)).isEqualTo(Files_linesOf_Test.EXPECTED_CONTENT);
    }

    @Test
    public void should_pass_if_windows_file_is_split_into_lines_using_charset() {
        Assertions.assertThat(Files.linesOf(Files_linesOf_Test.SAMPLE_WIN_FILE, Files_linesOf_Test.UTF_8)).isEqualTo(Files_linesOf_Test.EXPECTED_CONTENT);
    }

    @Test
    public void should_pass_if_mac_file_is_split_into_lines() {
        Assertions.assertThat(Files.linesOf(Files_linesOf_Test.SAMPLE_MAC_FILE, StandardCharsets.UTF_8)).isEqualTo(Files_linesOf_Test.EXPECTED_CONTENT);
    }

    @Test
    public void should_pass_if_mac_file_is_split_into_lines_using_charset() {
        Assertions.assertThat(Files.linesOf(Files_linesOf_Test.SAMPLE_MAC_FILE, Files_linesOf_Test.UTF_8)).isEqualTo(Files_linesOf_Test.EXPECTED_CONTENT);
    }
}

