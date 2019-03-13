/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.web.session;


import ModelDescriptionConstants.OP;
import ModelDescriptionConstants.OP_ADDR;
import ModelDescriptionConstants.OUTCOME;
import ModelDescriptionConstants.RESULT;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SessionInvalidateTestCase {
    @ArquillianResource
    public ManagementClient managementClient;

    @Test
    public void testInvalidateSessions() throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ModelNode operation = new ModelNode();
            operation.get(OP).set("invalidate-session");
            operation.get(OP_ADDR).set(PathAddress.parseCLIStyleAddress("/deployment=invalidate.war/subsystem=undertow").toModelNode());
            operation.get("session-id").set("fake");
            ModelNode opRes = managementClient.getControllerClient().execute(operation);
            Assert.assertEquals("success", opRes.get(OUTCOME).asString());
            Assert.assertEquals(false, opRes.get(RESULT).asBoolean());
            HttpGet get = new HttpGet((("http://" + (TestSuiteEnvironment.getServerAddress())) + ":8080/invalidate/SessionPersistenceServlet"));
            HttpResponse res = client.execute(get);
            String sessionId = null;
            for (Header cookie : res.getHeaders("Set-Cookie")) {
                if (cookie.getValue().startsWith("JSESSIONID=")) {
                    sessionId = cookie.getValue().split("=")[1].split("\\.")[0];
                    break;
                }
            }
            Assert.assertNotNull(sessionId);
            String result = EntityUtils.toString(res.getEntity());
            Assert.assertEquals("0", result);
            result = runGet(get, client);
            Assert.assertEquals("1", result);
            result = runGet(get, client);
            Assert.assertEquals("2", result);
            operation.get("session-id").set(sessionId);
            opRes = managementClient.getControllerClient().execute(operation);
            Assert.assertEquals("success", opRes.get(OUTCOME).asString());
            Assert.assertEquals(true, opRes.get(RESULT).asBoolean());
            result = runGet(get, client);
            Assert.assertEquals("0", result);
            result = runGet(get, client);
            Assert.assertEquals("1", result);
        }
    }
}

