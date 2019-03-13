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
package org.apache.hadoop.fs;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.Token;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestFileSystemTokens {
    private static String renewer = "renewer!";

    @Test
    public void testFsWithNoToken() throws Exception {
        FileSystemTestHelper.MockFileSystem fs = TestFileSystemTokens.createFileSystemForServiceName(null);
        Credentials credentials = new Credentials();
        fs.addDelegationTokens(TestFileSystemTokens.renewer, credentials);
        verifyTokenFetch(fs, false);
        Assert.assertEquals(0, credentials.numberOfTokens());
    }

    @Test
    public void testFsWithToken() throws Exception {
        Text service = new Text("singleTokenFs");
        FileSystemTestHelper.MockFileSystem fs = TestFileSystemTokens.createFileSystemForServiceName(service);
        Credentials credentials = new Credentials();
        fs.addDelegationTokens(TestFileSystemTokens.renewer, credentials);
        verifyTokenFetch(fs, true);
        Assert.assertEquals(1, credentials.numberOfTokens());
        Assert.assertNotNull(credentials.getToken(service));
    }

    @Test
    public void testFsWithTokenExists() throws Exception {
        Credentials credentials = new Credentials();
        Text service = new Text("singleTokenFs");
        FileSystemTestHelper.MockFileSystem fs = TestFileSystemTokens.createFileSystemForServiceName(service);
        Token<?> token = Mockito.mock(Token.class);
        credentials.addToken(service, token);
        fs.addDelegationTokens(TestFileSystemTokens.renewer, credentials);
        verifyTokenFetch(fs, false);
        Assert.assertEquals(1, credentials.numberOfTokens());
        Assert.assertSame(token, credentials.getToken(service));
    }

    @Test
    public void testFsWithChildTokens() throws Exception {
        Credentials credentials = new Credentials();
        Text service1 = new Text("singleTokenFs1");
        Text service2 = new Text("singleTokenFs2");
        FileSystemTestHelper.MockFileSystem fs1 = TestFileSystemTokens.createFileSystemForServiceName(service1);
        FileSystemTestHelper.MockFileSystem fs2 = TestFileSystemTokens.createFileSystemForServiceName(service2);
        FileSystemTestHelper.MockFileSystem fs3 = TestFileSystemTokens.createFileSystemForServiceName(null);
        FileSystemTestHelper.MockFileSystem multiFs = TestFileSystemTokens.createFileSystemForServiceName(null, fs1, fs2, fs3);
        multiFs.addDelegationTokens(TestFileSystemTokens.renewer, credentials);
        verifyTokenFetch(multiFs, false);// has no tokens of own, only child tokens

        verifyTokenFetch(fs1, true);
        verifyTokenFetch(fs2, true);
        verifyTokenFetch(fs3, false);
        Assert.assertEquals(2, credentials.numberOfTokens());
        Assert.assertNotNull(credentials.getToken(service1));
        Assert.assertNotNull(credentials.getToken(service2));
    }

    @Test
    public void testFsWithDuplicateChildren() throws Exception {
        Credentials credentials = new Credentials();
        Text service = new Text("singleTokenFs1");
        FileSystemTestHelper.MockFileSystem fs = TestFileSystemTokens.createFileSystemForServiceName(service);
        FileSystemTestHelper.MockFileSystem multiFs = TestFileSystemTokens.createFileSystemForServiceName(null, fs, new FilterFileSystem(fs));
        multiFs.addDelegationTokens(TestFileSystemTokens.renewer, credentials);
        verifyTokenFetch(multiFs, false);
        verifyTokenFetch(fs, true);
        Assert.assertEquals(1, credentials.numberOfTokens());
        Assert.assertNotNull(credentials.getToken(service));
    }

    @Test
    public void testFsWithDuplicateChildrenTokenExists() throws Exception {
        Credentials credentials = new Credentials();
        Text service = new Text("singleTokenFs1");
        Token<?> token = Mockito.mock(Token.class);
        credentials.addToken(service, token);
        FileSystemTestHelper.MockFileSystem fs = TestFileSystemTokens.createFileSystemForServiceName(service);
        FileSystemTestHelper.MockFileSystem multiFs = TestFileSystemTokens.createFileSystemForServiceName(null, fs, new FilterFileSystem(fs));
        multiFs.addDelegationTokens(TestFileSystemTokens.renewer, credentials);
        verifyTokenFetch(multiFs, false);
        verifyTokenFetch(fs, false);
        Assert.assertEquals(1, credentials.numberOfTokens());
        Assert.assertSame(token, credentials.getToken(service));
    }

    @Test
    public void testFsWithChildTokensOneExists() throws Exception {
        Credentials credentials = new Credentials();
        Text service1 = new Text("singleTokenFs1");
        Text service2 = new Text("singleTokenFs2");
        Token<?> token = Mockito.mock(Token.class);
        credentials.addToken(service2, token);
        FileSystemTestHelper.MockFileSystem fs1 = TestFileSystemTokens.createFileSystemForServiceName(service1);
        FileSystemTestHelper.MockFileSystem fs2 = TestFileSystemTokens.createFileSystemForServiceName(service2);
        FileSystemTestHelper.MockFileSystem fs3 = TestFileSystemTokens.createFileSystemForServiceName(null);
        FileSystemTestHelper.MockFileSystem multiFs = TestFileSystemTokens.createFileSystemForServiceName(null, fs1, fs2, fs3);
        multiFs.addDelegationTokens(TestFileSystemTokens.renewer, credentials);
        verifyTokenFetch(multiFs, false);
        verifyTokenFetch(fs1, true);
        verifyTokenFetch(fs2, false);// we had added its token to credentials

        verifyTokenFetch(fs3, false);
        Assert.assertEquals(2, credentials.numberOfTokens());
        Assert.assertNotNull(credentials.getToken(service1));
        Assert.assertSame(token, credentials.getToken(service2));
    }

    @Test
    public void testFsWithMyOwnAndChildTokens() throws Exception {
        Credentials credentials = new Credentials();
        Text service1 = new Text("singleTokenFs1");
        Text service2 = new Text("singleTokenFs2");
        Text myService = new Text("multiTokenFs");
        Token<?> token = Mockito.mock(Token.class);
        credentials.addToken(service2, token);
        FileSystemTestHelper.MockFileSystem fs1 = TestFileSystemTokens.createFileSystemForServiceName(service1);
        FileSystemTestHelper.MockFileSystem fs2 = TestFileSystemTokens.createFileSystemForServiceName(service2);
        FileSystemTestHelper.MockFileSystem multiFs = TestFileSystemTokens.createFileSystemForServiceName(myService, fs1, fs2);
        multiFs.addDelegationTokens(TestFileSystemTokens.renewer, credentials);
        verifyTokenFetch(multiFs, true);// its own token and also of its children

        verifyTokenFetch(fs1, true);
        verifyTokenFetch(fs2, false);// we had added its token to credentials

        Assert.assertEquals(3, credentials.numberOfTokens());
        Assert.assertNotNull(credentials.getToken(myService));
        Assert.assertNotNull(credentials.getToken(service1));
        Assert.assertNotNull(credentials.getToken(service2));
    }

    @Test
    public void testFsWithMyOwnExistsAndChildTokens() throws Exception {
        Credentials credentials = new Credentials();
        Text service1 = new Text("singleTokenFs1");
        Text service2 = new Text("singleTokenFs2");
        Text myService = new Text("multiTokenFs");
        Token<?> token = Mockito.mock(Token.class);
        credentials.addToken(myService, token);
        FileSystemTestHelper.MockFileSystem fs1 = TestFileSystemTokens.createFileSystemForServiceName(service1);
        FileSystemTestHelper.MockFileSystem fs2 = TestFileSystemTokens.createFileSystemForServiceName(service2);
        FileSystemTestHelper.MockFileSystem multiFs = TestFileSystemTokens.createFileSystemForServiceName(myService, fs1, fs2);
        multiFs.addDelegationTokens(TestFileSystemTokens.renewer, credentials);
        verifyTokenFetch(multiFs, false);// we had added its token to credentials

        verifyTokenFetch(fs1, true);
        verifyTokenFetch(fs2, true);
        Assert.assertEquals(3, credentials.numberOfTokens());
        Assert.assertSame(token, credentials.getToken(myService));
        Assert.assertNotNull(credentials.getToken(service1));
        Assert.assertNotNull(credentials.getToken(service2));
    }

    @Test
    public void testFsWithNestedDuplicatesChildren() throws Exception {
        Credentials credentials = new Credentials();
        Text service1 = new Text("singleTokenFs1");
        Text service2 = new Text("singleTokenFs2");
        Text service4 = new Text("singleTokenFs4");
        Text multiService = new Text("multiTokenFs");
        Token<?> token2 = Mockito.mock(Token.class);
        credentials.addToken(service2, token2);
        FileSystemTestHelper.MockFileSystem fs1 = TestFileSystemTokens.createFileSystemForServiceName(service1);
        FileSystemTestHelper.MockFileSystem fs1B = TestFileSystemTokens.createFileSystemForServiceName(service1);
        FileSystemTestHelper.MockFileSystem fs2 = TestFileSystemTokens.createFileSystemForServiceName(service2);
        FileSystemTestHelper.MockFileSystem fs3 = TestFileSystemTokens.createFileSystemForServiceName(null);
        FileSystemTestHelper.MockFileSystem fs4 = TestFileSystemTokens.createFileSystemForServiceName(service4);
        // now let's get dirty!  ensure dup tokens aren't fetched even when
        // repeated and dupped in a nested fs.  fs4 is a real test of the drill
        // down: multi-filter-multi-filter-filter-fs4.
        FileSystemTestHelper.MockFileSystem multiFs = TestFileSystemTokens.createFileSystemForServiceName(multiService, fs1, fs1B, fs2, fs2, new FilterFileSystem(fs3), new FilterFileSystem(new FilterFileSystem(fs4)));
        FileSystemTestHelper.MockFileSystem superMultiFs = TestFileSystemTokens.createFileSystemForServiceName(null, fs1, fs1B, fs1, new FilterFileSystem(fs3), new FilterFileSystem(multiFs));
        superMultiFs.addDelegationTokens(TestFileSystemTokens.renewer, credentials);
        verifyTokenFetch(superMultiFs, false);// does not have its own token

        verifyTokenFetch(multiFs, true);// has its own token

        verifyTokenFetch(fs1, true);
        verifyTokenFetch(fs2, false);// we had added its token to credentials

        verifyTokenFetch(fs3, false);// has no tokens

        verifyTokenFetch(fs4, true);
        Assert.assertEquals(4, credentials.numberOfTokens());// fs1+fs2+fs4+multifs (fs3=0)

        Assert.assertNotNull(credentials.getToken(service1));
        Assert.assertNotNull(credentials.getToken(service2));
        Assert.assertSame(token2, credentials.getToken(service2));
        Assert.assertNotNull(credentials.getToken(multiService));
        Assert.assertNotNull(credentials.getToken(service4));
    }
}

