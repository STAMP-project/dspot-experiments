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
package org.apache.hadoop.examples.terasort;


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileAlreadyExistsException;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestTeraSort extends HadoopTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TestTeraSort.class);

    public TestTeraSort() throws IOException {
        super(LOCAL_MR, LOCAL_FS, 1, 1);
    }

    // Input/Output paths for sort
    private static final Path TEST_DIR = new Path(new File(System.getProperty("test.build.data", "/tmp"), "terasort").getAbsoluteFile().toURI().toString());

    private static final Path SORT_INPUT_PATH = new Path(TestTeraSort.TEST_DIR, "sortin");

    private static final Path SORT_OUTPUT_PATH = new Path(TestTeraSort.TEST_DIR, "sortout");

    private static final Path TERA_OUTPUT_PATH = new Path(TestTeraSort.TEST_DIR, "validate");

    private static final String NUM_ROWS = "100";

    @Test
    public void testTeraSort() throws Exception {
        // Run TeraGen to generate input for 'terasort'
        runTeraGen(createJobConf(), TestTeraSort.SORT_INPUT_PATH);
        // Run teragen again to check for FAE
        try {
            runTeraGen(createJobConf(), TestTeraSort.SORT_INPUT_PATH);
            Assert.fail("Teragen output overwritten!");
        } catch (FileAlreadyExistsException fae) {
            TestTeraSort.LOG.info("Expected exception: ", fae);
        }
        // Run terasort
        runTeraSort(createJobConf(), TestTeraSort.SORT_INPUT_PATH, TestTeraSort.SORT_OUTPUT_PATH);
        // Run terasort again to check for FAE
        try {
            runTeraSort(createJobConf(), TestTeraSort.SORT_INPUT_PATH, TestTeraSort.SORT_OUTPUT_PATH);
            Assert.fail("Terasort output overwritten!");
        } catch (FileAlreadyExistsException fae) {
            TestTeraSort.LOG.info("Expected exception: ", fae);
        }
        // Run tera-validator to check if sort worked correctly
        runTeraValidator(createJobConf(), TestTeraSort.SORT_OUTPUT_PATH, TestTeraSort.TERA_OUTPUT_PATH);
    }

    @Test
    public void testTeraSortWithLessThanTwoArgs() throws Exception {
        String[] args = new String[1];
        Assert.assertEquals(new TeraSort().run(args), 2);
    }
}

