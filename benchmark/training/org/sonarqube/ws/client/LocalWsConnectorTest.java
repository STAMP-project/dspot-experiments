/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarqube.ws.client;


import MediaTypes.JSON;
import com.google.common.collect.ImmutableMap;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.server.ws.LocalConnector;
import org.sonarqube.ws.MediaTypes;


public class LocalWsConnectorTest {
    LocalConnector connector = Mockito.mock(LocalConnector.class);

    LocalWsConnector underTest = new LocalWsConnector(connector);

    @Test
    public void baseUrl_is_always_slash() {
        assertThat(underTest.baseUrl()).isEqualTo("/");
    }

    @Test
    public void call_request() throws Exception {
        WsRequest wsRequest = new PostRequest("api/issues/search").setMediaType(JSON).setParam("foo", "bar");
        answer(new LocalWsConnectorTest.DumbLocalResponse(400, MediaTypes.JSON, "{}".getBytes(StandardCharsets.UTF_8), Collections.<String>emptyList()));
        WsResponse wsResponse = underTest.call(wsRequest);
        verifyRequested("POST", "api/issues/search", JSON, ImmutableMap.of("foo", "bar"));
        assertThat(wsResponse.code()).isEqualTo(400);
        assertThat(wsResponse.content()).isEqualTo("{}");
        assertThat(IOUtils.toString(wsResponse.contentReader())).isEqualTo("{}");
        assertThat(IOUtils.toString(wsResponse.contentStream())).isEqualTo("{}");
        assertThat(wsResponse.contentType()).isEqualTo(JSON);
        assertThat(wsResponse.requestUrl()).isEqualTo("api/issues/search");
    }

    @Test
    public void call_request_with_defaults() throws Exception {
        // no parameters, no media type
        WsRequest wsRequest = new GetRequest("api/issues/search");
        answer(new LocalWsConnectorTest.DumbLocalResponse(200, MediaTypes.JSON, "".getBytes(StandardCharsets.UTF_8), Collections.<String>emptyList()));
        WsResponse wsResponse = underTest.call(wsRequest);
        verifyRequested("GET", "api/issues/search", JSON, Collections.<String, String>emptyMap());
        assertThat(wsResponse.code()).isEqualTo(200);
        assertThat(wsResponse.content()).isEqualTo("");
        assertThat(IOUtils.toString(wsResponse.contentReader())).isEqualTo("");
        assertThat(IOUtils.toString(wsResponse.contentStream())).isEqualTo("");
        assertThat(wsResponse.contentType()).isEqualTo(JSON);
    }

    private static class DumbLocalResponse implements LocalConnector.LocalResponse {
        private final int code;

        private final String mediaType;

        private final byte[] bytes;

        private final List<String> headers;

        public DumbLocalResponse(int code, String mediaType, byte[] bytes, List<String> headers) {
            this.code = code;
            this.mediaType = mediaType;
            this.bytes = bytes;
            this.headers = headers;
        }

        @Override
        public int getStatus() {
            return code;
        }

        @Override
        public String getMediaType() {
            return mediaType;
        }

        @Override
        public byte[] getBytes() {
            return bytes;
        }

        @Override
        public Collection<String> getHeaderNames() {
            return headers;
        }

        @Override
        public String getHeader(String name) {
            throw new UnsupportedOperationException();
        }
    }
}

