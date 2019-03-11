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


import Type.NULL;
import Weather.SCHEMA;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.io.DatumReader;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Reporter;
import org.junit.Assert;
import org.junit.Test;
import test.Weather;


/**
 * Tests mapred API with a specific record.
 */
public class TestWeather {
    private static final AtomicInteger mapCloseCalls = new AtomicInteger();

    private static final AtomicInteger mapConfigureCalls = new AtomicInteger();

    private static final AtomicInteger reducerCloseCalls = new AtomicInteger();

    private static final AtomicInteger reducerConfigureCalls = new AtomicInteger();

    /**
     * Uses default mapper with no reduces for a map-only identity job.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testMapOnly() throws Exception {
        JobConf job = new JobConf();
        String inDir = (System.getProperty("share.dir", "../../../share")) + "/test/data";
        Path input = new Path((inDir + "/weather.avro"));
        Path output = new Path("target/test/weather-ident");
        output.getFileSystem(job).delete(output);
        job.setJobName("identity map weather");
        AvroJob.setInputSchema(job, SCHEMA.);
        AvroJob.setOutputSchema(job, SCHEMA.);
        FileInputFormat.setInputPaths(job, input);
        FileOutputFormat.setOutputPath(job, output);
        FileOutputFormat.setCompressOutput(job, true);
        job.setNumReduceTasks(0);
        // map-only
        JobClient.runJob(job);
        // check output is correct
        DatumReader<Weather> reader = new org.apache.avro.specific.SpecificDatumReader();
        DataFileReader<Weather> check = new DataFileReader(new File((inDir + "/weather.avro")), reader);
        DataFileReader<Weather> sorted = new DataFileReader(new File(((output.toString()) + "/part-00000.avro")), reader);
        for (Weather w : sorted)
            Assert.assertEquals(check.next(), w);

        check.close();
        sorted.close();
    }

    // maps input Weather to Pair<Weather,Void>, to sort by Weather
    public static class SortMapper extends AvroMapper<Weather, Pair<Weather, Void>> {
        @Override
        public void map(Weather w, AvroCollector<Pair<Weather, Void>> collector, Reporter reporter) throws IOException {
            collector.collect(new Pair(w, ((Void) (null))));
        }

        @Override
        public void close() throws IOException {
            TestWeather.mapCloseCalls.incrementAndGet();
        }

        @Override
        public void configure(JobConf jobConf) {
            TestWeather.mapConfigureCalls.incrementAndGet();
        }
    }

    // output keys only, since values are empty
    public static class SortReducer extends AvroReducer<Weather, Void, Weather> {
        @Override
        public void reduce(Weather w, Iterable<Void> ignore, AvroCollector<Weather> collector, Reporter reporter) throws IOException {
            collector.collect(w);
        }

        @Override
        public void close() throws IOException {
            TestWeather.reducerCloseCalls.incrementAndGet();
        }

        @Override
        public void configure(JobConf jobConf) {
            TestWeather.reducerConfigureCalls.incrementAndGet();
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSort() throws Exception {
        JobConf job = new JobConf();
        String inDir = "../../../share/test/data";
        Path input = new Path((inDir + "/weather.avro"));
        Path output = new Path("target/test/weather-sort");
        output.getFileSystem(job).delete(output);
        job.setJobName("sort weather");
        AvroJob.setInputSchema(job, SCHEMA.);
        AvroJob.setMapOutputSchema(job, Pair.getPairSchema(SCHEMA., Schema.create(NULL)));
        AvroJob.setOutputSchema(job, SCHEMA.);
        AvroJob.setMapperClass(job, TestWeather.SortMapper.class);
        AvroJob.setReducerClass(job, TestWeather.SortReducer.class);
        FileInputFormat.setInputPaths(job, input);
        FileOutputFormat.setOutputPath(job, output);
        FileOutputFormat.setCompressOutput(job, true);
        AvroJob.setOutputCodec(job, SNAPPY_CODEC);
        JobClient.runJob(job);
        // check output is correct
        DatumReader<Weather> reader = new org.apache.avro.specific.SpecificDatumReader();
        DataFileReader<Weather> check = new DataFileReader(new File((inDir + "/weather-sorted.avro")), reader);
        DataFileReader<Weather> sorted = new DataFileReader(new File(((output.toString()) + "/part-00000.avro")), reader);
        for (Weather w : sorted)
            Assert.assertEquals(check.next(), w);

        check.close();
        sorted.close();
        // check that AvroMapper and AvroReducer get close() and configure() called
        Assert.assertEquals(1, TestWeather.mapCloseCalls.get());
        Assert.assertEquals(1, TestWeather.reducerCloseCalls.get());
        Assert.assertEquals(1, TestWeather.mapConfigureCalls.get());
        Assert.assertEquals(1, TestWeather.reducerConfigureCalls.get());
    }
}

