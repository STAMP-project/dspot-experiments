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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.templeton;


import HttpStatus.NOT_FOUND_404;
import HttpStatus.NOT_IMPLEMENTED_501;
import HttpStatus.OK_200;
import java.io.IOException;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A set of tests exercising e2e WebHCat DDL APIs.  These tests are somewhat
 * between WebHCat e2e (hcatalog/src/tests/e2e/templeton) tests and simple58
 *
 * unit tests.  This will start a WebHCat server and make REST calls to it.
 * It doesn't need Hadoop or (standalone) metastore to be running.
 * Running this is much simpler than e2e tests.
 *
 * Most of these tests check that HTTP Status code is what is expected and
 * Hive Error code {@link org.apache.hadoop.hive.ql.ErrorMsg} is what is
 * expected.
 *
 * It may be possible to extend this to more than just DDL later.
 */
public class TestWebHCatE2e {
    private static final Logger LOG = LoggerFactory.getLogger(TestWebHCatE2e.class);

    private static String templetonBaseUrl = "http://localhost:50111/templeton/v1";

    private static final String username = "johndoe";

    private static final String ERROR_CODE = "errorCode";

    private static Main templetonServer;

    private static final String charSet = "UTF-8";

    @Test
    public void getStatus() throws IOException {
        TestWebHCatE2e.LOG.debug("+getStatus()");
        TestWebHCatE2e.MethodCallRetVal p = TestWebHCatE2e.doHttpCall(((TestWebHCatE2e.templetonBaseUrl) + "/status"), TestWebHCatE2e.HTTP_METHOD_TYPE.GET);
        Assert.assertEquals(p.getAssertMsg(), OK_200, p.httpStatusCode);
        // Must be deterministic order map for comparison across Java versions
        Assert.assertTrue(p.getAssertMsg(), TestWebHCatE2e.jsonStringToSortedMap("{\"status\":\"ok\",\"version\":\"v1\"}").equals(TestWebHCatE2e.jsonStringToSortedMap(p.responseBody)));
        TestWebHCatE2e.LOG.debug("-getStatus()");
    }

    /**
     * Check that we return correct status code when the URL doesn't map to any method
     * in {@link Server}
     */
    @Test
    public void invalidPath() throws IOException {
        TestWebHCatE2e.MethodCallRetVal p = TestWebHCatE2e.doHttpCall(((TestWebHCatE2e.templetonBaseUrl) + "/no_such_mapping/database"), TestWebHCatE2e.HTTP_METHOD_TYPE.GET);
        Assert.assertEquals(p.getAssertMsg(), NOT_FOUND_404, p.httpStatusCode);
    }

    @Test
    public void getHadoopVersion() throws Exception {
        TestWebHCatE2e.MethodCallRetVal p = TestWebHCatE2e.doHttpCall(((TestWebHCatE2e.templetonBaseUrl) + "/version/hadoop"), TestWebHCatE2e.HTTP_METHOD_TYPE.GET);
        Assert.assertEquals(OK_200, p.httpStatusCode);
        Map<String, Object> props = JsonBuilder.jsonToMap(p.responseBody);
        Assert.assertEquals("hadoop", props.get("module"));
        Assert.assertTrue(p.getAssertMsg(), ((String) (props.get("version"))).matches("[1-3].[0-9]+.[0-9]+.*"));
    }

    @Test
    public void getHiveVersion() throws Exception {
        TestWebHCatE2e.MethodCallRetVal p = TestWebHCatE2e.doHttpCall(((TestWebHCatE2e.templetonBaseUrl) + "/version/hive"), TestWebHCatE2e.HTTP_METHOD_TYPE.GET);
        Assert.assertEquals(OK_200, p.httpStatusCode);
        Map<String, Object> props = JsonBuilder.jsonToMap(p.responseBody);
        Assert.assertEquals("hive", props.get("module"));
        Assert.assertTrue(p.getAssertMsg(), ((String) (props.get("version"))).matches("[0-9]+.[0-9]+.[0-9]+.*"));
    }

    @Test
    public void getPigVersion() throws Exception {
        TestWebHCatE2e.MethodCallRetVal p = TestWebHCatE2e.doHttpCall(((TestWebHCatE2e.templetonBaseUrl) + "/version/pig"), TestWebHCatE2e.HTTP_METHOD_TYPE.GET);
        Assert.assertEquals(NOT_IMPLEMENTED_501, p.httpStatusCode);
        Map<String, Object> props = JsonBuilder.jsonToMap(p.responseBody);
        Assert.assertEquals(p.getAssertMsg(), ("Pig version request not yet " + "implemented"), ((String) (props.get("error"))));
    }

    /**
     * Encapsulates information from HTTP method call
     */
    private static class MethodCallRetVal {
        private final int httpStatusCode;

        private final String responseBody;

        private final String submittedURL;

        private final String methodName;

        private MethodCallRetVal(int httpStatusCode, String responseBody, String submittedURL, String methodName) {
            this.httpStatusCode = httpStatusCode;
            this.responseBody = responseBody;
            this.submittedURL = submittedURL;
            this.methodName = methodName;
        }

        String getAssertMsg() {
            return ((((methodName) + " ") + (submittedURL)) + " ") + (responseBody);
        }
    }

    private static enum HTTP_METHOD_TYPE {

        GET,
        POST,
        DELETE,
        PUT;}
}

