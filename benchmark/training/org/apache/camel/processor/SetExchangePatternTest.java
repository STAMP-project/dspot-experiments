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


import ExchangePattern.InOnly;
import ExchangePattern.InOut;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Assert;
import org.junit.Test;


public class SetExchangePatternTest extends ContextTestSupport {
    @Test
    public void testInOut() throws Exception {
        assertMessageReceivedWithPattern("direct:testInOut", InOut);
    }

    @Test
    public void testInOnly() throws Exception {
        assertMessageReceivedWithPattern("direct:testInOnly", InOnly);
    }

    @Test
    public void testSetToInOnlyThenTo() throws Exception {
        assertMessageReceivedWithPattern("direct:testSetToInOnlyThenTo", InOnly);
    }

    @Test
    public void testSetToInOutThenTo() throws Exception {
        assertMessageReceivedWithPattern("direct:testSetToInOutThenTo", InOut);
    }

    @Test
    public void testToWithInOnlyParam() throws Exception {
        assertMessageReceivedWithPattern("direct:testToWithInOnlyParam", InOnly);
    }

    @Test
    public void testToWithInOutParam() throws Exception {
        assertMessageReceivedWithPattern("direct:testToWithInOutParam", InOut);
    }

    @Test
    public void testSetExchangePatternInOnly() throws Exception {
        assertMessageReceivedWithPattern("direct:testSetExchangePatternInOnly", InOnly);
    }

    @Test
    public void testPreserveOldMEPInOut() throws Exception {
        // the mock should get an InOut MEP
        getMockEndpoint("mock:result").expectedMessageCount(1);
        getMockEndpoint("mock:result").message(0).exchangePattern().isEqualTo(InOut);
        // we send an InOnly
        Exchange out = template.send("direct:testInOut", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Hello World");
                exchange.setPattern(InOnly);
            }
        });
        // the MEP should be preserved
        Assert.assertNotNull(out);
        Assert.assertEquals(InOnly, out.getPattern());
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testPreserveOldMEPInOnly() throws Exception {
        // the mock should get an InOnly MEP
        getMockEndpoint("mock:result").expectedMessageCount(1);
        getMockEndpoint("mock:result").message(0).exchangePattern().isEqualTo(InOnly);
        // we send an InOut
        Exchange out = template.send("direct:testInOnly", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Hello World");
                exchange.setPattern(InOut);
            }
        });
        // the MEP should be preserved
        Assert.assertNotNull(out);
        Assert.assertEquals(InOut, out.getPattern());
        assertMockEndpointsSatisfied();
    }
}

