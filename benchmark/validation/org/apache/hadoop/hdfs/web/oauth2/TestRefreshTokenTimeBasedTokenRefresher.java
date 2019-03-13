/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hadoop.hdfs.web.oauth2;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Timer;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;


public class TestRefreshTokenTimeBasedTokenRefresher {
    public static final Header CONTENT_TYPE_APPLICATION_JSON = new Header("Content-Type", "application/json");

    @Test
    public void refreshUrlIsCorrect() throws IOException {
        final int PORT = 7552;
        final String REFRESH_ADDRESS = ("http://localhost:" + PORT) + "/refresh";
        long tokenExpires = 0;
        Configuration conf = buildConf("refresh token key", Long.toString(tokenExpires), "joebob", REFRESH_ADDRESS);
        Timer mockTimer = Mockito.mock(Timer.class);
        Mockito.when(mockTimer.now()).thenReturn((tokenExpires + 1000L));
        AccessTokenProvider tokenProvider = new ConfRefreshTokenBasedAccessTokenProvider(mockTimer);
        tokenProvider.setConf(conf);
        // Build mock server to receive refresh request
        ClientAndServer mockServer = startClientAndServer(PORT);
        HttpRequest expectedRequest = // Note, OkHttp does not sort the param values, so we need to
        // do it ourselves via the ordering provided to ParameterBody...
        request().withMethod("POST").withPath("/refresh").withBody(ParameterBody.params(Parameter.param(OAuth2Constants.CLIENT_ID, "joebob"), Parameter.param(OAuth2Constants.GRANT_TYPE, OAuth2Constants.REFRESH_TOKEN), Parameter.param(OAuth2Constants.REFRESH_TOKEN, "refresh token key")));
        MockServerClient mockServerClient = new MockServerClient("localhost", PORT);
        // https://tools.ietf.org/html/rfc6749#section-5.1
        Map<String, Object> map = new TreeMap<>();
        map.put(OAuth2Constants.EXPIRES_IN, "0987654321");
        map.put(OAuth2Constants.TOKEN_TYPE, OAuth2Constants.BEARER);
        map.put(OAuth2Constants.ACCESS_TOKEN, "new access token");
        ObjectMapper mapper = new ObjectMapper();
        HttpResponse resp = response().withStatusCode(HttpStatus.SC_OK).withHeaders(TestRefreshTokenTimeBasedTokenRefresher.CONTENT_TYPE_APPLICATION_JSON).withBody(mapper.writeValueAsString(map));
        mockServerClient.when(expectedRequest, exactly(1)).respond(resp);
        Assert.assertEquals("new access token", tokenProvider.getAccessToken());
        mockServerClient.verify(expectedRequest);
        mockServerClient.clear(expectedRequest);
        mockServer.stop();
    }
}

