/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.tests.api;


import MediaType.TEXT_XML_TYPE;
import Status.OK;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;


/**
 * Test of {@link javax.xml.transform.Source Source} MessageBody Provider
 *
 * @author Miroslav Fuksa
 */
public class SourceEntityProviderTest extends JerseyTest {
    private static final String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"";

    private static final String xdkPrefix = "<?xml version = '1.0' encoding = 'UTF-8'?>";

    private static final String entity = (SourceEntityProviderTest.prefix) + "?><test><aaa/></test>";

    @Test
    public void sourceProviderTest() throws IOException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
        Source source = new StreamSource(new ByteArrayInputStream(SourceEntityProviderTest.entity.getBytes()));
        Response response = target().path("test").path("source").request().put(Entity.entity(source, TEXT_XML_TYPE));
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertTrue(response.readEntity(String.class).startsWith(StreamSource.class.toString()));
    }

    @Test
    public void streamProviderTest() throws IOException {
        StreamSource source = new StreamSource(new ByteArrayInputStream(SourceEntityProviderTest.entity.getBytes()));
        Response response = target().path("test").path("stream").request().put(Entity.entity(source, TEXT_XML_TYPE));
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertTrue(response.readEntity(String.class).startsWith(StreamSource.class.toString()));
    }

    @Test
    public void saxProviderTest() throws IOException, ParserConfigurationException, SAXException {
        SAXSource source = SourceEntityProviderTest.createSAXSource(SourceEntityProviderTest.entity);
        Response response = target().path("test").path("sax").request().put(Entity.entity(source, TEXT_XML_TYPE));
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertTrue(response.readEntity(String.class).startsWith(SAXSource.class.toString()));
    }

    @Test
    public void domProviderTest() throws IOException, ParserConfigurationException, SAXException {
        DOMSource source = SourceEntityProviderTest.createDOMSoruce(SourceEntityProviderTest.entity);
        Response response = target().path("test").path("dom").request().put(Entity.entity(source, TEXT_XML_TYPE));
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertTrue(response.readEntity(String.class).startsWith(DOMSource.class.toString()));
    }

    @Test
    public void getSourceTest() throws Exception {
        Response response = target().path("test").path("source").request().get();
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        String content = SourceEntityProviderTest.extractContent(response.readEntity(Source.class));
        Assert.assertTrue(((content.startsWith(SourceEntityProviderTest.prefix)) || (content.startsWith(SourceEntityProviderTest.xdkPrefix))));
    }

    @Test
    public void getStreamSourceTest() throws Exception {
        Response response = target().path("test").path("stream").request().get();
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        String content = SourceEntityProviderTest.extractContent(response.readEntity(StreamSource.class));
        Assert.assertTrue(((content.startsWith(SourceEntityProviderTest.prefix)) || (content.startsWith(SourceEntityProviderTest.xdkPrefix))));
    }

    @Test
    public void getSaxSourceTest() throws Exception {
        Response response = target().path("test").path("sax").request().get();
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        String content = SourceEntityProviderTest.extractContent(response.readEntity(SAXSource.class));
        Assert.assertTrue((((("Content '" + content) + "' does not start with the expected prefix '") + (SourceEntityProviderTest.prefix)) + "'"), ((content.startsWith(SourceEntityProviderTest.prefix)) || (content.startsWith(SourceEntityProviderTest.xdkPrefix))));
    }

    @Test
    public void getDomSourceTest() throws Exception {
        Response response = target().path("test").path("dom").request().get();
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        String content = SourceEntityProviderTest.extractContent(response.readEntity(DOMSource.class));
        Assert.assertTrue((((("Content '" + content) + "' does not start with the expected prefix '") + (SourceEntityProviderTest.prefix)) + "'"), ((content.startsWith(SourceEntityProviderTest.prefix)) || (content.startsWith(SourceEntityProviderTest.xdkPrefix))));
    }

    @Path("test")
    public static class TestResource {
        @PUT
        @Consumes("text/xml")
        @Path("source")
        public String putSourceAndReturnString(Source source) throws IOException, TransformerException {
            return (source.getClass()) + (SourceEntityProviderTest.extractContent(source));
        }

        @PUT
        @Consumes("text/xml")
        @Path("stream")
        public String putStreamSourceAndReturnString(StreamSource source) throws IOException, TransformerException {
            return (source.getClass()) + (SourceEntityProviderTest.extractContent(source));
        }

        @PUT
        @Consumes("text/xml")
        @Path("sax")
        public String putSaxSourceAndReturnString(SAXSource source) throws IOException, TransformerException {
            return (source.getClass()) + (SourceEntityProviderTest.extractContent(source));
        }

        @PUT
        @Consumes("text/xml")
        @Path("dom")
        public String putDomSourceAndReturnString(DOMSource source) throws IOException, TransformerException {
            return (source.getClass()) + (SourceEntityProviderTest.extractContent(source));
        }

        @GET
        @Produces("application/xml")
        @Path("source")
        public StreamSource getSource() {
            return new StreamSource(new ByteArrayInputStream(SourceEntityProviderTest.entity.getBytes()));
        }

        @GET
        @Produces("application/xml")
        @Path("stream")
        public StreamSource getStreamSource() {
            return new StreamSource(new ByteArrayInputStream(SourceEntityProviderTest.entity.getBytes()));
        }

        @GET
        @Produces("application/xml")
        @Path("sax")
        public SAXSource getSaxSource() throws ParserConfigurationException, SAXException {
            return SourceEntityProviderTest.createSAXSource(SourceEntityProviderTest.entity);
        }

        @GET
        @Produces("application/xml")
        @Path("dom")
        public DOMSource getDomSource() throws Exception {
            return SourceEntityProviderTest.createDOMSoruce(SourceEntityProviderTest.entity);
        }
    }
}

