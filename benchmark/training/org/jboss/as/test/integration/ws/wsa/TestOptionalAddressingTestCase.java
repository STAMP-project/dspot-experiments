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
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
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
public class TestOptionalAddressingTestCase {
    private static Logger log = Logger.getLogger(TestOptionalAddressingTestCase.class.getName());

    @ArquillianResource
    URL baseUrl;

    private static final String message = "optional-addressing";

    private static final String expectedResponse = "Hello optional-addressing!";

    @Test
    public void usingLocalWSDLWithoutAddressing() throws Exception {
        ServiceIface proxy = getServicePortFromWSDL("NoAddressingService.xml");
        Assert.assertEquals(TestOptionalAddressingTestCase.expectedResponse, proxy.sayHello(TestOptionalAddressingTestCase.message));
    }

    @Test
    public void usingLocalWSDLWithOptionalAddressing() throws Exception {
        ServiceIface proxy = getServicePortFromWSDL("OptionalAddressingService.xml");
        Assert.assertEquals(TestOptionalAddressingTestCase.expectedResponse, proxy.sayHello(TestOptionalAddressingTestCase.message));
    }

    @Test
    public void usingLocalWSDLWithAddressing() throws Exception {
        ServiceIface proxy = getServicePortFromWSDL("RequiredAddressingService.xml");
        Assert.assertEquals(TestOptionalAddressingTestCase.expectedResponse, proxy.sayHello(TestOptionalAddressingTestCase.message));
    }

    @Test
    public void usingWSDLFromDeployedEndpoint() throws Exception {
        QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wsaddressing", "AddressingService");
        URL wsdlURL = new URL(baseUrl, "/jaxws-wsa/AddressingService?wsdl");
        File wsdlFile = new File(((this.getClass().getSimpleName()) + ".wsdl"));
        TestNoAddressingTestCase.downloadWSDLToFile(wsdlURL, wsdlFile);
        Service service = Service.create(wsdlFile.toURI().toURL(), serviceName);
        ServiceIface proxy = ((ServiceIface) (service.getPort(ServiceIface.class)));
        Assert.assertEquals(TestOptionalAddressingTestCase.expectedResponse, proxy.sayHello(TestOptionalAddressingTestCase.message));
        wsdlFile.delete();
    }
}

