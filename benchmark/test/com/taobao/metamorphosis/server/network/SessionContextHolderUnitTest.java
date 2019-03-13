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
package com.taobao.metamorphosis.server.network;


import SessionContextHolder.GLOBAL_SESSION_KEY;
import com.taobao.gecko.service.Connection;
import com.taobao.metamorphosis.server.utils.XIDGenerator;
import com.taobao.metamorphosis.transaction.LocalTransactionId;
import com.taobao.metamorphosis.transaction.XATransactionId;
import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class SessionContextHolderUnitTest {
    @Test
    public void testGetOrCreateSessionContext_LocalTransactionId() {
        final Connection conn = EasyMock.createMock(Connection.class);
        final LocalTransactionId xid = new LocalTransactionId("test", 1);
        EasyMock.expect(conn.getAttribute("test")).andReturn(null);
        EasyMock.expect(conn.setAttributeIfAbsent("test", new SessionContextImpl("test", conn))).andReturn(null);
        EasyMock.replay(conn);
        final SessionContext ctx = SessionContextHolder.getOrCreateSessionContext(conn, xid);
        Assert.assertNotNull(ctx);
        Assert.assertEquals("test", ctx.getSessionId());
        Assert.assertSame(conn, ctx.getConnection());
        Assert.assertTrue(ctx.getTransactions().isEmpty());
        Assert.assertFalse(ctx.isInRecoverMode());
        EasyMock.verify(conn);
    }

    @Test
    public void testGetOrCreateSessionContext_XATransactionId() {
        final Connection conn = EasyMock.createMock(Connection.class);
        final XATransactionId xid = XIDGenerator.createXID(0);
        EasyMock.expect(conn.getAttribute(GLOBAL_SESSION_KEY)).andReturn(null);
        EasyMock.expect(conn.setAttributeIfAbsent(GLOBAL_SESSION_KEY, new SessionContextImpl(null, conn))).andReturn(null);
        EasyMock.replay(conn);
        final SessionContext ctx = SessionContextHolder.getOrCreateSessionContext(conn, xid);
        Assert.assertNotNull(ctx);
        Assert.assertNull(ctx.getSessionId());
        Assert.assertSame(conn, ctx.getConnection());
        Assert.assertTrue(ctx.getTransactions().isEmpty());
        Assert.assertFalse(ctx.isInRecoverMode());
        EasyMock.verify(conn);
    }
}

