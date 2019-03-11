/**
 * Copyright 2006-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.core.repository.support;


import DefaultTransactionDefinition.ISOLATION_READ_UNCOMMITTED;
import DefaultTransactionDefinition.ISOLATION_SERIALIZABLE;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.DefaultExecutionContextSerializer;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory;
import org.springframework.core.serializer.Serializer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;


/**
 *
 *
 * @author Lucas Ward
 * @author Will Schipp
 */
public class JobRepositoryFactoryBeanTests {
    private JobRepositoryFactoryBean factory;

    private DataFieldMaxValueIncrementerFactory incrementerFactory;

    private DataSource dataSource;

    private PlatformTransactionManager transactionManager;

    private String tablePrefix = "TEST_BATCH_PREFIX_";

    @Test
    public void testNoDatabaseType() throws Exception {
        DatabaseMetaData dmd = Mockito.mock(DatabaseMetaData.class);
        Connection con = Mockito.mock(Connection.class);
        Mockito.when(dataSource.getConnection()).thenReturn(con);
        Mockito.when(con.getMetaData()).thenReturn(dmd);
        Mockito.when(dmd.getDatabaseProductName()).thenReturn("Oracle");
        Mockito.when(incrementerFactory.isSupportedIncrementerType("ORACLE")).thenReturn(true);
        Mockito.when(incrementerFactory.getSupportedIncrementerTypes()).thenReturn(new String[0]);
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "STEP_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        factory.afterPropertiesSet();
        factory.getObject();
    }

    @Test
    public void testOracleLobHandler() throws Exception {
        factory.setDatabaseType("ORACLE");
        incrementerFactory = Mockito.mock(DataFieldMaxValueIncrementerFactory.class);
        Mockito.when(incrementerFactory.isSupportedIncrementerType("ORACLE")).thenReturn(true);
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "STEP_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        factory.setIncrementerFactory(incrementerFactory);
        factory.afterPropertiesSet();
        LobHandler lobHandler = ((LobHandler) (ReflectionTestUtils.getField(factory, "lobHandler")));
        Assert.assertTrue((lobHandler instanceof DefaultLobHandler));
    }

    @Test
    public void testCustomLobHandler() throws Exception {
        factory.setDatabaseType("ORACLE");
        incrementerFactory = Mockito.mock(DataFieldMaxValueIncrementerFactory.class);
        Mockito.when(incrementerFactory.isSupportedIncrementerType("ORACLE")).thenReturn(true);
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "STEP_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        factory.setIncrementerFactory(incrementerFactory);
        LobHandler lobHandler = new DefaultLobHandler();
        factory.setLobHandler(lobHandler);
        factory.afterPropertiesSet();
        Assert.assertEquals(lobHandler, ReflectionTestUtils.getField(factory, "lobHandler"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void tesDefaultSerializer() throws Exception {
        factory.setDatabaseType("ORACLE");
        incrementerFactory = Mockito.mock(DataFieldMaxValueIncrementerFactory.class);
        Mockito.when(incrementerFactory.isSupportedIncrementerType("ORACLE")).thenReturn(true);
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "STEP_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        factory.setIncrementerFactory(incrementerFactory);
        factory.afterPropertiesSet();
        Serializer<Map<String, Object>> serializer = ((Serializer<Map<String, Object>>) (ReflectionTestUtils.getField(factory, "serializer")));
        Assert.assertTrue((serializer instanceof Jackson2ExecutionContextStringSerializer));
    }

    @Test
    public void testCustomSerializer() throws Exception {
        factory.setDatabaseType("ORACLE");
        incrementerFactory = Mockito.mock(DataFieldMaxValueIncrementerFactory.class);
        Mockito.when(incrementerFactory.isSupportedIncrementerType("ORACLE")).thenReturn(true);
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "STEP_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        factory.setIncrementerFactory(incrementerFactory);
        ExecutionContextSerializer customSerializer = new DefaultExecutionContextSerializer();
        factory.setSerializer(customSerializer);
        factory.afterPropertiesSet();
        Assert.assertEquals(customSerializer, ReflectionTestUtils.getField(factory, "serializer"));
    }

    @Test
    public void testDefaultJdbcOperations() throws Exception {
        factory.setDatabaseType("ORACLE");
        incrementerFactory = Mockito.mock(DataFieldMaxValueIncrementerFactory.class);
        Mockito.when(incrementerFactory.isSupportedIncrementerType("ORACLE")).thenReturn(true);
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "STEP_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        factory.setIncrementerFactory(incrementerFactory);
        factory.afterPropertiesSet();
        JdbcOperations jdbcOperations = ((JdbcOperations) (ReflectionTestUtils.getField(factory, "jdbcOperations")));
        Assert.assertTrue((jdbcOperations instanceof JdbcTemplate));
    }

    @Test
    public void testCustomJdbcOperations() throws Exception {
        factory.setDatabaseType("ORACLE");
        incrementerFactory = Mockito.mock(DataFieldMaxValueIncrementerFactory.class);
        Mockito.when(incrementerFactory.isSupportedIncrementerType("ORACLE")).thenReturn(true);
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "JOB_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer("ORACLE", ((tablePrefix) + "STEP_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        factory.setIncrementerFactory(incrementerFactory);
        JdbcOperations customJdbcOperations = Mockito.mock(JdbcOperations.class);
        factory.setJdbcOperations(customJdbcOperations);
        factory.afterPropertiesSet();
        Assert.assertEquals(customJdbcOperations, ReflectionTestUtils.getField(factory, "jdbcOperations"));
    }

    @Test
    public void testMissingDataSource() throws Exception {
        factory.setDataSource(null);
        try {
            factory.afterPropertiesSet();
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            // expected
            String message = ex.getMessage();
            Assert.assertTrue(("Wrong message: " + message), message.contains("DataSource"));
        }
    }

    @Test
    public void testMissingTransactionManager() throws Exception {
        factory.setDatabaseType("mockDb");
        factory.setTransactionManager(null);
        try {
            Mockito.when(incrementerFactory.isSupportedIncrementerType("mockDb")).thenReturn(true);
            Mockito.when(incrementerFactory.getSupportedIncrementerTypes()).thenReturn(new String[0]);
            factory.afterPropertiesSet();
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            // expected
            String message = ex.getMessage();
            Assert.assertTrue(("Wrong message: " + message), message.contains("TransactionManager"));
        }
    }

    @Test
    public void testInvalidDatabaseType() throws Exception {
        factory.setDatabaseType("foo");
        try {
            Mockito.when(incrementerFactory.isSupportedIncrementerType("foo")).thenReturn(false);
            Mockito.when(incrementerFactory.getSupportedIncrementerTypes()).thenReturn(new String[0]);
            factory.afterPropertiesSet();
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            // expected
            String message = ex.getMessage();
            Assert.assertTrue(("Wrong message: " + message), message.contains("foo"));
        }
    }

    @Test
    public void testCreateRepository() throws Exception {
        String databaseType = "HSQL";
        factory.setDatabaseType(databaseType);
        Mockito.when(incrementerFactory.isSupportedIncrementerType("HSQL")).thenReturn(true);
        Mockito.when(incrementerFactory.getSupportedIncrementerTypes()).thenReturn(new String[0]);
        Mockito.when(incrementerFactory.getIncrementer(databaseType, ((tablePrefix) + "JOB_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer(databaseType, ((tablePrefix) + "JOB_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        Mockito.when(incrementerFactory.getIncrementer(databaseType, ((tablePrefix) + "STEP_EXECUTION_SEQ"))).thenReturn(new JobRepositoryFactoryBeanTests.StubIncrementer());
        factory.afterPropertiesSet();
        factory.getObject();
    }

    @Test
    public void testTransactionAttributesForCreateMethod() throws Exception {
        testCreateRepository();
        JobRepository repository = factory.getObject();
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionDefinition.setIsolationLevel(ISOLATION_SERIALIZABLE);
        Mockito.when(transactionManager.getTransaction(transactionDefinition)).thenReturn(null);
        Connection conn = Mockito.mock(Connection.class);
        Mockito.when(dataSource.getConnection()).thenReturn(conn);
        try {
            repository.createJobExecution("foo", new JobParameters());
            // we expect an exception but not from the txControl because we
            // provided the correct meta data
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected exception from DataSourceUtils
            Assert.assertEquals("No Statement specified", e.getMessage());
        }
    }

    @Test
    public void testSetTransactionAttributesForCreateMethod() throws Exception {
        factory.setIsolationLevelForCreate("ISOLATION_READ_UNCOMMITTED");
        testCreateRepository();
        JobRepository repository = factory.getObject();
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionDefinition.setIsolationLevel(ISOLATION_READ_UNCOMMITTED);
        Mockito.when(transactionManager.getTransaction(transactionDefinition)).thenReturn(null);
        Connection conn = Mockito.mock(Connection.class);
        Mockito.when(dataSource.getConnection()).thenReturn(conn);
        try {
            repository.createJobExecution("foo", new JobParameters());
            // we expect an exception but not from the txControl because we
            // provided the correct meta data
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected exception from DataSourceUtils
            Assert.assertEquals("No Statement specified", e.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCustomLobType() throws Exception {
        factory.setClobType(Integer.MAX_VALUE);
        testCreateRepository();
    }

    @Test
    public void testCustomLobType() throws Exception {
        factory.setClobType(Types.ARRAY);
        testCreateRepository();
        JobRepository repository = factory.getObject();
        Assert.assertNotNull(repository);
    }

    private static class StubIncrementer implements DataFieldMaxValueIncrementer {
        @Override
        public int nextIntValue() throws DataAccessException {
            return 0;
        }

        @Override
        public long nextLongValue() throws DataAccessException {
            return 0;
        }

        @Override
        public String nextStringValue() throws DataAccessException {
            return null;
        }
    }
}

