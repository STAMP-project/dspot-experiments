/**
 * Copyright 2017-2019 CodingApi .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codingapi.txlcn.tc.txmsg;


import DTXLocks.S_LOCK;
import DTXLocks.X_LOCK;
import com.codingapi.txlcn.tc.MiniConfiguration;
import com.codingapi.txlcn.txmsg.dto.MessageDto;
import com.codingapi.txlcn.txmsg.exception.RpcException;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Description:
 * Date: 19-1-23 ??10:52
 *
 * @author ujued
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MiniConfiguration.class)
public class RpcTest {
    @Autowired
    private ReliableMessenger messenger;

    @Test
    public void testLock() throws RpcException {
        // ?????True
        boolean result0 = messenger.acquireLocks("1", Sets.newSet("1"), S_LOCK);
        Assert.assertTrue(result0);
        // ????????????True
        boolean result1 = messenger.acquireLocks("2", Sets.newSet("1"), S_LOCK);
        Assert.assertTrue(result1);
        // ????????????False
        boolean result = messenger.acquireLocks("3", Sets.newSet("1", "2"), X_LOCK);
        Assert.assertFalse(result);
        messenger.releaseLocks(Sets.newSet("1"));
        // ?????True
        boolean result2 = messenger.acquireLocks("4", Sets.newSet("2", "3"), X_LOCK);
        Assert.assertTrue(result2);
        // ??? DTX ?, True
        boolean result3 = messenger.acquireLocks("4", Sets.newSet("2"), X_LOCK);
        Assert.assertTrue(result3);
    }

    /**
     * ????TM Cluster
     */
    @Test
    public void testReconnect() {
        for (int i = 0; i < 100; i++) {
            TMSearcher.search();
        }
    }

    @Test
    public void testCountDown() {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 110; i++) {
            System.out.println(countDownLatch.getCount());
            countDownLatch.countDown();
        }
    }

    @Test
    public void testCluster() throws RpcException {
        messenger.request(new MessageDto());
    }
}

