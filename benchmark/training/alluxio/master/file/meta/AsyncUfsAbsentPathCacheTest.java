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


import NoopJournalContext.INSTANCE;
import alluxio.AlluxioURI;
import alluxio.grpc.MountPOptions;
import alluxio.master.file.contexts.MountContext;
import alluxio.underfs.UfsManager;
import alluxio.underfs.UnderFileSystemConfiguration;
import alluxio.util.IdUtils;
import java.io.File;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link AsyncUfsAbsentPathCache}.
 */
public class AsyncUfsAbsentPathCacheTest {
    private static final int THREADS = 4;

    private AsyncUfsAbsentPathCache mUfsAbsentPathCache;

    private MountTable mMountTable;

    private long mMountId;

    private UfsManager mUfsManager;

    private String mLocalUfsPath;

    @Test
    public void isAbsentRoot() throws Exception {
        // /mnt/a will be the first absent path
        addAbsent(new AlluxioURI("/mnt/a/b"));
        checkAbsentPaths(new AlluxioURI("/mnt/a"));
        // /mnt/a will be the first absent path
        addAbsent(new AlluxioURI("/mnt/a/b/c"));
        checkAbsentPaths(new AlluxioURI("/mnt/a"));
        // /mnt/1 will be the first absent path
        addAbsent(new AlluxioURI("/mnt/1/2"));
        checkAbsentPaths(new AlluxioURI("/mnt/1"));
        // /mnt/1 will be the first absent path
        addAbsent(new AlluxioURI("/mnt/1/3"));
        checkAbsentPaths(new AlluxioURI("/mnt/1"));
    }

    @Test
    public void isAbsentDirectory() throws Exception {
        String ufsBase = "/a/b";
        String alluxioBase = "/mnt" + ufsBase;
        // Create ufs directories
        Assert.assertTrue(new File(((mLocalUfsPath) + ufsBase)).mkdirs());
        // 'base + /c' will be the first absent path
        addAbsent(new AlluxioURI((alluxioBase + "/c/d")));
        checkAbsentPaths(new AlluxioURI((alluxioBase + "/c")));
        // 'base + /c' will be the first absent path
        addAbsent(new AlluxioURI((alluxioBase + "/c/d/e")));
        checkAbsentPaths(new AlluxioURI((alluxioBase + "/c")));
        // '/a/1' will be the first absent path
        addAbsent(new AlluxioURI("/mnt/a/1/2"));
        checkAbsentPaths(new AlluxioURI("/mnt/a/1"));
        // '/1' will be the first absent path
        addAbsent(new AlluxioURI("/mnt/1/2"));
        checkAbsentPaths(new AlluxioURI("/mnt/1"));
    }

    @Test
    public void isAbsentAddUfsDirectory() throws Exception {
        String ufsBase = "/a/b";
        String alluxioBase = "/mnt" + ufsBase;
        // Create ufs directories
        Assert.assertTrue(new File(((mLocalUfsPath) + ufsBase)).mkdirs());
        // 'base + /c' will be the first absent path
        addAbsent(new AlluxioURI((alluxioBase + "/c/d/e")));
        checkAbsentPaths(new AlluxioURI((alluxioBase + "/c")));
        // Create a sub-directory in ufs
        Assert.assertTrue(new File((((mLocalUfsPath) + ufsBase) + "/c")).mkdirs());
        // Now, 'base + /c/d' will be the first absent path
        addAbsent(new AlluxioURI((alluxioBase + "/c/d/e")));
        checkAbsentPaths(new AlluxioURI((alluxioBase + "/c/d")));
    }

    @Test
    public void isAbsentRemoveUfsDirectory() throws Exception {
        String ufsBase = "/a/b";
        String alluxioBase = "/mnt" + ufsBase;
        // Create ufs directories
        Assert.assertTrue(new File(((mLocalUfsPath) + ufsBase)).mkdirs());
        // 'base + /c' will be the first absent path
        addAbsent(new AlluxioURI((alluxioBase + "/c/d/e")));
        checkAbsentPaths(new AlluxioURI((alluxioBase + "/c")));
        // delete '/a/b' from ufs
        Assert.assertTrue(new File(((mLocalUfsPath) + ufsBase)).delete());
        // Now, '/a/b' will be the first absent path
        addAbsent(new AlluxioURI((alluxioBase + "/c/d/e")));
        checkAbsentPaths(new AlluxioURI(alluxioBase));
    }

    @Test
    public void removeMountPoint() throws Exception {
        String ufsBase = "/a/b";
        String alluxioBase = "/mnt" + ufsBase;
        // Create ufs directories
        Assert.assertTrue(new File(((mLocalUfsPath) + ufsBase)).mkdirs());
        // 'base + /c' will be the first absent path
        addAbsent(new AlluxioURI((alluxioBase + "/c/d")));
        checkAbsentPaths(new AlluxioURI((alluxioBase + "/c")));
        // Unmount
        Assert.assertTrue(mMountTable.delete(INSTANCE, new AlluxioURI("/mnt")));
        // Re-mount the same ufs
        long newMountId = IdUtils.getRandomNonNegativeLong();
        MountPOptions options = MountContext.defaults().getOptions().build();
        mUfsManager.addMount(newMountId, new AlluxioURI(mLocalUfsPath), UnderFileSystemConfiguration.defaults().setReadOnly(options.getReadOnly()).setShared(options.getShared()).createMountSpecificConf(Collections.<String, String>emptyMap()));
        mMountTable.add(INSTANCE, new AlluxioURI("/mnt"), new AlluxioURI(mLocalUfsPath), newMountId, options);
        // The cache should not contain any paths now.
        Assert.assertFalse(mUfsAbsentPathCache.isAbsent(new AlluxioURI("/mnt/a/b/c/d")));
        Assert.assertFalse(mUfsAbsentPathCache.isAbsent(new AlluxioURI("/mnt/a/b/c")));
        Assert.assertFalse(mUfsAbsentPathCache.isAbsent(new AlluxioURI("/mnt/a/b")));
        Assert.assertFalse(mUfsAbsentPathCache.isAbsent(new AlluxioURI("/mnt/a")));
        Assert.assertFalse(mUfsAbsentPathCache.isAbsent(new AlluxioURI("/mnt/")));
    }

    @Test
    public void removePath() throws Exception {
        String ufsBase = "/a/b";
        String alluxioBase = "/mnt" + ufsBase;
        // Create ufs directories
        Assert.assertTrue(new File(((mLocalUfsPath) + ufsBase)).mkdirs());
        // 'base + /c' will be the first absent path
        addAbsent(new AlluxioURI((alluxioBase + "/c/d")));
        checkAbsentPaths(new AlluxioURI((alluxioBase + "/c")));
        // Create additional ufs directories
        Assert.assertTrue(new File((((mLocalUfsPath) + ufsBase) + "/c/d")).mkdirs());
        removeAbsent(new AlluxioURI((alluxioBase + "/c/d")));
        Assert.assertFalse(mUfsAbsentPathCache.isAbsent(new AlluxioURI("/mnt/a/b/c/d")));
        Assert.assertFalse(mUfsAbsentPathCache.isAbsent(new AlluxioURI("/mnt/a/b/c")));
        Assert.assertFalse(mUfsAbsentPathCache.isAbsent(new AlluxioURI("/mnt/a/b")));
        Assert.assertFalse(mUfsAbsentPathCache.isAbsent(new AlluxioURI("/mnt/a")));
        Assert.assertFalse(mUfsAbsentPathCache.isAbsent(new AlluxioURI("/mnt/")));
    }
}

