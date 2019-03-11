/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.indexer.path;


import MultipleInputs.DIR_FORMATS;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.druid.indexer.HadoopDruidIndexerConfig;
import org.apache.druid.indexer.HadoopIOConfig;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.segment.indexing.DataSchema;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.junit.Assert;
import org.junit.Test;


public class StaticPathSpecTest {
    private final ObjectMapper jsonMapper = new DefaultObjectMapper();

    @Test
    public void testSerdeCustomInputFormat() throws Exception {
        testSerde("/sample/path", TextInputFormat.class);
    }

    @Test
    public void testDeserializationNoInputFormat() throws Exception {
        testSerde("/sample/path", null);
    }

    @Test
    public void testAddingPaths() throws Exception {
        Job job = new Job();
        StaticPathSpec pathSpec = new StaticPathSpec("/a/c,/a/b/{c,d}", null);
        DataSchema schema = new DataSchema("ds", null, new AggregatorFactory[0], null, null, jsonMapper);
        HadoopIOConfig io = new HadoopIOConfig(null, null, null);
        pathSpec.addInputPaths(new HadoopDruidIndexerConfig(new org.apache.druid.indexer.HadoopIngestionSpec(schema, io, null)), job);
        String paths = job.getConfiguration().get(DIR_FORMATS);
        String formatter = TextInputFormat.class.getName();
        String[] expected = new String[]{ "/a/c;" + formatter, "/a/b/c;" + formatter, "/a/b/d;" + formatter };
        Assert.assertArrayEquals(expected, paths.split(","));
    }
}

