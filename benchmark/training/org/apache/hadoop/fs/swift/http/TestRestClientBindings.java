/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.swift.http;


import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.swift.SwiftTestConstants;
import org.apache.hadoop.fs.swift.exceptions.SwiftConfigurationException;
import org.junit.Assert;
import org.junit.Test;


public class TestRestClientBindings extends Assert implements SwiftTestConstants {
    private static final String SERVICE = "sname";

    private static final String CONTAINER = "cname";

    private static final String FS_URI = ((("swift://" + (TestRestClientBindings.CONTAINER)) + ".") + (TestRestClientBindings.SERVICE)) + "/";

    private static final String AUTH_URL = "http://localhost:8080/auth";

    private static final String USER = "user";

    private static final String PASS = "pass";

    private static final String TENANT = "tenant";

    private URI filesysURI;

    private Configuration conf;

    /* Hadoop 2.x+ only, as conf.unset() isn't a v1 feature
    public void testBindAgainstConfIncompleteInstance() throws Exception {
    String instance = RestClientBindings.buildSwiftInstancePrefix(SERVICE);
    conf.unset(instance + DOT_PASSWORD);
    expectBindingFailure(filesysURI, conf);
    }
     */
    @Test(expected = SwiftConfigurationException.class)
    public void testDottedServiceURL() throws Exception {
        RestClientBindings.bind(new URI("swift://hadoop.apache.org/"), conf);
    }

    @Test(expected = SwiftConfigurationException.class)
    public void testMissingServiceURL() throws Exception {
        RestClientBindings.bind(new URI("swift:///"), conf);
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testEmptyHostname() throws Throwable {
        TestRestClientBindings.expectExtractContainerFail("");
        TestRestClientBindings.expectExtractServiceFail("");
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testDot() throws Throwable {
        TestRestClientBindings.expectExtractContainerFail(".");
        TestRestClientBindings.expectExtractServiceFail(".");
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testSimple() throws Throwable {
        TestRestClientBindings.expectExtractContainerFail("simple");
        TestRestClientBindings.expectExtractServiceFail("simple");
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testTrailingDot() throws Throwable {
        TestRestClientBindings.expectExtractServiceFail("simple.");
    }

    @Test(timeout = SwiftTestConstants.SWIFT_TEST_TIMEOUT)
    public void testLeadingDot() throws Throwable {
        TestRestClientBindings.expectExtractServiceFail(".leading");
    }
}

