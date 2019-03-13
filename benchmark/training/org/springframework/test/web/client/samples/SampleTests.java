/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.test.web.client.samples;


import HttpMethod.GET;
import MediaType.APPLICATION_JSON;
import MediaType.TEXT_PLAIN;
import java.io.IOException;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.web.Person;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;


/**
 * Examples to demonstrate writing client-side REST tests with Spring MVC Test.
 * While the tests in this class invoke the RestTemplate directly, in actual
 * tests the RestTemplate may likely be invoked indirectly, i.e. through client
 * code.
 *
 * @author Rossen Stoyanchev
 */
public class SampleTests {
    private MockRestServiceServer mockServer;

    private RestTemplate restTemplate;

    @Test
    public void performGet() {
        String responseBody = "{\"name\" : \"Ludwig van Beethoven\", \"someDouble\" : \"1.6035\"}";
        this.mockServer.expect(MockRestRequestMatchers.requestTo("/composers/42")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess(responseBody, APPLICATION_JSON));
        @SuppressWarnings("unused")
        Person ludwig = this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
        // We are only validating the request. The response is mocked out.
        // hotel.getId() == 42
        // hotel.getName().equals("Holiday Inn")
        this.mockServer.verify();
    }

    @Test
    public void performGetManyTimes() {
        String responseBody = "{\"name\" : \"Ludwig van Beethoven\", \"someDouble\" : \"1.6035\"}";
        this.mockServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo("/composers/42")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess(responseBody, APPLICATION_JSON));
        @SuppressWarnings("unused")
        Person ludwig = this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
        // We are only validating the request. The response is mocked out.
        // hotel.getId() == 42
        // hotel.getName().equals("Holiday Inn")
        this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
        this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
        this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
        this.mockServer.verify();
    }

    @Test
    public void expectNever() {
        String responseBody = "{\"name\" : \"Ludwig van Beethoven\", \"someDouble\" : \"1.6035\"}";
        this.mockServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo("/composers/42")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess(responseBody, APPLICATION_JSON));
        this.mockServer.expect(ExpectedCount.never(), MockRestRequestMatchers.requestTo("/composers/43")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess(responseBody, APPLICATION_JSON));
        this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
        this.mockServer.verify();
    }

    @Test(expected = AssertionError.class)
    public void expectNeverViolated() {
        String responseBody = "{\"name\" : \"Ludwig van Beethoven\", \"someDouble\" : \"1.6035\"}";
        this.mockServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo("/composers/42")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess(responseBody, APPLICATION_JSON));
        this.mockServer.expect(ExpectedCount.never(), MockRestRequestMatchers.requestTo("/composers/43")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess(responseBody, APPLICATION_JSON));
        this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
        this.restTemplate.getForObject("/composers/{id}", Person.class, 43);
    }

    @Test
    public void performGetWithResponseBodyFromFile() {
        Resource responseBody = new ClassPathResource("ludwig.json", this.getClass());
        this.mockServer.expect(MockRestRequestMatchers.requestTo("/composers/42")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess(responseBody, APPLICATION_JSON));
        @SuppressWarnings("unused")
        Person ludwig = this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
        // hotel.getId() == 42
        // hotel.getName().equals("Holiday Inn")
        this.mockServer.verify();
    }

    @Test
    public void verify() {
        this.mockServer.expect(MockRestRequestMatchers.requestTo("/number")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess("1", TEXT_PLAIN));
        this.mockServer.expect(MockRestRequestMatchers.requestTo("/number")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess("2", TEXT_PLAIN));
        this.mockServer.expect(MockRestRequestMatchers.requestTo("/number")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess("4", TEXT_PLAIN));
        this.mockServer.expect(MockRestRequestMatchers.requestTo("/number")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess("8", TEXT_PLAIN));
        @SuppressWarnings("unused")
        String result1 = this.restTemplate.getForObject("/number", String.class);
        // result1 == "1"
        @SuppressWarnings("unused")
        String result2 = this.restTemplate.getForObject("/number", String.class);
        // result == "2"
        try {
            this.mockServer.verify();
        } catch (AssertionError error) {
            Assert.assertTrue(error.getMessage(), error.getMessage().contains("2 unsatisfied expectation(s)"));
        }
    }

    // SPR-14694
    @Test
    public void repeatedAccessToResponseViaResource() {
        Resource resource = new ClassPathResource("ludwig.json", this.getClass());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new SampleTests.ContentInterceptor(resource)));
        MockRestServiceServer mockServer = // enable repeated reads of response body
        MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).bufferContent().build();
        mockServer.expect(MockRestRequestMatchers.requestTo("/composers/42")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess(resource, APPLICATION_JSON));
        restTemplate.getForObject("/composers/{id}", Person.class, 42);
        mockServer.verify();
    }

    private static class ContentInterceptor implements ClientHttpRequestInterceptor {
        private final Resource resource;

        private ContentInterceptor(Resource resource) {
            this.resource = resource;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            ClientHttpResponse response = execution.execute(request, body);
            byte[] expected = FileCopyUtils.copyToByteArray(this.resource.getInputStream());
            byte[] actual = FileCopyUtils.copyToByteArray(response.getBody());
            Assert.assertEquals(new String(expected), new String(actual));
            return response;
        }
    }
}

