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
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.transaction.TransactionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * JUnit 4 based integration test which verifies support of Spring's
 * {@link Transactional &#64;Transactional}, {@link TestExecutionListeners
 * &#64;TestExecutionListeners}, and {@link ContextConfiguration
 * &#64;ContextConfiguration} annotations in conjunction with the
 * {@link SpringRunner} and the following
 * {@link TestExecutionListener TestExecutionListeners}:
 *
 * <ul>
 * <li>{@link DependencyInjectionTestExecutionListener}</li>
 * <li>{@link DirtiesContextTestExecutionListener}</li>
 * <li>{@link TransactionalTestExecutionListener}</li>
 * </ul>
 *
 * <p>This class specifically tests usage of {@code @Transactional} defined
 * at the <strong>class level</strong>.
 *
 * @author Sam Brannen
 * @since 2.5
 * @see MethodLevelTransactionalSpringRunnerTests
 */
@Transactional
public class ClassLevelTransactionalSpringRunnerTests extends AbstractTransactionalSpringRunnerTests {
    protected static JdbcTemplate jdbcTemplate;

    @Test
    public void modifyTestDataWithinTransaction() {
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertEquals("Deleting bob", 1, AbstractTransactionalSpringRunnerTests.deletePerson(ClassLevelTransactionalSpringRunnerTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.BOB));
        Assert.assertEquals("Adding jane", 1, AbstractTransactionalSpringRunnerTests.addPerson(ClassLevelTransactionalSpringRunnerTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.JANE));
        Assert.assertEquals("Adding sue", 1, AbstractTransactionalSpringRunnerTests.addPerson(ClassLevelTransactionalSpringRunnerTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.SUE));
        Assert.assertEquals("Verifying the number of rows in the person table within a transaction.", 2, AbstractTransactionalSpringRunnerTests.countRowsInPersonTable(ClassLevelTransactionalSpringRunnerTests.jdbcTemplate));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void modifyTestDataWithoutTransaction() {
        TransactionTestUtils.assertInTransaction(false);
        Assert.assertEquals("Adding luke", 1, AbstractTransactionalSpringRunnerTests.addPerson(ClassLevelTransactionalSpringRunnerTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.LUKE));
        Assert.assertEquals("Adding leia", 1, AbstractTransactionalSpringRunnerTests.addPerson(ClassLevelTransactionalSpringRunnerTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.LEIA));
        Assert.assertEquals("Adding yoda", 1, AbstractTransactionalSpringRunnerTests.addPerson(ClassLevelTransactionalSpringRunnerTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.YODA));
        Assert.assertEquals("Verifying the number of rows in the person table without a transaction.", 4, AbstractTransactionalSpringRunnerTests.countRowsInPersonTable(ClassLevelTransactionalSpringRunnerTests.jdbcTemplate));
    }
}

