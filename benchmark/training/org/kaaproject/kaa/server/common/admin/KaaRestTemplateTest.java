/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.server.common.admin;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;


public class KaaRestTemplateTest {
    private MockRestServiceServer mockServer;

    private KaaRestTemplate kaaRestTemplate;

    private TestMessage testMessage;

    @Test
    public void testGetMessage() {
        mockServer.expect(requestTo("http://localhost:8080/kaaTest")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess("resultSuccess", MediaType.TEXT_PLAIN));
        String result = testMessage.getMessage(kaaRestTemplate, TestHttpMethods.GET);
        mockServer.verify();
        Assert.assertThat(result, CoreMatchers.allOf(CoreMatchers.containsString("SUCCESS"), CoreMatchers.containsString("resultSuccess")));
    }

    @Test
    public void testGetMessage_500() {
        mockServer.expect(requestTo("http://localhost:8080/kaaTest")).andExpect(method(HttpMethod.GET)).andRespond(withServerError());
        String result = testMessage.getMessage(kaaRestTemplate, TestHttpMethods.GET);
        mockServer.verify();
        Assert.assertThat(result, CoreMatchers.allOf(CoreMatchers.containsString("FAILED"), CoreMatchers.containsString("500")));
    }

    @Test
    public void testGetMessage_404() {
        mockServer.expect(requestTo("http://localhost:8080/kaaTest")).andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.NOT_FOUND));
        String result = testMessage.getMessage(kaaRestTemplate, TestHttpMethods.GET);
        mockServer.verify();
        Assert.assertThat(result, CoreMatchers.allOf(CoreMatchers.containsString("FAILED"), CoreMatchers.containsString("404")));
    }

    @Test
    public void postBadRequestTest_400() throws Exception {
        ResponseCreator response = withBadRequest();
        this.mockServer.expect(requestTo("http://localhost:8080/kaaTest/post")).andExpect(method(HttpMethod.POST)).andRespond(response);
        String result = testMessage.getMessage(kaaRestTemplate, TestHttpMethods.POST);
        this.mockServer.verify();
        Assert.assertThat(result, CoreMatchers.allOf(CoreMatchers.containsString("FAILED")));
    }

    @Test
    public void putBadRequestTest_400() throws Exception {
        ResponseCreator response = withBadRequest();
        this.mockServer.expect(requestTo("http://localhost:8080/kaaTest/put")).andExpect(method(HttpMethod.PUT)).andRespond(response);
        String result = testMessage.getMessage(kaaRestTemplate, TestHttpMethods.PUT);
        this.mockServer.verify();
        Assert.assertThat(result, CoreMatchers.allOf(CoreMatchers.containsString("FAILED")));
    }

    @Test
    public void putSuccessRequestTest() throws Exception {
        ResponseCreator response = withSuccess();
        this.mockServer.expect(requestTo("http://localhost:8080/kaaTest/put")).andExpect(method(HttpMethod.PUT)).andRespond(response);
        String result = testMessage.getMessage(kaaRestTemplate, TestHttpMethods.PUT);
        this.mockServer.verify();
        Assert.assertThat(result, CoreMatchers.allOf(CoreMatchers.containsString("SUCCESS")));
    }

    @Test
    public void deleteSuccessRequestTest() throws Exception {
        ResponseCreator response = withSuccess();
        this.mockServer.expect(requestTo("http://localhost:8080/kaaTest/delete")).andExpect(method(HttpMethod.DELETE)).andRespond(response);
        String result = testMessage.getMessage(kaaRestTemplate, TestHttpMethods.DELETE);
        this.mockServer.verify();
        Assert.assertThat(result, CoreMatchers.allOf(CoreMatchers.containsString("SUCCESS")));
    }

    @Test
    public void deleteBadRequestTest_400() throws Exception {
        ResponseCreator response = withBadRequest();
        this.mockServer.expect(requestTo("http://localhost:8080/kaaTest/delete")).andExpect(method(HttpMethod.DELETE)).andRespond(response);
        String result = testMessage.getMessage(kaaRestTemplate, TestHttpMethods.DELETE);
        this.mockServer.verify();
        Assert.assertThat(result, CoreMatchers.allOf(CoreMatchers.containsString("FAILED"), CoreMatchers.containsString("400")));
    }
}

