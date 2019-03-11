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
import com.taobao.metamorphosis.network.HttpStatus;
import com.taobao.metamorphosis.network.TransactionCommand;
import com.taobao.metamorphosis.server.CommandProcessor;
import com.taobao.metamorphosis.transaction.LocalTransactionId;
import com.taobao.metamorphosis.transaction.TransactionId;
import com.taobao.metamorphosis.transaction.TransactionInfo;
import com.taobao.metamorphosis.transaction.TransactionInfo.TransactionType;
import javax.transaction.xa.XAResource;
import org.easymock.classextension.EasyMock;
import org.junit.Test;


public class TransactionProcessorUnitTest {
    private TransactionProcessor transactionProcessor;

    private CommandProcessor commandProcessor;

    private Connection conn;

    @Test
    public void testHandleBeginTransaction() throws Exception {
        final String sessionId = "test";
        final LocalTransactionId transactionId = new LocalTransactionId(sessionId, 1);
        final TransactionInfo info = new TransactionInfo(transactionId, sessionId, TransactionType.BEGIN);
        final TransactionCommand tc = new TransactionCommand(info, 1);
        this.commandProcessor.beginTransaction(new SessionContextImpl(sessionId, this.conn), transactionId, 0);
        EasyMock.expectLastCall();
        this.mockSessionContext(sessionId, sessionId);
        this.mockResponseOK();
        this.replay();
        this.transactionProcessor.handleRequest(tc, this.conn);
    }

    @Test
    public void testHandleEndTransaction() throws Exception {
        final String sessionId = "test";
        final LocalTransactionId transactionId = new LocalTransactionId(sessionId, 1);
        final TransactionInfo info = new TransactionInfo(transactionId, sessionId, TransactionType.END);
        final TransactionCommand tc = new TransactionCommand(info, 1);
        this.mockSessionContext(sessionId, sessionId);
        this.replay();
        this.transactionProcessor.handleRequest(tc, this.conn);
    }

    @Test
    public void testHandlePrepare() throws Exception {
        final String sessionId = "test";
        final LocalTransactionId transactionId = new LocalTransactionId(sessionId, 1);
        final TransactionInfo info = new TransactionInfo(transactionId, sessionId, TransactionType.PREPARE);
        final TransactionCommand tc = new TransactionCommand(info, 1);
        EasyMock.expect(this.commandProcessor.prepareTransaction(new SessionContextImpl(sessionId, this.conn), transactionId)).andReturn(XAResource.XA_OK);
        this.conn.response(new com.taobao.metamorphosis.network.BooleanCommand(HttpStatus.Success, String.valueOf(XAResource.XA_OK), 1));
        this.mockSessionContext(sessionId, sessionId);
        this.replay();
        this.transactionProcessor.handleRequest(tc, this.conn);
    }

    @Test
    public void testCommitOnePhase() throws Exception {
        final String sessionId = "test";
        final LocalTransactionId transactionId = new LocalTransactionId(sessionId, 1);
        final TransactionInfo info = new TransactionInfo(transactionId, sessionId, TransactionType.COMMIT_ONE_PHASE);
        final TransactionCommand tc = new TransactionCommand(info, 1);
        this.commandProcessor.commitTransaction(new SessionContextImpl(sessionId, this.conn), transactionId, true);
        EasyMock.expectLastCall();
        this.mockResponseOK();
        this.mockSessionContext(sessionId, sessionId);
        this.replay();
        this.transactionProcessor.handleRequest(tc, this.conn);
    }

    @Test
    public void testCommitTwoPhase() throws Exception {
        final String sessionId = "test";
        final LocalTransactionId transactionId = new LocalTransactionId(sessionId, 1);
        final TransactionInfo info = new TransactionInfo(transactionId, sessionId, TransactionType.COMMIT_TWO_PHASE);
        final TransactionCommand tc = new TransactionCommand(info, 1);
        this.commandProcessor.commitTransaction(new SessionContextImpl(sessionId, this.conn), transactionId, false);
        EasyMock.expectLastCall();
        this.mockResponseOK();
        this.mockSessionContext(sessionId, sessionId);
        this.replay();
        this.transactionProcessor.handleRequest(tc, this.conn);
    }

    @Test
    public void testForgetTransaction() throws Exception {
        final String sessionId = "test";
        final LocalTransactionId transactionId = new LocalTransactionId(sessionId, 1);
        final TransactionInfo info = new TransactionInfo(transactionId, sessionId, TransactionType.FORGET);
        final TransactionCommand tc = new TransactionCommand(info, 1);
        this.commandProcessor.forgetTransaction(new SessionContextImpl(sessionId, this.conn), transactionId);
        EasyMock.expectLastCall();
        this.mockResponseOK();
        this.mockSessionContext(sessionId, sessionId);
        this.replay();
        this.transactionProcessor.handleRequest(tc, this.conn);
    }

    @Test
    public void testRollback() throws Exception {
        final String sessionId = "test";
        final LocalTransactionId transactionId = new LocalTransactionId(sessionId, 1);
        final TransactionInfo info = new TransactionInfo(transactionId, sessionId, TransactionType.ROLLBACK);
        final TransactionCommand tc = new TransactionCommand(info, 1);
        this.commandProcessor.rollbackTransaction(new SessionContextImpl(sessionId, this.conn), transactionId);
        EasyMock.expectLastCall();
        this.mockResponseOK();
        this.mockSessionContext(sessionId, sessionId);
        this.replay();
        this.transactionProcessor.handleRequest(tc, this.conn);
    }

    @Test
    public void testRecoverTransaction() throws Exception {
        final String sessionId = "test";
        String uniqueQualifier = "unique-qualifier";
        final TransactionInfo info = new TransactionInfo(null, sessionId, TransactionType.RECOVER, uniqueQualifier);
        final TransactionCommand tc = new TransactionCommand(info, 1);
        final TransactionId[] ids = this.generateIds();
        final String resultString = this.generateIdsString(ids);
        this.mockSessionContext(GLOBAL_SESSION_KEY, sessionId);
        EasyMock.expect(this.commandProcessor.getPreparedTransactions(new SessionContextImpl("test", this.conn), uniqueQualifier)).andReturn(ids);
        this.conn.response(new com.taobao.metamorphosis.network.BooleanCommand(HttpStatus.Success, resultString, 1));
        EasyMock.expectLastCall();
        this.replay();
        this.transactionProcessor.handleRequest(tc, this.conn);
    }

    @Test
    public void testHandleException() throws Exception {
        final String sessionId = "test";
        String uniqueQualifier = "unique-qualifier";
        final TransactionInfo info = new TransactionInfo(null, sessionId, TransactionType.RECOVER, uniqueQualifier);
        final TransactionCommand tc = new TransactionCommand(info, 1);
        this.mockSessionContext(GLOBAL_SESSION_KEY, sessionId);
        EasyMock.expect(this.commandProcessor.getPreparedTransactions(new SessionContextImpl("test", this.conn), uniqueQualifier)).andThrow(new RuntimeException("just for test"));
        this.conn.response(new com.taobao.metamorphosis.network.BooleanCommand(HttpStatus.InternalServerError, "just for test", 1));
        EasyMock.expectLastCall();
        this.replay();
        this.transactionProcessor.handleRequest(tc, this.conn);
    }
}

