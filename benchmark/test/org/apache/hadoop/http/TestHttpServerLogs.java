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
package org.apache.hadoop.http;


import CommonConfigurationKeysPublic.HADOOP_HTTP_LOGS_ENABLED;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHttpServerLogs extends HttpServerFunctionalTest {
    static final Logger LOG = LoggerFactory.getLogger(TestHttpServerLogs.class);

    private static HttpServer2 server;

    @Test
    public void testLogsEnabled() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(HADOOP_HTTP_LOGS_ENABLED, true);
        startServer(conf);
        URL url = new URL((("http://" + (NetUtils.getHostPortString(TestHttpServerLogs.server.getConnectorAddress(0)))) + "/logs"));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(HttpStatus.SC_OK, conn.getResponseCode());
    }

    @Test
    public void testLogsDisabled() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(HADOOP_HTTP_LOGS_ENABLED, false);
        startServer(conf);
        URL url = new URL(((HttpServerFunctionalTest.baseUrl) + "/logs"));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(HttpStatus.SC_NOT_FOUND, conn.getResponseCode());
    }
}

