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
package org.springframework.cloud.gateway.filter.factory;


import java.net.URI;
import org.junit.Test;
import org.springframework.web.server.ServerWebExchange;


/**
 *
 *
 * @author Spencer Gibb
 */
public class RewritePathGatewayFilterFactoryTests {
    @Test
    public void rewritePathFilterWorks() {
        testRewriteFilter("/foo", "/baz", "/foo/bar", "/baz/bar");
    }

    @Test
    public void rewriteEncodedPathFilterWorks() {
        testRewriteFilter("/foo", "/baz", "/foo/bar%20foobar", "/baz/bar foobar");
    }

    @Test
    public void rewritePathFilterWithNamedGroupWorks() {
        testRewriteFilter("/foo/(?<id>\\d.*)", "/bar/baz/$\\{id}", "/foo/123", "/bar/baz/123");
    }

    @Test
    public void rewritePathWithEncodedParams() {
        ServerWebExchange exchange = testRewriteFilter("/foo", "/baz", "/foo/bar?name=%E6%89%8E%E6%A0%B9", "/baz/bar");
        URI uri = exchange.getRequest().getURI();
        assertThat(uri.getRawQuery()).isEqualTo("name=%E6%89%8E%E6%A0%B9");
    }
}

