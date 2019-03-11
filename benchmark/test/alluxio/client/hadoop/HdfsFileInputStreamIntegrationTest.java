/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.hadoop;


import PreconditionMessage.ERR_SEEK_NEGATIVE;
import PreconditionMessage.ERR_SEEK_PAST_END_OF_FILE;
import ReadType.CACHE;
import ReadType.NO_CACHE;
import alluxio.AlluxioURI;
import alluxio.client.file.FileSystem;
import alluxio.client.file.URIStatus;
import alluxio.hadoop.HdfsFileInputStream;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.util.io.BufferUtils;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Integration tests for {@link HdfsFileInputStream}.
 */
public final class HdfsFileInputStreamIntegrationTest extends BaseIntegrationTest {
    private static final int FILE_LEN = 255;

    private static final int BUFFER_SIZE = 50;

    private static final String IN_MEMORY_FILE = "/inMemoryFile";

    private static final String UFS_ONLY_FILE = "/ufsOnlyFile";

    @ClassRule
    public static LocalAlluxioClusterResource sLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().build();

    private FileSystem mFileSystem;

    private HdfsFileInputStream mInMemInputStream;

    private HdfsFileInputStream mUfsInputStream;

    @Rule
    public final ExpectedException mThrown = ExpectedException.none();

    /**
     * Tests {@link HdfsFileInputStream#available()}.
     */
    @Test
    public void available() throws Exception {
        Assert.assertEquals(HdfsFileInputStreamIntegrationTest.FILE_LEN, mInMemInputStream.available());
        createUfsInStream(NO_CACHE);
        Assert.assertEquals(HdfsFileInputStreamIntegrationTest.FILE_LEN, mUfsInputStream.available());
        // Advance the streams and check available() again.
        byte[] buf = new byte[HdfsFileInputStreamIntegrationTest.BUFFER_SIZE];
        int length1 = mInMemInputStream.read(buf);
        Assert.assertEquals(((HdfsFileInputStreamIntegrationTest.FILE_LEN) - length1), mInMemInputStream.available());
        int length2 = mUfsInputStream.read(buf);
        Assert.assertEquals(((HdfsFileInputStreamIntegrationTest.FILE_LEN) - length2), mUfsInputStream.available());
    }

    /**
     * Tests {@link HdfsFileInputStream#read()}.
     */
    @Test
    public void readTest1() throws Exception {
        createUfsInStream(NO_CACHE);
        for (int i = 0; i < (HdfsFileInputStreamIntegrationTest.FILE_LEN); i++) {
            int value = mInMemInputStream.read();
            Assert.assertEquals((i & 255), value);
            value = mUfsInputStream.read();
            Assert.assertEquals((i & 255), value);
        }
        Assert.assertEquals(HdfsFileInputStreamIntegrationTest.FILE_LEN, mInMemInputStream.getPos());
        Assert.assertEquals(HdfsFileInputStreamIntegrationTest.FILE_LEN, mUfsInputStream.getPos());
        int value = mInMemInputStream.read();
        Assert.assertEquals((-1), value);
        value = mUfsInputStream.read();
        Assert.assertEquals((-1), value);
    }

    /**
     * Tests {@link HdfsFileInputStream#read(byte[])}.
     */
    @Test
    public void readTest2() throws Exception {
        byte[] buf = new byte[HdfsFileInputStreamIntegrationTest.FILE_LEN];
        int length = mInMemInputStream.read(buf);
        Assert.assertEquals(HdfsFileInputStreamIntegrationTest.FILE_LEN, length);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf));
        createUfsInStream(NO_CACHE);
        Arrays.fill(buf, ((byte) (0)));
        length = mUfsInputStream.read(buf);
        Assert.assertEquals(HdfsFileInputStreamIntegrationTest.FILE_LEN, length);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf));
        Arrays.fill(buf, ((byte) (0)));
        length = mInMemInputStream.read(buf);
        Assert.assertEquals((-1), length);
        length = mUfsInputStream.read(buf);
        Assert.assertEquals((-1), length);
    }

    /**
     * Tests {@link HdfsFileInputStream#read(byte[], int, int)}.
     */
    @Test
    public void readTest3() throws Exception {
        byte[] buf = new byte[HdfsFileInputStreamIntegrationTest.FILE_LEN];
        int length = mInMemInputStream.read(buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
        Assert.assertEquals(HdfsFileInputStreamIntegrationTest.FILE_LEN, length);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf));
        createUfsInStream(NO_CACHE);
        Arrays.fill(buf, ((byte) (0)));
        length = mUfsInputStream.read(buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
        Assert.assertEquals(HdfsFileInputStreamIntegrationTest.FILE_LEN, length);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf));
        Arrays.fill(buf, ((byte) (0)));
        length = mInMemInputStream.read(buf, 0, 1);
        Assert.assertEquals((-1), length);
        length = mUfsInputStream.read(buf, 0, 1);
        Assert.assertEquals((-1), length);
    }

    /**
     * Tests {@link HdfsFileInputStream#read(long, byte[], int, int)}.
     */
    @Test
    public void readTest4() throws Exception {
        byte[] buf = new byte[HdfsFileInputStreamIntegrationTest.FILE_LEN];
        int length = mInMemInputStream.read(0, buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
        Assert.assertEquals(HdfsFileInputStreamIntegrationTest.FILE_LEN, length);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf));
        Assert.assertEquals(0, mInMemInputStream.getPos());
        createUfsInStream(NO_CACHE);
        Arrays.fill(buf, ((byte) (0)));
        length = mUfsInputStream.read(0, buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
        Assert.assertEquals(HdfsFileInputStreamIntegrationTest.FILE_LEN, length);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf));
        Assert.assertEquals(0, mUfsInputStream.getPos());
        buf = new byte[(HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10];
        Arrays.fill(buf, ((byte) (0)));
        length = mInMemInputStream.read(10, buf, 0, ((HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10));
        Assert.assertEquals(((HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10), length);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(10, ((HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10), buf));
        Assert.assertEquals(0, mInMemInputStream.getPos());
        Arrays.fill(buf, ((byte) (0)));
        length = mUfsInputStream.read(10, buf, 0, ((HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10));
        Assert.assertEquals(((HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10), length);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(10, ((HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10), buf));
        Assert.assertEquals(0, mUfsInputStream.getPos());
        Arrays.fill(buf, ((byte) (0)));
        length = mInMemInputStream.read((-1), buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
        Assert.assertEquals((-1), length);
        length = mUfsInputStream.read((-1), buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
        Assert.assertEquals((-1), length);
        length = mInMemInputStream.read(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
        Assert.assertEquals((-1), length);
        length = mUfsInputStream.read(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
        Assert.assertEquals((-1), length);
    }

    /**
     * Tests {@link HdfsFileInputStream#readFully(long, byte[])}.
     */
    @Test
    public void readFullyTest1() throws Exception {
        byte[] buf = new byte[HdfsFileInputStreamIntegrationTest.FILE_LEN];
        mInMemInputStream.readFully(0, buf);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf));
        Assert.assertEquals(0, mInMemInputStream.getPos());
        createUfsInStream(NO_CACHE);
        Arrays.fill(buf, ((byte) (0)));
        mUfsInputStream.readFully(0, buf);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf));
        Assert.assertEquals(0, mUfsInputStream.getPos());
        buf = new byte[(HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10];
        Arrays.fill(buf, ((byte) (0)));
        mInMemInputStream.readFully(10, buf);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(10, ((HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10), buf));
        Assert.assertEquals(0, mInMemInputStream.getPos());
        Arrays.fill(buf, ((byte) (0)));
        mUfsInputStream.readFully(10, buf);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(10, ((HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10), buf));
        Assert.assertEquals(0, mUfsInputStream.getPos());
        Arrays.fill(buf, ((byte) (0)));
        try {
            mInMemInputStream.readFully((-1), buf);
            Assert.fail("readFully() is expected to fail");
        } catch (EOFException e) {
            // this is expected
        }
        BufferUtils.equalConstantByteArray(((byte) (0)), HdfsFileInputStreamIntegrationTest.FILE_LEN, buf);
        try {
            mUfsInputStream.readFully((-1), buf);
            Assert.fail("readFully() is expected to fail");
        } catch (EOFException e) {
            // this is expected
        }
        BufferUtils.equalConstantByteArray(((byte) (0)), HdfsFileInputStreamIntegrationTest.FILE_LEN, buf);
        try {
            mInMemInputStream.readFully(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf);
            Assert.fail("readFully() is expected to fail");
        } catch (EOFException e) {
            // this is expected
        }
        BufferUtils.equalConstantByteArray(((byte) (0)), HdfsFileInputStreamIntegrationTest.FILE_LEN, buf);
        try {
            mUfsInputStream.readFully(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf);
            Assert.fail("readFully() is expected to fail");
        } catch (EOFException e) {
            // this is expected
        }
        BufferUtils.equalConstantByteArray(((byte) (0)), HdfsFileInputStreamIntegrationTest.FILE_LEN, buf);
    }

    /**
     * Tests {@link HdfsFileInputStream#readFully(long, byte[], int, int)}.
     */
    @Test
    public void readFullyTest2() throws Exception {
        byte[] buf = new byte[HdfsFileInputStreamIntegrationTest.FILE_LEN];
        mInMemInputStream.readFully(0, buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf));
        Assert.assertEquals(0, mInMemInputStream.getPos());
        createUfsInStream(NO_CACHE);
        Arrays.fill(buf, ((byte) (0)));
        mUfsInputStream.readFully(0, buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf));
        Assert.assertEquals(0, mUfsInputStream.getPos());
        buf = new byte[(HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10];
        Arrays.fill(buf, ((byte) (0)));
        mInMemInputStream.readFully(10, buf, 0, ((HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10));
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(10, ((HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10), buf));
        Assert.assertEquals(0, mInMemInputStream.getPos());
        Arrays.fill(buf, ((byte) (0)));
        mUfsInputStream.readFully(10, buf, 0, ((HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10));
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(10, ((HdfsFileInputStreamIntegrationTest.FILE_LEN) - 10), buf));
        Assert.assertEquals(0, mUfsInputStream.getPos());
        Arrays.fill(buf, ((byte) (0)));
        try {
            mInMemInputStream.readFully((-1), buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
            Assert.fail("readFully() is expected to fail");
        } catch (EOFException e) {
            // this is expected
        }
        BufferUtils.equalConstantByteArray(((byte) (0)), HdfsFileInputStreamIntegrationTest.FILE_LEN, buf);
        try {
            mUfsInputStream.readFully((-1), buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
            Assert.fail("readFully() is expected to fail");
        } catch (EOFException e) {
            // this is expected
        }
        BufferUtils.equalConstantByteArray(((byte) (0)), HdfsFileInputStreamIntegrationTest.FILE_LEN, buf);
        try {
            mInMemInputStream.readFully(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
            Assert.fail("readFully() is expected to fail");
        } catch (EOFException e) {
            // this is expected
        }
        BufferUtils.equalConstantByteArray(((byte) (0)), HdfsFileInputStreamIntegrationTest.FILE_LEN, buf);
        try {
            mUfsInputStream.readFully(HdfsFileInputStreamIntegrationTest.FILE_LEN, buf, 0, HdfsFileInputStreamIntegrationTest.FILE_LEN);
            Assert.fail("readFully() is expected to fail");
        } catch (EOFException e) {
            // this is expected
        }
        BufferUtils.equalConstantByteArray(((byte) (0)), HdfsFileInputStreamIntegrationTest.FILE_LEN, buf);
    }

    @Test
    public void inMemSeek() throws Exception {
        seekTest(mInMemInputStream);
    }

    @Test
    public void ufsSeek() throws Exception {
        createUfsInStream(NO_CACHE);
        seekTest(mUfsInputStream);
    }

    @Test
    public void seekNegative() throws Exception {
        mThrown.expect(IOException.class);
        mThrown.expectMessage(String.format(ERR_SEEK_NEGATIVE.toString(), (-1)));
        mInMemInputStream.seek((-1));
    }

    @Test
    public void seekPastEof() throws Exception {
        mThrown.expect(IOException.class);
        mThrown.expectMessage(String.format(ERR_SEEK_PAST_END_OF_FILE.toString(), ((HdfsFileInputStreamIntegrationTest.FILE_LEN) + 1)));
        mInMemInputStream.seek(((HdfsFileInputStreamIntegrationTest.FILE_LEN) + 1));
    }

    @Test
    public void seekNegativeUfs() throws Exception {
        mThrown.expect(IOException.class);
        mThrown.expectMessage(String.format(ERR_SEEK_NEGATIVE.toString(), (-1)));
        createUfsInStream(NO_CACHE);
        mUfsInputStream.seek((-1));
    }

    @Test
    public void seekPastEofUfs() throws Exception {
        mThrown.expect(IOException.class);
        mThrown.expectMessage(String.format(ERR_SEEK_PAST_END_OF_FILE.toString(), ((HdfsFileInputStreamIntegrationTest.FILE_LEN) + 1)));
        createUfsInStream(NO_CACHE);
        mUfsInputStream.seek(((HdfsFileInputStreamIntegrationTest.FILE_LEN) + 1));
    }

    @Test
    public void positionedReadCache() throws Exception {
        createUfsInStream(CACHE);
        mUfsInputStream.readFully(0, new byte[HdfsFileInputStreamIntegrationTest.FILE_LEN]);
        URIStatus statusUfsOnlyFile = mFileSystem.getStatus(new AlluxioURI(HdfsFileInputStreamIntegrationTest.UFS_ONLY_FILE));
        Assert.assertEquals(100, statusUfsOnlyFile.getInAlluxioPercentage());
    }

    @Test
    public void positionedReadNoCache() throws Exception {
        createUfsInStream(NO_CACHE);
        mUfsInputStream.readFully(0, new byte[HdfsFileInputStreamIntegrationTest.FILE_LEN]);
        URIStatus statusUfsOnlyFile = mFileSystem.getStatus(new AlluxioURI(HdfsFileInputStreamIntegrationTest.UFS_ONLY_FILE));
        Assert.assertEquals(0, statusUfsOnlyFile.getInAlluxioPercentage());
    }
}

