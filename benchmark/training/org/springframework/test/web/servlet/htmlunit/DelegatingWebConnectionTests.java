/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.test.web.servlet.htmlunit;


import TestGroup.PERFORMANCE;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.tests.Assume;


/**
 * Unit and integration tests for {@link DelegatingWebConnection}.
 *
 * @author Rob Winch
 * @since 4.2
 */
@RunWith(MockitoJUnitRunner.class)
public class DelegatingWebConnectionTests {
    private DelegatingWebConnection webConnection;

    private WebRequest request;

    private WebResponse expectedResponse;

    @Mock
    private WebRequestMatcher matcher1;

    @Mock
    private WebRequestMatcher matcher2;

    @Mock
    private WebConnection defaultConnection;

    @Mock
    private WebConnection connection1;

    @Mock
    private WebConnection connection2;

    @Test
    public void getResponseDefault() throws Exception {
        Mockito.when(defaultConnection.getResponse(request)).thenReturn(expectedResponse);
        WebResponse response = webConnection.getResponse(request);
        Assert.assertThat(response, CoreMatchers.sameInstance(expectedResponse));
        Mockito.verify(matcher1).matches(request);
        Mockito.verify(matcher2).matches(request);
        Mockito.verifyNoMoreInteractions(connection1, connection2);
        Mockito.verify(defaultConnection).getResponse(request);
    }

    @Test
    public void getResponseAllMatches() throws Exception {
        Mockito.when(matcher1.matches(request)).thenReturn(true);
        Mockito.when(connection1.getResponse(request)).thenReturn(expectedResponse);
        WebResponse response = webConnection.getResponse(request);
        Assert.assertThat(response, CoreMatchers.sameInstance(expectedResponse));
        Mockito.verify(matcher1).matches(request);
        Mockito.verifyNoMoreInteractions(matcher2, connection2, defaultConnection);
        Mockito.verify(connection1).getResponse(request);
    }

    @Test
    public void getResponseSecondMatches() throws Exception {
        Mockito.when(matcher2.matches(request)).thenReturn(true);
        Mockito.when(connection2.getResponse(request)).thenReturn(expectedResponse);
        WebResponse response = webConnection.getResponse(request);
        Assert.assertThat(response, CoreMatchers.sameInstance(expectedResponse));
        Mockito.verify(matcher1).matches(request);
        Mockito.verify(matcher2).matches(request);
        Mockito.verifyNoMoreInteractions(connection1, defaultConnection);
        Mockito.verify(connection2).getResponse(request);
    }

    @Test
    public void verifyExampleInClassLevelJavadoc() throws Exception {
        Assume.group(PERFORMANCE);
        WebClient webClient = new WebClient();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup().build();
        MockMvcWebConnection mockConnection = new MockMvcWebConnection(mockMvc, webClient);
        WebRequestMatcher cdnMatcher = new UrlRegexRequestMatcher(".*?//code.jquery.com/.*");
        WebConnection httpConnection = new com.gargoylesoftware.htmlunit.HttpWebConnection(webClient);
        webClient.setWebConnection(new DelegatingWebConnection(mockConnection, new org.springframework.test.web.servlet.htmlunit.DelegatingWebConnection.DelegateWebConnection(cdnMatcher, httpConnection)));
        Page page = webClient.getPage("http://code.jquery.com/jquery-1.11.0.min.js");
        Assert.assertThat(page.getWebResponse().getStatusCode(), CoreMatchers.equalTo(200));
        Assert.assertThat(page.getWebResponse().getContentAsString(), IsNot.not(isEmptyString()));
    }

    @Controller
    static class TestController {}
}

