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
package org.apache.zookeeper.server.persistence;


import FileTxnSnapLog.DatadirException;
import FileTxnSnapLog.LogDirContentCheckException;
import FileTxnSnapLog.SnapDirContentCheckException;
import ZooDefs.OpCode;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.jute.Record;
import org.apache.zookeeper.server.Request;
import org.apache.zookeeper.txn.SetDataTxn;
import org.apache.zookeeper.txn.TxnHeader;
import org.junit.Assert;
import org.junit.Test;


public class FileTxnSnapLogTest {
    private File tmpDir;

    private File logDir;

    private File snapDir;

    private File logVersionDir;

    private File snapVersionDir;

    /**
     * Test verifies the auto creation of log dir and snap dir.
     * Sets "zookeeper.datadir.autocreate" to true.
     */
    @Test
    public void testWithAutoCreateDataDir() throws IOException {
        Assert.assertFalse("log directory already exists", logDir.exists());
        Assert.assertFalse("snapshot directory already exists", snapDir.exists());
        FileTxnSnapLog fileTxnSnapLog = createFileTxnSnapLogWithAutoCreateDataDir(logDir, snapDir, "true");
        Assert.assertTrue(logDir.exists());
        Assert.assertTrue(snapDir.exists());
        Assert.assertTrue(fileTxnSnapLog.getDataDir().exists());
        Assert.assertTrue(fileTxnSnapLog.getSnapDir().exists());
    }

    /**
     * Test verifies server should fail when log dir or snap dir doesn't exist.
     * Sets "zookeeper.datadir.autocreate" to false.
     */
    @Test(expected = DatadirException.class)
    public void testWithoutAutoCreateDataDir() throws Exception {
        Assert.assertFalse("log directory already exists", logDir.exists());
        Assert.assertFalse("snapshot directory already exists", snapDir.exists());
        try {
            createFileTxnSnapLogWithAutoCreateDataDir(logDir, snapDir, "false");
        } catch (FileTxnSnapLog e) {
            Assert.assertFalse(logDir.exists());
            Assert.assertFalse(snapDir.exists());
            // rethrow exception
            throw e;
        }
        Assert.fail("Expected exception from FileTxnSnapLog");
    }

    @Test
    public void testAutoCreateDB() throws IOException {
        Assert.assertTrue("cannot create log directory", logDir.mkdir());
        Assert.assertTrue("cannot create snapshot directory", snapDir.mkdir());
        File initFile = new File(logDir, "initialize");
        Assert.assertFalse("initialize file already exists", initFile.exists());
        Map<Long, Integer> sessions = new ConcurrentHashMap<>();
        attemptAutoCreateDB(logDir, snapDir, sessions, "false", (-1L));
        attemptAutoCreateDB(logDir, snapDir, sessions, "true", 0L);
        Assert.assertTrue("cannot create initialize file", initFile.createNewFile());
        attemptAutoCreateDB(logDir, snapDir, sessions, "false", 0L);
    }

    @Test
    public void testGetTxnLogSyncElapsedTime() throws IOException {
        FileTxnSnapLog fileTxnSnapLog = createFileTxnSnapLogWithAutoCreateDataDir(logDir, snapDir, "true");
        TxnHeader hdr = new TxnHeader(1, 1, 1, 1, OpCode.setData);
        Record txn = new SetDataTxn("/foo", new byte[0], 1);
        Request req = new Request(0, 0, 0, hdr, txn, 0);
        try {
            fileTxnSnapLog.append(req);
            fileTxnSnapLog.commit();
            long syncElapsedTime = fileTxnSnapLog.getTxnLogElapsedSyncTime();
            Assert.assertNotEquals("Did not update syncElapsedTime!", (-1L), syncElapsedTime);
        } finally {
            fileTxnSnapLog.close();
        }
    }

    @Test
    public void testDirCheckWithCorrectFiles() throws IOException {
        twoDirSetupWithCorrectFiles();
        try {
            createFileTxnSnapLogWithNoAutoCreateDataDir(logDir, snapDir);
        } catch (FileTxnSnapLog | FileTxnSnapLog e) {
            Assert.fail("Should not throw ContentCheckException.");
        }
    }

    @Test
    public void testDirCheckWithSingleDirSetup() throws IOException {
        singleDirSetupWithCorrectFiles();
        try {
            createFileTxnSnapLogWithNoAutoCreateDataDir(logDir, logDir);
        } catch (FileTxnSnapLog | FileTxnSnapLog e) {
            Assert.fail("Should not throw ContentCheckException.");
        }
    }

    @Test(expected = LogDirContentCheckException.class)
    public void testDirCheckWithSnapFilesInLogDir() throws IOException {
        twoDirSetupWithCorrectFiles();
        // add snapshot files to the log version dir
        createSnapshotFile(logVersionDir, 3);
        createSnapshotFile(logVersionDir, 4);
        createFileTxnSnapLogWithNoAutoCreateDataDir(logDir, snapDir);
    }

    @Test(expected = SnapDirContentCheckException.class)
    public void testDirCheckWithLogFilesInSnapDir() throws IOException {
        twoDirSetupWithCorrectFiles();
        // add transaction log files to the snap version dir
        createLogFile(snapVersionDir, 3);
        createLogFile(snapVersionDir, 4);
        createFileTxnSnapLogWithNoAutoCreateDataDir(logDir, snapDir);
    }
}

