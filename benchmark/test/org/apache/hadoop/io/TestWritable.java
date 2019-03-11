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


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for Writable.
 */
public class TestWritable {
    private static final String TEST_CONFIG_PARAM = "frob.test";

    private static final String TEST_CONFIG_VALUE = "test";

    private static final String TEST_WRITABLE_CONFIG_PARAM = "test.writable";

    private static final String TEST_WRITABLE_CONFIG_VALUE = TestWritable.TEST_CONFIG_VALUE;

    /**
     * Example class used in test cases below.
     */
    public static class SimpleWritable implements Writable {
        private static final Random RANDOM = new Random();

        int state = TestWritable.SimpleWritable.RANDOM.nextInt();

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeInt(state);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            this.state = in.readInt();
        }

        public static TestWritable.SimpleWritable read(DataInput in) throws IOException {
            TestWritable.SimpleWritable result = new TestWritable.SimpleWritable();
            result.readFields(in);
            return result;
        }

        /**
         * Required by test code, below.
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestWritable.SimpleWritable))
                return false;

            TestWritable.SimpleWritable other = ((TestWritable.SimpleWritable) (o));
            return (this.state) == (other.state);
        }
    }

    public static class SimpleWritableComparable extends TestWritable.SimpleWritable implements Configurable , WritableComparable<TestWritable.SimpleWritableComparable> {
        private Configuration conf;

        public SimpleWritableComparable() {
        }

        public void setConf(Configuration conf) {
            this.conf = conf;
        }

        public Configuration getConf() {
            return this.conf;
        }

        public int compareTo(TestWritable.SimpleWritableComparable o) {
            return (this.state) - (o.state);
        }
    }

    /**
     * Test 1: Check that SimpleWritable.
     */
    @Test
    public void testSimpleWritable() throws Exception {
        TestWritable.testWritable(new TestWritable.SimpleWritable());
    }

    @Test
    public void testByteWritable() throws Exception {
        TestWritable.testWritable(new ByteWritable(((byte) (128))));
    }

    @Test
    public void testShortWritable() throws Exception {
        TestWritable.testWritable(new ShortWritable(((byte) (256))));
    }

    @Test
    public void testDoubleWritable() throws Exception {
        TestWritable.testWritable(new DoubleWritable(1.0));
    }

    private static class FrobComparator extends WritableComparator {
        public FrobComparator() {
            super(TestWritable.Frob.class);
        }

        @Override
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return 0;
        }
    }

    private static class Frob implements WritableComparable<TestWritable.Frob> {
        static {
            // register default comparator
            WritableComparator.define(TestWritable.Frob.class, new TestWritable.FrobComparator());
        }

        @Override
        public void write(DataOutput out) throws IOException {
        }

        @Override
        public void readFields(DataInput in) throws IOException {
        }

        @Override
        public int compareTo(TestWritable.Frob o) {
            return 0;
        }
    }

    /**
     * Test a user comparator that relies on deserializing both arguments for each
     * compare.
     */
    @Test
    public void testShortWritableComparator() throws Exception {
        ShortWritable writable1 = new ShortWritable(((short) (256)));
        ShortWritable writable2 = new ShortWritable(((short) (128)));
        ShortWritable writable3 = new ShortWritable(((short) (256)));
        final String SHOULD_NOT_MATCH_WITH_RESULT_ONE = "Result should be 1, should not match the writables";
        Assert.assertTrue(SHOULD_NOT_MATCH_WITH_RESULT_ONE, ((writable1.compareTo(writable2)) == 1));
        Assert.assertTrue(SHOULD_NOT_MATCH_WITH_RESULT_ONE, ((WritableComparator.get(ShortWritable.class).compare(writable1, writable2)) == 1));
        final String SHOULD_NOT_MATCH_WITH_RESULT_MINUS_ONE = "Result should be -1, should not match the writables";
        Assert.assertTrue(SHOULD_NOT_MATCH_WITH_RESULT_MINUS_ONE, ((writable2.compareTo(writable1)) == (-1)));
        Assert.assertTrue(SHOULD_NOT_MATCH_WITH_RESULT_MINUS_ONE, ((WritableComparator.get(ShortWritable.class).compare(writable2, writable1)) == (-1)));
        final String SHOULD_MATCH = "Result should be 0, should match the writables";
        Assert.assertTrue(SHOULD_MATCH, ((writable1.compareTo(writable1)) == 0));
        Assert.assertTrue(SHOULD_MATCH, ((WritableComparator.get(ShortWritable.class).compare(writable1, writable3)) == 0));
    }

    /**
     * Test that Writable's are configured by Comparator.
     */
    @Test
    public void testConfigurableWritableComparator() throws Exception {
        Configuration conf = new Configuration();
        conf.set(TestWritable.TEST_WRITABLE_CONFIG_PARAM, TestWritable.TEST_WRITABLE_CONFIG_VALUE);
        WritableComparator wc = WritableComparator.get(TestWritable.SimpleWritableComparable.class, conf);
        TestWritable.SimpleWritableComparable key = ((TestWritable.SimpleWritableComparable) (wc.newKey()));
        Assert.assertNotNull(wc.getConf());
        Assert.assertNotNull(key.getConf());
        Assert.assertEquals(key.getConf().get(TestWritable.TEST_WRITABLE_CONFIG_PARAM), TestWritable.TEST_WRITABLE_CONFIG_VALUE);
    }
}

