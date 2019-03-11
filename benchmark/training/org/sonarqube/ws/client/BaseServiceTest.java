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
import MediaTypes.PROTOBUF;
import Testing.Fake;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonarqube.ws.Testing;


public class BaseServiceTest {
    WsConnector wsConnector = Mockito.mock(WsConnector.class);

    @Test
    public void test_call() throws Exception {
        new BaseService(wsConnector, "api/issues") {
            public void test() throws IOException {
                GetRequest get = new GetRequest(path("issue")).setMediaType(JSON);
                Mockito.when(wsConnector.call(get)).thenReturn(new MockWsResponse().setContent("ok"));
                WsResponse response = call(get);
                assertThat(response.content()).isEqualTo("ok");
            }
        }.test();
    }

    @Test
    public void call_and_convert_protobuf() {
        new BaseService(wsConnector, "api/issues") {
            public void test() {
                GetRequest get = setParam("key", "ABC");
                Mockito.when(wsConnector.call(get)).thenReturn(BaseServiceTest.newProtobufFakeResponse());
                Testing.Fake message = call(get, Fake.parser());
                assertThat(message.getLabel()).isEqualTo("ok");
                assertThat(get.getPath()).isEqualTo("api/issues/issue");
                // media type automatically set to protobuf
                assertThat(get.getMediaType()).isEqualTo(PROTOBUF);
            }
        }.test();
    }

    @Test
    public void fail_if_http_error() {
        new BaseService(wsConnector, "api/issues") {
            public void test() {
                GetRequest get = setParam("key", "ABC");
                Mockito.when(wsConnector.call(get)).thenReturn(new MockWsResponse().setCode(403).setRequestUrl("https://local/foo").setContent("error"));
                try {
                    call(get, Fake.parser());
                    Assert.fail();
                } catch (HttpException e) {
                    assertThat(e.code()).isEqualTo(403);
                }
            }
        }.test();
    }

    @Test
    public void fail_to_parse_protobuf_response() {
        new BaseService(wsConnector, "api/issues") {
            public void test() {
                GetRequest get = setParam("key", "ABC");
                Mockito.when(wsConnector.call(get)).thenReturn(MockWsResponse.createJson("{}").setRequestUrl("http://local/api/issues/issue?key=ABC"));
                try {
                    call(get, Fake.parser());
                    Assert.fail();
                } catch (IllegalStateException e) {
                    assertThat(e).hasMessage("Fail to parse protobuf response of http://local/api/issues/issue?key=ABC");
                }
            }
        }.test();
    }
}

