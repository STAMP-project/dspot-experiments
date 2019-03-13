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
package org.springframework.web.reactive.result.condition;


import MediaType.TEXT_PLAIN;
import RequestMethod.GET;
import RequestMethod.POST;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PatternParseException;


/**
 * Unit tests for {@link RequestMappingInfo}.
 *
 * @author Rossen Stoyanchev
 */
public class RequestMappingInfoTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // TODO: CORS pre-flight (see @Ignore)
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
    public void throwWhenInvalidPattern() {
        this.thrown.expect(PatternParseException.class);
        this.thrown.expectMessage("Expected close capture character after variable name }");
        RequestMappingInfo.paths("/{foo").build();
    }

    @Test
    public void prependPatternWithSlash() {
        RequestMappingInfo actual = RequestMappingInfo.paths("foo").build();
        List<PathPattern> patterns = new java.util.ArrayList(actual.getPatternsCondition().getPatterns());
        Assert.assertEquals(1, patterns.size());
        Assert.assertEquals("/foo", patterns.get(0).getPatternString());
    }

    @Test
    public void matchPatternsCondition() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/foo"));
        RequestMappingInfo info = RequestMappingInfo.paths("/foo*", "/bar").build();
        RequestMappingInfo expected = RequestMappingInfo.paths("/foo*").build();
        Assert.assertEquals(expected, info.getMatchingCondition(exchange));
        info = RequestMappingInfo.paths("/**", "/foo*", "/foo").build();
        expected = RequestMappingInfo.paths("/foo", "/foo*", "/**").build();
        Assert.assertEquals(expected, info.getMatchingCondition(exchange));
    }

    @Test
    public void matchParamsCondition() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/foo?foo=bar"));
        RequestMappingInfo info = RequestMappingInfo.paths("/foo").params("foo=bar").build();
        RequestMappingInfo match = info.getMatchingCondition(exchange);
        Assert.assertNotNull(match);
        info = RequestMappingInfo.paths("/foo").params("foo!=bar").build();
        match = info.getMatchingCondition(exchange);
        Assert.assertNull(match);
    }

    @Test
    public void matchHeadersCondition() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/foo").header("foo", "bar").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        RequestMappingInfo info = RequestMappingInfo.paths("/foo").headers("foo=bar").build();
        RequestMappingInfo match = info.getMatchingCondition(exchange);
        Assert.assertNotNull(match);
        info = RequestMappingInfo.paths("/foo").headers("foo!=bar").build();
        match = info.getMatchingCondition(exchange);
        Assert.assertNull(match);
    }

    @Test
    public void matchConsumesCondition() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/foo").contentType(TEXT_PLAIN).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        RequestMappingInfo info = RequestMappingInfo.paths("/foo").consumes("text/plain").build();
        RequestMappingInfo match = info.getMatchingCondition(exchange);
        Assert.assertNotNull(match);
        info = RequestMappingInfo.paths("/foo").consumes("application/xml").build();
        match = info.getMatchingCondition(exchange);
        Assert.assertNull(match);
    }

    @Test
    public void matchProducesCondition() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/foo").accept(TEXT_PLAIN).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        RequestMappingInfo info = RequestMappingInfo.paths("/foo").produces("text/plain").build();
        RequestMappingInfo match = info.getMatchingCondition(exchange);
        Assert.assertNotNull(match);
        info = RequestMappingInfo.paths("/foo").produces("application/xml").build();
        match = info.getMatchingCondition(exchange);
        Assert.assertNull(match);
    }

    @Test
    public void matchCustomCondition() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/foo?foo=bar"));
        RequestMappingInfo info = RequestMappingInfo.paths("/foo").params("foo=bar").build();
        RequestMappingInfo match = info.getMatchingCondition(exchange);
        Assert.assertNotNull(match);
        info = RequestMappingInfo.paths("/foo").params("foo!=bar").customCondition(new ParamsRequestCondition("foo!=bar")).build();
        match = info.getMatchingCondition(exchange);
        Assert.assertNull(match);
    }

    @Test
    public void compareTwoHttpMethodsOneParam() {
        RequestMappingInfo none = RequestMappingInfo.paths().build();
        RequestMappingInfo oneMethod = RequestMappingInfo.paths().methods(GET).build();
        RequestMappingInfo oneMethodOneParam = RequestMappingInfo.paths().methods(GET).params("foo").build();
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/foo"));
        Comparator<RequestMappingInfo> comparator = ( info, otherInfo) -> info.compareTo(otherInfo, exchange);
        List<RequestMappingInfo> list = Arrays.asList(none, oneMethod, oneMethodOneParam);
        Collections.shuffle(list);
        list.sort(comparator);
        Assert.assertEquals(oneMethodOneParam, list.get(0));
        Assert.assertEquals(oneMethod, list.get(1));
        Assert.assertEquals(none, list.get(2));
    }

    @Test
    public void equals() {
        RequestMappingInfo info1 = RequestMappingInfo.paths("/foo").methods(GET).params("foo=bar").headers("foo=bar").consumes("text/plain").produces("text/plain").customCondition(new ParamsRequestCondition("customFoo=customBar")).build();
        RequestMappingInfo info2 = RequestMappingInfo.paths("/foo").methods(GET).params("foo=bar").headers("foo=bar").consumes("text/plain").produces("text/plain").customCondition(new ParamsRequestCondition("customFoo=customBar")).build();
        Assert.assertEquals(info1, info2);
        Assert.assertEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo", "/NOOOOOO").methods(GET).params("foo=bar").headers("foo=bar").consumes("text/plain").produces("text/plain").customCondition(new ParamsRequestCondition("customFoo=customBar")).build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo").methods(GET, POST).params("foo=bar").headers("foo=bar").consumes("text/plain").produces("text/plain").customCondition(new ParamsRequestCondition("customFoo=customBar")).build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo").methods(GET).params("/NOOOOOO").headers("foo=bar").consumes("text/plain").produces("text/plain").customCondition(new ParamsRequestCondition("customFoo=customBar")).build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo").methods(GET).params("foo=bar").headers("/NOOOOOO").consumes("text/plain").produces("text/plain").customCondition(new ParamsRequestCondition("customFoo=customBar")).build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo").methods(GET).params("foo=bar").headers("foo=bar").consumes("text/NOOOOOO").produces("text/plain").customCondition(new ParamsRequestCondition("customFoo=customBar")).build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo").methods(GET).params("foo=bar").headers("foo=bar").consumes("text/plain").produces("text/NOOOOOO").customCondition(new ParamsRequestCondition("customFoo=customBar")).build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
        info2 = RequestMappingInfo.paths("/foo").methods(GET).params("foo=bar").headers("foo=bar").consumes("text/plain").produces("text/plain").customCondition(new ParamsRequestCondition("customFoo=NOOOOOO")).build();
        Assert.assertFalse(info1.equals(info2));
        Assert.assertNotEquals(info1.hashCode(), info2.hashCode());
    }
}

