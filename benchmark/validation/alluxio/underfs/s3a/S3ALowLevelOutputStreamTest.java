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
package alluxio.underfs.s3a;


import alluxio.conf.InstancedConfiguration;
import alluxio.util.ConfigurationUtils;
import alluxio.util.FormatUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.PartETag;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.io.BufferedOutputStream;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Unit tests for the {@link S3ALowLevelOutputStream}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(S3ALowLevelOutputStream.class)
@SuppressWarnings("unchecked")
public class S3ALowLevelOutputStreamTest {
    private static final String BUCKET_NAME = "testBucket";

    private static final String PARTITION_SIZE = "8MB";

    private static final String KEY = "testKey";

    private static final String UPLOAD_ID = "testUploadId";

    private static InstancedConfiguration sConf = new InstancedConfiguration(ConfigurationUtils.defaults());

    private AmazonS3 mMockS3Client;

    private ListeningExecutorService mMockExecutor;

    private BufferedOutputStream mMockOutputStream;

    private ListenableFuture<PartETag> mMockTag;

    private S3ALowLevelOutputStream mStream;

    @Test
    public void writeByte() throws Exception {
        mStream.write(1);
        Mockito.verify(mMockS3Client).initiateMultipartUpload(ArgumentMatchers.any(InitiateMultipartUploadRequest.class));
        Mockito.verify(mMockOutputStream).write(new byte[]{ 1 }, 0, 1);
        Mockito.verify(mMockExecutor, Mockito.never()).submit(ArgumentMatchers.any(Callable.class));
        mStream.close();
        Mockito.verify(mMockExecutor).submit(ArgumentMatchers.any(Callable.class));
        Mockito.verify(mMockS3Client).completeMultipartUpload(ArgumentMatchers.any(CompleteMultipartUploadRequest.class));
    }

    @Test
    public void writeByteArray() throws Exception {
        int partSize = ((int) (FormatUtils.parseSpaceSize(S3ALowLevelOutputStreamTest.PARTITION_SIZE)));
        byte[] b = new byte[partSize + 1];
        mStream.write(b, 0, b.length);
        Mockito.verify(mMockS3Client).initiateMultipartUpload(ArgumentMatchers.any(InitiateMultipartUploadRequest.class));
        Mockito.verify(mMockOutputStream).write(b, 0, ((b.length) - 1));
        Mockito.verify(mMockOutputStream).write(b, ((b.length) - 1), 1);
        Mockito.verify(mMockExecutor).submit(ArgumentMatchers.any(Callable.class));
        mStream.close();
        Mockito.verify(mMockS3Client).completeMultipartUpload(ArgumentMatchers.any(CompleteMultipartUploadRequest.class));
    }

    @Test
    public void flush() throws Exception {
        int partSize = ((int) (FormatUtils.parseSpaceSize(S3ALowLevelOutputStreamTest.PARTITION_SIZE)));
        byte[] b = new byte[(2 * partSize) - 1];
        mStream.write(b, 0, b.length);
        Mockito.verify(mMockS3Client).initiateMultipartUpload(ArgumentMatchers.any(InitiateMultipartUploadRequest.class));
        Mockito.verify(mMockOutputStream).write(b, 0, partSize);
        Mockito.verify(mMockOutputStream).write(b, partSize, (partSize - 1));
        Mockito.verify(mMockExecutor).submit(ArgumentMatchers.any(Callable.class));
        mStream.flush();
        Mockito.verify(mMockExecutor, Mockito.times(2)).submit(ArgumentMatchers.any(Callable.class));
        Mockito.verify(mMockTag, Mockito.times(2)).get();
        mStream.close();
        Mockito.verify(mMockS3Client).completeMultipartUpload(ArgumentMatchers.any(CompleteMultipartUploadRequest.class));
    }

    @Test
    public void close() throws Exception {
        mStream.close();
        Mockito.verify(mMockS3Client, Mockito.never()).initiateMultipartUpload(ArgumentMatchers.any(InitiateMultipartUploadRequest.class));
        Mockito.verify(mMockS3Client, Mockito.never()).completeMultipartUpload(ArgumentMatchers.any(CompleteMultipartUploadRequest.class));
    }
}

