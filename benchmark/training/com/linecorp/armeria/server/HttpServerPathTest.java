/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server;


import HttpStatus.BAD_REQUEST;
import HttpStatus.OK;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.testing.server.ServerRule;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.ClassRule;
import org.junit.Test;


public class HttpServerPathTest {
    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.service("/service/foo", new AbstractHttpService() {
                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                    return HttpResponse.of(OK);
                }
            });
            sb.serviceUnder("/", new AbstractHttpService() {
                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                    return HttpResponse.of(OK);
                }
            });
        }
    };

    private static final Map<String, HttpStatus> TEST_URLS = new LinkedHashMap<>();

    static {
        // 200 test
        HttpServerPathTest.TEST_URLS.put("/", OK);
        HttpServerPathTest.TEST_URLS.put("//", OK);
        HttpServerPathTest.TEST_URLS.put("/service//foo", OK);
        HttpServerPathTest.TEST_URLS.put("/service/foo..bar", OK);
        HttpServerPathTest.TEST_URLS.put("/service..hello/foobar", OK);
        HttpServerPathTest.TEST_URLS.put("/service//test//////a/", OK);
        HttpServerPathTest.TEST_URLS.put("/service//test//////a/?flag=hello", OK);
        HttpServerPathTest.TEST_URLS.put("/service/foo:bar", OK);
        HttpServerPathTest.TEST_URLS.put("/service/foo::::::bar", OK);
        HttpServerPathTest.TEST_URLS.put("/cache/v1.0/rnd_team/get/krisjey:56578015655:1223", OK);
        HttpServerPathTest.TEST_URLS.put("/signout/56578015655?crumb=s-1475829101-cec4230588-%E2%98%83", OK);
        HttpServerPathTest.TEST_URLS.put(("/search/num=20&newwindow=1&espv=2&q=url+path+colon&oq=url+path+colon&gs_l=serp.3" + (("..0i30k1.80626.89265.0.89464.18.16.1.1.1.0.154.1387.0j12.12.0....0...1c.1j4.64.s" + "erp..4.14.1387...0j35i39k1j0i131k1j0i19k1j0i30i19k1j0i8i30i19k1j0i5i30i19k1j0i8i10") + "i30i19k1.Z6SsEq-rZDw")), OK);
        // Should allow the asterisk character in the path
        HttpServerPathTest.TEST_URLS.put("/service/foo*bar4", OK);
        // OK as long as double dots are not used as a 'parent directory'
        HttpServerPathTest.TEST_URLS.put("/..service/foobar1", OK);
        HttpServerPathTest.TEST_URLS.put("/service../foobar2", OK);
        HttpServerPathTest.TEST_URLS.put("/service/foobar3..", OK);
        // 400 test
        HttpServerPathTest.TEST_URLS.put("..", BAD_REQUEST);
        HttpServerPathTest.TEST_URLS.put(".\\", BAD_REQUEST);
        HttpServerPathTest.TEST_URLS.put("something", BAD_REQUEST);
        HttpServerPathTest.TEST_URLS.put("/service/foo|bar5", BAD_REQUEST);
        HttpServerPathTest.TEST_URLS.put("/service/foo\\bar6", BAD_REQUEST);
        HttpServerPathTest.TEST_URLS.put("/\\\\", BAD_REQUEST);
        HttpServerPathTest.TEST_URLS.put("/service/foo>bar", BAD_REQUEST);
        HttpServerPathTest.TEST_URLS.put("/service/foo<bar", BAD_REQUEST);
        HttpServerPathTest.TEST_URLS.put("/gwturl#user:45/comments", BAD_REQUEST);
        HttpServerPathTest.TEST_URLS.put("/service:name/hello", BAD_REQUEST);
        HttpServerPathTest.TEST_URLS.put("/service::::name/hello", BAD_REQUEST);
    }

    @Test(timeout = 10000)
    public void testPathOfUrl() throws Exception {
        for (Map.Entry<String, HttpStatus> url : HttpServerPathTest.TEST_URLS.entrySet()) {
            HttpServerPathTest.urlPathAssertion(url.getValue(), url.getKey());
        }
    }
}

