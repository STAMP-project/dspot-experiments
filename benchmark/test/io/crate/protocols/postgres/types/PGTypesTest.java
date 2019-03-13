/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.protocols.postgres.types;


import DataTypes.BOOLEAN;
import DataTypes.DOUBLE;
import DataTypes.FLOAT;
import DataTypes.INTEGER;
import DataTypes.IP;
import DataTypes.LONG;
import DataTypes.SHORT;
import DataTypes.STRING;
import DataTypes.TIMESTAMP;
import PGArray.CHAR_ARRAY;
import VarCharType.OID;
import com.google.common.collect.ImmutableList;
import io.crate.test.integration.CrateUnitTest;
import io.crate.types.ArrayType;
import io.crate.types.DataType;
import io.crate.types.DataTypes;
import io.crate.types.FloatType;
import io.crate.types.LongType;
import io.crate.types.ObjectType;
import io.crate.types.ShortType;
import io.crate.types.StringType;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class PGTypesTest extends CrateUnitTest {
    @Test
    public void testCrate2PGType() throws Exception {
        assertThat(PGTypes.get(STRING), Matchers.instanceOf(VarCharType.class));
        assertThat(PGTypes.get(ObjectType.untyped()), Matchers.instanceOf(JsonType.class));
        assertThat(PGTypes.get(BOOLEAN), Matchers.instanceOf(BooleanType.class));
        assertThat(PGTypes.get(SHORT), Matchers.instanceOf(SmallIntType.class));
        assertThat(PGTypes.get(INTEGER), Matchers.instanceOf(IntegerType.class));
        assertThat(PGTypes.get(LONG), Matchers.instanceOf(BigIntType.class));
        assertThat(PGTypes.get(FLOAT), Matchers.instanceOf(RealType.class));
        assertThat(PGTypes.get(DOUBLE), Matchers.instanceOf(DoubleType.class));
        assertThat("Crate IP type is mapped to PG varchar", PGTypes.get(IP), Matchers.instanceOf(VarCharType.class));
        assertThat("Crate array type is mapped to PGArray", PGTypes.get(new ArrayType(DataTypes.BYTE)), Matchers.instanceOf(PGArray.class));
        assertThat("Crate set type is mapped to PGArray", PGTypes.get(new io.crate.types.SetType(DataTypes.BYTE)), Matchers.instanceOf(PGArray.class));
    }

    @Test
    public void testPG2CrateType() throws Exception {
        assertThat(PGTypes.fromOID(OID), Matchers.instanceOf(StringType.class));
        assertThat(PGTypes.fromOID(JsonType.OID), Matchers.instanceOf(ObjectType.class));
        assertThat(PGTypes.fromOID(BooleanType.OID), Matchers.instanceOf(io.crate.types.BooleanType.class));
        assertThat(PGTypes.fromOID(SmallIntType.OID), Matchers.instanceOf(ShortType.class));
        assertThat(PGTypes.fromOID(IntegerType.OID), Matchers.instanceOf(io.crate.types.IntegerType.class));
        assertThat(PGTypes.fromOID(BigIntType.OID), Matchers.instanceOf(LongType.class));
        assertThat(PGTypes.fromOID(RealType.OID), Matchers.instanceOf(FloatType.class));
        assertThat(PGTypes.fromOID(DoubleType.OID), Matchers.instanceOf(io.crate.types.DoubleType.class));
        assertThat("PG array is mapped to Crate Array", PGTypes.fromOID(CHAR_ARRAY.oid()), Matchers.instanceOf(ArrayType.class));
    }

    @Test
    public void testTextOidIsMappedToString() {
        assertThat(PGTypes.fromOID(25), Is.is(STRING));
        assertThat(PGTypes.fromOID(1009), Is.is(new ArrayType(DataTypes.STRING)));
    }

    private static class Entry {
        final DataType type;

        final Object value;

        public Entry(DataType type, Object value) {
            this.type = type;
            this.value = value;
        }
    }

    @Test
    public void testByteReadWrite() throws Exception {
        for (PGTypesTest.Entry entry : ImmutableList.of(new PGTypesTest.Entry(DataTypes.STRING, "foobar"), new PGTypesTest.Entry(DataTypes.LONG, 392873L), new PGTypesTest.Entry(DataTypes.INTEGER, 1234), new PGTypesTest.Entry(DataTypes.SHORT, ((short) (42))), new PGTypesTest.Entry(DataTypes.FLOAT, 42.3F), new PGTypesTest.Entry(DataTypes.DOUBLE, 42.00003), new PGTypesTest.Entry(DataTypes.BOOLEAN, true), new PGTypesTest.Entry(DataTypes.TIMESTAMP, TIMESTAMP.value("2014-05-08")), new PGTypesTest.Entry(DataTypes.TIMESTAMP, TIMESTAMP.value("2014-05-08T16:34:33.123")), new PGTypesTest.Entry(DataTypes.TIMESTAMP, TIMESTAMP.value(999999999999999L)), new PGTypesTest.Entry(DataTypes.TIMESTAMP, TIMESTAMP.value((-999999999999999L))), new PGTypesTest.Entry(DataTypes.IP, "192.168.1.1"), new PGTypesTest.Entry(DataTypes.BYTE, ((byte) (20))), new PGTypesTest.Entry(new ArrayType(DataTypes.INTEGER), new Integer[]{ 10, null, 20 }), new PGTypesTest.Entry(new ArrayType(DataTypes.INTEGER), new Integer[0]), new PGTypesTest.Entry(new ArrayType(DataTypes.INTEGER), new Integer[]{ null, null }), new PGTypesTest.Entry(new ArrayType(DataTypes.INTEGER), new Integer[][]{ new Integer[]{ 10, null, 20 }, new Integer[]{ 1, 2, 3 } }), new PGTypesTest.Entry(new io.crate.types.SetType(DataTypes.STRING), new Object[]{ "test" }), new PGTypesTest.Entry(new io.crate.types.SetType(DataTypes.INTEGER), new Integer[]{ 10, null, 20 }))) {
            PGType pgType = PGTypes.get(entry.type);
            Object streamedValue = writeAndReadBinary(entry, pgType);
            assertThat(streamedValue, Is.is(entry.value));
            streamedValue = writeAndReadAsText(entry, pgType);
            assertThat(streamedValue, Is.is(entry.value));
        }
    }
}

