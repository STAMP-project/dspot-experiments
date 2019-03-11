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
package alluxio.underfs.kodo;


import alluxio.ConfigurationTestUtils;
import alluxio.conf.InstancedConfiguration;
import alluxio.underfs.options.DeleteOptions;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 * Unit tests for the {@link KodoUnderFileSystem}.
 */
public class KodoUnderFileSystemTest {
    private static InstancedConfiguration sConf = ConfigurationTestUtils.defaults();

    private KodoUnderFileSystem mKodoUnderFileSystem;

    private KodoClient mClient;

    private static final String PATH = "path";

    private static final String SRC = "src";

    private static final String DST = "dst";

    /**
     * Test case for {@link KodoUnderFileSystem#deleteDirectory(String, DeleteOptions)}.
     */
    @Test
    public void deleteNonRecursiveOnServiceException() throws IOException {
        Mockito.when(mClient.listFiles(Matchers.anyString(), Matchers.anyString(), Matchers.anyInt(), Matchers.eq(null))).thenThrow(Exception.class);
        boolean result = mKodoUnderFileSystem.deleteDirectory(KodoUnderFileSystemTest.PATH, DeleteOptions.defaults().setRecursive(false));
        Assert.assertFalse(result);
    }

    @Test
    public void deleteRecursiveOnServiceException() throws IOException {
        Mockito.when(mClient.listFiles(Matchers.anyString(), Matchers.anyString(), Matchers.anyInt(), Matchers.eq(null))).thenThrow(Exception.class);
        boolean result = mKodoUnderFileSystem.deleteDirectory(KodoUnderFileSystemTest.PATH, DeleteOptions.defaults().setRecursive(true));
        Assert.assertFalse(result);
    }

    /**
     * Test case for {@link KodoUnderFileSystem#renameFile(String, String)}.
     */
    @Test
    public void renameOnServiceException() throws IOException {
        Mockito.when(mClient.listFiles(Matchers.anyString(), Matchers.anyString(), Matchers.anyInt(), Matchers.eq(null))).thenThrow(Exception.class);
        boolean result = mKodoUnderFileSystem.renameFile(KodoUnderFileSystemTest.SRC, KodoUnderFileSystemTest.DST);
        Assert.assertFalse(result);
    }
}

