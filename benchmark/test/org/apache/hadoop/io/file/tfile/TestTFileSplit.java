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
package org.apache.hadoop.io.file.tfile;


import Compression.Algorithm.GZ;
import Compression.Algorithm.NONE;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;


public class TestTFileSplit {
    private static String ROOT = GenericTestUtils.getTestDir().getAbsolutePath();

    private static final int BLOCK_SIZE = 64 * 1024;

    private static final String KEY = "key";

    private static final String VALUE = "value";

    private FileSystem fs;

    private Configuration conf;

    private Path path;

    private Random random = new Random();

    private String comparator = "memcmp";

    private String outputFile = "TestTFileSplit";

    @Test
    public void testSplit() throws IOException {
        System.out.println("testSplit");
        createFile(100000, NONE.getName());
        checkRecNums();
        readFile();
        readRowSplits(10);
        fs.delete(path, true);
        createFile(500000, GZ.getName());
        checkRecNums();
        readFile();
        readRowSplits(83);
        fs.delete(path, true);
    }
}

