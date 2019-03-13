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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.junit.Test;


/**
 * Tests if mapper/reducer with empty/nonempty input works properly if
 * reporting is done using lines like "reporter:status:" and
 * "reporter:counter:" before map()/reduce() method is called.
 * Validates the task's log of STDERR if messages are written to stderr before
 * map()/reduce() is called.
 * Also validates job output.
 * Uses MiniMR since the local jobtracker doesn't track task status.
 */
public class TestStreamingStatus {
    protected static String TEST_ROOT_DIR = new File(System.getProperty("test.build.data", "/tmp"), TestStreamingStatus.class.getSimpleName()).toURI().toString().replace(' ', '+');

    protected String INPUT_FILE = (TestStreamingStatus.TEST_ROOT_DIR) + "/input.txt";

    protected String OUTPUT_DIR = (TestStreamingStatus.TEST_ROOT_DIR) + "/out";

    protected String input = "roses.are.red\nviolets.are.blue\nbunnies.are.pink\n";

    protected String map = null;

    protected String reduce = null;

    protected String scriptFile = (TestStreamingStatus.TEST_ROOT_DIR) + "/perlScript.pl";

    protected String scriptFileName = new Path(scriptFile).toUri().getPath();

    String expectedStderr = "my error msg before consuming input\n" + "my error msg after consuming input\n";

    String expectedOutput = null;// inited in setUp()


    String expectedStatus = "before consuming input";

    // This script does the following
    // (a) setting task status before reading input
    // (b) writing to stderr before reading input and after reading input
    // (c) writing to stdout before reading input
    // (d) incrementing user counter before reading input and after reading input
    // Write lines to stdout before reading input{(c) above} is to validate
    // the hanging task issue when input to task is empty(because of not starting
    // output thread).
    protected String script = (((((((("#!/usr/bin/perl\n" + "print STDERR \"reporter:status:") + (expectedStatus)) + "\\n\";\n") + "print STDERR \"reporter:counter:myOwnCounterGroup,myOwnCounter,1\\n\";\n") + "print STDERR \"my error msg before consuming input\\n\";\n") + "for($count = 1500; $count >= 1; $count--) {print STDOUT \"$count \";}") + "while(<STDIN>) {chomp;}\n") + "print STDERR \"my error msg after consuming input\\n\";\n") + "print STDERR \"reporter:counter:myOwnCounterGroup,myOwnCounter,1\\n\";\n";

    MiniMRCluster mr = null;

    FileSystem fs = null;

    JobConf conf = null;

    /**
     * Check if mapper/reducer with empty/nonempty input works properly if
     * reporting is done using lines like "reporter:status:" and
     * "reporter:counter:" before map()/reduce() method is called.
     * Validate the task's log of STDERR if messages are written
     * to stderr before map()/reduce() is called.
     * Also validate job output.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testReporting() throws Exception {
        testStreamJob(false);// nonempty input

        testStreamJob(true);// empty input

    }
}

