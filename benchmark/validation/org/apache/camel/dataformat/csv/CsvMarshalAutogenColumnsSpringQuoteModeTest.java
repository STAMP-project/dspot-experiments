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
package org.apache.camel.dataformat.csv;


import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;


/**
 * Spring based integration test for the <code>CsvDataFormat</code> demonstrating the usage of
 * the <tt>autogenColumns</tt>, <tt>configRef</tt> and <tt>strategyRef</tt> options.
 */
public class CsvMarshalAutogenColumnsSpringQuoteModeTest extends CamelSpringTestSupport {
    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;

    @EndpointInject(uri = "mock:result2")
    private MockEndpoint result2;

    @Test
    public void retrieveColumnsWithAutogenColumnsFalseAndItemColumnsSet() throws Exception {
        result.expectedMessageCount(1);
        template.sendBody("direct:start", CsvMarshalAutogenColumnsSpringQuoteModeTest.createBody());
        result.assertIsSatisfied();
        String body = result.getReceivedExchanges().get(0).getIn().getBody(String.class);
        String[] lines = body.split(LS);
        assertEquals(2, lines.length);
        assertEquals("\"Camel in Action\"", lines[0].trim());
        assertEquals("\"ActiveMQ in Action\"", lines[1].trim());
    }

    @Test
    public void retrieveColumnsWithAutogenColumnsFalseAndOrderIdAmountColumnsSet() throws Exception {
        result2.expectedMessageCount(1);
        template.sendBody("direct:start2", CsvMarshalAutogenColumnsSpringQuoteModeTest.createBody());
        result2.assertIsSatisfied();
        String body = result2.getReceivedExchanges().get(0).getIn().getBody(String.class);
        String[] lines = body.split(LS);
        assertEquals(2, lines.length);
        assertEquals("123|1", lines[0].trim());
        assertEquals("124|2", lines[1].trim());
    }
}

