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
package org.springframework.web.servlet.config.annotation;


import ContentNegotiationStrategy.MEDIA_TYPE_ALL_LIST;
import MediaType.ALL;
import MediaType.APPLICATION_JSON;
import MediaType.IMAGE_GIF;
import MediaType.IMAGE_GIF_VALUE;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.NativeWebRequest;


/**
 * Test fixture for {@link ContentNegotiationConfigurer} tests.
 *
 * @author Rossen Stoyanchev
 */
public class ContentNegotiationConfigurerTests {
    private ContentNegotiationConfigurer configurer;

    private NativeWebRequest webRequest;

    private MockHttpServletRequest servletRequest;

    @Test
    public void defaultSettings() throws Exception {
        ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();
        this.servletRequest.setRequestURI("/flower.gif");
        Assert.assertEquals("Should be able to resolve file extensions by default", IMAGE_GIF, manager.resolveMediaTypes(this.webRequest).get(0));
        this.servletRequest.setRequestURI("/flower?format=gif");
        this.servletRequest.addParameter("format", "gif");
        Assert.assertEquals("Should not resolve request parameters by default", MEDIA_TYPE_ALL_LIST, manager.resolveMediaTypes(this.webRequest));
        this.servletRequest.setRequestURI("/flower");
        this.servletRequest.addHeader("Accept", IMAGE_GIF_VALUE);
        Assert.assertEquals("Should resolve Accept header by default", IMAGE_GIF, manager.resolveMediaTypes(this.webRequest).get(0));
    }

    @Test
    public void addMediaTypes() throws Exception {
        this.configurer.mediaTypes(Collections.singletonMap("json", APPLICATION_JSON));
        ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();
        this.servletRequest.setRequestURI("/flower.json");
        Assert.assertEquals(APPLICATION_JSON, manager.resolveMediaTypes(this.webRequest).get(0));
    }

    @Test
    public void favorParameter() throws Exception {
        this.configurer.favorParameter(true);
        this.configurer.parameterName("f");
        this.configurer.mediaTypes(Collections.singletonMap("json", APPLICATION_JSON));
        ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();
        this.servletRequest.setRequestURI("/flower");
        this.servletRequest.addParameter("f", "json");
        Assert.assertEquals(APPLICATION_JSON, manager.resolveMediaTypes(this.webRequest).get(0));
    }

    @Test
    public void ignoreAcceptHeader() throws Exception {
        this.configurer.ignoreAcceptHeader(true);
        ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();
        this.servletRequest.setRequestURI("/flower");
        this.servletRequest.addHeader("Accept", IMAGE_GIF_VALUE);
        Assert.assertEquals(MEDIA_TYPE_ALL_LIST, manager.resolveMediaTypes(this.webRequest));
    }

    @Test
    public void setDefaultContentType() throws Exception {
        this.configurer.defaultContentType(APPLICATION_JSON);
        ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();
        Assert.assertEquals(APPLICATION_JSON, manager.resolveMediaTypes(this.webRequest).get(0));
    }

    @Test
    public void setMultipleDefaultContentTypes() throws Exception {
        this.configurer.defaultContentType(APPLICATION_JSON, ALL);
        ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();
        Assert.assertEquals(Arrays.asList(APPLICATION_JSON, ALL), manager.resolveMediaTypes(this.webRequest));
    }

    @Test
    public void setDefaultContentTypeStrategy() throws Exception {
        this.configurer.defaultContentTypeStrategy(new org.springframework.web.accept.FixedContentNegotiationStrategy(MediaType.APPLICATION_JSON));
        ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();
        Assert.assertEquals(APPLICATION_JSON, manager.resolveMediaTypes(this.webRequest).get(0));
    }
}

