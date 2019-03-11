/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.fs;


import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing the correctness of FileSystem.getFileBlockLocations.
 */
public class TestGetFileBlockLocations {
    private static String TEST_ROOT_DIR = GenericTestUtils.getTempPath("testGetFileBlockLocations");

    private static final int FileLength = (4 * 1024) * 1024;// 4MB


    private Configuration conf;

    private Path path;

    private FileSystem fs;

    private Random random;

    @Test
    public void testFailureNegativeParameters() throws IOException {
        FileStatus status = fs.getFileStatus(path);
        try {
            BlockLocation[] locations = fs.getFileBlockLocations(status, (-1), 100);
            Assert.fail("Expecting exception being throw");
        } catch (IllegalArgumentException e) {
        }
        try {
            BlockLocation[] locations = fs.getFileBlockLocations(status, 100, (-1));
            Assert.fail("Expecting exception being throw");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGetFileBlockLocations1() throws IOException {
        FileStatus status = fs.getFileStatus(path);
        oneTest(0, ((int) (status.getLen())), status);
        oneTest(0, (((int) (status.getLen())) * 2), status);
        oneTest((((int) (status.getLen())) * 2), (((int) (status.getLen())) * 4), status);
        oneTest((((int) (status.getLen())) / 2), (((int) (status.getLen())) * 3), status);
        oneTest(((int) (status.getLen())), (((int) (status.getLen())) * 2), status);
        for (int i = 0; i < 10; ++i) {
            oneTest(((((int) (status.getLen())) * i) / 10), ((((int) (status.getLen())) * (i + 1)) / 10), status);
        }
    }

    @Test
    public void testGetFileBlockLocations2() throws IOException {
        FileStatus status = fs.getFileStatus(path);
        for (int i = 0; i < 1000; ++i) {
            int offBegin = random.nextInt(((int) (2 * (status.getLen()))));
            int offEnd = random.nextInt(((int) (2 * (status.getLen()))));
            oneTest(offBegin, offEnd, status);
        }
    }
}

