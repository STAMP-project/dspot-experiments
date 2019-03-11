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
package org.springframework.cloud.netflix.ribbon.okhttp;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import okhttp3.Request;
import org.junit.Test;
import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.util.LinkedMultiValueMap;


/**
 *
 *
 * @author Spencer Gibb
 */
public class OkHttpRibbonRequestTests {
    @Test
    public void testNullEntity() throws Exception {
        String uri = "http://example.com";
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap();
        headers.add("my-header", "my-value");
        // headers.add(HttpEncoding.CONTENT_LENGTH, "5192");
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap();
        params.add("myparam", "myparamval");
        RibbonCommandContext context = new RibbonCommandContext("example", "GET", uri, false, headers, params, null, new ArrayList<org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer>());
        OkHttpRibbonRequest httpRequest = new OkHttpRibbonRequest(context);
        Request request = httpRequest.toRequest();
        assertThat(request.body()).as("body is not null").isNull();
        assertThat(request.url().toString()).as("uri is wrong").startsWith(uri);
        assertThat(request.header("my-header")).as("my-header is wrong").isEqualTo("my-value");
        assertThat(request.url().queryParameter("myparam")).as("myparam is missing").isEqualTo("myparamval");
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
}

