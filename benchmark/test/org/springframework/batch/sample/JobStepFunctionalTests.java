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


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.support.PropertiesConverter;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Sample using a step to launch a job.
 *
 * @author Dave Syer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JobStepFunctionalTests {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    private JdbcOperations jdbcTemplate;

    @Test
    public void testJobLaunch() throws Exception {
        jdbcTemplate.update("DELETE FROM TRADE");
        jobLauncherTestUtils.launchJob(new DefaultJobParametersConverter().getJobParameters(PropertiesConverter.stringToProperties("run.id(long)=1,parameter=true,run.date=20070122,input.file=classpath:data/fixedLengthImportJob/input/20070122.teststream.ImportTradeDataStep.txt")));
        int after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM TRADE", Integer.class);
        Assert.assertEquals(5, after);
    }
}

