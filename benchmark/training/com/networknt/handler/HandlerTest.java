/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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
package com.networknt.handler;


import Handler.config;
import Methods.GET;
import com.networknt.handler.config.EndpointSource;
import com.networknt.utility.Tuple;
import io.undertow.server.HttpHandler;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatcher;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static Handler.handlerListById;
import static Handler.methodToMatcherMap;


public class HandlerTest {
    @Test
    public void validClassNameWithoutAt_split_returnsCorrect() throws Exception {
        Tuple<String, Class> sample1 = Handler.splitClassAndName("com.networknt.handler.sample.SampleHttpHandler1");
        Assert.assertEquals("com.networknt.handler.sample.SampleHttpHandler1", sample1.first);
        Assert.assertEquals(Class.forName("com.networknt.handler.sample.SampleHttpHandler1"), sample1.second);
    }

    @Test
    public void validClassNameWithAt_split_returnsCorrect() throws Exception {
        Tuple<String, Class> sample1 = Handler.splitClassAndName("com.networknt.handler.sample.SampleHttpHandler1@Hello");
        Assert.assertEquals("Hello", sample1.first);
        Assert.assertEquals(Class.forName("com.networknt.handler.sample.SampleHttpHandler1"), sample1.second);
    }

    @Test
    public void validConfig_init_handlersCreated() {
        Handler.init();
        Map<String, List<HttpHandler>> handlers = handlerListById;
        Assert.assertEquals(1, handlers.get("third").size());
        Assert.assertEquals(2, handlers.get("secondBeforeFirst").size());
    }

    static class MockEndpointSource implements EndpointSource {
        @Override
        public Iterable<EndpointSource.Endpoint> listEndpoints() {
            return Arrays.asList(new EndpointSource.Endpoint("/my-api/first", "get"), new EndpointSource.Endpoint("/my-api/first", "put"), new EndpointSource.Endpoint("/my-api/second", "get"));
        }
    }

    @Test
    public void mixedPathsAndSource() {
        config.setPaths(Arrays.asList(mkPathChain(null, "/my-api/first", "post", "third"), mkPathChain(HandlerTest.MockEndpointSource.class.getName(), null, null, "secondBeforeFirst", "third"), mkPathChain(null, "/my-api/second", "put", "third")));
        Handler.init();
        Map<HttpString, PathTemplateMatcher<String>> methodToMatcher = methodToMatcherMap;
        PathTemplateMatcher<String> getMatcher = methodToMatcher.get(GET);
        PathTemplateMatcher.PathMatchResult<String> getFirst = getMatcher.match("/my-api/first");
        Assert.assertNotNull(getFirst);
        PathTemplateMatcher.PathMatchResult<String> getSecond = getMatcher.match("/my-api/second");
        Assert.assertNotNull(getSecond);
        PathTemplateMatcher.PathMatchResult<String> getThird = getMatcher.match("/my-api/third");
        Assert.assertNull(getThird);
    }

    @Test
    public void conflictingSourceAndPath_init_throws() {
        // Reconfigure path chain with an invalid path in the middle
        config.setPaths(Arrays.asList(mkPathChain(null, "/a/good/path", "POST", "third"), mkPathChain("source", "/conflicting/path", "PUT", "third"), mkPathChain("source", null, null, "some-chain", "third")));
        // Use expectThrows when porting to java5.
        // Not adding(exception=) to @Test since we care _where_ the exception is thrown
        try {
            Handler.init();
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Conflicting source"));
            Assert.assertTrue(e.getMessage().contains("and path"));
            Assert.assertTrue(e.getMessage().contains("and method"));
        }
    }

    @Test(expected = Exception.class)
    public void invalidMethod_init_throws() throws Exception {
        Handler.setConfig("invalid-method");
    }
}

