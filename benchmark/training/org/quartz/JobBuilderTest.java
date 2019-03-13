/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.quartz;


import JobKey.DEFAULT_GROUP;
import junit.framework.TestCase;


/**
 * Test JobBuilder functionality
 */
public class JobBuilderTest extends TestCase {
    @SuppressWarnings("deprecation")
    public static class TestStatefulJob implements StatefulJob {
        public void execute(JobExecutionContext context) throws JobExecutionException {
        }
    }

    public static class TestJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
        }
    }

    @DisallowConcurrentExecution
    @PersistJobDataAfterExecution
    public static class TestAnnotatedJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
        }
    }

    public void testJobBuilder() throws Exception {
        JobDetail job = JobBuilder.newJob().ofType(JobBuilderTest.TestJob.class).withIdentity("j1").storeDurably().build();
        TestCase.assertTrue(("Unexpected job name: " + (job.getKey().getName())), job.getKey().getName().equals("j1"));
        TestCase.assertTrue(("Unexpected job group: " + (job.getKey().getGroup())), job.getKey().getGroup().equals(DEFAULT_GROUP));
        TestCase.assertTrue(("Unexpected job key: " + (job.getKey())), job.getKey().equals(JobKey.jobKey("j1")));
        TestCase.assertTrue(("Unexpected job description: " + (job.getDescription())), ((job.getDescription()) == null));
        TestCase.assertTrue("Expected isDurable == true ", job.isDurable());
        TestCase.assertFalse("Expected requestsRecovery == false ", job.requestsRecovery());
        TestCase.assertFalse("Expected isConcurrentExectionDisallowed == false ", job.isConcurrentExectionDisallowed());
        TestCase.assertFalse("Expected isPersistJobDataAfterExecution == false ", job.isPersistJobDataAfterExecution());
        TestCase.assertTrue(("Unexpected job class: " + (job.getJobClass())), job.getJobClass().equals(JobBuilderTest.TestJob.class));
        job = JobBuilder.newJob().ofType(JobBuilderTest.TestAnnotatedJob.class).withIdentity("j1").withDescription("my description").storeDurably(true).requestRecovery().build();
        TestCase.assertTrue(("Unexpected job description: " + (job.getDescription())), job.getDescription().equals("my description"));
        TestCase.assertTrue("Expected isDurable == true ", job.isDurable());
        TestCase.assertTrue("Expected requestsRecovery == true ", job.requestsRecovery());
        TestCase.assertTrue("Expected isConcurrentExectionDisallowed == true ", job.isConcurrentExectionDisallowed());
        TestCase.assertTrue("Expected isPersistJobDataAfterExecution == true ", job.isPersistJobDataAfterExecution());
        job = JobBuilder.newJob().ofType(JobBuilderTest.TestStatefulJob.class).withIdentity("j1", "g1").requestRecovery(false).build();
        TestCase.assertTrue(("Unexpected job group: " + (job.getKey().getName())), job.getKey().getGroup().equals("g1"));
        TestCase.assertFalse("Expected isDurable == false ", job.isDurable());
        TestCase.assertFalse("Expected requestsRecovery == false ", job.requestsRecovery());
        TestCase.assertTrue("Expected isConcurrentExectionDisallowed == true ", job.isConcurrentExectionDisallowed());
        TestCase.assertTrue("Expected isPersistJobDataAfterExecution == true ", job.isPersistJobDataAfterExecution());
    }
}

