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


import Exchange.MAXIMUM_CACHE_POOL_SIZE;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;


public class DefaultProducerTemplateWithCustomCacheMaxSizeTest extends ContextTestSupport {
    @Test
    public void testCacheProducers() throws Exception {
        ProducerTemplate template = context.createProducerTemplate();
        Assert.assertEquals("Size should be 0", 0, template.getCurrentCacheSize());
        // test that we cache at most 500 producers to avoid it eating to much memory
        for (int i = 0; i < 203; i++) {
            Endpoint e = context.getEndpoint(("seda:queue:" + i));
            template.sendBody(e, "Hello");
        }
        // the eviction is async so force cleanup
        template.cleanUp();
        Assert.assertEquals("Size should be 200", 200, template.getCurrentCacheSize());
        template.stop();
        // should be 0
        Assert.assertEquals("Size should be 0", 0, template.getCurrentCacheSize());
    }

    @Test
    public void testInvalidSizeABC() {
        context.getGlobalOptions().put(MAXIMUM_CACHE_POOL_SIZE, "ABC");
        try {
            context.createProducerTemplate();
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            Assert.assertEquals("Property CamelMaximumCachePoolSize must be a positive number, was: ABC", e.getCause().getMessage());
        }
    }

    @Test
    public void testInvalidSizeZero() {
        context.getGlobalOptions().put(MAXIMUM_CACHE_POOL_SIZE, "0");
        try {
            context.createProducerTemplate();
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            Assert.assertEquals("Property CamelMaximumCachePoolSize must be a positive number, was: 0", e.getCause().getMessage());
        }
    }
}

