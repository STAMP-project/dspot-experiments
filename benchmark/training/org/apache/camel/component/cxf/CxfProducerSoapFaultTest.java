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


import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;


public class CxfProducerSoapFaultTest extends Assert {
    private static final String JAXWS_SERVER_ADDRESS = ("http://localhost:" + (CXFTestSupport.getPort1())) + "/CxfProducerSoapFaultTest/test";

    private static final String JAXWS_ENDPOINT_URI = ("cxf://" + (CxfProducerSoapFaultTest.JAXWS_SERVER_ADDRESS)) + "?serviceClass=org.apache.hello_world_soap_http.Greeter";

    protected CamelContext camelContext;

    protected ProducerTemplate template;

    @Test
    public void testAsyncSoapFault() throws Exception {
        invokeSoapFault(false);
    }

    @Test
    public void testSyncSoapFault() throws Exception {
        invokeSoapFault(true);
    }
}

