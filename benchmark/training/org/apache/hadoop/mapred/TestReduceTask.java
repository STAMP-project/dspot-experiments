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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.hadoop.util.Progressable;
import org.junit.Test;


/**
 * This test exercises the ValueIterator.
 */
public class TestReduceTask {
    static class NullProgress implements Progressable {
        public void progress() {
        }
    }

    private static class Pair {
        String key;

        String value;

        Pair(String k, String v) {
            key = k;
            value = v;
        }
    }

    private static TestReduceTask.Pair[][] testCases = new TestReduceTask.Pair[][]{ new TestReduceTask.Pair[]{ new TestReduceTask.Pair("k1", "v1"), new TestReduceTask.Pair("k2", "v2"), new TestReduceTask.Pair("k3", "v3"), new TestReduceTask.Pair("k3", "v4"), new TestReduceTask.Pair("k4", "v5"), new TestReduceTask.Pair("k5", "v6") }, new TestReduceTask.Pair[]{ new TestReduceTask.Pair("", "v1"), new TestReduceTask.Pair("k1", "v2"), new TestReduceTask.Pair("k2", "v3"), new TestReduceTask.Pair("k2", "v4") }, new TestReduceTask.Pair[]{  }, new TestReduceTask.Pair[]{ new TestReduceTask.Pair("k1", "v1"), new TestReduceTask.Pair("k1", "v2"), new TestReduceTask.Pair("k1", "v3"), new TestReduceTask.Pair("k1", "v4") } };

    @Test
    public void testValueIterator() throws Exception {
        Path tmpDir = new Path("build/test/test.reduce.task");
        Configuration conf = new Configuration();
        for (TestReduceTask.Pair[] testCase : TestReduceTask.testCases) {
            runValueIterator(tmpDir, testCase, conf, null);
        }
    }

    @Test
    public void testValueIteratorWithCompression() throws Exception {
        Path tmpDir = new Path("build/test/test.reduce.task.compression");
        Configuration conf = new Configuration();
        DefaultCodec codec = new DefaultCodec();
        codec.setConf(conf);
        for (TestReduceTask.Pair[] testCase : TestReduceTask.testCases) {
            runValueIterator(tmpDir, testCase, conf, codec);
        }
    }
}

