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
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.BinaryDiff;
import org.assertj.core.internal.BinaryDiffResult;
import org.assertj.core.util.TextFileWriter;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link BinaryDiff#diff(java.io.File, byte[])}</code>.
 *
 * @author Olivier Michallat
 * @author Joel Costigliola
 */
public class BinaryDiff_diff_File_byteArray_Test {
    private static BinaryDiff binaryDiff;

    private static TextFileWriter writer;

    private File actual;

    private byte[] expected;

    @Test
    public void should_return_no_diff_if_file_and_array_have_equal_content() throws IOException {
        BinaryDiff_diff_File_byteArray_Test.writer.write(actual, "test");
        // Note: writer inserts a new line after each line so we need it in our expected content
        expected = ("test" + (System.lineSeparator())).getBytes();
        BinaryDiffResult result = BinaryDiff_diff_File_byteArray_Test.binaryDiff.diff(actual, expected);
        Assertions.assertThat(result.hasNoDiff()).isTrue();
    }

    @Test
    public void should_return_diff_if_inputstreams_differ_on_one_byte() throws IOException {
        BinaryDiff_diff_File_byteArray_Test.writer.write(actual, "test");
        expected = ("fest" + (System.lineSeparator())).getBytes();
        BinaryDiffResult result = BinaryDiff_diff_File_byteArray_Test.binaryDiff.diff(actual, expected);
        Assertions.assertThat(result.offset).isEqualTo(0);
        Assertions.assertThat(result.actual).isEqualTo("0x74");
        Assertions.assertThat(result.expected).isEqualTo("0x66");
    }

    @Test
    public void should_return_diff_if_actual_is_shorter() throws IOException {
        BinaryDiff_diff_File_byteArray_Test.writer.write(actual, "foo");
        expected = (("foo" + (System.lineSeparator())) + "bar").getBytes();
        BinaryDiffResult result = BinaryDiff_diff_File_byteArray_Test.binaryDiff.diff(actual, expected);
        Assertions.assertThat(result.offset).isEqualTo((3 + (System.lineSeparator().length())));
        Assertions.assertThat(result.actual).isEqualTo("EOF");
        Assertions.assertThat(result.expected).isEqualTo("0x62");
    }

    @Test
    public void should_return_diff_if_expected_is_shorter() throws IOException {
        BinaryDiff_diff_File_byteArray_Test.writer.write(actual, "foobar");
        expected = "foo".getBytes();
        BinaryDiffResult result = BinaryDiff_diff_File_byteArray_Test.binaryDiff.diff(actual, expected);
        Assertions.assertThat(result.offset).isEqualTo(3);
        Assertions.assertThat(result.actual).isEqualTo("0x62");
        Assertions.assertThat(result.expected).isEqualTo("EOF");
    }
}

