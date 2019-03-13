/**
 * Copyright 2015 LINE Corporation
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
package com.linecorp.armeria.server.thrift;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;


public class ThriftOverHttp1Test extends AbstractThriftOverHttpTest {
    @Test
    public void testNonPostRequest() throws Exception {
        final HttpUriRequest[] reqs = new HttpUriRequest[]{ new HttpGet(AbstractThriftOverHttpTest.newUri("http", "/hello")), new HttpDelete(AbstractThriftOverHttpTest.newUri("http", "/hello")) };
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            for (HttpUriRequest r : reqs) {
                try (CloseableHttpResponse res = hc.execute(r)) {
                    assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 405 Method Not Allowed");
                    assertThat(EntityUtils.toString(res.getEntity())).isNotEqualTo("Hello, world!");
                }
            }
        }
    }
}

