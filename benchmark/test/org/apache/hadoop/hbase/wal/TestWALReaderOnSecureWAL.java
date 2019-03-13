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
/**
 * imports for things that haven't moved from regionserver.wal yet.
 */
package org.apache.hadoop.hbase.wal;


import WAL.Reader;
import WALProvider.Writer;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.regionserver.wal.ProtobufLogWriter;
import org.apache.hadoop.hbase.regionserver.wal.SecureProtobufLogReader;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.zookeeper.ZKSplitLog;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Test that verifies WAL written by SecureProtobufLogWriter is not readable by ProtobufLogReader
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestWALReaderOnSecureWAL {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALReaderOnSecureWAL.class);

    static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    final byte[] value = Bytes.toBytes("Test value");

    private static final String WAL_ENCRYPTION = "hbase.regionserver.wal.encryption";

    @Rule
    public TestName currentTest = new TestName();

    @Test
    public void testWALReaderOnSecureWALWithKeyValues() throws Exception {
        testSecureWALInternal(false);
    }

    @Test
    public void testWALReaderOnSecureWALWithOffheapKeyValues() throws Exception {
        testSecureWALInternal(true);
    }

    @Test
    public void testSecureWALReaderOnWAL() throws Exception {
        Configuration conf = TestWALReaderOnSecureWAL.TEST_UTIL.getConfiguration();
        conf.setClass("hbase.regionserver.hlog.reader.impl", SecureProtobufLogReader.class, Reader.class);
        conf.setClass("hbase.regionserver.hlog.writer.impl", ProtobufLogWriter.class, Writer.class);
        conf.setBoolean(TestWALReaderOnSecureWAL.WAL_ENCRYPTION, false);
        FileSystem fs = TestWALReaderOnSecureWAL.TEST_UTIL.getTestFileSystem();
        final WALFactory wals = new WALFactory(conf, ServerName.valueOf(currentTest.getMethodName(), 16010, System.currentTimeMillis()).toString());
        Path walPath = writeWAL(wals, currentTest.getMethodName(), false);
        // Ensure edits are plaintext
        long length = fs.getFileStatus(walPath).getLen();
        FSDataInputStream in = fs.open(walPath);
        byte[] fileData = new byte[((int) (length))];
        IOUtils.readFully(in, fileData);
        in.close();
        Assert.assertTrue("Cells should be plaintext", Bytes.contains(fileData, value));
        // Confirm the WAL can be read back by SecureProtobufLogReader
        try {
            WAL.Reader reader = wals.createReader(TestWALReaderOnSecureWAL.TEST_UTIL.getTestFileSystem(), walPath);
            reader.close();
        } catch (IOException ioe) {
            Assert.assertFalse(true);
        }
        FileStatus[] listStatus = fs.listStatus(walPath.getParent());
        Path rootdir = FSUtils.getRootDir(conf);
        try {
            WALSplitter s = new WALSplitter(wals, conf, rootdir, fs, null, null);
            s.splitLogFile(listStatus[0], null);
            Path file = new Path(ZKSplitLog.getSplitLogDir(rootdir, listStatus[0].getPath().getName()), "corrupt");
            Assert.assertTrue((!(fs.exists(file))));
        } catch (IOException ioe) {
            Assert.assertTrue("WAL should have been processed", false);
        }
        wals.close();
    }
}

