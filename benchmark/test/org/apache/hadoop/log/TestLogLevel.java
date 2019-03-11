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
package org.apache.hadoop.log;


import LogLevel.PROTOCOL_HTTP;
import LogLevel.PROTOCOL_HTTPS;
import java.io.File;
import java.net.SocketException;
import javax.net.ssl.SSLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.minikdc.KerberosSecurityTestcase;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test LogLevel.
 */
public class TestLogLevel extends KerberosSecurityTestcase {
    private static final File BASEDIR = GenericTestUtils.getRandomizedTestDir();

    private static String keystoresDir;

    private static String sslConfDir;

    private static Configuration conf;

    private static Configuration sslConf;

    private final String logName = TestLogLevel.class.getName();

    private String clientPrincipal;

    private String serverPrincipal;

    private final Log testlog = LogFactory.getLog(logName);

    private final Logger log = ((Log4JLogger) (testlog)).getLogger();

    private static final String PRINCIPAL = "loglevel.principal";

    private static final String KEYTAB = "loglevel.keytab";

    /**
     * Test client command line options. Does not validate server behavior.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 120000)
    public void testCommandOptions() throws Exception {
        final String className = this.getClass().getName();
        Assert.assertFalse(validateCommand(new String[]{ "-foo" }));
        // fail due to insufficient number of arguments
        Assert.assertFalse(validateCommand(new String[]{  }));
        Assert.assertFalse(validateCommand(new String[]{ "-getlevel" }));
        Assert.assertFalse(validateCommand(new String[]{ "-setlevel" }));
        Assert.assertFalse(validateCommand(new String[]{ "-getlevel", "foo.bar:8080" }));
        // valid command arguments
        Assert.assertTrue(validateCommand(new String[]{ "-getlevel", "foo.bar:8080", className }));
        Assert.assertTrue(validateCommand(new String[]{ "-setlevel", "foo.bar:8080", className, "DEBUG" }));
        Assert.assertTrue(validateCommand(new String[]{ "-getlevel", "foo.bar:8080", className, "-protocol", "http" }));
        Assert.assertTrue(validateCommand(new String[]{ "-getlevel", "foo.bar:8080", className, "-protocol", "https" }));
        Assert.assertTrue(validateCommand(new String[]{ "-setlevel", "foo.bar:8080", className, "DEBUG", "-protocol", "http" }));
        Assert.assertTrue(validateCommand(new String[]{ "-setlevel", "foo.bar:8080", className, "DEBUG", "-protocol", "https" }));
        // fail due to the extra argument
        Assert.assertFalse(validateCommand(new String[]{ "-getlevel", "foo.bar:8080", className, "-protocol", "https", "blah" }));
        Assert.assertFalse(validateCommand(new String[]{ "-setlevel", "foo.bar:8080", className, "DEBUG", "-protocol", "https", "blah" }));
        Assert.assertFalse(validateCommand(new String[]{ "-getlevel", "foo.bar:8080", className, "-protocol", "https", "-protocol", "https" }));
        Assert.assertFalse(validateCommand(new String[]{ "-getlevel", "foo.bar:8080", className, "-setlevel", "foo.bar:8080", className }));
    }

    /**
     * Test setting log level to "Info".
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testInfoLogLevel() throws Exception {
        testDynamicLogLevel(PROTOCOL_HTTP, PROTOCOL_HTTP, false, "Info");
    }

    /**
     * Test setting log level to "Error".
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testErrorLogLevel() throws Exception {
        testDynamicLogLevel(PROTOCOL_HTTP, PROTOCOL_HTTP, false, "Error");
    }

    /**
     * Server runs HTTP, no SPNEGO.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLogLevelByHttp() throws Exception {
        testDynamicLogLevel(PROTOCOL_HTTP, PROTOCOL_HTTP, false);
        try {
            testDynamicLogLevel(PROTOCOL_HTTP, PROTOCOL_HTTPS, false);
            Assert.fail(("A HTTPS Client should not have succeeded in connecting to a " + "HTTP server"));
        } catch (SSLException e) {
            GenericTestUtils.assertExceptionContains(("Error while authenticating " + "with endpoint"), e);
            GenericTestUtils.assertExceptionContains("recognized SSL message", e.getCause());
        }
    }

    /**
     * Server runs HTTP + SPNEGO.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLogLevelByHttpWithSpnego() throws Exception {
        testDynamicLogLevel(PROTOCOL_HTTP, PROTOCOL_HTTP, true);
        try {
            testDynamicLogLevel(PROTOCOL_HTTP, PROTOCOL_HTTPS, true);
            Assert.fail(("A HTTPS Client should not have succeeded in connecting to a " + "HTTP server"));
        } catch (SSLException e) {
            GenericTestUtils.assertExceptionContains(("Error while authenticating " + "with endpoint"), e);
            GenericTestUtils.assertExceptionContains("recognized SSL message", e.getCause());
        }
    }

    /**
     * Server runs HTTPS, no SPNEGO.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLogLevelByHttps() throws Exception {
        testDynamicLogLevel(PROTOCOL_HTTPS, PROTOCOL_HTTPS, false);
        try {
            testDynamicLogLevel(PROTOCOL_HTTPS, PROTOCOL_HTTP, false);
            Assert.fail(("A HTTP Client should not have succeeded in connecting to a " + "HTTPS server"));
        } catch (SocketException e) {
            GenericTestUtils.assertExceptionContains(("Error while authenticating " + "with endpoint"), e);
            GenericTestUtils.assertExceptionContains("Unexpected end of file from server", e.getCause());
        }
    }

    /**
     * Server runs HTTPS + SPNEGO.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLogLevelByHttpsWithSpnego() throws Exception {
        testDynamicLogLevel(PROTOCOL_HTTPS, PROTOCOL_HTTPS, true);
        try {
            testDynamicLogLevel(PROTOCOL_HTTPS, PROTOCOL_HTTP, true);
            Assert.fail(("A HTTP Client should not have succeeded in connecting to a " + "HTTPS server"));
        } catch (SocketException e) {
            GenericTestUtils.assertExceptionContains(("Error while authenticating " + "with endpoint"), e);
            GenericTestUtils.assertExceptionContains("Unexpected end of file from server", e.getCause());
        }
    }
}

