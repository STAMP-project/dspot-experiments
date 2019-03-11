/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.transaction.xa;


import DatabaseType.MySQL;
import Status.STATUS_ACTIVE;
import Status.STATUS_NO_TRANSACTION;
import TransactionType.XA;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import javax.sql.XADataSource;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import org.apache.shardingsphere.transaction.core.ResourceDataSource;
import org.apache.shardingsphere.transaction.xa.jta.datasource.SingleXADataSource;
import org.apache.shardingsphere.transaction.xa.spi.SingleXAResource;
import org.apache.shardingsphere.transaction.xa.spi.XATransactionManager;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class XAShardingTransactionManagerTest {
    private XAShardingTransactionManager xaShardingTransactionManager = new XAShardingTransactionManager();

    @Mock
    private XATransactionManager xaTransactionManager;

    @Mock
    private TransactionManager transactionManager;

    @Test
    public void assertGetTransactionType() {
        Assert.assertThat(xaShardingTransactionManager.getTransactionType(), CoreMatchers.is(XA));
    }

    @Test
    public void assertRegisterXATransactionalDataSources() {
        Collection<ResourceDataSource> resourceDataSources = createResourceDataSources(DruidXADataSource.class, MySQL);
        xaShardingTransactionManager.init(MySQL, resourceDataSources);
        for (ResourceDataSource each : resourceDataSources) {
            Mockito.verify(xaTransactionManager).registerRecoveryResource(each.getUniqueResourceName(), ((XADataSource) (each.getDataSource())));
        }
    }

    @Test
    public void assertRegisterAtomikosDataSourceBeans() {
        xaShardingTransactionManager.init(MySQL, createAtomikosDataSourceBeanResource());
        Mockito.verify(xaTransactionManager, Mockito.times(0)).registerRecoveryResource(ArgumentMatchers.anyString(), ArgumentMatchers.any(XADataSource.class));
    }

    @Test
    public void assertRegisterNoneXATransactionalDAtaSources() {
        Collection<ResourceDataSource> resourceDataSources = createResourceDataSources(HikariDataSource.class, MySQL);
        xaShardingTransactionManager.init(MySQL, resourceDataSources);
        Map<String, SingleXADataSource> cachedXADatasourceMap = getCachedSingleXADataSourceMap();
        Assert.assertThat(cachedXADatasourceMap.size(), CoreMatchers.is(2));
    }

    @Test
    public void assertIsInTransaction() throws SystemException {
        Mockito.when(transactionManager.getStatus()).thenReturn(STATUS_ACTIVE);
        Assert.assertTrue(xaShardingTransactionManager.isInTransaction());
    }

    @Test
    public void assertIsNotInTransaction() throws SystemException {
        Mockito.when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION);
        Assert.assertFalse(xaShardingTransactionManager.isInTransaction());
    }

    @Test
    public void assertGetConnection() {
        setCachedSingleXADataSourceMap("ds1");
        Connection actual = xaShardingTransactionManager.getConnection("ds1");
        Assert.assertThat(actual, CoreMatchers.instanceOf(Connection.class));
        Mockito.verify(xaTransactionManager).enlistResource(ArgumentMatchers.any(SingleXAResource.class));
    }

    @Test
    public void assertClose() throws Exception {
        setCachedSingleXADataSourceMap("ds1");
        xaShardingTransactionManager.close();
        Map<String, SingleXADataSource> cachedSingleXADataSourceMap = getCachedSingleXADataSourceMap();
        Mockito.verify(xaTransactionManager).removeRecoveryResource(ArgumentMatchers.anyString(), ArgumentMatchers.any(XADataSource.class));
        Assert.assertThat(cachedSingleXADataSourceMap.size(), CoreMatchers.is(0));
    }
}

