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
package org.apache.flink.table.type;


import BasicArrayTypeInfo.DOUBLE_ARRAY_TYPE_INFO;
import InternalTypes.BINARY;
import InternalTypes.DATE;
import InternalTypes.INT;
import InternalTypes.STRING;
import InternalTypes.TIME;
import InternalTypes.TIMESTAMP;
import PrimitiveArrayTypeInfo.DOUBLE_PRIMITIVE_ARRAY_TYPE_INFO;
import java.io.IOException;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.typeutils.CompositeType;
import org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo;
import org.junit.Assert;
import org.junit.Test;

import static InternalTypes.DOUBLE;
import static InternalTypes.INT;
import static InternalTypes.STRING;


/**
 * Test for {@link InternalType}s.
 */
public class InternalTypeTest {
    @Test
    public void testHashCodeAndEquals() throws IOException, ClassNotFoundException {
        testHashCodeAndEquals(INT);
        testHashCodeAndEquals(DATE);
        testHashCodeAndEquals(TIME);
        testHashCodeAndEquals(TIMESTAMP);
        testHashCodeAndEquals(BINARY);
        testHashCodeAndEquals(STRING);
        testHashCodeAndEquals(new DecimalType(15, 5));
        testHashCodeAndEquals(new GenericType(InternalTypeTest.class));
        testHashCodeAndEquals(new RowType(STRING, INT, INT));
        testHashCodeAndEquals(new ArrayType(INT));
        testHashCodeAndEquals(new ArrayType(STRING));
        testHashCodeAndEquals(new MapType(STRING, INT));
    }

    @Test
    public void testConverter() throws IOException, ClassNotFoundException {
        testConvertToRowType(new org.apache.flink.api.java.typeutils.RowTypeInfo(new TypeInformation[]{ Types.INT, Types.STRING }, new String[]{ "field1", "field2" }));
        testConvertToRowType(((CompositeType) (TypeInformation.of(InternalTypeTest.MyPojo.class))));
        testConvertCompare(DOUBLE_PRIMITIVE_ARRAY_TYPE_INFO, new ArrayType(DOUBLE));
        testConvertCompare(DOUBLE_ARRAY_TYPE_INFO, new ArrayType(DOUBLE));
        testConvertCompare(ObjectArrayTypeInfo.getInfoFor(TypeInformation.of(InternalTypeTest.MyPojo.class)), new ArrayType(TypeConverters.createInternalTypeFromTypeInfo(TypeInformation.of(InternalTypeTest.MyPojo.class))));
        testConvertCompare(new org.apache.flink.api.java.typeutils.MapTypeInfo(Types.INT, Types.STRING), new MapType(INT, STRING));
        testConvertCompare(new org.apache.flink.api.java.typeutils.GenericTypeInfo(InternalTypeTest.MyPojo.class), new GenericType(InternalTypeTest.MyPojo.class));
    }

    @Test
    public void testDecimalInferType() {
        Assert.assertEquals(DecimalType.of(20, 13), DecimalType.inferDivisionType(5, 2, 10, 4));
        Assert.assertEquals(DecimalType.of(7, 0), DecimalType.inferIntDivType(5, 2, 4));
        Assert.assertEquals(DecimalType.of(38, 5), DecimalType.inferAggSumType(5));
        Assert.assertEquals(DecimalType.of(38, 6), DecimalType.inferAggAvgType(5));
        Assert.assertEquals(DecimalType.of(8, 2), DecimalType.inferRoundType(10, 5, 2));
        Assert.assertEquals(DecimalType.of(8, 2), DecimalType.inferRoundType(10, 5, 2));
    }

    /**
     * Test pojo.
     */
    public static class MyPojo {
        public int field1;

        public String field2;
    }
}

