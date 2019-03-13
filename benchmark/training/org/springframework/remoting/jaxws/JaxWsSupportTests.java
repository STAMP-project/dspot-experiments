/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.remoting.jaxws;


import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.soap.AddressingFeature;
import org.junit.Test;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public class JaxWsSupportTests {
    @Test
    public void testJaxWsPortAccess() throws Exception {
        doTestJaxWsPortAccess(((WebServiceFeature[]) (null)));
    }

    @Test
    public void testJaxWsPortAccessWithFeature() throws Exception {
        doTestJaxWsPortAccess(new AddressingFeature());
    }

    public static class ServiceAccessor {
        @WebServiceRef
        public OrderService orderService;

        public OrderService myService;

        @WebServiceRef(value = JaxWsSupportTests.OrderServiceService.class, wsdlLocation = "http://localhost:9999/OrderService?wsdl")
        public void setMyService(OrderService myService) {
            this.myService = myService;
        }
    }

    @WebServiceClient(targetNamespace = "http://jaxws.remoting.springframework.org/", name = "OrderService")
    public static class OrderServiceService extends Service {
        public OrderServiceService() throws MalformedURLException {
            super(new URL("http://localhost:9999/OrderService?wsdl"), new QName("http://jaxws.remoting.springframework.org/", "OrderService"));
        }

        public OrderServiceService(URL wsdlDocumentLocation, QName serviceName) {
            super(wsdlDocumentLocation, serviceName);
        }
    }
}

