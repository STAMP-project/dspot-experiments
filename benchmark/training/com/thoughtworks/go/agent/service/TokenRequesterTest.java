/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.agent.service;


import com.thoughtworks.go.agent.common.ssl.GoAgentServerHttpClient;
import com.thoughtworks.go.config.AgentRegistry;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TokenRequesterTest {
    @Mock
    private AgentRegistry agentRegistry;

    @Mock
    private GoAgentServerHttpClient httpClient;

    private TokenRequester tokenRequester;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldGetTokenFromServer() throws Exception {
        final ArgumentCaptor<HttpRequestBase> argumentCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        final CloseableHttpResponse httpResponse = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(agentRegistry.uuid()).thenReturn("agent-uuid");
        Mockito.when(httpClient.execute(ArgumentMatchers.any(HttpRequestBase.class))).thenReturn(httpResponse);
        Mockito.when(httpResponse.getEntity()).thenReturn(new StringEntity("token-from-server"));
        Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("https", 1, 2), HttpStatus.SC_OK, null));
        final String token = tokenRequester.getToken();
        Mockito.verify(httpClient).execute(argumentCaptor.capture());
        final HttpRequestBase requestBase = argumentCaptor.getValue();
        final List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(requestBase.getURI(), StandardCharsets.UTF_8.name());
        Assert.assertThat(token, Matchers.is("token-from-server"));
        Assert.assertThat(findParam(nameValuePairs, "uuid").getValue(), Matchers.is("agent-uuid"));
    }

    @Test
    public void shouldErrorOutIfServerRejectTheRequest() throws Exception {
        final ArgumentCaptor<HttpRequestBase> argumentCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        final CloseableHttpResponse httpResponse = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(agentRegistry.uuid()).thenReturn("agent-uuid");
        Mockito.when(httpClient.execute(ArgumentMatchers.any(HttpRequestBase.class))).thenReturn(httpResponse);
        Mockito.when(httpResponse.getEntity()).thenReturn(new StringEntity("A token has already been issued for this agent."));
        Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("https", 1, 2), HttpStatus.SC_UNPROCESSABLE_ENTITY, null));
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("A token has already been issued for this agent.");
        tokenRequester.getToken();
    }
}

