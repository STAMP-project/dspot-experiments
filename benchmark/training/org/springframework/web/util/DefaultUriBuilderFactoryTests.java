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
package org.springframework.web.util;


import EncodingMode.NONE;
import EncodingMode.TEMPLATE_AND_VALUES;
import EncodingMode.URI_COMPONENT;
import EncodingMode.VALUES_ONLY;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Unit tests for {@link DefaultUriBuilderFactory}.
 *
 * @author Rossen Stoyanchev
 */
public class DefaultUriBuilderFactoryTests {
    @Test
    public void defaultSettings() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        URI uri = factory.uriString("/foo/{id}").build("a/b");
        TestCase.assertEquals("/foo/a%2Fb", uri.toString());
    }

    // SPR-17465
    @Test
    public void defaultSettingsWithBuilder() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        URI uri = factory.builder().path("/foo/{id}").build("a/b");
        TestCase.assertEquals("/foo/a%2Fb", uri.toString());
    }

    @Test
    public void baseUri() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("http://foo.com/v1?id=123");
        URI uri = factory.uriString("/bar").port(8080).build();
        TestCase.assertEquals("http://foo.com:8080/v1/bar?id=123", uri.toString());
    }

    @Test
    public void baseUriWithFullOverride() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("http://foo.com/v1?id=123");
        URI uri = factory.uriString("http://example.com/1/2").build();
        TestCase.assertEquals("Use of host should case baseUri to be completely ignored", "http://example.com/1/2", uri.toString());
    }

    @Test
    public void baseUriWithPathOverride() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("http://foo.com/v1");
        URI uri = factory.builder().replacePath("/baz").build();
        TestCase.assertEquals("http://foo.com/baz", uri.toString());
    }

    @Test
    public void defaultUriVars() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("http://{host}/v1");
        factory.setDefaultUriVariables(Collections.singletonMap("host", "foo.com"));
        URI uri = factory.uriString("/{id}").build(Collections.singletonMap("id", "123"));
        TestCase.assertEquals("http://foo.com/v1/123", uri.toString());
    }

    @Test
    public void defaultUriVarsWithOverride() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("http://{host}/v1");
        factory.setDefaultUriVariables(Collections.singletonMap("host", "spring.io"));
        URI uri = factory.uriString("/bar").build(Collections.singletonMap("host", "docs.spring.io"));
        TestCase.assertEquals("http://docs.spring.io/v1/bar", uri.toString());
    }

    @Test
    public void defaultUriVarsWithEmptyVarArg() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("http://{host}/v1");
        factory.setDefaultUriVariables(Collections.singletonMap("host", "foo.com"));
        URI uri = factory.uriString("/bar").build();
        TestCase.assertEquals("Expected delegation to build(Map) method", "http://foo.com/v1/bar", uri.toString());
    }

    @Test
    public void defaultUriVarsSpr14147() {
        Map<String, String> defaultUriVars = new HashMap<>(2);
        defaultUriVars.put("host", "api.example.com");
        defaultUriVars.put("port", "443");
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setDefaultUriVariables(defaultUriVars);
        URI uri = factory.expand("https://{host}:{port}/v42/customers/{id}", Collections.singletonMap("id", 123L));
        TestCase.assertEquals("https://api.example.com:443/v42/customers/123", uri.toString());
    }

    @Test
    public void encodeTemplateAndValues() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(TEMPLATE_AND_VALUES);
        UriBuilder uriBuilder = factory.uriString("/hotel list/{city} specials?q={value}");
        String expected = "/hotel%20list/Z%C3%BCrich%20specials?q=a%2Bb";
        Map<String, Object> vars = new HashMap<>();
        vars.put("city", "Z\u00fcrich");
        vars.put("value", "a+b");
        TestCase.assertEquals(expected, uriBuilder.build("Z\u00fcrich", "a+b").toString());
        TestCase.assertEquals(expected, uriBuilder.build(vars).toString());
    }

    @Test
    public void encodingValuesOnly() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(VALUES_ONLY);
        UriBuilder uriBuilder = factory.uriString("/foo/a%2Fb/{id}");
        String id = "c/d";
        String expected = "/foo/a%2Fb/c%2Fd";
        TestCase.assertEquals(expected, uriBuilder.build(id).toString());
        TestCase.assertEquals(expected, uriBuilder.build(Collections.singletonMap("id", id)).toString());
    }

    @Test
    public void encodingValuesOnlySpr14147() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(VALUES_ONLY);
        factory.setDefaultUriVariables(Collections.singletonMap("host", "www.example.com"));
        UriBuilder uriBuilder = factory.uriString("http://{host}/user/{userId}/dashboard");
        TestCase.assertEquals("http://www.example.com/user/john%3Bdoe/dashboard", uriBuilder.build(Collections.singletonMap("userId", "john;doe")).toString());
    }

    @Test
    public void encodingNone() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(NONE);
        UriBuilder uriBuilder = factory.uriString("/foo/a%2Fb/{id}");
        String id = "c%2Fd";
        String expected = "/foo/a%2Fb/c%2Fd";
        TestCase.assertEquals(expected, uriBuilder.build(id).toString());
        TestCase.assertEquals(expected, uriBuilder.build(Collections.singletonMap("id", id)).toString());
    }

    @Test
    public void parsePathWithDefaultSettings() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("/foo/{bar}");
        URI uri = factory.uriString("/baz/{id}").build("a/b", "c/d");
        TestCase.assertEquals("/foo/a%2Fb/baz/c%2Fd", uri.toString());
    }

    @Test
    public void parsePathIsTurnedOff() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("/foo/{bar}");
        factory.setEncodingMode(URI_COMPONENT);
        factory.setParsePath(false);
        URI uri = factory.uriString("/baz/{id}").build("a/b", "c/d");
        TestCase.assertEquals("/foo/a/b/baz/c/d", uri.toString());
    }

    // SPR-15201
    @Test
    public void pathWithTrailingSlash() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        URI uri = factory.expand("http://localhost:8080/spring/");
        TestCase.assertEquals("http://localhost:8080/spring/", uri.toString());
    }

    @Test
    public void pathWithDuplicateSlashes() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        URI uri = factory.expand("/foo/////////bar");
        TestCase.assertEquals("/foo/bar", uri.toString());
    }
}

