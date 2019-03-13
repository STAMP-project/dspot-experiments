/**
 * Copyright (C) 2013 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.libimobiledevice;


import AfcClient.DEVICE_INFO_KEY_FS_BLOCK_SIZE;
import AfcClient.DEVICE_INFO_KEY_FS_FREE_BYTES;
import AfcClient.DEVICE_INFO_KEY_FS_TOTAL_BYTES;
import AfcClient.DEVICE_INFO_KEY_MODEL;
import AfcClient.FILE_INFO_KEY_LINK_TARGET;
import AfcClient.FILE_INFO_KEY_ST_IFMT;
import AfcClient.FILE_INFO_KEY_ST_SIZE;
import AfcError.AFC_E_DIR_NOT_EMPTY;
import AfcError.AFC_E_OBJECT_NOT_FOUND;
import AfcFileMode.AFC_FOPEN_RDONLY;
import AfcFileMode.AFC_FOPEN_WRONLY;
import AfcLinkType.AFC_SYMLINK;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link AfcClient}.
 */
public class AfcClientTest {
    static AfcClient client;

    static IDevice device;

    static LockdowndClient lockdowndClient;

    @Test
    public void testReadDirectory() {
        List<String> dirs = Arrays.asList(AfcClientTest.client.readDirectory("/"));
        System.out.println(dirs);
        Assert.assertFalse(dirs.isEmpty());
        Assert.assertTrue(dirs.contains("Books"));
        Assert.assertTrue(dirs.contains("DCIM"));
        Assert.assertTrue(dirs.contains("Safari"));
    }

    @Test
    public void testGetDeviceInfo() throws Exception {
        Map<String, String> devInfo = AfcClientTest.client.getDeviceInfo();
        Assert.assertFalse(devInfo.isEmpty());
        Assert.assertTrue(devInfo.containsKey(DEVICE_INFO_KEY_FS_BLOCK_SIZE));
        Assert.assertTrue(devInfo.containsKey(DEVICE_INFO_KEY_FS_FREE_BYTES));
        Assert.assertTrue(devInfo.containsKey(DEVICE_INFO_KEY_FS_TOTAL_BYTES));
        Assert.assertTrue(devInfo.containsKey(DEVICE_INFO_KEY_MODEL));
        Assert.assertEquals(devInfo.get(DEVICE_INFO_KEY_FS_BLOCK_SIZE), ("" + (AfcClientTest.client.getBlockSize())));
        Assert.assertEquals(devInfo.get(DEVICE_INFO_KEY_FS_FREE_BYTES), ("" + (AfcClientTest.client.getFreeBytes())));
        Assert.assertEquals(devInfo.get(DEVICE_INFO_KEY_FS_TOTAL_BYTES), ("" + (AfcClientTest.client.getTotalBytes())));
        Assert.assertEquals(devInfo.get(DEVICE_INFO_KEY_MODEL), ("" + (AfcClientTest.client.getModel())));
    }

    @Test
    public void testReadWriteRemove() throws Exception {
        byte[] buffer = new byte[4096];
        int n = 0;
        long seed = System.currentTimeMillis();
        int size = 1 << 20;
        long fd = 0;
        fd = AfcClientTest.client.fileOpen("/FOO.TXT", AFC_FOPEN_WRONLY);
        AfcClientTest.RandomInputStream ris = new AfcClientTest.RandomInputStream(size, seed);
        while ((n = ris.read(buffer)) != (-1)) {
            AfcClientTest.client.fileWrite(fd, buffer, 0, n);
        } 
        ris.close();
        AfcClientTest.client.fileClose(fd);
        Map<String, String> fileInfo = AfcClientTest.client.getFileInfo("/FOO.TXT");
        Assert.assertEquals(("" + size), fileInfo.get(FILE_INFO_KEY_ST_SIZE));
        Random random = new Random(seed);
        fd = AfcClientTest.client.fileOpen("/FOO.TXT", AFC_FOPEN_RDONLY);
        int totalRead = 0;
        while ((n = AfcClientTest.client.fileRead(fd, buffer, 0, buffer.length)) != (-1)) {
            for (int i = 0; i < n; i++) {
                Assert.assertEquals(AfcClientTest.nextCharAZ(random), ((buffer[i]) & 255));
            }
            totalRead += n;
        } 
        AfcClientTest.client.fileClose(fd);
        Assert.assertEquals(size, totalRead);
        AfcClientTest.client.removePath("/FOO.TXT");
        try {
            AfcClientTest.client.getFileInfo("/FOO.TXT");
            Assert.fail("LibIMobileDeviceException expected");
        } catch (LibIMobileDeviceException e) {
            Assert.assertEquals(AFC_E_OBJECT_NOT_FOUND.swigValue(), e.getErrorCode());
        }
    }

    @Test
    public void testWriteSpeed() throws Exception {
        byte[] buffer = new byte[64 * 1024];
        long fd = AfcClientTest.client.fileOpen("/FOO.DAT", AFC_FOPEN_WRONLY);
        int n = 0;
        long start = System.currentTimeMillis();
        while (n < (1 << 24)) {
            n += AfcClientTest.client.fileWrite(fd, buffer, 0, buffer.length);
        } 
        long duration = (System.currentTimeMillis()) - start;
        AfcClientTest.client.fileClose(fd);
        System.out.format("%d bytes written in %d ms (%f kB/s)\n", n, duration, ((n / 1024.0) / (duration / 1000.0)));
        AfcClientTest.client.removePath("/FOO.DAT");
    }

    @Test
    public void testMakeDirectory() throws Exception {
        AfcClientTest.client.makeDirectory("/FOO");
        AfcClientTest.client.makeDirectory("/FOO/BAR");
        AfcClientTest.client.makeDirectory("/FOO/BAR/WOOZ/BAZ");
        Assert.assertEquals("S_IFDIR", AfcClientTest.client.getFileInfo("/FOO/BAR/WOOZ/BAZ").get(FILE_INFO_KEY_ST_IFMT));
        try {
            AfcClientTest.client.removePath("/FOO");
            Assert.fail("LibIMobileDeviceException expected");
        } catch (LibIMobileDeviceException e) {
            Assert.assertEquals(AFC_E_DIR_NOT_EMPTY.swigValue(), e.getErrorCode());
        }
        AfcClientTest.client.removePath("/FOO", true);
        try {
            AfcClientTest.client.getFileInfo("/FOO");
            Assert.fail("LibIMobileDeviceException expected");
        } catch (LibIMobileDeviceException e) {
            Assert.assertEquals(AFC_E_OBJECT_NOT_FOUND.swigValue(), e.getErrorCode());
        }
    }

    @Test
    public void testMakeLink() throws Exception {
        try {
            AfcClientTest.client.makeDirectory("/FOOBAR");
            AfcClientTest.client.makeLink(AFC_SYMLINK, "../Safari", "/FOOBAR/KAZ");
            Assert.assertEquals("S_IFLNK", AfcClientTest.client.getFileInfo("/FOOBAR/KAZ").get(FILE_INFO_KEY_ST_IFMT));
            Assert.assertEquals("../Safari", AfcClientTest.client.getFileInfo("/FOOBAR/KAZ").get(FILE_INFO_KEY_LINK_TARGET));
        } finally {
            try {
                AfcClientTest.client.removePath("/FOOBAR", true);
            } catch (LibIMobileDeviceException e) {
            }
        }
    }

    @Test
    public void testUpload() throws Exception {
        Path dir = Files.createTempDirectory(AfcClientTest.class.getSimpleName());
        try {
            Path foo = Files.createDirectory(dir.resolve("foo"));
            Path bar = Files.createDirectory(foo.resolve("bar"));
            Path wooz = Files.createFile(bar.resolve("wooz"));
            Files.createSymbolicLink(foo.resolve("rooz"), foo.relativize(wooz));
            try {
                AfcClientTest.client.upload(foo.toFile(), "/baz");
                StringWriter sw = new StringWriter();
                list("/baz", "baz", "", new PrintWriter(sw));
                Assert.assertEquals(("baz/\n" + ((("  foo/\n" + "    bar/\n") + "      wooz 0 (S_IFREG)\n") + "    rooz -> bar/wooz\n")), sw.toString());
            } finally {
                AfcClientTest.client.removePath("/baz", true);
            }
        } finally {
            FileUtils.deleteDirectory(dir.toFile());
        }
    }

    @Test
    public void testUploadSpeed() throws Exception {
        Path dir = Files.createTempDirectory(AfcClientTest.class.getSimpleName());
        try {
            Path foo = Files.createFile(dir.resolve("foo"));
            byte[] data = new byte[1 << 24];
            Files.write(foo, data);
            long start = System.currentTimeMillis();
            try {
                AfcClientTest.client.upload(foo.toFile(), "/wooz");
            } finally {
                long duration = (System.currentTimeMillis()) - start;
                System.out.format("%d bytes uploaded in %d ms (%f kB/s)\n", data.length, duration, (((data.length) / 1024.0) / (duration / 1000.0)));
                AfcClientTest.client.removePath("/wooz", true);
            }
        } finally {
            FileUtils.deleteDirectory(dir.toFile());
        }
    }

    private static class RandomInputStream extends InputStream {
        private final int size;

        private final Random random;

        private int position = 0;

        RandomInputStream(int size, long seed) {
            this.size = size;
            random = new Random(seed);
        }

        @Override
        public int read() throws IOException {
            if ((position) < (size)) {
                (position)++;
                return AfcClientTest.nextCharAZ(random);
            }
            return -1;
        }
    }
}

