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
package org.apache.hadoop.yarn.webapp;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TestParseRoute {
    @Test
    public void testNormalAction() {
        Assert.assertEquals(Arrays.asList("/foo/action", "foo", "action", ":a1", ":a2"), WebApp.parseRoute("/foo/action/:a1/:a2"));
    }

    @Test
    public void testDefaultController() {
        Assert.assertEquals(Arrays.asList("/", "default", "index"), WebApp.parseRoute("/"));
    }

    @Test
    public void testDefaultAction() {
        Assert.assertEquals(Arrays.asList("/foo", "foo", "index"), WebApp.parseRoute("/foo"));
        Assert.assertEquals(Arrays.asList("/foo", "foo", "index"), WebApp.parseRoute("/foo/"));
    }

    @Test
    public void testMissingAction() {
        Assert.assertEquals(Arrays.asList("/foo", "foo", "index", ":a1"), WebApp.parseRoute("/foo/:a1"));
    }

    @Test
    public void testDefaultCapture() {
        Assert.assertEquals(Arrays.asList("/", "default", "index", ":a"), WebApp.parseRoute("/:a"));
    }

    @Test
    public void testPartialCapture1() {
        Assert.assertEquals(Arrays.asList("/foo/action/bar", "foo", "action", "bar", ":a"), WebApp.parseRoute("/foo/action/bar/:a"));
    }

    @Test
    public void testPartialCapture2() {
        Assert.assertEquals(Arrays.asList("/foo/action", "foo", "action", ":a1", "bar", ":a2", ":a3"), WebApp.parseRoute("/foo/action/:a1/bar/:a2/:a3"));
    }

    @Test
    public void testLeadingPaddings() {
        Assert.assertEquals(Arrays.asList("/foo/action", "foo", "action", ":a"), WebApp.parseRoute(" /foo/action/ :a"));
    }

    @Test
    public void testTrailingPaddings() {
        Assert.assertEquals(Arrays.asList("/foo/action", "foo", "action", ":a"), WebApp.parseRoute("/foo/action//:a / "));
        Assert.assertEquals(Arrays.asList("/foo/action", "foo", "action"), WebApp.parseRoute("/foo/action / "));
    }

    @Test(expected = WebAppException.class)
    public void testMissingLeadingSlash() {
        WebApp.parseRoute("foo/bar");
    }
}

