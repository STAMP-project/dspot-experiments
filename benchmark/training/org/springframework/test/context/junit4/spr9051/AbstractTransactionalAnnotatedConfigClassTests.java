/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.test.context.junit4.spr9051;


import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.transaction.TransactionTestUtils;
import org.springframework.tests.sample.beans.Employee;
import org.springframework.transaction.annotation.Transactional;


/**
 * This set of tests (i.e., all concrete subclasses) investigates the claims made in
 * <a href="https://jira.spring.io/browse/SPR-9051" target="_blank">SPR-9051</a>
 * with regard to transactional tests.
 *
 * @author Sam Brannen
 * @since 3.2
 * @see org.springframework.test.context.testng.AnnotationConfigTransactionalTestNGSpringContextTests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AbstractTransactionalAnnotatedConfigClassTests {
    protected static final String JANE = "jane";

    protected static final String SUE = "sue";

    protected static final String YODA = "yoda";

    protected DataSource dataSourceFromTxManager;

    protected DataSource dataSourceViaInjection;

    protected JdbcTemplate jdbcTemplate;

    @Autowired
    private Employee employee;

    @Test
    public void autowiringFromConfigClass() {
        Assert.assertNotNull("The employee should have been autowired.", employee);
        Assert.assertEquals("John Smith", employee.getName());
    }

    @Test
    @Transactional
    public void modifyTestDataWithinTransaction() {
        TransactionTestUtils.assertInTransaction(true);
        assertAddPerson(AbstractTransactionalAnnotatedConfigClassTests.JANE);
        assertAddPerson(AbstractTransactionalAnnotatedConfigClassTests.SUE);
        assertNumRowsInPersonTable(3, "in modifyTestDataWithinTransaction()");
    }
}

