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
package com.linecorp.armeria.internal;


import Http2Error.INTERNAL_ERROR;
import Http2Error.PROTOCOL_ERROR;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import org.junit.Test;


public class Http2GoAwayHandlerTest {
    @Test
    public void testIsExpected() {
        final ByteBuf errorFlushing = Unpooled.copiedBuffer("Error flushing", StandardCharsets.UTF_8);
        final ByteBuf errorFlushing2 = Unpooled.copiedBuffer("Error flushing stream", StandardCharsets.UTF_8);
        final ByteBuf other = Unpooled.copiedBuffer("Other reasons", StandardCharsets.UTF_8);
        assertThat(Http2GoAwayHandler.isExpected(INTERNAL_ERROR.code(), errorFlushing)).isTrue();
        assertThat(Http2GoAwayHandler.isExpected(PROTOCOL_ERROR.code(), errorFlushing)).isFalse();
        assertThat(Http2GoAwayHandler.isExpected(INTERNAL_ERROR.code(), errorFlushing2)).isTrue();
        assertThat(Http2GoAwayHandler.isExpected(INTERNAL_ERROR.code(), other)).isFalse();
    }
}

