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
package org.apache.camel.impl;


import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class RouteIdFactoryTest extends ContextTestSupport {
    @Test
    public void testDirectRouteIdWithOptions() {
        Assert.assertEquals("start1", context.getRouteDefinitions().get(0).getId());
    }

    @Test
    public void testDirectRouteId() {
        Assert.assertEquals("start2", context.getRouteDefinitions().get(1).getId());
    }

    @Test
    public void testRestRouteIdWithVerbUri() {
        Assert.assertEquals("get-say-hello-bar", context.getRouteDefinitions().get(2).getId());
    }

    @Test
    public void testRestRouteIdWithoutVerbUri() {
        Assert.assertEquals("get-say-hello", context.getRouteDefinitions().get(3).getId());
    }

    @Test
    public void testRestRouteIdWithoutPathUri() {
        Assert.assertEquals("get-hello", context.getRouteDefinitions().get(4).getId());
    }
}

