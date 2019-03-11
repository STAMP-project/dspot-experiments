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


import Constants.IMPERSONATION_NONE;
import PropertyKey.SECURITY_GROUP_MAPPING_CACHE_TIMEOUT_MS;
import PropertyKey.SECURITY_GROUP_MAPPING_CLASS;
import PropertyKey.SECURITY_LOGIN_IMPERSONATION_USERNAME;
import PropertyKey.SECURITY_LOGIN_USERNAME;
import PropertyKey.USER_METRICS_COLLECTION_ENABLED;
import alluxio.AlluxioURI;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemContext;
import alluxio.client.file.URIStatus;
import alluxio.conf.ServerConfiguration;
import alluxio.security.group.GroupMappingService;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Integration tests for user impersonation.
 */
public final class ImpersonationIntegrationTest extends BaseIntegrationTest {
    private static final String IMPERSONATION_USER = "impersonation_user";

    private static final String IMPERSONATION_GROUP1 = "impersonation_group1";

    private static final String IMPERSONATION_GROUP2 = "impersonation_group2";

    private static final String HDFS_USER = "hdfs_user";

    private static final String HDFS_GROUP1 = "hdfs_group1";

    private static final String HDFS_GROUP2 = "hdfs_group2";

    private static final String CONNECTION_USER = "alluxio_user";

    private static final String IMPERSONATION_GROUPS_CONFIG = "alluxio.master.security.impersonation.alluxio_user.groups";

    private static final String IMPERSONATION_USERS_CONFIG = "alluxio.master.security.impersonation.alluxio_user.users";

    private static final HashMap<String, String> GROUPS = new HashMap<>();

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(USER_METRICS_COLLECTION_ENABLED, false).setProperty(SECURITY_LOGIN_USERNAME, ImpersonationIntegrationTest.CONNECTION_USER).setProperty(SECURITY_GROUP_MAPPING_CACHE_TIMEOUT_MS, 0).setProperty(SECURITY_GROUP_MAPPING_CLASS, ImpersonationIntegrationTest.CustomGroupMapping.class.getName()).build();

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_GROUPS_CONFIG, "*" })
    public void impersonationNotUsed() throws Exception {
        ServerConfiguration.set(SECURITY_LOGIN_IMPERSONATION_USERNAME, IMPERSONATION_NONE);
        FileSystemContext context = FileSystemContext.create(createHdfsSubject(), ServerConfiguration.global());
        FileSystem fs = mLocalAlluxioClusterResource.get().getClient(context);
        fs.createFile(new AlluxioURI("/impersonation-test")).close();
        List<URIStatus> listing = fs.listStatus(new AlluxioURI("/"));
        Assert.assertEquals(1, listing.size());
        URIStatus status = listing.get(0);
        Assert.assertNotEquals(ImpersonationIntegrationTest.IMPERSONATION_USER, status.getOwner());
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_GROUPS_CONFIG, "*" })
    public void impersonationArbitraryUserDisallowed() throws Exception {
        String arbitraryUser = "arbitrary_user";
        ServerConfiguration.set(SECURITY_LOGIN_IMPERSONATION_USERNAME, arbitraryUser);
        FileSystemContext context = FileSystemContext.create(createHdfsSubject(), ServerConfiguration.global());
        FileSystem fs = mLocalAlluxioClusterResource.get().getClient(context);
        fs.createFile(new AlluxioURI("/impersonation-test")).close();
        List<URIStatus> listing = fs.listStatus(new AlluxioURI("/"));
        Assert.assertEquals(1, listing.size());
        URIStatus status = listing.get(0);
        Assert.assertNotEquals(arbitraryUser, status.getOwner());
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_GROUPS_CONFIG, "*" })
    public void impersonationUsedHdfsUser() throws Exception {
        // test using the hdfs subject
        checkCreateFile(createHdfsSubject(), ImpersonationIntegrationTest.HDFS_USER);
    }

    @Test
    public void impersonationHdfsDisabled() throws Exception {
        try {
            checkCreateFile(createHdfsSubject(), ImpersonationIntegrationTest.HDFS_USER);
            Assert.fail("Connection succeeded, but impersonation should be denied.");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_USERS_CONFIG, ImpersonationIntegrationTest.HDFS_USER })
    public void impersonationHdfsUserAllowed() throws Exception {
        checkCreateFile(createHdfsSubject(), ImpersonationIntegrationTest.HDFS_USER);
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_USERS_CONFIG, "wrong_user1,wrong_user2," + (ImpersonationIntegrationTest.HDFS_USER) })
    public void impersonationHdfsUsersAllowed() throws Exception {
        checkCreateFile(createHdfsSubject(), ImpersonationIntegrationTest.HDFS_USER);
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_USERS_CONFIG, "wrong_user" })
    public void impersonationHdfsUserDenied() throws Exception {
        try {
            checkCreateFile(createHdfsSubject(), ImpersonationIntegrationTest.HDFS_USER);
            Assert.fail("Connection succeeded, but impersonation should be denied.");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_USERS_CONFIG, ImpersonationIntegrationTest.HDFS_USER, ImpersonationIntegrationTest.IMPERSONATION_GROUPS_CONFIG, ImpersonationIntegrationTest.HDFS_GROUP1 })
    public void impersonationUsersAllowedGroupsAllowed() throws Exception {
        checkCreateFile(createHdfsSubject(), ImpersonationIntegrationTest.HDFS_USER);
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_USERS_CONFIG, "wrong_user", ImpersonationIntegrationTest.IMPERSONATION_GROUPS_CONFIG, ImpersonationIntegrationTest.HDFS_GROUP1 })
    public void impersonationUsersDeniedGroupsAllowed() throws Exception {
        checkCreateFile(createHdfsSubject(), ImpersonationIntegrationTest.HDFS_USER);
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_USERS_CONFIG, ImpersonationIntegrationTest.HDFS_USER, ImpersonationIntegrationTest.IMPERSONATION_GROUPS_CONFIG, "wrong_group" })
    public void impersonationUsersAllowedGroupsDenied() throws Exception {
        checkCreateFile(createHdfsSubject(), ImpersonationIntegrationTest.HDFS_USER);
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_USERS_CONFIG, "wrong_user", ImpersonationIntegrationTest.IMPERSONATION_GROUPS_CONFIG, "wrong_group" })
    public void impersonationUsersDeniedGroupsDenied() throws Exception {
        try {
            checkCreateFile(createHdfsSubject(), ImpersonationIntegrationTest.HDFS_USER);
            Assert.fail("Connection succeeded, but impersonation should be denied.");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_GROUPS_CONFIG, ImpersonationIntegrationTest.HDFS_GROUP2 })
    public void impersonationHdfsGroupAllowed() throws Exception {
        checkCreateFile(createHdfsSubject(), ImpersonationIntegrationTest.HDFS_USER);
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_GROUPS_CONFIG, ((((ImpersonationIntegrationTest.IMPERSONATION_GROUP1) + ",") + (ImpersonationIntegrationTest.IMPERSONATION_GROUP2)) + ",") + (ImpersonationIntegrationTest.HDFS_GROUP1) })
    public void impersonationHdfsGroupsAllowed() throws Exception {
        checkCreateFile(createHdfsSubject(), ImpersonationIntegrationTest.HDFS_USER);
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { ImpersonationIntegrationTest.IMPERSONATION_GROUPS_CONFIG, "wrong_group" })
    public void impersonationHdfsGroupDenied() throws Exception {
        try {
            checkCreateFile(createHdfsSubject(), ImpersonationIntegrationTest.HDFS_USER);
            Assert.fail("Connection succeeded, but impersonation should be denied.");
        } catch (IOException e) {
            // expected
        }
    }

    public static class CustomGroupMapping implements GroupMappingService {
        public CustomGroupMapping() {
        }

        @Override
        public List<String> getGroups(String user) {
            if (ImpersonationIntegrationTest.GROUPS.containsKey(user)) {
                return Lists.newArrayList(ImpersonationIntegrationTest.GROUPS.get(user).split(","));
            }
            return new ArrayList<>();
        }
    }
}

