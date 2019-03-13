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
package org.assertj.core.internal.files;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.Diff;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.TextFileWriter;
import org.assertj.core.util.diff.Delta;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Diff#diff(File, String, java.nio.charset.Charset)}</code>.
 *
 * @author Olivier Michallat
 */
public class Diff_diff_File_String_Test {
    private static Diff diff;

    private static TextFileWriter writer;

    private File actual;

    @Test
    public void should_return_empty_diff_list_if_file_and_string_have_equal_content() throws IOException {
        String[] content = Arrays.array("line0", "line1");
        Diff_diff_File_String_Test.writer.write(actual, content);
        String expected = String.format("line0%nline1");
        List<Delta<String>> diffs = Diff_diff_File_String_Test.diff.diff(actual, expected, Charset.defaultCharset());
        Assertions.assertThat(diffs).isEmpty();
    }

    @Test
    public void should_return_diffs_if_file_and_string_do_not_have_equal_content() throws IOException {
        Diff_diff_File_String_Test.writer.write(actual, StandardCharsets.UTF_8, "Touch?");
        String expected = "Touch?";
        List<Delta<String>> diffs = Diff_diff_File_String_Test.diff.diff(actual, expected, StandardCharsets.ISO_8859_1);
        Assertions.assertThat(diffs).hasSize(1);
        Assertions.assertThat(diffs.get(0)).hasToString(String.format(("Changed content at line 1:%n" + ((("expecting:%n" + "  [\"Touch\u00e9\"]%n") + "but was:%n") + "  [\"Touch\u00c3\u00a9\"]%n"))));
    }

    @Test
    public void should_return_diffs_if_content_of_actual_is_shorter_than_content_of_expected() throws IOException {
        Diff_diff_File_String_Test.writer.write(actual, "line_0");
        String expected = String.format("line_0%nline_1");
        List<Delta<String>> diffs = Diff_diff_File_String_Test.diff.diff(actual, expected, Charset.defaultCharset());
        Assertions.assertThat(diffs).hasSize(1);
        Assertions.assertThat(diffs.get(0)).hasToString(String.format(("Missing content at line 2:%n" + "  [\"line_1\"]%n")));
    }

    @Test
    public void should_return_diffs_if_content_of_actual_is_longer_than_content_of_expected() throws IOException {
        Diff_diff_File_String_Test.writer.write(actual, "line_0", "line_1");
        String expected = "line_0";
        List<Delta<String>> diffs = Diff_diff_File_String_Test.diff.diff(actual, expected, Charset.defaultCharset());
        Assertions.assertThat(diffs).hasSize(1);
        Assertions.assertThat(diffs.get(0)).hasToString(String.format(("Extra content at line 2:%n" + "  [\"line_1\"]%n")));
    }
}

