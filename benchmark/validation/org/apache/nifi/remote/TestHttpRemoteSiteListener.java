/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.remote;


import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.remote.protocol.FlowFileTransaction;
import org.apache.nifi.remote.protocol.HandshakeProperties;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestHttpRemoteSiteListener {
    @Test
    public void testNormalTransactionProgress() {
        HttpRemoteSiteListener transactionManager = HttpRemoteSiteListener.getInstance(NiFiProperties.createBasicNiFiProperties(null, null));
        String transactionId = transactionManager.createTransaction();
        Assert.assertTrue("Transaction should be active.", transactionManager.isTransactionActive(transactionId));
        ProcessSession processSession = Mockito.mock(ProcessSession.class);
        FlowFileTransaction transaction = new FlowFileTransaction(processSession, null, null, 0, null, null);
        transactionManager.holdTransaction(transactionId, transaction, new HandshakeProperties());
        Assert.assertNotNull(transactionManager.getHandshakenProperties(transactionId));
        transaction = transactionManager.finalizeTransaction(transactionId);
        Assert.assertNotNull(transaction);
        Assert.assertFalse("Transaction should not be active anymore.", transactionManager.isTransactionActive(transactionId));
    }

    @Test
    public void testDuplicatedTransactionId() {
        HttpRemoteSiteListener transactionManager = HttpRemoteSiteListener.getInstance(NiFiProperties.createBasicNiFiProperties(null, null));
        String transactionId = transactionManager.createTransaction();
        Assert.assertTrue("Transaction should be active.", transactionManager.isTransactionActive(transactionId));
        ProcessSession processSession = Mockito.mock(ProcessSession.class);
        FlowFileTransaction transaction = new FlowFileTransaction(processSession, null, null, 0, null, null);
        transactionManager.holdTransaction(transactionId, transaction, null);
        try {
            transactionManager.holdTransaction(transactionId, transaction, null);
            Assert.fail("The same transaction id can't hold another transaction");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void testNoneExistingTransaction() {
        HttpRemoteSiteListener transactionManager = HttpRemoteSiteListener.getInstance(NiFiProperties.createBasicNiFiProperties(null, null));
        String transactionId = "does-not-exist-1";
        Assert.assertFalse("Transaction should not be active.", transactionManager.isTransactionActive(transactionId));
        ProcessSession processSession = Mockito.mock(ProcessSession.class);
        FlowFileTransaction transaction = new FlowFileTransaction(processSession, null, null, 0, null, null);
        try {
            transactionManager.holdTransaction(transactionId, transaction, null);
        } catch (IllegalStateException e) {
            Assert.fail(("Transaction can be held even if the transaction id is not valid anymore," + " in order to support large file or slow network."));
        }
        transactionId = "does-not-exist-2";
        try {
            transactionManager.finalizeTransaction(transactionId);
            Assert.fail("But transaction should not be finalized if it isn't active.");
        } catch (IllegalStateException e) {
        }
    }
}

