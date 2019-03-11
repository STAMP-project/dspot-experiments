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
package org.apache.zookeeper.test;


import ZooDefs.OpCode;
import ZooDefs.OpCode.create;
import ZooDefs.OpCode.multi;
import java.io.File;
import java.io.FileInputStream;
import org.apache.jute.BinaryInputArchive;
import org.apache.jute.Record;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.common.Time;
import org.apache.zookeeper.server.DataNode;
import org.apache.zookeeper.server.DataTree;
import org.apache.zookeeper.server.persistence.FileHeader;
import org.apache.zookeeper.server.persistence.FileTxnLog;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.txn.CreateTxn;
import org.apache.zookeeper.txn.TxnHeader;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoadFromLogNoServerTest extends ZKTestCase {
    protected static final Logger LOG = LoggerFactory.getLogger(LoadFromLogNoServerTest.class);

    /**
     * For ZOOKEEPER-1046. Verify if cversion and pzxid if incremented
     * after create/delete failure during restore.
     */
    @Test
    public void testTxnFailure() throws Exception {
        long count = 1;
        File tmpDir = ClientBase.createTmpDir();
        FileTxnSnapLog logFile = new FileTxnSnapLog(tmpDir, tmpDir);
        DataTree dt = new DataTree();
        dt.createNode("/test", new byte[0], null, 0, (-1), 1, 1);
        for (count = 1; count <= 3; count++) {
            dt.createNode(("/test/" + count), new byte[0], null, 0, (-1), count, Time.currentElapsedTime());
        }
        DataNode zk = dt.getNode("/test");
        // Make create to fail, then verify cversion.
        LoadFromLogNoServerTest.LOG.info((("Attempting to create " + "/test/") + (count - 1)));
        doOp(logFile, create, ("/test/" + (count - 1)), dt, zk, (-1));
        LoadFromLogNoServerTest.LOG.info((("Attempting to create " + "/test/") + (count - 1)));
        doOp(logFile, create, ("/test/" + (count - 1)), dt, zk, ((zk.stat.getCversion()) + 1));
        LoadFromLogNoServerTest.LOG.info((("Attempting to create " + "/test/") + (count - 1)));
        doOp(logFile, multi, ("/test/" + (count - 1)), dt, zk, ((zk.stat.getCversion()) + 1));
        LoadFromLogNoServerTest.LOG.info((("Attempting to create " + "/test/") + (count - 1)));
        doOp(logFile, multi, ("/test/" + (count - 1)), dt, zk, (-1));
        // Make delete fo fail, then verify cversion.
        // this doesn't happen anymore, we only set the cversion on create
        // LOG.info("Attempting to delete " + "/test/" + (count + 1));
        // doOp(logFile, OpCode.delete, "/test/" + (count + 1), dt, zk);
    }

    /**
     * Simulates ZOOKEEPER-1069 and verifies that flush() before padLogFile
     * fixes it.
     */
    @Test
    public void testPad() throws Exception {
        File tmpDir = ClientBase.createTmpDir();
        FileTxnLog txnLog = new FileTxnLog(tmpDir);
        TxnHeader txnHeader = new TxnHeader(43981, 291, 291, Time.currentElapsedTime(), OpCode.create);
        Record txn = new CreateTxn("/Test", new byte[0], null, false, 1);
        txnLog.append(txnHeader, txn);
        FileInputStream in = new FileInputStream((((tmpDir.getPath()) + "/log.") + (Long.toHexString(txnHeader.getZxid()))));
        BinaryInputArchive ia = BinaryInputArchive.getArchive(in);
        FileHeader header = new FileHeader();
        header.deserialize(ia, "fileheader");
        LoadFromLogNoServerTest.LOG.info(((("Received magic : " + (header.getMagic())) + " Expected : ") + (FileTxnLog.TXNLOG_MAGIC)));
        Assert.assertTrue("Missing magic number ", ((header.getMagic()) == (FileTxnLog.TXNLOG_MAGIC)));
    }
}

