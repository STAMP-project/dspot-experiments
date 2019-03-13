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


import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Navigate;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class SplitWithEndTest extends ContextTestSupport {
    @Test
    public void testRouteIsCorrectAtRuntime() throws Exception {
        // use navigate to find that the end works as expected
        Navigate<Processor> nav = context.getRoutes().get(0).navigate();
        List<Processor> node = nav.next();
        // there should be 4 outputs as the end in the otherwise should
        // ensure that the transform and last send is not within the choice
        Assert.assertEquals(4, node.size());
        // the navigate API is a bit simple at this time of writing so it does take a little
        // bit of ugly code to find the correct processor in the runtime route
        TestSupport.assertIsInstanceOf(SendProcessor.class, TestSupport.unwrapChannel(node.get(0)).getNextProcessor());
        TestSupport.assertIsInstanceOf(Splitter.class, TestSupport.unwrapChannel(node.get(1)).getNextProcessor());
        TestSupport.assertIsInstanceOf(TransformProcessor.class, TestSupport.unwrapChannel(node.get(2)).getNextProcessor());
        TestSupport.assertIsInstanceOf(SendProcessor.class, TestSupport.unwrapChannel(node.get(3)).getNextProcessor());
    }

    @Test
    public void testSplit() throws Exception {
        getMockEndpoint("mock:start").expectedBodiesReceived("Hello,World,Moon");
        getMockEndpoint("mock:last").expectedBodiesReceived("last hi Hello@hi World@hi Moon");
        template.sendBody("direct:start", "Hello,World,Moon");
        assertMockEndpointsSatisfied();
    }

    public class MySplitBean {
        public String hi(String s) {
            return "hi " + s;
        }
    }
}

