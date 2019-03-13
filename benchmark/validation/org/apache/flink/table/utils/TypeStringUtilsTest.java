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
package org.apache.flink.table.utils;


import Types.BIG_DEC;
import Types.BOOLEAN;
import Types.BYTE;
import Types.DOUBLE;
import Types.FLOAT;
import Types.INT;
import Types.LONG;
import Types.SHORT;
import Types.SQL_DATE;
import Types.SQL_TIME;
import Types.SQL_TIMESTAMP;
import Types.STRING;
import Types.VOID;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.table.api.ValidationException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link TypeStringUtils}.
 */
public class TypeStringUtilsTest {
    @Test
    public void testPrimitiveTypes() {
        testReadAndWrite("VARCHAR", STRING);
        testReadAndWrite("BOOLEAN", BOOLEAN);
        testReadAndWrite("TINYINT", BYTE);
        testReadAndWrite("SMALLINT", SHORT);
        testReadAndWrite("INT", INT);
        testReadAndWrite("BIGINT", LONG);
        testReadAndWrite("FLOAT", FLOAT);
        testReadAndWrite("DOUBLE", DOUBLE);
        testReadAndWrite("DECIMAL", BIG_DEC);
        testReadAndWrite("DATE", SQL_DATE);
        testReadAndWrite("TIME", SQL_TIME);
        testReadAndWrite("TIMESTAMP", SQL_TIMESTAMP);
        // unsupported type information
        testReadAndWrite(("ANY<java.lang.Void, " + ((((((((("rO0ABXNyADJvcmcuYXBhY2hlLmZsaW5rLmFwaS5jb21tb24udHlwZWluZm8uQmFzaWNUeXBlSW5mb_oE8IKl" + "ad0GAgAETAAFY2xhenp0ABFMamF2YS9sYW5nL0NsYXNzO0wAD2NvbXBhcmF0b3JDbGFzc3EAfgABWwAXcG9z") + "c2libGVDYXN0VGFyZ2V0VHlwZXN0ABJbTGphdmEvbGFuZy9DbGFzcztMAApzZXJpYWxpemVydAA2TG9yZy9h") + "cGFjaGUvZmxpbmsvYXBpL2NvbW1vbi90eXBldXRpbHMvVHlwZVNlcmlhbGl6ZXI7eHIANG9yZy5hcGFjaGUu") + "ZmxpbmsuYXBpLmNvbW1vbi50eXBlaW5mby5UeXBlSW5mb3JtYXRpb26UjchIurN66wIAAHhwdnIADmphdmEu") + "bGFuZy5Wb2lkAAAAAAAAAAAAAAB4cHB1cgASW0xqYXZhLmxhbmcuQ2xhc3M7qxbXrsvNWpkCAAB4cAAAAABz") + "cgA5b3JnLmFwYWNoZS5mbGluay5hcGkuY29tbW9uLnR5cGV1dGlscy5iYXNlLlZvaWRTZXJpYWxpemVyAAAA") + "AAAAAAECAAB4cgBCb3JnLmFwYWNoZS5mbGluay5hcGkuY29tbW9uLnR5cGV1dGlscy5iYXNlLlR5cGVTZXJp") + "YWxpemVyU2luZ2xldG9ueamHqscud0UCAAB4cgA0b3JnLmFwYWNoZS5mbGluay5hcGkuY29tbW9uLnR5cGV1") + "dGlscy5UeXBlU2VyaWFsaXplcgAAAAAAAAABAgAAeHA>")), VOID);
    }

    @Test
    public void testWriteComplexTypes() {
        testReadAndWrite("ROW<f0 DECIMAL, f1 TINYINT>", Types.ROW(BIG_DEC, BYTE));
        testReadAndWrite("ROW<hello DECIMAL, world TINYINT>", Types.ROW_NAMED(new String[]{ "hello", "world" }, BIG_DEC, BYTE));
        testReadAndWrite("POJO<org.apache.flink.table.utils.TypeStringUtilsTest$TestPojo>", TypeExtractor.createTypeInfo(TypeStringUtilsTest.TestPojo.class));
        testReadAndWrite("ANY<org.apache.flink.table.utils.TypeStringUtilsTest$TestNoPojo>", TypeExtractor.createTypeInfo(TypeStringUtilsTest.TestNoPojo.class));
        testReadAndWrite("MAP<VARCHAR, ROW<f0 DECIMAL, f1 TINYINT>>", Types.MAP(STRING, Types.ROW(BIG_DEC, BYTE)));
        testReadAndWrite("MULTISET<ROW<f0 DECIMAL, f1 TINYINT>>", new org.apache.flink.api.java.typeutils.MultisetTypeInfo(Types.ROW(BIG_DEC, BYTE)));
        testReadAndWrite("PRIMITIVE_ARRAY<TINYINT>", Types.PRIMITIVE_ARRAY(BYTE));
        testReadAndWrite("OBJECT_ARRAY<POJO<org.apache.flink.table.utils.TypeStringUtilsTest$TestPojo>>", Types.OBJECT_ARRAY(TypeExtractor.createTypeInfo(TypeStringUtilsTest.TestPojo.class)));
        // test escaping
        Assert.assertEquals(Types.ROW_NAMED(new String[]{ "he         \nllo", "world" }, BIG_DEC, BYTE), TypeStringUtils.readTypeInfo("ROW<`he         \nllo` DECIMAL, world TINYINT>"));
        Assert.assertEquals(Types.ROW_NAMED(new String[]{ "he`llo", "world" }, BIG_DEC, BYTE), TypeStringUtils.readTypeInfo("ROW<`he``llo` DECIMAL, world TINYINT>"));
        // test backward compatibility with brackets ()
        Assert.assertEquals(Types.ROW_NAMED(new String[]{ "he         \nllo", "world" }, BIG_DEC, BYTE), TypeStringUtils.readTypeInfo("ROW(`he         \nllo` DECIMAL, world TINYINT)"));
        // test nesting
        testReadAndWrite("ROW<singleton ROW<f0 INT>, twoField ROW<`Field 1` ROW<f0 DECIMAL>, `Field``s 2` VARCHAR>>", Types.ROW_NAMED(new String[]{ "singleton", "twoField" }, Types.ROW(INT), Types.ROW_NAMED(new String[]{ "Field 1", "Field`s 2" }, Types.ROW(BIG_DEC), STRING)));
    }

    @Test(expected = ValidationException.class)
    public void testSyntaxError1() {
        TypeStringUtils.readTypeInfo("ROW<<f0 DECIMAL, f1 TINYINT>");// additional <

    }

    @Test(expected = ValidationException.class)
    public void testSyntaxError2() {
        TypeStringUtils.readTypeInfo("ROW<f0 DECIMAL DECIMAL, f1 TINYINT>");// duplicate type

    }

    @Test(expected = ValidationException.class)
    public void testSyntaxError3() {
        TypeStringUtils.readTypeInfo("ROW<f0 INVALID, f1 TINYINT>");// invalid type

    }

    // --------------------------------------------------------------------------------------------
    /**
     * Test POJO.
     */
    public static class TestPojo {
        public int field1;

        public String field2;
    }

    /**
     * Test invalid POJO.
     */
    public static class TestNoPojo {
        public int field1;

        public String field2;

        public TestNoPojo(int field1, String field2) {
            // no default constructor
            this.field1 = field1;
            this.field2 = field2;
        }
    }
}

