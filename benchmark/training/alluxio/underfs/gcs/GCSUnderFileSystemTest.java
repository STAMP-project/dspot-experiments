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
package alluxio.underfs.gcs;


import alluxio.underfs.options.DeleteOptions;
import java.io.IOException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.GoogleStorageService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 * Unit tests for the {@link GCSUnderFileSystem}.
 */
public class GCSUnderFileSystemTest {
    private GCSUnderFileSystem mGCSUnderFileSystem;

    private GoogleStorageService mClient;

    private static final String PATH = "path";

    private static final String SRC = "src";

    private static final String DST = "dst";

    private static final String BUCKET_NAME = "bucket";

    private static final short BUCKET_MODE = 0;

    private static final String ACCOUNT_OWNER = "account owner";

    /**
     * Test case for {@link GCSUnderFileSystem#deleteDirectory(String, DeleteOptions)}.
     */
    @Test
    public void deleteNonRecursiveOnServiceException() throws IOException, ServiceException {
        Mockito.when(mClient.listObjectsChunked(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyLong(), Matchers.anyString())).thenThrow(ServiceException.class);
        boolean result = mGCSUnderFileSystem.deleteDirectory(GCSUnderFileSystemTest.PATH, DeleteOptions.defaults().setRecursive(false));
        Assert.assertFalse(result);
    }

    /**
     * Test case for {@link GCSUnderFileSystem#deleteDirectory(String, DeleteOptions)}.
     */
    @Test
    public void deleteRecursiveOnServiceException() throws IOException, ServiceException {
        Mockito.when(mClient.listObjectsChunked(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyLong(), Matchers.anyString())).thenThrow(ServiceException.class);
        boolean result = mGCSUnderFileSystem.deleteDirectory(GCSUnderFileSystemTest.PATH, DeleteOptions.defaults().setRecursive(true));
        Assert.assertFalse(result);
    }

    /**
     * Test case for {@link GCSUnderFileSystem#renameFile(String, String)}.
     */
    @Test
    public void renameOnServiceException() throws IOException, ServiceException {
        Mockito.when(mClient.listObjectsChunked(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyLong(), Matchers.anyString())).thenThrow(ServiceException.class);
        boolean result = mGCSUnderFileSystem.renameFile(GCSUnderFileSystemTest.SRC, GCSUnderFileSystemTest.DST);
        Assert.assertFalse(result);
    }
}

