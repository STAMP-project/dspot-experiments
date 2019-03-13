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
package alluxio.client.fs;


import PropertyKey.MASTER_MOUNT_TABLE_ROOT_UFS;
import alluxio.AlluxioURI;
import alluxio.Constants;
import alluxio.client.file.FileSystem;
import alluxio.conf.ServerConfiguration;
import alluxio.testutils.LocalAlluxioClusterResource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static alluxio.client.fs.ConcurrentFileSystemMasterUtils.UnaryOperation.LIST_STATUS;


/**
 * Tests loading UFS metadata many times concurrently.
 */
public final class ConcurrentFileSystemMasterLoadMetadataTest {
    private FileSystem mFileSystem;

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().build();

    @Test
    public void loadMetadataManyDirectories() throws Exception {
        String ufsPath = ServerConfiguration.get(MASTER_MOUNT_TABLE_ROOT_UFS);
        for (int i = 0; i < 5000; i++) {
            Files.createDirectory(Paths.get(ufsPath, ("a" + i)));
        }
        // Run 20 concurrent listStatus calls on the root.
        List<AlluxioURI> paths = Collections.nCopies(20, new AlluxioURI("/"));
        List<Throwable> errors = ConcurrentFileSystemMasterUtils.unaryOperation(mFileSystem, LIST_STATUS, paths.toArray(new AlluxioURI[]{  }), (60 * (Constants.SECOND_MS)));
        Assert.assertEquals(Collections.EMPTY_LIST, errors);
    }

    @Test
    public void loadMetadataManyFiles() throws Exception {
        String ufsPath = ServerConfiguration.get(MASTER_MOUNT_TABLE_ROOT_UFS);
        for (int i = 0; i < 5000; i++) {
            Files.createFile(Paths.get(ufsPath, ("a" + i)));
        }
        // Run 20 concurrent listStatus calls on the root.
        List<AlluxioURI> paths = Collections.nCopies(20, new AlluxioURI("/"));
        List<Throwable> errors = ConcurrentFileSystemMasterUtils.unaryOperation(mFileSystem, LIST_STATUS, paths.toArray(new AlluxioURI[]{  }), (60 * (Constants.SECOND_MS)));
        Assert.assertEquals(Collections.EMPTY_LIST, errors);
    }
}

