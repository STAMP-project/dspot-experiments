/**
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.io;


import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.net.Socket;
import java.nio.channels.Pipe;
import junit.framework.TestCase;


/**
 * Test that interrupting a thread blocked on I/O causes that thread to throw
 * an InterruptedIOException.
 */
public final class InterruptedStreamTest extends TestCase {
    private static final int BUFFER_SIZE = 1024 * 1024;

    private Socket[] sockets;

    public void testInterruptPipedInputStream() throws Exception {
        PipedOutputStream out = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(out);
        testInterruptInputStream(in);
    }

    public void testInterruptPipedOutputStream() throws Exception {
        PipedOutputStream out = new PipedOutputStream();
        new PipedInputStream(out);
        testInterruptOutputStream(out);
    }

    public void testInterruptPipedReader() throws Exception {
        PipedWriter writer = new PipedWriter();
        PipedReader reader = new PipedReader(writer);
        testInterruptReader(reader);
    }

    public void testInterruptPipedWriter() throws Exception {
        final PipedWriter writer = new PipedWriter();
        new PipedReader(writer);
        testInterruptWriter(writer);
    }

    public void testInterruptReadablePipeChannel() throws Exception {
        testInterruptReadableChannel(Pipe.open().source());
    }

    public void testInterruptWritablePipeChannel() throws Exception {
        testInterruptWritableChannel(Pipe.open().sink());
    }

    public void testInterruptReadableSocketChannel() throws Exception {
        sockets = newSocketChannelPair();
        testInterruptReadableChannel(sockets[0].getChannel());
    }

    public void testInterruptWritableSocketChannel() throws Exception {
        sockets = newSocketChannelPair();
        testInterruptWritableChannel(sockets[0].getChannel());
    }
}

