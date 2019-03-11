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
package org.apache.shardingsphere.transaction.spring.boot;


import TransactionType.LOCAL;
import TransactionType.XA;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.apache.shardingsphere.transaction.aspect.ShardingTransactionalAspect;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;
import org.apache.shardingsphere.transaction.spring.boot.fixture.ShardingTransactionalTestService;
import org.apache.shardingsphere.transaction.spring.boot.util.TransactionManagerMockUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ShardingTransactionalSpringBootTest.class)
@SpringBootApplication
@ComponentScan("org.apache.shardingsphere.transaction.spring.boot.fixture")
public class ShardingTransactionalSpringBootTest {
    @Autowired
    private ShardingTransactionalTestService testService;

    @Autowired
    private ShardingTransactionalAspect aspect;

    private final Statement statement = Mockito.mock(Statement.class);

    private final JpaTransactionManager jpaTransactionManager = Mockito.mock(JpaTransactionManager.class);

    private final DataSourceTransactionManager dataSourceTransactionManager = Mockito.mock(DataSourceTransactionManager.class);

    @Test
    public void assertChangeTransactionTypeToXA() {
        testService.testChangeTransactionTypeToXA();
        Assert.assertThat(TransactionTypeHolder.get(), CoreMatchers.is(LOCAL));
    }

    @Test
    public void assertChangeTransactionTypeToBASE() {
        testService.testChangeTransactionTypeToBASE();
        Assert.assertThat(TransactionTypeHolder.get(), CoreMatchers.is(LOCAL));
    }

    @Test
    public void assertChangeTransactionTypeToLocal() {
        TransactionTypeHolder.set(XA);
        testService.testChangeTransactionTypeToLOCAL();
        Assert.assertThat(TransactionTypeHolder.get(), CoreMatchers.is(LOCAL));
    }

    @Test
    public void assertChangeTransactionTypeInClass() {
        testService.testChangeTransactionTypeInClass();
        Assert.assertThat(TransactionTypeHolder.get(), CoreMatchers.is(LOCAL));
    }

    @Test(expected = ShardingException.class)
    public void assertChangeTransactionTypeForProxyWithIllegalTransactionManager() throws SQLException {
        TransactionManagerMockUtil.testChangeProxyTransactionTypeToLOCAL(testService, aspect, Mockito.mock(PlatformTransactionManager.class));
    }

    @Test(expected = ShardingException.class)
    public void assertChangeTransactionTypeForProxyFailed() throws SQLException {
        Mockito.when(statement.execute(ArgumentMatchers.anyString())).thenThrow(new SQLException("test switch exception"));
        TransactionManagerMockUtil.testChangeProxyTransactionTypeToLOCAL(testService, aspect, dataSourceTransactionManager);
    }

    @Test
    public void assertChangeTransactionTypeToLOCALForProxy() throws SQLException {
        Mockito.when(statement.execute(ArgumentMatchers.anyString())).thenReturn(true);
        TransactionManagerMockUtil.testChangeProxyTransactionTypeToLOCAL(testService, aspect, dataSourceTransactionManager);
        TransactionManagerMockUtil.testChangeProxyTransactionTypeToLOCAL(testService, aspect, jpaTransactionManager);
        Mockito.verify(statement, Mockito.times(2)).execute("SCTL:SET TRANSACTION_TYPE=LOCAL");
    }

    @Test
    public void assertChangeTransactionTypeToXAForProxy() throws SQLException {
        Mockito.when(statement.execute(ArgumentMatchers.anyString())).thenReturn(true);
        TransactionManagerMockUtil.testChangeProxyTransactionTypeToXA(testService, aspect, dataSourceTransactionManager);
        TransactionManagerMockUtil.testChangeProxyTransactionTypeToXA(testService, aspect, jpaTransactionManager);
        Mockito.verify(statement, Mockito.times(2)).execute("SCTL:SET TRANSACTION_TYPE=XA");
    }

    @Test
    public void assertChangeTransactionTypeToBASEForProxy() throws SQLException {
        Mockito.when(statement.execute(ArgumentMatchers.anyString())).thenReturn(true);
        TransactionManagerMockUtil.testChangeProxyTransactionTypeToBASE(testService, aspect, dataSourceTransactionManager);
        TransactionManagerMockUtil.testChangeProxyTransactionTypeToBASE(testService, aspect, jpaTransactionManager);
        Mockito.verify(statement, Mockito.times(2)).execute("SCTL:SET TRANSACTION_TYPE=BASE");
    }
}

