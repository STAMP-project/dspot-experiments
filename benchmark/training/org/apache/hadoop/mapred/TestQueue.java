/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapred;


import DeprecatedQueueConfigurationParser.MAPRED_QUEUE_NAMES_KEY;
import QueueACL.ADMINISTER_JOBS;
import QueueACL.SUBMIT_JOB;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * TestCounters checks the sanity and recoverability of Queue
 */
public class TestQueue {
    private static File testDir = new File(System.getProperty("test.build.data", "/tmp"), TestJobConf.class.getSimpleName());

    /**
     * test QueueManager
     * configuration from file
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 5000)
    public void testQueue() throws IOException {
        File f = null;
        try {
            f = writeFile();
            QueueManager manager = new QueueManager(f.getCanonicalPath(), true);
            manager.setSchedulerInfo("first", "queueInfo");
            manager.setSchedulerInfo("second", "queueInfoqueueInfo");
            Queue root = manager.getRoot();
            Assert.assertTrue(((root.getChildren().size()) == 2));
            Iterator<Queue> iterator = root.getChildren().iterator();
            Queue firstSubQueue = iterator.next();
            Assert.assertEquals("first", firstSubQueue.getName());
            Assert.assertEquals(firstSubQueue.getAcls().get("mapred.queue.first.acl-submit-job").toString(), "Users [user1, user2] and members of the groups [group1, group2] are allowed");
            Queue secondSubQueue = iterator.next();
            Assert.assertEquals("second", secondSubQueue.getName());
            Assert.assertEquals(secondSubQueue.getProperties().getProperty("key"), "value");
            Assert.assertEquals(secondSubQueue.getProperties().getProperty("key1"), "value1");
            // test status
            Assert.assertEquals(firstSubQueue.getState().getStateName(), "running");
            Assert.assertEquals(secondSubQueue.getState().getStateName(), "stopped");
            Set<String> template = new HashSet<String>();
            template.add("first");
            template.add("second");
            Assert.assertEquals(manager.getLeafQueueNames(), template);
            // test user access
            UserGroupInformation mockUGI = Mockito.mock(UserGroupInformation.class);
            Mockito.when(mockUGI.getShortUserName()).thenReturn("user1");
            String[] groups = new String[]{ "group1" };
            Mockito.when(mockUGI.getGroupNames()).thenReturn(groups);
            Assert.assertTrue(manager.hasAccess("first", SUBMIT_JOB, mockUGI));
            Assert.assertFalse(manager.hasAccess("second", SUBMIT_JOB, mockUGI));
            Assert.assertFalse(manager.hasAccess("first", ADMINISTER_JOBS, mockUGI));
            Mockito.when(mockUGI.getShortUserName()).thenReturn("user3");
            Assert.assertTrue(manager.hasAccess("first", ADMINISTER_JOBS, mockUGI));
            QueueAclsInfo[] qai = manager.getQueueAcls(mockUGI);
            Assert.assertEquals(qai.length, 1);
            // test refresh queue
            manager.refreshQueues(getConfiguration(), null);
            iterator = root.getChildren().iterator();
            Queue firstSubQueue1 = iterator.next();
            Queue secondSubQueue1 = iterator.next();
            // tets equal method
            Assert.assertTrue(firstSubQueue.equals(firstSubQueue1));
            Assert.assertEquals(firstSubQueue1.getState().getStateName(), "running");
            Assert.assertEquals(secondSubQueue1.getState().getStateName(), "stopped");
            Assert.assertEquals(firstSubQueue1.getSchedulingInfo(), "queueInfo");
            Assert.assertEquals(secondSubQueue1.getSchedulingInfo(), "queueInfoqueueInfo");
            // test JobQueueInfo
            Assert.assertEquals(firstSubQueue.getJobQueueInfo().getQueueName(), "first");
            Assert.assertEquals(firstSubQueue.getJobQueueInfo().getQueueState(), "running");
            Assert.assertEquals(firstSubQueue.getJobQueueInfo().getSchedulingInfo(), "queueInfo");
            Assert.assertEquals(secondSubQueue.getJobQueueInfo().getChildren().size(), 0);
            // test
            Assert.assertEquals(manager.getSchedulerInfo("first"), "queueInfo");
            Set<String> queueJobQueueInfos = new HashSet<String>();
            for (JobQueueInfo jobInfo : manager.getJobQueueInfos()) {
                queueJobQueueInfos.add(jobInfo.getQueueName());
            }
            Set<String> rootJobQueueInfos = new HashSet<String>();
            for (Queue queue : root.getChildren()) {
                rootJobQueueInfos.add(queue.getJobQueueInfo().getQueueName());
            }
            Assert.assertEquals(queueJobQueueInfos, rootJobQueueInfos);
            // test getJobQueueInfoMapping
            Assert.assertEquals(manager.getJobQueueInfoMapping().get("first").getQueueName(), "first");
            // test dumpConfiguration
            Writer writer = new StringWriter();
            Configuration conf = getConfiguration();
            conf.unset(MAPRED_QUEUE_NAMES_KEY);
            QueueManager.dumpConfiguration(writer, f.getAbsolutePath(), conf);
            String result = writer.toString();
            Assert.assertTrue(((result.indexOf("\"name\":\"first\",\"state\":\"running\",\"acl_submit_job\":\"user1,user2 group1,group2\",\"acl_administer_jobs\":\"user3,user4 group3,group4\",\"properties\":[],\"children\":[]")) > 0));
            writer = new StringWriter();
            QueueManager.dumpConfiguration(writer, conf);
            result = writer.toString();
            Assert.assertTrue(result.contains("{\"queues\":[{\"name\":\"default\",\"state\":\"running\",\"acl_submit_job\":\"*\",\"acl_administer_jobs\":\"*\",\"properties\":[],\"children\":[]},{\"name\":\"q1\",\"state\":\"running\",\"acl_submit_job\":\" \",\"acl_administer_jobs\":\" \",\"properties\":[],\"children\":[{\"name\":\"q1:q2\",\"state\":\"running\",\"acl_submit_job\":\" \",\"acl_administer_jobs\":\" \",\"properties\":["));
            Assert.assertTrue(result.contains("{\"key\":\"capacity\",\"value\":\"20\"}"));
            Assert.assertTrue(result.contains("{\"key\":\"user-limit\",\"value\":\"30\"}"));
            Assert.assertTrue(result.contains("],\"children\":[]}]}]}"));
            // test constructor QueueAclsInfo
            QueueAclsInfo qi = new QueueAclsInfo();
            Assert.assertNull(qi.getQueueName());
        } finally {
            if (f != null) {
                f.delete();
            }
        }
    }

    @Test(timeout = 5000)
    public void testDefaultConfig() {
        QueueManager manager = new QueueManager(true);
        Assert.assertEquals(manager.getRoot().getChildren().size(), 2);
    }

    /**
     * test for Qmanager with empty configuration
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 5000)
    public void test2Queue() throws IOException {
        Configuration conf = getConfiguration();
        QueueManager manager = new QueueManager(conf);
        manager.setSchedulerInfo("first", "queueInfo");
        manager.setSchedulerInfo("second", "queueInfoqueueInfo");
        Queue root = manager.getRoot();
        // test children queues
        Assert.assertTrue(((root.getChildren().size()) == 2));
        Iterator<Queue> iterator = root.getChildren().iterator();
        Queue firstSubQueue = iterator.next();
        Assert.assertEquals("first", firstSubQueue.getName());
        Assert.assertEquals(firstSubQueue.getAcls().get("mapred.queue.first.acl-submit-job").toString(), "Users [user1, user2] and members of the groups [group1, group2] are allowed");
        Queue secondSubQueue = iterator.next();
        Assert.assertEquals("second", secondSubQueue.getName());
        Assert.assertEquals(firstSubQueue.getState().getStateName(), "running");
        Assert.assertEquals(secondSubQueue.getState().getStateName(), "stopped");
        Assert.assertTrue(manager.isRunning("first"));
        Assert.assertFalse(manager.isRunning("second"));
        Assert.assertEquals(firstSubQueue.getSchedulingInfo(), "queueInfo");
        Assert.assertEquals(secondSubQueue.getSchedulingInfo(), "queueInfoqueueInfo");
        // test leaf queue
        Set<String> template = new HashSet<String>();
        template.add("first");
        template.add("second");
        Assert.assertEquals(manager.getLeafQueueNames(), template);
    }
}

