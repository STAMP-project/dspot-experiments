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
package org.apache.avro.reflect;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.junit.Assert;
import org.junit.Test;


public class TestReflectDatumReader {
    @Test
    public void testRead_PojoWithList() throws IOException {
        TestReflectDatumReader.PojoWithList pojoWithList = new TestReflectDatumReader.PojoWithList();
        pojoWithList.setId(42);
        pojoWithList.setRelatedIds(Arrays.asList(1, 2, 3));
        byte[] serializedBytes = TestReflectDatumReader.serializeWithReflectDatumWriter(pojoWithList, TestReflectDatumReader.PojoWithList.class);
        Decoder decoder = DecoderFactory.get().binaryDecoder(serializedBytes, null);
        ReflectDatumReader<TestReflectDatumReader.PojoWithList> reflectDatumReader = new ReflectDatumReader(TestReflectDatumReader.PojoWithList.class);
        TestReflectDatumReader.PojoWithList deserialized = new TestReflectDatumReader.PojoWithList();
        reflectDatumReader.read(deserialized, decoder);
        Assert.assertEquals(pojoWithList, deserialized);
    }

    @Test
    public void testRead_PojoWithArray() throws IOException {
        TestReflectDatumReader.PojoWithArray pojoWithArray = new TestReflectDatumReader.PojoWithArray();
        pojoWithArray.setId(42);
        pojoWithArray.setRelatedIds(new int[]{ 1, 2, 3 });
        byte[] serializedBytes = TestReflectDatumReader.serializeWithReflectDatumWriter(pojoWithArray, TestReflectDatumReader.PojoWithArray.class);
        Decoder decoder = DecoderFactory.get().binaryDecoder(serializedBytes, null);
        ReflectDatumReader<TestReflectDatumReader.PojoWithArray> reflectDatumReader = new ReflectDatumReader(TestReflectDatumReader.PojoWithArray.class);
        TestReflectDatumReader.PojoWithArray deserialized = new TestReflectDatumReader.PojoWithArray();
        reflectDatumReader.read(deserialized, decoder);
        Assert.assertEquals(pojoWithArray, deserialized);
    }

    public static class PojoWithList {
        private int id;

        private List<Integer> relatedIds;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<Integer> getRelatedIds() {
            return relatedIds;
        }

        public void setRelatedIds(List<Integer> relatedIds) {
            this.relatedIds = relatedIds;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + (id);
            result = (prime * result) + ((relatedIds) == null ? 0 : relatedIds.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            TestReflectDatumReader.PojoWithList other = ((TestReflectDatumReader.PojoWithList) (obj));
            if ((id) != (other.id))
                return false;

            if ((relatedIds) == null) {
                if ((other.relatedIds) != null)
                    return false;

            } else
                if (!(relatedIds.equals(other.relatedIds)))
                    return false;


            return true;
        }
    }

    public static class PojoWithArray {
        private int id;

        private int[] relatedIds;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int[] getRelatedIds() {
            return relatedIds;
        }

        public void setRelatedIds(int[] relatedIds) {
            this.relatedIds = relatedIds;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + (id);
            result = (prime * result) + (Arrays.hashCode(relatedIds));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            TestReflectDatumReader.PojoWithArray other = ((TestReflectDatumReader.PojoWithArray) (obj));
            if ((id) != (other.id))
                return false;

            if (!(Arrays.equals(relatedIds, other.relatedIds)))
                return false;

            return true;
        }
    }
}

