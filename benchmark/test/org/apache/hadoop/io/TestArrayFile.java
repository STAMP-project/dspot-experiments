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


import ArrayFile.Reader;
import ArrayFile.Writer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Progressable;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Support for flat files of binary key/value pairs.
 */
public class TestArrayFile {
    private static final Logger LOG = LoggerFactory.getLogger(TestArrayFile.class);

    private static final Path TEST_DIR = new Path(GenericTestUtils.getTempPath(TestMapFile.class.getSimpleName()));

    private static String TEST_FILE = new Path(TestArrayFile.TEST_DIR, "test.array").toString();

    @Test
    public void testArrayFile() throws Exception {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.getLocal(conf);
        RandomDatum[] data = TestArrayFile.generate(10000);
        TestArrayFile.writeTest(fs, data, TestArrayFile.TEST_FILE);
        TestArrayFile.readTest(fs, data, TestArrayFile.TEST_FILE, conf);
    }

    @Test
    public void testEmptyFile() throws Exception {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.getLocal(conf);
        TestArrayFile.writeTest(fs, new RandomDatum[0], TestArrayFile.TEST_FILE);
        ArrayFile.Reader reader = new ArrayFile.Reader(fs, TestArrayFile.TEST_FILE, conf);
        Assert.assertNull(reader.get(0, new RandomDatum()));
        reader.close();
    }

    /**
     * test on {@link ArrayFile.Reader} iteration methods
     * <pre>
     * {@code next(), seek()} in and out of range.
     * </pre>
     */
    @Test
    public void testArrayFileIteration() {
        int SIZE = 10;
        Configuration conf = new Configuration();
        try {
            FileSystem fs = FileSystem.get(conf);
            ArrayFile.Writer writer = new ArrayFile.Writer(conf, fs, TestArrayFile.TEST_FILE, LongWritable.class, CompressionType.RECORD, TestArrayFile.defaultProgressable);
            Assert.assertNotNull("testArrayFileIteration error !!!", writer);
            for (int i = 0; i < SIZE; i++)
                writer.append(new LongWritable(i));

            writer.close();
            ArrayFile.Reader reader = new ArrayFile.Reader(fs, TestArrayFile.TEST_FILE, conf);
            LongWritable nextWritable = new LongWritable(0);
            for (int i = 0; i < SIZE; i++) {
                nextWritable = ((LongWritable) (reader.next(nextWritable)));
                Assert.assertEquals(nextWritable.get(), i);
            }
            Assert.assertTrue("testArrayFileIteration seek error !!!", reader.seek(new LongWritable(6)));
            nextWritable = ((LongWritable) (reader.next(nextWritable)));
            Assert.assertTrue("testArrayFileIteration error !!!", ((reader.key()) == 7));
            Assert.assertTrue("testArrayFileIteration error !!!", nextWritable.equals(new LongWritable(7)));
            Assert.assertFalse("testArrayFileIteration error !!!", reader.seek(new LongWritable((SIZE + 5))));
            reader.close();
        } catch (Exception ex) {
            Assert.fail("testArrayFileWriterConstruction error !!!");
        }
    }

    private static final Progressable defaultProgressable = new Progressable() {
        @Override
        public void progress() {
        }
    };
}

