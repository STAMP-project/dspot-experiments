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
package org.apache.hadoop.hbase.thrift;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.thrift.transport.TTransportException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Start the HBase Thrift HTTP server on a random port through the command-line
 * interface and talk to it from client side.
 */
@Category({ ClientTests.class, LargeTests.class })
public class TestThriftHttpServer {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestThriftHttpServer.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestThriftHttpServer.class);

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private Thread httpServerThread;

    private volatile Exception httpServerException;

    private Exception clientSideException;

    private ThriftServer thriftServer;

    int port;

    @Test
    public void testExceptionThrownWhenMisConfigured() throws Exception {
        Configuration conf = new Configuration(TestThriftHttpServer.TEST_UTIL.getConfiguration());
        conf.set("hbase.thrift.security.qop", "privacy");
        conf.setBoolean("hbase.thrift.ssl.enabled", false);
        ThriftServer server = null;
        ExpectedException thrown = ExpectedException.none();
        try {
            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage(("Thrift HTTP Server's QoP is privacy, " + "but hbase.thrift.ssl.enabled is false"));
            server = new ThriftServer(conf);
            server.run();
            Assert.fail("Thrift HTTP Server starts up even with wrong security configurations.");
        } catch (Exception e) {
        }
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testRunThriftServerWithHeaderBufferLength() throws Exception {
        // Test thrift server with HTTP header length less than 64k
        try {
            runThriftServer((1024 * 63));
        } catch (TTransportException tex) {
            Assert.assertFalse(tex.getMessage().equals("HTTP Response code: 431"));
        }
        // Test thrift server with HTTP header length more than 64k, expect an exception
        exception.expect(TTransportException.class);
        exception.expectMessage("HTTP Response code: 431");
        runThriftServer((1024 * 64));
    }

    @Test
    public void testRunThriftServer() throws Exception {
        runThriftServer(0);
    }

    protected static volatile boolean tableCreated = false;
}

