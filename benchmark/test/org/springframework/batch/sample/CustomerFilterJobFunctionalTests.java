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
package org.springframework.batch.sample;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/simple-job-launcher-context.xml", "/jobs/customerFilterJob.xml", "/job-runner-context.xml" })
public class CustomerFilterJobFunctionalTests {
    private static final String GET_CUSTOMERS = "select NAME, CREDIT from CUSTOMER order by NAME";

    private List<CustomerFilterJobFunctionalTests.Customer> customers;

    private int activeRow = 0;

    private JdbcOperations jdbcTemplate;

    private Map<String, Double> credits = new HashMap<>();

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testFilterJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        customers = Arrays.asList(new CustomerFilterJobFunctionalTests.Customer("customer1", credits.get("customer1")), new CustomerFilterJobFunctionalTests.Customer("customer2", credits.get("customer2")), new CustomerFilterJobFunctionalTests.Customer("customer3", 100500), new CustomerFilterJobFunctionalTests.Customer("customer4", credits.get("customer4")), new CustomerFilterJobFunctionalTests.Customer("customer5", 32345), new CustomerFilterJobFunctionalTests.Customer("customer6", 123456));
        activeRow = 0;
        jdbcTemplate.query(CustomerFilterJobFunctionalTests.GET_CUSTOMERS, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                CustomerFilterJobFunctionalTests.Customer customer = customers.get(((activeRow)++));
                Assert.assertEquals(customer.getName(), rs.getString(1));
                Assert.assertEquals(customer.getCredit(), rs.getDouble(2), 0.01);
            }
        });
        Map<String, Object> step1Execution = this.getStepExecution(jobExecution, "uploadCustomer");
        Assert.assertEquals("4", step1Execution.get("READ_COUNT").toString());
        Assert.assertEquals("1", step1Execution.get("FILTER_COUNT").toString());
        Assert.assertEquals("3", step1Execution.get("WRITE_COUNT").toString());
    }

    private static class Customer {
        private String name;

        private double credit;

        public Customer(String name, double credit) {
            this.name = name;
            this.credit = credit;
        }

        /**
         *
         *
         * @return the credit
         */
        public double getCredit() {
            return credit;
        }

        /**
         *
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /* (non-Javadoc)

        @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits(credit);
            result = (PRIME * result) + ((int) (temp ^ (temp >>> 32)));
            result = (PRIME * result) + ((name) == null ? 0 : name.hashCode());
            return result;
        }

        /* (non-Javadoc)

        @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            final CustomerFilterJobFunctionalTests.Customer other = ((CustomerFilterJobFunctionalTests.Customer) (obj));
            if ((Double.doubleToLongBits(credit)) != (Double.doubleToLongBits(other.credit)))
                return false;

            if ((name) == null) {
                if ((other.name) != null)
                    return false;

            } else
                if (!(name.equals(other.name)))
                    return false;


            return true;
        }
    }
}

