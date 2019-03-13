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
package org.apache.camel.processor;


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class SplitterOnPrepareTest extends ContextTestSupport {
    @Test
    public void testSplitterOnPrepare() throws Exception {
        getMockEndpoint("mock:a").expectedMessageCount(2);
        getMockEndpoint("mock:a").allMessages().body(String.class).isEqualTo("1 Tony the Tiger");
        List<Animal> animals = new ArrayList<>();
        animals.add(new Animal(1, "Tiger"));
        animals.add(new Animal(1, "Tiger"));
        template.sendBody("direct:start", animals);
        assertMockEndpointsSatisfied();
    }

    public static class ProcessorA implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            Animal body = exchange.getIn().getBody(Animal.class);
            Assert.assertEquals(1, body.getId());
            Assert.assertEquals("Tony the Tiger", body.getName());
        }
    }

    public static final class FixNamePrepare implements Processor {
        public void process(Exchange exchange) throws Exception {
            Animal body = exchange.getIn().getBody(Animal.class);
            Assert.assertEquals(1, body.getId());
            Assert.assertEquals("Tiger", body.getName());
            body.setName("Tony the Tiger");
        }
    }
}

