/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.test.context.jdbc;


import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


/**
 * Transactional integration tests for {@link Sql @Sql} that verify proper
 * support for {@link ExecutionPhase#AFTER_TEST_METHOD}.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(classes = EmptyDatabaseConfig.class)
@DirtiesContext
public class TransactionalAfterTestMethodSqlScriptsTests extends AbstractTransactionalJUnit4SpringContextTests {
    @Rule
    public TestName testName = new TestName();

    // test## is required for @FixMethodOrder.
    @Test
    @SqlGroup({ @Sql({ "schema.sql", "data.sql" }), @Sql(scripts = "drop-schema.sql", executionPhase = AFTER_TEST_METHOD) })
    public void test01() {
        assertNumUsers(1);
    }

    // test## is required for @FixMethodOrder.
    @Test
    @Sql({ "schema.sql", "data.sql", "data-add-dogbert.sql" })
    public void test02() {
        assertNumUsers(2);
    }
}

