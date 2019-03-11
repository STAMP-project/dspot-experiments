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


import java.io.IOException;
import org.junit.Test;


/**
 * Test that streaming consumes stderr from the streaming process
 * (before, during, and after the main processing of mapred input),
 * and that stderr messages count as task progress.
 */
public class TestStreamingStderr {
    public TestStreamingStderr() throws IOException {
        UtilTest utilTest = new UtilTest(getClass().getName());
        utilTest.checkUserDir();
        utilTest.redirectIfAntJunit();
    }

    // This test will fail by blocking forever if the stderr isn't
    // consumed by Hadoop for tasks that don't have any input.
    @Test
    public void testStderrNoInput() throws Exception {
        runStreamJob("target/stderr-pre", false, 10000, 0, 0);
    }

    // Streaming should continue to read stderr even after all input has
    // been consumed.
    @Test
    public void testStderrAfterOutput() throws Exception {
        runStreamJob("target/stderr-post", false, 0, 0, 10000);
    }

    // This test should produce a task timeout if stderr lines aren't
    // counted as progress. This won't actually work until
    // LocalJobRunner supports timeouts.
    @Test
    public void testStderrCountsAsProgress() throws Exception {
        runStreamJob("target/stderr-progress", true, 10, 1000, 0);
    }
}

