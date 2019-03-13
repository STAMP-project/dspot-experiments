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
package org.apache.druid.data.input.orc;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.druid.data.input.MapBasedInputRow;
import org.apache.druid.data.input.impl.InputRowParser;
import org.apache.druid.indexer.HadoopDruidIndexerConfig;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.OrcNewInputFormat;
import org.apache.hadoop.hive.ql.io.orc.OrcStruct;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DruidOrcInputFormatTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    String timestamp = "2016-01-01T00:00:00.000Z";

    String col1 = "bar";

    String[] col2 = new String[]{ "dat1", "dat2", "dat3" };

    double val1 = 1.1;

    Job job;

    HadoopDruidIndexerConfig config;

    File testFile;

    Path path;

    FileSplit split;

    @Test
    public void testRead() throws IOException, InterruptedException {
        InputFormat inputFormat = ReflectionUtils.newInstance(OrcNewInputFormat.class, job.getConfiguration());
        TaskAttemptContext context = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(job.getConfiguration(), new TaskAttemptID());
        RecordReader reader = inputFormat.createRecordReader(split, context);
        InputRowParser<OrcStruct> parser = ((InputRowParser<OrcStruct>) (config.getParser()));
        reader.initialize(split, context);
        reader.nextKeyValue();
        OrcStruct data = ((OrcStruct) (reader.getCurrentValue()));
        MapBasedInputRow row = ((MapBasedInputRow) (parser.parseBatch(data).get(0)));
        Assert.assertTrue(((row.getEvent().keySet().size()) == 4));
        Assert.assertEquals(DateTimes.of(timestamp), row.getTimestamp());
        Assert.assertEquals(parser.getParseSpec().getDimensionsSpec().getDimensionNames(), row.getDimensions());
        Assert.assertEquals(col1, row.getEvent().get("col1"));
        Assert.assertEquals(Arrays.asList(col2), row.getDimension("col2"));
        reader.close();
    }

    @Test
    public void testReadDateColumn() throws IOException, InterruptedException {
        File testFile2 = makeOrcFileWithDate();
        Path path = new Path(testFile2.getAbsoluteFile().toURI());
        FileSplit split = new FileSplit(path, 0, testFile2.length(), null);
        InputFormat inputFormat = ReflectionUtils.newInstance(OrcNewInputFormat.class, job.getConfiguration());
        TaskAttemptContext context = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(job.getConfiguration(), new TaskAttemptID());
        RecordReader reader = inputFormat.createRecordReader(split, context);
        InputRowParser<OrcStruct> parser = ((InputRowParser<OrcStruct>) (config.getParser()));
        reader.initialize(split, context);
        reader.nextKeyValue();
        OrcStruct data = ((OrcStruct) (reader.getCurrentValue()));
        MapBasedInputRow row = ((MapBasedInputRow) (parser.parseBatch(data).get(0)));
        Assert.assertTrue(((row.getEvent().keySet().size()) == 4));
        Assert.assertEquals(DateTimes.of(timestamp), row.getTimestamp());
        Assert.assertEquals(parser.getParseSpec().getDimensionsSpec().getDimensionNames(), row.getDimensions());
        Assert.assertEquals(col1, row.getEvent().get("col1"));
        Assert.assertEquals(Arrays.asList(col2), row.getDimension("col2"));
        reader.close();
    }
}

