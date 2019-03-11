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
package org.apache.hadoop.mapreduce.lib.map;


import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.apache.hadoop.mapreduce.Mapper;
import org.junit.Test;


public class TestMultithreadedMapper extends HadoopTestCase {
    public TestMultithreadedMapper() throws IOException {
        super(HadoopTestCase.LOCAL_MR, HadoopTestCase.LOCAL_FS, 1, 1);
    }

    @Test
    public void testOKRun() throws Exception {
        run(false, false);
    }

    @Test
    public void testIOExRun() throws Exception {
        run(true, false);
    }

    @Test
    public void testRuntimeExRun() throws Exception {
        run(false, true);
    }

    public static class IDMap extends Mapper<LongWritable, Text, LongWritable, Text> {
        private boolean ioEx = false;

        private boolean rtEx = false;

        public void setup(Context context) {
            ioEx = context.getConfiguration().getBoolean("multithreaded.ioException", false);
            rtEx = context.getConfiguration().getBoolean("multithreaded.runtimeException", false);
        }

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            if (ioEx) {
                throw new IOException();
            }
            if (rtEx) {
                throw new RuntimeException();
            }
            super.map(key, value, context);
        }
    }
}

