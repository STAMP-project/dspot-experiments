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
package alluxio.master.file.meta;


import alluxio.AlluxioURI;
import alluxio.underfs.UfsManager;
import alluxio.underfs.UnderFileSystem;
import alluxio.util.IdUtils;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link LazyUfsBlockLocationCache}.
 */
public class LazyUfsBlockLocationCacheTest {
    private String mLocalUfsPath;

    private UnderFileSystem mLocalUfs;

    private long mMountId;

    private UfsManager mUfsManager;

    private MountTable mMountTable;

    private LazyUfsBlockLocationCache mUfsBlockLocationCache;

    @Test
    public void get() throws Exception {
        final long blockId = IdUtils.getRandomNonNegativeLong();
        final AlluxioURI fileUri = new AlluxioURI("/mnt/file");
        final String localFilePath = new AlluxioURI(mLocalUfsPath).join("file").getPath();
        mLocalUfs.create(localFilePath);
        final List<String> ufsLocations = mLocalUfs.getFileLocations(localFilePath);
        for (String location : ufsLocations) {
            System.out.println(location);
        }
        Assert.assertNull(mUfsBlockLocationCache.get(blockId));
        List<String> locations = mUfsBlockLocationCache.get(blockId, fileUri, 0);
        Assert.assertArrayEquals(ufsLocations.toArray(), locations.toArray());
        locations = mUfsBlockLocationCache.get(blockId);
        Assert.assertArrayEquals(ufsLocations.toArray(), locations.toArray());
        mUfsBlockLocationCache.invalidate(blockId);
        Assert.assertNull(mUfsBlockLocationCache.get(blockId));
    }
}

