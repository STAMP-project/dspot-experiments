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
package org.apache.hadoop.hdfs.server.namenode;


import FSEditLogOp.MkdirOp;
import FSEditLogOp.OpInstanceCache;
import FSEditLogOpCodes.OP_ADD;
import FSEditLogOpCodes.OP_CLOSE;
import FSEditLogOpCodes.OP_SET_GENSTAMP_V1;
import HdfsServerConstants.INVALID_TXID;
import NameNodeLayoutVersion.CURRENT_LAYOUT_VERSION;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EnumMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.util.Holder;
import org.apache.hadoop.hdfs.web.URLConnectionFactory;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PathUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestEditLogFileInputStream {
    private static final Logger LOG = LoggerFactory.getLogger(TestEditLogFileInputStream.class);

    private static final byte[] FAKE_LOG_DATA = TestEditLog.HADOOP20_SOME_EDITS;

    private static final File TEST_DIR = PathUtils.getTestDir(TestEditLogFileInputStream.class);

    @Test
    public void testReadURL() throws Exception {
        HttpURLConnection conn = Mockito.mock(HttpURLConnection.class);
        Mockito.doReturn(new ByteArrayInputStream(TestEditLogFileInputStream.FAKE_LOG_DATA)).when(conn).getInputStream();
        Mockito.doReturn(HttpURLConnection.HTTP_OK).when(conn).getResponseCode();
        Mockito.doReturn(Integer.toString(TestEditLogFileInputStream.FAKE_LOG_DATA.length)).when(conn).getHeaderField("Content-Length");
        URLConnectionFactory factory = Mockito.mock(URLConnectionFactory.class);
        Mockito.doReturn(conn).when(factory).openConnection(Mockito.<URL>any(), ArgumentMatchers.anyBoolean());
        URL url = new URL("http://localhost/fakeLog");
        EditLogInputStream elis = EditLogFileInputStream.fromUrl(factory, url, INVALID_TXID, INVALID_TXID, false);
        // Read the edit log and verify that we got all of the data.
        EnumMap<FSEditLogOpCodes, Holder<Integer>> counts = FSImageTestUtil.countEditLogOpTypes(elis);
        Assert.assertThat(counts.get(OP_ADD).held, CoreMatchers.is(1));
        Assert.assertThat(counts.get(OP_SET_GENSTAMP_V1).held, CoreMatchers.is(1));
        Assert.assertThat(counts.get(OP_CLOSE).held, CoreMatchers.is(1));
        // Check that length header was picked up.
        Assert.assertEquals(TestEditLogFileInputStream.FAKE_LOG_DATA.length, elis.length());
        elis.close();
    }

    @Test
    public void testByteStringLog() throws Exception {
        ByteString bytes = ByteString.copyFrom(TestEditLogFileInputStream.FAKE_LOG_DATA);
        EditLogInputStream elis = EditLogFileInputStream.fromByteString(bytes, INVALID_TXID, INVALID_TXID, true);
        // Read the edit log and verify that all of the data is present
        EnumMap<FSEditLogOpCodes, Holder<Integer>> counts = FSImageTestUtil.countEditLogOpTypes(elis);
        Assert.assertThat(counts.get(OP_ADD).held, CoreMatchers.is(1));
        Assert.assertThat(counts.get(OP_SET_GENSTAMP_V1).held, CoreMatchers.is(1));
        Assert.assertThat(counts.get(OP_CLOSE).held, CoreMatchers.is(1));
        Assert.assertEquals(TestEditLogFileInputStream.FAKE_LOG_DATA.length, elis.length());
        elis.close();
    }

    /**
     * Regression test for HDFS-8965 which verifies that
     * FSEditLogFileInputStream#scanOp verifies Op checksums.
     */
    @Test(timeout = 60000)
    public void testScanCorruptEditLog() throws Exception {
        Configuration conf = new Configuration();
        File editLog = new File(GenericTestUtils.getTempPath("testCorruptEditLog"));
        TestEditLogFileInputStream.LOG.debug(("Creating test edit log file: " + editLog));
        EditLogFileOutputStream elos = new EditLogFileOutputStream(conf, editLog.getAbsoluteFile(), 8192);
        elos.create(CURRENT_LAYOUT_VERSION);
        FSEditLogOp.OpInstanceCache cache = new FSEditLogOp.OpInstanceCache();
        FSEditLogOp.MkdirOp mkdirOp = MkdirOp.getInstance(cache);
        mkdirOp.reset();
        mkdirOp.setRpcCallId(123);
        mkdirOp.setTransactionId(1);
        mkdirOp.setInodeId(789L);
        mkdirOp.setPath("/mydir");
        PermissionStatus perms = PermissionStatus.createImmutable("myuser", "mygroup", FsPermission.createImmutable(((short) (511))));
        mkdirOp.setPermissionStatus(perms);
        elos.write(mkdirOp);
        mkdirOp.reset();
        mkdirOp.setRpcCallId(456);
        mkdirOp.setTransactionId(2);
        mkdirOp.setInodeId(123L);
        mkdirOp.setPath("/mydir2");
        perms = PermissionStatus.createImmutable("myuser", "mygroup", FsPermission.createImmutable(((short) (438))));
        mkdirOp.setPermissionStatus(perms);
        elos.write(mkdirOp);
        elos.setReadyToFlush();
        elos.flushAndSync(false);
        elos.close();
        long fileLen = editLog.length();
        TestEditLogFileInputStream.LOG.debug(((("Corrupting last 4 bytes of edit log file " + editLog) + ", whose length is ") + fileLen));
        RandomAccessFile rwf = new RandomAccessFile(editLog, "rw");
        rwf.seek((fileLen - 4));
        int b = rwf.readInt();
        rwf.seek((fileLen - 4));
        rwf.writeInt((b + 1));
        rwf.close();
        EditLogFileInputStream elis = new EditLogFileInputStream(editLog);
        Assert.assertEquals(CURRENT_LAYOUT_VERSION, elis.getVersion(true));
        Assert.assertEquals(1, elis.scanNextOp());
        TestEditLogFileInputStream.LOG.debug(("Read transaction 1 from " + editLog));
        try {
            elis.scanNextOp();
            Assert.fail("Expected scanNextOp to fail when op checksum was corrupt.");
        } catch (IOException e) {
            TestEditLogFileInputStream.LOG.debug(("Caught expected checksum error when reading corrupt " + "transaction 2"), e);
            GenericTestUtils.assertExceptionContains("Transaction is corrupt.", e);
        }
        elis.close();
    }
}

