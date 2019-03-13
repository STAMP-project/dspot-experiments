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


import FileSystem.FS_DEFAULT_NAME_KEY;
import Job.USE_WILDCARD_FOR_LIBJARS;
import MRConfig.FRAMEWORK_NAME;
import MRJobConfig.JOB_MAX_MAP;
import MRJobConfig.MR_ENCRYPTED_INTERMEDIATE_DATA;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.SleepJob;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;


/**
 * check for the job submission options of
 * -jt local -libjars
 */
public class TestLocalJobSubmission {
    private static Path TEST_ROOT_DIR = new Path(System.getProperty("test.build.data", "/tmp"));

    /**
     * Test the local job submission options of -jt local -libjars.
     *
     * @throws IOException
     * 		thrown if there's an error creating the JAR file
     */
    @Test
    public void testLocalJobLibjarsOption() throws IOException {
        Configuration conf = new Configuration();
        testLocalJobLibjarsOption(conf);
        conf.setBoolean(USE_WILDCARD_FOR_LIBJARS, false);
        testLocalJobLibjarsOption(conf);
    }

    /**
     * test the local job submission with
     * intermediate data encryption enabled.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testLocalJobEncryptedIntermediateData() throws IOException {
        Configuration conf = new Configuration();
        conf.set(FRAMEWORK_NAME, "local");
        conf.setBoolean(MR_ENCRYPTED_INTERMEDIATE_DATA, true);
        final String[] args = new String[]{ "-m", "1", "-r", "1", "-mt", "1", "-rt", "1" };
        int res = -1;
        try {
            res = ToolRunner.run(conf, new SleepJob(), args);
        } catch (Exception e) {
            System.out.println(("Job failed with " + (e.getLocalizedMessage())));
            e.printStackTrace(System.out);
            Assert.fail("Job failed");
        }
        Assert.assertEquals("dist job res is not 0:", 0, res);
    }

    /**
     * test JOB_MAX_MAP configuration.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testJobMaxMapConfig() throws Exception {
        Configuration conf = new Configuration();
        conf.set(FRAMEWORK_NAME, "local");
        conf.setInt(JOB_MAX_MAP, 0);
        final String[] args = new String[]{ "-m", "1", "-r", "1", "-mt", "1", "-rt", "1" };
        int res = -1;
        try {
            res = ToolRunner.run(conf, new SleepJob(), args);
            Assert.fail("Job should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getLocalizedMessage().contains("The number of map tasks 1 exceeded limit"));
        }
    }

    /**
     * Test local job submission with a file option.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testLocalJobFilesOption() throws IOException {
        Path jarPath = makeJar(new Path(TestLocalJobSubmission.TEST_ROOT_DIR, "test.jar"));
        Configuration conf = new Configuration();
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://localhost:9000");
        conf.set(FRAMEWORK_NAME, "local");
        final String[] args = new String[]{ "-jt", "local", "-files", jarPath.toString(), "-m", "1", "-r", "1", "-mt", "1", "-rt", "1" };
        int res = -1;
        try {
            res = ToolRunner.run(conf, new SleepJob(), args);
        } catch (Exception e) {
            System.out.println(("Job failed with " + (e.getLocalizedMessage())));
            e.printStackTrace(System.out);
            Assert.fail("Job failed");
        }
        Assert.assertEquals("dist job res is not 0:", 0, res);
    }

    /**
     * Test local job submission with an archive option.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testLocalJobArchivesOption() throws IOException {
        Path jarPath = makeJar(new Path(TestLocalJobSubmission.TEST_ROOT_DIR, "test.jar"));
        Configuration conf = new Configuration();
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://localhost:9000");
        conf.set(FRAMEWORK_NAME, "local");
        final String[] args = new String[]{ "-jt", "local", "-archives", jarPath.toString(), "-m", "1", "-r", "1", "-mt", "1", "-rt", "1" };
        int res = -1;
        try {
            res = ToolRunner.run(conf, new SleepJob(), args);
        } catch (Exception e) {
            System.out.println(("Job failed with " + (e.getLocalizedMessage())));
            e.printStackTrace(System.out);
            Assert.fail("Job failed");
        }
        Assert.assertEquals("dist job res is not 0:", 0, res);
    }
}

