/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.internal.storage;


import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import java.util.Arrays;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionBridge;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.api.transaction.TransactionCheckBuilder;
import org.apache.curator.framework.api.transaction.TransactionCreateBuilder;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class JobNodeStorageTest {
    @Mock
    private CoordinatorRegistryCenter regCenter;

    private JobNodeStorage jobNodeStorage = new JobNodeStorage(regCenter, "test_job");

    @Test
    public void assertIsJobNodeExisted() {
        Mockito.when(regCenter.isExisted("/test_job/config")).thenReturn(true);
        Assert.assertTrue(jobNodeStorage.isJobNodeExisted("config"));
        Mockito.verify(regCenter).isExisted("/test_job/config");
    }

    @Test
    public void assertGetJobNodeData() {
        Mockito.when(regCenter.get("/test_job/config/cron")).thenReturn("0/1 * * * * ?");
        Assert.assertThat(jobNodeStorage.getJobNodeData("config/cron"), CoreMatchers.is("0/1 * * * * ?"));
        Mockito.verify(regCenter).get("/test_job/config/cron");
    }

    @Test
    public void assertGetJobNodeDataDirectly() {
        Mockito.when(regCenter.getDirectly("/test_job/config/cron")).thenReturn("0/1 * * * * ?");
        Assert.assertThat(jobNodeStorage.getJobNodeDataDirectly("config/cron"), CoreMatchers.is("0/1 * * * * ?"));
        Mockito.verify(regCenter).getDirectly("/test_job/config/cron");
    }

    @Test
    public void assertGetJobNodeChildrenKeys() {
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Arrays.asList("host0", "host1"));
        Assert.assertThat(jobNodeStorage.getJobNodeChildrenKeys("servers"), CoreMatchers.is(Arrays.asList("host0", "host1")));
        Mockito.verify(regCenter).getChildrenKeys("/test_job/servers");
    }

    @Test
    public void assertCreateJobNodeIfNeeded() {
        Mockito.when(regCenter.isExisted("/test_job")).thenReturn(true);
        Mockito.when(regCenter.isExisted("/test_job/config")).thenReturn(false);
        jobNodeStorage.createJobNodeIfNeeded("config");
        Mockito.verify(regCenter).isExisted("/test_job");
        Mockito.verify(regCenter).isExisted("/test_job/config");
        Mockito.verify(regCenter).persist("/test_job/config", "");
    }

    @Test
    public void assertCreateJobNodeIfRootJobNodeIsNotExist() {
        Mockito.when(regCenter.isExisted("/test_job")).thenReturn(false);
        Mockito.when(regCenter.isExisted("/test_job/config")).thenReturn(true);
        jobNodeStorage.createJobNodeIfNeeded("config");
        Mockito.verify(regCenter).isExisted("/test_job");
        Mockito.verify(regCenter, Mockito.times(0)).isExisted("/test_job/config");
        Mockito.verify(regCenter, Mockito.times(0)).persist("/test_job/config", "");
    }

    @Test
    public void assertCreateJobNodeIfNotNeeded() {
        Mockito.when(regCenter.isExisted("/test_job")).thenReturn(true);
        Mockito.when(regCenter.isExisted("/test_job/config")).thenReturn(true);
        jobNodeStorage.createJobNodeIfNeeded("config");
        Mockito.verify(regCenter).isExisted("/test_job");
        Mockito.verify(regCenter).isExisted("/test_job/config");
        Mockito.verify(regCenter, Mockito.times(0)).persist("/test_job/config", "");
    }

    @Test
    public void assertRemoveJobNodeIfNeeded() {
        Mockito.when(regCenter.isExisted("/test_job/config")).thenReturn(true);
        jobNodeStorage.removeJobNodeIfExisted("config");
        Mockito.verify(regCenter).isExisted("/test_job/config");
        Mockito.verify(regCenter).remove("/test_job/config");
    }

    @Test
    public void assertRemoveJobNodeIfNotNeeded() {
        Mockito.when(regCenter.isExisted("/test_job/config")).thenReturn(false);
        jobNodeStorage.removeJobNodeIfExisted("config");
        Mockito.verify(regCenter).isExisted("/test_job/config");
        Mockito.verify(regCenter, Mockito.times(0)).remove("/test_job/config");
    }

    @Test
    public void assertFillJobNode() {
        jobNodeStorage.fillJobNode("config/cron", "0/1 * * * * ?");
        Mockito.verify(regCenter).persist("/test_job/config/cron", "0/1 * * * * ?");
    }

    @Test
    public void assertFillEphemeralJobNode() {
        jobNodeStorage.fillEphemeralJobNode("config/cron", "0/1 * * * * ?");
        Mockito.verify(regCenter).persistEphemeral("/test_job/config/cron", "0/1 * * * * ?");
    }

    @Test
    public void assertUpdateJobNode() {
        jobNodeStorage.updateJobNode("config/cron", "0/1 * * * * ?");
        Mockito.verify(regCenter).update("/test_job/config/cron", "0/1 * * * * ?");
    }

    @Test
    public void assertReplaceJobNode() {
        jobNodeStorage.replaceJobNode("config/cron", "0/1 * * * * ?");
        Mockito.verify(regCenter).persist("/test_job/config/cron", "0/1 * * * * ?");
    }

    @Test
    public void assertExecuteInTransactionSuccess() throws Exception {
        CuratorFramework client = Mockito.mock(CuratorFramework.class);
        CuratorTransaction curatorTransaction = Mockito.mock(CuratorTransaction.class);
        TransactionCheckBuilder transactionCheckBuilder = Mockito.mock(TransactionCheckBuilder.class);
        CuratorTransactionBridge curatorTransactionBridge = Mockito.mock(CuratorTransactionBridge.class);
        CuratorTransactionFinal curatorTransactionFinal = Mockito.mock(CuratorTransactionFinal.class);
        Mockito.when(regCenter.getRawClient()).thenReturn(client);
        Mockito.when(client.inTransaction()).thenReturn(curatorTransaction);
        Mockito.when(curatorTransaction.check()).thenReturn(transactionCheckBuilder);
        Mockito.when(transactionCheckBuilder.forPath("/")).thenReturn(curatorTransactionBridge);
        Mockito.when(curatorTransactionBridge.and()).thenReturn(curatorTransactionFinal);
        TransactionCreateBuilder transactionCreateBuilder = Mockito.mock(TransactionCreateBuilder.class);
        Mockito.when(curatorTransactionFinal.create()).thenReturn(transactionCreateBuilder);
        Mockito.when(transactionCreateBuilder.forPath("/test_transaction")).thenReturn(curatorTransactionBridge);
        Mockito.when(curatorTransactionBridge.and()).thenReturn(curatorTransactionFinal);
        jobNodeStorage.executeInTransaction(new TransactionExecutionCallback() {
            @Override
            public void execute(final CuratorTransactionFinal curatorTransactionFinal) throws Exception {
                curatorTransactionFinal.create().forPath("/test_transaction").and();
            }
        });
        Mockito.verify(regCenter).getRawClient();
        Mockito.verify(client).inTransaction();
        Mockito.verify(curatorTransaction).check();
        Mockito.verify(transactionCheckBuilder).forPath("/");
        Mockito.verify(curatorTransactionBridge, Mockito.times(2)).and();
        Mockito.verify(curatorTransactionFinal).create();
        Mockito.verify(transactionCreateBuilder).forPath("/test_transaction");
        Mockito.verify(curatorTransactionFinal).commit();
    }

    @Test(expected = RuntimeException.class)
    public void assertExecuteInTransactionFailure() throws Exception {
        CuratorFramework client = Mockito.mock(CuratorFramework.class);
        CuratorTransaction curatorTransaction = Mockito.mock(CuratorTransaction.class);
        TransactionCheckBuilder transactionCheckBuilder = Mockito.mock(TransactionCheckBuilder.class);
        CuratorTransactionBridge curatorTransactionBridge = Mockito.mock(CuratorTransactionBridge.class);
        CuratorTransactionFinal curatorTransactionFinal = Mockito.mock(CuratorTransactionFinal.class);
        Mockito.when(regCenter.getRawClient()).thenReturn(client);
        Mockito.when(client.inTransaction()).thenReturn(curatorTransaction);
        Mockito.when(curatorTransaction.check()).thenReturn(transactionCheckBuilder);
        Mockito.when(transactionCheckBuilder.forPath("/")).thenReturn(curatorTransactionBridge);
        Mockito.when(curatorTransactionBridge.and()).thenReturn(curatorTransactionFinal);
        TransactionCreateBuilder transactionCreateBuilder = Mockito.mock(TransactionCreateBuilder.class);
        Mockito.when(curatorTransactionFinal.create()).thenReturn(transactionCreateBuilder);
        Mockito.when(transactionCreateBuilder.forPath("/test_transaction")).thenReturn(curatorTransactionBridge);
        Mockito.when(curatorTransactionBridge.and()).thenThrow(new RuntimeException());
        jobNodeStorage.executeInTransaction(new TransactionExecutionCallback() {
            @Override
            public void execute(final CuratorTransactionFinal curatorTransactionFinal) throws Exception {
                curatorTransactionFinal.create().forPath("/test_transaction").and();
            }
        });
        Mockito.verify(regCenter).getRawClient();
        Mockito.verify(client).inTransaction();
        Mockito.verify(curatorTransaction).check();
        Mockito.verify(transactionCheckBuilder).forPath("/");
        Mockito.verify(curatorTransactionBridge, Mockito.times(2)).and();
        Mockito.verify(curatorTransactionFinal).create();
        Mockito.verify(transactionCreateBuilder).forPath("/test_transaction");
        Mockito.verify(curatorTransactionFinal, Mockito.times(0)).commit();
    }

    @Test
    public void assertAddConnectionStateListener() {
        CuratorFramework client = Mockito.mock(CuratorFramework.class);
        @SuppressWarnings("unchecked")
        Listenable<ConnectionStateListener> listeners = Mockito.mock(Listenable.class);
        ConnectionStateListener listener = Mockito.mock(ConnectionStateListener.class);
        Mockito.when(client.getConnectionStateListenable()).thenReturn(listeners);
        Mockito.when(regCenter.getRawClient()).thenReturn(client);
        jobNodeStorage.addConnectionStateListener(listener);
        Mockito.verify(listeners).addListener(listener);
    }

    @Test
    public void assertAddDataListener() {
        TreeCache treeCache = Mockito.mock(TreeCache.class);
        @SuppressWarnings("unchecked")
        Listenable<TreeCacheListener> listeners = Mockito.mock(Listenable.class);
        TreeCacheListener listener = Mockito.mock(TreeCacheListener.class);
        Mockito.when(treeCache.getListenable()).thenReturn(listeners);
        Mockito.when(regCenter.getRawCache("/test_job")).thenReturn(treeCache);
        jobNodeStorage.addDataListener(listener);
        Mockito.verify(listeners).addListener(listener);
    }

    @Test
    public void assertGetRegistryCenterTime() {
        Mockito.when(regCenter.getRegistryCenterTime("/test_job/systemTime/current")).thenReturn(0L);
        Assert.assertThat(jobNodeStorage.getRegistryCenterTime(), CoreMatchers.is(0L));
        Mockito.verify(regCenter).getRegistryCenterTime("/test_job/systemTime/current");
    }
}

