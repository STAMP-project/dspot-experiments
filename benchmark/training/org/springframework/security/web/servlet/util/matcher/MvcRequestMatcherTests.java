/**
 * Copyright 2012-2016 the original author or authors.
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
package org.springframework.security.web.servlet.util.matcher;


import HttpMethod.GET;
import HttpMethod.POST;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.handler.MatchableHandlerMapping;
import org.springframework.web.servlet.handler.RequestMatchResult;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class MvcRequestMatcherTests {
    @Mock
    HandlerMappingIntrospector introspector;

    @Mock
    MatchableHandlerMapping mapping;

    @Mock
    RequestMatchResult result;

    @Captor
    ArgumentCaptor<String> pattern;

    MockHttpServletRequest request;

    MvcRequestMatcher matcher;

    @Test
    public void extractUriTemplateVariablesSuccess() throws Exception {
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenReturn(this.mapping);
        this.matcher = new MvcRequestMatcher(this.introspector, "/{p}");
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenReturn(null);
        assertThat(this.matcher.extractUriTemplateVariables(this.request)).containsEntry("p", "path");
    }

    @Test
    public void extractUriTemplateVariablesFail() throws Exception {
        Mockito.when(this.result.extractUriTemplateVariables()).thenReturn(Collections.<String, String>emptyMap());
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenReturn(this.mapping);
        Mockito.when(this.mapping.match(ArgumentMatchers.eq(this.request), this.pattern.capture())).thenReturn(this.result);
        assertThat(this.matcher.extractUriTemplateVariables(this.request)).isEmpty();
    }

    @Test
    public void extractUriTemplateVariablesDefaultSuccess() throws Exception {
        this.matcher = new MvcRequestMatcher(this.introspector, "/{p}");
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenReturn(null);
        assertThat(this.matcher.extractUriTemplateVariables(this.request)).containsEntry("p", "path");
    }

    @Test
    public void extractUriTemplateVariablesDefaultFail() throws Exception {
        this.matcher = new MvcRequestMatcher(this.introspector, "/nomatch/{p}");
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenReturn(null);
        assertThat(this.matcher.extractUriTemplateVariables(this.request)).isEmpty();
    }

    @Test
    public void matchesServletPathTrue() throws Exception {
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenReturn(this.mapping);
        Mockito.when(this.mapping.match(ArgumentMatchers.eq(this.request), this.pattern.capture())).thenReturn(this.result);
        this.matcher.setServletPath("/spring");
        this.request.setServletPath("/spring");
        assertThat(this.matcher.matches(this.request)).isTrue();
        assertThat(this.pattern.getValue()).isEqualTo("/path");
    }

    @Test
    public void matchesServletPathFalse() throws Exception {
        this.matcher.setServletPath("/spring");
        this.request.setServletPath("/");
        assertThat(this.matcher.matches(this.request)).isFalse();
    }

    @Test
    public void matchesPathOnlyTrue() throws Exception {
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenReturn(this.mapping);
        Mockito.when(this.mapping.match(ArgumentMatchers.eq(this.request), this.pattern.capture())).thenReturn(this.result);
        assertThat(this.matcher.matches(this.request)).isTrue();
        assertThat(this.pattern.getValue()).isEqualTo("/path");
    }

    @Test
    public void matchesDefaultMatches() throws Exception {
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenReturn(null);
        assertThat(this.matcher.matches(this.request)).isTrue();
    }

    @Test
    public void matchesDefaultDoesNotMatch() throws Exception {
        this.request.setServletPath("/other");
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenReturn(null);
        assertThat(this.matcher.matches(this.request)).isFalse();
    }

    @Test
    public void matchesPathOnlyFalse() throws Exception {
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenReturn(this.mapping);
        assertThat(this.matcher.matches(this.request)).isFalse();
    }

    @Test
    public void matchesMethodAndPathTrue() throws Exception {
        this.matcher.setMethod(GET);
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenReturn(this.mapping);
        Mockito.when(this.mapping.match(ArgumentMatchers.eq(this.request), this.pattern.capture())).thenReturn(this.result);
        assertThat(this.matcher.matches(this.request)).isTrue();
        assertThat(this.pattern.getValue()).isEqualTo("/path");
    }

    @Test
    public void matchesMethodAndPathFalseMethod() throws Exception {
        this.matcher.setMethod(POST);
        assertThat(this.matcher.matches(this.request)).isFalse();
        // method compare should be done first since faster
        Mockito.verifyZeroInteractions(this.introspector);
    }

    /**
     * Malicious users can specify any HTTP Method to create a stacktrace and try to
     * expose useful information about the system. We should ensure we ignore invalid HTTP
     * methods.
     *
     * @throws Exception
     * 		if an error occurs
     */
    @Test
    public void matchesInvalidMethodOnRequest() throws Exception {
        this.matcher.setMethod(GET);
        this.request.setMethod("invalid");
        assertThat(this.matcher.matches(this.request)).isFalse();
        // method compare should be done first since faster
        Mockito.verifyZeroInteractions(this.introspector);
    }

    @Test
    public void matchesMethodAndPathFalsePath() throws Exception {
        this.matcher.setMethod(GET);
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenReturn(this.mapping);
        assertThat(this.matcher.matches(this.request)).isFalse();
    }

    @Test
    public void matchesGetMatchableHandlerMappingNull() throws Exception {
        assertThat(this.matcher.matches(this.request)).isTrue();
    }

    @Test
    public void matchesGetMatchableHandlerMappingThrows() throws Exception {
        Mockito.when(this.introspector.getMatchableHandlerMapping(this.request)).thenThrow(new org.springframework.web.HttpRequestMethodNotSupportedException(this.request.getMethod()));
        assertThat(this.matcher.matches(this.request)).isTrue();
    }

    @Test
    public void toStringWhenAll() {
        this.matcher.setMethod(GET);
        this.matcher.setServletPath("/spring");
        assertThat(this.matcher.toString()).isEqualTo("Mvc [pattern='/path', servletPath='/spring', GET]");
    }

    @Test
    public void toStringWhenHttpMethod() {
        this.matcher.setMethod(GET);
        assertThat(this.matcher.toString()).isEqualTo("Mvc [pattern='/path', GET]");
    }

    @Test
    public void toStringWhenServletPath() {
        this.matcher.setServletPath("/spring");
        assertThat(this.matcher.toString()).isEqualTo("Mvc [pattern='/path', servletPath='/spring']");
    }

    @Test
    public void toStringWhenOnlyPattern() {
        assertThat(this.matcher.toString()).isEqualTo("Mvc [pattern='/path']");
    }
}

