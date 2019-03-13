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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SessionManagementTestCase {
    private static final String SESSION_ID = "session-id";

    private static final String ATTRIBUTE = "attribute";

    private static final String INVALIDATE_SESSION = "invalidate-session";

    private static final String LIST_SESSIONS = "list-sessions";

    private static final String LIST_SESSION_ATTRIBUTE_NAMES = "list-session-attribute-names";

    private static final String LIST_SESSION_ATTRIBUTES = "list-session-attributes";

    private static final String GET_SESSION_ATTRIBUTE = "get-session-attribute";

    private static final String GET_SESSION_LAST_ACCESSED_TIME = "get-session-last-accessed-time";

    private static final String GET_SESSION_LAST_ACCESSED_TIME_MILLIS = "get-session-last-accessed-time-millis";

    private static final String GET_SESSION_CREATION_TIME = "get-session-creation-time";

    private static final String GET_SESSION_CREATION_TIME_MILLIS = "get-session-creation-time-millis";

    @ArquillianResource
    public ManagementClient managementClient;

    @Test
    public void testSessionManagementOperations() throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ModelNode operation = new ModelNode();
            operation.get(OP).set(SessionManagementTestCase.LIST_SESSIONS);
            operation.get(OP_ADDR).set(PathAddress.parseCLIStyleAddress("/deployment=management.war/subsystem=undertow").toModelNode());
            ModelNode opRes = managementClient.getControllerClient().execute(operation);
            Assert.assertEquals(opRes.toString(), "success", opRes.get(OUTCOME).asString());
            Assert.assertEquals(Collections.emptyList(), opRes.get(RESULT).asList());
            long c1 = System.currentTimeMillis();
            HttpGet get = new HttpGet((("http://" + (TestSuiteEnvironment.getServerAddress())) + ":8080/management/SessionPersistenceServlet"));
            HttpResponse res = client.execute(get);
            long c2 = System.currentTimeMillis();
            String sessionId = null;
            for (Header cookie : res.getHeaders("Set-Cookie")) {
                if (cookie.getValue().startsWith("JSESSIONID=")) {
                    sessionId = cookie.getValue().split("=")[1].split("\\.")[0];
                    break;
                }
            }
            Assert.assertNotNull(sessionId);
            opRes = managementClient.getControllerClient().execute(operation);
            Assert.assertEquals(opRes.toString(), "success", opRes.get(OUTCOME).asString());
            Assert.assertEquals(opRes.toString(), Collections.singletonList(new ModelNode(sessionId)), opRes.get(RESULT).asList());
            operation.get(SessionManagementTestCase.SESSION_ID).set(sessionId);
            opRes = executeOperation(operation, SessionManagementTestCase.GET_SESSION_CREATION_TIME_MILLIS);
            long time1 = opRes.get(RESULT).asLong();
            Assert.assertTrue((c1 <= time1));
            Assert.assertTrue((time1 <= c2));
            opRes = executeOperation(operation, SessionManagementTestCase.GET_SESSION_CREATION_TIME);
            long sessionCreationTime = LocalDateTime.parse(opRes.get(RESULT).asString(), DateTimeFormatter.ISO_DATE_TIME).toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now())).toEpochMilli();
            Assert.assertEquals(time1, sessionCreationTime);
            opRes = executeOperation(operation, SessionManagementTestCase.GET_SESSION_LAST_ACCESSED_TIME_MILLIS);
            Assert.assertEquals(time1, opRes.get(RESULT).asLong());
            opRes = executeOperation(operation, SessionManagementTestCase.GET_SESSION_LAST_ACCESSED_TIME);
            long aTime2 = LocalDateTime.parse(opRes.get(RESULT).asString(), DateTimeFormatter.ISO_DATE_TIME).toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now())).toEpochMilli();
            Assert.assertEquals(time1, aTime2);
            Assert.assertEquals(sessionCreationTime, aTime2);
            opRes = executeOperation(operation, SessionManagementTestCase.LIST_SESSION_ATTRIBUTE_NAMES);
            List<ModelNode> resultList = opRes.get(RESULT).asList();
            Assert.assertEquals(1, resultList.size());
            Assert.assertEquals(opRes.toString(), "val", resultList.get(0).asString());
            opRes = executeOperation(operation, SessionManagementTestCase.LIST_SESSION_ATTRIBUTES);
            List<Property> properties = opRes.get(RESULT).asPropertyList();
            Assert.assertEquals(opRes.toString(), 1, properties.size());
            Property property = properties.get(0);
            Assert.assertEquals(opRes.toString(), "val", property.getName());
            Assert.assertEquals(opRes.toString(), "0", property.getValue().asString());
            // we want to make sure that the values will be different
            // so we wait 10ms
            Thread.sleep(10);
            long a1 = System.currentTimeMillis();
            client.execute(get);
            long a2 = System.currentTimeMillis();
            do {
                // because the last access time is updated after the request returns there is a possible race here
                // to get around this we execute this op in a loop and wait for the value to change
                // in 99% of cases this will only iterate once
                // because of the 10ms sleep above they should ways be different
                // we have a max wait time of 1s if something goes wrong
                opRes = executeOperation(operation, SessionManagementTestCase.GET_SESSION_LAST_ACCESSED_TIME_MILLIS);
                time1 = opRes.get(RESULT).asLong();
                if (time1 != sessionCreationTime) {
                    break;
                }
            } while ((System.currentTimeMillis()) < (a1 + 1000) );
            Assert.assertTrue((a1 <= time1));
            Assert.assertTrue((time1 <= a2));
            opRes = executeOperation(operation, SessionManagementTestCase.GET_SESSION_LAST_ACCESSED_TIME);
            long time2 = LocalDateTime.parse(opRes.get(RESULT).asString(), DateTimeFormatter.ISO_DATE_TIME).toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now())).toEpochMilli();
            Assert.assertEquals(time1, time2);
            operation.get(SessionManagementTestCase.ATTRIBUTE).set("val");
            opRes = executeOperation(operation, SessionManagementTestCase.GET_SESSION_ATTRIBUTE);
            Assert.assertEquals("1", opRes.get(RESULT).asString());
            executeOperation(operation, SessionManagementTestCase.INVALIDATE_SESSION);
            opRes = executeOperation(operation, SessionManagementTestCase.LIST_SESSIONS);
            Assert.assertEquals(Collections.emptyList(), opRes.get(RESULT).asList());
        }
    }

    @Test
    public void testSessionManagementOperationsNegative() throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ModelNode operation = new ModelNode();
            operation.get(OP_ADDR).set(PathAddress.parseCLIStyleAddress("/deployment=management.war/subsystem=undertow").toModelNode());
            String sessionId = "non-existing-id";
            operation.get(SessionManagementTestCase.SESSION_ID).set(sessionId);
            negativeTestsCheck(operation, SessionManagementTestCase.LIST_SESSION_ATTRIBUTE_NAMES);
            negativeTestsCheck(operation, SessionManagementTestCase.LIST_SESSION_ATTRIBUTES);
            operation.get(SessionManagementTestCase.ATTRIBUTE).set("val");
            negativeTestsCheck(operation, SessionManagementTestCase.GET_SESSION_ATTRIBUTE);
            executeOperation(operation, SessionManagementTestCase.INVALIDATE_SESSION);
            HttpGet get = new HttpGet((("http://" + (TestSuiteEnvironment.getServerAddress())) + ":8080/management/SessionPersistenceServlet"));
            HttpResponse res = client.execute(get);
            sessionId = null;
            for (Header cookie : res.getHeaders("Set-Cookie")) {
                if (cookie.getValue().startsWith("JSESSIONID=")) {
                    sessionId = cookie.getValue().split("=")[1].split("\\.")[0];
                    break;
                }
            }
            operation.get(SessionManagementTestCase.SESSION_ID).set(sessionId);
            operation.get(SessionManagementTestCase.ATTRIBUTE).set("non-existing");
            ModelNode opRes = executeOperation(operation, SessionManagementTestCase.GET_SESSION_ATTRIBUTE);
            Assert.assertEquals("undefined", opRes.get(RESULT).asString());
            // Invalidate created session.
            executeOperation(operation, SessionManagementTestCase.INVALIDATE_SESSION);
        }
    }
}

