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
package org.apache.hadoop.streaming;


import java.io.File;
import java.io.IOException;
import org.junit.Test;


/**
 * This class tests if hadoopStreaming fails a job when the mapper or
 * reducers have non-zero exit status and the
 * stream.non.zero.exit.status.is.failure jobconf is set.
 */
public class TestStreamingExitStatus {
    protected File TEST_DIR = new File("target/TestStreamingExitStatus").getAbsoluteFile();

    protected File INPUT_FILE = new File(TEST_DIR, "input.txt");

    protected File OUTPUT_DIR = new File(TEST_DIR, "out");

    protected String failingTask = UtilTest.makeJavaCommand(FailApp.class, new String[]{ "true" });

    protected String echoTask = UtilTest.makeJavaCommand(FailApp.class, new String[]{ "false" });

    public TestStreamingExitStatus() throws IOException {
        UtilTest utilTest = new UtilTest(getClass().getName());
        utilTest.checkUserDir();
        utilTest.redirectIfAntJunit();
    }

    @Test
    public void testMapFailOk() throws Exception {
        runStreamJob(false, true);
    }

    @Test
    public void testMapFailNotOk() throws Exception {
        runStreamJob(true, true);
    }

    @Test
    public void testReduceFailOk() throws Exception {
        runStreamJob(false, false);
    }

    @Test
    public void testReduceFailNotOk() throws Exception {
        runStreamJob(true, false);
    }
}

