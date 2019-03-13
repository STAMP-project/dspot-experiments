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
public class PipedOutputStreamTest {
    static class PReader implements Runnable {
        PipedInputStream reader;

        public PipedInputStream getReader() {
            return reader;
        }

        PReader(PipedOutputStream out) {
            try {
                reader = new PipedInputStream(out);
            } catch (Exception e) {
                System.out.println("Couldn't start reader");
            }
        }

        public int available() {
            try {
                return reader.available();
            } catch (Exception e) {
                return -1;
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(1000);
                    Thread.yield();
                } 
            } catch (InterruptedException e) {
                // Do nothing
            }
        }

        public String read(int nbytes) {
            byte[] buf = new byte[nbytes];
            try {
                reader.read(buf, 0, nbytes);
                return new String(buf, "UTF-8");
            } catch (IOException e) {
                System.out.println("Exception reading info");
                return "ERROR";
            }
        }
    }

    private Thread rt;

    private PipedOutputStreamTest.PReader reader;

    private PipedOutputStream out;

    @Test
    public void constructor() {
        // Used in tests
    }

    @Test
    public void constructorLjava_io_PipedInputStream() throws Exception {
        out = new PipedOutputStream(new PipedInputStream());
        out.write('b');
    }

    @Test
    public void close() throws Exception {
        out = new PipedOutputStream();
        reader = new PipedOutputStreamTest.PReader(out);
        rt = new Thread(reader);
        rt.start();
        out.close();
    }

    @Test
    public void connectLjava_io_PipedInputStream_Exception() throws IOException {
        out = new PipedOutputStream();
        out.connect(new PipedInputStream());
        try {
            out.connect(null);
            Assert.fail("should throw NullPointerException");// $NON-NLS-1$

        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void connectLjava_io_PipedInputStream() {
        try {
            out = new PipedOutputStream();
            reader = new PipedOutputStreamTest.PReader(out);
            rt = new Thread(reader);
            rt.start();
            out.connect(new PipedInputStream());
            Assert.fail("Failed to throw exception attempting connect on already connected stream");
        } catch (IOException e) {
            // Expected
        }
    }

    @Test
    public void flush() throws IOException {
        out = new PipedOutputStream();
        reader = new PipedOutputStreamTest.PReader(out);
        rt = new Thread(reader);
        rt.start();
        out.write("HelloWorld".getBytes("UTF-8"), 0, 10);
        Assert.assertTrue("Bytes written before flush", ((reader.available()) != 0));
        out.flush();
        Assert.assertEquals("Wrote incorrect bytes", "HelloWorld", reader.read(10));
    }

    @Test
    public void write$BII() throws IOException {
        out = new PipedOutputStream();
        reader = new PipedOutputStreamTest.PReader(out);
        rt = new Thread(reader);
        rt.start();
        out.write("HelloWorld".getBytes("UTF-8"), 0, 10);
        out.flush();
        Assert.assertEquals("Wrote incorrect bytes", "HelloWorld", reader.read(10));
    }

    @Test
    public void test_writeI() throws IOException {
        out = new PipedOutputStream();
        reader = new PipedOutputStreamTest.PReader(out);
        rt = new Thread(reader);
        rt.start();
        out.write('c');
        out.flush();
        Assert.assertEquals("Wrote incorrect byte", "c", reader.read(1));
    }
}

