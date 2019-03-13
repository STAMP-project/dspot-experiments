/**
 * Copyright 2014 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.utils;


import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FileIOUtilsTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private File sourceDir;

    private File destDir;

    private File baseDir;

    @Test
    public void testDumpNumberToFileAndReadFromFile() throws IOException {
        final String fileName = "number";
        final long num = 94127;
        final File fileToDump = dumpNumberToTempFile(fileName, num);
        assertThat(FileIOUtils.readNumberFromFile(fileToDump.toPath())).isEqualTo(num);
    }

    @Test
    public void testDumpNumberToExistingFile() throws IOException {
        final String fileName = "number";
        final long firstNum = 94127;
        final long secondNum = 94128;
        dumpNumberToTempFile(fileName, firstNum);
        assertThatThrownBy(() -> dumpNumberToTempFile(fileName, secondNum)).isInstanceOf(IOException.class).hasMessageContaining("already exists");
    }

    @Test
    public void testHardlinkCopy() throws IOException {
        final int hardLinkCount = FileIOUtils.createDeepHardlink(this.sourceDir, this.destDir);
        assertThat(areDirsEqual(this.sourceDir, this.destDir, true)).isTrue();
        FileUtils.deleteDirectory(this.destDir);
        assertThat(hardLinkCount).isEqualTo(5);
        assertThat(areDirsEqual(this.baseDir, this.sourceDir, true)).isTrue();
    }

    @Test
    public void testHardlinkCopyNonSource() {
        assertThatThrownBy(() -> {
            FileIOUtils.createDeepHardlink(new File(this.sourceDir, "idonotexist"), this.destDir);
        }).isInstanceOf(IOException.class);
    }

    @Test
    public void testAsciiUTF() throws IOException {
        final String foreignText = "abcdefghijklmnopqrstuvwxyz";
        final byte[] utf8ByteArray = createUTF8ByteArray(foreignText);
        final int length = utf8ByteArray.length;
        System.out.println(((((("char length:" + (foreignText.length())) + " utf8BytesLength:") + (utf8ByteArray.length)) + " for:") + foreignText));
        final Pair<Integer, Integer> pair = FileIOUtils.getUtf8Range(utf8ByteArray, 1, (length - 6));
        System.out.println(("Pair :" + (pair.toString())));
        final String recreatedString = new String(utf8ByteArray, 1, (length - 6), "UTF-8");
        System.out.println(("recreatedString:" + recreatedString));
        final String correctString = new String(utf8ByteArray, pair.getFirst(), pair.getSecond(), "UTF-8");
        System.out.println(("correctString:" + correctString));
        Assert.assertEquals(pair, new Pair(1, 20));
        // Two characters stripped from this.
        Assert.assertEquals(correctString.length(), ((foreignText.length()) - 6));
    }

    @Test
    public void testForeignUTF() throws IOException {
        final String foreignText = "?????, ? ??? ??????";
        final byte[] utf8ByteArray = createUTF8ByteArray(foreignText);
        final int length = utf8ByteArray.length;
        System.out.println(((((("char length:" + (foreignText.length())) + " utf8BytesLength:") + (utf8ByteArray.length)) + " for:") + foreignText));
        final Pair<Integer, Integer> pair = FileIOUtils.getUtf8Range(utf8ByteArray, 1, (length - 6));
        System.out.println(("Pair :" + (pair.toString())));
        final String recreatedString = new String(utf8ByteArray, 1, (length - 6), "UTF-8");
        System.out.println(("recreatedString:" + recreatedString));
        String correctString = new String(utf8ByteArray, pair.getFirst(), pair.getSecond(), "UTF-8");
        System.out.println(("correctString:" + correctString));
        Assert.assertEquals(pair, new Pair(3, 40));
        // Two characters stripped from this.
        Assert.assertEquals(correctString.length(), ((foreignText.length()) - 3));
        // Testing mixed bytes
        final String mixedText = "abc?????, ? ??? ??????";
        final byte[] mixedBytes = createUTF8ByteArray(mixedText);
        final Pair<Integer, Integer> pair2 = FileIOUtils.getUtf8Range(mixedBytes, 1, (length - 4));
        correctString = new String(mixedBytes, pair2.getFirst(), pair2.getSecond(), "UTF-8");
        System.out.println(("correctString:" + correctString));
        Assert.assertEquals(pair2, new Pair(1, 45));
        // Two characters stripped from this.
        Assert.assertEquals(correctString.length(), ((mixedText.length()) - 3));
    }
}

