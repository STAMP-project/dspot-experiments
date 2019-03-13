/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.netty4.http;


import org.junit.Assert;
import org.junit.Test;


public class SecurityConstraintMappingTest extends Assert {
    @Test
    public void testDefault() {
        SecurityConstraintMapping matcher = new SecurityConstraintMapping();
        Assert.assertNotNull(matcher.restricted("/"));
        Assert.assertNotNull(matcher.restricted("/foo"));
    }

    @Test
    public void testFoo() {
        SecurityConstraintMapping matcher = new SecurityConstraintMapping();
        matcher.addInclusion("/foo");
        Assert.assertNull(matcher.restricted("/"));
        Assert.assertNotNull(matcher.restricted("/foo"));
        Assert.assertNull(matcher.restricted("/foobar"));
        Assert.assertNull(matcher.restricted("/foo/bar"));
    }

    @Test
    public void testFooWildcard() {
        SecurityConstraintMapping matcher = new SecurityConstraintMapping();
        matcher.addInclusion("/foo*");
        Assert.assertNull(matcher.restricted("/"));
        Assert.assertNotNull(matcher.restricted("/foo"));
        Assert.assertNotNull(matcher.restricted("/foobar"));
        Assert.assertNotNull(matcher.restricted("/foo/bar"));
    }

    @Test
    public void testFooBar() {
        SecurityConstraintMapping matcher = new SecurityConstraintMapping();
        matcher.addInclusion("/foo");
        matcher.addInclusion("/bar");
        Assert.assertNull(matcher.restricted("/"));
        Assert.assertNotNull(matcher.restricted("/foo"));
        Assert.assertNull(matcher.restricted("/foobar"));
        Assert.assertNull(matcher.restricted("/foo/bar"));
        Assert.assertNotNull(matcher.restricted("/bar"));
        Assert.assertNull(matcher.restricted("/barbar"));
        Assert.assertNull(matcher.restricted("/bar/bar"));
    }

    @Test
    public void testFooBarWildcard() {
        SecurityConstraintMapping matcher = new SecurityConstraintMapping();
        matcher.addInclusion("/foo*");
        matcher.addInclusion("/bar*");
        Assert.assertNull(matcher.restricted("/"));
        Assert.assertNotNull(matcher.restricted("/foo"));
        Assert.assertNotNull(matcher.restricted("/foobar"));
        Assert.assertNotNull(matcher.restricted("/foo/bar"));
        Assert.assertNotNull(matcher.restricted("/bar"));
        Assert.assertNotNull(matcher.restricted("/barbar"));
        Assert.assertNotNull(matcher.restricted("/bar/bar"));
    }

    @Test
    public void testFooExclusion() {
        SecurityConstraintMapping matcher = new SecurityConstraintMapping();
        matcher.addInclusion("/foo/*");
        matcher.addExclusion("/foo/public/*");
        Assert.assertNull(matcher.restricted("/"));
        Assert.assertNotNull(matcher.restricted("/foo"));
        Assert.assertNotNull(matcher.restricted("/foo/bar"));
        Assert.assertNull(matcher.restricted("/foo/public"));
        Assert.assertNull(matcher.restricted("/foo/public/open"));
    }

    @Test
    public void testDefaultExclusion() {
        // everything is restricted unless its from the public
        SecurityConstraintMapping matcher = new SecurityConstraintMapping();
        matcher.addExclusion("/public/*");
        matcher.addExclusion("/index");
        matcher.addExclusion("/index.html");
        Assert.assertNotNull(matcher.restricted("/"));
        Assert.assertNotNull(matcher.restricted("/foo"));
        Assert.assertNotNull(matcher.restricted("/foo/bar"));
        Assert.assertNull(matcher.restricted("/public"));
        Assert.assertNull(matcher.restricted("/public/open"));
        Assert.assertNull(matcher.restricted("/index"));
        Assert.assertNull(matcher.restricted("/index.html"));
    }
}

