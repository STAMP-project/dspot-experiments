/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.net;


import ch.qos.logback.core.net.mock.MockContext;
import ch.qos.logback.core.net.server.test.ServerSocketUtil;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import ch.qos.logback.core.util.ExecutorServiceUtil;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Integration tests for {@link ch.qos.logback.core.net.AbstractSocketAppender}.
 *
 * @author Carl Harris
 * @author Sebastian Gr&ouml;bler
 */
public class AbstractSocketAppenderIntegrationTest {
    private static final int TIMEOUT = 2000;

    private ScheduledExecutorService executorService = ExecutorServiceUtil.newScheduledExecutorService();

    private MockContext mockContext = new MockContext(executorService);

    private AutoFlushingObjectWriter objectWriter;

    private ObjectWriterFactory objectWriterFactory = new AbstractSocketAppenderIntegrationTest.SpyProducingObjectWriterFactory();

    private LinkedBlockingDeque<String> deque = Mockito.spy(new LinkedBlockingDeque<String>(1));

    private QueueFactory queueFactory = Mockito.mock(QueueFactory.class);

    private AbstractSocketAppenderIntegrationTest.InstrumentedSocketAppender instrumentedAppender = new AbstractSocketAppenderIntegrationTest.InstrumentedSocketAppender(queueFactory, objectWriterFactory);

    @Test
    public void dispatchesEvents() throws Exception {
        // given
        ServerSocket serverSocket = ServerSocketUtil.createServerSocket();
        setRemoteHost(serverSocket.getInetAddress().getHostAddress());
        setPort(serverSocket.getLocalPort());
        start();
        Socket appenderSocket = serverSocket.accept();
        serverSocket.close();
        // when
        append("some event");
        // wait for event to be taken from deque and being written into the stream
        Mockito.verify(deque, Mockito.timeout(AbstractSocketAppenderIntegrationTest.TIMEOUT).atLeastOnce()).takeFirst();
        Mockito.verify(objectWriter, Mockito.timeout(AbstractSocketAppenderIntegrationTest.TIMEOUT)).write("some event");
        // then
        ObjectInputStream ois = new ObjectInputStream(appenderSocket.getInputStream());
        Assert.assertEquals("some event", ois.readObject());
        appenderSocket.close();
    }

    private static class InstrumentedSocketAppender extends AbstractSocketAppender<String> {
        public InstrumentedSocketAppender(QueueFactory queueFactory, ObjectWriterFactory objectWriterFactory) {
            super(queueFactory, objectWriterFactory);
        }

        @Override
        protected void postProcessEvent(String event) {
        }

        @Override
        protected PreSerializationTransformer<String> getPST() {
            return new PreSerializationTransformer<String>() {
                public Serializable transform(String event) {
                    return event;
                }
            };
        }
    }

    private class SpyProducingObjectWriterFactory extends ObjectWriterFactory {
        @Override
        public AutoFlushingObjectWriter newAutoFlushingObjectWriter(OutputStream outputStream) throws IOException {
            objectWriter = Mockito.spy(super.newAutoFlushingObjectWriter(outputStream));
            return objectWriter;
        }
    }
}

