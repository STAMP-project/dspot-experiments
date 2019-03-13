/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.http.client;


import HttpMethod.GET;
import HttpStatus.OK;
import java.net.URI;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class StreamingSimpleClientHttpRequestFactoryTests extends AbstractHttpRequestFactoryTestCase {
    // SPR-8809
    @Test
    public void interceptor() throws Exception {
        final String headerName = "MyHeader";
        final String headerValue = "MyValue";
        ClientHttpRequestInterceptor interceptor = ( request, body, execution) -> {
            request.getHeaders().add(headerName, headerValue);
            return execution.execute(request, body);
        };
        InterceptingClientHttpRequestFactory factory = new InterceptingClientHttpRequestFactory(createRequestFactory(), Collections.singletonList(interceptor));
        ClientHttpResponse response = null;
        try {
            ClientHttpRequest request = factory.createRequest(new URI(((baseUrl) + "/echo")), GET);
            response = request.execute();
            Assert.assertEquals("Invalid response status", OK, response.getStatusCode());
            org.springframework.http.HttpHeaders responseHeaders = response.getHeaders();
            Assert.assertEquals("Custom header invalid", headerValue, responseHeaders.getFirst(headerName));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}

