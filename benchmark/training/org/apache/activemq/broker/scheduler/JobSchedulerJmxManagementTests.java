/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.broker.scheduler;


import java.util.List;
import javax.management.openmbean.TabularData;
import org.apache.activemq.broker.jmx.JobSchedulerViewMBean;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests of the JMX JobSchedulerStore management MBean.
 */
public class JobSchedulerJmxManagementTests extends JobSchedulerTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JobSchedulerJmxManagementTests.class);

    @Test
    public void testJobSchedulerMBeanIsRegistered() throws Exception {
        JobSchedulerViewMBean view = getJobSchedulerMBean();
        Assert.assertNotNull(view);
        Assert.assertTrue(view.getAllJobs().isEmpty());
    }

    @Test
    public void testGetNumberOfJobs() throws Exception {
        JobSchedulerViewMBean view = getJobSchedulerMBean();
        Assert.assertNotNull(view);
        Assert.assertTrue(view.getAllJobs().isEmpty());
        scheduleMessage(60000, (-1), (-1));
        Assert.assertFalse(view.getAllJobs().isEmpty());
        Assert.assertEquals(1, view.getAllJobs().size());
        scheduleMessage(60000, (-1), (-1));
        Assert.assertEquals(2, view.getAllJobs().size());
    }

    @Test
    public void testRemvoeJob() throws Exception {
        JobSchedulerViewMBean view = getJobSchedulerMBean();
        Assert.assertNotNull(view);
        Assert.assertTrue(view.getAllJobs().isEmpty());
        scheduleMessage(60000, (-1), (-1));
        Assert.assertFalse(view.getAllJobs().isEmpty());
        TabularData jobs = view.getAllJobs();
        Assert.assertEquals(1, jobs.size());
        for (Object key : jobs.keySet()) {
            String jobId = ((List<?>) (key)).get(0).toString();
            JobSchedulerJmxManagementTests.LOG.info("Attempting to remove Job: {}", jobId);
            view.removeJob(jobId);
        }
        Assert.assertTrue(view.getAllJobs().isEmpty());
    }

    @Test
    public void testRemvoeJobInRange() throws Exception {
        JobSchedulerViewMBean view = getJobSchedulerMBean();
        Assert.assertNotNull(view);
        Assert.assertTrue(view.getAllJobs().isEmpty());
        scheduleMessage(60000, (-1), (-1));
        Assert.assertFalse(view.getAllJobs().isEmpty());
        String now = JobSupport.getDateTime(System.currentTimeMillis());
        String later = JobSupport.getDateTime(((System.currentTimeMillis()) + (120 * 1000)));
        view.removeAllJobs(now, later);
        Assert.assertTrue(view.getAllJobs().isEmpty());
    }

    @Test
    public void testGetNextScheduledJob() throws Exception {
        JobSchedulerViewMBean view = getJobSchedulerMBean();
        Assert.assertNotNull(view);
        Assert.assertTrue(view.getAllJobs().isEmpty());
        scheduleMessage(60000, (-1), (-1));
        Assert.assertFalse(view.getAllJobs().isEmpty());
        long before = (System.currentTimeMillis()) + (57 * 1000);
        long toLate = (System.currentTimeMillis()) + (63 * 1000);
        String next = view.getNextScheduleTime();
        long nextTime = JobSupport.getDataTime(next);
        JobSchedulerJmxManagementTests.LOG.info("Next Scheduled Time: {} should be after: {}", next, JobSupport.getDateTime(before));
        Assert.assertTrue((nextTime > before));
        Assert.assertTrue((nextTime < toLate));
    }

    @Test
    public void testGetExecutionCount() throws Exception {
        final JobSchedulerViewMBean view = getJobSchedulerMBean();
        Assert.assertNotNull(view);
        Assert.assertTrue(view.getAllJobs().isEmpty());
        scheduleMessage(10000, 1000, 10);
        Assert.assertFalse(view.getAllJobs().isEmpty());
        TabularData jobs = view.getAllJobs();
        Assert.assertEquals(1, jobs.size());
        String jobId = null;
        for (Object key : jobs.keySet()) {
            jobId = ((List<?>) (key)).get(0).toString();
        }
        final String fixedJobId = jobId;
        JobSchedulerJmxManagementTests.LOG.info("Attempting to get execution count for Job: {}", jobId);
        Assert.assertEquals(0, view.getExecutionCount(jobId));
        Assert.assertTrue("Should execute again", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (view.getExecutionCount(fixedJobId)) > 0;
            }
        }));
    }
}

