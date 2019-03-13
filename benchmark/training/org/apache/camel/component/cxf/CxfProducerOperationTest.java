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
package org.apache.camel.component.cxf;


import CxfConstants.OPERATION_NAME;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Assert;
import org.junit.Test;


public class CxfProducerOperationTest extends CxfProducerTest {
    private static final String NAMESPACE = "http://apache.org/hello_world_soap_http";

    @Test
    public void testSendingComplexParameter() throws Exception {
        Exchange exchange = template.send(getSimpleEndpointUri(), new Processor() {
            public void process(final Exchange exchange) {
                // we need to override the operation name first
                final List<String> para1 = new ArrayList<>();
                para1.add("para1");
                final List<String> para2 = new ArrayList<>();
                para2.add("para2");
                List<List<String>> parameters = new ArrayList<>();
                parameters.add(para1);
                parameters.add(para2);
                // The object array version is working too
                // Object[] parameters = new Object[] {para1, para2};
                exchange.getIn().setBody(parameters);
                exchange.getIn().setHeader(OPERATION_NAME, "complexParameters");
            }
        });
        if ((exchange.getException()) != null) {
            throw exchange.getException();
        }
        Assert.assertEquals("Get a wrong response.", "param:para1para2", exchange.getOut().getBody(String.class));
    }
}

