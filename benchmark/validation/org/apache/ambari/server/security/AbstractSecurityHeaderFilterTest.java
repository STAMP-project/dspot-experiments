/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.security;


import AbstractSecurityHeaderFilter.CACHE_CONTROL_HEADER;
import AbstractSecurityHeaderFilter.PRAGMA_HEADER;
import AbstractSecurityHeaderFilter.STRICT_TRANSPORT_HEADER;
import AbstractSecurityHeaderFilter.X_CONTENT_TYPE_HEADER;
import AbstractSecurityHeaderFilter.X_FRAME_OPTIONS_HEADER;
import AbstractSecurityHeaderFilter.X_XSS_PROTECTION_HEADER;
import Configuration.API_USE_SSL;
import Configuration.CLIENT_API_SSL_CRT_PASS_FILE_NAME;
import Configuration.CLIENT_API_SSL_KSTR_DIR_NAME;
import Configuration.HTTP_CHARSET;
import Configuration.VIEWS_HTTP_CHARSET;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.Assert;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.state.stack.OsFamily;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public abstract class AbstractSecurityHeaderFilterTest extends EasyMockSupport {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Class<? extends AbstractSecurityHeaderFilter> filterClass;

    private final Map<String, String> propertyNameMap;

    private final Map<String, String> defatulPropertyValueMap;

    protected AbstractSecurityHeaderFilterTest(Class<? extends AbstractSecurityHeaderFilter> filterClass, Map<String, String> propertyNameMap, Map<String, String> defatulPropertyValueMap) {
        this.filterClass = filterClass;
        this.propertyNameMap = propertyNameMap;
        this.defatulPropertyValueMap = defatulPropertyValueMap;
    }

    @Test
    public void testDoFilter_DefaultValuesNoSSL() throws Exception {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Properties properties = new Properties();
                properties.setProperty(API_USE_SSL.getKey(), "false");
                properties.setProperty(HTTP_CHARSET.getKey(), HTTP_CHARSET.getDefaultValue());
                bind(OsFamily.class).toInstance(createNiceMock(OsFamily.class));
                bind(Configuration.class).toInstance(new Configuration(properties));
            }
        });
        FilterConfig filterConfig = createNiceMock(FilterConfig.class);
        HttpServletRequest servletRequest = createStrictMock(HttpServletRequest.class);
        expectHttpServletRequestMock(servletRequest);
        HttpServletResponse servletResponse = createStrictMock(HttpServletResponse.class);
        servletResponse.setHeader(X_FRAME_OPTIONS_HEADER, defatulPropertyValueMap.get(X_FRAME_OPTIONS_HEADER));
        expectLastCall().once();
        servletResponse.setHeader(X_XSS_PROTECTION_HEADER, defatulPropertyValueMap.get(X_XSS_PROTECTION_HEADER));
        expectLastCall().once();
        servletResponse.setHeader(X_CONTENT_TYPE_HEADER, defatulPropertyValueMap.get(X_CONTENT_TYPE_HEADER));
        expectLastCall().once();
        servletResponse.setHeader(CACHE_CONTROL_HEADER, defatulPropertyValueMap.get(CACHE_CONTROL_HEADER));
        expectLastCall().once();
        servletResponse.setHeader(PRAGMA_HEADER, defatulPropertyValueMap.get(PRAGMA_HEADER));
        expectLastCall().once();
        servletResponse.setCharacterEncoding(HTTP_CHARSET.getDefaultValue());
        expectLastCall().once();
        FilterChain filterChain = createStrictMock(FilterChain.class);
        filterChain.doFilter(servletRequest, servletResponse);
        expectLastCall().once();
        replayAll();
        AbstractSecurityHeaderFilter securityFilter = injector.getInstance(filterClass);
        Assert.assertNotNull(securityFilter);
        securityFilter.init(filterConfig);
        securityFilter.doFilter(servletRequest, servletResponse, filterChain);
        verifyAll();
    }

    @Test
    public void testDoFilter_DefaultValuesSSL() throws Exception {
        final File httpPassFile = temporaryFolder.newFile();
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Properties properties = new Properties();
                properties.setProperty(API_USE_SSL.getKey(), "true");
                properties.setProperty(CLIENT_API_SSL_KSTR_DIR_NAME.getKey(), httpPassFile.getParent());
                properties.setProperty(CLIENT_API_SSL_CRT_PASS_FILE_NAME.getKey(), httpPassFile.getName());
                properties.setProperty(HTTP_CHARSET.getKey(), HTTP_CHARSET.getDefaultValue());
                bind(OsFamily.class).toInstance(createNiceMock(OsFamily.class));
                bind(Configuration.class).toInstance(new Configuration(properties));
            }
        });
        FilterConfig filterConfig = createNiceMock(FilterConfig.class);
        HttpServletRequest servletRequest = createStrictMock(HttpServletRequest.class);
        expectHttpServletRequestMock(servletRequest);
        HttpServletResponse servletResponse = createStrictMock(HttpServletResponse.class);
        servletResponse.setHeader(STRICT_TRANSPORT_HEADER, defatulPropertyValueMap.get(STRICT_TRANSPORT_HEADER));
        expectLastCall().once();
        servletResponse.setHeader(X_FRAME_OPTIONS_HEADER, defatulPropertyValueMap.get(X_FRAME_OPTIONS_HEADER));
        expectLastCall().once();
        servletResponse.setHeader(X_XSS_PROTECTION_HEADER, defatulPropertyValueMap.get(X_XSS_PROTECTION_HEADER));
        expectLastCall().once();
        servletResponse.setHeader(X_CONTENT_TYPE_HEADER, defatulPropertyValueMap.get(X_CONTENT_TYPE_HEADER));
        expectLastCall().once();
        servletResponse.setHeader(CACHE_CONTROL_HEADER, defatulPropertyValueMap.get(CACHE_CONTROL_HEADER));
        expectLastCall().once();
        servletResponse.setHeader(PRAGMA_HEADER, defatulPropertyValueMap.get(PRAGMA_HEADER));
        expectLastCall().once();
        servletResponse.setCharacterEncoding(HTTP_CHARSET.getDefaultValue());
        expectLastCall().once();
        FilterChain filterChain = createStrictMock(FilterChain.class);
        filterChain.doFilter(servletRequest, servletResponse);
        expectLastCall().once();
        replayAll();
        AbstractSecurityHeaderFilter securityFilter = injector.getInstance(filterClass);
        Assert.assertNotNull(securityFilter);
        securityFilter.init(filterConfig);
        securityFilter.doFilter(servletRequest, servletResponse, filterChain);
        verifyAll();
    }

    @Test
    public void testDoFilter_CustomValuesNoSSL() throws Exception {
        final File httpPassFile = temporaryFolder.newFile();
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Properties properties = new Properties();
                properties.setProperty(CLIENT_API_SSL_KSTR_DIR_NAME.getKey(), httpPassFile.getParent());
                properties.setProperty(CLIENT_API_SSL_CRT_PASS_FILE_NAME.getKey(), httpPassFile.getName());
                properties.setProperty(propertyNameMap.get(STRICT_TRANSPORT_HEADER), "custom1");
                properties.setProperty(propertyNameMap.get(X_FRAME_OPTIONS_HEADER), "custom2");
                properties.setProperty(propertyNameMap.get(X_XSS_PROTECTION_HEADER), "custom3");
                properties.setProperty(propertyNameMap.get(X_CONTENT_TYPE_HEADER), "custom4");
                properties.setProperty(propertyNameMap.get(CACHE_CONTROL_HEADER), "custom5");
                properties.setProperty(propertyNameMap.get(PRAGMA_HEADER), "custom6");
                properties.setProperty(HTTP_CHARSET.getKey(), "custom7");
                properties.setProperty(VIEWS_HTTP_CHARSET.getKey(), "custom7");
                bind(OsFamily.class).toInstance(createNiceMock(OsFamily.class));
                bind(Configuration.class).toInstance(new Configuration(properties));
            }
        });
        FilterConfig filterConfig = createNiceMock(FilterConfig.class);
        HttpServletRequest servletRequest = createStrictMock(HttpServletRequest.class);
        expectHttpServletRequestMock(servletRequest);
        HttpServletResponse servletResponse = createStrictMock(HttpServletResponse.class);
        servletResponse.setHeader(X_FRAME_OPTIONS_HEADER, "custom2");
        expectLastCall().once();
        servletResponse.setHeader(X_XSS_PROTECTION_HEADER, "custom3");
        expectLastCall().once();
        servletResponse.setHeader(X_CONTENT_TYPE_HEADER, "custom4");
        expectLastCall().once();
        servletResponse.setHeader(CACHE_CONTROL_HEADER, "custom5");
        expectLastCall().once();
        servletResponse.setHeader(PRAGMA_HEADER, "custom6");
        expectLastCall().once();
        servletResponse.setCharacterEncoding("custom7");
        expectLastCall().once();
        FilterChain filterChain = createStrictMock(FilterChain.class);
        filterChain.doFilter(servletRequest, servletResponse);
        expectLastCall().once();
        replayAll();
        AbstractSecurityHeaderFilter securityFilter = injector.getInstance(filterClass);
        Assert.assertNotNull(securityFilter);
        securityFilter.init(filterConfig);
        securityFilter.doFilter(servletRequest, servletResponse, filterChain);
        verifyAll();
    }

    @Test
    public void testDoFilter_CustomValuesSSL() throws Exception {
        final File httpPassFile = temporaryFolder.newFile();
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Properties properties = new Properties();
                properties.setProperty(API_USE_SSL.getKey(), "true");
                properties.setProperty(CLIENT_API_SSL_KSTR_DIR_NAME.getKey(), httpPassFile.getParent());
                properties.setProperty(CLIENT_API_SSL_CRT_PASS_FILE_NAME.getKey(), httpPassFile.getName());
                properties.setProperty(propertyNameMap.get(STRICT_TRANSPORT_HEADER), "custom1");
                properties.setProperty(propertyNameMap.get(X_FRAME_OPTIONS_HEADER), "custom2");
                properties.setProperty(propertyNameMap.get(X_XSS_PROTECTION_HEADER), "custom3");
                properties.setProperty(propertyNameMap.get(X_CONTENT_TYPE_HEADER), "custom4");
                properties.setProperty(propertyNameMap.get(CACHE_CONTROL_HEADER), "custom5");
                properties.setProperty(propertyNameMap.get(PRAGMA_HEADER), "custom6");
                properties.setProperty(HTTP_CHARSET.getKey(), "custom7");
                properties.setProperty(VIEWS_HTTP_CHARSET.getKey(), "custom7");
                bind(OsFamily.class).toInstance(createNiceMock(OsFamily.class));
                bind(Configuration.class).toInstance(new Configuration(properties));
            }
        });
        FilterConfig filterConfig = createNiceMock(FilterConfig.class);
        HttpServletRequest servletRequest = createStrictMock(HttpServletRequest.class);
        expectHttpServletRequestMock(servletRequest);
        HttpServletResponse servletResponse = createStrictMock(HttpServletResponse.class);
        servletResponse.setHeader(STRICT_TRANSPORT_HEADER, "custom1");
        expectLastCall().once();
        servletResponse.setHeader(X_FRAME_OPTIONS_HEADER, "custom2");
        expectLastCall().once();
        servletResponse.setHeader(X_XSS_PROTECTION_HEADER, "custom3");
        expectLastCall().once();
        servletResponse.setHeader(X_CONTENT_TYPE_HEADER, "custom4");
        expectLastCall().once();
        servletResponse.setHeader(CACHE_CONTROL_HEADER, "custom5");
        expectLastCall().once();
        servletResponse.setHeader(PRAGMA_HEADER, "custom6");
        expectLastCall().once();
        servletResponse.setCharacterEncoding("custom7");
        expectLastCall().once();
        FilterChain filterChain = createStrictMock(FilterChain.class);
        filterChain.doFilter(servletRequest, servletResponse);
        expectLastCall().once();
        replayAll();
        AbstractSecurityHeaderFilter securityFilter = injector.getInstance(filterClass);
        Assert.assertNotNull(securityFilter);
        securityFilter.init(filterConfig);
        securityFilter.doFilter(servletRequest, servletResponse, filterChain);
        verifyAll();
    }

    @Test
    public void testDoFilter_EmptyValuesNoSSL() throws Exception {
        final File httpPassFile = temporaryFolder.newFile();
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Properties properties = new Properties();
                properties.setProperty(CLIENT_API_SSL_KSTR_DIR_NAME.getKey(), httpPassFile.getParent());
                properties.setProperty(CLIENT_API_SSL_CRT_PASS_FILE_NAME.getKey(), httpPassFile.getName());
                properties.setProperty(HTTP_CHARSET.getKey(), "");
                properties.setProperty(VIEWS_HTTP_CHARSET.getKey(), "");
                properties.setProperty(propertyNameMap.get(STRICT_TRANSPORT_HEADER), "");
                properties.setProperty(propertyNameMap.get(X_FRAME_OPTIONS_HEADER), "");
                properties.setProperty(propertyNameMap.get(X_XSS_PROTECTION_HEADER), "");
                properties.setProperty(propertyNameMap.get(X_CONTENT_TYPE_HEADER), "");
                properties.setProperty(propertyNameMap.get(CACHE_CONTROL_HEADER), "");
                properties.setProperty(propertyNameMap.get(PRAGMA_HEADER), "");
                bind(OsFamily.class).toInstance(createNiceMock(OsFamily.class));
                bind(Configuration.class).toInstance(new Configuration(properties));
            }
        });
        FilterConfig filterConfig = createNiceMock(FilterConfig.class);
        HttpServletRequest servletRequest = createStrictMock(HttpServletRequest.class);
        expectHttpServletRequestMock(servletRequest);
        HttpServletResponse servletResponse = createStrictMock(HttpServletResponse.class);
        FilterChain filterChain = createStrictMock(FilterChain.class);
        filterChain.doFilter(servletRequest, servletResponse);
        expectLastCall().once();
        replayAll();
        AbstractSecurityHeaderFilter securityFilter = injector.getInstance(filterClass);
        Assert.assertNotNull(securityFilter);
        securityFilter.init(filterConfig);
        securityFilter.doFilter(servletRequest, servletResponse, filterChain);
        verifyAll();
    }

    @Test
    public void testDoFilter_EmptyValuesSSL() throws Exception {
        final File httpPassFile = temporaryFolder.newFile();
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Properties properties = new Properties();
                properties.setProperty(API_USE_SSL.getKey(), "true");
                properties.setProperty(CLIENT_API_SSL_KSTR_DIR_NAME.getKey(), httpPassFile.getParent());
                properties.setProperty(CLIENT_API_SSL_CRT_PASS_FILE_NAME.getKey(), httpPassFile.getName());
                properties.setProperty(HTTP_CHARSET.getKey(), "");
                properties.setProperty(VIEWS_HTTP_CHARSET.getKey(), "");
                properties.setProperty(propertyNameMap.get(STRICT_TRANSPORT_HEADER), "");
                properties.setProperty(propertyNameMap.get(X_FRAME_OPTIONS_HEADER), "");
                properties.setProperty(propertyNameMap.get(X_XSS_PROTECTION_HEADER), "");
                properties.setProperty(propertyNameMap.get(X_CONTENT_TYPE_HEADER), "");
                properties.setProperty(propertyNameMap.get(CACHE_CONTROL_HEADER), "");
                properties.setProperty(propertyNameMap.get(PRAGMA_HEADER), "");
                bind(OsFamily.class).toInstance(createNiceMock(OsFamily.class));
                bind(Configuration.class).toInstance(new Configuration(properties));
            }
        });
        FilterConfig filterConfig = createNiceMock(FilterConfig.class);
        HttpServletRequest servletRequest = createStrictMock(HttpServletRequest.class);
        expectHttpServletRequestMock(servletRequest);
        HttpServletResponse servletResponse = createStrictMock(HttpServletResponse.class);
        FilterChain filterChain = createStrictMock(FilterChain.class);
        filterChain.doFilter(servletRequest, servletResponse);
        expectLastCall().once();
        replayAll();
        AbstractSecurityHeaderFilter securityFilter = injector.getInstance(filterClass);
        Assert.assertNotNull(securityFilter);
        securityFilter.init(filterConfig);
        securityFilter.doFilter(servletRequest, servletResponse, filterChain);
        verifyAll();
    }
}

