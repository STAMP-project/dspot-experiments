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


import TaskCounter.REDUCE_OUTPUT_RECORDS;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.hsqldb.server.Server;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// import org.apache.hadoop.examples.DBCountPageView;
/**
 * Test aspects of DataDrivenDBInputFormat
 */
public class TestDataDrivenDBInputFormat extends HadoopTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TestDataDrivenDBInputFormat.class);

    private static final String DB_NAME = "dddbif";

    private static final String DB_URL = "jdbc:hsqldb:hsql://localhost/" + (TestDataDrivenDBInputFormat.DB_NAME);

    private static final String DRIVER_CLASS = "org.hsqldb.jdbc.JDBCDriver";

    private Server server;

    private Connection connection;

    private static final String OUT_DIR;

    public TestDataDrivenDBInputFormat() throws IOException {
        super(HadoopTestCase.LOCAL_MR, HadoopTestCase.LOCAL_FS, 1, 1);
    }

    static {
        OUT_DIR = (System.getProperty("test.build.data", "/tmp")) + "/dddbifout";
    }

    public static class DateCol implements WritableComparable , DBWritable {
        Date d;

        public String toString() {
            return d.toString();
        }

        public void readFields(ResultSet rs) throws SQLException {
            d = rs.getDate(1);
        }

        public void write(PreparedStatement ps) {
            // not needed.
        }

        public void readFields(DataInput in) throws IOException {
            long v = in.readLong();
            d = new Date(v);
        }

        public void write(DataOutput out) throws IOException {
            out.writeLong(d.getTime());
        }

        @Override
        public int hashCode() {
            return ((int) (d.getTime()));
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof TestDataDrivenDBInputFormat.DateCol) {
                Long v = Long.valueOf(d.getTime());
                Long other = Long.valueOf(((TestDataDrivenDBInputFormat.DateCol) (o)).d.getTime());
                return v.compareTo(other);
            } else {
                return -1;
            }
        }
    }

    public static class ValMapper extends Mapper<Object, Object, Object, NullWritable> {
        public void map(Object k, Object v, Context c) throws IOException, InterruptedException {
            c.write(v, NullWritable.get());
        }
    }

    @Test
    public void testDateSplits() throws Exception {
        Statement s = connection.createStatement();
        final String DATE_TABLE = "datetable";
        final String COL = "foo";
        try {
            // delete the table if it already exists.
            s.executeUpdate(("DROP TABLE " + DATE_TABLE));
        } catch (SQLException e) {
        }
        // Create the table.
        s.executeUpdate((((("CREATE TABLE " + DATE_TABLE) + "(") + COL) + " DATE)"));
        s.executeUpdate((("INSERT INTO " + DATE_TABLE) + " VALUES('2010-04-01')"));
        s.executeUpdate((("INSERT INTO " + DATE_TABLE) + " VALUES('2010-04-02')"));
        s.executeUpdate((("INSERT INTO " + DATE_TABLE) + " VALUES('2010-05-01')"));
        s.executeUpdate((("INSERT INTO " + DATE_TABLE) + " VALUES('2011-04-01')"));
        // commit this tx.
        connection.commit();
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///");
        FileSystem fs = FileSystem.getLocal(conf);
        fs.delete(new Path(TestDataDrivenDBInputFormat.OUT_DIR), true);
        // now do a dd import
        Job job = Job.getInstance(conf);
        job.setMapperClass(TestDataDrivenDBInputFormat.ValMapper.class);
        job.setReducerClass(Reducer.class);
        job.setMapOutputKeyClass(TestDataDrivenDBInputFormat.DateCol.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.setOutputKeyClass(TestDataDrivenDBInputFormat.DateCol.class);
        job.setOutputValueClass(NullWritable.class);
        job.setNumReduceTasks(1);
        job.getConfiguration().setInt("mapreduce.map.tasks", 2);
        FileOutputFormat.setOutputPath(job, new Path(TestDataDrivenDBInputFormat.OUT_DIR));
        DBConfiguration.configureDB(job.getConfiguration(), TestDataDrivenDBInputFormat.DRIVER_CLASS, TestDataDrivenDBInputFormat.DB_URL, null, null);
        DataDrivenDBInputFormat.setInput(job, TestDataDrivenDBInputFormat.DateCol.class, DATE_TABLE, null, COL, COL);
        boolean ret = job.waitForCompletion(true);
        Assert.assertTrue("job failed", ret);
        // Check to see that we imported as much as we thought we did.
        Assert.assertEquals("Did not get all the records", 4, job.getCounters().findCounter(REDUCE_OUTPUT_RECORDS).getValue());
    }
}

