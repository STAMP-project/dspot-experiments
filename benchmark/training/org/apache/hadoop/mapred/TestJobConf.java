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


import JobConf.MAPREDUCE_JOB_MAP_MEMORY_MB_PROPERTY;
import JobConf.MAPREDUCE_JOB_REDUCE_MEMORY_MB_PROPERTY;
import JobConf.MAPRED_JOB_MAP_MEMORY_MB_PROPERTY;
import JobConf.MAPRED_JOB_REDUCE_MEMORY_MB_PROPERTY;
import JobConf.MAPRED_MAP_TASK_JAVA_OPTS;
import JobConf.MAPRED_REDUCE_TASK_JAVA_OPTS;
import JobConf.MAPRED_TASK_MAXVMEM_PROPERTY;
import JobPriority.DEFAULT;
import JobPriority.HIGH;
import JobPriority.LOW;
import JobPriority.NORMAL;
import JobPriority.UNDEFINED_PRIORITY;
import JobPriority.VERY_HIGH;
import MRJobConfig.DEFAULT_MAP_MEMORY_MB;
import MRJobConfig.DEFAULT_REDUCE_MEMORY_MB;
import MRJobConfig.MAP_MEMORY_MB;
import MRJobConfig.REDUCE_MEMORY_MB;
import MRJobConfig.TASK_PROFILE_PARAMS;
import java.util.regex.Pattern;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * test JobConf
 */
public class TestJobConf {
    /**
     * test getters and setters of JobConf
     */
    @SuppressWarnings("deprecation")
    @Test(timeout = 5000)
    public void testJobConf() {
        JobConf conf = new JobConf();
        // test default value
        Pattern pattern = conf.getJarUnpackPattern();
        Assert.assertEquals(Pattern.compile("(?:classes/|lib/).*").toString(), pattern.toString());
        // default value
        Assert.assertFalse(conf.getKeepFailedTaskFiles());
        conf.setKeepFailedTaskFiles(true);
        Assert.assertTrue(conf.getKeepFailedTaskFiles());
        // default value
        Assert.assertNull(conf.getKeepTaskFilesPattern());
        conf.setKeepTaskFilesPattern("123454");
        Assert.assertEquals("123454", conf.getKeepTaskFilesPattern());
        // default value
        Assert.assertNotNull(conf.getWorkingDirectory());
        conf.setWorkingDirectory(new Path("test"));
        Assert.assertTrue(conf.getWorkingDirectory().toString().endsWith("test"));
        // default value
        Assert.assertEquals(1, conf.getNumTasksToExecutePerJvm());
        // default value
        Assert.assertNull(conf.getKeyFieldComparatorOption());
        conf.setKeyFieldComparatorOptions("keySpec");
        Assert.assertEquals("keySpec", conf.getKeyFieldComparatorOption());
        // default value
        Assert.assertFalse(conf.getUseNewReducer());
        conf.setUseNewReducer(true);
        Assert.assertTrue(conf.getUseNewReducer());
        // default
        Assert.assertTrue(conf.getMapSpeculativeExecution());
        Assert.assertTrue(conf.getReduceSpeculativeExecution());
        Assert.assertTrue(conf.getSpeculativeExecution());
        conf.setReduceSpeculativeExecution(false);
        Assert.assertTrue(conf.getSpeculativeExecution());
        conf.setMapSpeculativeExecution(false);
        Assert.assertFalse(conf.getSpeculativeExecution());
        Assert.assertFalse(conf.getMapSpeculativeExecution());
        Assert.assertFalse(conf.getReduceSpeculativeExecution());
        conf.setSessionId("ses");
        Assert.assertEquals("ses", conf.getSessionId());
        Assert.assertEquals(3, conf.getMaxTaskFailuresPerTracker());
        conf.setMaxTaskFailuresPerTracker(2);
        Assert.assertEquals(2, conf.getMaxTaskFailuresPerTracker());
        Assert.assertEquals(0, conf.getMaxMapTaskFailuresPercent());
        conf.setMaxMapTaskFailuresPercent(50);
        Assert.assertEquals(50, conf.getMaxMapTaskFailuresPercent());
        Assert.assertEquals(0, conf.getMaxReduceTaskFailuresPercent());
        conf.setMaxReduceTaskFailuresPercent(70);
        Assert.assertEquals(70, conf.getMaxReduceTaskFailuresPercent());
        // by default
        Assert.assertEquals(DEFAULT.name(), conf.getJobPriority().name());
        conf.setJobPriority(HIGH);
        Assert.assertEquals(HIGH.name(), conf.getJobPriority().name());
        Assert.assertNull(conf.getJobSubmitHostName());
        conf.setJobSubmitHostName("hostname");
        Assert.assertEquals("hostname", conf.getJobSubmitHostName());
        // default
        Assert.assertNull(conf.getJobSubmitHostAddress());
        conf.setJobSubmitHostAddress("ww");
        Assert.assertEquals("ww", conf.getJobSubmitHostAddress());
        // default value
        Assert.assertFalse(conf.getProfileEnabled());
        conf.setProfileEnabled(true);
        Assert.assertTrue(conf.getProfileEnabled());
        // default value
        Assert.assertEquals(conf.getProfileTaskRange(true).toString(), "0-2");
        Assert.assertEquals(conf.getProfileTaskRange(false).toString(), "0-2");
        conf.setProfileTaskRange(true, "0-3");
        Assert.assertEquals(conf.getProfileTaskRange(false).toString(), "0-2");
        Assert.assertEquals(conf.getProfileTaskRange(true).toString(), "0-3");
        // default value
        Assert.assertNull(conf.getMapDebugScript());
        conf.setMapDebugScript("mDbgScript");
        Assert.assertEquals("mDbgScript", conf.getMapDebugScript());
        // default value
        Assert.assertNull(conf.getReduceDebugScript());
        conf.setReduceDebugScript("rDbgScript");
        Assert.assertEquals("rDbgScript", conf.getReduceDebugScript());
        // default value
        Assert.assertNull(conf.getJobLocalDir());
        Assert.assertEquals("default", conf.getQueueName());
        conf.setQueueName("qname");
        Assert.assertEquals("qname", conf.getQueueName());
        conf.setMemoryForMapTask((100 * 1000));
        Assert.assertEquals((100 * 1000), conf.getMemoryForMapTask());
        conf.setMemoryForReduceTask((1000 * 1000));
        Assert.assertEquals((1000 * 1000), conf.getMemoryForReduceTask());
        Assert.assertEquals((-1), conf.getMaxPhysicalMemoryForTask());
        Assert.assertEquals("The variable key is no longer used.", JobConf.deprecatedString("key"));
        // make sure mapreduce.map|reduce.java.opts are not set by default
        // so that they won't override mapred.child.java.opts
        Assert.assertEquals("mapreduce.map.java.opts should not be set by default", null, conf.get(MAPRED_MAP_TASK_JAVA_OPTS));
        Assert.assertEquals("mapreduce.reduce.java.opts should not be set by default", null, conf.get(MAPRED_REDUCE_TASK_JAVA_OPTS));
    }

    /**
     * Ensure that M/R 1.x applications can get and set task virtual memory with
     * old property names
     */
    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testDeprecatedPropertyNameForTaskVmem() {
        JobConf configuration = new JobConf();
        configuration.setLong(MAPRED_JOB_MAP_MEMORY_MB_PROPERTY, 1024);
        configuration.setLong(MAPRED_JOB_REDUCE_MEMORY_MB_PROPERTY, 1024);
        Assert.assertEquals(1024, configuration.getMemoryForMapTask());
        Assert.assertEquals(1024, configuration.getMemoryForReduceTask());
        // Make sure new property names aren't broken by the old ones
        configuration.setLong(MAPREDUCE_JOB_MAP_MEMORY_MB_PROPERTY, 1025);
        configuration.setLong(MAPREDUCE_JOB_REDUCE_MEMORY_MB_PROPERTY, 1025);
        Assert.assertEquals(1025, configuration.getMemoryForMapTask());
        Assert.assertEquals(1025, configuration.getMemoryForReduceTask());
        configuration.setMemoryForMapTask(2048);
        configuration.setMemoryForReduceTask(2048);
        Assert.assertEquals(2048, configuration.getLong(MAPRED_JOB_MAP_MEMORY_MB_PROPERTY, (-1)));
        Assert.assertEquals(2048, configuration.getLong(MAPRED_JOB_REDUCE_MEMORY_MB_PROPERTY, (-1)));
        // Make sure new property names aren't broken by the old ones
        Assert.assertEquals(2048, configuration.getLong(MAPREDUCE_JOB_MAP_MEMORY_MB_PROPERTY, (-1)));
        Assert.assertEquals(2048, configuration.getLong(MAPREDUCE_JOB_REDUCE_MEMORY_MB_PROPERTY, (-1)));
    }

    @Test
    public void testProfileParamsDefaults() {
        JobConf configuration = new JobConf();
        String result = configuration.getProfileParams();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("file=%s"));
        Assert.assertTrue(result.startsWith("-agentlib:hprof"));
    }

    @Test
    public void testProfileParamsSetter() {
        JobConf configuration = new JobConf();
        configuration.setProfileParams("test");
        Assert.assertEquals("test", configuration.get(TASK_PROFILE_PARAMS));
    }

    @Test
    public void testProfileParamsGetter() {
        JobConf configuration = new JobConf();
        configuration.set(TASK_PROFILE_PARAMS, "test");
        Assert.assertEquals("test", configuration.getProfileParams());
    }

    /**
     * Testing mapred.task.maxvmem replacement with new values
     */
    @Test
    public void testMemoryConfigForMapOrReduceTask() {
        JobConf configuration = new JobConf();
        configuration.set(MAP_MEMORY_MB, String.valueOf(300));
        configuration.set(REDUCE_MEMORY_MB, String.valueOf(300));
        Assert.assertEquals(configuration.getMemoryForMapTask(), 300);
        Assert.assertEquals(configuration.getMemoryForReduceTask(), 300);
        configuration.set("mapred.task.maxvmem", String.valueOf(((2 * 1024) * 1024)));
        configuration.set(MAP_MEMORY_MB, String.valueOf(300));
        configuration.set(REDUCE_MEMORY_MB, String.valueOf(300));
        Assert.assertEquals(configuration.getMemoryForMapTask(), 2);
        Assert.assertEquals(configuration.getMemoryForReduceTask(), 2);
        configuration = new JobConf();
        configuration.set("mapred.task.maxvmem", "-1");
        configuration.set(MAP_MEMORY_MB, String.valueOf(300));
        configuration.set(REDUCE_MEMORY_MB, String.valueOf(400));
        Assert.assertEquals(configuration.getMemoryForMapTask(), 300);
        Assert.assertEquals(configuration.getMemoryForReduceTask(), 400);
        configuration = new JobConf();
        configuration.set("mapred.task.maxvmem", String.valueOf(((2 * 1024) * 1024)));
        configuration.set(MAP_MEMORY_MB, "-1");
        configuration.set(REDUCE_MEMORY_MB, "-1");
        Assert.assertEquals(configuration.getMemoryForMapTask(), 2);
        Assert.assertEquals(configuration.getMemoryForReduceTask(), 2);
        configuration = new JobConf();
        configuration.set("mapred.task.maxvmem", String.valueOf((-1)));
        configuration.set(MAP_MEMORY_MB, "-1");
        configuration.set(REDUCE_MEMORY_MB, "-1");
        Assert.assertEquals(configuration.getMemoryForMapTask(), DEFAULT_MAP_MEMORY_MB);
        Assert.assertEquals(configuration.getMemoryForReduceTask(), DEFAULT_REDUCE_MEMORY_MB);
        configuration = new JobConf();
        configuration.set("mapred.task.maxvmem", String.valueOf(((2 * 1024) * 1024)));
        configuration.set(MAP_MEMORY_MB, "3");
        configuration.set(REDUCE_MEMORY_MB, "3");
        Assert.assertEquals(configuration.getMemoryForMapTask(), 2);
        Assert.assertEquals(configuration.getMemoryForReduceTask(), 2);
    }

    /**
     * Test that negative values for MAPRED_TASK_MAXVMEM_PROPERTY cause
     * new configuration keys' values to be used.
     */
    @Test
    public void testNegativeValueForTaskVmem() {
        JobConf configuration = new JobConf();
        configuration.set(MAPRED_TASK_MAXVMEM_PROPERTY, "-3");
        Assert.assertEquals(DEFAULT_MAP_MEMORY_MB, configuration.getMemoryForMapTask());
        Assert.assertEquals(DEFAULT_REDUCE_MEMORY_MB, configuration.getMemoryForReduceTask());
        configuration.set(MAP_MEMORY_MB, "4");
        configuration.set(REDUCE_MEMORY_MB, "5");
        Assert.assertEquals(4, configuration.getMemoryForMapTask());
        Assert.assertEquals(5, configuration.getMemoryForReduceTask());
    }

    /**
     * Test that negative values for new configuration keys get passed through.
     */
    @Test
    public void testNegativeValuesForMemoryParams() {
        JobConf configuration = new JobConf();
        configuration.set(MAP_MEMORY_MB, "-5");
        configuration.set(REDUCE_MEMORY_MB, "-6");
        Assert.assertEquals(DEFAULT_MAP_MEMORY_MB, configuration.getMemoryForMapTask());
        Assert.assertEquals(DEFAULT_REDUCE_MEMORY_MB, configuration.getMemoryForReduceTask());
    }

    /**
     * Test deprecated accessor and mutator method for mapred.task.maxvmem
     */
    @Test
    public void testMaxVirtualMemoryForTask() {
        JobConf configuration = new JobConf();
        // get test case
        configuration.set(MAP_MEMORY_MB, String.valueOf(300));
        configuration.set(REDUCE_MEMORY_MB, String.valueOf((-1)));
        Assert.assertEquals(configuration.getMaxVirtualMemoryForTask(), ((1024 * 1024) * 1024));
        configuration = new JobConf();
        configuration.set(MAP_MEMORY_MB, String.valueOf((-1)));
        configuration.set(REDUCE_MEMORY_MB, String.valueOf(200));
        Assert.assertEquals(configuration.getMaxVirtualMemoryForTask(), ((1024 * 1024) * 1024));
        configuration = new JobConf();
        configuration.set(MAP_MEMORY_MB, String.valueOf((-1)));
        configuration.set(REDUCE_MEMORY_MB, String.valueOf((-1)));
        configuration.set("mapred.task.maxvmem", String.valueOf(((1 * 1024) * 1024)));
        Assert.assertEquals(configuration.getMaxVirtualMemoryForTask(), ((1 * 1024) * 1024));
        configuration = new JobConf();
        configuration.set("mapred.task.maxvmem", String.valueOf(((1 * 1024) * 1024)));
        Assert.assertEquals(configuration.getMaxVirtualMemoryForTask(), ((1 * 1024) * 1024));
        // set test case
        configuration = new JobConf();
        configuration.setMaxVirtualMemoryForTask(((2 * 1024) * 1024));
        Assert.assertEquals(configuration.getMemoryForMapTask(), 2);
        Assert.assertEquals(configuration.getMemoryForReduceTask(), 2);
        configuration = new JobConf();
        configuration.set(MAP_MEMORY_MB, String.valueOf(300));
        configuration.set(REDUCE_MEMORY_MB, String.valueOf(400));
        configuration.setMaxVirtualMemoryForTask(((2 * 1024) * 1024));
        Assert.assertEquals(configuration.getMemoryForMapTask(), 2);
        Assert.assertEquals(configuration.getMemoryForReduceTask(), 2);
    }

    /**
     * Ensure that by default JobContext.MAX_TASK_FAILURES_PER_TRACKER is less
     * JobContext.MAP_MAX_ATTEMPTS and JobContext.REDUCE_MAX_ATTEMPTS so that
     * failed tasks will be retried on other nodes
     */
    @Test
    public void testMaxTaskFailuresPerTracker() {
        JobConf jobConf = new JobConf(true);
        Assert.assertTrue(("By default JobContext.MAX_TASK_FAILURES_PER_TRACKER was " + "not less than JobContext.MAP_MAX_ATTEMPTS and REDUCE_MAX_ATTEMPTS"), (((jobConf.getMaxTaskFailuresPerTracker()) < (jobConf.getMaxMapAttempts())) && ((jobConf.getMaxTaskFailuresPerTracker()) < (jobConf.getMaxReduceAttempts()))));
    }

    /**
     * Test parsing various types of Java heap options.
     */
    @Test
    public void testParseMaximumHeapSizeMB() {
        // happy cases
        Assert.assertEquals(4096, JobConf.parseMaximumHeapSizeMB("-Xmx4294967296"));
        Assert.assertEquals(4096, JobConf.parseMaximumHeapSizeMB("-Xmx4194304k"));
        Assert.assertEquals(4096, JobConf.parseMaximumHeapSizeMB("-Xmx4096m"));
        Assert.assertEquals(4096, JobConf.parseMaximumHeapSizeMB("-Xmx4g"));
        // sad cases
        Assert.assertEquals((-1), JobConf.parseMaximumHeapSizeMB("-Xmx4?"));
        Assert.assertEquals((-1), JobConf.parseMaximumHeapSizeMB(""));
    }

    /**
     * Test various Job Priority
     */
    @Test
    public void testJobPriorityConf() {
        JobConf conf = new JobConf();
        // by default
        Assert.assertEquals(DEFAULT.name(), conf.getJobPriority().name());
        Assert.assertEquals(0, conf.getJobPriorityAsInteger());
        // Set JobPriority.LOW using old API, and verify output from both getter
        conf.setJobPriority(LOW);
        Assert.assertEquals(LOW.name(), conf.getJobPriority().name());
        Assert.assertEquals(2, conf.getJobPriorityAsInteger());
        // Set JobPriority.VERY_HIGH using old API, and verify output
        conf.setJobPriority(VERY_HIGH);
        Assert.assertEquals(VERY_HIGH.name(), conf.getJobPriority().name());
        Assert.assertEquals(5, conf.getJobPriorityAsInteger());
        // Set 3 as priority using new API, and verify output from both getter
        conf.setJobPriorityAsInteger(3);
        Assert.assertEquals(NORMAL.name(), conf.getJobPriority().name());
        Assert.assertEquals(3, conf.getJobPriorityAsInteger());
        // Set 4 as priority using new API, and verify output
        conf.setJobPriorityAsInteger(4);
        Assert.assertEquals(HIGH.name(), conf.getJobPriority().name());
        Assert.assertEquals(4, conf.getJobPriorityAsInteger());
        // Now set some high integer values and verify output from old api
        conf.setJobPriorityAsInteger(57);
        Assert.assertEquals(UNDEFINED_PRIORITY.name(), conf.getJobPriority().name());
        Assert.assertEquals(57, conf.getJobPriorityAsInteger());
        // Error case where UNDEFINED_PRIORITY is set explicitly
        conf.setJobPriority(UNDEFINED_PRIORITY);
        Assert.assertEquals(UNDEFINED_PRIORITY.name(), conf.getJobPriority().name());
        // As UNDEFINED_PRIORITY cannot be mapped to any integer value, resetting
        // to default as 0.
        Assert.assertEquals(0, conf.getJobPriorityAsInteger());
    }
}

