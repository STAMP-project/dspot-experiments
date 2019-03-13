/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.test.web.client.response;


import HttpStatus.BAD_REQUEST;
import HttpStatus.CREATED;
import HttpStatus.FORBIDDEN;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import MediaType.TEXT_PLAIN;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.util.StreamUtils;


/**
 * Tests for the {@link MockRestResponseCreators} static factory methods.
 *
 * @author Rossen Stoyanchev
 */
public class ResponseCreatorsTests {
    @Test
    public void success() throws Exception {
        MockClientHttpResponse response = ((MockClientHttpResponse) (MockRestResponseCreators.withSuccess().createResponse(null)));
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(response.getHeaders().isEmpty());
        Assert.assertEquals(0, StreamUtils.copyToByteArray(response.getBody()).length);
    }

    @Test
    public void successWithContent() throws Exception {
        DefaultResponseCreator responseCreator = MockRestResponseCreators.withSuccess("foo", TEXT_PLAIN);
        MockClientHttpResponse response = ((MockClientHttpResponse) (responseCreator.createResponse(null)));
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(TEXT_PLAIN, response.getHeaders().getContentType());
        Assert.assertArrayEquals("foo".getBytes(), StreamUtils.copyToByteArray(response.getBody()));
    }

    @Test
    public void successWithContentWithoutContentType() throws Exception {
        DefaultResponseCreator responseCreator = MockRestResponseCreators.withSuccess("foo", null);
        MockClientHttpResponse response = ((MockClientHttpResponse) (responseCreator.createResponse(null)));
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertNull(response.getHeaders().getContentType());
        Assert.assertArrayEquals("foo".getBytes(), StreamUtils.copyToByteArray(response.getBody()));
    }

    @Test
    public void created() throws Exception {
        URI location = new URI("/foo");
        DefaultResponseCreator responseCreator = MockRestResponseCreators.withCreatedEntity(location);
        MockClientHttpResponse response = ((MockClientHttpResponse) (responseCreator.createResponse(null)));
        Assert.assertEquals(CREATED, response.getStatusCode());
        Assert.assertEquals(location, response.getHeaders().getLocation());
        Assert.assertEquals(0, StreamUtils.copyToByteArray(response.getBody()).length);
    }

    @Test
    public void noContent() throws Exception {
        DefaultResponseCreator responseCreator = MockRestResponseCreators.withNoContent();
        MockClientHttpResponse response = ((MockClientHttpResponse) (responseCreator.createResponse(null)));
        Assert.assertEquals(NO_CONTENT, response.getStatusCode());
        Assert.assertTrue(response.getHeaders().isEmpty());
        Assert.assertEquals(0, StreamUtils.copyToByteArray(response.getBody()).length);
    }

    @Test
    public void badRequest() throws Exception {
        DefaultResponseCreator responseCreator = MockRestResponseCreators.withBadRequest();
        MockClientHttpResponse response = ((MockClientHttpResponse) (responseCreator.createResponse(null)));
        Assert.assertEquals(BAD_REQUEST, response.getStatusCode());
        Assert.assertTrue(response.getHeaders().isEmpty());
        Assert.assertEquals(0, StreamUtils.copyToByteArray(response.getBody()).length);
    }

    @Test
    public void unauthorized() throws Exception {
        DefaultResponseCreator responseCreator = MockRestResponseCreators.withUnauthorizedRequest();
        MockClientHttpResponse response = ((MockClientHttpResponse) (responseCreator.createResponse(null)));
        Assert.assertEquals(UNAUTHORIZED, response.getStatusCode());
        Assert.assertTrue(response.getHeaders().isEmpty());
        Assert.assertEquals(0, StreamUtils.copyToByteArray(response.getBody()).length);
    }

    @Test
    public void serverError() throws Exception {
        DefaultResponseCreator responseCreator = MockRestResponseCreators.withServerError();
        MockClientHttpResponse response = ((MockClientHttpResponse) (responseCreator.createResponse(null)));
        Assert.assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertTrue(response.getHeaders().isEmpty());
        Assert.assertEquals(0, StreamUtils.copyToByteArray(response.getBody()).length);
    }

    @Test
    public void withStatus() throws Exception {
        DefaultResponseCreator responseCreator = MockRestResponseCreators.withStatus(FORBIDDEN);
        MockClientHttpResponse response = ((MockClientHttpResponse) (responseCreator.createResponse(null)));
        Assert.assertEquals(FORBIDDEN, response.getStatusCode());
        Assert.assertTrue(response.getHeaders().isEmpty());
        Assert.assertEquals(0, StreamUtils.copyToByteArray(response.getBody()).length);
    }
}

