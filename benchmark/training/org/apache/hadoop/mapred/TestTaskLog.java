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


import LogName.DEBUGOUT;
import LogName.STDOUT;
import YarnConfiguration.YARN_APP_CONTAINER_LOG_DIR;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * TestCounters checks the sanity and recoverability of Queue
 */
public class TestTaskLog {
    private static final String testDirName = TestTaskLog.class.getSimpleName();

    private static final String testDir = ((System.getProperty("test.build.data", (("target" + (File.separatorChar)) + "test-dir"))) + (File.separatorChar)) + (TestTaskLog.testDirName);

    /**
     * test TaskAttemptID
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 50000)
    public void testTaskLog() throws IOException {
        // test TaskLog
        System.setProperty(YARN_APP_CONTAINER_LOG_DIR, "testString");
        Assert.assertEquals(TaskLog.getMRv2LogDir(), "testString");
        TaskAttemptID taid = Mockito.mock(TaskAttemptID.class);
        JobID jid = new JobID("job", 1);
        Mockito.when(taid.getJobID()).thenReturn(jid);
        Mockito.when(taid.toString()).thenReturn("JobId");
        File f = TaskLog.getTaskLogFile(taid, true, STDOUT);
        Assert.assertTrue(f.getAbsolutePath().endsWith((("testString" + (File.separatorChar)) + "stdout")));
        // test getRealTaskLogFileLocation
        File indexFile = TaskLog.getIndexFile(taid, true);
        if (!(indexFile.getParentFile().exists())) {
            indexFile.getParentFile().mkdirs();
        }
        indexFile.delete();
        indexFile.createNewFile();
        TaskLog.syncLogs(TestTaskLog.testDir, taid, true);
        Assert.assertTrue(indexFile.getAbsolutePath().endsWith((((((("userlogs" + (File.separatorChar)) + "job_job_0001") + (File.separatorChar)) + "JobId.cleanup") + (File.separatorChar)) + "log.index")));
        f = TaskLog.getRealTaskLogFileLocation(taid, true, DEBUGOUT);
        if (f != null) {
            Assert.assertTrue(f.getAbsolutePath().endsWith((((TestTaskLog.testDirName) + (File.separatorChar)) + "debugout")));
            FileUtils.copyFile(indexFile, f);
        }
        // test obtainLogDirOwner
        Assert.assertTrue(((TaskLog.obtainLogDirOwner(taid).length()) > 0));
        // test TaskLog.Reader
        Assert.assertTrue(((readTaskLog(TaskLog.LogName.DEBUGOUT, taid, true).length()) > 0));
    }

    /**
     * test without TASK_LOG_DIR
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 50000)
    public void testTaskLogWithoutTaskLogDir() throws IOException {
        // TaskLog tasklog= new TaskLog();
        System.clearProperty(YARN_APP_CONTAINER_LOG_DIR);
        // test TaskLog
        Assert.assertEquals(TaskLog.getMRv2LogDir(), null);
        TaskAttemptID taid = Mockito.mock(TaskAttemptID.class);
        JobID jid = new JobID("job", 1);
        Mockito.when(taid.getJobID()).thenReturn(jid);
        Mockito.when(taid.toString()).thenReturn("JobId");
        File f = TaskLog.getTaskLogFile(taid, true, STDOUT);
        Assert.assertTrue(f.getAbsolutePath().endsWith("stdout"));
    }
}

