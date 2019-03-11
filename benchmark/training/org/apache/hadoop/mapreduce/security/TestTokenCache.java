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
package org.apache.hadoop.mapreduce.security;


import MRJobConfig.MAPREDUCE_JOB_CREDENTIALS_BINARY;
import YarnConfiguration.RM_PRINCIPAL;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper.MockFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.Master;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.Token;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestTokenCache {
    private static Configuration conf;

    private static String renewer;

    @Test
    public void testObtainTokens() throws Exception {
        Credentials credentials = new Credentials();
        FileSystem fs = Mockito.mock(FileSystem.class);
        TokenCache.obtainTokensForNamenodesInternal(fs, credentials, TestTokenCache.conf, TestTokenCache.renewer);
        Mockito.verify(fs).addDelegationTokens(ArgumentMatchers.eq(TestTokenCache.renewer), ArgumentMatchers.eq(credentials));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testBinaryCredentialsWithoutScheme() throws Exception {
        testBinaryCredentials(false);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testBinaryCredentialsWithScheme() throws Exception {
        testBinaryCredentials(true);
    }

    @Test
    public void testSingleTokenFetch() throws Exception {
        Configuration conf = new Configuration();
        conf.set(RM_PRINCIPAL, "mapred/host@REALM");
        String renewer = Master.getMasterPrincipal(conf);
        Credentials credentials = new Credentials();
        final MockFileSystem fs = new MockFileSystem();
        final MockFileSystem mockFs = ((MockFileSystem) (fs.getRawFileSystem()));
        Mockito.when(mockFs.getCanonicalServiceName()).thenReturn("host:0");
        Mockito.when(mockFs.getUri()).thenReturn(new URI("mockfs://host:0"));
        Path mockPath = Mockito.mock(Path.class);
        Mockito.when(mockPath.getFileSystem(conf)).thenReturn(mockFs);
        Path[] paths = new Path[]{ mockPath, mockPath };
        Mockito.when(mockFs.addDelegationTokens("me", credentials)).thenReturn(null);
        TokenCache.obtainTokensForNamenodesInternal(credentials, paths, conf);
        Mockito.verify(mockFs, Mockito.times(1)).addDelegationTokens(renewer, credentials);
    }

    @Test
    public void testCleanUpTokenReferral() throws Exception {
        Configuration conf = new Configuration();
        conf.set(MAPREDUCE_JOB_CREDENTIALS_BINARY, "foo");
        TokenCache.cleanUpTokenReferral(conf);
        Assert.assertNull(conf.get(MAPREDUCE_JOB_CREDENTIALS_BINARY));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetTokensForNamenodes() throws IOException, URISyntaxException {
        Path TEST_ROOT_DIR = new Path(System.getProperty("test.build.data", "test/build/data"));
        // ick, but need fq path minus file:/
        String binaryTokenFile = FileSystem.getLocal(TestTokenCache.conf).makeQualified(new Path(TEST_ROOT_DIR, "tokenFile")).toUri().getPath();
        MockFileSystem fs1 = createFileSystemForServiceName("service1");
        Credentials creds = new Credentials();
        Token<?> token1 = fs1.getDelegationToken(TestTokenCache.renewer);
        creds.addToken(token1.getService(), token1);
        // wait to set, else the obtain tokens call above will fail with FNF
        TestTokenCache.conf.set(MAPREDUCE_JOB_CREDENTIALS_BINARY, binaryTokenFile);
        creds.writeTokenStorageFile(new Path(binaryTokenFile), TestTokenCache.conf);
        TokenCache.obtainTokensForNamenodesInternal(fs1, creds, TestTokenCache.conf, TestTokenCache.renewer);
        String fs_addr = fs1.getCanonicalServiceName();
        Token<?> nnt = TokenCache.getDelegationToken(creds, fs_addr);
        Assert.assertNotNull("Token for nn is null", nnt);
    }
}

