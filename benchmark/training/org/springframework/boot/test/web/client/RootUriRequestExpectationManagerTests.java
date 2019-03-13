/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.test.web.client;


import java.net.URI;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestExpectationManager;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestTemplate;


/**
 * Tests for {@link RootUriRequestExpectationManager}.
 *
 * @author Phillip Webb
 */
public class RootUriRequestExpectationManagerTests {
    private String uri = "http://example.com";

    @Mock
    private RequestExpectationManager delegate;

    private RootUriRequestExpectationManager manager;

    @Captor
    private ArgumentCaptor<ClientHttpRequest> requestCaptor;

    @Test
    public void createWhenRootUriIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new RootUriRequestExpectationManager(null, this.delegate)).withMessageContaining("RootUri must not be null");
    }

    @Test
    public void createWhenExpectationManagerIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new RootUriRequestExpectationManager(this.uri, null)).withMessageContaining("ExpectationManager must not be null");
    }

    @Test
    public void expectRequestShouldDelegateToExpectationManager() {
        ExpectedCount count = ExpectedCount.once();
        RequestMatcher requestMatcher = Mockito.mock(RequestMatcher.class);
        this.manager.expectRequest(count, requestMatcher);
        Mockito.verify(this.delegate).expectRequest(count, requestMatcher);
    }

    @Test
    public void validateRequestWhenUriDoesNotStartWithRootUriShouldDelegateToExpectationManager() throws Exception {
        ClientHttpRequest request = Mockito.mock(ClientHttpRequest.class);
        BDDMockito.given(request.getURI()).willReturn(new URI("http://spring.io/test"));
        this.manager.validateRequest(request);
        Mockito.verify(this.delegate).validateRequest(request);
    }

    @Test
    public void validateRequestWhenUriStartsWithRootUriShouldReplaceUri() throws Exception {
        ClientHttpRequest request = Mockito.mock(ClientHttpRequest.class);
        BDDMockito.given(request.getURI()).willReturn(new URI(((this.uri) + "/hello")));
        this.manager.validateRequest(request);
        Mockito.verify(this.delegate).validateRequest(this.requestCaptor.capture());
        HttpRequestWrapper actual = ((HttpRequestWrapper) (this.requestCaptor.getValue()));
        assertThat(actual.getRequest()).isSameAs(request);
        assertThat(actual.getURI()).isEqualTo(new URI("/hello"));
    }

    @Test
    public void validateRequestWhenRequestUriAssertionIsThrownShouldReplaceUriInMessage() throws Exception {
        ClientHttpRequest request = Mockito.mock(ClientHttpRequest.class);
        BDDMockito.given(request.getURI()).willReturn(new URI(((this.uri) + "/hello")));
        BDDMockito.given(this.delegate.validateRequest(ArgumentMatchers.any(ClientHttpRequest.class))).willThrow(new AssertionError("Request URI expected:</hello> was:<http://example.com/bad>"));
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> this.manager.validateRequest(request)).withMessageContaining("Request URI expected:<http://example.com/hello>");
    }

    @Test
    public void resetRequestShouldDelegateToExpectationManager() {
        this.manager.reset();
        Mockito.verify(this.delegate).reset();
    }

    @Test
    public void bindToShouldReturnMockRestServiceServer() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        MockRestServiceServer bound = RootUriRequestExpectationManager.bindTo(restTemplate);
        assertThat(bound).isNotNull();
    }

    @Test
    public void bindToWithExpectationManagerShouldReturnMockRestServiceServer() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        MockRestServiceServer bound = RootUriRequestExpectationManager.bindTo(restTemplate, this.delegate);
        assertThat(bound).isNotNull();
    }

    @Test
    public void forRestTemplateWhenUsingRootUriTemplateHandlerShouldReturnRootUriRequestExpectationManager() {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(this.uri).build();
        RequestExpectationManager actual = RootUriRequestExpectationManager.forRestTemplate(restTemplate, this.delegate);
        assertThat(actual).isInstanceOf(RootUriRequestExpectationManager.class);
        assertThat(actual).extracting("rootUri").containsExactly(this.uri);
    }

    @Test
    public void forRestTemplateWhenNotUsingRootUriTemplateHandlerShouldReturnOriginalRequestExpectationManager() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        RequestExpectationManager actual = RootUriRequestExpectationManager.forRestTemplate(restTemplate, this.delegate);
        assertThat(actual).isSameAs(this.delegate);
    }

    @Test
    public void boundRestTemplateShouldPrefixRootUri() {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri("http://example.com").build();
        MockRestServiceServer server = RootUriRequestExpectationManager.bindTo(restTemplate);
        server.expect(requestTo("/hello")).andRespond(withSuccess());
        restTemplate.getForEntity("/hello", String.class);
    }

    @Test
    public void boundRestTemplateWhenUrlIncludesDomainShouldNotPrefixRootUri() {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri("http://example.com").build();
        MockRestServiceServer server = RootUriRequestExpectationManager.bindTo(restTemplate);
        server.expect(requestTo("/hello")).andRespond(withSuccess());
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> restTemplate.getForEntity("http://spring.io/hello", .class)).withMessageContaining("expected:<http://example.com/hello> but was:<http://spring.io/hello>");
    }
}

