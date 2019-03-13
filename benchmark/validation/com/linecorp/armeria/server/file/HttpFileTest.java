/**
 * Copyright 2019 LINE Corporation
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
package com.linecorp.armeria.server.file;


import HttpHeaderNames.CACHE_CONTROL;
import HttpHeaderNames.CONTENT_TYPE;
import MediaType.PLAIN_TEXT_UTF_8;
import ServerCacheControl.REVALIDATED;
import com.linecorp.armeria.common.HttpHeaders;
import org.junit.Test;


public class HttpFileTest {
    @Test
    public void additionalHeaders() throws Exception {
        final HttpFile f = contentType(PLAIN_TEXT_UTF_8).cacheControl(REVALIDATED).build();
        // Make sure content-type auto-detection is disabled.
        assertThat(contentType()).isNull();
        // Make sure all additional headers are set as expected.
        final HttpHeaders headers = f.readHeaders();
        assertThat(headers).isNotNull();
        assertThat(headers.getAll(com.linecorp.armeria.common.HttpHeaderNames.of("foo"))).containsExactly("1", "2");
        assertThat(headers.getAll(com.linecorp.armeria.common.HttpHeaderNames.of("bar"))).containsExactly("3");
        assertThat(headers.getAll(CONTENT_TYPE)).containsExactly(PLAIN_TEXT_UTF_8.toString());
        assertThat(headers.getAll(CACHE_CONTROL)).containsExactly(REVALIDATED.asHeaderValue());
    }
}

