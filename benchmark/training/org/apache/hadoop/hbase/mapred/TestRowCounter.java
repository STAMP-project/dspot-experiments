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
package org.apache.hadoop.hbase.mapred;


import TableInputFormat.COLUMN_LIST;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapred.RowCounter.RowCounterMapper;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hbase.thirdparty.com.google.common.base.Joiner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ MapReduceTests.class, SmallTests.class })
public class TestRowCounter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRowCounter.class);

    @Test
    @SuppressWarnings("deprecation")
    public void shouldPrintUsage() throws Exception {
        String expectedOutput = "rowcounter <outputdir> <tablename> <column1> [<column2>...]";
        String result = new TestRowCounter.OutputReader(System.out) {
            @Override
            void doRead() {
                Assert.assertEquals((-1), RowCounter.printUsage());
            }
        }.read();
        Assert.assertTrue(result.startsWith(expectedOutput));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void shouldExitAndPrintUsageSinceParameterNumberLessThanThree() throws Exception {
        final String[] args = new String[]{ "one", "two" };
        String line = "ERROR: Wrong number of parameters: " + (args.length);
        String result = new TestRowCounter.OutputReader(System.err) {
            @Override
            void doRead() throws Exception {
                Assert.assertEquals((-1), new RowCounter().run(args));
            }
        }.read();
        Assert.assertTrue(result.startsWith(line));
    }

    @Test
    @SuppressWarnings({ "deprecation", "unchecked" })
    public void shouldRegInReportEveryIncomingRow() throws IOException {
        int iterationNumber = 999;
        RowCounter.RowCounterMapper mapper = new RowCounter.RowCounterMapper();
        Reporter reporter = Mockito.mock(Reporter.class);
        for (int i = 0; i < iterationNumber; i++)
            mapper.map(Mockito.mock(ImmutableBytesWritable.class), Mockito.mock(Result.class), Mockito.mock(OutputCollector.class), reporter);

        Mockito.verify(reporter, Mockito.times(iterationNumber)).incrCounter(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
    }

    @Test
    @SuppressWarnings({ "deprecation" })
    public void shouldCreateAndRunSubmittableJob() throws Exception {
        RowCounter rCounter = new RowCounter();
        rCounter.setConf(HBaseConfiguration.create());
        String[] args = new String[]{ "\temp", "tableA", "column1", "column2", "column3" };
        JobConf jobConfig = rCounter.createSubmittableJob(args);
        Assert.assertNotNull(jobConfig);
        Assert.assertEquals(0, jobConfig.getNumReduceTasks());
        Assert.assertEquals("rowcounter", jobConfig.getJobName());
        Assert.assertEquals(jobConfig.getMapOutputValueClass(), Result.class);
        Assert.assertEquals(jobConfig.getMapperClass(), RowCounterMapper.class);
        Assert.assertEquals(jobConfig.get(COLUMN_LIST), Joiner.on(' ').join("column1", "column2", "column3"));
        Assert.assertEquals(jobConfig.getMapOutputKeyClass(), ImmutableBytesWritable.class);
    }

    enum Outs {

        OUT,
        ERR;}

    private abstract static class OutputReader {
        private final PrintStream ps;

        private PrintStream oldPrintStream;

        private TestRowCounter.Outs outs;

        protected OutputReader(PrintStream ps) {
            this.ps = ps;
        }

        protected String read() throws Exception {
            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            if ((ps) == (System.out)) {
                oldPrintStream = System.out;
                outs = TestRowCounter.Outs.OUT;
                System.setOut(new PrintStream(outBytes));
            } else
                if ((ps) == (System.err)) {
                    oldPrintStream = System.err;
                    outs = TestRowCounter.Outs.ERR;
                    System.setErr(new PrintStream(outBytes));
                } else {
                    throw new IllegalStateException("OutputReader: unsupported PrintStream");
                }

            try {
                doRead();
                return new String(outBytes.toByteArray());
            } finally {
                switch (outs) {
                    case OUT :
                        {
                            System.setOut(oldPrintStream);
                            break;
                        }
                    case ERR :
                        {
                            System.setErr(oldPrintStream);
                            break;
                        }
                    default :
                        throw new IllegalStateException("OutputReader: unsupported PrintStream");
                }
            }
        }

        abstract void doRead() throws Exception;
    }
}

