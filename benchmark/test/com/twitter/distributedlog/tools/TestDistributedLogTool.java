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
package com.twitter.distributedlog.tools;


import KeeperException.Code.NOAUTH;
import com.twitter.distributedlog.DLMTestUtil;
import com.twitter.distributedlog.DLSN;
import com.twitter.distributedlog.DistributedLogManager;
import com.twitter.distributedlog.LocalDLMEmulator;
import com.twitter.distributedlog.LogReader;
import com.twitter.distributedlog.LogRecordWithDLSN;
import com.twitter.distributedlog.TestDistributedLogBase;
import com.twitter.distributedlog.admin.DistributedLogAdmin;
import com.twitter.distributedlog.exceptions.ZKException;
import java.net.URI;
import org.apache.bookkeeper.client.BKException.BKNoSuchLedgerExistsException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDistributedLogTool extends TestDistributedLogBase {
    static final Logger LOG = LoggerFactory.getLogger(TestDistributedLogTool.class);

    static final String defaultLedgerPath = LocalDLMEmulator.getBkLedgerPath();

    static final String defaultPath = "/test/namespace";

    static final String defaultHost = "127.0.0.1";

    static final String defaultPrivilegedZkAclId = "NathanielP";

    static URI defaultUri = null;

    static final String ADMIN_TOOL = DistributedLogAdmin.class.getName();

    @Test(timeout = 60000)
    public void testToolCreate() throws Exception {
        TestDistributedLogTool.createStream(TestDistributedLogTool.defaultUri, "0", "TestPrefix", null);
    }

    @Test(timeout = 60000)
    public void testToolCreateZkAclId() throws Exception {
        TestDistributedLogTool.createStream(TestDistributedLogTool.defaultUri, "0", "CreateAclStream", TestDistributedLogTool.defaultPrivilegedZkAclId);
        try {
            DistributedLogManager dlm = DLMTestUtil.createNewDLM("0CreateAclStream", TestDistributedLogBase.conf, TestDistributedLogTool.defaultUri);
            DLMTestUtil.generateCompletedLogSegments(dlm, TestDistributedLogBase.conf, 3, 1000);
            dlm.close();
        } catch (ZKException ex) {
            Assert.assertEquals(NOAUTH, ex.getKeeperExceptionCode());
        }
    }

    @Test(timeout = 60000)
    public void testToolDelete() throws Exception {
        TestDistributedLogTool.createStream(TestDistributedLogTool.defaultUri, "1", "TestPrefix", null);
        deleteStream(TestDistributedLogTool.defaultUri, "1TestPrefix");
    }

    @Test(timeout = 60000)
    public void testToolDeleteAllocPool() throws Exception {
        try {
            DeleteAllocatorPoolCommand cmd = new DeleteAllocatorPoolCommand();
            cmd.setUri(TestDistributedLogTool.defaultUri);
            Assert.assertEquals(0, cmd.runCmd());
            Assert.fail("should have failed");
        } catch (org.apache.zookeeper ex) {
        }
    }

    @Test(timeout = 60000)
    public void testToolList() throws Exception {
        list(TestDistributedLogTool.defaultUri);
    }

    @Test(timeout = 60000)
    public void testToolDump() throws Exception {
        DumpCommand cmd = new DumpCommand();
        cmd.setUri(TestDistributedLogTool.defaultUri);
        cmd.setStreamName("DefaultStream");
        cmd.setFromTxnId(Long.valueOf(0));
        Assert.assertEquals(0, cmd.runCmd());
    }

    @Test(timeout = 60000)
    public void testToolShow() throws Exception {
        ShowCommand cmd = new ShowCommand();
        cmd.setUri(TestDistributedLogTool.defaultUri);
        cmd.setStreamName("DefaultStream");
        Assert.assertEquals(0, cmd.runCmd());
    }

    @Test(timeout = 60000)
    public void testToolTruncate() throws Exception {
        DistributedLogManager dlm = DLMTestUtil.createNewDLM("TruncateStream", TestDistributedLogBase.conf, TestDistributedLogTool.defaultUri);
        DLMTestUtil.generateCompletedLogSegments(dlm, TestDistributedLogBase.conf, 3, 1000);
        dlm.close();
        TruncateCommand cmd = new TruncateCommand();
        cmd.setUri(TestDistributedLogTool.defaultUri);
        cmd.setFilter("TruncateStream");
        cmd.setForce(true);
        Assert.assertEquals(0, cmd.runCmd());
    }

    @Test(timeout = 60000)
    public void testToolInspect() throws Exception {
        InspectCommand cmd = new InspectCommand();
        cmd.setUri(TestDistributedLogTool.defaultUri);
        cmd.setForce(true);
        Assert.assertEquals(0, cmd.runCmd());
    }

    @Test(timeout = 60000)
    public void testToolReadLastConfirmed() throws Exception {
        ReadLastConfirmedCommand cmd = new ReadLastConfirmedCommand();
        cmd.setUri(TestDistributedLogTool.defaultUri);
        cmd.setLedgerId(99999999);
        // Too hard to predict ledger entry id. Settle for basicaly
        // correct functionality.
        try {
            cmd.runCmd();
        } catch (BKNoSuchLedgerExistsException ex) {
        }
    }

    @Test(timeout = 60000)
    public void testToolReadEntriesCommand() throws Exception {
        ReadEntriesCommand cmd = new ReadEntriesCommand();
        cmd.setUri(TestDistributedLogTool.defaultUri);
        cmd.setLedgerId(99999999);
        try {
            cmd.runCmd();
        } catch (BKNoSuchLedgerExistsException ex) {
        }
    }

    @Test(timeout = 60000)
    public void testToolTruncateStream() throws Exception {
        DistributedLogManager dlm = DLMTestUtil.createNewDLM("testToolTruncateStream", TestDistributedLogBase.conf, TestDistributedLogTool.defaultUri);
        DLMTestUtil.generateCompletedLogSegments(dlm, TestDistributedLogBase.conf, 3, 1000);
        DLSN dlsn = new DLSN(2, 1, 0);
        TruncateStreamCommand cmd = new TruncateStreamCommand();
        cmd.setDlsn(dlsn);
        cmd.setUri(TestDistributedLogTool.defaultUri);
        cmd.setStreamName("testToolTruncateStream");
        cmd.setForce(true);
        Assert.assertEquals(0, cmd.runCmd());
        LogReader reader = dlm.getInputStream(0);
        LogRecordWithDLSN record = reader.readNext(false);
        Assert.assertEquals(dlsn, record.getDlsn());
        reader.close();
        dlm.close();
    }
}

