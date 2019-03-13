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
package org.springframework.cloud.gateway.filter.headers;


import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;


/**
 *
 *
 * @author Spencer Gibb
 * @author Biju Kunjummen
 */
public class HttpHeadersFilterTests {
    @Test
    public void httpHeadersFilterTests() {
        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost:8080/get").header("X-A", "aValue").header("X-B", "bValue").header("X-C", "cValue").build();
        List<HttpHeadersFilter> filters = Arrays.asList(( h, e) -> this.filter(h, "X-A"), ( h, e) -> this.filter(h, "X-B"));
        HttpHeaders headers = HttpHeadersFilter.filterRequest(filters, MockServerWebExchange.from(request));
        assertThat(headers).containsOnlyKeys("X-C");
    }
}

