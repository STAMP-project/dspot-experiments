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


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.DelegationTokenRenewer.Renewable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestDelegationTokenRenewer {
    public abstract class RenewableFileSystem extends FileSystem implements Renewable {}

    private static final long RENEW_CYCLE = 1000;

    private DelegationTokenRenewer renewer;

    Configuration conf;

    FileSystem fs;

    @Test
    public void testAddRemoveRenewAction() throws IOException, InterruptedException {
        Text service = new Text("myservice");
        Configuration conf = Mockito.mock(Configuration.class);
        Token<?> token = Mockito.mock(Token.class);
        Mockito.doReturn(service).when(token).getService();
        Mockito.doAnswer(new Answer<Long>() {
            public Long answer(InvocationOnMock invocation) {
                return (Time.now()) + (TestDelegationTokenRenewer.RENEW_CYCLE);
            }
        }).when(token).renew(ArgumentMatchers.any(Configuration.class));
        TestDelegationTokenRenewer.RenewableFileSystem fs = Mockito.mock(TestDelegationTokenRenewer.RenewableFileSystem.class);
        Mockito.doReturn(conf).when(fs).getConf();
        getRenewToken();
        renewer.addRenewAction(fs);
        Assert.assertEquals("FileSystem not added to DelegationTokenRenewer", 1, renewer.getRenewQueueLength());
        Thread.sleep(((TestDelegationTokenRenewer.RENEW_CYCLE) * 2));
        Mockito.verify(token, Mockito.atLeast(2)).renew(ArgumentMatchers.eq(conf));
        Mockito.verify(token, Mockito.atMost(3)).renew(ArgumentMatchers.eq(conf));
        Mockito.verify(token, Mockito.never()).cancel(ArgumentMatchers.any(Configuration.class));
        renewer.removeRenewAction(fs);
        Mockito.verify(token).cancel(ArgumentMatchers.eq(conf));
        getDelegationToken(null);
        setDelegationToken(ArgumentMatchers.any());
        Assert.assertEquals("FileSystem not removed from DelegationTokenRenewer", 0, renewer.getRenewQueueLength());
    }

    @Test
    public void testAddRenewActionWithNoToken() throws IOException, InterruptedException {
        Configuration conf = Mockito.mock(Configuration.class);
        TestDelegationTokenRenewer.RenewableFileSystem fs = Mockito.mock(TestDelegationTokenRenewer.RenewableFileSystem.class);
        Mockito.doReturn(conf).when(fs).getConf();
        getRenewToken();
        renewer.addRenewAction(fs);
        getRenewToken();
        Assert.assertEquals(0, renewer.getRenewQueueLength());
    }

    @Test
    public void testGetNewTokenOnRenewFailure() throws IOException, InterruptedException {
        Text service = new Text("myservice");
        Configuration conf = Mockito.mock(Configuration.class);
        final Token<?> token1 = Mockito.mock(Token.class);
        Mockito.doReturn(service).when(token1).getService();
        Mockito.doThrow(new IOException("boom")).when(token1).renew(ArgumentMatchers.eq(conf));
        final Token<?> token2 = Mockito.mock(Token.class);
        Mockito.doReturn(service).when(token2).getService();
        Mockito.doAnswer(new Answer<Long>() {
            public Long answer(InvocationOnMock invocation) {
                return (Time.now()) + (TestDelegationTokenRenewer.RENEW_CYCLE);
            }
        }).when(token2).renew(ArgumentMatchers.eq(conf));
        TestDelegationTokenRenewer.RenewableFileSystem fs = Mockito.mock(TestDelegationTokenRenewer.RenewableFileSystem.class);
        Mockito.doReturn(conf).when(fs).getConf();
        getRenewToken();
        getDelegationToken(null);
        addDelegationTokens(null, null);
        renewer.addRenewAction(fs);
        Assert.assertEquals(1, renewer.getRenewQueueLength());
        Thread.sleep(TestDelegationTokenRenewer.RENEW_CYCLE);
        getRenewToken();
        Mockito.verify(token1, Mockito.atLeast(1)).renew(ArgumentMatchers.eq(conf));
        Mockito.verify(token1, Mockito.atMost(2)).renew(ArgumentMatchers.eq(conf));
        addDelegationTokens(null, null);
        Mockito.verify(fs).setDelegationToken(ArgumentMatchers.eq(token2));
        Assert.assertEquals(1, renewer.getRenewQueueLength());
        renewer.removeRenewAction(fs);
        Mockito.verify(token2).cancel(ArgumentMatchers.eq(conf));
        Assert.assertEquals(0, renewer.getRenewQueueLength());
    }

    @Test
    public void testStopRenewalWhenFsGone() throws IOException, InterruptedException {
        Configuration conf = Mockito.mock(Configuration.class);
        Token<?> token = Mockito.mock(Token.class);
        Mockito.doReturn(new Text("myservice")).when(token).getService();
        Mockito.doAnswer(new Answer<Long>() {
            public Long answer(InvocationOnMock invocation) {
                return (Time.now()) + (TestDelegationTokenRenewer.RENEW_CYCLE);
            }
        }).when(token).renew(ArgumentMatchers.any(Configuration.class));
        TestDelegationTokenRenewer.RenewableFileSystem fs = Mockito.mock(TestDelegationTokenRenewer.RenewableFileSystem.class);
        Mockito.doReturn(conf).when(fs).getConf();
        getRenewToken();
        renewer.addRenewAction(fs);
        Assert.assertEquals(1, renewer.getRenewQueueLength());
        Thread.sleep(TestDelegationTokenRenewer.RENEW_CYCLE);
        Mockito.verify(token, Mockito.atLeast(1)).renew(ArgumentMatchers.eq(conf));
        Mockito.verify(token, Mockito.atMost(2)).renew(ArgumentMatchers.eq(conf));
        // drop weak ref
        fs = null;
        System.gc();
        System.gc();
        System.gc();
        // next renew should detect the fs as gone
        Thread.sleep(TestDelegationTokenRenewer.RENEW_CYCLE);
        Mockito.verify(token, Mockito.atLeast(1)).renew(ArgumentMatchers.eq(conf));
        Mockito.verify(token, Mockito.atMost(2)).renew(ArgumentMatchers.eq(conf));
        Assert.assertEquals(0, renewer.getRenewQueueLength());
    }

    @Test(timeout = 4000)
    public void testMultipleTokensDoNotDeadlock() throws IOException, InterruptedException {
        Configuration conf = Mockito.mock(Configuration.class);
        FileSystem fs = Mockito.mock(FileSystem.class);
        Mockito.doReturn(conf).when(fs).getConf();
        long distantFuture = (Time.now()) + (3600 * 1000);// 1h

        Token<?> token1 = Mockito.mock(Token.class);
        Mockito.doReturn(new Text("myservice1")).when(token1).getService();
        Mockito.doReturn(distantFuture).when(token1).renew(ArgumentMatchers.eq(conf));
        Token<?> token2 = Mockito.mock(Token.class);
        Mockito.doReturn(new Text("myservice2")).when(token2).getService();
        Mockito.doReturn(distantFuture).when(token2).renew(ArgumentMatchers.eq(conf));
        TestDelegationTokenRenewer.RenewableFileSystem fs1 = Mockito.mock(TestDelegationTokenRenewer.RenewableFileSystem.class);
        Mockito.doReturn(conf).when(fs1).getConf();
        getRenewToken();
        TestDelegationTokenRenewer.RenewableFileSystem fs2 = Mockito.mock(TestDelegationTokenRenewer.RenewableFileSystem.class);
        Mockito.doReturn(conf).when(fs2).getConf();
        getRenewToken();
        renewer.addRenewAction(fs1);
        renewer.addRenewAction(fs2);
        Assert.assertEquals(2, renewer.getRenewQueueLength());
        renewer.removeRenewAction(fs1);
        Assert.assertEquals(1, renewer.getRenewQueueLength());
        renewer.removeRenewAction(fs2);
        Assert.assertEquals(0, renewer.getRenewQueueLength());
        Mockito.verify(token1).cancel(ArgumentMatchers.eq(conf));
        Mockito.verify(token2).cancel(ArgumentMatchers.eq(conf));
    }
}

