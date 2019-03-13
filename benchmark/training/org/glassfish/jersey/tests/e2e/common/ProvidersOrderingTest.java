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
package org.glassfish.jersey.tests.e2e.common;


import MediaType.APPLICATION_XML_TYPE;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.message.internal.AbstractMessageReaderWriterProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class ProvidersOrderingTest extends JerseyTest {
    public static class MyType {}

    public static class MyTypeExt extends ProvidersOrderingTest.MyType {}

    public static class MyTypeExtExt extends ProvidersOrderingTest.MyTypeExt {}

    @Produces("application/test1")
    public static class MyMBW1 implements MessageBodyWriter<ProvidersOrderingTest.MyType> {
        private final List<Class<?>> callList;

        public MyMBW1(List<Class<?>> callList) {
            this.callList = callList;
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            callList.add(this.getClass());
            return false;
        }

        @Override
        public long getSize(ProvidersOrderingTest.MyType myType, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(ProvidersOrderingTest.MyType myType, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        }
    }

    @Produces("application/*")
    public static class MyMBW2 implements MessageBodyWriter<ProvidersOrderingTest.MyType> {
        private final List<Class<?>> callList;

        public MyMBW2(List<Class<?>> callList) {
            this.callList = callList;
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            callList.add(this.getClass());
            return false;
        }

        @Override
        public long getSize(ProvidersOrderingTest.MyType myType, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(ProvidersOrderingTest.MyType myType, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        }
    }

    @Produces("*/*")
    public static class MyMBW3 implements MessageBodyWriter<ProvidersOrderingTest.MyType> {
        private final List<Class<?>> callList;

        public MyMBW3(List<Class<?>> callList) {
            this.callList = callList;
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            callList.add(this.getClass());
            return true;
        }

        @Override
        public long getSize(ProvidersOrderingTest.MyType myType, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(ProvidersOrderingTest.MyType myType, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            entityStream.write("test".getBytes());
        }
    }

    @Produces("application/*")
    public static class MyMBW4 implements MessageBodyWriter<ProvidersOrderingTest.MyTypeExt> {
        private final List<Class<?>> callList;

        public MyMBW4(List<Class<?>> callList) {
            this.callList = callList;
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            callList.add(this.getClass());
            return false;
        }

        @Override
        public long getSize(ProvidersOrderingTest.MyTypeExt myType, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(ProvidersOrderingTest.MyTypeExt myType, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        }
    }

    @Produces("application/xml")
    public static class MyMBW5 implements MessageBodyWriter<Object> {
        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return false;
        }

        @Override
        public long getSize(Object myType, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(Object myType, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        }
    }

    @Consumes("application/test")
    public static class MyMBR1 implements MessageBodyReader<ProvidersOrderingTest.MyType> {
        private final List<Class<?>> callList;

        public MyMBR1(List<Class<?>> callList) {
            this.callList = callList;
        }

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            callList.add(this.getClass());
            return false;
        }

        @Override
        public ProvidersOrderingTest.MyType readFrom(Class<ProvidersOrderingTest.MyType> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            return null;
        }
    }

    @Consumes("application/test1")
    public static class MyMBR2 implements MessageBodyReader<ProvidersOrderingTest.MyTypeExt> {
        private final List<Class<?>> callList;

        public MyMBR2(List<Class<?>> callList) {
            this.callList = callList;
        }

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            callList.add(this.getClass());
            return false;
        }

        @Override
        public ProvidersOrderingTest.MyTypeExt readFrom(Class<ProvidersOrderingTest.MyTypeExt> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            return null;
        }
    }

    @Consumes("application/test1")
    public static class MyMBR3 implements MessageBodyReader<ProvidersOrderingTest.MyTypeExtExt> {
        private final List<Class<?>> callList;

        public MyMBR3(List<Class<?>> callList) {
            this.callList = callList;
        }

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            callList.add(this.getClass());
            return true;
        }

        @Override
        public ProvidersOrderingTest.MyTypeExtExt readFrom(Class<ProvidersOrderingTest.MyTypeExtExt> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            return new ProvidersOrderingTest.MyTypeExtExt();
        }
    }

    @Path("/")
    public static class MyResource {
        @Context
        MessageBodyWorkers messageBodyWorkers;

        @PUT
        @Consumes("application/test1")
        public ProvidersOrderingTest.MyType getMyType(ProvidersOrderingTest.MyTypeExtExt myType) {
            final Map<MediaType, List<MessageBodyWriter>> writers = messageBodyWorkers.getWriters(APPLICATION_XML_TYPE);
            final List<MessageBodyWriter> messageBodyWriters = writers.get(APPLICATION_XML_TYPE);
            Assert.assertTrue(((messageBodyWriters.get(0)) instanceof ProvidersOrderingTest.MyMBW5));
            return myType;
        }

        @Path("bytearray")
        @POST
        public byte[] bytearray(byte[] bytes) {
            return bytes;
        }
    }

    private List<Class<?>> callList;

    @Test
    @SuppressWarnings("unchecked")
    public void orderingTest() {
        callList.clear();
        WebTarget target = target();
        try {
            Response response = target.request("application/test1").put(Entity.entity("test", "application/test1"), Response.class);
            Assert.assertNotNull(response);
            Assert.assertEquals("Request was not handled correctly, most likely fault in MessageBodyWorker selection.", 200, response.getStatus());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            Assert.fail("Request was not handled correctly, most likely fault in MessageBodyWorker selection.");
        }
        List<Class<?>> classes = // MBR - smallest type distance (MBR1 and MBR2 are not called because of that)
        // MBW - type distance
        // MBW - most specific media type application/test1
        // MBW - application/*
        // MBW - */*, first usable writer (returns true to isWriteable call)
        Arrays.asList(ProvidersOrderingTest.MyMBR3.class, ProvidersOrderingTest.MyMBW4.class, ProvidersOrderingTest.MyMBW1.class, ProvidersOrderingTest.MyMBW2.class, ProvidersOrderingTest.MyMBW3.class);
        Assert.assertEquals(classes, callList);
    }

    @Test
    public void replaceBuiltInProvider() {
        callList.clear();
        WebTarget target = target("bytearray");
        try {
            Response response = target.request().post(Entity.text("replaceBuiltInProvider"), Response.class);
            Assert.assertNotNull(response);
            Assert.assertEquals("Request was not handled correctly, most likely fault in MessageBodyWorker selection.", 200, response.getStatus());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            Assert.fail("Request was not handled correctly, most likely fault in MessageBodyWorker selection.");
        }
        Assert.assertTrue(((ProvidersOrderingTest.MyByteArrayProvider.counter) == 2));// used to read and write entity on server side.

    }

    @Produces({ "application/octet-stream", "*/*" })
    @Consumes({ "application/octet-stream", "*/*" })
    public static final class MyByteArrayProvider extends AbstractMessageReaderWriterProvider<byte[]> {
        public static int counter = 0;

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == (byte[].class);
        }

        @Override
        public byte[] readFrom(Class<byte[]> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeTo(entityStream, out);
            (ProvidersOrderingTest.MyByteArrayProvider.counter)++;
            return out.toByteArray();
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == (byte[].class);
        }

        @Override
        public void writeTo(byte[] t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
            (ProvidersOrderingTest.MyByteArrayProvider.counter)++;
            entityStream.write(t);
        }

        @Override
        public long getSize(byte[] t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return t.length;
        }
    }
}

