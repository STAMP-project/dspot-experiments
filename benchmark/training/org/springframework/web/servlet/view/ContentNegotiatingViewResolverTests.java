/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.web.servlet.view;


import HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XHTML_XML;
import View.SELECTED_CONTENT_TYPE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.FixedContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.MappingMediaTypeFileExtensionResolver;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class ContentNegotiatingViewResolverTests {
    private ContentNegotiatingViewResolver viewResolver;

    private MockHttpServletRequest request;

    @Test
    public void getMediaTypeAcceptHeaderWithProduces() throws Exception {
        Set<MediaType> producibleTypes = Collections.singleton(APPLICATION_XHTML_XML);
        request.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, producibleTypes);
        request.addHeader("Accept", "text/html,application/xml;q=0.9,application/xhtml+xml,*/*;q=0.8");
        viewResolver.afterPropertiesSet();
        List<MediaType> result = viewResolver.getMediaTypes(request);
        Assert.assertEquals("Invalid content type", new MediaType("application", "xhtml+xml"), result.get(0));
    }

    @Test
    public void resolveViewNameWithPathExtension() throws Exception {
        request.setRequestURI("/test.xls");
        ViewResolver viewResolverMock = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Collections.singletonList(viewResolverMock));
        viewResolver.afterPropertiesSet();
        View viewMock = Mockito.mock(View.class, "application_xls");
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(viewResolverMock.resolveViewName(viewName, locale)).willReturn(null);
        BDDMockito.given(viewResolverMock.resolveViewName((viewName + ".xls"), locale)).willReturn(viewMock);
        BDDMockito.given(viewMock.getContentType()).willReturn("application/vnd.ms-excel");
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertSame("Invalid view", viewMock, result);
    }

    @Test
    public void resolveViewNameWithAcceptHeader() throws Exception {
        request.addHeader("Accept", "application/vnd.ms-excel");
        Map<String, MediaType> mapping = Collections.singletonMap("xls", MediaType.valueOf("application/vnd.ms-excel"));
        MappingMediaTypeFileExtensionResolver extensionsResolver = new MappingMediaTypeFileExtensionResolver(mapping);
        ContentNegotiationManager manager = new ContentNegotiationManager(new HeaderContentNegotiationStrategy());
        manager.addFileExtensionResolvers(extensionsResolver);
        viewResolver.setContentNegotiationManager(manager);
        ViewResolver viewResolverMock = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Collections.singletonList(viewResolverMock));
        View viewMock = Mockito.mock(View.class, "application_xls");
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(viewResolverMock.resolveViewName(viewName, locale)).willReturn(null);
        BDDMockito.given(viewResolverMock.resolveViewName((viewName + ".xls"), locale)).willReturn(viewMock);
        BDDMockito.given(viewMock.getContentType()).willReturn("application/vnd.ms-excel");
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertSame("Invalid view", viewMock, result);
    }

    @Test
    public void resolveViewNameWithInvalidAcceptHeader() throws Exception {
        request.addHeader("Accept", "application");
        ViewResolver viewResolverMock = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Collections.singletonList(viewResolverMock));
        viewResolver.afterPropertiesSet();
        View result = viewResolver.resolveViewName("test", Locale.ENGLISH);
        Assert.assertNull(result);
    }

    @Test
    public void resolveViewNameWithRequestParameter() throws Exception {
        request.addParameter("format", "xls");
        Map<String, MediaType> mapping = Collections.singletonMap("xls", MediaType.valueOf("application/vnd.ms-excel"));
        ParameterContentNegotiationStrategy paramStrategy = new ParameterContentNegotiationStrategy(mapping);
        viewResolver.setContentNegotiationManager(new ContentNegotiationManager(paramStrategy));
        ViewResolver viewResolverMock = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Collections.singletonList(viewResolverMock));
        viewResolver.afterPropertiesSet();
        View viewMock = Mockito.mock(View.class, "application_xls");
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(viewResolverMock.resolveViewName(viewName, locale)).willReturn(null);
        BDDMockito.given(viewResolverMock.resolveViewName((viewName + ".xls"), locale)).willReturn(viewMock);
        BDDMockito.given(viewMock.getContentType()).willReturn("application/vnd.ms-excel");
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertSame("Invalid view", viewMock, result);
    }

    @Test
    public void resolveViewNameWithDefaultContentType() throws Exception {
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        MediaType mediaType = new MediaType("application", "xml");
        FixedContentNegotiationStrategy fixedStrategy = new FixedContentNegotiationStrategy(mediaType);
        viewResolver.setContentNegotiationManager(new ContentNegotiationManager(fixedStrategy));
        ViewResolver viewResolverMock1 = Mockito.mock(ViewResolver.class, "viewResolver1");
        ViewResolver viewResolverMock2 = Mockito.mock(ViewResolver.class, "viewResolver2");
        viewResolver.setViewResolvers(Arrays.asList(viewResolverMock1, viewResolverMock2));
        viewResolver.afterPropertiesSet();
        View viewMock1 = Mockito.mock(View.class, "application_xml");
        View viewMock2 = Mockito.mock(View.class, "text_html");
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(viewResolverMock1.resolveViewName(viewName, locale)).willReturn(viewMock1);
        BDDMockito.given(viewResolverMock2.resolveViewName(viewName, locale)).willReturn(viewMock2);
        BDDMockito.given(viewMock1.getContentType()).willReturn("application/xml");
        BDDMockito.given(viewMock2.getContentType()).willReturn("text/html;charset=ISO-8859-1");
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertSame("Invalid view", viewMock1, result);
    }

    @Test
    public void resolveViewNameAcceptHeader() throws Exception {
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        ViewResolver viewResolverMock1 = Mockito.mock(ViewResolver.class);
        ViewResolver viewResolverMock2 = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Arrays.asList(viewResolverMock1, viewResolverMock2));
        viewResolver.afterPropertiesSet();
        View viewMock1 = Mockito.mock(View.class, "application_xml");
        View viewMock2 = Mockito.mock(View.class, "text_html");
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(viewResolverMock1.resolveViewName(viewName, locale)).willReturn(viewMock1);
        BDDMockito.given(viewResolverMock2.resolveViewName(viewName, locale)).willReturn(viewMock2);
        BDDMockito.given(viewMock1.getContentType()).willReturn("application/xml");
        BDDMockito.given(viewMock2.getContentType()).willReturn("text/html;charset=ISO-8859-1");
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertSame("Invalid view", viewMock2, result);
    }

    // SPR-9160
    @Test
    public void resolveViewNameAcceptHeaderSortByQuality() throws Exception {
        request.addHeader("Accept", "text/plain;q=0.5, application/json");
        viewResolver.setContentNegotiationManager(new ContentNegotiationManager(new HeaderContentNegotiationStrategy()));
        ViewResolver htmlViewResolver = Mockito.mock(ViewResolver.class);
        ViewResolver jsonViewResolver = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Arrays.asList(htmlViewResolver, jsonViewResolver));
        View htmlView = Mockito.mock(View.class, "text_html");
        View jsonViewMock = Mockito.mock(View.class, "application_json");
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(htmlViewResolver.resolveViewName(viewName, locale)).willReturn(htmlView);
        BDDMockito.given(jsonViewResolver.resolveViewName(viewName, locale)).willReturn(jsonViewMock);
        BDDMockito.given(htmlView.getContentType()).willReturn("text/html");
        BDDMockito.given(jsonViewMock.getContentType()).willReturn("application/json");
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertSame("Invalid view", jsonViewMock, result);
    }

    // SPR-9807
    @Test
    public void resolveViewNameAcceptHeaderWithSuffix() throws Exception {
        request.addHeader("Accept", "application/vnd.example-v2+xml");
        ViewResolver viewResolverMock = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Arrays.asList(viewResolverMock));
        viewResolver.afterPropertiesSet();
        View viewMock = Mockito.mock(View.class, "application_xml");
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(viewResolverMock.resolveViewName(viewName, locale)).willReturn(viewMock);
        BDDMockito.given(viewMock.getContentType()).willReturn("application/*+xml");
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertSame("Invalid view", viewMock, result);
        Assert.assertEquals(new MediaType("application", "vnd.example-v2+xml"), request.getAttribute(SELECTED_CONTENT_TYPE));
    }

    @Test
    public void resolveViewNameAcceptHeaderDefaultView() throws Exception {
        request.addHeader("Accept", "application/json");
        ViewResolver viewResolverMock1 = Mockito.mock(ViewResolver.class);
        ViewResolver viewResolverMock2 = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Arrays.asList(viewResolverMock1, viewResolverMock2));
        View viewMock1 = Mockito.mock(View.class, "application_xml");
        View viewMock2 = Mockito.mock(View.class, "text_html");
        View viewMock3 = Mockito.mock(View.class, "application_json");
        List<View> defaultViews = new ArrayList<>();
        defaultViews.add(viewMock3);
        viewResolver.setDefaultViews(defaultViews);
        viewResolver.afterPropertiesSet();
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(viewResolverMock1.resolveViewName(viewName, locale)).willReturn(viewMock1);
        BDDMockito.given(viewResolverMock2.resolveViewName(viewName, locale)).willReturn(viewMock2);
        BDDMockito.given(viewMock1.getContentType()).willReturn("application/xml");
        BDDMockito.given(viewMock2.getContentType()).willReturn("text/html;charset=ISO-8859-1");
        BDDMockito.given(viewMock3.getContentType()).willReturn("application/json");
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertSame("Invalid view", viewMock3, result);
    }

    @Test
    public void resolveViewNameFilename() throws Exception {
        request.setRequestURI("/test.html");
        ViewResolver viewResolverMock1 = Mockito.mock(ViewResolver.class, "viewResolver1");
        ViewResolver viewResolverMock2 = Mockito.mock(ViewResolver.class, "viewResolver2");
        viewResolver.setViewResolvers(Arrays.asList(viewResolverMock1, viewResolverMock2));
        viewResolver.afterPropertiesSet();
        View viewMock1 = Mockito.mock(View.class, "application_xml");
        View viewMock2 = Mockito.mock(View.class, "text_html");
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(viewResolverMock1.resolveViewName(viewName, locale)).willReturn(viewMock1);
        BDDMockito.given(viewResolverMock1.resolveViewName((viewName + ".html"), locale)).willReturn(null);
        BDDMockito.given(viewResolverMock2.resolveViewName(viewName, locale)).willReturn(null);
        BDDMockito.given(viewResolverMock2.resolveViewName((viewName + ".html"), locale)).willReturn(viewMock2);
        BDDMockito.given(viewMock1.getContentType()).willReturn("application/xml");
        BDDMockito.given(viewMock2.getContentType()).willReturn("text/html;charset=ISO-8859-1");
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertSame("Invalid view", viewMock2, result);
    }

    @Test
    public void resolveViewNameFilenameDefaultView() throws Exception {
        request.setRequestURI("/test.json");
        Map<String, MediaType> mapping = Collections.singletonMap("json", APPLICATION_JSON);
        PathExtensionContentNegotiationStrategy pathStrategy = new PathExtensionContentNegotiationStrategy(mapping);
        viewResolver.setContentNegotiationManager(new ContentNegotiationManager(pathStrategy));
        ViewResolver viewResolverMock1 = Mockito.mock(ViewResolver.class);
        ViewResolver viewResolverMock2 = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Arrays.asList(viewResolverMock1, viewResolverMock2));
        View viewMock1 = Mockito.mock(View.class, "application_xml");
        View viewMock2 = Mockito.mock(View.class, "text_html");
        View viewMock3 = Mockito.mock(View.class, "application_json");
        List<View> defaultViews = new ArrayList<>();
        defaultViews.add(viewMock3);
        viewResolver.setDefaultViews(defaultViews);
        viewResolver.afterPropertiesSet();
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(viewResolverMock1.resolveViewName(viewName, locale)).willReturn(viewMock1);
        BDDMockito.given(viewResolverMock1.resolveViewName((viewName + ".json"), locale)).willReturn(null);
        BDDMockito.given(viewResolverMock2.resolveViewName(viewName, locale)).willReturn(viewMock2);
        BDDMockito.given(viewResolverMock2.resolveViewName((viewName + ".json"), locale)).willReturn(null);
        BDDMockito.given(viewMock1.getContentType()).willReturn("application/xml");
        BDDMockito.given(viewMock2.getContentType()).willReturn("text/html;charset=ISO-8859-1");
        BDDMockito.given(viewMock3.getContentType()).willReturn("application/json");
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertSame("Invalid view", viewMock3, result);
    }

    @Test
    public void resolveViewContentTypeNull() throws Exception {
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        ViewResolver viewResolverMock = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Collections.singletonList(viewResolverMock));
        viewResolver.afterPropertiesSet();
        View viewMock = Mockito.mock(View.class, "application_xml");
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(viewResolverMock.resolveViewName(viewName, locale)).willReturn(viewMock);
        BDDMockito.given(viewMock.getContentType()).willReturn(null);
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertNull("Invalid view", result);
    }

    @Test
    public void resolveViewNameRedirectView() throws Exception {
        request.addHeader("Accept", "application/json");
        request.setRequestURI("/test");
        StaticWebApplicationContext webAppContext = new StaticWebApplicationContext();
        webAppContext.setServletContext(new MockServletContext());
        webAppContext.refresh();
        UrlBasedViewResolver urlViewResolver = new InternalResourceViewResolver();
        urlViewResolver.setApplicationContext(webAppContext);
        ViewResolver xmlViewResolver = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Arrays.<ViewResolver>asList(xmlViewResolver, urlViewResolver));
        View xmlView = Mockito.mock(View.class, "application_xml");
        View jsonView = Mockito.mock(View.class, "application_json");
        viewResolver.setDefaultViews(Arrays.asList(jsonView));
        viewResolver.afterPropertiesSet();
        String viewName = "redirect:anotherTest";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(xmlViewResolver.resolveViewName(viewName, locale)).willReturn(xmlView);
        BDDMockito.given(jsonView.getContentType()).willReturn("application/json");
        View actualView = viewResolver.resolveViewName(viewName, locale);
        Assert.assertEquals("Invalid view", RedirectView.class, actualView.getClass());
    }

    @Test
    public void resolveViewNoMatch() throws Exception {
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9");
        ViewResolver viewResolverMock = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Collections.singletonList(viewResolverMock));
        viewResolver.afterPropertiesSet();
        View viewMock = Mockito.mock(View.class, "application_xml");
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(viewResolverMock.resolveViewName(viewName, locale)).willReturn(viewMock);
        BDDMockito.given(viewMock.getContentType()).willReturn("application/pdf");
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertNull("Invalid view", result);
    }

    @Test
    public void resolveViewNoMatchUseUnacceptableStatus() throws Exception {
        viewResolver.setUseNotAcceptableStatusCode(true);
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9");
        ViewResolver viewResolverMock = Mockito.mock(ViewResolver.class);
        viewResolver.setViewResolvers(Collections.singletonList(viewResolverMock));
        viewResolver.afterPropertiesSet();
        View viewMock = Mockito.mock(View.class, "application_xml");
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        BDDMockito.given(viewResolverMock.resolveViewName(viewName, locale)).willReturn(viewMock);
        BDDMockito.given(viewMock.getContentType()).willReturn("application/pdf");
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertNotNull("Invalid view", result);
        MockHttpServletResponse response = new MockHttpServletResponse();
        result.render(null, request, response);
        Assert.assertEquals("Invalid status code set", 406, response.getStatus());
    }

    @Test
    public void nestedViewResolverIsNotSpringBean() throws Exception {
        StaticWebApplicationContext webAppContext = new StaticWebApplicationContext();
        webAppContext.setServletContext(new MockServletContext());
        webAppContext.refresh();
        InternalResourceViewResolver nestedResolver = new InternalResourceViewResolver();
        nestedResolver.setApplicationContext(webAppContext);
        nestedResolver.setViewClass(InternalResourceView.class);
        viewResolver.setViewResolvers(new ArrayList(Arrays.asList(nestedResolver)));
        FixedContentNegotiationStrategy fixedStrategy = new FixedContentNegotiationStrategy(MediaType.TEXT_HTML);
        viewResolver.setContentNegotiationManager(new ContentNegotiationManager(fixedStrategy));
        viewResolver.afterPropertiesSet();
        String viewName = "view";
        Locale locale = Locale.ENGLISH;
        View result = viewResolver.resolveViewName(viewName, locale);
        Assert.assertNotNull("Invalid view", result);
    }
}

