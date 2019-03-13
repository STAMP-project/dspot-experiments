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
package org.apache.beam.runners.spark.io;


import java.io.File;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.io.AvroIO;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Avro pipeline test.
 */
public class AvroPipelineTest {
    private File inputFile;

    private File outputFile;

    @Rule
    public final TemporaryFolder tmpDir = new TemporaryFolder();

    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    @Test
    public void testGeneric() throws Exception {
        Schema schema = new Schema.Parser().parse(Resources.getResource("person.avsc").openStream());
        GenericRecord savedRecord = new org.apache.avro.generic.GenericData.Record(schema);
        savedRecord.put("name", "John Doe");
        savedRecord.put("age", 42);
        savedRecord.put("siblingnames", Lists.newArrayList("Jimmy", "Jane"));
        populateGenericFile(Lists.newArrayList(savedRecord), schema);
        PCollection<GenericRecord> input = pipeline.apply(AvroIO.readGenericRecords(schema).from(inputFile.getAbsolutePath()));
        input.apply(AvroIO.writeGenericRecords(schema).to(outputFile.getAbsolutePath()));
        pipeline.run();
        List<GenericRecord> records = readGenericFile();
        Assert.assertEquals(Lists.newArrayList(savedRecord), records);
    }
}

