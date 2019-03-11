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


import Ids.OPEN_ACL_UNSAFE;
import KeeperException.Code.BADARGUMENTS;
import KeeperException.Code.MARSHALLINGERROR;
import OpCode.error;
import OpCode.setData;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.KeeperException.SessionExpiredException;
import org.apache.zookeeper.KeeperException.SessionMovedException;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.ZooDefs.OpCode;
import org.apache.zookeeper.proto.SetDataRequest;
import org.apache.zookeeper.server.ZooKeeperServer.ChangeRecord;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PrepRequestProcessorTest extends ClientBase {
    private static final Logger LOG = LoggerFactory.getLogger(PrepRequestProcessorTest.class);

    private static final int CONNECTION_TIMEOUT = 3000;

    private static String HOSTPORT = "127.0.0.1:" + (PortAssignment.unique());

    private CountDownLatch pLatch;

    private ZooKeeperServer zks;

    private ServerCnxnFactory servcnxnf;

    private PrepRequestProcessor processor;

    private Request outcome;

    @Test
    public void testPRequest() throws Exception {
        pLatch = new CountDownLatch(1);
        processor = new PrepRequestProcessor(zks, new PrepRequestProcessorTest.MyRequestProcessor());
        Request foo = new Request(null, 1L, 1, OpCode.create, ByteBuffer.allocate(3), null);
        processor.pRequest(foo);
        Assert.assertEquals("Request should have marshalling error", new org.apache.zookeeper.txn.ErrorTxn(MARSHALLINGERROR.intValue()), outcome.getTxn());
        Assert.assertTrue("request hasn't been processed in chain", pLatch.await(5, TimeUnit.SECONDS));
    }

    /**
     * This test checks that a successful multi will change outstanding record
     * and failed multi shouldn't change outstanding record.
     */
    @Test
    public void testMultiOutstandingChange() throws Exception {
        zks.getZKDatabase().dataTree.createNode("/foo", new byte[0], OPEN_ACL_UNSAFE, 0, 0, 0, 0);
        Assert.assertNull(zks.outstandingChangesForPath.get("/foo"));
        process(Arrays.asList(Op.setData("/foo", new byte[0], (-1))));
        ChangeRecord cr = zks.outstandingChangesForPath.get("/foo");
        Assert.assertNotNull("Change record wasn't set", cr);
        Assert.assertEquals("Record zxid wasn't set correctly", 1, cr.zxid);
        process(Arrays.asList(Op.delete("/foo", (-1))));
        cr = zks.outstandingChangesForPath.get("/foo");
        Assert.assertEquals("Record zxid wasn't set correctly", 2, cr.zxid);
        // It should fail and shouldn't change outstanding record.
        process(Arrays.asList(Op.delete("/foo", (-1))));
        cr = zks.outstandingChangesForPath.get("/foo");
        // zxid should still be previous result because record's not changed.
        Assert.assertEquals("Record zxid wasn't set correctly", 2, cr.zxid);
    }

    /**
     * ZOOKEEPER-2052:
     * This test checks that if a multi operation aborted, and during the multi there is side effect
     * that changed outstandingChangesForPath, after aborted the side effect should be removed and
     * everything should be restored correctly.
     */
    @Test
    public void testMultiRollbackNoLastChange() throws Exception {
        zks.getZKDatabase().dataTree.createNode("/foo", new byte[0], OPEN_ACL_UNSAFE, 0, 0, 0, 0);
        zks.getZKDatabase().dataTree.createNode("/foo/bar", new byte[0], OPEN_ACL_UNSAFE, 0, 0, 0, 0);
        Assert.assertNull(zks.outstandingChangesForPath.get("/foo"));
        // multi record:
        // set "/foo" => succeed, leave a outstanding change
        // delete "/foo" => fail, roll back change
        process(Arrays.asList(Op.setData("/foo", new byte[0], (-1)), Op.delete("/foo", (-1))));
        // aborting multi shouldn't leave any record.
        Assert.assertNull(zks.outstandingChangesForPath.get("/foo"));
    }

    /**
     * It tests that PrepRequestProcessor will return BadArgument KeeperException
     * if the request path (if it exists) is not valid, e.g. empty string.
     */
    @Test
    public void testInvalidPath() throws Exception {
        pLatch = new CountDownLatch(1);
        processor = new PrepRequestProcessor(zks, new PrepRequestProcessorTest.MyRequestProcessor());
        SetDataRequest record = new SetDataRequest("", new byte[0], (-1));
        Request req = createRequest(record, setData);
        processor.pRequest(req);
        pLatch.await();
        Assert.assertEquals(outcome.getHdr().getType(), error);
        Assert.assertEquals(outcome.getException().code(), BADARGUMENTS);
    }

    private class MyRequestProcessor implements RequestProcessor {
        @Override
        public void processRequest(Request request) {
            // getting called by PrepRequestProcessor
            outcome = request;
            pLatch.countDown();
        }

        @Override
        public void shutdown() {
            // TODO Auto-generated method stub
        }
    }

    private class MySessionTracker implements SessionTracker {
        @Override
        public boolean trackSession(long id, int to) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean commitSession(long id, int to) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void checkSession(long sessionId, Object owner) throws SessionExpiredException, SessionMovedException {
            // TODO Auto-generated method stub
        }

        @Override
        public long createSession(int sessionTimeout) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void dumpSessions(PrintWriter pwriter) {
            // TODO Auto-generated method stub
        }

        @Override
        public void removeSession(long sessionId) {
            // TODO Auto-generated method stub
        }

        public int upgradeSession(long sessionId) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void setOwner(long id, Object owner) throws SessionExpiredException {
            // TODO Auto-generated method stub
        }

        @Override
        public void shutdown() {
            // TODO Auto-generated method stub
        }

        @Override
        public boolean touchSession(long sessionId, int sessionTimeout) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void setSessionClosing(long sessionId) {
            // TODO Auto-generated method stub
        }

        @Override
        public boolean isTrackingSession(long sessionId) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void checkGlobalSession(long sessionId, Object owner) throws SessionExpiredException, SessionMovedException {
            // TODO Auto-generated method stub
        }

        @Override
        public Map<Long, Set<Long>> getSessionExpiryMap() {
            return new HashMap<Long, Set<Long>>();
        }

        @Override
        public long getLocalSessionCount() {
            return 0;
        }
    }
}

