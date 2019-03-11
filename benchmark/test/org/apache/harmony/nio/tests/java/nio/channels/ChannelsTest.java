/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.nio.tests.java.nio.channels;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import junit.framework.TestCase;
import tests.support.Support_PortManager;


/**
 * Note: the test case uses a temp text file named "test" which contains 31
 * characters : "P@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]"
 */
public class ChannelsTest extends TestCase {
    private static final String CODE_SET = "GB2312";// $NON-NLS-1$


    private static final String BAD_CODE_SET = "GB2313";// $NON-NLS-1$


    private FileInputStream fins;

    private FileOutputStream fouts;

    private final int writebufSize = 60;

    private final int testNum = 10;

    private final int fileSize = 31;// the file size


    private File tmpFile;

    // test if new Channel to input is null
    public void testNewChannelInputStream_InputNull() throws IOException {
        ByteBuffer byteBuf = ByteBuffer.allocate(this.testNum);
        this.fins = null;
        int readres = this.testNum;
        try {
            ReadableByteChannel rbChannel = Channels.newChannel(this.fins);
            TestCase.assertNotNull(rbChannel);
            readres = rbChannel.read(byteBuf);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        TestCase.assertEquals(this.testNum, readres);
    }

    // test if buffer to read is null
    public void testNewChannelInputStream_BufferNull() throws IOException {
        ByteBuffer byteBuf = ByteBuffer.allocate(this.testNum);
        int readres = this.testNum;
        this.fins = new FileInputStream(tmpFile);
        ReadableByteChannel rbChannel = Channels.newChannel(this.fins);
        TestCase.assertNotNull(rbChannel);
        try {
            readres = rbChannel.read(null);
            TestCase.fail();
        } catch (NullPointerException e) {
            // correct
        }
        TestCase.assertEquals(this.testNum, readres);
        readres = 0;
        try {
            readres = rbChannel.read(byteBuf);
        } catch (NullPointerException e) {
            TestCase.fail();
        }
        TestCase.assertEquals(this.testNum, readres);
    }

    /* Test method for 'java.nio.channels.Channels.NewChannel' */
    public void testNewChannelInputStream() throws IOException {
        int bufSize = 10;
        int readres = 0;
        byte[] byteArray = new byte[bufSize];
        ByteBuffer byteBuf = ByteBuffer.allocate(bufSize);
        this.fins = new FileInputStream(tmpFile);
        readres = this.fins.read(byteArray);
        TestCase.assertEquals(bufSize, readres);
        TestCase.assertFalse((0 == (this.fins.available())));
        ReadableByteChannel rbChannel = Channels.newChannel(this.fins);
        // fins still reads.
        TestCase.assertFalse((0 == (this.fins.available())));
        readres = this.fins.read(byteArray);
        TestCase.assertEquals(bufSize, readres);
        // rbChannel also reads.
        TestCase.assertNotNull(rbChannel);
        readres = rbChannel.read(byteBuf);
        TestCase.assertEquals(bufSize, readres);
        InputStream ins = Channels.newInputStream(rbChannel);
        TestCase.assertNotNull(ins);
        TestCase.assertEquals(0, ins.available());
    }

    // test if fout to change is null
    public void testNewChannelOutputStream_inputNull() throws IOException {
        int writeres = this.testNum;
        ByteBuffer writebuf = ByteBuffer.allocate(this.writebufSize);
        for (int val = 0; val < ((this.writebufSize) / 2); val++) {
            writebuf.putChar(((char) (val + 64)));
        }
        this.fouts = null;
        try {
            WritableByteChannel rbChannel = Channels.newChannel(this.fouts);
            writeres = rbChannel.write(writebuf);
            TestCase.assertEquals(0, writeres);
            writebuf.flip();
            writeres = rbChannel.write(writebuf);
            TestCase.fail("Should throw NPE.");
        } catch (NullPointerException expected) {
        }
    }

    // test if write buf is null
    public void testNewChannelOutputStream_BufNull() throws IOException {
        int writeres = this.testNum;
        ByteBuffer writebuf = null;
        try {
            this.fouts = new FileOutputStream(tmpFile);
        } catch (FileNotFoundException e) {
            TestCase.fail();
        }
        WritableByteChannel rbChannel = Channels.newChannel(this.fouts);
        try {
            writeres = rbChannel.write(writebuf);
            TestCase.fail();
        } catch (NullPointerException e) {
            // correct
        }
        TestCase.assertEquals(this.testNum, writeres);
    }

    /* Test method for 'java.nio.channels.Channels.NewChannel(OutputStream)' */
    public void testNewChannelOutputStream() throws IOException {
        int writeNum = 0;
        ByteBuffer writebuf = ByteBuffer.allocateDirect(this.writebufSize);
        for (int val = 0; val < ((this.writebufSize) / 2); val++) {
            writebuf.putChar(((char) (val + 64)));
        }
        this.fouts = new FileOutputStream(tmpFile);
        WritableByteChannel testChannel = this.fouts.getChannel();
        WritableByteChannel rbChannel = Channels.newChannel(this.fouts);
        TestCase.assertTrue(testChannel.isOpen());
        TestCase.assertTrue(rbChannel.isOpen());
        byte[] bit = new byte[1];
        bit[0] = 80;
        this.fouts.write(bit);
        this.fouts.flush();
        this.fins = new FileInputStream(tmpFile);
        TestCase.assertEquals(this.fins.available(), 1);
        this.fins.close();
        writeNum = rbChannel.write(writebuf);
        // write success ,but output null
        TestCase.assertEquals(0, writeNum);
        // close of fouts does not affect on channel
        this.fouts.close();
        writeNum = rbChannel.write(writebuf);
        TestCase.assertEquals(0, writeNum);
        try {
            writeNum = testChannel.write(writebuf);
            TestCase.fail();
        } catch (ClosedChannelException e) {
            // correct
        }
        TestCase.assertEquals(0, writeNum);
        // close of rbchannel does affect on testchannel(same channel)
        rbChannel.close();
        try {
            writeNum = testChannel.write(writebuf);
            TestCase.fail();
        } catch (ClosedChannelException e) {
            // correct
        }
    }

    public void testNewInputStreamReadableByteChannel_InputNull() throws Exception {
        byte[] readbuf = new byte[this.testNum];
        this.fins = new FileInputStream(tmpFile);
        ReadableByteChannel readbc = this.fins.getChannel();
        TestCase.assertEquals(this.fileSize, this.fins.available());
        TestCase.assertTrue(readbc.isOpen());
        try {
            InputStream testins = Channels.newInputStream(null);
            TestCase.assertNotNull(testins);
            testins.read(readbuf);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testNewInputStreamReadableByteChannel() throws Exception {
        ByteBuffer readbcbuf = ByteBuffer.allocateDirect(this.testNum);
        byte[] readbuf = new byte[this.testNum];
        this.fins = new FileInputStream(tmpFile);
        ReadableByteChannel readbc = this.fins.getChannel();
        TestCase.assertEquals(this.fileSize, this.fins.available());
        TestCase.assertTrue(readbc.isOpen());
        InputStream testins = Channels.newInputStream(readbc);
        // read in testins and fins use the same pointer
        testins.read(readbuf);
        TestCase.assertEquals(this.fins.available(), ((this.fileSize) - (this.testNum)));
        int readNum = readbc.read(readbcbuf);
        TestCase.assertEquals(readNum, this.testNum);
        TestCase.assertEquals(this.fins.available(), ((this.fileSize) - ((this.testNum) * 2)));
        testins.read(readbuf);
        TestCase.assertEquals(this.fins.available(), ((this.fileSize) - ((this.testNum) * 3)));
        // readbc.close() affect testins
        readbc.close();
        TestCase.assertFalse(readbc.isOpen());
        try {
            testins.read(readbuf);
            TestCase.fail();
        } catch (ClosedChannelException e) {
            // correct
        }
    }

    public void testNewOutputStreamWritableByteChannel_InputNull() throws Exception {
        byte[] writebuf = new byte[this.testNum];
        try {
            OutputStream testouts = Channels.newOutputStream(null);
            TestCase.assertNotNull(testouts);
            testouts.write(writebuf);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            WritableByteChannel writebc = Channels.newChannel(((OutputStream) (null)));
            TestCase.assertTrue(writebc.isOpen());
            OutputStream testoutputS = Channels.newOutputStream(writebc);
            testoutputS.write(writebuf);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testNewOutputStreamWritableByteChannel() throws Exception {
        byte[] writebuf = new byte[this.testNum];
        ByteBuffer writebcbuf = ByteBuffer.allocateDirect(this.testNum);
        this.fouts = new FileOutputStream(tmpFile);
        WritableByteChannel writebc = this.fouts.getChannel();
        TestCase.assertTrue(writebc.isOpen());
        OutputStream testouts = Channels.newOutputStream(writebc);
        // read in testins and fins use the same pointer
        testouts.write(writebuf);
        this.assertFileSizeSame(tmpFile, this.testNum);
        writebc.write(writebcbuf);
        this.assertFileSizeSame(tmpFile, ((this.testNum) * 2));
        testouts.write(writebuf);
        this.assertFileSizeSame(tmpFile, ((this.testNum) * 3));
        // readbc.close() affect testins
        writebc.close();
        TestCase.assertFalse(writebc.isOpen());
        try {
            testouts.write(writebuf);
            TestCase.fail();
        } catch (ClosedChannelException e) {
            // correct
        }
    }

    public void testnewReaderCharsetError() throws Exception {
        this.fins = new FileInputStream(tmpFile);
        ReadableByteChannel rbChannel = Channels.newChannel(this.fins);
        try {
            // $NON-NLS-1$
            Channels.newReader(rbChannel, Charset.forName(ChannelsTest.BAD_CODE_SET).newDecoder(), (-1));
            TestCase.fail();
        } catch (UnsupportedCharsetException e) {
            // correct
        }
    }

    public void testnewWriterCharsetError() throws Exception {
        this.fouts = new FileOutputStream(tmpFile);
        WritableByteChannel wbChannel = Channels.newChannel(this.fouts);
        try {
            Channels.newWriter(wbChannel, Charset.forName(ChannelsTest.BAD_CODE_SET).newEncoder(), (-1));
            TestCase.fail();
        } catch (UnsupportedCharsetException e) {
            // correct
        }
    }

    /* Test method for
    'java.nio.channels.Channels.newReader(ReadableByteChannel, String)'
     */
    public void testNewReaderReadableByteChannelString_InputNull() throws IOException {
        int bufSize = this.testNum;
        int readres = 0;
        CharBuffer charBuf = CharBuffer.allocate(bufSize);
        this.fins = new FileInputStream(tmpFile);
        // channel null
        Reader testReader;
        try {
            testReader = Channels.newReader(null, Charset.forName(ChannelsTest.CODE_SET).newDecoder(), (-1));
            TestCase.assertNotNull(testReader);
            TestCase.assertFalse(testReader.ready());
            readres = testReader.read(((CharBuffer) (null)));
            TestCase.fail();
        } catch (NullPointerException e) {
            // correct
        }
        TestCase.assertEquals(0, readres);
        this.fins = null;
        // channel with null inputs
        try {
            ReadableByteChannel rbChannel = Channels.newChannel(this.fins);
            testReader = Channels.newReader(rbChannel, Charset.forName(ChannelsTest.CODE_SET).newDecoder(), (-1));
            TestCase.assertNotNull(testReader);
            TestCase.assertFalse(testReader.ready());
            readres = testReader.read(charBuf);
            TestCase.fail();
        } catch (NullPointerException e) {
            // correct
        }
    }

    /* Test method for
    'java.nio.channels.Channels.newReader(ReadableByteChannel, String)'
     */
    public void testNewReaderReadableByteChannelString_internalBufferZero() throws IOException {
        int bufSize = this.testNum;
        int readres = 0;
        CharBuffer charBuf = CharBuffer.allocate(bufSize);
        this.fins = new FileInputStream(tmpFile);
        // channel null
        Reader testReader;
        try {
            testReader = Channels.newReader(null, Charset.forName(ChannelsTest.CODE_SET).newDecoder(), 0);
            TestCase.assertNotNull(testReader);
            TestCase.assertFalse(testReader.ready());
            readres = testReader.read(((CharBuffer) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        TestCase.assertEquals(0, readres);
        this.fins = null;
        // channel with null inputs
        try {
            ReadableByteChannel rbChannel = Channels.newChannel(this.fins);
            testReader = Channels.newReader(rbChannel, Charset.forName(ChannelsTest.CODE_SET).newDecoder(), (-1));
            TestCase.assertNotNull(testReader);
            TestCase.assertFalse(testReader.ready());
            readres = testReader.read(charBuf);
            TestCase.fail();
        } catch (NullPointerException e) {
            // correct
        }
    }

    /* Test method for
    'java.nio.channels.Channels.newReader(ReadableByteChannel, String)'
     */
    public void testNewReaderReadableByteChannelString() throws IOException {
        int bufSize = this.testNum;
        int readres = 0;
        CharBuffer charBuf = CharBuffer.allocate(bufSize);
        this.fins = new FileInputStream(tmpFile);
        ReadableByteChannel rbChannel = Channels.newChannel(this.fins);
        Reader testReader = // $NON-NLS-1$
        Channels.newReader(rbChannel, Charset.forName(ChannelsTest.CODE_SET).newDecoder(), (-1));
        Reader testReader_s = Channels.newReader(rbChannel, ChannelsTest.CODE_SET);// $NON-NLS-1$

        TestCase.assertEquals(this.fileSize, this.fins.available());
        // not ready...
        TestCase.assertFalse(testReader.ready());
        TestCase.assertFalse(testReader_s.ready());
        // still reads
        readres = testReader.read(charBuf);
        TestCase.assertEquals(bufSize, readres);
        TestCase.assertEquals(0, this.fins.available());
        try {
            readres = testReader.read(((CharBuffer) (null)));
            TestCase.fail();
        } catch (NullPointerException e) {
            // correct
        }
        readres = testReader_s.read(charBuf);
        TestCase.assertEquals(0, readres);
        TestCase.assertTrue(testReader.ready());
        TestCase.assertFalse(testReader_s.ready());
    }

    /* Zero-Buffer */
    public void testNewWriterWritableByteChannelString_internalBufZero() throws IOException {
        String writebuf = "";// $NON-NLS-1$

        for (int val = 0; val < ((this.writebufSize) / 2); val++) {
            writebuf = writebuf + ((char) (val + 64));
        }
        // null channel
        try {
            Writer testWriter = Channels.newWriter(null, Charset.forName(ChannelsTest.CODE_SET).newEncoder(), (-1));
        } catch (NullPointerException expected) {
        }
        // channel with null input
        this.fouts = null;
        try {
            WritableByteChannel wbChannel = Channels.newChannel(this.fouts);
        } catch (NullPointerException expected) {
        }
    }

    /* this test cannot be passed when buffer set to 0! */
    public void testNewWriterWritableByteChannelString_InputNull() throws IOException {
        this.fouts = new FileOutputStream(tmpFile);
        WritableByteChannel wbChannel = Channels.newChannel(this.fouts);
        Writer testWriter = // $NON-NLS-1$
        Channels.newWriter(wbChannel, Charset.forName(ChannelsTest.CODE_SET).newEncoder(), 1);
        String writebuf = "";// $NON-NLS-1$

        for (int val = 0; val < ((this.writebufSize) / 2); val++) {
            writebuf = writebuf + ((char) (val + 64));
        }
        // can write to buffer
        testWriter.write(writebuf);
        testWriter.flush();
        testWriter.close();
    }

    /* Test method for
    'java.nio.channels.Channels.newWriter(WritableByteChannel, String)'
     */
    public void testNewWriterWritableByteChannelString() throws IOException {
        this.fouts = new FileOutputStream(tmpFile);
        WritableByteChannel wbChannel = Channels.newChannel(this.fouts);
        Writer testWriter = Channels.newWriter(wbChannel, ChannelsTest.CODE_SET);// $NON-NLS-1$

        Writer testWriter_s = // $NON-NLS-1$
        Channels.newWriter(wbChannel, Charset.forName(ChannelsTest.CODE_SET).newEncoder(), (-1));
        String writebuf = "";// $NON-NLS-1$

        for (int val = 0; val < ((this.writebufSize) / 2); val++) {
            writebuf = writebuf + ((char) (val + 64));
        }
        byte[] bit = new byte[1];
        bit[0] = 80;
        this.fouts.write(bit);
        this.assertFileSizeSame(tmpFile, 1);
        // writer continues to write after '1',what the fouts write
        testWriter.write(writebuf);
        testWriter.flush();
        this.assertFileSizeSame(tmpFile, (((this.writebufSize) / 2) + 1));
        // testwriter_s does not know if testwrite writes
        testWriter_s.write(writebuf);
        testWriter.flush();
        this.assertFileSizeSame(tmpFile, (((this.writebufSize) / 2) + 1));
        // testwriter_s even does not know if himself writes?
        testWriter_s.write(writebuf);
        testWriter.flush();
        this.assertFileSizeSame(tmpFile, (((this.writebufSize) / 2) + 1));
        // close the fouts, no longer writable for testWriter
        for (int val = 0; val < (this.writebufSize); val++) {
            writebuf = writebuf + ((char) (val + 64));
        }
        this.fouts.close();
        testWriter_s.write(writebuf);
        testWriter.flush();
        this.assertFileSizeSame(tmpFile, (((this.writebufSize) / 2) + 1));
    }

    /**
     *
     *
     * @unknown java.nio.channels.Channels#newReader(ReadableByteChannel channel,
    String charsetName)
     */
    public void test_newReader_LReadableByteChannel_LString() throws IOException {
        InetSocketAddress localAddr = new InetSocketAddress("127.0.0.1", Support_PortManager.getNextPort());
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(localAddr);
        SocketChannel sc = SocketChannel.open();
        sc.connect(localAddr);
        sc.configureBlocking(false);
        TestCase.assertFalse(sc.isBlocking());
        ssc.accept().close();
        ssc.close();
        TestCase.assertFalse(sc.isBlocking());
        Reader reader = Channels.newReader(sc, "UTF16");
        try {
            int i = reader.read();
            TestCase.fail("should throw IllegalBlockingModeException");
        } catch (IllegalBlockingModeException expected) {
        }
        try {
            Channels.newInputStream(sc).read();
            TestCase.fail("should throw IllegalBlockingModeException");
        } catch (IllegalBlockingModeException expected) {
        }
        sc.close();
    }
}

