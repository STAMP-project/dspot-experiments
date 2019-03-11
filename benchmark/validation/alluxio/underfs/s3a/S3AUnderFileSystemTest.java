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


import ObjectUnderFileSystem.ObjectPermissions;
import PropertyKey.S3A_ACCESS_KEY;
import PropertyKey.S3A_SECRET_KEY;
import UfsMode.NO_ACCESS;
import UfsMode.READ_ONLY;
import UfsMode.READ_WRITE;
import alluxio.AlluxioURI;
import alluxio.ConfigurationTestUtils;
import alluxio.conf.InstancedConfiguration;
import alluxio.conf.PropertyKey;
import alluxio.underfs.ObjectUnderFileSystem;
import alluxio.underfs.UfsMode;
import alluxio.underfs.UnderFileSystemConfiguration;
import alluxio.underfs.options.DeleteOptions;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 * Unit tests for the {@link S3AUnderFileSystem}.
 */
public class S3AUnderFileSystemTest {
    private static final String PATH = "path";

    private static final String SRC = "src";

    private static final String DST = "dst";

    private static InstancedConfiguration sConf = ConfigurationTestUtils.defaults();

    private static final String BUCKET_NAME = "bucket";

    private static final String DEFAULT_OWNER = "";

    private static final short DEFAULT_MODE = 448;

    private S3AUnderFileSystem mS3UnderFileSystem;

    private AmazonS3Client mClient;

    private ListeningExecutorService mExecutor;

    private TransferManager mManager;

    @Rule
    public final ExpectedException mThrown = ExpectedException.none();

    @Test
    public void deleteNonRecursiveOnAmazonClientException() throws IOException {
        Mockito.when(mClient.listObjectsV2(Matchers.any(ListObjectsV2Request.class))).thenThrow(AmazonClientException.class);
        mThrown.expect(IOException.class);
        mS3UnderFileSystem.deleteDirectory(S3AUnderFileSystemTest.PATH, DeleteOptions.defaults().setRecursive(false));
    }

    @Test
    public void deleteRecursiveOnAmazonClientException() throws IOException {
        Mockito.when(mClient.listObjectsV2(Matchers.any(ListObjectsV2Request.class))).thenThrow(AmazonClientException.class);
        mThrown.expect(IOException.class);
        mS3UnderFileSystem.deleteDirectory(S3AUnderFileSystemTest.PATH, DeleteOptions.defaults().setRecursive(true));
    }

    @Test
    public void isFile404() throws IOException {
        AmazonServiceException e = new AmazonServiceException("");
        e.setStatusCode(404);
        Mockito.when(mClient.getObjectMetadata(Matchers.anyString(), Matchers.anyString())).thenThrow(e);
        Assert.assertFalse(mS3UnderFileSystem.isFile(S3AUnderFileSystemTest.SRC));
    }

    @Test
    public void isFileException() throws IOException {
        AmazonServiceException e = new AmazonServiceException("");
        e.setStatusCode(403);
        Mockito.when(mClient.getObjectMetadata(Matchers.anyString(), Matchers.anyString())).thenThrow(e);
        mThrown.expect(IOException.class);
        Assert.assertFalse(mS3UnderFileSystem.isFile(S3AUnderFileSystemTest.SRC));
    }

    @Test
    public void renameOnAmazonClientException() throws IOException {
        Mockito.when(mClient.getObjectMetadata(Matchers.anyString(), Matchers.anyString())).thenThrow(AmazonClientException.class);
        mThrown.expect(IOException.class);
        mS3UnderFileSystem.renameFile(S3AUnderFileSystemTest.SRC, S3AUnderFileSystemTest.DST);
    }

    @Test
    public void createCredentialsFromConf() throws Exception {
        Map<PropertyKey, String> conf = new HashMap<>();
        conf.put(S3A_ACCESS_KEY, "key1");
        conf.put(S3A_SECRET_KEY, "key2");
        try (Closeable c = toResource()) {
            UnderFileSystemConfiguration ufsConf = UnderFileSystemConfiguration.defaults(S3AUnderFileSystemTest.sConf);
            AWSCredentialsProvider credentialsProvider = S3AUnderFileSystem.createAwsCredentialsProvider(ufsConf);
            Assert.assertEquals("key1", credentialsProvider.getCredentials().getAWSAccessKeyId());
            Assert.assertEquals("key2", credentialsProvider.getCredentials().getAWSSecretKey());
            Assert.assertTrue((credentialsProvider instanceof AWSStaticCredentialsProvider));
        }
    }

    @Test
    public void createCredentialsFromDefault() throws Exception {
        // Unset AWS properties if present
        Map<PropertyKey, String> conf = new HashMap<>();
        conf.put(S3A_ACCESS_KEY, null);
        conf.put(S3A_SECRET_KEY, null);
        try (Closeable c = toResource()) {
            UnderFileSystemConfiguration ufsConf = UnderFileSystemConfiguration.defaults(S3AUnderFileSystemTest.sConf);
            AWSCredentialsProvider credentialsProvider = S3AUnderFileSystem.createAwsCredentialsProvider(ufsConf);
            Assert.assertTrue((credentialsProvider instanceof DefaultAWSCredentialsProviderChain));
        }
    }

    @Test
    public void getPermissionsCached() throws Exception {
        Mockito.when(mClient.getS3AccountOwner()).thenReturn(new Owner("0", "test"));
        Mockito.when(mClient.getBucketAcl(Mockito.anyString())).thenReturn(new AccessControlList());
        mS3UnderFileSystem.getPermissions();
        mS3UnderFileSystem.getPermissions();
        Mockito.verify(mClient).getS3AccountOwner();
        Mockito.verify(mClient).getBucketAcl(Mockito.anyString());
    }

    @Test
    public void getPermissionsDefault() throws Exception {
        Mockito.when(mClient.getS3AccountOwner()).thenThrow(AmazonClientException.class);
        ObjectUnderFileSystem.ObjectPermissions permissions = mS3UnderFileSystem.getPermissions();
        Assert.assertEquals(S3AUnderFileSystemTest.DEFAULT_OWNER, permissions.getGroup());
        Assert.assertEquals(S3AUnderFileSystemTest.DEFAULT_OWNER, permissions.getOwner());
        Assert.assertEquals(S3AUnderFileSystemTest.DEFAULT_MODE, permissions.getMode());
    }

    @Test
    public void getOperationMode() throws Exception {
        Map<String, UfsMode> physicalUfsState = new Hashtable<>();
        // Check default
        Assert.assertEquals(READ_WRITE, mS3UnderFileSystem.getOperationMode(physicalUfsState));
        physicalUfsState.put(new AlluxioURI(("swift://" + (S3AUnderFileSystemTest.BUCKET_NAME))).getRootPath(), NO_ACCESS);
        Assert.assertEquals(READ_WRITE, mS3UnderFileSystem.getOperationMode(physicalUfsState));
        // Check setting NO_ACCESS mode
        physicalUfsState.put(new AlluxioURI(("s3a://" + (S3AUnderFileSystemTest.BUCKET_NAME))).getRootPath(), NO_ACCESS);
        Assert.assertEquals(NO_ACCESS, mS3UnderFileSystem.getOperationMode(physicalUfsState));
        // Check setting READ_ONLY mode
        physicalUfsState.put(new AlluxioURI(("s3a://" + (S3AUnderFileSystemTest.BUCKET_NAME))).getRootPath(), READ_ONLY);
        Assert.assertEquals(READ_ONLY, mS3UnderFileSystem.getOperationMode(physicalUfsState));
        // Check setting READ_WRITE mode
        physicalUfsState.put(new AlluxioURI(("s3a://" + (S3AUnderFileSystemTest.BUCKET_NAME))).getRootPath(), READ_WRITE);
        Assert.assertEquals(READ_WRITE, mS3UnderFileSystem.getOperationMode(physicalUfsState));
    }
}

