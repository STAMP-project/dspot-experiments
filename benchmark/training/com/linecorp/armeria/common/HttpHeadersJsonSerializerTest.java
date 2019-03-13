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
package com.linecorp.armeria.common;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.AsciiString;
import org.junit.Test;


public class HttpHeadersJsonSerializerTest {
    private static final AsciiString NAME = HttpHeaderNames.of("a");

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void singleValue() {
        assertThatJson(HttpHeadersJsonSerializerTest.mapper.valueToTree(HttpHeaders.of(HttpHeadersJsonSerializerTest.NAME, "0"))).isEqualTo("{\"a\":\"0\"}");
    }

    @Test
    public void multipleValues() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeadersJsonSerializerTest.NAME, "0");
        headers.add(HttpHeadersJsonSerializerTest.NAME, "1");
        assertThatJson(HttpHeadersJsonSerializerTest.mapper.valueToTree(headers)).isEqualTo("{\"a\":[\"0\",\"1\"]}");
    }
}

