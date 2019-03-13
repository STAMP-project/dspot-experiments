/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.test.integration.transaction;


import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.transactions.TestXAResource;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for EAP7-981 / WFLY-10009
 *
 * @author istraka
 */
@RunWith(Arquillian.class)
@ServerSetup({ MaximumTimeoutTestCase.TimeoutSetup.class })
public class MaximumTimeoutTestCase {
    private static final String MESSAGE_REGEX = ".*\\bWARN\\b.*WFLYTX0039:.*%d";

    private static final String MAX_TIMEOUT_ATTR = "maximum-timeout";

    private static final String DEF_TIMEOUT_ATTR = "default-timeout";

    private static final int MAX_TIMEOUT1 = 400;

    private static final int MAX_TIMEOUT2 = 500;

    private static final int DEFAULT_TIMEOUT = 100;

    private static final int NO_TIMEOUT = 0;

    private static final PathAddress TX_ADDRESS = PathAddress.pathAddress().append(SUBSYSTEM, "transactions");

    private static final PathAddress LOG_FILE_ADDRESS = PathAddress.pathAddress().append(SUBSYSTEM, "logging").append("log-file", "server.log");

    private static Logger LOGGER = Logger.getLogger(MaximumTimeoutTestCase.class);

    @Resource(mappedName = "java:/TransactionManager")
    private static TransactionManager txm;

    @ArquillianResource
    private ManagementClient managementClient;

    @Test
    public void testMaximumTimeout() throws IOException, HeuristicMixedException, HeuristicRollbackException, NotSupportedException, RollbackException, SystemException, XAException {
        MaximumTimeoutTestCase.setDefaultTimeout(managementClient.getControllerClient(), MaximumTimeoutTestCase.NO_TIMEOUT);
        MaximumTimeoutTestCase.setMaximumTimeout(managementClient.getControllerClient(), MaximumTimeoutTestCase.MAX_TIMEOUT1);
        MaximumTimeoutTestCase.txm.setTransactionTimeout(MaximumTimeoutTestCase.NO_TIMEOUT);
        XAResource xaer = new TestXAResource();
        MaximumTimeoutTestCase.txm.begin();
        MaximumTimeoutTestCase.txm.getTransaction().enlistResource(xaer);
        int timeout = xaer.getTransactionTimeout();
        Assert.assertEquals(MaximumTimeoutTestCase.MAX_TIMEOUT1, timeout);
        MaximumTimeoutTestCase.txm.commit();
        MaximumTimeoutTestCase.setMaximumTimeout(managementClient.getControllerClient(), MaximumTimeoutTestCase.MAX_TIMEOUT2);
        xaer = new TestXAResource();
        MaximumTimeoutTestCase.txm.begin();
        MaximumTimeoutTestCase.txm.getTransaction().enlistResource(xaer);
        timeout = xaer.getTransactionTimeout();
        Assert.assertEquals(MaximumTimeoutTestCase.MAX_TIMEOUT2, timeout);
        MaximumTimeoutTestCase.txm.commit();
    }

    @Test
    public void testDefaultTimeout() throws IOException, HeuristicMixedException, HeuristicRollbackException, NotSupportedException, RollbackException, SystemException, XAException {
        MaximumTimeoutTestCase.setMaximumTimeout(managementClient.getControllerClient(), MaximumTimeoutTestCase.MAX_TIMEOUT1);
        MaximumTimeoutTestCase.setDefaultTimeout(managementClient.getControllerClient(), MaximumTimeoutTestCase.DEFAULT_TIMEOUT);
        XAResource xaer = new TestXAResource();
        MaximumTimeoutTestCase.txm.setTransactionTimeout(MaximumTimeoutTestCase.NO_TIMEOUT);
        MaximumTimeoutTestCase.txm.begin();
        MaximumTimeoutTestCase.txm.getTransaction().enlistResource(xaer);
        int timeout = xaer.getTransactionTimeout();
        Assert.assertEquals(MaximumTimeoutTestCase.DEFAULT_TIMEOUT, timeout);
        MaximumTimeoutTestCase.txm.commit();
        xaer = new TestXAResource();
        MaximumTimeoutTestCase.txm.setTransactionTimeout(20);
        MaximumTimeoutTestCase.txm.begin();
        MaximumTimeoutTestCase.txm.getTransaction().enlistResource(xaer);
        timeout = xaer.getTransactionTimeout();
        Assert.assertEquals(20, timeout);
        MaximumTimeoutTestCase.txm.commit();
        xaer = new TestXAResource();
        MaximumTimeoutTestCase.txm.setTransactionTimeout(MaximumTimeoutTestCase.NO_TIMEOUT);
        MaximumTimeoutTestCase.txm.begin();
        MaximumTimeoutTestCase.txm.getTransaction().enlistResource(xaer);
        timeout = xaer.getTransactionTimeout();
        Assert.assertEquals(MaximumTimeoutTestCase.DEFAULT_TIMEOUT, timeout);
        MaximumTimeoutTestCase.txm.commit();
    }

    @Test
    public void testLogFile() {
        MaximumTimeoutTestCase.setMaximumTimeout(managementClient.getControllerClient(), MaximumTimeoutTestCase.MAX_TIMEOUT1);
        MaximumTimeoutTestCase.setDefaultTimeout(managementClient.getControllerClient(), MaximumTimeoutTestCase.NO_TIMEOUT);
        MaximumTimeoutTestCase.setMaximumTimeout(managementClient.getControllerClient(), MaximumTimeoutTestCase.MAX_TIMEOUT2);
        List<ModelNode> nodes = MaximumTimeoutTestCase.getLogs(managementClient.getControllerClient());
        boolean firstMessageFound = false;
        boolean secondMessageFound = false;
        for (ModelNode node : nodes) {
            String line = node.asString();
            if (!firstMessageFound) {
                if (line.matches(String.format(MaximumTimeoutTestCase.MESSAGE_REGEX, MaximumTimeoutTestCase.MAX_TIMEOUT1))) {
                    firstMessageFound = true;
                }
            } else {
                if (line.matches(String.format(MaximumTimeoutTestCase.MESSAGE_REGEX, MaximumTimeoutTestCase.MAX_TIMEOUT2))) {
                    secondMessageFound = true;
                }
            }
        }
        Assert.assertTrue((firstMessageFound && secondMessageFound));
    }

    static class TimeoutSetup implements ServerSetupTask {
        private int defaultTimeout;

        private int maxTimeout;

        @Override
        public void setup(ManagementClient managementClient, String s) throws Exception {
            ModelNode op = MaximumTimeoutTestCase.executeForResult(managementClient.getControllerClient(), Util.getReadAttributeOperation(MaximumTimeoutTestCase.TX_ADDRESS, MaximumTimeoutTestCase.MAX_TIMEOUT_ATTR));
            maxTimeout = op.asInt();
            op = MaximumTimeoutTestCase.executeForResult(managementClient.getControllerClient(), Util.getReadAttributeOperation(MaximumTimeoutTestCase.TX_ADDRESS, MaximumTimeoutTestCase.DEF_TIMEOUT_ATTR));
            defaultTimeout = op.asInt();
            MaximumTimeoutTestCase.LOGGER.info(("max timeout: " + (maxTimeout)));
            MaximumTimeoutTestCase.LOGGER.info(("default timeout: " + (defaultTimeout)));
        }

        @Override
        public void tearDown(ManagementClient managementClient, String s) throws Exception {
            MaximumTimeoutTestCase.setDefaultTimeout(managementClient.getControllerClient(), defaultTimeout);
            MaximumTimeoutTestCase.setMaximumTimeout(managementClient.getControllerClient(), maxTimeout);
            setup(managementClient, s);
        }
    }
}

