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
package org.apache.hadoop.hbase.http;


import java.net.URL;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.security.ssl.SSLFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This testcase issues SSL certificates configures the HttpServer to serve
 * HTTPS using the created certficates and calls an echo servlet using the
 * corresponding HTTPS URL.
 */
@Category({ MiscTests.class, SmallTests.class })
public class TestSSLHttpServer extends HttpServerFunctionalTest {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSSLHttpServer.class);

    private static final String BASEDIR = ((System.getProperty("test.build.dir", "target/test-dir")) + "/") + (TestSSLHttpServer.class.getSimpleName());

    private static final Logger LOG = LoggerFactory.getLogger(TestSSLHttpServer.class);

    private static Configuration conf;

    private static HttpServer server;

    private static URL baseUrl;

    private static String keystoresDir;

    private static String sslConfDir;

    private static SSLFactory clientSslFactory;

    @Test
    public void testEcho() throws Exception {
        Assert.assertEquals("a:b\nc:d\n", TestSSLHttpServer.readOut(new URL(TestSSLHttpServer.baseUrl, "/echo?a=b&c=d")));
        Assert.assertEquals("a:b\nc&lt;:d\ne:&gt;\n", TestSSLHttpServer.readOut(new URL(TestSSLHttpServer.baseUrl, "/echo?a=b&c<=d&e=>")));
    }
}

