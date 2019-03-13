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


import java.io.IOException;
import java.io.ObjectOutputStream;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 * Unit tests for {@link ch.qos.logback.core.net.AutoFlushingObjectWriter}.
 *
 * @author Sebastian Gr&ouml;bler
 */
public class AutoFlushingObjectWriterTest {
    private AutoFlushingObjectWriterTest.InstrumentedObjectOutputStream objectOutputStream;

    @Test
    public void writesToUnderlyingObjectOutputStream() throws IOException {
        // given
        ObjectWriter objectWriter = new AutoFlushingObjectWriter(objectOutputStream, 2);
        String object = "foo";
        // when
        objectWriter.write(object);
        // then
        Mockito.verify(objectOutputStream).writeObjectOverride(object);
    }

    @Test
    public void flushesAfterWrite() throws IOException {
        // given
        ObjectWriter objectWriter = new AutoFlushingObjectWriter(objectOutputStream, 2);
        String object = "foo";
        // when
        objectWriter.write(object);
        // then
        InOrder inOrder = Mockito.inOrder(objectOutputStream);
        inOrder.verify(objectOutputStream).writeObjectOverride(object);
        inOrder.verify(objectOutputStream).flush();
    }

    @Test
    public void resetsObjectOutputStreamAccordingToGivenResetFrequency() throws IOException {
        // given
        ObjectWriter objectWriter = new AutoFlushingObjectWriter(objectOutputStream, 2);
        String object = "foo";
        // when
        objectWriter.write(object);
        objectWriter.write(object);
        objectWriter.write(object);
        objectWriter.write(object);
        // then
        InOrder inOrder = Mockito.inOrder(objectOutputStream);
        inOrder.verify(objectOutputStream).writeObjectOverride(object);
        inOrder.verify(objectOutputStream).writeObjectOverride(object);
        inOrder.verify(objectOutputStream).reset();
        inOrder.verify(objectOutputStream).writeObjectOverride(object);
        inOrder.verify(objectOutputStream).writeObjectOverride(object);
        inOrder.verify(objectOutputStream).reset();
    }

    private static class InstrumentedObjectOutputStream extends ObjectOutputStream {
        protected InstrumentedObjectOutputStream() throws IOException, SecurityException {
        }

        @Override
        protected void writeObjectOverride(final Object obj) throws IOException {
            // nop
        }

        @Override
        public void flush() throws IOException {
            // nop
        }

        @Override
        public void reset() throws IOException {
            // nop
        }
    }
}

