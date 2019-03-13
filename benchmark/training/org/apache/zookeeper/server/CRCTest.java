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
package org.apache.zookeeper.server;


import CreateMode.PERSISTENT;
import Ids.OPEN_ACL_UNSAFE;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.persistence.FileSnap;
import org.apache.zookeeper.server.persistence.FileTxnLog;
import org.apache.zookeeper.server.persistence.TxnLog.TxnIterator;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CRCTest extends ZKTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(CRCTest.class);

    private static final String HOSTPORT = "127.0.0.1:" + (PortAssignment.unique());

    /**
     * test checksums for the logs and snapshots.
     * the reader should fail on reading
     * a corrupt snapshot and a corrupt log
     * file
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testChecksums() throws Exception {
        File tmpDir = ClientBase.createTmpDir();
        ClientBase.setupTestEnv();
        ZooKeeperServer zks = new ZooKeeperServer(tmpDir, tmpDir, 3000);
        SyncRequestProcessor.setSnapCount(150);
        final int PORT = Integer.parseInt(CRCTest.HOSTPORT.split(":")[1]);
        ServerCnxnFactory f = ServerCnxnFactory.createFactory(PORT, (-1));
        f.startup(zks);
        CRCTest.LOG.info("starting up the zookeeper server .. waiting");
        Assert.assertTrue("waiting for server being up", ClientBase.waitForServerUp(CRCTest.HOSTPORT, ClientBase.CONNECTION_TIMEOUT));
        ZooKeeper zk = ClientBase.createZKClient(CRCTest.HOSTPORT);
        try {
            for (int i = 0; i < 2000; i++) {
                zk.create(("/crctest- " + i), ("/crctest- " + i).getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
            }
        } finally {
            zk.close();
        }
        f.shutdown();
        zks.shutdown();
        Assert.assertTrue("waiting for server down", ClientBase.waitForServerDown(CRCTest.HOSTPORT, ClientBase.CONNECTION_TIMEOUT));
        File versionDir = new File(tmpDir, "version-2");
        File[] list = versionDir.listFiles();
        // there should be only two files
        // one the snapshot and the other logFile
        File snapFile = null;
        File logFile = null;
        for (File file : list) {
            CRCTest.LOG.info(("file is " + file));
            if (file.getName().startsWith("log")) {
                logFile = file;
                corruptFile(logFile);
            }
        }
        FileTxnLog flog = new FileTxnLog(versionDir);
        TxnIterator itr = flog.read(1);
        // we will get a checksum failure
        try {
            while (itr.next()) {
            } 
            Assert.assertTrue(false);
        } catch (IOException ie) {
            CRCTest.LOG.info("crc corruption", ie);
        }
        itr.close();
        // find the last snapshot
        FileSnap snap = new FileSnap(versionDir);
        List<File> snapFiles = snap.findNRecentSnapshots(2);
        snapFile = snapFiles.get(0);
        corruptFile(snapFile);
        boolean cfile = false;
        try {
            cfile = getCheckSum(snap, snapFile);
        } catch (IOException ie) {
            // the last snapshot seems incompelte
            // corrupt the last but one
            // and use that
            snapFile = snapFiles.get(1);
            corruptFile(snapFile);
            cfile = getCheckSum(snap, snapFile);
        }
        Assert.assertTrue(cfile);
    }
}

