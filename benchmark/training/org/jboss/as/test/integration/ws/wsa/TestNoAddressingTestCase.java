/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.ws.wsa;


import java.io.File;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="mailto:rsvoboda@redhat.com">Rostislav Svoboda</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class TestNoAddressingTestCase {
    @ArquillianResource
    URL baseUrl;

    private static final String message = "no-addressing";

    private static final String expectedResponse = "Hello no-addressing!";

    @Test
    public void usingLocalWSDLWithoutAddressing() throws Exception {
        ServiceIface proxy = getServicePortFromWSDL("NoAddressingService.xml");
        Assert.assertEquals(TestNoAddressingTestCase.expectedResponse, proxy.sayHello(TestNoAddressingTestCase.message));
    }

    @Test
    public void usingLocalWSDLWithOptionalAddressing() throws Exception {
        ServiceIface proxy = getServicePortFromWSDL("OptionalAddressingService.xml");
        Assert.assertEquals(TestNoAddressingTestCase.expectedResponse, proxy.sayHello(TestNoAddressingTestCase.message));
    }

    @Test
    public void usingLocalWSDLWithAddressing() throws Exception {
        ServiceIface proxy = getServicePortFromWSDL("RequiredAddressingService.xml");
        try {
            proxy.sayHello(TestNoAddressingTestCase.message);
            Assert.fail("Message Addressing is defined in local wsdl but shouldn't be used on deployed endpoint");
        } catch (WebServiceException e) {
            // Expected, Message Addressing Property is not present
        }
    }

    @Test
    public void usingWSDLFromDeployedEndpoint() throws Exception {
        QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wsaddressing", "AddressingService");
        URL wsdlURL = new URL(baseUrl, "/jaxws-wsa/AddressingService?wsdl");
        File wsdlFile = new File(((this.getClass().getSimpleName()) + ".wsdl"));
        TestNoAddressingTestCase.downloadWSDLToFile(wsdlURL, wsdlFile);
        Service service = Service.create(wsdlFile.toURI().toURL(), serviceName);
        ServiceIface proxy = ((ServiceIface) (service.getPort(ServiceIface.class)));
        Assert.assertEquals(TestNoAddressingTestCase.expectedResponse, proxy.sayHello(TestNoAddressingTestCase.message));
        wsdlFile.delete();
    }
}

