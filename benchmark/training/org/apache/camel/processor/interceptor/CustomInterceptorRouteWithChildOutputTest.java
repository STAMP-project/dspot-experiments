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
package org.apache.camel.processor.interceptor;


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.apache.camel.model.LogDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.SplitDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.spi.InterceptStrategy;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class CustomInterceptorRouteWithChildOutputTest extends ContextTestSupport {
    private CustomInterceptorRouteWithChildOutputTest.MyInterceptor myInterceptor = new CustomInterceptorRouteWithChildOutputTest.MyInterceptor();

    @Test
    public void testCustomInterceptor() throws Exception {
        getMockEndpoint("mock:child").expectedMessageCount(3);
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:start", "A,B,C");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(4, myInterceptor.getDefs().size());
        TestSupport.assertIsInstanceOf(LogDefinition.class, myInterceptor.getDefs().get(0));
        TestSupport.assertIsInstanceOf(ToDefinition.class, myInterceptor.getDefs().get(1));
        Assert.assertEquals("mock:child", myInterceptor.getDefs().get(1).getLabel());
        TestSupport.assertIsInstanceOf(SplitDefinition.class, myInterceptor.getDefs().get(2));
        TestSupport.assertIsInstanceOf(ToDefinition.class, myInterceptor.getDefs().get(3));
        Assert.assertEquals("mock:result", myInterceptor.getDefs().get(3).getLabel());
    }

    @SuppressWarnings("rawtypes")
    private static class MyInterceptor implements InterceptStrategy {
        private final List<ProcessorDefinition> defs = new ArrayList<>();

        @Override
        public Processor wrapProcessorInInterceptors(CamelContext context, NamedNode definition, Processor target, Processor nextTarget) throws Exception {
            defs.add(((ProcessorDefinition<?>) (definition)));
            return target;
        }

        public List<ProcessorDefinition> getDefs() {
            return defs;
        }
    }
}

