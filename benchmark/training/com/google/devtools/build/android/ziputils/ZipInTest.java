/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.android.ziputils;


import ZipEntry.Status;
import com.google.devtools.build.android.ziputils.ZipIn.ZipEntry;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipInputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link ZipIn}.
 */
@RunWith(JUnit4.class)
public class ZipInTest {
    private static final int ENTRY_COUNT = 1000;

    private FakeFileSystem fileSystem;

    /**
     * Test of endOfCentralDirectory method, of class ZipIn.
     */
    @Test
    public void testEndOfCentralDirectory() throws Exception {
        String filename = "test.zip";
        byte[] bytes;
        ByteBuffer buffer;
        String comment;
        int commentLen;
        int offset;
        ZipIn zipIn;
        EndOfCentralDirectory result;
        String subcase;
        // Find it, even if it's the only useful thing in the file.
        subcase = " EOCD found it, ";
        bytes = new byte[]{ 80, 75, 5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        fileSystem.addFile(filename, bytes);
        zipIn = newZipIn(filename);
        result = zipIn.endOfCentralDirectory();
        assertWithMessage((subcase + "found")).that(result).isNotNull();
        subcase = " EOCD not there at all, ";
        bytes = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        fileSystem.addFile(filename, bytes);
        zipIn = newZipIn(filename);
        try {
            zipIn.endOfCentralDirectory();
            Assert.fail((subcase + "expected IllegalStateException"));
        } catch (Exception ex) {
            assertWithMessage((subcase + "caught exception")).that(ex.getClass()).isSameAs(IllegalStateException.class);
        }
        // If we can't read it, it's not there
        subcase = " EOCD too late to read, ";
        bytes = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 80, 75, 5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        fileSystem.addFile(filename, bytes);
        zipIn = newZipIn(filename);
        try {
            zipIn.endOfCentralDirectory();
            Assert.fail((subcase + "expected IndexOutOfBoundsException"));
        } catch (Exception ex) {
            assertWithMessage((subcase + "caught exception")).that(ex.getClass()).isSameAs(IndexOutOfBoundsException.class);
        }
        // Current implementation doesn't know to scan past a bad EOCD record.
        // I'm not sure if it should.
        subcase = " EOCD good hidden by bad, ";
        bytes = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 80, 75, 5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 80, 75, 5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        fileSystem.addFile(filename, bytes);
        zipIn = newZipIn(filename);
        try {
            zipIn.endOfCentralDirectory();
            Assert.fail((subcase + "expected IndexOutOfBoundsException"));
        } catch (Exception ex) {
            assertWithMessage((subcase + "caught exception")).that(ex.getClass()).isSameAs(IndexOutOfBoundsException.class);
        }
        // Minimal format checking here, assuming the EndOfDirectoryTest class
        // test for that.
        subcase = " EOCD truncated comment, ";
        bytes = new byte[100];
        buffer = ByteBuffer.wrap(bytes);
        comment = "optional file comment";
        commentLen = comment.getBytes(StandardCharsets.UTF_8).length;
        offset = ((bytes.length) - (ZipInputStream.ENDHDR)) - commentLen;
        buffer.position(offset);
        EndOfCentralDirectory.view(buffer, comment);
        byte[] truncated = Arrays.copyOf(bytes, ((bytes.length) - 5));
        fileSystem.addFile(filename, truncated);
        zipIn = newZipIn(filename);
        try {
            // not sure this is the exception we want!
            zipIn.endOfCentralDirectory();
            Assert.fail((subcase + "expected IllegalArgumentException"));
        } catch (Exception ex) {
            assertWithMessage((subcase + "caught exception")).that(ex.getClass()).isSameAs(IllegalArgumentException.class);
        }
        subcase = " EOCD no comment, ";
        bytes = new byte[100];
        buffer = ByteBuffer.wrap(bytes);
        comment = null;
        commentLen = 0;
        offset = ((bytes.length) - (ZipInputStream.ENDHDR)) - commentLen;
        buffer.position(offset);
        EndOfCentralDirectory.view(buffer, comment);
        fileSystem.addFile(filename, bytes);
        zipIn = newZipIn(filename);
        result = zipIn.endOfCentralDirectory();
        assertWithMessage((subcase + "found")).that(result).isNotNull();
        assertWithMessage((subcase + "comment")).that(result.getComment()).isEqualTo("");
        assertWithMessage((subcase + "marker")).that(((int) (result.get(EndOfCentralDirectory.ENDSIG)))).isEqualTo(ZipInputStream.ENDSIG);
        subcase = " EOCD comment, ";
        bytes = new byte[100];
        buffer = ByteBuffer.wrap(bytes);
        comment = "optional file comment";
        commentLen = comment.getBytes(StandardCharsets.UTF_8).length;
        offset = ((bytes.length) - (ZipInputStream.ENDHDR)) - commentLen;
        buffer.position(offset);
        EndOfCentralDirectory.view(buffer, comment);
        assertWithMessage((subcase + "setup")).that(new String(bytes, ((bytes.length) - commentLen), commentLen, StandardCharsets.UTF_8)).isEqualTo(comment);
        fileSystem.addFile(filename, bytes);
        zipIn = newZipIn(filename);
        result = zipIn.endOfCentralDirectory();
        assertWithMessage((subcase + "found")).that(result).isNotNull();
        assertWithMessage((subcase + "comment")).that(result.getComment()).isEqualTo(comment);
        assertWithMessage((subcase + "marker")).that(((int) (result.get(EndOfCentralDirectory.ENDSIG)))).isEqualTo(ZipInputStream.ENDSIG);
        subcase = " EOCD extra data, ";
        bytes = new byte[100];
        buffer = ByteBuffer.wrap(bytes);
        comment = null;
        commentLen = 0;
        offset = (((bytes.length) - (ZipInputStream.ENDHDR)) - commentLen) - 10;
        buffer.position(offset);
        EndOfCentralDirectory.view(buffer, comment);
        fileSystem.addFile(filename, bytes);
        zipIn = newZipIn(filename);
        result = zipIn.endOfCentralDirectory();
        assertWithMessage((subcase + "found")).that(result).isNotNull();
        assertWithMessage((subcase + "comment")).that(result.getComment()).isEqualTo("");
        assertWithMessage((subcase + "marker")).that(((int) (result.get(EndOfCentralDirectory.ENDSIG)))).isEqualTo(ZipInputStream.ENDSIG);
    }

    /**
     * Test of centralDirectory method, of class ZipIn.
     */
    @Test
    public void testCentralDirectory() throws Exception {
        String filename = "test.zip";
        ByteBuffer buffer;
        int offset;
        ZipIn zipIn;
        String subcase;
        subcase = " EOCD extra data, ";
        String commonName = "thisIsNotNormal.txt";
        int filenameLen = commonName.getBytes(StandardCharsets.UTF_8).length;
        int count = ZipInTest.ENTRY_COUNT;
        int dirEntry = ZipInputStream.CENHDR;
        int before = count;
        int between = 0;// implementation doesn't tolerate data between dir entries, does the spec?

        int after = 20;
        int eocd = ZipInputStream.ENDHDR;
        int total = (((before + (count * (dirEntry + filenameLen))) + ((count - 1) * between)) + after) + eocd;
        byte[] bytes = new byte[total];
        offset = before;
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                offset += between;
            }
            buffer = ByteBuffer.wrap(bytes, offset, ((bytes.length) - offset));
            DirectoryEntry.view(buffer, commonName, null, null).set(DirectoryEntry.CENHOW, ((short) (8))).set(DirectoryEntry.CENSIZ, before).set(DirectoryEntry.CENLEN, (2 * before)).set(DirectoryEntry.CENOFF, i);// Not valid of course, but we're only testing central dir parsing.

            // and there are currently no checks in the parser to see if offset makes sense.
            offset += dirEntry + filenameLen;
        }
        offset += after;
        buffer = ByteBuffer.wrap(bytes, offset, ((bytes.length) - offset));
        EndOfCentralDirectory.view(buffer, null).set(EndOfCentralDirectory.ENDOFF, before).set(EndOfCentralDirectory.ENDSIZ, ((offset - before) - after)).set(EndOfCentralDirectory.ENDTOT, ((short) (count))).set(EndOfCentralDirectory.ENDSUB, ((short) (count)));
        fileSystem.addFile(filename, bytes);
        zipIn = newZipIn(filename);
        CentralDirectory result = zipIn.centralDirectory();
        assertWithMessage((subcase + "found")).that(result).isNotNull();
        List<DirectoryEntry> list = result.list();
        assertWithMessage((subcase + "size")).that(list.size()).isEqualTo(count);
        for (int i = 0; i < (list.size()); i++) {
            assertWithMessage((((subcase + "offset check[") + i) + "]")).that(list.get(i).get(DirectoryEntry.CENOFF)).isEqualTo(i);
        }
    }

    /**
     * Test of scanEntries method, of class ZipIn.
     */
    @Test
    public void testScanEntries() throws Exception {
        int count = (ZipInTest.ENTRY_COUNT) * 100;
        String filename = "test.jar";
        ZipFileBuilder builder = new ZipFileBuilder();
        for (int i = 0; i < count; i++) {
            builder.add((("pkg/f" + i) + ".class"), "All day long");
        }
        builder.create(filename);
        final ZipIn zipIn = newZipIn(filename);
        zipIn.scanEntries(new EntryHandler() {
            int count = 0;

            @Override
            public void handle(ZipIn in, LocalFileHeader header, DirectoryEntry dirEntry, ByteBuffer data) throws IOException {
                assertThat(in).isSameAs(zipIn);
                String filename = ("pkg/f" + (count)) + ".class";
                assertThat(header.getFilename()).isEqualTo(filename);
                assertThat(dirEntry.getFilename()).isEqualTo(filename);
                (count)++;
            }
        });
    }

    /**
     * Test of nextHeaderFrom method, of class ZipIn.
     */
    @Test
    public void testNextHeaderFrom_long() throws Exception {
        int count = ZipInTest.ENTRY_COUNT;
        String filename = "test.jar";
        ZipFileBuilder builder = new ZipFileBuilder();
        for (int i = 0; i < count; i++) {
            builder.add((("pkg/f" + i) + ".class"), "All day long");
        }
        builder.create(filename);
        final ZipIn zipIn = newZipIn(filename);
        zipIn.endOfCentralDirectory();
        count = 0;
        int offset = 0;
        LocalFileHeader header;
        do {
            header = zipIn.nextHeaderFrom(offset);
            String name = ("pkg/f" + count) + ".class";
            if (header != null) {
                assertThat(header.getFilename()).isEqualTo(name);
                count++;
                offset = ((int) (header.fileOffset())) + 4;
            }
        } while (header != null );
        assertThat(count).isEqualTo(ZipInTest.ENTRY_COUNT);
    }

    /**
     * Test of nextHeaderFrom method, of class ZipIn.
     */
    @Test
    public void testNextHeaderFrom_DirectoryEntry() throws Exception {
        int count = ZipInTest.ENTRY_COUNT;
        String filename = "test.jar";
        ZipFileBuilder builder = new ZipFileBuilder();
        for (int i = 0; i < count; i++) {
            builder.add((("pkg/f" + i) + ".class"), "All day long");
        }
        builder.create(filename);
        final ZipIn zipIn = newZipIn(filename);
        zipIn.centralDirectory();
        List<DirectoryEntry> list = zipIn.centralDirectory().list();
        count = 0;
        String name;
        LocalFileHeader header = zipIn.nextHeaderFrom(null);
        for (DirectoryEntry dirEntry : list) {
            name = ("pkg/f" + count) + ".class";
            assertThat(dirEntry.getFilename()).isEqualTo(name);
            assertThat(header.getFilename()).isEqualTo(name);
            header = zipIn.nextHeaderFrom(dirEntry);
            count++;
        }
        assertThat(header).isNull();
    }

    /**
     * Test of localHeaderFor method, of class ZipIn.
     */
    @Test
    public void testLocalHeaderFor() throws Exception {
        int count = ZipInTest.ENTRY_COUNT;
        String filename = "test.jar";
        ZipFileBuilder builder = new ZipFileBuilder();
        for (int i = 0; i < count; i++) {
            builder.add((("pkg/f" + i) + ".class"), "All day long");
        }
        builder.create(filename);
        final ZipIn zipIn = newZipIn(filename);
        zipIn.centralDirectory();
        List<DirectoryEntry> list = zipIn.centralDirectory().list();
        count = 0;
        String name;
        LocalFileHeader header;
        for (DirectoryEntry dirEntry : list) {
            name = ("pkg/f" + count) + ".class";
            header = zipIn.localHeaderFor(dirEntry);
            assertThat(dirEntry.getFilename()).isEqualTo(name);
            assertThat(header.getFilename()).isEqualTo(name);
            count++;
        }
    }

    /**
     * Test of localHeaderAt method, of class ZipIn.
     */
    @Test
    public void testLocalHeaderAt() throws Exception {
        int count = ZipInTest.ENTRY_COUNT;
        String filename = "test.jar";
        ZipFileBuilder builder = new ZipFileBuilder();
        for (int i = 0; i < count; i++) {
            builder.add((("pkg/f" + i) + ".class"), "All day long");
        }
        builder.create(filename);
        final ZipIn zipIn = newZipIn(filename);
        zipIn.centralDirectory();
        List<DirectoryEntry> list = zipIn.centralDirectory().list();
        count = 0;
        String name;
        LocalFileHeader header;
        for (DirectoryEntry dirEntry : list) {
            name = ("pkg/f" + count) + ".class";
            header = zipIn.localHeaderAt(dirEntry.get(DirectoryEntry.CENOFF));
            assertThat(dirEntry.getFilename()).isEqualTo(name);
            assertThat(header.getFilename()).isEqualTo(name);
            count++;
        }
    }

    /**
     * Test of nextFrom method, of class ZipIn.
     */
    @Test
    public void testNextFrom_long() throws Exception {
        int count = ZipInTest.ENTRY_COUNT;
        String filename = "test.jar";
        ZipFileBuilder builder = new ZipFileBuilder();
        for (int i = 0; i < count; i++) {
            builder.add((("pkg/f" + i) + ".class"), "All day long");
        }
        builder.create(filename);
        final ZipIn zipIn = newZipIn(filename);
        zipIn.centralDirectory();
        count = 0;
        int offset = 0;
        ZipEntry zipEntry;
        do {
            zipEntry = zipIn.nextFrom(offset);
            String name = ("pkg/f" + count) + ".class";
            if ((zipEntry.getCode()) != (Status.ENTRY_NOT_FOUND)) {
                assertThat(zipEntry.getHeader()).isNotNull();
                assertThat(zipEntry.getDirEntry()).isNotNull();
                assertThat(zipEntry.getHeader().getFilename()).isEqualTo(name);
                assertThat(zipEntry.getDirEntry().getFilename()).isEqualTo(name);
                count++;
                offset = ((int) (zipEntry.getHeader().fileOffset())) + 4;
            }
        } while ((zipEntry.getCode()) != (Status.ENTRY_NOT_FOUND) );
        assertThat(count).isEqualTo(ZipInTest.ENTRY_COUNT);
    }

    /**
     * Test of nextFrom method, of class ZipIn.
     */
    @Test
    public void testNextFrom_DirectoryEntry() throws Exception {
        int count = ZipInTest.ENTRY_COUNT;
        String filename = "test.jar";
        ZipFileBuilder builder = new ZipFileBuilder();
        for (int i = 0; i < count; i++) {
            builder.add((("pkg/f" + i) + ".class"), "All day long");
        }
        builder.create(filename);
        final ZipIn zipIn = newZipIn(filename);
        zipIn.centralDirectory();
        List<DirectoryEntry> list = zipIn.centralDirectory().list();
        count = 0;
        String name;
        ZipEntry zipEntry = zipIn.nextFrom(null);
        for (DirectoryEntry dirEntry : list) {
            if ((zipEntry.getCode()) == (Status.ENTRY_NOT_FOUND)) {
                break;
            }
            name = ("pkg/f" + count) + ".class";
            assertThat(zipEntry.getHeader()).isNotNull();
            assertThat(zipEntry.getDirEntry()).isNotNull();
            assertThat(zipEntry.getHeader().getFilename()).isEqualTo(name);
            assertThat(zipEntry.getDirEntry().getFilename()).isEqualTo(name);
            zipEntry = zipIn.nextFrom(dirEntry);
            count++;
        }
        assertThat(count).isEqualTo(ZipInTest.ENTRY_COUNT);
    }

    /**
     * Test of entryAt method, of class ZipIn.
     */
    @Test
    public void testEntryAt() throws Exception {
        int count = ZipInTest.ENTRY_COUNT;
        String filename = "test.jar";
        ZipFileBuilder builder = new ZipFileBuilder();
        for (int i = 0; i < count; i++) {
            builder.add((("pkg/f" + i) + ".class"), "All day long");
        }
        builder.create(filename);
        final ZipIn zipIn = newZipIn(filename);
        zipIn.centralDirectory();
        List<DirectoryEntry> list = zipIn.centralDirectory().list();
        count = 0;
        String name;
        ZipEntry zipEntry;
        for (DirectoryEntry dirEntry : list) {
            zipEntry = zipIn.entryAt(dirEntry.get(DirectoryEntry.CENOFF));
            name = ("pkg/f" + count) + ".class";
            assertThat(zipEntry.getHeader()).isNotNull();
            assertThat(zipEntry.getDirEntry()).isNotNull();
            assertThat(zipEntry.getHeader().getFilename()).isEqualTo(name);
            assertThat(zipEntry.getDirEntry().getFilename()).isEqualTo(name);
            count++;
        }
        assertThat(count).isEqualTo(ZipInTest.ENTRY_COUNT);
    }

    /**
     * Test of entryWith method, of class ZipIn.
     */
    @Test
    public void testEntryWith() throws Exception {
        int count = ZipInTest.ENTRY_COUNT;
        String filename = "test.jar";
        ZipFileBuilder builder = new ZipFileBuilder();
        for (int i = 0; i < count; i++) {
            builder.add((("pkg/f" + i) + ".class"), "All day long");
        }
        builder.create(filename);
        final ZipIn zipIn = newZipIn(filename);
        zipIn.centralDirectory();
        count = 0;
        int offset = 0;
        LocalFileHeader header;
        do {
            header = zipIn.nextHeaderFrom(offset);
            String name = ("pkg/f" + count) + ".class";
            if (header != null) {
                ZipEntry zipEntry = zipIn.entryWith(header);
                assertThat(zipEntry.getDirEntry()).isNotNull();
                assertThat(zipEntry.getHeader()).isSameAs(header);
                assertThat(zipEntry.getHeader().getFilename()).isEqualTo(name);
                assertThat(zipEntry.getDirEntry().getFilename()).isEqualTo(name);
                assertThat(header.getFilename()).isEqualTo(name);
                count++;
                offset = ((int) (header.fileOffset())) + 4;
            }
        } while (header != null );
        assertThat(count).isEqualTo(ZipInTest.ENTRY_COUNT);
    }
}

