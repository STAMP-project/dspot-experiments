/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.test.extension.rts;


import TxMediaType.TX_STATUS_MEDIA_TYPE;
import TxStatusMediaType.TX_COMMITTED;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.jbossts.star.util.TxSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.test.extension.rts.common.WorkRestATResource;


/**
 *
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 * @author <a href="mailto:mmusgrov@redhat.com">Michael Musgrove</a>
 */
@RunAsClient
@RunWith(Arquillian.class)
public final class CoordinatorTestCase extends AbstractTestCase {
    private static final String DEPENDENCIES = "Dependencies: org.jboss.narayana.rts\n";

    private static final String SERVER_HOST_PORT = ((TestSuiteEnvironment.getServerAddress()) + ":") + (TestSuiteEnvironment.getHttpPort());

    @ArquillianResource
    private ManagementClient managementClient;

    @Test
    public void testListTransactions() {
        TxSupport[] txns = new TxSupport[]{ new TxSupport(), new TxSupport() };
        int txnCount = new TxSupport().txCount();
        for (TxSupport txn : txns) {
            txn.startTx();
        }
        // there should be txns.length more transactions
        Assert.assertEquals((txnCount + (txns.length)), txns[0].txCount());
        for (TxSupport txn : txns) {
            txn.commitTx();
        }
        // the number of transactions should be back to the original number
        Assert.assertEquals(txnCount, txns[0].txCount());
    }

    @Test
    public void test1PCAbort() throws Exception {
        TxSupport txn = new TxSupport();
        String pUrl = (getDeploymentUrl()) + (WorkRestATResource.PATH_SEGMENT);
        String pid = null;
        String pVal;
        pid = modifyResource(txn, pUrl, pid, "p1", "v1");
        pVal = getResourceProperty(txn, pUrl, pid, "p1");
        Assert.assertEquals(pVal, "v1");
        txn.startTx();
        pid = enlistResource(txn, ((pUrl + "?pId=") + pid));
        modifyResource(txn, pUrl, pid, "p1", "v2");
        pVal = getResourceProperty(txn, pUrl, pid, "p1");
        Assert.assertEquals(pVal, "v2");
        txn.rollbackTx();
        pVal = getResourceProperty(txn, pUrl, pid, "p1");
        Assert.assertEquals(pVal, "v1");
    }

    @Test
    public void test1PCCommit() throws Exception {
        TxSupport txn = new TxSupport();
        String pUrl = (getDeploymentUrl()) + (WorkRestATResource.PATH_SEGMENT);
        String pid = null;
        String pVal;
        pid = modifyResource(txn, pUrl, pid, "p1", "v1");
        pVal = getResourceProperty(txn, pUrl, pid, "p1");
        Assert.assertEquals(pVal, "v1");
        txn.startTx();
        pid = enlistResource(txn, ((pUrl + "?pId=") + pid));
        modifyResource(txn, pUrl, pid, "p1", "v2");
        pVal = getResourceProperty(txn, pUrl, pid, "p1");
        Assert.assertEquals(pVal, "v2");
        txn.commitTx();
        pVal = getResourceProperty(txn, pUrl, pid, "p1");
        Assert.assertEquals(pVal, "v2");
    }

    @Test
    public void test2PC() throws Exception {
        TxSupport txn = new TxSupport();
        String pUrl = (getDeploymentUrl()) + (WorkRestATResource.PATH_SEGMENT);
        String[] pid = new String[2];
        String[] pVal = new String[2];
        for (int i = 0; i < (pid.length); i++) {
            pid[i] = modifyResource(txn, pUrl, null, "p1", "v1");
            pVal[i] = getResourceProperty(txn, pUrl, pid[i], "p1");
            Assert.assertEquals(pVal[i], "v1");
        }
        txn.startTx();
        for (int i = 0; i < (pid.length); i++) {
            enlistResource(txn, ((pUrl + "?pId=") + (pid[i])));
            modifyResource(txn, pUrl, pid[i], "p1", "v2");
            pVal[i] = getResourceProperty(txn, pUrl, pid[i], "p1");
            Assert.assertEquals(pVal[i], "v2");
        }
        txn.rollbackTx();
        for (int i = 0; i < (pid.length); i++) {
            pVal[i] = getResourceProperty(txn, pUrl, pid[i], "p1");
            Assert.assertEquals(pVal[i], "v1");
        }
    }

    @Test
    public void testCommitInvalidTx() throws IOException {
        TxSupport txn = new TxSupport().startTx();
        String terminator = txn.getTerminatorURI();
        terminator += "/_dead";
        // an attempt to commit on this URI should fail:
        txn.httpRequest(new int[]{ HttpURLConnection.HTTP_NOT_FOUND }, terminator, "PUT", TX_STATUS_MEDIA_TYPE, TX_COMMITTED);
        // commit it properly
        txn.commitTx();
    }

    @Test
    public void testTimeoutCleanup() throws InterruptedException {
        TxSupport txn = new TxSupport();
        int txnCount = txn.txCount();
        txn.startTx(1000);
        txn.enlistTestResource(((getDeploymentUrl()) + (WorkRestATResource.PATH_SEGMENT)), false);
        // Let the txn timeout
        Thread.sleep(2000);
        Assert.assertEquals(txnCount, txn.txCount());
    }
}

