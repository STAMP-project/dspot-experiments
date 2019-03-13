/**
 * Copyright 2008-2014 the original author or authors.
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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/simple-job-launcher-context.xml", "/jobs/compositeItemWriterSampleJob.xml", "/job-runner-context.xml" })
public class CompositeItemWriterSampleFunctionalTests {
    private static final String GET_TRADES = "SELECT isin, quantity, price, customer FROM TRADE order by isin";

    private static final String EXPECTED_OUTPUT_FILE = "Trade: [isin=UK21341EAH41,quantity=211,price=31.11,customer=customer1]" + ((("Trade: [isin=UK21341EAH42,quantity=212,price=32.11,customer=customer2]" + "Trade: [isin=UK21341EAH43,quantity=213,price=33.11,customer=customer3]") + "Trade: [isin=UK21341EAH44,quantity=214,price=34.11,customer=customer4]") + "Trade: [isin=UK21341EAH45,quantity=215,price=35.11,customer=customer5]");

    private JdbcOperations jdbcTemplate;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testJobLaunch() throws Exception {
        jdbcTemplate.update("DELETE from TRADE");
        int before = jdbcTemplate.queryForObject("SELECT COUNT(*) from TRADE", Integer.class);
        jobLauncherTestUtils.launchJob();
        checkOutputFile("build/test-outputs/CustomerReport1.txt");
        checkOutputFile("build/test-outputs/CustomerReport2.txt");
        checkOutputTable(before);
    }
}

