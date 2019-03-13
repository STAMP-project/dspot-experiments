/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.e2e.entity;


import HttpHeaders.CONTENT_LENGTH;
import MediaType.APPLICATION_XML_TYPE;
import MediaType.TEXT_PLAIN_TYPE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.junit.Assert;
import org.junit.Test;


/**
 * JERSEY-1540.
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class EmptyEntityTest extends AbstractTypeTester {
    public static class TestEmtpyBean {
        private final String value;

        public TestEmtpyBean(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Provider
    public static class TestEmptyBeanProvider implements MessageBodyReader<EmptyEntityTest.TestEmtpyBean> , MessageBodyWriter<EmptyEntityTest.TestEmtpyBean> {
        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return EmptyEntityTest.TestEmtpyBean.class.isAssignableFrom(type);
        }

        @Override
        public EmptyEntityTest.TestEmtpyBean readFrom(Class<EmptyEntityTest.TestEmtpyBean> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            final String value = ReaderWriter.readFromAsString(entityStream, mediaType);
            return new EmptyEntityTest.TestEmtpyBean(value);
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return EmptyEntityTest.TestEmtpyBean.class.isAssignableFrom(type);
        }

        @Override
        public long getSize(EmptyEntityTest.TestEmtpyBean testEmtpyBean, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(EmptyEntityTest.TestEmtpyBean testEmtpyBean, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            ReaderWriter.writeToAsString(testEmtpyBean.getValue(), entityStream, mediaType);
        }
    }

    @XmlRootElement
    public static class TestJaxbBean {}

    @Path("empty")
    public static class EmptyResource {
        @POST
        @Path("jaxbelem")
        public String jaxbelem(JAXBElement<String> jaxb) {
            return "ERROR";// shouldn't be called

        }

        @POST
        @Path("jaxbbean")
        public String jaxbbean(EmptyEntityTest.TestJaxbBean bean) {
            return "ERROR";// shouldn't be called

        }

        @POST
        @Path("jaxblist")
        public String jaxblist(List<EmptyEntityTest.TestJaxbBean> beans) {
            return "ERROR";// shouldn't be called

        }

        @POST
        @Path("boolean")
        public String bool(Boolean b) {
            return "ERROR";// shouldn't be called

        }

        @POST
        @Path("character")
        public String character(Character c) {
            return "ERROR";// shouldn't be called

        }

        @POST
        @Path("integer")
        public String integer(Integer i) {
            return "ERROR";// shouldn't be called

        }

        @POST
        @Path("emptybean")
        public String emptyBean(EmptyEntityTest.TestEmtpyBean bean) {
            return bean.getValue().isEmpty() ? "PASSED" : "ERROR";
        }

        @GET
        @Path("getempty")
        public Response getEmpty(@Context
        HttpHeaders headers) {
            return Response.ok().type(headers.getAcceptableMediaTypes().get(0)).header(CONTENT_LENGTH, 0).build();
        }
    }

    public EmptyEntityTest() {
        enable(TestProperties.LOG_TRAFFIC);
    }

    @Test
    public void testSendEmptyJAXBElement() {
        WebTarget target = target("empty/jaxbelem");
        final Response response = target.request().post(Entity.entity(null, APPLICATION_XML_TYPE));
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testSendEmptyJAXBbean() {
        WebTarget target = target("empty/jaxbbean");
        final Response response = target.request().post(Entity.entity(null, APPLICATION_XML_TYPE));
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testSendEmptyJAXBlist() {
        WebTarget target = target("empty/jaxblist");
        final Response response = target.request().post(Entity.entity(null, APPLICATION_XML_TYPE));
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testSendEmptyBoolean() {
        WebTarget target = target("empty/boolean");
        final Response response = target.request().post(Entity.entity(null, TEXT_PLAIN_TYPE));
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testSendEmptyCharacter() {
        WebTarget target = target("empty/character");
        final Response response = target.request().post(Entity.entity(null, TEXT_PLAIN_TYPE));
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testSendEmptyInteger() {
        WebTarget target = target("empty/integer");
        final Response response = target.request().post(Entity.entity(null, TEXT_PLAIN_TYPE));
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testSendEmptyBean() {
        WebTarget target = target("empty/emptybean");
        final Response response = target.request().post(Entity.entity(null, TEXT_PLAIN_TYPE));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("PASSED", response.readEntity(String.class));
    }

    @Test
    public void testReceiveEmptyJAXBElement() {
        WebTarget target = target("empty/getempty");
        final Response response = target.request("application/xml").get();
        Assert.assertEquals(200, response.getStatus());
        try {
            response.readEntity(new javax.ws.rs.core.GenericType<JAXBElement<String>>() {});
            Assert.fail("ProcessingException expected.");
        } catch (ProcessingException ex) {
            Assert.assertSame(NoContentException.class, ex.getCause().getClass());
        }
        try {
            target.request("application/xml").get(new javax.ws.rs.core.GenericType<JAXBElement<String>>() {});
            Assert.fail("ResponseProcessingException expected.");
        } catch (ResponseProcessingException ex) {
            Assert.assertSame(NoContentException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void testReceiveEmptyJAXBbean() {
        WebTarget target = target("empty/getempty");
        final Response response = target.request("application/xml").get();
        Assert.assertEquals(200, response.getStatus());
        try {
            response.readEntity(EmptyEntityTest.TestJaxbBean.class);
            Assert.fail("ProcessingException expected.");
        } catch (ProcessingException ex) {
            Assert.assertSame(NoContentException.class, ex.getCause().getClass());
        }
        try {
            target.request("application/xml").get(EmptyEntityTest.TestJaxbBean.class);
            Assert.fail("ResponseProcessingException expected.");
        } catch (ResponseProcessingException ex) {
            Assert.assertSame(NoContentException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void testReceiveEmptyJAXBlist() {
        WebTarget target = target("empty/getempty");
        final Response response = target.request("application/xml").get();
        Assert.assertEquals(200, response.getStatus());
        try {
            response.readEntity(new javax.ws.rs.core.GenericType<List<EmptyEntityTest.TestJaxbBean>>() {});
            Assert.fail("ProcessingException expected.");
        } catch (ProcessingException ex) {
            Assert.assertSame(NoContentException.class, ex.getCause().getClass());
        }
        try {
            target.request("application/xml").get(new javax.ws.rs.core.GenericType<List<EmptyEntityTest.TestJaxbBean>>() {});
            Assert.fail("ResponseProcessingException expected.");
        } catch (ResponseProcessingException ex) {
            Assert.assertSame(NoContentException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void testReceiveEmptyBoolean() {
        WebTarget target = target("empty/getempty");
        final Response response = target.request("text/plain").get();
        Assert.assertEquals(200, response.getStatus());
        try {
            response.readEntity(Boolean.class);
            Assert.fail("ProcessingException expected.");
        } catch (ProcessingException ex) {
            Assert.assertSame(NoContentException.class, ex.getCause().getClass());
        }
        try {
            target.request("text/plain").get(Boolean.class);
            Assert.fail("ResponseProcessingException expected.");
        } catch (ResponseProcessingException ex) {
            Assert.assertSame(NoContentException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void testReceiveEmptyCharacter() {
        WebTarget target = target("empty/getempty");
        final Response response = target.request("text/plain").get();
        Assert.assertEquals(200, response.getStatus());
        try {
            response.readEntity(Character.class);
            Assert.fail("ProcessingException expected.");
        } catch (ProcessingException ex) {
            Assert.assertSame(NoContentException.class, ex.getCause().getClass());
        }
        try {
            target.request("text/plain").get(Character.class);
            Assert.fail("ResponseProcessingException expected.");
        } catch (ResponseProcessingException ex) {
            Assert.assertSame(NoContentException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void testReceiveEmptyInteger() {
        WebTarget target = target("empty/getempty");
        final Response response = target.request("text/plain").get();
        Assert.assertEquals(200, response.getStatus());
        try {
            response.readEntity(Integer.class);
            Assert.fail("ProcessingException expected.");
        } catch (ProcessingException ex) {
            Assert.assertSame(NoContentException.class, ex.getCause().getClass());
        }
        try {
            target.request("text/plain").get(Integer.class);
            Assert.fail("ResponseProcessingException expected.");
        } catch (ResponseProcessingException ex) {
            Assert.assertSame(NoContentException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void testReceiveEmptyBean() {
        WebTarget target = target("empty/getempty");
        final Response response = target.request("text/plain").get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(response.readEntity(EmptyEntityTest.TestEmtpyBean.class).getValue().isEmpty());
        Assert.assertTrue(target.request("text/plain").get(EmptyEntityTest.TestEmtpyBean.class).getValue().isEmpty());
    }
}

