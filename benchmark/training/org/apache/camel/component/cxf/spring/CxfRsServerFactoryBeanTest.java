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
package org.apache.camel.component.cxf.spring;


import java.util.List;
import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.component.cxf.jaxrs.testbean.CustomerService;
import org.junit.Assert;
import org.junit.Test;


public class CxfRsServerFactoryBeanTest extends AbstractSpringBeanTestSupport {
    static int port = CXFTestSupport.getPort1();

    @Test
    public void testCxfRsServerFactoryBean() {
        SpringJAXRSServerFactoryBean sfb1 = ctx.getBean("rsServer1", SpringJAXRSServerFactoryBean.class);
        Assert.assertEquals("Get a wrong address", sfb1.getAddress(), (("http://localhost:" + (CxfRsServerFactoryBeanTest.port)) + "/CxfRsServerFactoryBeanTest/server1"));
        List<Class<?>> resource1Classes = sfb1.getResourceClasses();
        Assert.assertEquals("Get a wrong size of resouceClasses", resource1Classes.size(), 1);
        Assert.assertEquals("Get a wrong resource class", resource1Classes.get(0), CustomerService.class);
        SpringJAXRSServerFactoryBean sfb2 = ctx.getBean("rsServer2", SpringJAXRSServerFactoryBean.class);
        Assert.assertEquals("Get a wrong address", sfb2.getAddress(), (("http://localhost:" + (CxfRsServerFactoryBeanTest.port)) + "/CxfRsServerFactoryBeanTest/server2"));
        sfb2.getResourceClasses();
        List<Class<?>> resource2Classes = sfb2.getResourceClasses();
        Assert.assertEquals("Get a wrong size of resouceClasses", resource2Classes.size(), 1);
        Assert.assertEquals("Get a wrong resource class", resource2Classes.get(0), CustomerService.class);
        Assert.assertEquals("Got the wrong schemalocations size", 1, sfb2.getSchemaLocations().size());
        Assert.assertEquals("Got the wrong schemalocation", "classpath:wsdl/Message.xsd", sfb2.getSchemaLocations().get(0));
    }
}

