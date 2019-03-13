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
package org.springframework.web.servlet.mvc.method;


import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.ORIGIN;
import RequestMethod.OPTIONS;
import RequestMethod.POST;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;


/**
 * Test fixture for {@link RequestMappingInfo} tests.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class RequestMappingInfoTests {
    @Test
    public void createEmpty() {
        RequestMappingInfo info = RequestMappingInfo.paths().build();
        Assert.assertEquals(0, info.getPatternsCondition().getPatterns().size());
        Assert.assertEquals(0, info.getMethodsCondition().getMethods().size());
        Assert.assertEquals(true, info.getConsumesCondition().isEmpty());
        Assert.assertEquals(true, info.getProducesCondition().isEmpty());
        Assert.assertNotNull(info.getParamsCondition());
        Assert.assertNotNull(info.getHeadersCondition());
        Assert.assertNull(info.getCustomCondition());
    }

    @Test
    public void matchPatternsCondition() {
        HttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        RequestMappingInfo info = RequestMappingInfo.paths("/foo*", "/bar").build();
        RequestMappingInfo expected = RequestMappingInfo.paths("/foo*").build();
        Assert.assertEquals(expected, info.getMatchingCondition(request));
        info = RequestMappingInfo.paths("/**", "/foo*", "/foo").build();
        expected = RequestMappingInfo.paths("/foo", "/foo*", "/**").build();
        Assert.assertEquals(expected, info.getMatchingCondition(request));
    }

    @Test
    public void matchParamsCondition() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        request.setParameter("foo", "bar");
        RequestMappingInfo info = RequestMappingInfo.paths("/foo").params("foo=bar").build();
        RequestMappingInfo match = info.getMatchingCondition(request);
        Assert.assertNotNull(match);
        info = RequestMappingInfo.paths("/foo").params("foo!=bar").build();
        match = info.getMatchingCondition(request);
        Assert.assertNull(match);
    }

    @Test
    public void matchHeadersCondition() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        request.addHeader("foo", "bar");
        RequestMappingInfo info = RequestMappingInfo.paths("/foo").headers("foo=bar").build();
        RequestMappingInfo match = info.getMatchingCondition(request);
        Assert.assertNotNull(match);
        info = RequestMappingInfo.paths("/foo").headers("foo!=bar").build();
        match = info.getMatchingCondition(request);
        Assert.assertNull(match);
    }

    @Test
    public void matchConsumesCondition() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        request.setContentType("text/plain");
        RequestMappingInfo info = RequestMappingInfo.paths("/foo").consumes("text/plain").build();
        RequestMappingInfo match = info.getMatchingCondition(request);
        Assert.assertNotNull(match);
        info = RequestMappingInfo.paths("/foo").consumes("application/xml").build();
        match = info.getMatchingCondition(request);
        Assert.assertNull(match);
    }

    @Test
    public void matchProducesCondition() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        request.addHeader("Accept", "text/plain");
        RequestMappingInfo info = RequestMappingInfo.paths("/foo").produces("text/plain").build();
        RequestMappingInfo match = info.getMatchingCondition(request);
        Assert.assertNotNull(match);
        info = RequestMappingInfo.paths("/foo").produces("application/xml").build();
        match = info.getMatchingCondition(request);
        Assert.assertNull(match);
    }

    @Test
    public void matchCustomCondition() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        request.setParameter("foo", "bar");
        RequestMappingInfo info = RequestMappingInfo.paths("/foo").params("foo=bar").build();
        RequestMappingInfo match = info.getMatchingCondition(request);
        Assert.assertNotNull(match);
        info = RequestMappingInfo.paths("/foo").params("foo!=bar").params("foo!=bar").build();
        match = info.getMatchingCondition(request);
        Assert.assertNull(match);
    }

    @Test
    public void compareToWithImpicitVsExplicitHttpMethodDeclaration() {
        RequestMappingInfo noMethods = RequestMappingInfo.paths().build();
        RequestMappingInfo oneMethod = RequestMappingInfo.paths().methods(GET).build();
        RequestMappingInfo oneMethodOneParam = RequestMappingInfo.paths().methods(GET).params("foo").build();
        Comparator<RequestMappingInfo> comparator = ( info, otherInfo) -> info.compareTo(otherInfo, new MockHttpServletRequest());
        List<RequestMappingInfo> list = Arrays.asList(noMethods, oneMethod, oneMethodOneParam);
        Collections.shuffle(list);
        Collections.sort(list, comparator);
        Assert.assertEquals(oneMethodOneParam, list.get(0));
        Assert.assertEquals(oneMethod, list.get(1));
        Assert.assertEquals(noMethods, list.get(2));
    }

    // SPR-14383
    @Test
    public void compareToWithHttpHeadMapping() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("HEAD");
        request.addHeader("Accept", "application/json");
        RequestMappingInfo noMethods = RequestMappingInfo.paths().build();
        RequestMappingInfo getMethod = RequestMappingInfo.paths().methods(GET).produces("application/json").build();
        RequestMappingInfo headMethod = RequestMappingInfo.paths().methods(HEAD).build();
        Comparator<RequestMappingInfo> comparator = ( info, otherInfo) -> info.compareTo(otherInfo, request);
        List<RequestMappingInfo> list = Arrays.asList(noMethods, getMethod, headMethod);
        Collections.shuffle(list);
        Collections.sort(list, comparator);
        Assert.assertEquals(headMethod, list.get(0));
        Assert.assertEquals(getMethod, list.get(1));
        Assert.assertEquals(noMethods, list.get(2));
    }

    @Test
    public void equals() {
        RequestMappingInfo info1 = RequestMappingInfo.paths("/foo").methods(GET).params("foo=bar", "customFoo=customBar").headers("foo=bar").consumes("text/plain").produces("text/plain").build();
        RequestMappingInfo info2 = RequestMappingInfo.paths("/foo").methods(GET).params("foo=bar", "customFoo=customBar").headers("foo=bar").consumes("text/plain").produces("text/plain").build();
        Assert.assertEquals(info1, info2);
        Assert.assertEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo", "/NOOOOOO").methods(GET).params("foo=bar", "customFoo=customBar").headers("foo=bar").consumes("text/plain").produces("text/plain").build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo").methods(GET, POST).params("foo=bar", "customFoo=customBar").headers("foo=bar").consumes("text/plain").produces("text/plain").build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo").methods(GET).params("/NOOOOOO", "customFoo=customBar").headers("foo=bar").consumes("text/plain").produces("text/plain").build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo").methods(GET).params("foo=bar", "customFoo=customBar").headers("/NOOOOOO").consumes("text/plain").produces("text/plain").build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo").methods(GET).params("foo=bar", "customFoo=customBar").headers("foo=bar").consumes("text/NOOOOOO").produces("text/plain").build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo").methods(GET).params("foo=bar", "customFoo=customBar").headers("foo=bar").consumes("text/plain").produces("text/NOOOOOO").build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo").methods(GET).params("foo=bar", "customFoo=NOOOOOO").headers("foo=bar").consumes("text/plain").produces("text/plain").build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
    }

    @Test
    public void preFlightRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/foo");
        request.addHeader(ORIGIN, "http://domain.com");
        request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "POST");
        RequestMappingInfo info = RequestMappingInfo.paths("/foo").methods(POST).build();
        RequestMappingInfo match = info.getMatchingCondition(request);
        Assert.assertNotNull(match);
        info = RequestMappingInfo.paths("/foo").methods(OPTIONS).build();
        match = info.getMatchingCondition(request);
        Assert.assertNull("Pre-flight should match the ACCESS_CONTROL_REQUEST_METHOD", match);
    }
}

