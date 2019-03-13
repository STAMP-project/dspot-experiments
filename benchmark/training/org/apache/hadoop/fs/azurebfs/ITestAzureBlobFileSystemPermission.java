/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.azurebfs;


import CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY;
import java.util.UUID;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.azurebfs.utils.Parallelized;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test permission operations.
 */
@RunWith(Parallelized.class)
public class ITestAzureBlobFileSystemPermission extends AbstractAbfsIntegrationTest {
    private static Path testRoot = new Path("/test");

    private static final String DEFAULT_UMASK_VALUE = "027";

    private static final FsPermission DEFAULT_UMASK_PERMISSION = new FsPermission(ITestAzureBlobFileSystemPermission.DEFAULT_UMASK_VALUE);

    private static final int KILOBYTE = 1024;

    private FsPermission permission;

    private Path path;

    public ITestAzureBlobFileSystemPermission(FsPermission testPermission) throws Exception {
        super();
        permission = testPermission;
    }

    @Test
    public void testFilePermission() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        fs.getConf().set(FS_PERMISSIONS_UMASK_KEY, ITestAzureBlobFileSystemPermission.DEFAULT_UMASK_VALUE);
        path = new Path(ITestAzureBlobFileSystemPermission.testRoot, UUID.randomUUID().toString());
        fs.mkdirs(path.getParent(), new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE));
        fs.removeDefaultAcl(path.getParent());
        fs.create(path, permission, true, ITestAzureBlobFileSystemPermission.KILOBYTE, ((short) (1)), ((ITestAzureBlobFileSystemPermission.KILOBYTE) - 1), null);
        FileStatus status = fs.getFileStatus(path);
        Assert.assertEquals(permission.applyUMask(ITestAzureBlobFileSystemPermission.DEFAULT_UMASK_PERMISSION), status.getPermission());
    }

    @Test
    public void testFolderPermission() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        fs.getConf().set(FS_PERMISSIONS_UMASK_KEY, "027");
        path = new Path(ITestAzureBlobFileSystemPermission.testRoot, UUID.randomUUID().toString());
        fs.mkdirs(path.getParent(), new FsPermission(FsAction.ALL, FsAction.WRITE, FsAction.NONE));
        fs.removeDefaultAcl(path.getParent());
        fs.mkdirs(path, permission);
        FileStatus status = fs.getFileStatus(path);
        Assert.assertEquals(permission.applyUMask(ITestAzureBlobFileSystemPermission.DEFAULT_UMASK_PERMISSION), status.getPermission());
    }
}

