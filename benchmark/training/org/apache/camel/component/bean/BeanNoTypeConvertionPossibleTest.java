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
package org.apache.camel.component.bean;


import java.util.Date;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.NoTypeConversionAvailableException;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class BeanNoTypeConvertionPossibleTest extends ContextTestSupport {
    @Test
    public void testBeanNoTypeConvertionPossibleFail() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);
        // we send in a Date object which cannot be converted to XML so it should fail
        try {
            template.requestBody("direct:start", new Date());
            Assert.fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            NoTypeConversionAvailableException ntae = TestSupport.assertIsInstanceOf(NoTypeConversionAvailableException.class, e.getCause().getCause());
            Assert.assertEquals(Date.class, ntae.getFromType());
            Assert.assertEquals(Document.class, ntae.getToType());
            Assert.assertNotNull(ntae.getValue());
            Assert.assertNotNull(ntae.getMessage());
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanNoTypeConvertionPossibleOK() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("77889,667,457");
        template.requestBody("direct:start", "<foo>bar</foo>");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanNoTypeConvertionPossibleOKNullBody() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.message(0).body().isNull();
        String body = null;
        template.requestBody("direct:start", body);
        assertMockEndpointsSatisfied();
    }
}

