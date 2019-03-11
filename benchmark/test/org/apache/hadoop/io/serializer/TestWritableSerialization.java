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
package org.apache.hadoop.io.serializer;


import java.io.Serializable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.TestGenericWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparator;
import org.junit.Assert;
import org.junit.Test;


public class TestWritableSerialization {
    private static final Configuration conf = new Configuration();

    @Test
    public void testWritableSerialization() throws Exception {
        Text before = new Text("test writable");
        Text after = SerializationTestUtil.testSerialization(TestWritableSerialization.conf, before);
        Assert.assertEquals(before, after);
    }

    @Test
    public void testWritableConfigurable() throws Exception {
        // set the configuration parameter
        TestWritableSerialization.conf.set(TestGenericWritable.CONF_TEST_KEY, TestGenericWritable.CONF_TEST_VALUE);
        // reuse TestGenericWritable inner classes to test
        // writables that also implement Configurable.
        TestGenericWritable.FooGenericWritable generic = new TestGenericWritable.FooGenericWritable();
        generic.setConf(TestWritableSerialization.conf);
        TestGenericWritable.Baz baz = new TestGenericWritable.Baz();
        set(baz);
        TestGenericWritable.Baz result = SerializationTestUtil.testSerialization(TestWritableSerialization.conf, baz);
        Assert.assertEquals(baz, result);
        Assert.assertNotNull(result.getConf());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testWritableComparatorJavaSerialization() throws Exception {
        Serialization ser = new JavaSerialization();
        Serializer<TestWritableSerialization.TestWC> serializer = ser.getSerializer(TestWritableSerialization.TestWC.class);
        DataOutputBuffer dob = new DataOutputBuffer();
        serializer.open(dob);
        TestWritableSerialization.TestWC orig = new TestWritableSerialization.TestWC(0);
        serializer.serialize(orig);
        serializer.close();
        Deserializer<TestWritableSerialization.TestWC> deserializer = ser.getDeserializer(TestWritableSerialization.TestWC.class);
        DataInputBuffer dib = new DataInputBuffer();
        dib.reset(dob.getData(), 0, dob.getLength());
        deserializer.open(dib);
        TestWritableSerialization.TestWC deser = deserializer.deserialize(null);
        deserializer.close();
        Assert.assertEquals(orig, deser);
    }

    static class TestWC extends WritableComparator implements Serializable {
        static final long serialVersionUID = 17220;

        final int val;

        TestWC() {
            this(7);
        }

        TestWC(int val) {
            this.val = val;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof TestWritableSerialization.TestWC) {
                return (((TestWritableSerialization.TestWC) (o)).val) == (val);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return val;
        }
    }
}

