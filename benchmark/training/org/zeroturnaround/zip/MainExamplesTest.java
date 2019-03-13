/**
 * Copyright (C) 2012 ZeroTurnaround LLC <support@zeroturnaround.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.zeroturnaround.zip;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import junit.framework.TestCase;
import org.zeroturnaround.zip.commons.FileUtils;


public final class MainExamplesTest extends TestCase {
    /* Unpacking */
    public static final String DEMO_ZIP = "src/test/resources/demo.zip";

    public static final String DUPLICATE_ZIP = "src/test/resources/duplicate.zip";

    public static final String DEMO_COPY_ZIP = "src/test/resources/demo-copy.zip";

    public static final String FOO_TXT = "foo.txt";

    public static void testContains() {
        boolean exists = ZipUtil.containsEntry(new File(MainExamplesTest.DEMO_ZIP), MainExamplesTest.FOO_TXT);
        TestCase.assertTrue(exists);
    }

    public static void testUnpackEntryImMemory() {
        byte[] bytes = ZipUtil.unpackEntry(new File(MainExamplesTest.DEMO_ZIP), MainExamplesTest.FOO_TXT);
        TestCase.assertEquals(bytes.length, 12);
    }

    public static void testUnpackEntry() throws IOException {
        File tmpFile = File.createTempFile("prefix", "suffix");
        ZipUtil.unpackEntry(new File(MainExamplesTest.DEMO_ZIP), MainExamplesTest.FOO_TXT, tmpFile);
        TestCase.assertTrue(((tmpFile.length()) > 0));
    }

    public static void testUnpack() throws IOException {
        File tmpDir = File.createTempFile("prefix", "suffix");
        tmpDir.delete();
        tmpDir.mkdir();
        ZipUtil.unpack(new File(MainExamplesTest.DEMO_ZIP), tmpDir);
        File fooFile = new File(tmpDir, MainExamplesTest.FOO_TXT);
        TestCase.assertTrue(fooFile.exists());
    }

    public static void testUnpackInPlace() throws Exception {
        File demoFile = new File(MainExamplesTest.DEMO_ZIP);
        File outDir = File.createTempFile("prefix", "suffix");
        outDir.delete();
        outDir.mkdir();
        File outFile = new File(outDir, "demo");
        FileOutputStream fio = new FileOutputStream(outFile);
        // so the zip file will be outDir/demo <- this is a zip archive
        FileUtils.copy(demoFile, fio);
        // close the stream so that locks on the file can be released (on windows, not doing this prevents the file from being moved)
        fio.close();
        // we explode the zip archive
        ZipUtil.explode(outFile);
        // we expect the outDir/demo/foo.txt to exist now
        TestCase.assertTrue(new File(outFile, MainExamplesTest.FOO_TXT).exists());
    }

    /* Comparison */
    public static void testEntryEquals() {
        boolean equals = ZipUtil.entryEquals(new File(MainExamplesTest.DEMO_ZIP), new File(MainExamplesTest.DEMO_COPY_ZIP), MainExamplesTest.FOO_TXT);
        TestCase.assertTrue(equals);
    }

    public static void testEntryEqualsDifferentNames() {
        boolean equals = ZipUtil.entryEquals(new File(MainExamplesTest.DEMO_ZIP), new File(MainExamplesTest.DEMO_COPY_ZIP), "foo1.txt", "foo2.txt");
        TestCase.assertTrue(equals);
    }

    public void testArchiveEquals() {
        boolean result = ZipUtil.archiveEquals(new File(MainExamplesTest.DEMO_ZIP), new File(MainExamplesTest.DEMO_COPY_ZIP));
        TestCase.assertTrue(result);
    }

    public void testArchiveEqualsNo() {
        boolean result = ZipUtil.archiveEquals(new File(MainExamplesTest.DEMO_ZIP), new File(MainExamplesTest.DUPLICATE_ZIP));
        TestCase.assertFalse(result);
    }
}

