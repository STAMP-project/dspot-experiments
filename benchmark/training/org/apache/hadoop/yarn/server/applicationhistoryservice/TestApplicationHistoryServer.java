/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.applicationhistoryservice;


import ExitUtil.ExitException;
import STATE.INITED;
import STATE.STARTED;
import STATE.STOPPED;
import YarnConfiguration.DEFAULT_TIMELINE_SERVICE_CLIENT_THREAD_COUNT;
import YarnConfiguration.TIMELINE_SERVICE_HANDLER_THREAD_COUNT;
import YarnConfiguration.TIMELINE_SERVICE_LEVELDB_TTL_INTERVAL_MS;
import YarnConfiguration.TIMELINE_SERVICE_STATE_STORE_CLASS;
import YarnConfiguration.TIMELINE_SERVICE_STORE;
import YarnConfiguration.TIMELINE_SERVICE_TTL_MS;
import YarnConfiguration.TIMELINE_SERVICE_UI_NAMES;
import YarnConfiguration.TIMELINE_SERVICE_WEBAPP_ADDRESS;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.lib.StaticUserWebFilter;
import org.apache.hadoop.security.AuthenticationFilterInitializer;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.timeline.MemoryTimelineStore;
import org.apache.hadoop.yarn.server.timeline.TimelineStore;
import org.apache.hadoop.yarn.server.timeline.recovery.MemoryTimelineStateStore;
import org.apache.hadoop.yarn.server.timeline.recovery.TimelineStateStore;
import org.apache.hadoop.yarn.server.timeline.security.TimelineAuthenticationFilterInitializer;
import org.junit.Assert;
import org.junit.Test;


public class TestApplicationHistoryServer {
    // simple test init/start/stop ApplicationHistoryServer. Status should change.
    @Test(timeout = 60000)
    public void testStartStopServer() throws Exception {
        ApplicationHistoryServer historyServer = new ApplicationHistoryServer();
        Configuration config = new YarnConfiguration();
        config.setClass(TIMELINE_SERVICE_STORE, MemoryTimelineStore.class, TimelineStore.class);
        config.setClass(TIMELINE_SERVICE_STATE_STORE_CLASS, MemoryTimelineStateStore.class, TimelineStateStore.class);
        config.set(TIMELINE_SERVICE_WEBAPP_ADDRESS, "localhost:0");
        try {
            try {
                historyServer.init(config);
                config.setInt(TIMELINE_SERVICE_HANDLER_THREAD_COUNT, 0);
                historyServer.start();
                Assert.fail();
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains(TIMELINE_SERVICE_HANDLER_THREAD_COUNT));
            }
            config.setInt(TIMELINE_SERVICE_HANDLER_THREAD_COUNT, DEFAULT_TIMELINE_SERVICE_CLIENT_THREAD_COUNT);
            historyServer = new ApplicationHistoryServer();
            historyServer.init(config);
            Assert.assertEquals(INITED, historyServer.getServiceState());
            ApplicationHistoryClientService historyService = historyServer.getClientService();
            Assert.assertNotNull(historyServer.getClientService());
            Assert.assertEquals(INITED, historyService.getServiceState());
            historyServer.start();
            Assert.assertEquals(STARTED, historyServer.getServiceState());
            Assert.assertEquals(STARTED, historyService.getServiceState());
            historyServer.stop();
            Assert.assertEquals(STOPPED, historyServer.getServiceState());
        } finally {
            historyServer.stop();
        }
    }

    // test launch method
    @Test(timeout = 60000)
    public void testLaunch() throws Exception {
        ExitUtil.disableSystemExit();
        ApplicationHistoryServer historyServer = null;
        try {
            // Not able to modify the config of this test case,
            // but others have been customized to avoid conflicts
            historyServer = ApplicationHistoryServer.launchAppHistoryServer(new String[0]);
        } catch (ExitUtil e) {
            Assert.assertEquals(0, e.status);
            ExitUtil.resetFirstExitException();
            Assert.fail();
        } finally {
            if (historyServer != null) {
                historyServer.stop();
            }
        }
    }

    // test launch method with -D arguments
    @Test(timeout = 60000)
    public void testLaunchWithArguments() throws Exception {
        ExitUtil.disableSystemExit();
        ApplicationHistoryServer historyServer = null;
        try {
            // Not able to modify the config of this test case,
            // but others have been customized to avoid conflicts
            String[] args = new String[2];
            args[0] = ("-D" + (YarnConfiguration.TIMELINE_SERVICE_LEVELDB_TTL_INTERVAL_MS)) + "=4000";
            args[1] = ("-D" + (YarnConfiguration.TIMELINE_SERVICE_TTL_MS)) + "=200";
            historyServer = ApplicationHistoryServer.launchAppHistoryServer(args);
            Configuration conf = historyServer.getConfig();
            Assert.assertEquals("4000", conf.get(TIMELINE_SERVICE_LEVELDB_TTL_INTERVAL_MS));
            Assert.assertEquals("200", conf.get(TIMELINE_SERVICE_TTL_MS));
        } catch (ExitUtil e) {
            Assert.assertEquals(0, e.status);
            ExitUtil.resetFirstExitException();
            Assert.fail();
        } finally {
            if (historyServer != null) {
                historyServer.stop();
            }
        }
    }

    @Test(timeout = 240000)
    public void testFilterOverrides() throws Exception {
        HashMap<String, String> driver = new HashMap<String, String>();
        driver.put("", TimelineAuthenticationFilterInitializer.class.getName());
        driver.put(StaticUserWebFilter.class.getName(), (((StaticUserWebFilter.class.getName()) + ",") + (TimelineAuthenticationFilterInitializer.class.getName())));
        driver.put(AuthenticationFilterInitializer.class.getName(), TimelineAuthenticationFilterInitializer.class.getName());
        driver.put(TimelineAuthenticationFilterInitializer.class.getName(), TimelineAuthenticationFilterInitializer.class.getName());
        driver.put((((AuthenticationFilterInitializer.class.getName()) + ",") + (TimelineAuthenticationFilterInitializer.class.getName())), TimelineAuthenticationFilterInitializer.class.getName());
        driver.put((((AuthenticationFilterInitializer.class.getName()) + ", ") + (TimelineAuthenticationFilterInitializer.class.getName())), TimelineAuthenticationFilterInitializer.class.getName());
        for (Map.Entry<String, String> entry : driver.entrySet()) {
            String filterInitializer = entry.getKey();
            String expectedValue = entry.getValue();
            ApplicationHistoryServer historyServer = new ApplicationHistoryServer();
            Configuration config = new YarnConfiguration();
            config.setClass(TIMELINE_SERVICE_STORE, MemoryTimelineStore.class, TimelineStore.class);
            config.setClass(TIMELINE_SERVICE_STATE_STORE_CLASS, MemoryTimelineStateStore.class, TimelineStateStore.class);
            config.set(TIMELINE_SERVICE_WEBAPP_ADDRESS, "localhost:0");
            try {
                config.set("hadoop.http.filter.initializers", filterInitializer);
                historyServer.init(config);
                historyServer.start();
                Configuration tmp = historyServer.getConfig();
                Assert.assertEquals(expectedValue, tmp.get("hadoop.http.filter.initializers"));
            } finally {
                historyServer.stop();
            }
        }
    }

    @Test(timeout = 240000)
    public void testHostedUIs() throws Exception {
        ApplicationHistoryServer historyServer = new ApplicationHistoryServer();
        Configuration config = new YarnConfiguration();
        config.setClass(TIMELINE_SERVICE_STORE, MemoryTimelineStore.class, TimelineStore.class);
        config.setClass(TIMELINE_SERVICE_STATE_STORE_CLASS, MemoryTimelineStateStore.class, TimelineStateStore.class);
        config.set(TIMELINE_SERVICE_WEBAPP_ADDRESS, "localhost:0");
        final String UI1 = "UI1";
        String connFileStr = "";
        File diskFile = new File("./pom.xml");
        String diskFileStr = readInputStream(new FileInputStream(diskFile));
        try {
            config.set(TIMELINE_SERVICE_UI_NAMES, UI1);
            config.set(((YarnConfiguration.TIMELINE_SERVICE_UI_WEB_PATH_PREFIX) + UI1), ("/" + UI1));
            config.set(((YarnConfiguration.TIMELINE_SERVICE_UI_ON_DISK_PATH_PREFIX) + UI1), "./");
            historyServer.init(config);
            historyServer.start();
            URL url = new URL((((("http://localhost:" + (historyServer.getPort())) + "/") + UI1) + "/pom.xml"));
            HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
            conn.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            connFileStr = readInputStream(conn.getInputStream());
        } finally {
            historyServer.stop();
        }
        Assert.assertEquals("Web file contents should be the same as on disk contents", diskFileStr, connFileStr);
    }
}

