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
package org.apache.hadoop.yarn.service.client;


import HttpServletResponse.SC_NOT_FOUND;
import HttpServletResponse.SC_OK;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for CLI to API Service.
 */
public class TestApiServiceClient {
    private static ApiServiceClient asc;

    private static ApiServiceClient badAsc;

    private static Server server;

    /**
     * A mocked version of API Service for testing purpose.
     */
    @SuppressWarnings("serial")
    public static class TestServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            System.out.println("Get was called");
            if (((req.getPathInfo()) != null) && (req.getPathInfo().contains("nonexistent-app"))) {
                resp.setStatus(SC_NOT_FOUND);
            } else {
                resp.setStatus(SC_OK);
            }
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setStatus(SC_OK);
        }

        @Override
        protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setStatus(SC_OK);
        }

        @Override
        protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setStatus(SC_OK);
        }
    }

    @Test
    public void testLaunch() {
        String fileName = "target/test-classes/example-app.json";
        String appName = "example-app";
        long lifetime = 3600L;
        String queue = "default";
        try {
            int result = TestApiServiceClient.asc.actionLaunch(fileName, appName, lifetime, queue);
            Assert.assertEquals(EXIT_SUCCESS, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testBadLaunch() {
        String fileName = "unknown_file";
        String appName = "unknown_app";
        long lifetime = 3600L;
        String queue = "default";
        try {
            int result = TestApiServiceClient.badAsc.actionLaunch(fileName, appName, lifetime, queue);
            Assert.assertEquals(EXIT_EXCEPTION_THROWN, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testStatus() {
        String appName = "nonexistent-app";
        try {
            String result = TestApiServiceClient.asc.getStatusString(appName);
            Assert.assertEquals("Status reponse don't match", ((" Service " + appName) + " not found"), result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testStop() {
        String appName = "example-app";
        try {
            int result = TestApiServiceClient.asc.actionStop(appName);
            Assert.assertEquals(EXIT_SUCCESS, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testBadStop() {
        String appName = "unknown_app";
        try {
            int result = TestApiServiceClient.badAsc.actionStop(appName);
            Assert.assertEquals(EXIT_EXCEPTION_THROWN, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testStart() {
        String appName = "example-app";
        try {
            int result = TestApiServiceClient.asc.actionStart(appName);
            Assert.assertEquals(EXIT_SUCCESS, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testBadStart() {
        String appName = "unknown_app";
        try {
            int result = TestApiServiceClient.badAsc.actionStart(appName);
            Assert.assertEquals(EXIT_EXCEPTION_THROWN, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSave() {
        String fileName = "target/test-classes/example-app.json";
        String appName = "example-app";
        long lifetime = 3600L;
        String queue = "default";
        try {
            int result = TestApiServiceClient.asc.actionSave(fileName, appName, lifetime, queue);
            Assert.assertEquals(EXIT_SUCCESS, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testBadSave() {
        String fileName = "unknown_file";
        String appName = "unknown_app";
        long lifetime = 3600L;
        String queue = "default";
        try {
            int result = TestApiServiceClient.badAsc.actionSave(fileName, appName, lifetime, queue);
            Assert.assertEquals(EXIT_EXCEPTION_THROWN, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testFlex() {
        String appName = "example-app";
        HashMap<String, String> componentCounts = new HashMap<String, String>();
        try {
            int result = TestApiServiceClient.asc.actionFlex(appName, componentCounts);
            Assert.assertEquals(EXIT_SUCCESS, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testBadFlex() {
        String appName = "unknown_app";
        HashMap<String, String> componentCounts = new HashMap<String, String>();
        try {
            int result = TestApiServiceClient.badAsc.actionFlex(appName, componentCounts);
            Assert.assertEquals(EXIT_EXCEPTION_THROWN, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDestroy() {
        String appName = "example-app";
        try {
            int result = TestApiServiceClient.asc.actionDestroy(appName);
            Assert.assertEquals(EXIT_SUCCESS, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testBadDestroy() {
        String appName = "unknown_app";
        try {
            int result = TestApiServiceClient.badAsc.actionDestroy(appName);
            Assert.assertEquals(EXIT_EXCEPTION_THROWN, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testInitiateServiceUpgrade() {
        String appName = "example-app";
        String upgradeFileName = "target/test-classes/example-app.json";
        try {
            int result = TestApiServiceClient.asc.initiateUpgrade(appName, upgradeFileName, false);
            Assert.assertEquals(EXIT_SUCCESS, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testInstancesUpgrade() {
        String appName = "example-app";
        try {
            int result = TestApiServiceClient.asc.actionUpgradeInstances(appName, Lists.newArrayList("comp-1", "comp-2"));
            Assert.assertEquals(EXIT_SUCCESS, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }

    @Test
    public void testComponentsUpgrade() {
        String appName = "example-app";
        try {
            int result = TestApiServiceClient.asc.actionUpgradeComponents(appName, Lists.newArrayList("comp"));
            Assert.assertEquals(EXIT_SUCCESS, result);
        } catch (IOException | YarnException e) {
            Assert.fail();
        }
    }
}

