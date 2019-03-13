/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.weld.ejb;


import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.ejb.ConcurrentAccessException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test two things:
 * 1. EJBTHREE-697: First concurrent call doesn't throw exception
 * 2. make sure the SFSB is instantiated in the same thread as the Servlet, so propagation works
 * <p/>
 * Make sure a concurrent call to a SFSB proxy over Weld gets a ConcurrentAccessException.
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SessionObjectReferenceTestCase {
    @ArquillianResource
    private URL url;

    @Test
    public void testEcho() throws Exception {
        String result = performCall("simple", "Hello+world");
        XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8)));
        List<String> results = ((List<String>) (decoder.readObject()));
        List<Exception> exceptions = ((List<Exception>) (decoder.readObject()));
        String sharedContext = ((String) (decoder.readObject()));
        decoder.close();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("Echo Hello world", results.get(0));
        Assert.assertEquals(1, exceptions.size());
        Assert.assertTrue(((exceptions.get(0)) instanceof ConcurrentAccessException));
        Assert.assertEquals("Shared context", sharedContext);
    }

    private static final String WEB_XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + ((((((((((((("<!--org.jboss.as.weld.deployment.processors.WebIntegrationProcessor checks for the existence of WebMetaData -->\n" + "<web-app version=\"3.0\" xmlns=\"http://java.sun.com/xml/ns/j2ee\"\n") + "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "         xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_3_0.xsd\">\n") + "    <!-- if I have a web.xml, annotations won\'t work anymore -->\n") + "    <servlet>\n") + "        <servlet-name>SimpleServlet</servlet-name>\n") + "        <servlet-class>org.jboss.as.test.integration.weld.ejb.SimpleServlet</servlet-class>\n") + "    </servlet>\n") + "    <servlet-mapping>\n") + "        <servlet-name>SimpleServlet</servlet-name>\n") + "        <url-pattern>/simple</url-pattern>\n") + "    </servlet-mapping>\n") + "</web-app>");
}

