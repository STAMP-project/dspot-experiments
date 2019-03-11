/**
 * Copyright 2017 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.io;


import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class PipedInputStreamTest {
    static class PWriter implements Runnable {
        PipedOutputStream pos;

        public byte[] bytes;

        @Override
        public void run() {
            try {
                pos.write(bytes);
                synchronized(this) {
                    notify();
                }
            } catch (IOException e) {
                e.printStackTrace(System.out);
                System.out.println("Could not write bytes");
            }
        }

        PWriter(PipedOutputStream pout, int nbytes) {
            pos = pout;
            bytes = new byte[nbytes];
            for (int i = 0; i < (bytes.length); i++) {
                bytes[i] = ((byte) ((System.currentTimeMillis()) % 9));
            }
        }
    }

    private Thread t;

    private PipedInputStreamTest.PWriter pw;

    private PipedInputStream pis;

    private PipedOutputStream pos;

    @Test
    public void constructor() {
        // Used in tests
    }

    @Test
    public void constructorLjava_io_PipedOutputStream() throws IOException {
        pis = new PipedInputStream(new PipedOutputStream());
        pis.available();
    }

    @Test
    public void available() throws Exception {
        pis = new PipedInputStream();
        pos = new PipedOutputStream();
        pis.connect(pos);
        pw = new PipedInputStreamTest.PWriter(pos, 1000);
        t = new Thread(pw);
        t.start();
        synchronized(pw) {
            pw.wait(10000);
        }
        Assert.assertTrue(("Available returned incorrect number of bytes: " + (pis.available())), ((pis.available()) == 1000));
        PipedInputStream pin = new PipedInputStream();
        PipedOutputStream pout = new PipedOutputStream(pin);
        // We know the PipedInputStream buffer size is 1024.
        // Writing another byte would cause the write to wait
        // for a read before returning
        for (int i = 0; i < 1024; i++) {
            pout.write(i);
        }
        Assert.assertEquals("Incorrect available count", 1024, pin.available());
    }

    @Test
    public void close() throws IOException {
        pis = new PipedInputStream();
        pos = new PipedOutputStream();
        pis.connect(pos);
        pis.close();
        try {
            pos.write(((byte) (127)));
            Assert.fail("Failed to throw expected exception");
        } catch (IOException e) {
            // The spec for PipedInput saya an exception should be thrown if
            // a write is attempted to a closed input. The PipedOuput spec
            // indicates that an exception should be thrown only when the
            // piped input thread is terminated without closing
        }
    }

    @Test
    public void connectLjava_io_PipedOutputStream() throws Exception {
        pis = new PipedInputStream();
        pos = new PipedOutputStream();
        Assert.assertEquals("Non-conected pipe returned non-zero available bytes", 0, pis.available());
        pis.connect(pos);
        pw = new PipedInputStreamTest.PWriter(pos, 1000);
        t = new Thread(pw);
        t.start();
        synchronized(pw) {
            pw.wait(10000);
        }
        Assert.assertEquals("Available returned incorrect number of bytes", 1000, pis.available());
    }

    @Test
    public void test_read() throws Exception {
        pis = new PipedInputStream();
        pos = new PipedOutputStream();
        pis.connect(pos);
        pw = new PipedInputStreamTest.PWriter(pos, 1000);
        t = new Thread(pw);
        t.start();
        synchronized(pw) {
            pw.wait(10000);
        }
        Assert.assertEquals("Available returned incorrect number of bytes", 1000, pis.available());
        Assert.assertEquals("read returned incorrect byte", pw.bytes[0], ((byte) (pis.read())));
    }

    @Test
    public void test_read$BII() throws Exception {
        pis = new PipedInputStream();
        pos = new PipedOutputStream();
        pis.connect(pos);
        pw = new PipedInputStreamTest.PWriter(pos, 1000);
        t = new Thread(pw);
        t.start();
        byte[] buf = new byte[400];
        synchronized(pw) {
            pw.wait(10000);
        }
        Assert.assertTrue(("Available returned incorrect number of bytes: " + (pis.available())), ((pis.available()) == 1000));
        pis.read(buf, 0, 400);
        for (int i = 0; i < 400; i++) {
            Assert.assertEquals("read returned incorrect byte[]", pw.bytes[i], buf[i]);
        }
    }

    @Test
    public void read$BII_2() throws IOException {
        PipedInputStream obj = new PipedInputStream();
        try {
            obj.read(new byte[0], 0, (-1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException t) {
            Assert.assertEquals("IndexOutOfBoundsException rather than a subclass expected", IndexOutOfBoundsException.class, t.getClass());
        }
    }

    @Test
    public void read$BII_3() throws IOException {
        PipedInputStream obj = new PipedInputStream();
        try {
            obj.read(new byte[0], (-1), 0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException t) {
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException t) {
            // Do nothing
        }
    }

    @Test
    public void read$BII_4() throws IOException {
        PipedInputStream obj = new PipedInputStream();
        try {
            obj.read(new byte[0], (-1), (-1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException t) {
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException t) {
            // Do nothing
        }
    }

    static class Worker extends Thread {
        PipedOutputStream out;

        Worker(PipedOutputStream pos) {
            this.out = pos;
        }

        @Override
        public void run() {
            try {
                out.write(20);
                out.close();
                Thread.sleep(5000);
            } catch (Exception e) {
                // Do nothing
            }
        }
    }

    @Test
    public void read_after_write_close() throws Exception {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream();
        in.connect(out);
        Thread worker = new PipedInputStreamTest.Worker(out);
        worker.start();
        Thread.sleep(2000);
        Assert.assertEquals("Should read 20.", 20, in.read());
        worker.join();
        Assert.assertEquals("Write end is closed, should return -1", (-1), in.read());
        byte[] buf = new byte[1];
        Assert.assertEquals("Write end is closed, should return -1", (-1), in.read(buf, 0, 1));
        Assert.assertEquals("Buf len 0 should return first", 0, in.read(buf, 0, 0));
        in.close();
        out.close();
    }
}

