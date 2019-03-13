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
package org.apache.hadoop.hdfs;


import CreateEncryptionZoneFlag.NO_TRASH;
import java.io.File;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.EnumSet;
import org.apache.hadoop.crypto.key.kms.server.MiniKMS;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestWrapper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.client.CreateEncryptionZoneFlag;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test for HDFS encryption zone without external Kerberos KDC by leveraging
 * Kerby-based MiniKDC, MiniKMS and MiniDFSCluster. This provides additional
 * unit test coverage on Secure(Kerberos) KMS + HDFS.
 */
public class TestSecureEncryptionZoneWithKMS {
    public static final Logger LOG = LoggerFactory.getLogger(TestSecureEncryptionZoneWithKMS.class);

    private static final Path TEST_PATH = new Path("/test-dir");

    private static HdfsConfiguration baseConf;

    private static File baseDir;

    private static String keystoresDir;

    private static String sslConfDir;

    private static final EnumSet<CreateEncryptionZoneFlag> NO_TRASH = EnumSet.of(CreateEncryptionZoneFlag.NO_TRASH);

    private static final String HDFS_USER_NAME = "hdfs";

    private static final String SPNEGO_USER_NAME = "HTTP";

    private static final String OOZIE_USER_NAME = "oozie";

    private static final String OOZIE_PROXIED_USER_NAME = "oozie_user";

    private static String hdfsPrincipal;

    private static String spnegoPrincipal;

    private static String ooziePrincipal;

    private static String keytab;

    // MiniKDC
    private static MiniKdc kdc;

    // MiniKMS
    private static MiniKMS miniKMS;

    private final String testKey = "test_key";

    private static boolean testKeyCreated = false;

    private static final long AUTH_TOKEN_VALIDITY = 1;

    // MiniDFS
    private MiniDFSCluster cluster;

    private HdfsConfiguration conf;

    private FileSystem fs;

    private HdfsAdmin dfsAdmin;

    private FileSystemTestWrapper fsWrapper;

    @Rule
    public Timeout timeout = new Timeout(120000);

    @Test
    public void testSecureEncryptionZoneWithKMS() throws IOException, InterruptedException {
        final Path zonePath = new Path(TestSecureEncryptionZoneWithKMS.TEST_PATH, "TestEZ1");
        fsWrapper.mkdir(zonePath, FsPermission.getDirDefault(), true);
        fsWrapper.setOwner(zonePath, TestSecureEncryptionZoneWithKMS.OOZIE_PROXIED_USER_NAME, "supergroup");
        dfsAdmin.createEncryptionZone(zonePath, testKey, TestSecureEncryptionZoneWithKMS.NO_TRASH);
        UserGroupInformation oozieUgi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(TestSecureEncryptionZoneWithKMS.ooziePrincipal, TestSecureEncryptionZoneWithKMS.keytab);
        UserGroupInformation proxyUserUgi = UserGroupInformation.createProxyUser(TestSecureEncryptionZoneWithKMS.OOZIE_PROXIED_USER_NAME, oozieUgi);
        proxyUserUgi.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws IOException {
                // Get a client handler within the proxy user context for createFile
                try (DistributedFileSystem dfs = cluster.getFileSystem()) {
                    for (int i = 0; i < 3; i++) {
                        Path filePath = new Path(zonePath, (("testData." + i) + ".dat"));
                        DFSTestUtil.createFile(dfs, filePath, 1024, ((short) (3)), 1L);
                    }
                    return null;
                } catch (IOException e) {
                    throw new IOException(e);
                }
            }
        });
    }

    @Test
    public void testCreateZoneAfterAuthTokenExpiry() throws Exception {
        final UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(TestSecureEncryptionZoneWithKMS.hdfsPrincipal, TestSecureEncryptionZoneWithKMS.keytab);
        TestSecureEncryptionZoneWithKMS.LOG.info("Created ugi: {} ", ugi);
        ugi.doAs(((PrivilegedExceptionAction<Object>) (() -> {
            final Path zone = new Path("/expire1");
            fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
            dfsAdmin.createEncryptionZone(zone, testKey, TestSecureEncryptionZoneWithKMS.NO_TRASH);
            final Path zone1 = new Path("/expire2");
            fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
            final long sleepInterval = ((TestSecureEncryptionZoneWithKMS.AUTH_TOKEN_VALIDITY) + 1) * 1000;
            TestSecureEncryptionZoneWithKMS.LOG.info("Sleeping {} seconds to wait for kms auth token expiration", sleepInterval);
            Thread.sleep(sleepInterval);
            dfsAdmin.createEncryptionZone(zone1, testKey, TestSecureEncryptionZoneWithKMS.NO_TRASH);
            return null;
        })));
    }
}

