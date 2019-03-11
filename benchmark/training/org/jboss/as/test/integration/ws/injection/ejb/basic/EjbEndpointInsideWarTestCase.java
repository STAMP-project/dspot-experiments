/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ws.injection.ejb.basic;


import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.ws.injection.ejb.basic.webservice.EndpointIface;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * [AS7-3411] Test injection + handlers with EJB3 endpoint packaged inside a WAR.
 *
 * @author Robert Reimann
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class EjbEndpointInsideWarTestCase {
    private static final Logger log = Logger.getLogger(EjbEndpointInsideWarTestCase.class);

    @ArquillianResource
    URL baseUrl;

    @Test
    public void testEjb3InsideWarEndpoint() throws Exception {
        QName serviceName = new QName("http://jbossws.org/injection", "EJB3Service");
        URL wsdlURL = new URL(baseUrl, "/jaxws-injection-ejb3/EJB3Service?wsdl");
        Service service = Service.create(wsdlURL, serviceName);
        EndpointIface proxy = service.getPort(EndpointIface.class);
        Assert.assertEquals("Hello World!:Inbound:TestHandler:EJB3Bean:Outbound:TestHandler", proxy.echo("Hello World!"));
    }
}

