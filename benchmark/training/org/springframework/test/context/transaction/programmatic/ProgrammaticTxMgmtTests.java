/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.test.context.transaction.programmatic;


import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.transaction.TransactionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * JUnit-based integration tests that verify support for programmatic transaction
 * management within the <em>Spring TestContext Framework</em>.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class ProgrammaticTxMgmtTests {
    private String sqlScriptEncoding;

    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected ApplicationContext applicationContext;

    @Rule
    public TestName testName = new TestName();

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void isActiveWithNonExistentTransactionContext() {
        Assert.assertFalse(TestTransaction.isActive());
    }

    @Test(expected = IllegalStateException.class)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void flagForRollbackWithNonExistentTransactionContext() {
        TestTransaction.flagForRollback();
    }

    @Test(expected = IllegalStateException.class)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void flagForCommitWithNonExistentTransactionContext() {
        TestTransaction.flagForCommit();
    }

    @Test(expected = IllegalStateException.class)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void isFlaggedForRollbackWithNonExistentTransactionContext() {
        TestTransaction.isFlaggedForRollback();
    }

    @Test(expected = IllegalStateException.class)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void startTxWithNonExistentTransactionContext() {
        TestTransaction.start();
    }

    @Test(expected = IllegalStateException.class)
    public void startTxWithExistingTransaction() {
        TestTransaction.start();
    }

    @Test(expected = IllegalStateException.class)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void endTxWithNonExistentTransactionContext() {
        TestTransaction.end();
    }

    @Test
    public void commitTxAndStartNewTx() {
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertTrue(TestTransaction.isActive());
        assertUsers("Dilbert");
        deleteFromTables("user");
        assertUsers();
        // Commit
        TestTransaction.flagForCommit();
        Assert.assertFalse(TestTransaction.isFlaggedForRollback());
        TestTransaction.end();
        TransactionTestUtils.assertInTransaction(false);
        Assert.assertFalse(TestTransaction.isActive());
        assertUsers();
        executeSqlScript("classpath:/org/springframework/test/context/jdbc/data-add-dogbert.sql", false);
        assertUsers("Dogbert");
        TestTransaction.start();
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertTrue(TestTransaction.isActive());
    }

    @Test
    public void commitTxButDoNotStartNewTx() {
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertTrue(TestTransaction.isActive());
        assertUsers("Dilbert");
        deleteFromTables("user");
        assertUsers();
        // Commit
        TestTransaction.flagForCommit();
        Assert.assertFalse(TestTransaction.isFlaggedForRollback());
        TestTransaction.end();
        Assert.assertFalse(TestTransaction.isActive());
        TransactionTestUtils.assertInTransaction(false);
        assertUsers();
        executeSqlScript("classpath:/org/springframework/test/context/jdbc/data-add-dogbert.sql", false);
        assertUsers("Dogbert");
    }

    @Test
    public void rollbackTxAndStartNewTx() {
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertTrue(TestTransaction.isActive());
        assertUsers("Dilbert");
        deleteFromTables("user");
        assertUsers();
        // Rollback (automatically)
        Assert.assertTrue(TestTransaction.isFlaggedForRollback());
        TestTransaction.end();
        Assert.assertFalse(TestTransaction.isActive());
        TransactionTestUtils.assertInTransaction(false);
        assertUsers("Dilbert");
        // Start new transaction with default rollback semantics
        TestTransaction.start();
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertTrue(TestTransaction.isFlaggedForRollback());
        Assert.assertTrue(TestTransaction.isActive());
        executeSqlScript("classpath:/org/springframework/test/context/jdbc/data-add-dogbert.sql", false);
        assertUsers("Dilbert", "Dogbert");
    }

    @Test
    public void rollbackTxButDoNotStartNewTx() {
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertTrue(TestTransaction.isActive());
        assertUsers("Dilbert");
        deleteFromTables("user");
        assertUsers();
        // Rollback (automatically)
        Assert.assertTrue(TestTransaction.isFlaggedForRollback());
        TestTransaction.end();
        Assert.assertFalse(TestTransaction.isActive());
        TransactionTestUtils.assertInTransaction(false);
        assertUsers("Dilbert");
    }

    @Test
    @Commit
    public void rollbackTxAndStartNewTxWithDefaultCommitSemantics() {
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertTrue(TestTransaction.isActive());
        assertUsers("Dilbert");
        deleteFromTables("user");
        assertUsers();
        // Rollback
        TestTransaction.flagForRollback();
        Assert.assertTrue(TestTransaction.isFlaggedForRollback());
        TestTransaction.end();
        Assert.assertFalse(TestTransaction.isActive());
        TransactionTestUtils.assertInTransaction(false);
        assertUsers("Dilbert");
        // Start new transaction with default commit semantics
        TestTransaction.start();
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertFalse(TestTransaction.isFlaggedForRollback());
        Assert.assertTrue(TestTransaction.isActive());
        executeSqlScript("classpath:/org/springframework/test/context/jdbc/data-add-dogbert.sql", false);
        assertUsers("Dilbert", "Dogbert");
    }

    // -------------------------------------------------------------------------
    @Configuration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public DataSource dataSource() {
            return // 
            // 
            // 
            new EmbeddedDatabaseBuilder().generateUniqueName(true).addScript("classpath:/org/springframework/test/context/jdbc/schema.sql").build();
        }
    }
}

