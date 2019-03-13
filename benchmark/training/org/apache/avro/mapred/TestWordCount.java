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
package org.apache.avro.mapred;


import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.avro.util.Utf8;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestWordCount {
    @ClassRule
    public static TemporaryFolder INPUT_DIR = new TemporaryFolder();

    @ClassRule
    public static TemporaryFolder OUTPUT_DIR = new TemporaryFolder();

    public static class MapImpl extends AvroMapper<Utf8, Pair<Utf8, Long>> {
        @Override
        public void map(Utf8 text, AvroCollector<Pair<Utf8, Long>> collector, Reporter reporter) throws IOException {
            StringTokenizer tokens = new StringTokenizer(text.toString());
            while (tokens.hasMoreTokens())
                collector.collect(new Pair(new Utf8(tokens.nextToken()), 1L));

        }
    }

    public static class ReduceImpl extends AvroReducer<Utf8, Long, Pair<Utf8, Long>> {
        @Override
        public void reduce(Utf8 word, Iterable<Long> counts, AvroCollector<Pair<Utf8, Long>> collector, Reporter reporter) throws IOException {
            long sum = 0;
            for (long count : counts)
                sum += count;

            collector.collect(new Pair(word, sum));
        }
    }

    @Test
    public void runTestsInOrder() throws Exception {
        String pathOut = TestWordCount.OUTPUT_DIR.getRoot().getPath();
        testJob(pathOut);
        testProjection(pathOut);
    }
}

