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
package org.apache.hadoop.mapreduce.lib.partition;


import MRJobConfig.MAP_OUTPUT_KEY_FIELD_SEPARATOR;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.junit.Test;


public class TestMRKeyFieldBasedComparator extends HadoopTestCase {
    Configuration conf;

    String line1 = "123 -123 005120 123.9 0.01 0.18 010 10.0 4444.1 011 011 234";

    String line2 = "134 -12 005100 123.10 -1.01 0.19 02 10.1 4444";

    public TestMRKeyFieldBasedComparator() throws IOException {
        super(HadoopTestCase.LOCAL_MR, HadoopTestCase.LOCAL_FS, 1, 1);
        conf = createJobConf();
        conf.set(MAP_OUTPUT_KEY_FIELD_SEPARATOR, " ");
    }

    @Test
    public void testBasicUnixComparator() throws Exception {
        testComparator("-k1,1n", 1);
        testComparator("-k2,2n", 1);
        testComparator("-k2.2,2n", 2);
        testComparator("-k3.4,3n", 2);
        testComparator("-k3.2,3.3n -k4,4n", 2);
        testComparator("-k3.2,3.3n -k4,4nr", 1);
        testComparator("-k2.4,2.4n", 2);
        testComparator("-k7,7", 1);
        testComparator("-k7,7n", 2);
        testComparator("-k8,8n", 1);
        testComparator("-k9,9", 2);
        testComparator("-k11,11", 2);
        testComparator("-k10,10", 2);
        testWithoutMRJob("-k9,9", 1);
        testWithoutMRJob("-k9n", 1);
    }

    byte[] line1_bytes = line1.getBytes();

    byte[] line2_bytes = line2.getBytes();
}

