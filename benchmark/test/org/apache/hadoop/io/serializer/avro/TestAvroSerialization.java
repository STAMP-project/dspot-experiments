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
package org.apache.hadoop.io.serializer.avro;


import AvroReflectSerialization.AVRO_REFLECT_PACKAGES;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.serializer.SerializationFactory;
import org.apache.hadoop.io.serializer.SerializationTestUtil;
import org.junit.Assert;
import org.junit.Test;


public class TestAvroSerialization {
    private static final Configuration conf = new Configuration();

    @Test
    public void testSpecific() throws Exception {
        AvroRecord before = new AvroRecord();
        before.intField = 5;
        AvroRecord after = SerializationTestUtil.testSerialization(TestAvroSerialization.conf, before);
        Assert.assertEquals(before, after);
    }

    @Test
    public void testReflectPkg() throws Exception {
        Record before = new Record();
        before.x = 10;
        TestAvroSerialization.conf.set(AVRO_REFLECT_PACKAGES, before.getClass().getPackage().getName());
        Record after = SerializationTestUtil.testSerialization(TestAvroSerialization.conf, before);
        Assert.assertEquals(before, after);
    }

    @Test
    public void testAcceptHandlingPrimitivesAndArrays() throws Exception {
        SerializationFactory factory = new SerializationFactory(TestAvroSerialization.conf);
        Assert.assertNull(factory.getSerializer(byte[].class));
        Assert.assertNull(factory.getSerializer(byte.class));
    }

    @Test
    public void testReflectInnerClass() throws Exception {
        TestAvroSerialization.InnerRecord before = new TestAvroSerialization.InnerRecord();
        before.x = 10;
        TestAvroSerialization.conf.set(AVRO_REFLECT_PACKAGES, before.getClass().getPackage().getName());
        TestAvroSerialization.InnerRecord after = SerializationTestUtil.testSerialization(TestAvroSerialization.conf, before);
        Assert.assertEquals(before, after);
    }

    @Test
    public void testReflect() throws Exception {
        TestAvroSerialization.RefSerializable before = new TestAvroSerialization.RefSerializable();
        before.x = 10;
        TestAvroSerialization.RefSerializable after = SerializationTestUtil.testSerialization(TestAvroSerialization.conf, before);
        Assert.assertEquals(before, after);
    }

    public static class InnerRecord {
        public int x = 7;

        @Override
        public int hashCode() {
            return x;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            final TestAvroSerialization.InnerRecord other = ((TestAvroSerialization.InnerRecord) (obj));
            if ((x) != (other.x))
                return false;

            return true;
        }
    }

    public static class RefSerializable implements AvroReflectSerializable {
        public int x = 7;

        @Override
        public int hashCode() {
            return x;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            final TestAvroSerialization.RefSerializable other = ((TestAvroSerialization.RefSerializable) (obj));
            if ((x) != (other.x))
                return false;

            return true;
        }
    }
}

