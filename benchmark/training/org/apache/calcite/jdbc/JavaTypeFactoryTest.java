/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.jdbc;


import SqlTypeName.INTEGER;
import SqlTypeName.VARCHAR;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.rel.type.RelDataType;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link org.apache.calcite.jdbc.JavaTypeFactoryImpl}.
 */
public final class JavaTypeFactoryTest {
    private static final JavaTypeFactoryImpl TYPE_FACTORY = new JavaTypeFactoryImpl();

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2677">[CALCITE-2677]
     * Struct types with one field are not mapped correctly to Java Classes</a>.
     */
    @Test
    public void testGetJavaClassWithOneFieldStructDataTypeV1() {
        RelDataType structWithOneField = JavaTypeFactoryTest.TYPE_FACTORY.createStructType(JavaTypeFactoryTest.OneFieldStruct.class);
        Assert.assertEquals(JavaTypeFactoryTest.OneFieldStruct.class, JavaTypeFactoryTest.TYPE_FACTORY.getJavaClass(structWithOneField));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2677">[CALCITE-2677]
     * Struct types with one field are not mapped correctly to Java Classes</a>.
     */
    @Test
    public void testGetJavaClassWithOneFieldStructDataTypeV2() {
        RelDataType structWithOneField = JavaTypeFactoryTest.TYPE_FACTORY.createStructType(ImmutableList.of(JavaTypeFactoryTest.TYPE_FACTORY.createSqlType(INTEGER)), ImmutableList.of("intField"));
        assertRecordType(JavaTypeFactoryTest.TYPE_FACTORY.getJavaClass(structWithOneField));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2677">[CALCITE-2677]
     * Struct types with one field are not mapped correctly to Java Classes</a>.
     */
    @Test
    public void testGetJavaClassWithTwoFieldsStructDataType() {
        RelDataType structWithTwoFields = JavaTypeFactoryTest.TYPE_FACTORY.createStructType(JavaTypeFactoryTest.TwoFieldStruct.class);
        Assert.assertEquals(JavaTypeFactoryTest.TwoFieldStruct.class, JavaTypeFactoryTest.TYPE_FACTORY.getJavaClass(structWithTwoFields));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2677">[CALCITE-2677]
     * Struct types with one field are not mapped correctly to Java Classes</a>.
     */
    @Test
    public void testGetJavaClassWithTwoFieldsStructDataTypeV2() {
        RelDataType structWithTwoFields = JavaTypeFactoryTest.TYPE_FACTORY.createStructType(ImmutableList.of(JavaTypeFactoryTest.TYPE_FACTORY.createSqlType(INTEGER), JavaTypeFactoryTest.TYPE_FACTORY.createSqlType(VARCHAR)), ImmutableList.of("intField", "strField"));
        assertRecordType(JavaTypeFactoryTest.TYPE_FACTORY.getJavaClass(structWithTwoFields));
    }

    /**
     *
     */
    private static class OneFieldStruct {
        public Integer intField;
    }

    /**
     *
     */
    private static class TwoFieldStruct {
        public Integer intField;

        public String strField;
    }
}

/**
 * End JavaTypeFactoryTest.java
 */
