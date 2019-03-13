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
package org.springframework.test.context.junit4.rules;


import java.util.concurrent.TimeUnit;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.TransactionalSqlScriptsTests;


/**
 * This class is an extension of {@link TransactionalSqlScriptsTests}
 * that has been modified to use {@link SpringClassRule} and
 * {@link SpringMethodRule}.
 *
 * @author Sam Brannen
 * @since 4.2
 */
// Note: @FixMethodOrder is NOT @Inherited.
// Overriding @Sql declaration to reference scripts using relative path.
@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Sql({ "../../jdbc/schema.sql", "../../jdbc/data.sql" })
public class TransactionalSqlScriptsSpringRuleTests extends TransactionalSqlScriptsTests {
    @ClassRule
    public static final SpringClassRule springClassRule = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Rule
    public Timeout timeout = Timeout.builder().withTimeout(10, TimeUnit.SECONDS).build();

    /**
     * Redeclared to ensure that {@code @FixMethodOrder} is properly applied.
     */
    // test##_ prefix is required for @FixMethodOrder.
    @Test
    @Override
    public void test01_classLevelScripts() {
        assertNumUsers(1);
    }

    /**
     * Overriding {@code @Sql} declaration to reference scripts using relative path.
     */
    // test##_ prefix is required for @FixMethodOrder.
    @Test
    @Sql({ "../../jdbc/drop-schema.sql", "../../jdbc/schema.sql", "../../jdbc/data.sql", "../../jdbc/data-add-dogbert.sql" })
    @Override
    public void test02_methodLevelScripts() {
        assertNumUsers(2);
    }
}

