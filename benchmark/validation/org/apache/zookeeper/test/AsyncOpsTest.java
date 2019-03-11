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


import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AsyncOpsTest extends ClientBase {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncOpsTest.class);

    private ZooKeeper zk;

    @Test
    public void testAsyncCreate() {
        new AsyncOps.StringCB(zk).verifyCreate();
    }

    @Test
    public void testAsyncCreate2() {
        new AsyncOps.Create2CB(zk).verifyCreate();
    }

    @Test
    public void testAsyncCreateThree() {
        CountDownLatch latch = new CountDownLatch(3);
        AsyncOps.StringCB op1 = new AsyncOps.StringCB(zk, latch);
        op1.setPath("/op1");
        AsyncOps.StringCB op2 = new AsyncOps.StringCB(zk, latch);
        op2.setPath("/op2");
        AsyncOps.StringCB op3 = new AsyncOps.StringCB(zk, latch);
        op3.setPath("/op3");
        op1.create();
        op2.create();
        op3.create();
        op1.verify();
        op2.verify();
        op3.verify();
    }

    @Test
    public void testAsyncCreateFailure_NodeExists() {
        new AsyncOps.StringCB(zk).verifyCreateFailure_NodeExists();
    }

    @Test
    public void testAsyncCreateFailure_NoNode() {
        new AsyncOps.StringCB(zk).verifyCreateFailure_NoNode();
    }

    @Test
    public void testAsyncCreateFailure_NoChildForEphemeral() {
        new AsyncOps.StringCB(zk).verifyCreateFailure_NoChildForEphemeral();
    }

    @Test
    public void testAsyncCreate2Failure_NodeExists() {
        new AsyncOps.Create2CB(zk).verifyCreateFailure_NodeExists();
    }

    @Test
    public void testAsyncCreate2Failure_NoNode() {
        new AsyncOps.Create2CB(zk).verifyCreateFailure_NoNode();
    }

    @Test
    public void testAsyncCreate2Failure_NoChildForEphemeral() {
        new AsyncOps.Create2CB(zk).verifyCreateFailure_NoChildForEphemeral();
    }

    @Test
    public void testAsyncDelete() {
        new AsyncOps.VoidCB(zk).verifyDelete();
    }

    @Test
    public void testAsyncDeleteFailure_NoNode() {
        new AsyncOps.VoidCB(zk).verifyDeleteFailure_NoNode();
    }

    @Test
    public void testAsyncDeleteFailure_BadVersion() {
        new AsyncOps.VoidCB(zk).verifyDeleteFailure_BadVersion();
    }

    @Test
    public void testAsyncDeleteFailure_NotEmpty() {
        new AsyncOps.VoidCB(zk).verifyDeleteFailure_NotEmpty();
    }

    @Test
    public void testAsyncSync() {
        new AsyncOps.VoidCB(zk).verifySync();
    }

    @Test
    public void testAsyncSetACL() {
        new AsyncOps.StatCB(zk).verifySetACL();
    }

    @Test
    public void testAsyncSetACLFailure_NoNode() {
        new AsyncOps.StatCB(zk).verifySetACLFailure_NoNode();
    }

    @Test
    public void testAsyncSetACLFailure_BadVersion() {
        new AsyncOps.StatCB(zk).verifySetACLFailure_BadVersion();
    }

    @Test
    public void testAsyncSetData() {
        new AsyncOps.StatCB(zk).verifySetData();
    }

    @Test
    public void testAsyncSetDataFailure_NoNode() {
        new AsyncOps.StatCB(zk).verifySetDataFailure_NoNode();
    }

    @Test
    public void testAsyncSetDataFailure_BadVersion() {
        new AsyncOps.StatCB(zk).verifySetDataFailure_BadVersion();
    }

    @Test
    public void testAsyncExists() {
        new AsyncOps.StatCB(zk).verifyExists();
    }

    @Test
    public void testAsyncExistsFailure_NoNode() {
        new AsyncOps.StatCB(zk).verifyExistsFailure_NoNode();
    }

    @Test
    public void testAsyncGetACL() {
        new AsyncOps.ACLCB(zk).verifyGetACL();
    }

    @Test
    public void testAsyncGetACLFailure_NoNode() {
        new AsyncOps.ACLCB(zk).verifyGetACLFailure_NoNode();
    }

    @Test
    public void testAsyncGetChildrenEmpty() {
        new AsyncOps.ChildrenCB(zk).verifyGetChildrenEmpty();
    }

    @Test
    public void testAsyncGetChildrenSingle() {
        new AsyncOps.ChildrenCB(zk).verifyGetChildrenSingle();
    }

    @Test
    public void testAsyncGetChildrenTwo() {
        new AsyncOps.ChildrenCB(zk).verifyGetChildrenTwo();
    }

    @Test
    public void testAsyncGetChildrenFailure_NoNode() {
        new AsyncOps.ChildrenCB(zk).verifyGetChildrenFailure_NoNode();
    }

    @Test
    public void testAsyncGetChildren2Empty() {
        new AsyncOps.Children2CB(zk).verifyGetChildrenEmpty();
    }

    @Test
    public void testAsyncGetChildren2Single() {
        new AsyncOps.Children2CB(zk).verifyGetChildrenSingle();
    }

    @Test
    public void testAsyncGetChildren2Two() {
        new AsyncOps.Children2CB(zk).verifyGetChildrenTwo();
    }

    @Test
    public void testAsyncGetChildren2Failure_NoNode() {
        new AsyncOps.Children2CB(zk).verifyGetChildrenFailure_NoNode();
    }

    @Test
    public void testAsyncGetData() {
        new AsyncOps.DataCB(zk).verifyGetData();
    }

    @Test
    public void testAsyncGetDataFailure_NoNode() {
        new AsyncOps.DataCB(zk).verifyGetDataFailure_NoNode();
    }

    @Test
    public void testAsyncMulti() {
        new AsyncOps.MultiCB(zk).verifyMulti();
    }

    @Test
    public void testAsyncMultiFailure_AllErrorResult() {
        new AsyncOps.MultiCB(zk).verifyMultiFailure_AllErrorResult();
    }

    @Test
    public void testAsyncMultiFailure_NoSideEffect() throws Exception {
        new AsyncOps.MultiCB(zk).verifyMultiFailure_NoSideEffect();
    }

    @Test
    public void testAsyncMultiSequential_NoSideEffect() throws Exception {
        new AsyncOps.MultiCB(zk).verifyMultiSequential_NoSideEffect();
    }
}

