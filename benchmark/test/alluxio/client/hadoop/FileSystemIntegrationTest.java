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


import AuthType.SIMPLE;
import PropertyKey.SECURITY_AUTHENTICATION_TYPE;
import PropertyKey.SECURITY_AUTHORIZATION_PERMISSION_ENABLED;
import alluxio.hadoop.FileSystem;
import alluxio.testutils.LocalAlluxioClusterResource;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Integration tests for {@link FileSystem}.
 */
public class FileSystemIntegrationTest {
    @ClassRule
    public static LocalAlluxioClusterResource sLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(SECURITY_AUTHENTICATION_TYPE, SIMPLE.getAuthName()).setProperty(SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "true").build();

    private static FileSystem sTFS;

    @Test
    public void closeFileSystem() throws Exception {
        Path file = new Path("/createfile");
        FsPermission permission = FsPermission.createImmutable(((short) (438)));
        FSDataOutputStream o = /* ignored */
        /* ignored */
        /* ignored */
        /* ignored */
        /* ignored */
        FileSystemIntegrationTest.sTFS.create(file, permission, false, 10, ((short) (1)), 512, null);
        o.writeBytes("Test Bytes");
        o.close();
        // with mark of delete-on-exit, the close method will try to delete it
        FileSystemIntegrationTest.sTFS.deleteOnExit(file);
        FileSystemIntegrationTest.sTFS.close();
    }
}

