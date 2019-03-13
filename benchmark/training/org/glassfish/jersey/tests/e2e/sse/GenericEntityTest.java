/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.e2e.sse;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.util.Pair;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import javax.ws.rs.sse.SseEventSource;
import junit.framework.TestCase;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


public class GenericEntityTest extends JerseyTest {
    private static final int BUFFER_SIZE = 20;

    @Test
    public void testGenericString() throws InterruptedException {
        WebTarget sseTarget = target("genericentityresource/string");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        GenericEntityTest.MessageLatch<String> messageLatch = new GenericEntityTest.MessageLatch<>(countDownLatch);
        try (SseEventSource source = SseEventSource.target(sseTarget).build()) {
            source.register(( event) -> messageLatch.consume(event.readData()));
            source.open();
            TestCase.assertTrue(countDownLatch.await(5, TimeUnit.SECONDS));
            TestCase.assertEquals("Cindy", messageLatch.data().get(0));
        }
    }

    @Test
    public void testGenericPair() throws InterruptedException {
        WebTarget sseTarget = target("genericentityresource/pair").register(GenericEntityTest.PairMBRW.class);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        GenericEntityTest.MessageLatch<Pair<String, Integer>> messageLatch = new GenericEntityTest.MessageLatch<>(countDownLatch);
        try (SseEventSource source = SseEventSource.target(sseTarget).build()) {
            source.register(( event) -> messageLatch.consume(event.readData(.class)));
            source.open();
            TestCase.assertTrue(countDownLatch.await(5, TimeUnit.SECONDS));
            Pair<String, Integer> pair = messageLatch.data().get(0);
            TestCase.assertEquals("Cindy", pair.getKey());
            TestCase.assertEquals(30, pair.getValue().intValue());
        }
    }

    @Test
    public void testGenericList() throws InterruptedException {
        WebTarget sseTarget = target("genericentityresource/list").register(GenericEntityTest.ListPairMBRW.class);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        GenericEntityTest.MessageLatch<Pair<String, Integer>> messageLatch = new GenericEntityTest.MessageLatch<>(countDownLatch);
        try (SseEventSource source = SseEventSource.target(sseTarget).build()) {
            source.register(( event) -> messageLatch.consume(((List<Pair<String, Integer>>) (event.readData(.class)))));
            source.open();
            TestCase.assertTrue(countDownLatch.await(5, TimeUnit.SECONDS));
            Pair<String, Integer> cindy = messageLatch.data().get(0);
            Pair<String, Integer> jack = messageLatch.data().get(1);
            TestCase.assertEquals("Cindy", cindy.getKey());
            TestCase.assertEquals(30, cindy.getValue().intValue());
            TestCase.assertEquals("Jack", jack.getKey());
            TestCase.assertEquals(32, jack.getValue().intValue());
        }
    }

    @Singleton
    @Path("genericentityresource")
    public static class SSEGenericEntityResource {
        @GET
        @Path("string")
        @Produces(MediaType.SERVER_SENT_EVENTS)
        public void sendString(@Context
        SseEventSink sink, @Context
        Sse sse) {
            GenericEntity<String> ges = new GenericEntity<String>("Cindy") {};
            try (SseEventSink s = sink) {
                s.send(sse.newEventBuilder().data(ges).build());
            }
        }

        @GET
        @Path("pair")
        @Produces(MediaType.SERVER_SENT_EVENTS)
        public void sendPair(@Context
        SseEventSink sink, @Context
        Sse sse) {
            Pair<String, Integer> person = new Pair<>("Cindy", 30);
            GenericEntity<Pair<String, Integer>> entity = new GenericEntity<Pair<String, Integer>>(person) {};
            try (SseEventSink s = sink) {
                s.send(sse.newEventBuilder().data(entity).build());
            }
        }

        @GET
        @Path("list")
        @Produces(MediaType.SERVER_SENT_EVENTS)
        public void sendList(@Context
        SseEventSink sink, @Context
        Sse sse) {
            Pair<String, Integer> person1 = new Pair<>("Cindy", 30);
            Pair<String, Integer> person2 = new Pair<>("Jack", 32);
            java.util.List<Pair<String, Integer>> people = new LinkedList<>();
            people.add(person1);
            people.add(person2);
            GenericEntity<java.util.List<Pair<String, Integer>>> entity = new GenericEntity<java.util.List<Pair<String, Integer>>>(people) {};
            try (SseEventSink s = sink) {
                s.send(sse.newEventBuilder().data(entity).build());
            }
        }
    }

    private static class PairMBRW implements MessageBodyReader<Pair<String, Integer>> , MessageBodyWriter<Pair<String, Integer>> {
        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == (Pair.class);
        }

        @Override
        public Pair<String, Integer> readFrom(Class<Pair<String, Integer>> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            byte[] buffer = new byte[GenericEntityTest.BUFFER_SIZE];
            entityStream.read(buffer);
            return GenericEntityTest.PairMBRW.readFrom(new String(buffer, Charset.defaultCharset()).trim());
        }

        static Pair<String, Integer> readFrom(String from) {
            String[] split = from.split(",", 2);
            return new Pair<String, Integer>(split[0], Integer.parseInt(split[1]));
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == (Pair.class);
        }

        @Override
        public void writeTo(Pair<String, Integer> stringIntegerPair, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            GenericEntityTest.PairMBRW.writeTo(stringIntegerPair, entityStream);
        }

        static void writeTo(Pair<String, Integer> stringIntegerPair, OutputStream entityStream) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append(stringIntegerPair.getKey()).append(",").append(stringIntegerPair.getValue());
            entityStream.write(sb.toString().getBytes(Charset.defaultCharset()));
        }
    }

    private static class ListPairMBRW implements MessageBodyReader<java.util.List<Pair<String, Integer>>> , MessageBodyWriter<java.util.List<Pair<String, Integer>>> {
        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == (java.util.List.class);
        }

        @Override
        public java.util.List<Pair<String, Integer>> readFrom(Class<java.util.List<Pair<String, Integer>>> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            java.util.List<Pair<String, Integer>> list = new LinkedList<>();
            byte[] buffer = new byte[20];
            entityStream.read(buffer);
            StringTokenizer st = new StringTokenizer(new String(buffer, Charset.defaultCharset()).trim(), ";", false);
            while (st.hasMoreTokens()) {
                list.add(GenericEntityTest.PairMBRW.readFrom(st.nextToken()));
            } 
            return list;
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == (java.util.List.class);
        }

        @Override
        public void writeTo(java.util.List<Pair<String, Integer>> pairs, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            for (Pair<String, Integer> pair : pairs) {
                GenericEntityTest.PairMBRW.writeTo(pair, entityStream);
                entityStream.write(";".getBytes());
            }
        }
    }

    private static class MessageLatch<T> {
        private CountDownLatch countDownLatch;

        private java.util.List<T> data = new LinkedList<>();

        private MessageLatch(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        private void consume(java.util.List<T> list) {
            for (T o : list) {
                data.add(o);
                countDownLatch.countDown();
            }
        }

        private void consume(T o) {
            data.add(o);
            countDownLatch.countDown();
        }

        private java.util.List<T> data() {
            return data;
        }
    }
}

