/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.test.context.junit4;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.transaction.TransactionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * JUnit 4 based integration test which verifies
 * {@link BeforeTransaction @BeforeTransaction} and
 * {@link AfterTransaction @AfterTransaction} behavior.
 *
 * @author Sam Brannen
 * @since 2.5
 */
@Transactional
public class BeforeAndAfterTransactionAnnotationTests extends AbstractTransactionalSpringRunnerTests {
    protected static JdbcTemplate jdbcTemplate;

    protected static int numBeforeTransactionCalls = 0;

    protected static int numAfterTransactionCalls = 0;

    protected boolean inTransaction = false;

    @Rule
    public final TestName testName = new TestName();

    @Test
    public void transactionalMethod1() {
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertEquals("Adding jane", 1, AbstractTransactionalSpringRunnerTests.addPerson(BeforeAndAfterTransactionAnnotationTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.JANE));
        Assert.assertEquals("Verifying the number of rows in the person table within transactionalMethod1().", 2, AbstractTransactionalSpringRunnerTests.countRowsInPersonTable(BeforeAndAfterTransactionAnnotationTests.jdbcTemplate));
    }

    @Test
    public void transactionalMethod2() {
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertEquals("Adding jane", 1, AbstractTransactionalSpringRunnerTests.addPerson(BeforeAndAfterTransactionAnnotationTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.JANE));
        Assert.assertEquals("Adding sue", 1, AbstractTransactionalSpringRunnerTests.addPerson(BeforeAndAfterTransactionAnnotationTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.SUE));
        Assert.assertEquals("Verifying the number of rows in the person table within transactionalMethod2().", 3, AbstractTransactionalSpringRunnerTests.countRowsInPersonTable(BeforeAndAfterTransactionAnnotationTests.jdbcTemplate));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void nonTransactionalMethod() {
        TransactionTestUtils.assertInTransaction(false);
        Assert.assertEquals("Adding luke", 1, AbstractTransactionalSpringRunnerTests.addPerson(BeforeAndAfterTransactionAnnotationTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.LUKE));
        Assert.assertEquals("Adding leia", 1, AbstractTransactionalSpringRunnerTests.addPerson(BeforeAndAfterTransactionAnnotationTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.LEIA));
        Assert.assertEquals("Adding yoda", 1, AbstractTransactionalSpringRunnerTests.addPerson(BeforeAndAfterTransactionAnnotationTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.YODA));
        Assert.assertEquals("Verifying the number of rows in the person table without a transaction.", 3, AbstractTransactionalSpringRunnerTests.countRowsInPersonTable(BeforeAndAfterTransactionAnnotationTests.jdbcTemplate));
    }
}

