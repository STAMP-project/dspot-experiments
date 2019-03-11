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
package org.apache.hadoop.security.alias;


import CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.ProviderUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH;
import static UserProvider.SCHEME_NAME;


public class TestCredentialProviderFactory {
    public static final Logger LOG = LoggerFactory.getLogger(TestCredentialProviderFactory.class);

    @Rule
    public final TestName test = new TestName();

    private static char[] chars = new char[]{ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9' };

    private static final File tmpDir = GenericTestUtils.getTestDir("creds");

    @Test
    public void testFactory() throws Exception {
        Configuration conf = new Configuration();
        final String userUri = (SCHEME_NAME) + ":///";
        final Path jksPath = new Path(TestCredentialProviderFactory.tmpDir.toString(), "test.jks");
        final String jksUri = ((JavaKeyStoreProvider.SCHEME_NAME) + "://file") + (jksPath.toUri());
        conf.set(CREDENTIAL_PROVIDER_PATH, ((userUri + ",") + jksUri));
        List<CredentialProvider> providers = CredentialProviderFactory.getProviders(conf);
        Assert.assertEquals(2, providers.size());
        Assert.assertEquals(UserProvider.class, providers.get(0).getClass());
        Assert.assertEquals(JavaKeyStoreProvider.class, providers.get(1).getClass());
        Assert.assertEquals(userUri, providers.get(0).toString());
        Assert.assertEquals(jksUri, providers.get(1).toString());
    }

    @Test
    public void testFactoryErrors() throws Exception {
        Configuration conf = new Configuration();
        conf.set(CREDENTIAL_PROVIDER_PATH, "unknown:///");
        try {
            List<CredentialProvider> providers = CredentialProviderFactory.getProviders(conf);
            Assert.assertTrue("should throw!", false);
        } catch (IOException e) {
            Assert.assertEquals(("No CredentialProviderFactory for unknown:/// in " + (CREDENTIAL_PROVIDER_PATH)), e.getMessage());
        }
    }

    @Test
    public void testUriErrors() throws Exception {
        Configuration conf = new Configuration();
        conf.set(CREDENTIAL_PROVIDER_PATH, "unkn@own:/x/y");
        try {
            List<CredentialProvider> providers = CredentialProviderFactory.getProviders(conf);
            Assert.assertTrue("should throw!", false);
        } catch (IOException e) {
            Assert.assertEquals((("Bad configuration of " + (CREDENTIAL_PROVIDER_PATH)) + " at unkn@own:/x/y"), e.getMessage());
        }
    }

    @Test
    public void testUserProvider() throws Exception {
        Configuration conf = new Configuration();
        final String ourUrl = (SCHEME_NAME) + ":///";
        conf.set(CREDENTIAL_PROVIDER_PATH, ourUrl);
        TestCredentialProviderFactory.checkSpecificProvider(conf, ourUrl);
        // see if the credentials are actually in the UGI
        Credentials credentials = UserGroupInformation.getCurrentUser().getCredentials();
        Assert.assertArrayEquals(new byte[]{ '1', '2', '3' }, credentials.getSecretKey(new Text("pass2")));
    }

    @Test
    public void testJksProvider() throws Exception {
        Configuration conf = new Configuration();
        final Path jksPath = new Path(TestCredentialProviderFactory.tmpDir.toString(), "test.jks");
        final String ourUrl = ((JavaKeyStoreProvider.SCHEME_NAME) + "://file") + (jksPath.toUri());
        File file = new File(TestCredentialProviderFactory.tmpDir, "test.jks");
        file.delete();
        conf.set(CREDENTIAL_PROVIDER_PATH, ourUrl);
        TestCredentialProviderFactory.checkSpecificProvider(conf, ourUrl);
        Path path = ProviderUtils.unnestUri(new URI(ourUrl));
        FileSystem fs = path.getFileSystem(conf);
        FileStatus s = fs.getFileStatus(path);
        Assert.assertEquals("rw-------", s.getPermission().toString());
        Assert.assertTrue((file + " should exist"), file.isFile());
        // check permission retention after explicit change
        fs.setPermission(path, new FsPermission("777"));
        checkPermissionRetention(conf, ourUrl, path);
    }

    @Test
    public void testLocalJksProvider() throws Exception {
        Configuration conf = new Configuration();
        final Path jksPath = new Path(TestCredentialProviderFactory.tmpDir.toString(), "test.jks");
        final String ourUrl = ((LocalJavaKeyStoreProvider.SCHEME_NAME) + "://file") + (jksPath.toUri());
        File file = new File(TestCredentialProviderFactory.tmpDir, "test.jks");
        file.delete();
        conf.set(CREDENTIAL_PROVIDER_PATH, ourUrl);
        TestCredentialProviderFactory.checkSpecificProvider(conf, ourUrl);
        Path path = ProviderUtils.unnestUri(new URI(ourUrl));
        FileSystem fs = path.getFileSystem(conf);
        FileStatus s = fs.getFileStatus(path);
        Assert.assertEquals(("Unexpected permissions: " + (s.getPermission().toString())), "rw-------", s.getPermission().toString());
        Assert.assertTrue((file + " should exist"), file.isFile());
        // check permission retention after explicit change
        fs.setPermission(path, new FsPermission("777"));
        checkPermissionRetention(conf, ourUrl, path);
    }
}

