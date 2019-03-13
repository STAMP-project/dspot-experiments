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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import junit.framework.TestCase;


public class CharsetTest extends TestCase {
    private static final File file = new File("src/test/resources/umlauts-o\u0308a\u0308s\u030c.zip");

    // See StackOverFlow post why I'm not using just unicode
    // http://stackoverflow.com/questions/6153345/different-utf8-encoding-in-filenames-os-x/6153713#6153713
    private static final List fileContents = new ArrayList() {
        {
            add("umlauts-o?a?s?/");
            add("umlauts-o\u0308a\u0308s\u030c/Ro\u0308mer.txt");// R?mer - but using the escape code that HFS uses

            add("umlauts-o\u0308a\u0308s\u030c/Raudja\u0308rv.txt");// Raudj?rv - but escape code from HFS

            add("umlauts-o\u0308a\u0308s\u030c/S\u030celajev.txt");// ?elajev - but escape code from HFS

        }
    };

    public void testIterateWithCharset() throws Exception {
        if (ignoreTestIfJava6()) {
            return;
        }
        FileInputStream fis = new FileInputStream(CharsetTest.file);
        ZipUtil.iterate(fis, new ZipEntryCallback() {
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                TestCase.assertTrue(zipEntry.getName(), CharsetTest.fileContents.contains(zipEntry.getName()));
            }
        }, Charset.forName("UTF8"));
    }

    public void testIterateWithEntryNamesAndCharset() throws Exception {
        if (ignoreTestIfJava6()) {
            return;
        }
        FileInputStream fis = new FileInputStream(CharsetTest.file);
        String[] entryNames = ((String[]) (CharsetTest.fileContents.toArray(new String[]{  })));
        ZipUtil.iterate(fis, entryNames, new ZipEntryCallback() {
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                TestCase.assertTrue(zipEntry.getName(), CharsetTest.fileContents.contains(zipEntry.getName()));
            }
        }, Charset.forName("UTF8"));
    }

    public void testZipFileGetEntriesWithCharset() throws Exception {
        if (ignoreTestIfJava6()) {
            return;
        }
        ZipFile zf = ZipFileUtil.getZipFile(CharsetTest.file, Charset.forName("UTF8"));
        Enumeration entries = zf.entries();
        while (entries.hasMoreElements()) {
            ZipEntry ze = ((ZipEntry) (entries.nextElement()));
            TestCase.assertTrue(ze.getName(), CharsetTest.fileContents.contains(ze.getName()));
        } 
    }

    /* I'm using a archive created on Windows 10. The files in the archive have
    umlauts in their name. The default encoding in compression is IBM437 (I didn't
    know that but found out from [1]. Unpacking this archive with any other encoding
    will result in wrong filenames (windows-1252) or Zip exception during the
    getEntry() or when opening the file.

    [1] http://stackoverflow.com/questions/1510791/how-to-create-zip-files-with-specific-encoding
     */
    public void testIterateExtractWithCharset() throws Exception {
        if (ignoreTestIfJava6()) {
            return;
        }
        final File src = new File("src/test/resources/windows-compressed.zip");
        FileInputStream inputStream = new FileInputStream(src);
        ZipUtil.iterate(inputStream, new ZipEntryCallback() {
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                if ((zipEntry.getName().indexOf("raud")) != (-1)) {
                    TestCase.assertEquals("windows-default-encoded/raudj?rv.txt", zipEntry.getName());
                } else {
                    TestCase.assertEquals("windows-default-encoded/r?mer.txt", zipEntry.getName());
                }
            }
        }, Charset.forName("IBM437"));
        inputStream.close();
    }

    /* If a charset is not specified for the unpack then the test will just fail. */
    public void testExtractWithCharset() throws Exception {
        if (ignoreTestIfJava6()) {
            return;
        }
        final File src = new File("src/test/resources/windows-compressed.zip");
        File tmpDir = Files.createTempDirectory("zt-zip-tests").toFile();
        ZipUtil.unpack(src, tmpDir, Charset.forName("IBM437"));
    }

    public void testExtractEntryWithCharset() throws Exception {
        if (ignoreTestIfJava6()) {
            return;
        }
        final File src = new File("src/test/resources/windows-compressed.zip");
        byte[] bytes = ZipUtil.unpackEntry(src, "windows-default-encoded/r?mer.txt", Charset.forName("IBM437"));
        TestCase.assertTrue(((bytes.length) > 0));
    }

    /* If a charset is not specified for the unpack then the test will just fail. */
    public void testExtractWithCharsetUsingStream() throws Exception {
        if (ignoreTestIfJava6()) {
            return;
        }
        final File src = new File("src/test/resources/windows-compressed.zip");
        FileInputStream inputStream = new FileInputStream(src);
        File tmpDir = Files.createTempDirectory("zt-zip-tests").toFile();
        ZipUtil.unpack(inputStream, tmpDir, Charset.forName("IBM437"));
        inputStream.close();
    }
}

