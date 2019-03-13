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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.rcfile;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.serde2.SerDeUtils;
import org.apache.hadoop.hive.serde2.columnar.BytesRefArrayWritable;
import org.apache.hadoop.hive.serde2.columnar.BytesRefWritable;
import org.apache.hadoop.hive.serde2.columnar.ColumnarSerDe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TestRCFile.
 */
public class TestRCFileMapReduceInputFormat extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TestRCFileMapReduceInputFormat.class);

    private static Configuration conf = new Configuration();

    private static ColumnarSerDe serDe;

    private static Path file;

    private static FileSystem fs;

    private static Properties tbl;

    static {
        try {
            TestRCFileMapReduceInputFormat.fs = FileSystem.getLocal(TestRCFileMapReduceInputFormat.conf);
            Path dir = new Path(((System.getProperty("test.tmp.dir", ".")) + "/mapred"));
            TestRCFileMapReduceInputFormat.file = new Path(dir, "test_rcfile");
            TestRCFileMapReduceInputFormat.fs.delete(dir, true);
            // the SerDe part is from TestLazySimpleSerDe
            TestRCFileMapReduceInputFormat.serDe = new ColumnarSerDe();
            // Create the SerDe
            TestRCFileMapReduceInputFormat.tbl = TestRCFileMapReduceInputFormat.createProperties();
            SerDeUtils.initializeSerDe(TestRCFileMapReduceInputFormat.serDe, TestRCFileMapReduceInputFormat.conf, TestRCFileMapReduceInputFormat.tbl, null);
        } catch (Exception e) {
        }
    }

    private static BytesRefArrayWritable patialS = new BytesRefArrayWritable();

    private static byte[][] bytesArray = null;

    private static BytesRefArrayWritable s = null;

    static {
        try {
            TestRCFileMapReduceInputFormat.bytesArray = new byte[][]{ "123".getBytes("UTF-8"), "456".getBytes("UTF-8"), "789".getBytes("UTF-8"), "1000".getBytes("UTF-8"), "5.3".getBytes("UTF-8"), "hive and hadoop".getBytes("UTF-8"), new byte[0], "NULL".getBytes("UTF-8") };
            TestRCFileMapReduceInputFormat.s = new BytesRefArrayWritable(TestRCFileMapReduceInputFormat.bytesArray.length);
            TestRCFileMapReduceInputFormat.s.set(0, new BytesRefWritable("123".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.s.set(1, new BytesRefWritable("456".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.s.set(2, new BytesRefWritable("789".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.s.set(3, new BytesRefWritable("1000".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.s.set(4, new BytesRefWritable("5.3".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.s.set(5, new BytesRefWritable("hive and hadoop".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.s.set(6, new BytesRefWritable("NULL".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.s.set(7, new BytesRefWritable("NULL".getBytes("UTF-8")));
            // partial test init
            TestRCFileMapReduceInputFormat.patialS.set(0, new BytesRefWritable("NULL".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.patialS.set(1, new BytesRefWritable("NULL".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.patialS.set(2, new BytesRefWritable("789".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.patialS.set(3, new BytesRefWritable("1000".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.patialS.set(4, new BytesRefWritable("NULL".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.patialS.set(5, new BytesRefWritable("NULL".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.patialS.set(6, new BytesRefWritable("NULL".getBytes("UTF-8")));
            TestRCFileMapReduceInputFormat.patialS.set(7, new BytesRefWritable("NULL".getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
        }
    }

    public void testSynAndSplit() throws IOException, InterruptedException {
        splitBeforeSync();
        splitRightBeforeSync();
        splitInMiddleOfSync();
        splitRightAfterSync();
        splitAfterSync();
    }
}

