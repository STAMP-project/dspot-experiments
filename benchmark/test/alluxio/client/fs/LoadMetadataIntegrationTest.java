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


import LoadMetadataPType.ALWAYS;
import LoadMetadataPType.NEVER;
import LoadMetadataPType.ONCE;
import PropertyKey.Name;
import PropertyKey.USER_FILE_METADATA_LOAD_TYPE;
import alluxio.AlluxioURI;
import alluxio.AuthenticatedUserRule;
import alluxio.Constants;
import alluxio.UnderFileSystemFactoryRegistryRule;
import alluxio.client.file.FileSystem;
import alluxio.client.file.URIStatus;
import alluxio.conf.ServerConfiguration;
import alluxio.grpc.GetStatusPOptions;
import alluxio.grpc.ListStatusPOptions;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.testutils.underfs.sleeping.SleepingUnderFileSystemFactory;
import alluxio.testutils.underfs.sleeping.SleepingUnderFileSystemOptions;
import alluxio.util.CommonUtils;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests the loading of metadata and the available options.
 */
public class LoadMetadataIntegrationTest extends BaseIntegrationTest {
    private static final long SLEEP_MS = (Constants.SECOND_MS) / 2;

    private static final long LONG_SLEEP_MS = (Constants.SECOND_MS) * 2;

    private FileSystem mFileSystem;

    private String mLocalUfsPath = Files.createTempDir().getAbsolutePath();

    @Rule
    public AuthenticatedUserRule mAuthenticatedUser = new AuthenticatedUserRule("test", ServerConfiguration.global());

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().build();

    @ClassRule
    public static UnderFileSystemFactoryRegistryRule sUnderfilesystemfactoryregistry = new UnderFileSystemFactoryRegistryRule(new SleepingUnderFileSystemFactory(new SleepingUnderFileSystemOptions().setExistsMs(LoadMetadataIntegrationTest.SLEEP_MS).setListStatusWithOptionsMs(LoadMetadataIntegrationTest.LONG_SLEEP_MS)));

    @Test
    public void loadMetadataAlways() throws Exception {
        GetStatusPOptions options = GetStatusPOptions.newBuilder().setLoadMetadataType(ALWAYS).build();
        checkGetStatus("/mnt/dir1/dirA/fileDNE1", options, false, true);
        checkGetStatus("/mnt/dir1/dirA/fileDNE1", options, false, true);
        checkGetStatus("/mnt/dir1/dirA/fileDNE2", options, false, true);
        checkGetStatus("/mnt/dir1/dirA/file", options, true, true);
        checkGetStatus("/mnt/dir1/dirA/dirDNE/", options, false, true);
    }

    @Test
    public void loadMetadataNever() throws Exception {
        GetStatusPOptions options = GetStatusPOptions.newBuilder().setLoadMetadataType(NEVER).build();
        checkGetStatus("/mnt/dir1/dirA/fileDNE1", options, false, false);
        checkGetStatus("/mnt/dir1/dirA/fileDNE1", options, false, false);
        checkGetStatus("/mnt/dir1/dirA/fileDNE2", options, false, false);
        checkGetStatus("/mnt/dir1/dirA/file", options, false, false);
        checkGetStatus("/mnt/dir1/dirA/dirDNE/", options, false, false);
        checkGetStatus("/mnt/dir1/dirA/dirDNE/fileDNE3", options, false, false);
    }

    @Test
    public void loadMetadataOnce() throws Exception {
        GetStatusPOptions options = GetStatusPOptions.newBuilder().setLoadMetadataType(ONCE).build();
        checkGetStatus("/mnt/dir1/dirA/fileDNE1", options, false, true);
        checkGetStatus("/mnt/dir1/dirA/fileDNE1", options, false, false);
        checkGetStatus("/mnt/dir1/dirA/fileDNE2", options, false, true);
        checkGetStatus("/mnt/dir1/dirA/file", options, true, true);
        checkGetStatus("/mnt/dir1/dirA/dirDNE/", options, false, true);
        checkGetStatus("/mnt/dir1/dirA/dirDNE/dir1", options, false, false);
        checkGetStatus("/mnt/dir1/dirA/dirDNE/dir1/file1", options, false, false);
        checkGetStatus("/mnt/dir1/dirA/dirDNE/dir2", options, false, false);
    }

    @Test
    public void loadMetadataOnceAfterUfsCreate() throws Exception {
        GetStatusPOptions options = GetStatusPOptions.newBuilder().setLoadMetadataType(ONCE).build();
        // dirB does not exist yet
        checkGetStatus("/mnt/dir1/dirA/dirB/file", options, false, true);
        // create dirB in UFS
        Assert.assertTrue(new File(((mLocalUfsPath) + "/dir1/dirA/dirB")).mkdirs());
        // 'ONCE' still should not load the metadata
        checkGetStatus("/mnt/dir1/dirA/dirB/file", options, false, false);
        // load metadata for dirB with 'ALWAYS'
        checkGetStatus("/mnt/dir1/dirA/dirB", GetStatusPOptions.newBuilder().setLoadMetadataType(ALWAYS).build(), true, true);
        // 'ONCE' should now load the metadata
        checkGetStatus("/mnt/dir1/dirA/dirB/file", options, false, true);
    }

    @Test
    public void loadMetadataOnceAfterUfsDelete() throws Exception {
        GetStatusPOptions options = GetStatusPOptions.newBuilder().setLoadMetadataType(ONCE).build();
        // create dirB in UFS
        Assert.assertTrue(new File(((mLocalUfsPath) + "/dir1/dirA/dirB")).mkdirs());
        checkGetStatus("/mnt/dir1/dirA/dirB/file", options, false, true);
        checkGetStatus("/mnt/dir1/dirA/dirB/file", options, false, false);
        // delete dirB in UFS
        Assert.assertTrue(new File(((mLocalUfsPath) + "/dir1/dirA/dirB")).delete());
        // 'ONCE' should not be affected if UFS is changed
        checkGetStatus("/mnt/dir1/dirA/dirB/file", options, false, false);
        // force load metadata with 'ALWAYS'
        checkGetStatus("/mnt/dir1/dirA/dirB", GetStatusPOptions.newBuilder().setLoadMetadataType(ALWAYS).build(), false, true);
        // 'ONCE' should still not load metadata, since the ancestor is absent
        checkGetStatus("/mnt/dir1/dirA/dirB/file", options, false, false);
    }

    @LocalAlluxioClusterResource.Config(confParams = { Name.USER_FILE_METADATA_LOAD_TYPE, "ALWAYS" })
    @Test
    public void loadAlwaysConfiguration() throws Exception {
        GetStatusPOptions options = GetStatusPOptions.getDefaultInstance();
        checkGetStatus("/mnt/dir1/dirA/fileDNE1", options, false, true);
        checkGetStatus("/mnt/dir1/dirA/fileDNE1", options, false, true);
    }

    @LocalAlluxioClusterResource.Config(confParams = { Name.USER_FILE_METADATA_LOAD_TYPE, "ONCE" })
    @Test
    public void loadOnceConfiguration() throws Exception {
        GetStatusPOptions options = GetStatusPOptions.getDefaultInstance();
        checkGetStatus("/mnt/dir1/dirA/fileDNE1", options, false, true);
        checkGetStatus("/mnt/dir1/dirA/fileDNE1", options, false, false);
    }

    @LocalAlluxioClusterResource.Config(confParams = { Name.USER_FILE_METADATA_LOAD_TYPE, "NEVER" })
    @Test
    public void loadNeverConfiguration() throws Exception {
        GetStatusPOptions options = GetStatusPOptions.getDefaultInstance();
        checkGetStatus("/mnt/dir1/dirA/fileDNE1", options, false, false);
        checkGetStatus("/mnt/dir1/dirA/fileDNE1", options, false, false);
    }

    @Test
    public void loadRecursive() throws Exception {
        ServerConfiguration.set(USER_FILE_METADATA_LOAD_TYPE, LoadMetadataType.ONCE.toString());
        ListStatusPOptions options = ListStatusPOptions.newBuilder().setRecursive(true).build();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                new File(((((((mLocalUfsPath) + "/dir") + i) + "/dir") + j) + "/")).mkdirs();
                FileWriter fileWriter = new FileWriter((((((((mLocalUfsPath) + "/dir") + i) + "/dir") + j) + "/") + "file"));
                fileWriter.write(("test" + i));
                fileWriter.close();
            }
        }
        long startMs = CommonUtils.getCurrentMs();
        List<URIStatus> list = mFileSystem.listStatus(new AlluxioURI("/mnt"), options);
        long durationMs = (CommonUtils.getCurrentMs()) - startMs;
        // 25 files, 25 level 2 dirs, 5 level 1 dirs, 1 file and 1 dir created in before
        Assert.assertEquals((((25 * 2) + 5) + 2), list.size());
        // Should load metadata once, in one recursive call
        Assert.assertTrue(("Expected to be between one and two SLEEP_MS. actual duration (ms): " + durationMs), ((durationMs >= (LoadMetadataIntegrationTest.LONG_SLEEP_MS)) && (durationMs <= (2 * (LoadMetadataIntegrationTest.LONG_SLEEP_MS)))));
    }
}

