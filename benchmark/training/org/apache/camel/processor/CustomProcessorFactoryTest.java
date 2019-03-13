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


import org.apache.camel.ContextTestSupport;
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.apache.camel.model.SetBodyDefinition;
import org.apache.camel.model.SplitDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.spi.ProcessorFactory;
import org.apache.camel.spi.RouteContext;
import org.junit.Test;


// END SNIPPET: e3
public class CustomProcessorFactoryTest extends ContextTestSupport {
    // END SNIPPET: e1
    // START SNIPPET: e2
    @Test
    public void testAlterDefinitionUsingProcessorFactory() throws Exception {
        getMockEndpoint("mock:foo").expectedBodiesReceived("body was altered");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testAlterDefinitionUsingProcessorFactoryWithChild() throws Exception {
        getMockEndpoint("mock:split").expectedBodiesReceived("body was altered", "body was altered");
        getMockEndpoint("mock:extra").expectedBodiesReceived("body was altered", "body was altered");
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello,World");
        template.sendBody("direct:foo", "Hello,World");
        assertMockEndpointsSatisfied();
    }

    // END SNIPPET: e2
    // START SNIPPET: e3
    public static class MyFactory implements ProcessorFactory {
        public Processor createChildProcessor(RouteContext routeContext, NamedNode definition, boolean mandatory) throws Exception {
            return null;
        }

        public Processor createProcessor(RouteContext routeContext, NamedNode definition) throws Exception {
            if (definition instanceof SplitDefinition) {
                // add additional output to the splitter
                SplitDefinition split = ((SplitDefinition) (definition));
                split.addOutput(new ToDefinition("mock:extra"));
            }
            if (definition instanceof SetBodyDefinition) {
                SetBodyDefinition set = ((SetBodyDefinition) (definition));
                set.setExpression(new ConstantExpression("body was altered"));
            }
            // return null to let the default implementation create the processor, we just wanted to alter the definition
            // before the processor was created
            return null;
        }
    }
}

