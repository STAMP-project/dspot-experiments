/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
 * 2110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.ee.suspend;


import ModelDescriptionConstants.OP;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.HttpRequest;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for suspend/resume functionality with EE concurrency
 */
@RunWith(Arquillian.class)
public class EEConcurrencySuspendTestCase {
    protected static Logger log = Logger.getLogger(EEConcurrencySuspendTestCase.class);

    @ArquillianResource
    private ManagementClient managementClient;

    @Test
    public void testRequestInShutdown() throws Exception {
        final String address = ((("http://" + (TestSuiteEnvironment.getServerAddress())) + ":") + (TestSuiteEnvironment.getHttpPort())) + "/ee-suspend/ShutdownServlet";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        boolean suspended = false;
        try {
            Future<Object> result = executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return HttpRequest.get(address, 60, TimeUnit.SECONDS);
                }
            });
            Thread.sleep(1000);// nasty, but we need to make sure the HTTP request has started

            Assert.assertEquals(ShutdownServlet.TEXT, result.get());
            ModelNode op = new ModelNode();
            op.get(OP).set("suspend");
            EEConcurrencySuspendTestCase.execute(managementClient.getControllerClient(), op);
            op = new ModelNode();
            op.get(OP).set(READ_ATTRIBUTE_OPERATION);
            op.get(NAME).set(SUSPEND_STATE);
            waitUntilSuspendStateResult(op, "SUSPENDING");
            ShutdownServlet.requestLatch.countDown();
            op = new ModelNode();
            op.get(OP).set(READ_ATTRIBUTE_OPERATION);
            op.get(NAME).set(SUSPEND_STATE);
            waitUntilSuspendStateResult(op, "SUSPENDED");
            // server is now suspended,check we get 503 http status code
            final HttpURLConnection conn = ((HttpURLConnection) (new URL(address).openConnection()));
            try {
                conn.setDoInput(true);
                int responseCode = conn.getResponseCode();
                Assert.assertEquals(503, responseCode);
            } finally {
                conn.disconnect();
            }
            suspended = true;
        } finally {
            ShutdownServlet.requestLatch.countDown();
            executorService.shutdown();
            if (suspended) {
                // if suspended, test if it is resumed
                ModelNode op = new ModelNode();
                op.get(OP).set("resume");
                EEConcurrencySuspendTestCase.execute(managementClient.getControllerClient(), op);
                op = new ModelNode();
                op.get(OP).set(READ_ATTRIBUTE_OPERATION);
                op.get(NAME).set(SUSPEND_STATE);
                Assert.assertEquals("server-state is not <RUNNING> after resume operation. ", "RUNNING", EEConcurrencySuspendTestCase.executeForStringResult(managementClient.getControllerClient(), op));
            }
        }
    }
}

