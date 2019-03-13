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
package org.springframework.web.filter;


import WebUtils.ERROR_REQUEST_URI_ATTRIBUTE;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.mock.web.test.MockFilterConfig;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;


/**
 *
 *
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Vedran Pavic
 */
public class CharacterEncodingFilterTests {
    private static final String FILTER_NAME = "boot";

    private static final String ENCODING = "UTF-8";

    @Test
    public void forceEncodingAlwaysSetsEncoding() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        request.setCharacterEncoding(CharacterEncodingFilterTests.ENCODING);
        BDDMockito.given(request.getAttribute(ERROR_REQUEST_URI_ATTRIBUTE)).willReturn(null);
        BDDMockito.given(request.getAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME))).willReturn(null);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        CharacterEncodingFilter filter = new CharacterEncodingFilter(CharacterEncodingFilterTests.ENCODING, true);
        filter.init(new MockFilterConfig(CharacterEncodingFilterTests.FILTER_NAME));
        filter.doFilter(request, response, filterChain);
        Mockito.verify(request).setAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME), Boolean.TRUE);
        Mockito.verify(request).removeAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME));
        Mockito.verify(response).setCharacterEncoding(CharacterEncodingFilterTests.ENCODING);
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    public void encodingIfEmptyAndNotForced() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        BDDMockito.given(request.getCharacterEncoding()).willReturn(null);
        BDDMockito.given(request.getAttribute(ERROR_REQUEST_URI_ATTRIBUTE)).willReturn(null);
        BDDMockito.given(request.getAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME))).willReturn(null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        CharacterEncodingFilter filter = new CharacterEncodingFilter(CharacterEncodingFilterTests.ENCODING);
        filter.init(new MockFilterConfig(CharacterEncodingFilterTests.FILTER_NAME));
        filter.doFilter(request, response, filterChain);
        Mockito.verify(request).setCharacterEncoding(CharacterEncodingFilterTests.ENCODING);
        Mockito.verify(request).setAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME), Boolean.TRUE);
        Mockito.verify(request).removeAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME));
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doesNotIfEncodingIsNotEmptyAndNotForced() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        BDDMockito.given(request.getCharacterEncoding()).willReturn(CharacterEncodingFilterTests.ENCODING);
        BDDMockito.given(request.getAttribute(ERROR_REQUEST_URI_ATTRIBUTE)).willReturn(null);
        BDDMockito.given(request.getAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME))).willReturn(null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        CharacterEncodingFilter filter = new CharacterEncodingFilter(CharacterEncodingFilterTests.ENCODING);
        filter.init(new MockFilterConfig(CharacterEncodingFilterTests.FILTER_NAME));
        filter.doFilter(request, response, filterChain);
        Mockito.verify(request).setAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME), Boolean.TRUE);
        Mockito.verify(request).removeAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME));
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    public void withBeanInitialization() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        BDDMockito.given(request.getCharacterEncoding()).willReturn(null);
        BDDMockito.given(request.getAttribute(ERROR_REQUEST_URI_ATTRIBUTE)).willReturn(null);
        BDDMockito.given(request.getAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME))).willReturn(null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding(CharacterEncodingFilterTests.ENCODING);
        filter.setBeanName(CharacterEncodingFilterTests.FILTER_NAME);
        filter.setServletContext(new MockServletContext());
        filter.doFilter(request, response, filterChain);
        Mockito.verify(request).setCharacterEncoding(CharacterEncodingFilterTests.ENCODING);
        Mockito.verify(request).setAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME), Boolean.TRUE);
        Mockito.verify(request).removeAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME));
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    public void withIncompleteInitialization() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        BDDMockito.given(request.getCharacterEncoding()).willReturn(null);
        BDDMockito.given(request.getAttribute(ERROR_REQUEST_URI_ATTRIBUTE)).willReturn(null);
        BDDMockito.given(request.getAttribute(filteredName(CharacterEncodingFilter.class.getName()))).willReturn(null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        CharacterEncodingFilter filter = new CharacterEncodingFilter(CharacterEncodingFilterTests.ENCODING);
        filter.doFilter(request, response, filterChain);
        Mockito.verify(request).setCharacterEncoding(CharacterEncodingFilterTests.ENCODING);
        Mockito.verify(request).setAttribute(filteredName(CharacterEncodingFilter.class.getName()), Boolean.TRUE);
        Mockito.verify(request).removeAttribute(filteredName(CharacterEncodingFilter.class.getName()));
        Mockito.verify(filterChain).doFilter(request, response);
    }

    // SPR-14240
    @Test
    public void setForceEncodingOnRequestOnly() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        request.setCharacterEncoding(CharacterEncodingFilterTests.ENCODING);
        BDDMockito.given(request.getAttribute(ERROR_REQUEST_URI_ATTRIBUTE)).willReturn(null);
        BDDMockito.given(request.getAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME))).willReturn(null);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        CharacterEncodingFilter filter = new CharacterEncodingFilter(CharacterEncodingFilterTests.ENCODING, true, false);
        filter.init(new MockFilterConfig(CharacterEncodingFilterTests.FILTER_NAME));
        filter.doFilter(request, response, filterChain);
        Mockito.verify(request).setAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME), Boolean.TRUE);
        Mockito.verify(request).removeAttribute(filteredName(CharacterEncodingFilterTests.FILTER_NAME));
        Mockito.verify(request, Mockito.times(2)).setCharacterEncoding(CharacterEncodingFilterTests.ENCODING);
        Mockito.verify(response, Mockito.never()).setCharacterEncoding(CharacterEncodingFilterTests.ENCODING);
        Mockito.verify(filterChain).doFilter(request, response);
    }
}

