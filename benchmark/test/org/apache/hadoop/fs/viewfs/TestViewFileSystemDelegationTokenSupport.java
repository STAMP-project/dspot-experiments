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
package org.apache.hadoop.fs.viewfs;


import FsConstants.VIEWFS_URI;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsConstants;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RawLocalFileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.Token;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test ViewFileSystem's support for having delegation tokens fetched and cached
 * for the file system.
 *
 * Currently this class just ensures that getCanonicalServiceName() always
 * returns <code>null</code> for ViewFileSystem instances.
 */
public class TestViewFileSystemDelegationTokenSupport {
    private static final String MOUNT_TABLE_NAME = "vfs-cluster";

    static Configuration conf;

    static FileSystem viewFs;

    static TestViewFileSystemDelegationTokenSupport.FakeFileSystem fs1;

    static TestViewFileSystemDelegationTokenSupport.FakeFileSystem fs2;

    /**
     * Regression test for HADOOP-8408.
     */
    @Test
    public void testGetCanonicalServiceNameWithNonDefaultMountTable() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        ConfigUtil.addLink(conf, TestViewFileSystemDelegationTokenSupport.MOUNT_TABLE_NAME, "/user", new URI("file:///"));
        FileSystem viewFs = FileSystem.get(new URI((((FsConstants.VIEWFS_SCHEME) + "://") + (TestViewFileSystemDelegationTokenSupport.MOUNT_TABLE_NAME))), conf);
        String serviceName = viewFs.getCanonicalServiceName();
        Assert.assertNull(serviceName);
    }

    @Test
    public void testGetCanonicalServiceNameWithDefaultMountTable() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        ConfigUtil.addLink(conf, "/user", new URI("file:///"));
        FileSystem viewFs = FileSystem.get(VIEWFS_URI, conf);
        String serviceName = viewFs.getCanonicalServiceName();
        Assert.assertNull(serviceName);
    }

    @Test
    public void testGetChildFileSystems() throws Exception {
        Assert.assertNull(getChildFileSystems());
        Assert.assertNull(getChildFileSystems());
        List<FileSystem> children = Arrays.asList(TestViewFileSystemDelegationTokenSupport.viewFs.getChildFileSystems());
        Assert.assertEquals(2, children.size());
        Assert.assertTrue(children.contains(TestViewFileSystemDelegationTokenSupport.fs1));
        Assert.assertTrue(children.contains(TestViewFileSystemDelegationTokenSupport.fs2));
    }

    @Test
    public void testAddDelegationTokens() throws Exception {
        Credentials creds = new Credentials();
        Token<?>[] fs1Tokens = addTokensWithCreds(TestViewFileSystemDelegationTokenSupport.fs1, creds);
        Assert.assertEquals(1, fs1Tokens.length);
        Assert.assertEquals(1, creds.numberOfTokens());
        Token<?>[] fs2Tokens = addTokensWithCreds(TestViewFileSystemDelegationTokenSupport.fs2, creds);
        Assert.assertEquals(1, fs2Tokens.length);
        Assert.assertEquals(2, creds.numberOfTokens());
        Credentials savedCreds = creds;
        creds = new Credentials();
        // should get the same set of tokens as explicitly fetched above
        Token<?>[] viewFsTokens = TestViewFileSystemDelegationTokenSupport.viewFs.addDelegationTokens("me", creds);
        Assert.assertEquals(2, viewFsTokens.length);
        Assert.assertTrue(creds.getAllTokens().containsAll(savedCreds.getAllTokens()));
        Assert.assertEquals(savedCreds.numberOfTokens(), creds.numberOfTokens());
        // should get none, already have all tokens
        viewFsTokens = TestViewFileSystemDelegationTokenSupport.viewFs.addDelegationTokens("me", creds);
        Assert.assertEquals(0, viewFsTokens.length);
        Assert.assertTrue(creds.getAllTokens().containsAll(savedCreds.getAllTokens()));
        Assert.assertEquals(savedCreds.numberOfTokens(), creds.numberOfTokens());
    }

    static class FakeFileSystem extends RawLocalFileSystem {
        URI uri;

        @Override
        public void initialize(URI name, Configuration conf) throws IOException {
            this.uri = name;
        }

        @Override
        public Path getInitialWorkingDirectory() {
            return new Path("/");// ctor calls getUri before the uri is inited...

        }

        @Override
        public URI getUri() {
            return uri;
        }

        @Override
        public String getCanonicalServiceName() {
            return String.valueOf((((this.getUri()) + "/") + (this.hashCode())));
        }

        @Override
        public Token<?> getDelegationToken(String renewer) throws IOException {
            Token<?> token = new Token<org.apache.hadoop.security.token.TokenIdentifier>();
            token.setService(new Text(getCanonicalServiceName()));
            return token;
        }

        @Override
        public void close() {
        }
    }
}

