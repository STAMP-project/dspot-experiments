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
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class HandleFaultPerRouteTest extends ContextTestSupport {
    @Test
    public void testHandleFaultPerRoute() throws Exception {
        MockEndpoint a = getMockEndpoint("mock:a");
        a.expectedMessageCount(1);
        MockEndpoint b = getMockEndpoint("mock:b");
        b.expectedMessageCount(1);
        MockEndpoint c = getMockEndpoint("mock:c");
        c.expectedMessageCount(1);
        Exchange outA = template.send("direct:a", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getOut().setFault(true);
                exchange.getOut().setBody(new IllegalArgumentException("A"));
            }
        });
        Assert.assertTrue("Should be failed", outA.isFailed());
        TestSupport.assertIsInstanceOf(IllegalArgumentException.class, outA.getException());
        Assert.assertEquals("A", outA.getException().getMessage());
        Exchange outB = template.send("direct:b", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getOut().setFault(true);
                exchange.getOut().setBody(new IllegalArgumentException("B"));
            }
        });
        Assert.assertTrue("Should be failed", outB.isFailed());
        Assert.assertNull("Should not handle fault", outB.getException());
        Assert.assertTrue((((outB.getOut()) != null) && (outB.getOut().isFault())));
        TestSupport.assertIsInstanceOf(IllegalArgumentException.class, outB.getOut().getBody());
        Exchange outC = template.send("direct:c", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getOut().setFault(true);
                exchange.getOut().setBody(new IllegalArgumentException("C"));
            }
        });
        Assert.assertTrue("Should be failed", outC.isFailed());
        TestSupport.assertIsInstanceOf(IllegalArgumentException.class, outC.getException());
        Assert.assertEquals("C", outC.getException().getMessage());
    }
}

