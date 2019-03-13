/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.netflix.zuul.filters.route;


import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpRequest.Verb;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import org.junit.Test;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.LinkedMultiValueMap;


/**
 *
 *
 * @author Spencer Gibb
 */
public class RestClientRibbonCommandTests {
    private ZuulProperties zuulProperties;

    /**
     * Tests old constructors kept for backwards compatibility with Spring Cloud Sleuth
     * 1.x versions
     */
    @Test
    @Deprecated
    public void testNullEntityWithOldConstruct() throws Exception {
        String uri = "http://example.com";
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap();
        headers.add("my-header", "my-value");
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap();
        params.add("myparam", "myparamval");
        RestClientRibbonCommand command = new RestClientRibbonCommand("cmd", null, Verb.GET, uri, false, headers, params, null);
        HttpRequest request = command.createRequest();
        assertThat(request.getUri().toString()).as("uri is wrong").startsWith(uri);
        assertThat(request.getHttpHeaders().getFirstValue("my-header")).as("my-header is wrong").isEqualTo("my-value");
        assertThat(request.getQueryParams().get("myparam").iterator().next()).as("myparam is missing").isEqualTo("myparamval");
        command = new RestClientRibbonCommand("cmd", null, new org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext("example", "GET", uri, false, headers, params, null), zuulProperties);
        request = command.createRequest();
        assertThat(request.getUri().toString()).as("uri is wrong").startsWith(uri);
        assertThat(request.getHttpHeaders().getFirstValue("my-header")).as("my-header is wrong").isEqualTo("my-value");
        assertThat(request.getQueryParams().get("myparam").iterator().next()).as("myparam is missing").isEqualTo("myparamval");
    }

    @Test
    public void testNullEntity() throws Exception {
        String uri = "http://example.com";
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap();
        headers.add("my-header", "my-value");
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap();
        params.add("myparam", "myparamval");
        RestClientRibbonCommand command = new RestClientRibbonCommand("cmd", null, new org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext("example", "GET", uri, false, headers, params, null, new ArrayList<org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer>()), zuulProperties);
        HttpRequest request = command.createRequest();
        assertThat(request.getUri().toString()).as("uri is wrong").startsWith(uri);
        assertThat(request.getHttpHeaders().getFirstValue("my-header")).as("my-header is wrong").isEqualTo("my-value");
        assertThat(request.getQueryParams().get("myparam").iterator().next()).as("myparam is missing").isEqualTo("myparamval");
    }

    // this situation happens, see
    // https://github.com/spring-cloud/spring-cloud-netflix/issues/1042#issuecomment-227723877
    @Test
    public void testEmptyEntityGet() throws Exception {
        String entityValue = "";
        testEntity(entityValue, new ByteArrayInputStream(entityValue.getBytes()), false, "GET");
    }

    @Test
    public void testNonEmptyEntityPost() throws Exception {
        String entityValue = "abcd";
        testEntity(entityValue, new ByteArrayInputStream(entityValue.getBytes()), true, "POST");
    }

    @Test
    public void testNonEmptyEntityDelete() throws Exception {
        String entityValue = "abcd";
        testEntity(entityValue, new ByteArrayInputStream(entityValue.getBytes()), true, "DELETE");
    }
}

