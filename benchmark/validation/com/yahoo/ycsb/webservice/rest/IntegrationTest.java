/**
 * Copyright (c) 2016 YCSB contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.yahoo.ycsb.webservice.rest;


import com.yahoo.ycsb.Client;
import java.util.List;
import org.apache.catalina.startup.Tomcat;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runners.MethodSorters;


/**
 * Integration test cases to verify the end to end working of the rest-binding
 * module. It performs these steps in order. 1. Runs an embedded Tomcat
 * server with a mock RESTFul web service. 2. Invokes the {@link Client}
 * class with the required parameters to start benchmarking the mock REST
 * service. 3. Compares the response stored in the output file by {@link Client}
 * class with the response expected. 4. Stops the embedded Tomcat server.
 * Cases for verifying the handling of different HTTP status like 2xx & 5xx have
 * been included in success and failure test cases.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntegrationTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    private static int port = 8080;

    private static Tomcat tomcat;

    private static final String WORKLOAD_FILEPATH = IntegrationTest.class.getClassLoader().getResource("workload_rest").getPath();

    private static final String TRACE_FILEPATH = IntegrationTest.class.getClassLoader().getResource("trace.txt").getPath();

    private static final String ERROR_TRACE_FILEPATH = IntegrationTest.class.getClassLoader().getResource("error_trace.txt").getPath();

    private static final String RESULTS_FILEPATH = (IntegrationTest.class.getClassLoader().getResource(".").getPath()) + "results.txt";

    // All read operations during benchmark are executed successfully with an HTTP OK status.
    @Test
    public void testReadOpsBenchmarkSuccess() throws InterruptedException {
        exit.expectSystemExit();
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                List<String> results = Utils.read(IntegrationTest.RESULTS_FILEPATH);
                Assert.assertEquals(true, results.contains("[READ], Return=OK, 1"));
                Utils.delete(IntegrationTest.RESULTS_FILEPATH);
            }
        });
        Client.main(getArgs(IntegrationTest.TRACE_FILEPATH, 1, 0, 0, 0));
    }

    // All read operations during benchmark are executed with an HTTP 500 error.
    @Test
    public void testReadOpsBenchmarkFailure() throws InterruptedException {
        exit.expectSystemExit();
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                List<String> results = Utils.read(IntegrationTest.RESULTS_FILEPATH);
                Assert.assertEquals(true, results.contains("[READ], Return=ERROR, 1"));
                Utils.delete(IntegrationTest.RESULTS_FILEPATH);
            }
        });
        Client.main(getArgs(IntegrationTest.ERROR_TRACE_FILEPATH, 1, 0, 0, 0));
    }

    // All insert operations during benchmark are executed successfully with an HTTP OK status.
    @Test
    public void testInsertOpsBenchmarkSuccess() throws InterruptedException {
        exit.expectSystemExit();
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                List<String> results = Utils.read(IntegrationTest.RESULTS_FILEPATH);
                Assert.assertEquals(true, results.contains("[INSERT], Return=OK, 1"));
                Utils.delete(IntegrationTest.RESULTS_FILEPATH);
            }
        });
        Client.main(getArgs(IntegrationTest.TRACE_FILEPATH, 0, 1, 0, 0));
    }

    // All read operations during benchmark are executed with an HTTP 500 error.
    @Test
    public void testInsertOpsBenchmarkFailure() throws InterruptedException {
        exit.expectSystemExit();
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                List<String> results = Utils.read(IntegrationTest.RESULTS_FILEPATH);
                Assert.assertEquals(true, results.contains("[INSERT], Return=ERROR, 1"));
                Utils.delete(IntegrationTest.RESULTS_FILEPATH);
            }
        });
        Client.main(getArgs(IntegrationTest.ERROR_TRACE_FILEPATH, 0, 1, 0, 0));
    }

    // All update operations during benchmark are executed successfully with an HTTP OK status.
    @Test
    public void testUpdateOpsBenchmarkSuccess() throws InterruptedException {
        exit.expectSystemExit();
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                List<String> results = Utils.read(IntegrationTest.RESULTS_FILEPATH);
                Assert.assertEquals(true, results.contains("[UPDATE], Return=OK, 1"));
                Utils.delete(IntegrationTest.RESULTS_FILEPATH);
            }
        });
        Client.main(getArgs(IntegrationTest.TRACE_FILEPATH, 0, 0, 1, 0));
    }

    // All read operations during benchmark are executed with an HTTP 500 error.
    @Test
    public void testUpdateOpsBenchmarkFailure() throws InterruptedException {
        exit.expectSystemExit();
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                List<String> results = Utils.read(IntegrationTest.RESULTS_FILEPATH);
                Assert.assertEquals(true, results.contains("[UPDATE], Return=ERROR, 1"));
                Utils.delete(IntegrationTest.RESULTS_FILEPATH);
            }
        });
        Client.main(getArgs(IntegrationTest.ERROR_TRACE_FILEPATH, 0, 0, 1, 0));
    }

    // All delete operations during benchmark are executed successfully with an HTTP OK status.
    @Test
    public void testDeleteOpsBenchmarkSuccess() throws InterruptedException {
        exit.expectSystemExit();
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                List<String> results = Utils.read(IntegrationTest.RESULTS_FILEPATH);
                Assert.assertEquals(true, results.contains("[DELETE], Return=OK, 1"));
                Utils.delete(IntegrationTest.RESULTS_FILEPATH);
            }
        });
        Client.main(getArgs(IntegrationTest.TRACE_FILEPATH, 0, 0, 0, 1));
    }

    // All read operations during benchmark are executed with an HTTP 500 error.
    @Test
    public void testDeleteOpsBenchmarkFailure() throws InterruptedException {
        exit.expectSystemExit();
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                List<String> results = Utils.read(IntegrationTest.RESULTS_FILEPATH);
                Assert.assertEquals(true, results.contains("[DELETE], Return=ERROR, 1"));
                Utils.delete(IntegrationTest.RESULTS_FILEPATH);
            }
        });
        Client.main(getArgs(IntegrationTest.ERROR_TRACE_FILEPATH, 0, 0, 0, 1));
    }
}

