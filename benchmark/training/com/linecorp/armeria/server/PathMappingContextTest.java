/**
 * Copyright 2017 LINE Corporation
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


import HttpHeaderNames.ACCEPT;
import MediaType.JSON_UTF_8;
import MediaType.XML_UTF_8;
import com.google.common.collect.ImmutableList;
import com.linecorp.armeria.common.DefaultHttpHeaders;
import com.linecorp.armeria.common.HttpMethod;
import com.linecorp.armeria.common.MediaTypeSet;
import java.util.List;
import org.junit.Test;

import static com.linecorp.armeria.common.MediaType.JSON_UTF_8;


public class PathMappingContextTest {
    @Test
    public void testProduceTypes() {
        final DefaultHttpHeaders headers = new DefaultHttpHeaders();
        headers.add(ACCEPT, "text/html, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8");
        final MediaTypeSet producibleTypes = new MediaTypeSet(com.linecorp.armeria.common.MediaType.create("application", "xml"), com.linecorp.armeria.common.MediaType.create("text", "html"));
        final List<com.linecorp.armeria.common.MediaType> selectedTypes = DefaultPathMappingContext.resolveProduceTypes(headers, producibleTypes);
        assertThat(selectedTypes).hasSize(3);
        assertThat(selectedTypes.get(0).type()).isEqualTo("text");
        assertThat(selectedTypes.get(1).type()).isEqualTo("application");
        assertThat(selectedTypes.get(2).type()).isEqualTo("*");
    }

    @Test
    public void testProduceTypes2() {
        final DefaultHttpHeaders headers = new DefaultHttpHeaders();
        headers.add(ACCEPT, "text/html ;charset=UTF-8, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8");
        final MediaTypeSet producibleTypes = new MediaTypeSet(com.linecorp.armeria.common.MediaType.create("application", "xml"), com.linecorp.armeria.common.MediaType.create("text", "html"));
        final List<com.linecorp.armeria.common.MediaType> selectedTypes = DefaultPathMappingContext.resolveProduceTypes(headers, producibleTypes);
        assertThat(selectedTypes).hasSize(2);
        assertThat(selectedTypes.get(0).type()).isEqualTo("application");
        assertThat(selectedTypes.get(1).type()).isEqualTo("*");
    }

    @Test
    public void testCompareMediaTypes() {
        // Sort by their quality factor.
        assertThat(DefaultPathMappingContext.compareMediaType(com.linecorp.armeria.common.MediaType.parse("application/octet-stream;q=0.8"), com.linecorp.armeria.common.MediaType.parse("text/plain;q=0.9"))).isGreaterThan(0);
        // Sort by their coverage. (the number of wildcards)
        assertThat(DefaultPathMappingContext.compareMediaType(com.linecorp.armeria.common.MediaType.parse("text/*;q=0.9"), com.linecorp.armeria.common.MediaType.parse("text/plain;q=0.9"))).isGreaterThan(0);
        // Sort by lexicographic order.
        assertThat(DefaultPathMappingContext.compareMediaType(com.linecorp.armeria.common.MediaType.parse("text/plain;q=0.9"), com.linecorp.armeria.common.MediaType.parse("application/octet-stream;q=0.9"))).isGreaterThan(0);
    }

    @Test
    public void testEquals() {
        final VirtualHost virtualHost = PathMappingContextTest.virtualHost();
        PathMappingContext ctx1;
        PathMappingContext ctx2;
        final PathMappingContext ctx3;
        ctx1 = new DefaultPathMappingContext(virtualHost, "example.com", HttpMethod.GET, "/hello", null, JSON_UTF_8, ImmutableList.of(JSON_UTF_8, XML_UTF_8));
        ctx2 = new DefaultPathMappingContext(virtualHost, "example.com", HttpMethod.GET, "/hello", null, JSON_UTF_8, ImmutableList.of(JSON_UTF_8, XML_UTF_8));
        ctx3 = new DefaultPathMappingContext(virtualHost, "example.com", HttpMethod.GET, "/hello", null, JSON_UTF_8, ImmutableList.of(XML_UTF_8, JSON_UTF_8));
        assertThat(ctx1.hashCode()).isEqualTo(ctx2.hashCode());
        assertThat(ctx1).isEqualTo(ctx2);
        assertThat(ctx1).isNotEqualTo(ctx3);
        ctx1 = new DefaultPathMappingContext(virtualHost, "example.com", HttpMethod.GET, "/hello", "a=1&b=1", null, null);
        ctx2 = new DefaultPathMappingContext(virtualHost, "example.com", HttpMethod.GET, "/hello", "a=1", null, null);
        assertThat(ctx1.hashCode()).isEqualTo(ctx2.hashCode());
        assertThat(ctx1).isEqualTo(ctx2);
    }
}

