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
package org.springframework.cloud.gateway.filter;


import org.junit.Test;


public class WebsocketRoutingFilterTests {
    @Test
    public void testConvertHttpToWs() {
        assertThat(WebsocketRoutingFilter.convertHttpToWs("http")).isEqualTo("ws");
        assertThat(WebsocketRoutingFilter.convertHttpToWs("HTTP")).isEqualTo("ws");
        assertThat(WebsocketRoutingFilter.convertHttpToWs("https")).isEqualTo("wss");
        assertThat(WebsocketRoutingFilter.convertHttpToWs("HTTPS")).isEqualTo("wss");
        assertThat(WebsocketRoutingFilter.convertHttpToWs("tcp")).isEqualTo("tcp");
    }
}

