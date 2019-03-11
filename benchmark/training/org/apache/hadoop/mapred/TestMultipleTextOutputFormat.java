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


import JobContext.TASK_ATTEMPT_ID;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;
import org.junit.Assert;
import org.junit.Test;

import static FileOutputCommitter.TEMP_DIR_NAME;


public class TestMultipleTextOutputFormat {
    private static JobConf defaultConf = new JobConf();

    private static FileSystem localFs = null;

    static {
        try {
            TestMultipleTextOutputFormat.localFs = FileSystem.getLocal(TestMultipleTextOutputFormat.defaultConf);
        } catch (IOException e) {
            throw new RuntimeException("init failure", e);
        }
    }

    // A random task attempt id for testing.
    private static String attempt = "attempt_200707121733_0001_m_000000_0";

    private static Path workDir = new Path(new Path(new Path(System.getProperty("test.build.data", "."), "data"), TEMP_DIR_NAME), ("_" + (TestMultipleTextOutputFormat.attempt)));

    static class KeyBasedMultipleTextOutputFormat extends MultipleTextOutputFormat<Text, Text> {
        protected String generateFileNameForKeyValue(Text key, Text v, String name) {
            return ((key.toString().substring(0, 1)) + "-") + name;
        }
    }

    @Test
    public void testFormat() throws Exception {
        JobConf job = new JobConf();
        job.set(TASK_ATTEMPT_ID, TestMultipleTextOutputFormat.attempt);
        FileOutputFormat.setOutputPath(job, TestMultipleTextOutputFormat.workDir.getParent().getParent());
        FileOutputFormat.setWorkOutputPath(job, TestMultipleTextOutputFormat.workDir);
        FileSystem fs = TestMultipleTextOutputFormat.workDir.getFileSystem(job);
        if (!(fs.mkdirs(TestMultipleTextOutputFormat.workDir))) {
            Assert.fail("Failed to create output directory");
        }
        // System.out.printf("workdir: %s\n", workDir.toString());
        TestMultipleTextOutputFormat.test1(job);
        TestMultipleTextOutputFormat.test2(job);
        String file_11 = "1-part-00000";
        File expectedFile_11 = new File(new Path(TestMultipleTextOutputFormat.workDir, file_11).toString());
        // System.out.printf("expectedFile_11: %s\n", new Path(workDir, file_11).toString());
        StringBuffer expectedOutput = new StringBuffer();
        for (int i = 10; i < 20; i++) {
            expectedOutput.append(("" + i)).append('\t').append(("" + i)).append("\n");
        }
        String output = UtilsForTests.slurp(expectedFile_11);
        // System.out.printf("File_2 output: %s\n", output);
        Assert.assertEquals(output, expectedOutput.toString());
        String file_12 = "2-part-00000";
        File expectedFile_12 = new File(new Path(TestMultipleTextOutputFormat.workDir, file_12).toString());
        // System.out.printf("expectedFile_12: %s\n", new Path(workDir, file_12).toString());
        expectedOutput = new StringBuffer();
        for (int i = 20; i < 30; i++) {
            expectedOutput.append(("" + i)).append('\t').append(("" + i)).append("\n");
        }
        output = UtilsForTests.slurp(expectedFile_12);
        // System.out.printf("File_2 output: %s\n", output);
        Assert.assertEquals(output, expectedOutput.toString());
        String file_13 = "3-part-00000";
        File expectedFile_13 = new File(new Path(TestMultipleTextOutputFormat.workDir, file_13).toString());
        // System.out.printf("expectedFile_13: %s\n", new Path(workDir, file_13).toString());
        expectedOutput = new StringBuffer();
        for (int i = 30; i < 40; i++) {
            expectedOutput.append(("" + i)).append('\t').append(("" + i)).append("\n");
        }
        output = UtilsForTests.slurp(expectedFile_13);
        // System.out.printf("File_2 output: %s\n", output);
        Assert.assertEquals(output, expectedOutput.toString());
        String file_2 = "2/3";
        File expectedFile_2 = new File(new Path(TestMultipleTextOutputFormat.workDir, file_2).toString());
        // System.out.printf("expectedFile_2: %s\n", new Path(workDir, file_2).toString());
        expectedOutput = new StringBuffer();
        for (int i = 10; i < 40; i++) {
            expectedOutput.append(("" + i)).append('\t').append(("" + i)).append("\n");
        }
        output = UtilsForTests.slurp(expectedFile_2);
        // System.out.printf("File_2 output: %s\n", output);
        Assert.assertEquals(output, expectedOutput.toString());
    }
}

