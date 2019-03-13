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
package org.apache.camel.component.cxf.multipart;


import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;


public class CXFMultiPartTest extends CamelTestSupport {
    public static final QName SERVICE_NAME = new QName("http://camel.apache.org/cxf/multipart", "MultiPartInvokeService");

    public static final QName ROUTE_PORT_NAME = new QName("http://camel.apache.org/cxf/multipart", "MultiPartInvokePort");

    protected static Endpoint endpoint;

    protected AbstractXmlApplicationContext applicationContext;

    @Test
    public void testInvokingServiceFromCXFClient() throws Exception {
        String reply = invokeMultiPartService((("http://localhost:" + (CXFTestSupport.getPort3())) + "/CXFMultiPartTest/CamelContext/RouterPort"), "in0", "in1");
        assertNotNull("No response received from service", reply);
        assertTrue(reply.equals("in0 in1"));
        assertNotNull("No response received from service", reply);
        assertTrue(reply.equals("in0 in1"));
    }
}

