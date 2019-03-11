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
package org.apache.hadoop.mapreduce.lib.db;


import DBConfiguration.INPUT_BOUNDING_QUERY;
import MRJobConfig.NUM_MAPS;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat.DBInputSplit;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat.NullDBWritable;
import org.apache.hadoop.mapreduce.lib.db.DataDrivenDBInputFormat.DataDrivenDBInputSplit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestDbClasses {
    /**
     * test splitters from DataDrivenDBInputFormat. For different data types may
     * be different splitter
     */
    @Test(timeout = 10000)
    public void testDataDrivenDBInputFormatSplitter() {
        DataDrivenDBInputFormat<NullDBWritable> format = new DataDrivenDBInputFormat<NullDBWritable>();
        testCommonSplitterTypes(format);
        Assert.assertEquals(DateSplitter.class, format.getSplitter(Types.TIMESTAMP).getClass());
        Assert.assertEquals(DateSplitter.class, format.getSplitter(Types.DATE).getClass());
        Assert.assertEquals(DateSplitter.class, format.getSplitter(Types.TIME).getClass());
    }

    @Test(timeout = 10000)
    public void testDataDrivenDBInputFormat() throws Exception {
        JobContext jobContext = Mockito.mock(JobContext.class);
        Configuration configuration = new Configuration();
        configuration.setInt(NUM_MAPS, 1);
        Mockito.when(jobContext.getConfiguration()).thenReturn(configuration);
        DataDrivenDBInputFormat<NullDBWritable> format = new DataDrivenDBInputFormat<NullDBWritable>();
        List<InputSplit> splits = format.getSplits(jobContext);
        Assert.assertEquals(1, splits.size());
        DataDrivenDBInputSplit split = ((DataDrivenDBInputSplit) (splits.get(0)));
        Assert.assertEquals("1=1", split.getLowerClause());
        Assert.assertEquals("1=1", split.getUpperClause());
        // 2
        configuration.setInt(NUM_MAPS, 2);
        DataDrivenDBInputFormat.setBoundingQuery(configuration, "query");
        Assert.assertEquals("query", configuration.get(INPUT_BOUNDING_QUERY));
        Job job = Mockito.mock(Job.class);
        Mockito.when(job.getConfiguration()).thenReturn(configuration);
        DataDrivenDBInputFormat.setInput(job, NullDBWritable.class, "query", "Bounding Query");
        Assert.assertEquals("Bounding Query", configuration.get(INPUT_BOUNDING_QUERY));
    }

    @Test(timeout = 10000)
    public void testOracleDataDrivenDBInputFormat() throws Exception {
        OracleDataDrivenDBInputFormat<NullDBWritable> format = new TestDbClasses.OracleDataDrivenDBInputFormatForTest();
        testCommonSplitterTypes(format);
        Assert.assertEquals(OracleDateSplitter.class, format.getSplitter(Types.TIMESTAMP).getClass());
        Assert.assertEquals(OracleDateSplitter.class, format.getSplitter(Types.DATE).getClass());
        Assert.assertEquals(OracleDateSplitter.class, format.getSplitter(Types.TIME).getClass());
    }

    /**
     * test generate sql script for OracleDBRecordReader.
     */
    @Test(timeout = 20000)
    public void testOracleDBRecordReader() throws Exception {
        DBInputSplit splitter = new DBInputSplit(1, 10);
        Configuration configuration = new Configuration();
        Connection connect = DriverForTest.getConnection();
        DBConfiguration dbConfiguration = new DBConfiguration(configuration);
        dbConfiguration.setInputOrderBy("Order");
        String[] fields = new String[]{ "f1", "f2" };
        OracleDBRecordReader<NullDBWritable> recorder = new OracleDBRecordReader<NullDBWritable>(splitter, NullDBWritable.class, configuration, connect, dbConfiguration, "condition", fields, "table");
        Assert.assertEquals("SELECT * FROM (SELECT a.*,ROWNUM dbif_rno FROM ( SELECT f1, f2 FROM table WHERE condition ORDER BY Order ) a WHERE rownum <= 10 ) WHERE dbif_rno > 1", recorder.getSelectQuery());
    }

    private class OracleDataDrivenDBInputFormatForTest extends OracleDataDrivenDBInputFormat<NullDBWritable> {
        @Override
        public DBConfiguration getDBConf() {
            String[] names = new String[]{ "field1", "field2" };
            DBConfiguration result = Mockito.mock(DBConfiguration.class);
            Mockito.when(result.getInputConditions()).thenReturn("conditions");
            Mockito.when(result.getInputFieldNames()).thenReturn(names);
            Mockito.when(result.getInputTableName()).thenReturn("table");
            return result;
        }

        @Override
        public Connection getConnection() {
            return DriverForTest.getConnection();
        }
    }
}

