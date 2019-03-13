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


import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class TestMRCJCJobConf {
    private static final String JAR_RELATIVE_PATH = "build/test/mapred/testjar/testjob.jar";

    private static final String CLASSNAME = "testjar.ClassWordCount";

    private static String TEST_DIR_WITH_SPECIAL_CHARS = ((System.getProperty("test.build.data", "/tmp")) + (File.separator)) + "test jobconf with + and spaces";

    @Test
    public void testFindContainingJar() throws Exception {
        testJarAtPath(TestMRCJCJobConf.JAR_RELATIVE_PATH);
    }

    /**
     * Test that findContainingJar works correctly even if the
     * path has a "+" sign or spaces in it
     */
    @Test
    public void testFindContainingJarWithPlus() throws Exception {
        new File(TestMRCJCJobConf.TEST_DIR_WITH_SPECIAL_CHARS).mkdirs();
        Configuration conf = new Configuration();
        FileSystem localfs = FileSystem.getLocal(conf);
        FileUtil.copy(localfs, new Path(TestMRCJCJobConf.JAR_RELATIVE_PATH), localfs, new Path(TestMRCJCJobConf.TEST_DIR_WITH_SPECIAL_CHARS, "test.jar"), false, true, conf);
        testJarAtPath((((TestMRCJCJobConf.TEST_DIR_WITH_SPECIAL_CHARS) + (File.separator)) + "test.jar"));
    }
}

