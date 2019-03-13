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
 * This class tests hadoopStreaming in MapReduce local mode.
 * This testcase looks at different cases of tab position in input.
 */
public class TestStreamingKeyValue {
    protected File INPUT_FILE = new File("target/input.txt");

    protected File OUTPUT_DIR = new File("target/stream_out");

    // First line of input has 'key' 'tab' 'value'
    // Second line of input starts with a tab character.
    // So, it has empty key and the whole line as value.
    // Third line of input does not have any tab character.
    // So, the whole line is the key and value is empty.
    protected String input = "roses are \tred\t\n\tviolets are blue\nbunnies are pink\n" + ("this is for testing a big\tinput line\n" + "small input\n");

    protected String outputWithoutKey = "\tviolets are blue\nbunnies are pink\t\n" + (("roses are \tred\t\n" + "small input\t\n") + "this is for testing a big\tinput line\n");

    protected String outputWithKey = "0\troses are \tred\t\n" + ((("16\t\tviolets are blue\n" + "34\tbunnies are pink\n") + "51\tthis is for testing a big\tinput line\n") + "88\tsmall input\n");

    private StreamJob job;

    public TestStreamingKeyValue() throws IOException {
        UtilTest utilTest = new UtilTest(getClass().getName());
        utilTest.checkUserDir();
        utilTest.redirectIfAntJunit();
    }

    /**
     * Run the job with the indicating the input format key should be emitted.
     */
    @Test
    public void testCommandLineWithKey() throws Exception {
        runStreamJob(outputWithKey, false);
    }

    /**
     * Run the job the default way (the input format key is not emitted).
     */
    @Test
    public void testCommandLineWithoutKey() throws Exception {
        runStreamJob(outputWithoutKey, true);
    }
}

