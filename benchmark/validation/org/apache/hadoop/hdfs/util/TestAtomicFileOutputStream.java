/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.util;


import com.google.common.base.Joiner;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.PathUtils;
import org.apache.hadoop.test.PlatformAssumptions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestAtomicFileOutputStream {
    private static final String TEST_STRING = "hello world";

    private static final String TEST_STRING_2 = "goodbye world";

    private static final File TEST_DIR = PathUtils.getTestDir(TestAtomicFileOutputStream.class);

    private static final File DST_FILE = new File(TestAtomicFileOutputStream.TEST_DIR, "test.txt");

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Test case where there is no existing file
     */
    @Test
    public void testWriteNewFile() throws IOException {
        OutputStream fos = new AtomicFileOutputStream(TestAtomicFileOutputStream.DST_FILE);
        Assert.assertFalse(TestAtomicFileOutputStream.DST_FILE.exists());
        fos.write(TestAtomicFileOutputStream.TEST_STRING.getBytes());
        fos.flush();
        Assert.assertFalse(TestAtomicFileOutputStream.DST_FILE.exists());
        fos.close();
        Assert.assertTrue(TestAtomicFileOutputStream.DST_FILE.exists());
        String readBackData = DFSTestUtil.readFile(TestAtomicFileOutputStream.DST_FILE);
        Assert.assertEquals(TestAtomicFileOutputStream.TEST_STRING, readBackData);
    }

    /**
     * Test case where there is no existing file
     */
    @Test
    public void testOverwriteFile() throws IOException {
        Assert.assertTrue("Creating empty dst file", TestAtomicFileOutputStream.DST_FILE.createNewFile());
        OutputStream fos = new AtomicFileOutputStream(TestAtomicFileOutputStream.DST_FILE);
        Assert.assertTrue("Empty file still exists", TestAtomicFileOutputStream.DST_FILE.exists());
        fos.write(TestAtomicFileOutputStream.TEST_STRING.getBytes());
        fos.flush();
        // Original contents still in place
        Assert.assertEquals("", DFSTestUtil.readFile(TestAtomicFileOutputStream.DST_FILE));
        fos.close();
        // New contents replace original file
        String readBackData = DFSTestUtil.readFile(TestAtomicFileOutputStream.DST_FILE);
        Assert.assertEquals(TestAtomicFileOutputStream.TEST_STRING, readBackData);
    }

    /**
     * Test case where the flush() fails at close time - make sure
     * that we clean up after ourselves and don't touch any
     * existing file at the destination
     */
    @Test
    public void testFailToFlush() throws IOException {
        // Create a file at destination
        FileOutputStream fos = new FileOutputStream(TestAtomicFileOutputStream.DST_FILE);
        fos.write(TestAtomicFileOutputStream.TEST_STRING_2.getBytes());
        fos.close();
        OutputStream failingStream = createFailingStream();
        failingStream.write(TestAtomicFileOutputStream.TEST_STRING.getBytes());
        try {
            failingStream.close();
            Assert.fail("Close didn't throw exception");
        } catch (IOException ioe) {
            // expected
        }
        // Should not have touched original file
        Assert.assertEquals(TestAtomicFileOutputStream.TEST_STRING_2, DFSTestUtil.readFile(TestAtomicFileOutputStream.DST_FILE));
        Assert.assertEquals("Temporary file should have been cleaned up", TestAtomicFileOutputStream.DST_FILE.getName(), Joiner.on(",").join(TestAtomicFileOutputStream.TEST_DIR.list()));
    }

    @Test
    public void testFailToRename() throws IOException {
        PlatformAssumptions.assumeWindows();
        OutputStream fos = null;
        try {
            fos = new AtomicFileOutputStream(TestAtomicFileOutputStream.DST_FILE);
            fos.write(TestAtomicFileOutputStream.TEST_STRING.getBytes());
            FileUtil.setWritable(TestAtomicFileOutputStream.TEST_DIR, false);
            exception.expect(IOException.class);
            exception.expectMessage("failure in native rename");
            try {
                fos.close();
            } finally {
                fos = null;
            }
        } finally {
            IOUtils.cleanup(null, fos);
            FileUtil.setWritable(TestAtomicFileOutputStream.TEST_DIR, true);
        }
    }
}

