/**
 * Copyright 2018-2019 the original author or authors.
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
package org.springframework.cloud.gateway.filter.headers;


import Type.REQUEST;
import Type.RESPONSE;
import java.util.Arrays;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;


/**
 *
 *
 * @author Biju Kunjummen
 */
public class HttpHeadersFilterMixedTypeTests {
    @Test
    public void relevantDownstreamFiltersShouldActOnHeaders() {
        MockServerHttpRequest mockRequest = MockServerHttpRequest.get("/get").header("header1", "value1").header("header2", "value2").header("header3", "value3").build();
        HttpHeadersFilter filter1 = filterRemovingHeaders(RESPONSE, "header1");
        HttpHeadersFilter filter2 = filterRemovingHeaders(REQUEST, "header2");
        HttpHeaders result = HttpHeadersFilter.filterRequest(Arrays.asList(filter1, filter2), MockServerWebExchange.from(mockRequest));
        assertThat(result).containsOnlyKeys("header1", "header3");
    }
}

