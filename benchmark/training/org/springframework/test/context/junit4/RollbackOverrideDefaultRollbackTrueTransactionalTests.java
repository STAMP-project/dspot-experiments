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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.transaction.TransactionTestUtils;


/**
 * Extension of {@link DefaultRollbackTrueRollbackAnnotationTransactionalTests}
 * which tests method-level <em>rollback override</em> behavior via the
 * {@link Rollback @Rollback} annotation.
 *
 * @author Sam Brannen
 * @since 2.5
 * @see Rollback
 */
public class RollbackOverrideDefaultRollbackTrueTransactionalTests extends DefaultRollbackTrueRollbackAnnotationTransactionalTests {
    private static JdbcTemplate jdbcTemplate;

    @Test
    @Rollback(false)
    @Override
    public void modifyTestDataWithinTransaction() {
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertEquals("Adding jane", 1, AbstractTransactionalSpringRunnerTests.addPerson(RollbackOverrideDefaultRollbackTrueTransactionalTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.JANE));
        Assert.assertEquals("Adding sue", 1, AbstractTransactionalSpringRunnerTests.addPerson(RollbackOverrideDefaultRollbackTrueTransactionalTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.SUE));
        Assert.assertEquals("Verifying the number of rows in the person table within a transaction.", 3, AbstractTransactionalSpringRunnerTests.countRowsInPersonTable(RollbackOverrideDefaultRollbackTrueTransactionalTests.jdbcTemplate));
    }
}

