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
package org.apache.hadoop.hdfs.nfs.nfs3;


import FileSystem.FS_DEFAULT_NAME_KEY;
import UserGroupInformation.AuthenticationMethod.PROXY;
import java.io.IOException;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.nfs.conf.NfsConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class TestDFSClientCache {
    @Test
    public void testEviction() throws IOException {
        NfsConfiguration conf = new NfsConfiguration();
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://localhost");
        // Only one entry will be in the cache
        final int MAX_CACHE_SIZE = 1;
        DFSClientCache cache = new DFSClientCache(conf, MAX_CACHE_SIZE);
        int namenodeId = Nfs3Utils.getNamenodeId(conf);
        DFSClient c1 = cache.getDfsClient("test1", namenodeId);
        Assert.assertTrue(cache.getDfsClient("test1", namenodeId).toString().contains("ugi=test1"));
        Assert.assertEquals(c1, cache.getDfsClient("test1", namenodeId));
        Assert.assertFalse(TestDFSClientCache.isDfsClientClose(c1));
        cache.getDfsClient("test2", namenodeId);
        Assert.assertTrue(TestDFSClientCache.isDfsClientClose(c1));
        Assert.assertTrue("cache size should be the max size or less", ((cache.getClientCache().size()) <= MAX_CACHE_SIZE));
    }

    @Test
    public void testGetUserGroupInformationSecure() throws IOException {
        String userName = "user1";
        String currentUser = "test-user";
        NfsConfiguration conf = new NfsConfiguration();
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://localhost");
        UserGroupInformation currentUserUgi = UserGroupInformation.createRemoteUser(currentUser);
        currentUserUgi.setAuthenticationMethod(AuthenticationMethod.KERBEROS);
        UserGroupInformation.setLoginUser(currentUserUgi);
        DFSClientCache cache = new DFSClientCache(conf);
        UserGroupInformation ugiResult = cache.getUserGroupInformation(userName, currentUserUgi);
        Assert.assertThat(ugiResult.getUserName(), Is.is(userName));
        Assert.assertThat(ugiResult.getRealUser(), Is.is(currentUserUgi));
        Assert.assertThat(ugiResult.getAuthenticationMethod(), Is.is(PROXY));
    }

    @Test
    public void testGetUserGroupInformation() throws IOException {
        String userName = "user1";
        String currentUser = "currentUser";
        UserGroupInformation currentUserUgi = UserGroupInformation.createUserForTesting(currentUser, new String[0]);
        NfsConfiguration conf = new NfsConfiguration();
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://localhost");
        DFSClientCache cache = new DFSClientCache(conf);
        UserGroupInformation ugiResult = cache.getUserGroupInformation(userName, currentUserUgi);
        Assert.assertThat(ugiResult.getUserName(), Is.is(userName));
        Assert.assertThat(ugiResult.getRealUser(), Is.is(currentUserUgi));
        Assert.assertThat(ugiResult.getAuthenticationMethod(), Is.is(PROXY));
    }
}

