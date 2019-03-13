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
package org.apache.camel.processor.intercept;


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spi.InterceptStrategy;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ParentChildInterceptStrategyTest extends ContextTestSupport {
    protected static final List<String> LIST = new ArrayList<>();

    @Test
    public void testParentChild() throws Exception {
        getMockEndpoint("mock:done").expectedMessageCount(1);
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(1);
        getMockEndpoint("mock:c").expectedMessageCount(1);
        getMockEndpoint("mock:d").expectedMessageCount(0);
        getMockEndpoint("mock:e").expectedMessageCount(0);
        template.sendBody("direct:start", "Hello Camel");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(7, ParentChildInterceptStrategyTest.LIST.size());
        Assert.assertEquals("Parent route -> target task-a", ParentChildInterceptStrategyTest.LIST.get(0));
        Assert.assertEquals("Parent when -> target task-b", ParentChildInterceptStrategyTest.LIST.get(1));
        Assert.assertEquals("Parent when -> target task-c", ParentChildInterceptStrategyTest.LIST.get(2));
        Assert.assertEquals("Parent when2 -> target task-d", ParentChildInterceptStrategyTest.LIST.get(3));
        Assert.assertEquals("Parent otherwise -> target task-e", ParentChildInterceptStrategyTest.LIST.get(4));
        Assert.assertEquals("Parent route -> target choice", ParentChildInterceptStrategyTest.LIST.get(5));
        // the last one has no custom id so its using its label instead
        Assert.assertEquals("Parent route -> target mock:done", ParentChildInterceptStrategyTest.LIST.get(6));
    }

    public static final class MyParentChildInterceptStrategy implements InterceptStrategy {
        @Override
        public Processor wrapProcessorInInterceptors(final CamelContext context, final NamedNode node, final Processor target, final Processor nextTarget) throws Exception {
            ProcessorDefinition<?> definition = ((ProcessorDefinition<?>) (node));
            String targetId = (definition.hasCustomIdAssigned()) ? definition.getId() : definition.getLabel();
            ProcessorDefinition<?> parent = definition.getParent();
            String parentId = "";
            if (parent != null) {
                parentId = (parent.hasCustomIdAssigned()) ? parent.getId() : parent.getLabel();
            }
            ParentChildInterceptStrategyTest.LIST.add(((("Parent " + parentId) + " -> target ") + targetId));
            return target;
        }
    }
}

