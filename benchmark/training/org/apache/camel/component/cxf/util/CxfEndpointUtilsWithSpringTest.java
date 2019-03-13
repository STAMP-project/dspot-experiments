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
package org.apache.camel.component.cxf.util;


import DataFormat.PAYLOAD;
import DataFormat.RAW;
import javax.xml.namespace.QName;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.CxfEndpointUtils;
import org.apache.camel.component.cxf.CxfSpringEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;


public class CxfEndpointUtilsWithSpringTest extends CxfEndpointUtilsTest {
    protected AbstractXmlApplicationContext applicationContext;

    @Test
    public void testGetServiceClass() throws Exception {
        CxfEndpoint endpoint = createEndpoint("cxf:bean:helloServiceEndpoint?serviceClass=#helloServiceImpl");
        Assert.assertEquals("org.apache.camel.component.cxf.HelloServiceImpl", endpoint.getServiceClass().getName());
    }

    @Test
    public void testGetProperties() throws Exception {
        CxfSpringEndpoint endpoint = ((CxfSpringEndpoint) (createEndpoint(getEndpointURI())));
        QName service = endpoint.getServiceName();
        Assert.assertEquals("We should get the right service name", CxfEndpointUtilsTest.SERVICE_NAME, service);
        Assert.assertEquals("The cxf endpoint's DataFromat should be RAW", RAW, endpoint.getDataFormat().dealias());
        endpoint = ((CxfSpringEndpoint) (createEndpoint("cxf:bean:testPropertiesEndpoint")));
        service = CxfEndpointUtils.getServiceName(endpoint);
        Assert.assertEquals("We should get the right service name", CxfEndpointUtilsTest.SERVICE_NAME, service);
        QName port = CxfEndpointUtils.getPortName(endpoint);
        Assert.assertEquals("We should get the right endpoint name", CxfEndpointUtilsTest.PORT_NAME, port);
    }

    @Test
    public void testGetDataFormatFromCxfEndpontProperties() throws Exception {
        CxfEndpoint endpoint = createEndpoint(((getEndpointURI()) + "?dataFormat=PAYLOAD"));
        Assert.assertEquals("We should get the PAYLOAD DataFormat", PAYLOAD, endpoint.getDataFormat());
    }
}

