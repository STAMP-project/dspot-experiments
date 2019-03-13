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
package org.springframework.boot.actuate.autoconfigure.endpoint.web.documentation;


import HttpHeaders.ACCEPT;
import HttpHeaders.CONTENT_TYPE;
import JsonFieldType.STRING;
import java.net.URI;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.trace.http.HttpExchangeTracer;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceEndpoint;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.Include;
import org.springframework.boot.actuate.trace.http.TraceableRequest;
import org.springframework.boot.actuate.trace.http.TraceableResponse;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Tests for generating documentation describing {@link HttpTraceEndpoint}.
 *
 * @author Andy Wilkinson
 */
public class HttpTraceEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    @MockBean
    private HttpTraceRepository repository;

    @Test
    public void traces() throws Exception {
        TraceableRequest request = Mockito.mock(TraceableRequest.class);
        BDDMockito.given(request.getUri()).willReturn(URI.create("https://api.example.com"));
        BDDMockito.given(request.getMethod()).willReturn("GET");
        BDDMockito.given(request.getHeaders()).willReturn(Collections.singletonMap(ACCEPT, Arrays.asList("application/json")));
        TraceableResponse response = Mockito.mock(TraceableResponse.class);
        BDDMockito.given(response.getStatus()).willReturn(200);
        BDDMockito.given(response.getHeaders()).willReturn(Collections.singletonMap(CONTENT_TYPE, Arrays.asList("application/json")));
        Principal principal = Mockito.mock(Principal.class);
        BDDMockito.given(principal.getName()).willReturn("alice");
        HttpExchangeTracer tracer = new HttpExchangeTracer(EnumSet.allOf(Include.class));
        HttpTrace trace = tracer.receivedRequest(request);
        tracer.sendingResponse(trace, response, () -> principal, () -> UUID.randomUUID().toString());
        BDDMockito.given(this.repository.findAll()).willReturn(Arrays.asList(trace));
        this.mockMvc.perform(get("/actuator/httptrace")).andExpect(status().isOk()).andDo(document("httptrace", responseFields(fieldWithPath("traces").description("An array of traced HTTP request-response exchanges."), fieldWithPath("traces.[].timestamp").description("Timestamp of when the traced exchange occurred."), fieldWithPath("traces.[].principal").description("Principal of the exchange, if any.").optional(), fieldWithPath("traces.[].principal.name").description("Name of the principal.").optional(), fieldWithPath("traces.[].request.method").description("HTTP method of the request."), fieldWithPath("traces.[].request.remoteAddress").description("Remote address from which the request was received, if known.").optional().type(STRING), fieldWithPath("traces.[].request.uri").description("URI of the request."), fieldWithPath("traces.[].request.headers").description("Headers of the request, keyed by header name."), fieldWithPath("traces.[].request.headers.*.[]").description("Values of the header"), fieldWithPath("traces.[].response.status").description("Status of the response"), fieldWithPath("traces.[].response.headers").description("Headers of the response, keyed by header name."), fieldWithPath("traces.[].response.headers.*.[]").description("Values of the header"), fieldWithPath("traces.[].session").description("Session associated with the exchange, if any.").optional(), fieldWithPath("traces.[].session.id").description("ID of the session."), fieldWithPath("traces.[].timeTaken").description("Time, in milliseconds, taken to handle the exchange."))));
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public HttpTraceEndpoint httpTraceEndpoint(HttpTraceRepository repository) {
            return new HttpTraceEndpoint(repository);
        }
    }
}

