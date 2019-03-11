/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
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
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.transaction;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class TransactionIdUnitTest {
    private final Random rand = new Random();

    @Test
    public void testValueOf() {
        final byte[] branchQualifier = this.randomBytes();
        final byte[] globalTransactionId = this.randomBytes();
        final XATransactionId xid = new XATransactionId(100, branchQualifier, globalTransactionId, "unique-qualifier");
        final LocalTransactionId id = new LocalTransactionId("sessionId", (-99));
        final String key1 = xid.getTransactionKey();
        final String key2 = id.getTransactionKey();
        Assert.assertEquals(xid, TransactionId.valueOf(key1));
        Assert.assertEquals(id, TransactionId.valueOf(key2));
    }

    @Test
    public void testXATransactionId() {
        final byte[] branchQualifier = this.randomBytes();
        final byte[] globalTransactionId = this.randomBytes();
        final XATransactionId id = new XATransactionId(100, branchQualifier, globalTransactionId, "unique-qualifier");
        Assert.assertArrayEquals(branchQualifier, id.getBranchQualifier());
        Assert.assertArrayEquals(globalTransactionId, id.getGlobalTransactionId());
        Assert.assertEquals(100, id.getFormatId());
        Assert.assertEquals("unique-qualifier", id.getUniqueQualifier());
        final String key = id.getTransactionKey();
        Assert.assertNotNull(key);
        final XATransactionId newId = new XATransactionId(key);
        Assert.assertNotSame(id, newId);
        Assert.assertEquals(id, newId);
        Assert.assertEquals(0, id.compareTo(newId));
        Assert.assertTrue(id.isXATransaction());
        Assert.assertFalse(id.isLocalTransaction());
    }

    @Test
    public void testLocalTransactionId() {
        final LocalTransactionId id = new LocalTransactionId("sessionId", (-99));
        Assert.assertFalse(id.isXATransaction());
        Assert.assertTrue(id.isLocalTransaction());
        Assert.assertEquals("sessionId", id.getSessionId());
        Assert.assertEquals((-99), id.getValue());
        final String s = id.getTransactionKey();
        Assert.assertNotNull(s);
        final LocalTransactionId newId = new LocalTransactionId(s);
        Assert.assertEquals(id, newId);
        Assert.assertEquals(0, id.compareTo(newId));
    }
}

