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
package org.apache.hadoop.mapred.lib;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Random;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Path;
import org.apache.hadoop.mapred.Reporter;
import org.junit.Test;

import static Reporter.NULL;


public class TestLineInputFormat {
    private static int MAX_LENGTH = 200;

    private static JobConf defaultConf = new JobConf();

    private static java.io.FileSystem localFs = null;

    static {
        try {
            TestLineInputFormat.localFs = FileSystem.getLocal(TestLineInputFormat.defaultConf);
        } catch (IOException e) {
            throw new RuntimeException("init failure", e);
        }
    }

    private static Path workDir = new Path(new Path(System.getProperty("test.build.data", "."), "data"), "TestLineInputFormat");

    @Test
    public void testFormat() throws Exception {
        JobConf job = new JobConf();
        Path file = new Path(TestLineInputFormat.workDir, "test.txt");
        int seed = new Random().nextInt();
        Random random = new Random(seed);
        TestLineInputFormat.localFs.delete(TestLineInputFormat.workDir, true);
        FileInputFormat.setInputPaths(job, TestLineInputFormat.workDir);
        int numLinesPerMap = 5;
        job.setInt("mapreduce.input.lineinputformat.linespermap", numLinesPerMap);
        // for a variety of lengths
        for (int length = 0; length < (TestLineInputFormat.MAX_LENGTH); length += (random.nextInt(((TestLineInputFormat.MAX_LENGTH) / 10))) + 1) {
            // create a file with length entries
            Writer writer = new OutputStreamWriter(TestLineInputFormat.localFs.create(file));
            try {
                for (int i = 0; i < length; i++) {
                    writer.write(Integer.toString(i));
                    writer.write("\n");
                }
            } finally {
                writer.close();
            }
            checkFormat(job, numLinesPerMap);
        }
    }

    // A reporter that does nothing
    private static final Reporter voidReporter = NULL;
}

