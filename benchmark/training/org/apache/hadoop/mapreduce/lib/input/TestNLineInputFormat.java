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
package org.apache.hadoop.mapreduce.lib.input;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Test;


public class TestNLineInputFormat {
    private static int MAX_LENGTH = 200;

    private static Configuration conf = new Configuration();

    private static FileSystem localFs = null;

    static {
        try {
            TestNLineInputFormat.localFs = FileSystem.getLocal(TestNLineInputFormat.conf);
        } catch (IOException e) {
            throw new RuntimeException("init failure", e);
        }
    }

    private static Path workDir = new Path(new Path(System.getProperty("test.build.data", "."), "data"), "TestNLineInputFormat");

    @Test
    public void testFormat() throws Exception {
        Job job = Job.getInstance(TestNLineInputFormat.conf);
        Path file = new Path(TestNLineInputFormat.workDir, "test.txt");
        TestNLineInputFormat.localFs.delete(TestNLineInputFormat.workDir, true);
        FileInputFormat.setInputPaths(job, TestNLineInputFormat.workDir);
        int numLinesPerMap = 5;
        NLineInputFormat.setNumLinesPerSplit(job, numLinesPerMap);
        for (int length = 0; length < (TestNLineInputFormat.MAX_LENGTH); length += 1) {
            // create a file with length entries
            Writer writer = new OutputStreamWriter(TestNLineInputFormat.localFs.create(file));
            try {
                for (int i = 0; i < length; i++) {
                    writer.write(((Integer.toString(i)) + " some more text"));
                    writer.write("\n");
                }
            } finally {
                writer.close();
            }
            int lastN = 0;
            if (length != 0) {
                lastN = length % 5;
                if (lastN == 0) {
                    lastN = 5;
                }
            }
            checkFormat(job, numLinesPerMap, lastN);
        }
    }
}

