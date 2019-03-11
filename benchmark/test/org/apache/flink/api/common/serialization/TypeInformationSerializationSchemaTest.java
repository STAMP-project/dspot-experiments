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
package org.apache.flink.api.common.serialization;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link TypeInformationSerializationSchema}.
 */
public class TypeInformationSerializationSchemaTest {
    @Test
    public void testDeSerialization() {
        try {
            TypeInformation<TypeInformationSerializationSchemaTest.MyPOJO> info = TypeExtractor.getForClass(TypeInformationSerializationSchemaTest.MyPOJO.class);
            TypeInformationSerializationSchema<TypeInformationSerializationSchemaTest.MyPOJO> schema = new TypeInformationSerializationSchema<TypeInformationSerializationSchemaTest.MyPOJO>(info, new ExecutionConfig());
            TypeInformationSerializationSchemaTest.MyPOJO[] types = new TypeInformationSerializationSchemaTest.MyPOJO[]{ new TypeInformationSerializationSchemaTest.MyPOJO(72, new Date(763784523L), new Date(88234L)), new TypeInformationSerializationSchemaTest.MyPOJO((-1), new Date(11111111111111L)), new TypeInformationSerializationSchemaTest.MyPOJO(42), new TypeInformationSerializationSchemaTest.MyPOJO(17, new Date(222763784523L)) };
            for (TypeInformationSerializationSchemaTest.MyPOJO val : types) {
                byte[] serialized = schema.serialize(val);
                TypeInformationSerializationSchemaTest.MyPOJO deser = schema.deserialize(serialized);
                Assert.assertEquals(val, deser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSerializability() {
        try {
            TypeInformation<TypeInformationSerializationSchemaTest.MyPOJO> info = TypeExtractor.getForClass(TypeInformationSerializationSchemaTest.MyPOJO.class);
            TypeInformationSerializationSchema<TypeInformationSerializationSchemaTest.MyPOJO> schema = new TypeInformationSerializationSchema<TypeInformationSerializationSchemaTest.MyPOJO>(info, new ExecutionConfig());
            // this needs to succeed
            CommonTestUtils.createCopySerializable(schema);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    // ------------------------------------------------------------------------
    // Test data types
    // ------------------------------------------------------------------------
    private static class MyPOJO {
        public int aField;

        public List<Date> aList;

        public MyPOJO() {
        }

        public MyPOJO(int iVal, Date... dates) {
            this.aField = iVal;
            this.aList = new ArrayList<>(Arrays.asList(dates));
        }

        @Override
        public int hashCode() {
            return aField;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TypeInformationSerializationSchemaTest.MyPOJO) {
                TypeInformationSerializationSchemaTest.MyPOJO that = ((TypeInformationSerializationSchemaTest.MyPOJO) (obj));
                return ((this.aField) == (that.aField)) && ((this.aList) == null ? (that.aList) == null : ((that.aList) != null) && (this.aList.equals(that.aList)));
            }
            return super.equals(obj);
        }

        @Override
        public String toString() {
            return (("MyPOJO " + (aField)) + " ") + (aList);
        }
    }
}

