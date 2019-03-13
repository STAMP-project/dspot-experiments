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
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class InflightRepositoryRouteTest extends ContextTestSupport {
    @Test
    public void testInflight() throws Exception {
        context.setInflightRepository(new InflightRepositoryRouteTest.MyInflightRepo());
        Assert.assertEquals(0, context.getInflightRepository().size());
        template.sendBody("direct:start", "Hello World");
        Assert.assertEquals(0, context.getInflightRepository().size());
        Assert.assertEquals(0, context.getInflightRepository().size("foo"));
    }

    private class MyInflightRepo extends DefaultInflightRepository {
        @Override
        public void add(Exchange exchange) {
            super.add(exchange);
            Assert.assertEquals(1, context.getInflightRepository().size());
        }

        @Override
        public void add(Exchange exchange, String routeId) {
            super.add(exchange, routeId);
            Assert.assertEquals(1, context.getInflightRepository().size("foo"));
        }
    }
}

