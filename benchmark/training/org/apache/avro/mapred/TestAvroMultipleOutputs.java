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


import Schema.Type.STRING;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.avro.Schema;
import org.apache.avro.util.Utf8;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestAvroMultipleOutputs {
    @Rule
    public TemporaryFolder INPUT_DIR = new TemporaryFolder();

    @Rule
    public TemporaryFolder OUTPUT_DIR = new TemporaryFolder();

    public static class MapImpl extends AvroMapper<Utf8, Pair<Utf8, Long>> {
        private AvroMultipleOutputs amos;

        public void configure(JobConf Job) {
            this.amos = new AvroMultipleOutputs(Job);
        }

        @Override
        public void map(Utf8 text, AvroCollector<Pair<Utf8, Long>> collector, Reporter reporter) throws IOException {
            StringTokenizer tokens = new StringTokenizer(text.toString());
            while (tokens.hasMoreTokens()) {
                String tok = tokens.nextToken();
                collector.collect(new Pair(new Utf8(tok), 1L));
                amos.getCollector("myavro2", reporter).collect(new Pair<Utf8, Long>(new Utf8(tok), 1L).toString());
            } 
        }

        public void close() throws IOException {
            amos.close();
        }
    }

    public static class ReduceImpl extends AvroReducer<Utf8, Long, Pair<Utf8, Long>> {
        private AvroMultipleOutputs amos;

        public void configure(JobConf Job) {
            amos = new AvroMultipleOutputs(Job);
        }

        @Override
        public void reduce(Utf8 word, Iterable<Long> counts, AvroCollector<Pair<Utf8, Long>> collector, Reporter reporter) throws IOException {
            long sum = 0;
            for (long count : counts)
                sum += count;

            Pair<Utf8, Long> outputvalue = new Pair(word, sum);
            amos.getCollector("myavro", reporter).collect(outputvalue);
            amos.collect("myavro1", reporter, outputvalue.toString());
            amos.collect("myavro", reporter, new Pair<Utf8, Long>(new Utf8(""), 0L).getSchema(), outputvalue, "testavrofile");
            amos.collect("myavro", reporter, Schema.create(STRING), outputvalue.toString(), "testavrofile1");
            collector.collect(new Pair(word, sum));
        }

        public void close() throws IOException {
            amos.close();
        }
    }

    @Test
    public void runTestsInOrder() throws Exception {
        String avroPath = OUTPUT_DIR.getRoot().getPath();
        testJob(avroPath);
        testProjection(avroPath);
        testProjectionNewMethodsOne(avroPath);
        testProjectionNewMethodsTwo(avroPath);
        testProjection1(avroPath);
        testJobNoreducer();
        testProjectionNoreducer(avroPath);
    }
}

