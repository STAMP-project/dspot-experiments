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
package org.apache.hadoop.hbase.http.log;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.http.HttpServer;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.net.NetUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;


@Category({ MiscTests.class, SmallTests.class })
public class TestLogLevel {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLogLevel.class);

    static final PrintStream out = System.out;

    @Test
    @SuppressWarnings("deprecation")
    public void testDynamicLogLevel() throws Exception {
        String logName = TestLogLevel.class.getName();
        Logger testlog = LoggerFactory.getLogger(logName);
        // only test Log4JLogger
        if (testlog instanceof Log4jLoggerAdapter) {
            org.apache.log4j.Logger log = LogManager.getLogger(logName);
            log.debug("log.debug1");
            log.info("log.info1");
            log.error("log.error1");
            Assert.assertTrue((!(Level.ERROR.equals(log.getEffectiveLevel()))));
            HttpServer server = null;
            try {
                server = new HttpServer.Builder().setName("..").addEndpoint(new URI("http://localhost:0")).setFindPort(true).build();
                server.start();
                String authority = NetUtils.getHostPortString(server.getConnectorAddress(0));
                // servlet
                URL url = new URL(((((("http://" + authority) + "/logLevel?log=") + logName) + "&level=") + (Level.ERROR)));
                TestLogLevel.out.println(("*** Connecting to " + url));
                try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    for (String line; (line = in.readLine()) != null; TestLogLevel.out.println(line));
                }
                log.debug("log.debug2");
                log.info("log.info2");
                log.error("log.error2");
                Assert.assertTrue(Level.ERROR.equals(log.getEffectiveLevel()));
                // command line
                String[] args = new String[]{ "-setlevel", authority, logName, Level.DEBUG.toString() };
                LogLevel.main(args);
                log.debug("log.debug3");
                log.info("log.info3");
                log.error("log.error3");
                Assert.assertTrue(Level.DEBUG.equals(log.getEffectiveLevel()));
            } finally {
                if (server != null) {
                    server.stop();
                }
            }
        } else {
            TestLogLevel.out.println(((testlog.getClass()) + " not tested."));
        }
    }
}

