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
package org.apache.hadoop.mapreduce.v2;


import RMAppState.FAILED;
import RMAppState.FINISHED;
import RMAppState.KILLED;
import java.io.IOException;
import java.util.EnumSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMRJobsWithProfiler {
    private static final Logger LOG = LoggerFactory.getLogger(TestMRJobsWithProfiler.class);

    private static final EnumSet<RMAppState> TERMINAL_RM_APP_STATES = EnumSet.of(FINISHED, FAILED, KILLED);

    private static final int PROFILED_TASK_ID = 1;

    private static MiniMRYarnCluster mrCluster;

    private static final Configuration CONF = new Configuration();

    private static final java.io.FileSystem localFs;

    static {
        try {
            localFs = FileSystem.getLocal(TestMRJobsWithProfiler.CONF);
        } catch (IOException io) {
            throw new RuntimeException("problem getting local fs", io);
        }
    }

    private static final Path TEST_ROOT_DIR = new Path("target", ((TestMRJobs.class.getName()) + "-tmpDir")).makeQualified(getUri(), getWorkingDirectory());

    private static final Path APP_JAR = new Path(TestMRJobsWithProfiler.TEST_ROOT_DIR, "MRAppJar.jar");

    @Test(timeout = 150000)
    public void testDefaultProfiler() throws Exception {
        TestMRJobsWithProfiler.LOG.info("Starting testDefaultProfiler");
        testProfilerInternal(true);
    }

    @Test(timeout = 150000)
    public void testDifferentProfilers() throws Exception {
        TestMRJobsWithProfiler.LOG.info("Starting testDefaultProfiler");
        testProfilerInternal(false);
    }
}

