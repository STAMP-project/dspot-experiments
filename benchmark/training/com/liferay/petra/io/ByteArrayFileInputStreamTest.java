/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.petra.io;


import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 *
 *
 * @author Shuyang Zhou
 */
public class ByteArrayFileInputStreamTest {
    @ClassRule
    public static final CodeCoverageAssertor codeCoverageAssertor = CodeCoverageAssertor.INSTANCE;

    @Test
    public void testaAailable() throws Exception {
        // Uninitialized
        ByteArrayFileInputStream byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);
        Assert.assertEquals(0, byteArrayFileInputStream.available());
        // byte[]
        byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 2048);
        byteArrayFileInputStream.read();
        Assert.assertNotNull(ByteArrayFileInputStreamTest._dataField.get(byteArrayFileInputStream));
        Assert.assertNull(ByteArrayFileInputStreamTest._fileInputStreamField.get(byteArrayFileInputStream));
        Assert.assertEquals(1, ByteArrayFileInputStreamTest._indexField.getInt(byteArrayFileInputStream));
        Assert.assertEquals(1023, byteArrayFileInputStream.available());
        byteArrayFileInputStream.close();
        // FileInputStream
        byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);
        byteArrayFileInputStream.read();
        Assert.assertNull(ByteArrayFileInputStreamTest._dataField.get(byteArrayFileInputStream));
        FileInputStream fileInputStream = ((FileInputStream) (ByteArrayFileInputStreamTest._fileInputStreamField.get(byteArrayFileInputStream)));
        Assert.assertNotNull(fileInputStream);
        Assert.assertEquals(0, ByteArrayFileInputStreamTest._indexField.getInt(byteArrayFileInputStream));
        Assert.assertEquals(fileInputStream.available(), byteArrayFileInputStream.available());
        byteArrayFileInputStream.close();
    }

    @Test
    public void testBlockRead() throws IOException {
        // byte[]
        ByteArrayFileInputStream byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 2048);
        byte[] buffer = new byte[17];
        int index = 0;
        int length = 0;
        while ((length = byteArrayFileInputStream.read(buffer)) != (-1)) {
            for (int i = 0; i < length; i++) {
                Assert.assertEquals(((index++) & 255), ((buffer[i]) & 255));
            }
        } 
        byteArrayFileInputStream.close();
        byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 2048);
        // 0 length
        Assert.assertEquals(0, byteArrayFileInputStream.read(null, (-1), 0));
        buffer = new byte[48];
        index = 0;
        length = 0;
        while ((length = byteArrayFileInputStream.read(buffer, 16, 16)) != (-1)) {
            for (int i = 0; i < length; i++) {
                Assert.assertEquals(((index++) & 255), ((buffer[(i + 16)]) & 255));
            }
        } 
        byteArrayFileInputStream.close();
        // FileInputStream
        byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);
        buffer = new byte[17];
        index = 0;
        length = 0;
        while ((length = byteArrayFileInputStream.read(buffer)) != (-1)) {
            for (int i = 0; i < length; i++) {
                Assert.assertEquals(((index++) & 255), ((buffer[i]) & 255));
            }
        } 
        byteArrayFileInputStream.close();
        byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);
        // 0 length
        Assert.assertEquals(0, byteArrayFileInputStream.read(null, (-1), 0));
        buffer = new byte[48];
        index = 0;
        length = 0;
        while ((length = byteArrayFileInputStream.read(buffer, 16, 16)) != (-1)) {
            for (int i = 0; i < length; i++) {
                Assert.assertEquals(((index++) & 255), ((buffer[(i + 16)]) & 255));
            }
        } 
        byteArrayFileInputStream.close();
    }

    @Test
    public void testClose() throws Exception {
        // Do not delete on close
        ByteArrayFileInputStream byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);
        byteArrayFileInputStream.read();
        byteArrayFileInputStream.close();
        Assert.assertNull(ByteArrayFileInputStreamTest._dataField.get(byteArrayFileInputStream));
        Assert.assertNull(ByteArrayFileInputStreamTest._fileField.get(byteArrayFileInputStream));
        Assert.assertNull(ByteArrayFileInputStreamTest._fileInputStreamField.get(byteArrayFileInputStream));
        Assert.assertTrue(_testFile.exists());
        // Delete on close
        byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512, true);
        byteArrayFileInputStream.close();
        Assert.assertNull(ByteArrayFileInputStreamTest._dataField.get(byteArrayFileInputStream));
        Assert.assertNull(ByteArrayFileInputStreamTest._fileField.get(byteArrayFileInputStream));
        Assert.assertNull(ByteArrayFileInputStreamTest._fileInputStreamField.get(byteArrayFileInputStream));
        Assert.assertFalse(_testFile.exists());
        byteArrayFileInputStream.close();
    }

    @Test
    public void testConstructor() throws Exception {
        // File is a dir
        try {
            new ByteArrayFileInputStream(_testDir, 1024);
            Assert.fail();
        } catch (IllegalArgumentException iae) {
        }
        // File does not exist
        try {
            new ByteArrayFileInputStream(new File("No Such File"), 1024);
            Assert.fail();
        } catch (IllegalArgumentException iae) {
        }
        // Constructor 1
        ByteArrayFileInputStream byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);
        Assert.assertEquals(_testFile, ByteArrayFileInputStreamTest._fileField.get(byteArrayFileInputStream));
        Assert.assertEquals(1024, ByteArrayFileInputStreamTest._fileSizeField.getLong(byteArrayFileInputStream));
        Assert.assertEquals(512, ByteArrayFileInputStreamTest._thresholdField.getInt(byteArrayFileInputStream));
        Assert.assertFalse(ByteArrayFileInputStreamTest._deleteOnCloseField.getBoolean(byteArrayFileInputStream));
        // Constructor 2, do not delete on close
        byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512, false);
        Assert.assertEquals(_testFile, ByteArrayFileInputStreamTest._fileField.get(byteArrayFileInputStream));
        Assert.assertEquals(1024, ByteArrayFileInputStreamTest._fileSizeField.getLong(byteArrayFileInputStream));
        Assert.assertEquals(512, ByteArrayFileInputStreamTest._thresholdField.getInt(byteArrayFileInputStream));
        Assert.assertFalse(ByteArrayFileInputStreamTest._deleteOnCloseField.getBoolean(byteArrayFileInputStream));
        // Constructor 2, delete on close
        byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512, true);
        Assert.assertEquals(_testFile, ByteArrayFileInputStreamTest._fileField.get(byteArrayFileInputStream));
        Assert.assertEquals(1024, ByteArrayFileInputStreamTest._fileSizeField.getLong(byteArrayFileInputStream));
        Assert.assertEquals(512, ByteArrayFileInputStreamTest._thresholdField.getInt(byteArrayFileInputStream));
        Assert.assertTrue(ByteArrayFileInputStreamTest._deleteOnCloseField.getBoolean(byteArrayFileInputStream));
    }

    @Test
    public void testGetFile() throws Exception {
        ByteArrayFileInputStream byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);
        Assert.assertSame(_testFile, byteArrayFileInputStream.getFile());
        byteArrayFileInputStream.close();
    }

    @Test
    public void testMark() throws IOException {
        // byte[]
        ByteArrayFileInputStream byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 2048);
        Assert.assertTrue(byteArrayFileInputStream.markSupported());
        for (int i = 0; i < 512; i++) {
            Assert.assertEquals((i & 255), byteArrayFileInputStream.read());
        }
        byteArrayFileInputStream.mark(0);
        for (int i = 512; i < 1024; i++) {
            Assert.assertEquals((i & 255), byteArrayFileInputStream.read());
        }
        Assert.assertEquals((-1), byteArrayFileInputStream.read());
        // In memory reset to index 512
        byteArrayFileInputStream.reset();
        for (int i = 512; i < 1024; i++) {
            Assert.assertEquals((i & 255), byteArrayFileInputStream.read());
        }
        byteArrayFileInputStream.close();
        // FileInputStream
        byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);
        Assert.assertFalse(byteArrayFileInputStream.markSupported());
        for (int i = 0; i < 1024; i++) {
            Assert.assertEquals((i & 255), byteArrayFileInputStream.read());
        }
        Assert.assertEquals((-1), byteArrayFileInputStream.read());
        // FileInputStream reset to index 0
        byteArrayFileInputStream.reset();
        // Calling reset twice does not cause a NullPointerException
        byteArrayFileInputStream.reset();
        for (int i = 0; i < 1024; i++) {
            Assert.assertEquals((i & 255), byteArrayFileInputStream.read());
        }
        byteArrayFileInputStream.close();
    }

    @Test
    public void testSkip() throws IOException {
        // byte[]
        ByteArrayFileInputStream byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 2048);
        // Negative length
        Assert.assertEquals(0, byteArrayFileInputStream.skip((-1)));
        int count = 1024 / 17;
        for (int i = 0; i < count; i++) {
            Assert.assertEquals(17, byteArrayFileInputStream.skip(17));
        }
        Assert.assertEquals((1024 % 17), byteArrayFileInputStream.skip(17));
        Assert.assertEquals(0, byteArrayFileInputStream.skip(17));
        byteArrayFileInputStream.close();
        // FileInputStream
        byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);
        // 0 length
        Assert.assertEquals(0, byteArrayFileInputStream.skip(0));
        for (int i = 0; i < 1024; i++) {
            Assert.assertEquals(17, byteArrayFileInputStream.skip(17));
        }
        // Skip EOF
        byteArrayFileInputStream.skip(17);
        Assert.assertEquals((-1), byteArrayFileInputStream.read());
        byteArrayFileInputStream.close();
    }

    private static final Field _dataField = ReflectionTestUtil.getField(ByteArrayFileInputStream.class, "_data");

    private static final Field _deleteOnCloseField = ReflectionTestUtil.getField(ByteArrayFileInputStream.class, "_deleteOnClose");

    private static final Field _fileField = ReflectionTestUtil.getField(ByteArrayFileInputStream.class, "_file");

    private static final Field _fileInputStreamField = ReflectionTestUtil.getField(ByteArrayFileInputStream.class, "_fileInputStream");

    private static final Field _fileSizeField = ReflectionTestUtil.getField(ByteArrayFileInputStream.class, "_fileSize");

    private static final Field _indexField = ReflectionTestUtil.getField(ByteArrayFileInputStream.class, "_index");

    private static final Field _thresholdField = ReflectionTestUtil.getField(ByteArrayFileInputStream.class, "_threshold");

    private File _testDir;

    private File _testFile;
}

