/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;


public class GetAllChildrenNumberTest extends ClientBase {
    private static final String BASE = "/getAllChildrenNumberTest";

    private static final String BASE_EXT = (GetAllChildrenNumberTest.BASE) + "EXT";

    private static final int PERSISTENT_CNT = 2;

    private static final int EPHEMERAL_CNT = 3;

    private ZooKeeper zk;

    @Test
    public void testGetAllChildrenNumberSync() throws InterruptedException, KeeperException {
        // a bad case
        try {
            zk.getAllChildrenNumber(null);
            Assert.fail("the path for getAllChildrenNumber must not be null.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        Assert.assertEquals(GetAllChildrenNumberTest.EPHEMERAL_CNT, zk.getAllChildrenNumber(((GetAllChildrenNumberTest.BASE) + "/0")));
        Assert.assertEquals(0, zk.getAllChildrenNumber(((GetAllChildrenNumberTest.BASE) + "/0/ephem0")));
        Assert.assertEquals(0, zk.getAllChildrenNumber(GetAllChildrenNumberTest.BASE_EXT));
        Assert.assertEquals(((GetAllChildrenNumberTest.PERSISTENT_CNT) + ((GetAllChildrenNumberTest.PERSISTENT_CNT) * (GetAllChildrenNumberTest.EPHEMERAL_CNT))), zk.getAllChildrenNumber(GetAllChildrenNumberTest.BASE));
        // 6(EPHEMERAL) + 2(PERSISTENT) + 3("/zookeeper,/zookeeper/quota,/zookeeper/config") + 1(BASE_EXT) + 1(BASE) = 13
        Assert.assertEquals(13, zk.getAllChildrenNumber("/"));
    }

    @Test
    public void testGetAllChildrenNumberAsync() throws IOException, InterruptedException, KeeperException {
        final CountDownLatch doneProcessing = new CountDownLatch(1);
        zk.getAllChildrenNumber("/", new AsyncCallback.AllChildrenNumberCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, int number) {
                if (path == null) {
                    Assert.fail(String.format("the path of getAllChildrenNumber was null."));
                }
                Assert.assertEquals(13, number);
                doneProcessing.countDown();
            }
        }, null);
        long waitForCallbackSecs = 2L;
        if (!(doneProcessing.await(waitForCallbackSecs, TimeUnit.SECONDS))) {
            Assert.fail(String.format("getAllChildrenNumber didn't callback within %d seconds", waitForCallbackSecs));
        }
    }
}

