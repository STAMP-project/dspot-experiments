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
package org.apache.hadoop.io;


import CompressionType.BLOCK;
import CompressionType.NONE;
import SetFile.Reader;
import org.apache.hadoop.fs.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Support for flat files of binary key/value pairs.
 */
public class TestSetFile {
    private static final Logger LOG = LoggerFactory.getLogger(TestSetFile.class);

    private static String FILE = GenericTestUtils.getTempPath("test.set");

    private static Configuration conf = new Configuration();

    @Test
    public void testSetFile() throws Exception {
        FileSystem fs = FileSystem.getLocal(TestSetFile.conf);
        try {
            RandomDatum[] data = TestSetFile.generate(10000);
            TestSetFile.writeTest(fs, data, TestSetFile.FILE, NONE);
            TestSetFile.readTest(fs, data, TestSetFile.FILE);
            TestSetFile.writeTest(fs, data, TestSetFile.FILE, BLOCK);
            TestSetFile.readTest(fs, data, TestSetFile.FILE);
        } finally {
            close();
        }
    }

    /**
     * test {@code SetFile.Reader} methods
     * next(), get() in combination
     */
    @Test
    public void testSetFileAccessMethods() {
        try {
            FileSystem fs = FileSystem.getLocal(TestSetFile.conf);
            int size = 10;
            writeData(fs, size);
            SetFile.Reader reader = createReader(fs);
            Assert.assertTrue("testSetFileWithConstruction1 error !!!", reader.next(new IntWritable(0)));
            // don't know why reader.get(i) return i+1
            Assert.assertEquals("testSetFileWithConstruction2 error !!!", new IntWritable(((size / 2) + 1)), reader.get(new IntWritable((size / 2))));
            Assert.assertNull("testSetFileWithConstruction3 error !!!", reader.get(new IntWritable((size * 2))));
        } catch (Exception ex) {
            Assert.fail("testSetFileWithConstruction error !!!");
        }
    }
}

