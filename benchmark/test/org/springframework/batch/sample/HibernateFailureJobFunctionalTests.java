/**
 * Copyright 2007-2014 the original author or authors.
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
package org.springframework.batch.sample;


import java.math.BigDecimal;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.sample.domain.trade.internal.CustomerCreditIncreaseProcessor;
import org.springframework.batch.sample.domain.trade.internal.HibernateCreditDao;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.orm.hibernate5.HibernateJdbcException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Test for HibernateJob - checks that customer credit has been updated to
 * expected value.
 *
 * @author Dave Syer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/simple-job-launcher-context.xml", "/hibernate-context.xml", "/jobs/hibernateJob.xml", "/job-runner-context.xml" })
public class HibernateFailureJobFunctionalTests {
    private static final BigDecimal CREDIT_INCREASE = CustomerCreditIncreaseProcessor.FIXED_AMOUNT;

    private static final String DELETE_CUSTOMERS = "DELETE FROM CUSTOMER";

    private static final String ALL_CUSTOMERS = "select * from CUSTOMER order by ID";

    private static final String CREDIT_COLUMN = "CREDIT";

    private static String[] customers = new String[]{ "INSERT INTO CUSTOMER (id, version, name, credit) VALUES (1, 0, 'customer1', 100000)", "INSERT INTO CUSTOMER (id, version, name, credit) VALUES (2, 0, 'customer2', 100000)", "INSERT INTO CUSTOMER (id, version, name, credit) VALUES (3, 0, 'customer3', 100000)", "INSERT INTO CUSTOMER (id, version, name, credit) VALUES (4, 0, 'customer4', 100000)" };

    protected static final String ID_COLUMN = "ID";

    @Autowired
    private HibernateCreditDao writer;

    private JdbcOperations jdbcTemplate;

    private PlatformTransactionManager transactionManager;

    private List<BigDecimal> creditsBeforeUpdate;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testLaunchJob() throws Exception {
        validatePreConditions();
        JobParameters params = new JobParametersBuilder().addString("key", "failureJob").toJobParameters();
        writer.setFailOnFlush(2);
        try {
            jobLauncherTestUtils.launchJob(params);
        } catch (HibernateJdbcException e) {
            // This is what would happen if the flush happened outside the
            // RepeatContext:
            throw e;
        } catch (UncategorizedSQLException e) {
            // This is what would happen if the job wasn't configured to skip
            // exceptions at the step level.
            // assertEquals(1, writer.getErrors().size());
            throw e;
        }
        int after = jdbcTemplate.queryForObject("SELECT COUNT(*) from CUSTOMER", Integer.class);
        Assert.assertEquals(4, after);
        validatePostConditions();
    }
}

