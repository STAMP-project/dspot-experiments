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
package org.apache.hadoop.tools.mapred;


import java.util.Random;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.Credentials;
import org.junit.Test;


public class TestUniformSizeInputFormat {
    private static MiniDFSCluster cluster;

    private static final int N_FILES = 20;

    private static final int SIZEOF_EACH_FILE = 1024;

    private static final Random random = new Random();

    private static int totalFileSize = 0;

    private static final Credentials CREDENTIALS = new Credentials();

    @Test
    public void testGetSplits() throws Exception {
        testGetSplits(9);
        for (int i = 1; i < (TestUniformSizeInputFormat.N_FILES); ++i)
            testGetSplits(i);

    }
}

