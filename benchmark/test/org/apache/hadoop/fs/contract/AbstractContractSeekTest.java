/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.contract;


import java.io.EOFException;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Seek operations
 */
public abstract class AbstractContractSeekTest extends AbstractFSContractTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractContractSeekTest.class);

    public static final int DEFAULT_RANDOM_SEEK_COUNT = 100;

    private Path smallSeekFile;

    private Path zeroByteFile;

    private FSDataInputStream instream;

    @Test
    public void testSeekZeroByteFile() throws Throwable {
        describe("seek and read a 0 byte file");
        instream = getFileSystem().open(zeroByteFile);
        Assert.assertEquals(0, instream.getPos());
        // expect initial read to fai;
        int result = instream.read();
        assertMinusOne("initial byte read", result);
        byte[] buffer = new byte[1];
        // expect that seek to 0 works
        instream.seek(0);
        // reread, expect same exception
        result = instream.read();
        assertMinusOne("post-seek byte read", result);
        result = instream.read(buffer, 0, 1);
        assertMinusOne("post-seek buffer read", result);
    }

    @Test
    public void testBlockReadZeroByteFile() throws Throwable {
        describe("do a block read on a 0 byte file");
        instream = getFileSystem().open(zeroByteFile);
        Assert.assertEquals(0, instream.getPos());
        // expect that seek to 0 works
        byte[] buffer = new byte[1];
        int result = instream.read(buffer, 0, 1);
        assertMinusOne("block read zero byte file", result);
    }

    /**
     * Seek and read on a closed file.
     * Some filesystems let callers seek on a closed file -these must
     * still fail on the subsequent reads.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testSeekReadClosedFile() throws Throwable {
        instream = getFileSystem().open(smallSeekFile);
        AbstractFSContractTestBase.getLogger().debug(("Stream is of type " + (instream.getClass().getCanonicalName())));
        instream.close();
        try {
            instream.seek(0);
            if (!(isSupported(ContractOptions.SUPPORTS_SEEK_ON_CLOSED_FILE))) {
                Assert.fail("seek succeeded on a closed stream");
            }
        } catch (IOException e) {
            // expected a closed file
        }
        try {
            int data = instream.available();
            if (!(isSupported(ContractOptions.SUPPORTS_AVAILABLE_ON_CLOSED_FILE))) {
                Assert.fail(("available() succeeded on a closed stream, got " + data));
            }
        } catch (IOException e) {
            // expected a closed file
        }
        try {
            int data = instream.read();
            Assert.fail(("read() succeeded on a closed stream, got " + data));
        } catch (IOException e) {
            // expected a closed file
        }
        try {
            byte[] buffer = new byte[1];
            int result = instream.read(buffer, 0, 1);
            Assert.fail(("read(buffer, 0, 1) succeeded on a closed stream, got " + result));
        } catch (IOException e) {
            // expected a closed file
        }
        // what position does a closed file have?
        try {
            long offset = instream.getPos();
        } catch (IOException e) {
            // its valid to raise error here; but the test is applied to make
            // sure there's no other exception like an NPE.
        }
        // and close again
        instream.close();
    }

    @Test
    public void testNegativeSeek() throws Throwable {
        instream = getFileSystem().open(smallSeekFile);
        Assert.assertEquals(0, instream.getPos());
        try {
            instream.seek((-1));
            long p = instream.getPos();
            AbstractContractSeekTest.LOG.warn(("Seek to -1 returned a position of " + p));
            int result = instream.read();
            Assert.fail(((("expected an exception, got data " + result) + " at a position of ") + p));
        } catch (EOFException e) {
            // bad seek -expected
            handleExpectedException(e);
        } catch (IOException e) {
            // bad seek -expected, but not as preferred as an EOFException
            handleRelaxedException("a negative seek", "EOFException", e);
        }
        Assert.assertEquals(0, instream.getPos());
    }

    @Test
    public void testSeekFile() throws Throwable {
        describe("basic seek operations");
        instream = getFileSystem().open(smallSeekFile);
        Assert.assertEquals(0, instream.getPos());
        // expect that seek to 0 works
        instream.seek(0);
        int result = instream.read();
        Assert.assertEquals(0, result);
        Assert.assertEquals(1, instream.read());
        Assert.assertEquals(2, instream.getPos());
        Assert.assertEquals(2, instream.read());
        Assert.assertEquals(3, instream.getPos());
        instream.seek(128);
        Assert.assertEquals(128, instream.getPos());
        Assert.assertEquals(128, instream.read());
        instream.seek(63);
        Assert.assertEquals(63, instream.read());
    }

    @Test
    public void testSeekAndReadPastEndOfFile() throws Throwable {
        describe("verify that reading past the last bytes in the file returns -1");
        instream = getFileSystem().open(smallSeekFile);
        Assert.assertEquals(0, instream.getPos());
        // expect that seek to 0 works
        // go just before the end
        instream.seek(((AbstractFSContractTestBase.TEST_FILE_LEN) - 2));
        Assert.assertTrue("Premature EOF", ((instream.read()) != (-1)));
        Assert.assertTrue("Premature EOF", ((instream.read()) != (-1)));
        assertMinusOne("read past end of file", instream.read());
    }

    @Test
    public void testSeekPastEndOfFileThenReseekAndRead() throws Throwable {
        describe("do a seek past the EOF, then verify the stream recovers");
        instream = getFileSystem().open(smallSeekFile);
        // go just before the end. This may or may not fail; it may be delayed until the
        // read
        boolean canSeekPastEOF = !(getContract().isSupported(ContractOptions.REJECTS_SEEK_PAST_EOF, true));
        try {
            instream.seek(((AbstractFSContractTestBase.TEST_FILE_LEN) + 1));
            // if this doesn't trigger, then read() is expected to fail
            assertMinusOne("read after seeking past EOF", instream.read());
        } catch (EOFException e) {
            // This is an error iff the FS claims to be able to seek past the EOF
            if (canSeekPastEOF) {
                // a failure wasn't expected
                throw e;
            }
            handleExpectedException(e);
        } catch (IOException e) {
            // This is an error iff the FS claims to be able to seek past the EOF
            if (canSeekPastEOF) {
                // a failure wasn't expected
                throw e;
            }
            handleRelaxedException("a seek past the end of the file", "EOFException", e);
        }
        // now go back and try to read from a valid point in the file
        instream.seek(1);
        Assert.assertTrue("Premature EOF", ((instream.read()) != (-1)));
    }

    /**
     * Seek round a file bigger than IO buffers
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testSeekBigFile() throws Throwable {
        describe("Seek round a large file and verify the bytes are what is expected");
        Path testSeekFile = path("bigseekfile.txt");
        byte[] block = ContractTestUtils.dataset((100 * 1024), 0, 255);
        ContractTestUtils.createFile(getFileSystem(), testSeekFile, true, block);
        instream = getFileSystem().open(testSeekFile);
        Assert.assertEquals(0, instream.getPos());
        // expect that seek to 0 works
        instream.seek(0);
        int result = instream.read();
        Assert.assertEquals(0, result);
        Assert.assertEquals(1, instream.read());
        Assert.assertEquals(2, instream.read());
        // do seek 32KB ahead
        instream.seek(32768);
        Assert.assertEquals("@32768", block[32768], ((byte) (instream.read())));
        instream.seek(40000);
        Assert.assertEquals("@40000", block[40000], ((byte) (instream.read())));
        instream.seek(8191);
        Assert.assertEquals("@8191", block[8191], ((byte) (instream.read())));
        instream.seek(0);
        Assert.assertEquals("@0", 0, ((byte) (instream.read())));
        // try read & readFully
        instream.seek(0);
        Assert.assertEquals(0, instream.getPos());
        instream.read();
        Assert.assertEquals(1, instream.getPos());
        byte[] buf = new byte[80 * 1024];
        instream.readFully(1, buf, 0, buf.length);
        Assert.assertEquals(1, instream.getPos());
    }

    @Test
    public void testPositionedBulkReadDoesntChangePosition() throws Throwable {
        describe("verify that a positioned read does not change the getPos() value");
        assumeSupportsPositionedReadable();
        Path testSeekFile = path("bigseekfile.txt");
        byte[] block = ContractTestUtils.dataset(65536, 0, 255);
        ContractTestUtils.createFile(getFileSystem(), testSeekFile, true, block);
        instream = getFileSystem().open(testSeekFile);
        instream.seek(39999);
        Assert.assertTrue(((-1) != (instream.read())));
        Assert.assertEquals(40000, instream.getPos());
        int v = 256;
        byte[] readBuffer = new byte[v];
        Assert.assertEquals(v, instream.read(128, readBuffer, 0, v));
        // have gone back
        Assert.assertEquals(40000, instream.getPos());
        // content is the same too
        Assert.assertEquals("@40000", block[40000], ((byte) (instream.read())));
        // now verify the picked up data
        for (int i = 0; i < 256; i++) {
            Assert.assertEquals(("@" + i), block[(i + 128)], readBuffer[i]);
        }
    }

    /**
     * Lifted from TestLocalFileSystem:
     * Regression test for HADOOP-9307: BufferedFSInputStream returning
     * wrong results after certain sequences of seeks and reads.
     */
    @Test
    public void testRandomSeeks() throws Throwable {
        int limit = getContract().getLimit(ContractOptions.TEST_RANDOM_SEEK_COUNT, AbstractContractSeekTest.DEFAULT_RANDOM_SEEK_COUNT);
        describe((("Testing " + limit) + " random seeks"));
        int filesize = 10 * 1024;
        byte[] buf = ContractTestUtils.dataset(filesize, 0, 255);
        Path randomSeekFile = path("testrandomseeks.bin");
        ContractTestUtils.createFile(getFileSystem(), randomSeekFile, true, buf);
        Random r = new Random();
        // Record the sequence of seeks and reads which trigger a failure.
        int[] seeks = new int[10];
        int[] reads = new int[10];
        try (FSDataInputStream stm = getFileSystem().open(randomSeekFile)) {
            for (int i = 0; i < limit; i++) {
                int seekOff = r.nextInt(buf.length);
                int toRead = r.nextInt(Math.min(((buf.length) - seekOff), 32000));
                seeks[(i % (seeks.length))] = seekOff;
                reads[(i % (reads.length))] = toRead;
                ContractTestUtils.verifyRead(stm, buf, seekOff, toRead);
            }
        } catch (AssertionError afe) {
            StringBuilder sb = new StringBuilder();
            sb.append("Sequence of actions:\n");
            for (int j = 0; j < (seeks.length); j++) {
                sb.append("seek @ ").append(seeks[j]).append("  ").append("read ").append(reads[j]).append("\n");
            }
            AbstractContractSeekTest.LOG.error(sb.toString());
            throw afe;
        }
    }

    @Test
    public void testReadFullyZeroByteFile() throws Throwable {
        describe("readFully against a 0 byte file");
        assumeSupportsPositionedReadable();
        instream = getFileSystem().open(zeroByteFile);
        Assert.assertEquals(0, instream.getPos());
        byte[] buffer = new byte[1];
        instream.readFully(0, buffer, 0, 0);
        Assert.assertEquals(0, instream.getPos());
        // seek to 0 read 0 bytes from it
        instream.seek(0);
        Assert.assertEquals(0, instream.read(buffer, 0, 0));
    }

    @Test
    public void testReadFullyPastEOFZeroByteFile() throws Throwable {
        assumeSupportsPositionedReadable();
        describe("readFully past the EOF of a 0 byte file");
        instream = getFileSystem().open(zeroByteFile);
        byte[] buffer = new byte[1];
        // try to read past end of file
        try {
            instream.readFully(0, buffer, 0, 16);
            Assert.fail("Expected an exception");
        } catch (IllegalArgumentException | IndexOutOfBoundsException | EOFException e) {
            // expected
        }
    }

    @Test
    public void testReadFullySmallFile() throws Throwable {
        describe("readFully operations");
        assumeSupportsPositionedReadable();
        instream = getFileSystem().open(smallSeekFile);
        byte[] buffer = new byte[256];
        // expect negative length to fail
        try {
            instream.readFully(0, buffer, 0, (-16));
            Assert.fail("Expected an exception");
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            // expected
        }
        // negative offset into buffer
        try {
            instream.readFully(0, buffer, (-1), 16);
            Assert.fail("Expected an exception");
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            // expected
        }
        // expect negative position to fail, ideally with EOF
        try {
            instream.readFully((-1), buffer);
            Assert.fail("Expected an exception");
        } catch (EOFException e) {
            handleExpectedException(e);
        } catch (IOException | IllegalArgumentException | IndexOutOfBoundsException e) {
            handleRelaxedException("readFully with a negative position ", "EOFException", e);
        }
        // read more than the offset allows
        try {
            instream.readFully(0, buffer, ((buffer.length) - 8), 16);
            Assert.fail("Expected an exception");
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            // expected
        }
        // read properly
        Assert.assertEquals(0, instream.getPos());
        instream.readFully(0, buffer);
        Assert.assertEquals(0, instream.getPos());
        // now read the entire file in one go
        byte[] fullFile = new byte[AbstractFSContractTestBase.TEST_FILE_LEN];
        instream.readFully(0, fullFile);
        Assert.assertEquals(0, instream.getPos());
        try {
            instream.readFully(16, fullFile);
            Assert.fail("Expected an exception");
        } catch (EOFException e) {
            handleExpectedException(e);
        } catch (IOException e) {
            handleRelaxedException("readFully which reads past EOF ", "EOFException", e);
        }
    }

    @Test
    public void testReadFullyPastEOF() throws Throwable {
        describe("readFully past the EOF of a file");
        assumeSupportsPositionedReadable();
        instream = getFileSystem().open(smallSeekFile);
        byte[] buffer = new byte[256];
        // now read past the end of the file
        try {
            instream.readFully(((AbstractFSContractTestBase.TEST_FILE_LEN) + 1), buffer);
            Assert.fail("Expected an exception");
        } catch (EOFException e) {
            handleExpectedException(e);
        } catch (IOException e) {
            handleRelaxedException("readFully with an offset past EOF ", "EOFException", e);
        }
        // read zero bytes from an offset past EOF.
        try {
            instream.readFully(((AbstractFSContractTestBase.TEST_FILE_LEN) + 1), buffer, 0, 0);
            // a zero byte read may fail-fast
            AbstractContractSeekTest.LOG.info("Filesystem short-circuits 0-byte reads");
        } catch (EOFException e) {
            handleExpectedException(e);
        } catch (IOException e) {
            handleRelaxedException("readFully(0 bytes) with an offset past EOF ", "EOFException", e);
        }
    }

    @Test
    public void testReadFullyZeroBytebufferPastEOF() throws Throwable {
        describe("readFully zero bytes from an offset past EOF");
        assumeSupportsPositionedReadable();
        instream = getFileSystem().open(smallSeekFile);
        byte[] buffer = new byte[256];
        try {
            instream.readFully(((AbstractFSContractTestBase.TEST_FILE_LEN) + 1), buffer, 0, 0);
            // a zero byte read may fail-fast
            AbstractContractSeekTest.LOG.info("Filesystem short-circuits 0-byte reads");
        } catch (EOFException e) {
            handleExpectedException(e);
        } catch (IOException e) {
            handleRelaxedException("readFully(0 bytes) with an offset past EOF ", "EOFException", e);
        }
    }

    @Test
    public void testReadNullBuffer() throws Throwable {
        describe("try to read a null buffer ");
        assumeSupportsPositionedReadable();
        try (FSDataInputStream in = getFileSystem().open(smallSeekFile)) {
            // Null buffer
            int r = in.read(0, null, 0, 16);
            Assert.fail(("Expected an exception from a read into a null buffer, got " + r));
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testReadSmallFile() throws Throwable {
        describe("PositionedRead.read operations");
        assumeSupportsPositionedReadable();
        instream = getFileSystem().open(smallSeekFile);
        byte[] buffer = new byte[256];
        int r;
        // expect negative length to fail
        try {
            r = instream.read(0, buffer, 0, (-16));
            Assert.fail(("Expected an exception, got " + r));
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            // expected
        }
        // negative offset into buffer
        try {
            r = instream.read(0, buffer, (-1), 16);
            Assert.fail(("Expected an exception, got " + r));
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            // expected
        }
        // negative position
        try {
            r = instream.read((-1), buffer, 0, 16);
            Assert.fail(("Expected an exception, got " + r));
        } catch (EOFException e) {
            handleExpectedException(e);
        } catch (IOException | IllegalArgumentException | IndexOutOfBoundsException e) {
            handleRelaxedException("read() with a negative position ", "EOFException", e);
        }
        // read more than the offset allows
        try {
            r = instream.read(0, buffer, ((buffer.length) - 8), 16);
            Assert.fail(("Expected an exception, got " + r));
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            // expected
        }
        // read properly
        Assert.assertEquals(0, instream.getPos());
        instream.readFully(0, buffer);
        Assert.assertEquals(0, instream.getPos());
        // now read the entire file in one go
        byte[] fullFile = new byte[AbstractFSContractTestBase.TEST_FILE_LEN];
        Assert.assertEquals(AbstractFSContractTestBase.TEST_FILE_LEN, instream.read(0, fullFile, 0, fullFile.length));
        Assert.assertEquals(0, instream.getPos());
        // now read past the end of the file
        Assert.assertEquals((-1), instream.read(((AbstractFSContractTestBase.TEST_FILE_LEN) + 16), buffer, 0, 1));
    }

    @Test
    public void testReadAtExactEOF() throws Throwable {
        describe("read at the end of the file");
        instream = getFileSystem().open(smallSeekFile);
        instream.seek(((AbstractFSContractTestBase.TEST_FILE_LEN) - 1));
        Assert.assertTrue("read at last byte", ((instream.read()) > 0));
        Assert.assertEquals("read just past EOF", (-1), instream.read());
    }
}

