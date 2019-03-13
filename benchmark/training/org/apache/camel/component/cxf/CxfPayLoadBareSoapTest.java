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


import java.util.concurrent.atomic.AtomicInteger;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;

import static javax.jws.soap.SOAPBinding.ParameterStyle.BARE;


public class CxfPayLoadBareSoapTest extends CamelTestSupport {
    private static final int PORT = AvailablePortFinder.getNextAvailable();

    private static final String ORIGINAL_URL = String.format("http://localhost:%s/original/Service", CxfPayLoadBareSoapTest.PORT);

    private static final String PROXY_URL = String.format("http://localhost:%s/proxy/Service", CxfPayLoadBareSoapTest.PORT);

    private static final CxfPayLoadBareSoapTest.BareSoapServiceImpl IMPLEMENTATION = new CxfPayLoadBareSoapTest.BareSoapServiceImpl();

    @Test
    public void testInvokeProxyService() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(CxfPayLoadBareSoapTest.BareSoapService.class);
        factory.setAddress(CxfPayLoadBareSoapTest.PROXY_URL);
        factory.setBus(BusFactory.newInstance().createBus());
        CxfPayLoadBareSoapTest.BareSoapService client = ((CxfPayLoadBareSoapTest.BareSoapService) (factory.create()));
        client.doSomething();
        assertEquals("Proxied service should have been invoked once", 1, CxfPayLoadBareSoapTest.IMPLEMENTATION.invocations.get());
    }

    @WebService
    @SOAPBinding(parameterStyle = BARE)
    public interface BareSoapService {
        void doSomething();
    }

    public static class BareSoapServiceImpl implements CxfPayLoadBareSoapTest.BareSoapService {
        private AtomicInteger invocations = new AtomicInteger(0);

        public void doSomething() {
            invocations.incrementAndGet();
        }
    }
}

