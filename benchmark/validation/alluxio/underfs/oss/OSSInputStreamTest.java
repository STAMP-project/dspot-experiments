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
package alluxio.underfs.oss;


import alluxio.conf.AlluxioConfiguration;
import alluxio.util.ConfigurationUtils;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import java.io.IOException;
import java.io.InputStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for the {@link OSSInputStream}.
 */
public class OSSInputStreamTest {
    private static final String BUCKET_NAME = "testBucket";

    private static final String OBJECT_KEY = "testObjectKey";

    private static AlluxioConfiguration sConf = new alluxio.conf.InstancedConfiguration(ConfigurationUtils.defaults());

    private OSSInputStream mOssInputStream;

    private OSSClient mOssClient;

    private InputStream[] mInputStreamSpy;

    private OSSObject[] mOssObject;

    /**
     * The exception expected to be thrown.
     */
    @Rule
    public final ExpectedException mExceptionRule = ExpectedException.none();

    @Test
    public void close() throws IOException {
        mOssInputStream.close();
        mExceptionRule.expect(IOException.class);
        mExceptionRule.expectMessage(CoreMatchers.is("Stream closed"));
        mOssInputStream.read();
    }

    @Test
    public void readInt() throws IOException {
        Assert.assertEquals(1, mOssInputStream.read());
        Assert.assertEquals(2, mOssInputStream.read());
        Assert.assertEquals(3, mOssInputStream.read());
    }

    @Test
    public void readByteArray() throws IOException {
        byte[] bytes = new byte[3];
        int readCount = mOssInputStream.read(bytes, 0, 3);
        Assert.assertEquals(3, readCount);
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3 }, bytes);
    }

    @Test
    public void skip() throws IOException {
        Assert.assertEquals(1, mOssInputStream.read());
        mOssInputStream.skip(1);
        Assert.assertEquals(3, mOssInputStream.read());
    }
}

