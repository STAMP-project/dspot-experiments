/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2015, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.fibers.io;


import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.strands.channels.Channels;
import co.paralleluniverse.strands.channels.IntChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;


/**
 *
 *
 * @author pron
 */
public class FiberAsyncIOTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    private static final int PORT = 1234;

    private static final Charset charset = Charset.forName("UTF-8");

    private static final CharsetEncoder encoder = FiberAsyncIOTest.charset.newEncoder();

    private static final CharsetDecoder decoder = FiberAsyncIOTest.charset.newDecoder();

    private final FiberScheduler scheduler;

    public FiberAsyncIOTest() {
        scheduler = new FiberForkJoinScheduler("test", 4, null, false);
    }

    @Test
    public void testFiberAsyncSocket() throws Exception {
        final IntChannel sync = Channels.newIntChannel(0);
        final Fiber server = start();
        final Fiber client = start();
        client.join();
        server.join();
    }

    @Test
    public void testFiberAsyncFile() throws Exception {
        new Fiber(scheduler, new co.paralleluniverse.strands.SuspendableRunnable() {
            @Override
            public void run() throws co.paralleluniverse.fibers.SuspendExecution {
                try (FiberFileChannel ch = FiberFileChannel.open(java.nio.file.Paths.get(System.getProperty("user.home"), "fibertest.bin"), java.nio.file.StandardOpenOption.READ, java.nio.file.StandardOpenOption.WRITE, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING)) {
                    java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocateDirect(1024);
                    String text = "this is my text blahblah";
                    ch.write(FiberAsyncIOTest.encoder.encode(java.nio.CharBuffer.wrap(text)));
                    ch.position(0);
                    ch.read(buf);
                    buf.flip();
                    String read = FiberAsyncIOTest.decoder.decode(buf).toString();
                    org.junit.Assert.assertThat(read, org.hamcrest.CoreMatchers.equalTo(text));
                    buf.clear();
                    ch.position(5);
                    ch.read(buf);
                    buf.flip();
                    read = FiberAsyncIOTest.decoder.decode(buf).toString();
                    org.junit.Assert.assertThat(read, org.hamcrest.CoreMatchers.equalTo(text.substring(5)));
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start().join();
    }
}

