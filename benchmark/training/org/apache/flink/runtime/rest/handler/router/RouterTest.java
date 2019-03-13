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
package org.apache.flink.runtime.rest.handler.router;


import java.util.Set;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.http.HttpMethod;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link Router}.
 */
public class RouterTest {
    private Router<String> router;

    @Test
    public void testIgnoreSlashesAtBothEnds() {
        Assert.assertEquals("index", router.route(GET, "articles").target());
        Assert.assertEquals("index", router.route(GET, "/articles").target());
        Assert.assertEquals("index", router.route(GET, "//articles").target());
        Assert.assertEquals("index", router.route(GET, "articles/").target());
        Assert.assertEquals("index", router.route(GET, "articles//").target());
        Assert.assertEquals("index", router.route(GET, "/articles/").target());
        Assert.assertEquals("index", router.route(GET, "//articles//").target());
    }

    @Test
    public void testEmptyParams() {
        RouteResult<String> routed = router.route(GET, "/articles");
        Assert.assertEquals("index", routed.target());
        Assert.assertEquals(0, routed.pathParams().size());
    }

    @Test
    public void testParams() {
        RouteResult<String> routed = router.route(GET, "/articles/123");
        Assert.assertEquals("show", routed.target());
        Assert.assertEquals(1, routed.pathParams().size());
        Assert.assertEquals("123", routed.pathParams().get("id"));
    }

    @Test
    public void testNone() {
        RouteResult<String> routed = router.route(GET, "/noexist");
        Assert.assertEquals("404", routed.target());
    }

    @Test
    public void testSplatWildcard() {
        RouteResult<String> routed = router.route(GET, "/download/foo/bar.png");
        Assert.assertEquals("download", routed.target());
        Assert.assertEquals(1, routed.pathParams().size());
        Assert.assertEquals("foo/bar.png", routed.pathParams().get("*"));
    }

    @Test
    public void testOrder() {
        RouteResult<String> routed1 = router.route(GET, "/articles/new");
        Assert.assertEquals("new", routed1.target());
        Assert.assertEquals(0, routed1.pathParams().size());
        RouteResult<String> routed2 = router.route(GET, "/articles/123");
        Assert.assertEquals("show", routed2.target());
        Assert.assertEquals(1, routed2.pathParams().size());
        Assert.assertEquals("123", routed2.pathParams().get("id"));
        RouteResult<String> routed3 = router.route(GET, "/notfound");
        Assert.assertEquals("404", routed3.target());
        Assert.assertEquals(0, routed3.pathParams().size());
        RouteResult<String> routed4 = router.route(GET, "/articles/overview");
        Assert.assertEquals("overview", routed4.target());
        Assert.assertEquals(0, routed4.pathParams().size());
        RouteResult<String> routed5 = router.route(GET, "/articles/overview/detailed");
        Assert.assertEquals("detailed", routed5.target());
        Assert.assertEquals(0, routed5.pathParams().size());
    }

    @Test
    public void testAnyMethod() {
        RouteResult<String> routed1 = router.route(GET, "/anyMethod");
        Assert.assertEquals("anyMethod", routed1.target());
        Assert.assertEquals(0, routed1.pathParams().size());
        RouteResult<String> routed2 = router.route(POST, "/anyMethod");
        Assert.assertEquals("anyMethod", routed2.target());
        Assert.assertEquals(0, routed2.pathParams().size());
    }

    @Test
    public void testRemoveByPathPattern() {
        router.removePathPattern("/articles");
        RouteResult<String> routed = router.route(GET, "/articles");
        Assert.assertEquals("404", routed.target());
    }

    @Test
    public void testAllowedMethods() {
        Assert.assertEquals(9, router.allAllowedMethods().size());
        Set<HttpMethod> methods = router.allowedMethods("/articles");
        Assert.assertEquals(2, methods.size());
        Assert.assertTrue(methods.contains(GET));
        Assert.assertTrue(methods.contains(POST));
    }

    @Test
    public void testSubclasses() {
        Router<Class<? extends RouterTest.Action>> router = new Router<Class<? extends RouterTest.Action>>().addRoute(GET, "/articles", RouterTest.Index.class).addRoute(GET, "/articles/:id", RouterTest.Show.class);
        RouteResult<Class<? extends RouterTest.Action>> routed1 = router.route(GET, "/articles");
        RouteResult<Class<? extends RouterTest.Action>> routed2 = router.route(GET, "/articles/123");
        Assert.assertNotNull(routed1);
        Assert.assertNotNull(routed2);
        Assert.assertEquals(RouterTest.Index.class, routed1.target());
        Assert.assertEquals(RouterTest.Show.class, routed2.target());
    }

    private static final class StringRouter {
        // Utility classes should not have a public or default constructor.
        private StringRouter() {
        }

        static Router<String> create() {
            return new Router<String>().addGet("/articles", "index").addGet("/articles/new", "new").addGet("/articles/overview", "overview").addGet("/articles/overview/detailed", "detailed").addGet("/articles/:id", "show").addGet("/articles/:id/:format", "show").addPost("/articles", "post").addPatch("/articles/:id", "patch").addDelete("/articles/:id", "delete").addAny("/anyMethod", "anyMethod").addGet("/download/:*", "download").notFound("404");
        }
    }

    private interface Action {}

    private class Index implements RouterTest.Action {}

    private class Show implements RouterTest.Action {}
}

