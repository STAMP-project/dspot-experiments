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
package org.apache.calcite.adapter.enumerable;


import JavaRowFormat.ARRAY;
import SqlTypeName.INTEGER;
import SqlTypeName.VARCHAR;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link org.apache.calcite.adapter.enumerable.PhysTypeImpl}.
 */
public final class PhysTypeTest {
    private static final JavaTypeFactory TYPE_FACTORY = new JavaTypeFactoryImpl();

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2677">[CALCITE-2677]
     * Struct types with one field are not mapped correctly to Java Classes</a>.
     */
    @Test
    public void testFieldClassOnColumnOfOneFieldStructType() {
        RelDataType columnType = PhysTypeTest.TYPE_FACTORY.createStructType(ImmutableList.of(PhysTypeTest.TYPE_FACTORY.createSqlType(INTEGER)), ImmutableList.of("intField"));
        RelDataType rowType = PhysTypeTest.TYPE_FACTORY.createStructType(ImmutableList.of(columnType), ImmutableList.of("structField"));
        PhysType rowPhysType = PhysTypeImpl.of(PhysTypeTest.TYPE_FACTORY, rowType, ARRAY);
        Assert.assertEquals(Object[].class, rowPhysType.fieldClass(0));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2677">[CALCITE-2677]
     * Struct types with one field are not mapped correctly to Java Classes</a>.
     */
    @Test
    public void testFieldClassOnColumnOfTwoFieldStructType() {
        RelDataType columnType = PhysTypeTest.TYPE_FACTORY.createStructType(ImmutableList.of(PhysTypeTest.TYPE_FACTORY.createSqlType(INTEGER), PhysTypeTest.TYPE_FACTORY.createSqlType(VARCHAR)), ImmutableList.of("intField", "strField"));
        RelDataType rowType = PhysTypeTest.TYPE_FACTORY.createStructType(ImmutableList.of(columnType), ImmutableList.of("structField"));
        PhysType rowPhysType = PhysTypeImpl.of(PhysTypeTest.TYPE_FACTORY, rowType, ARRAY);
        Assert.assertEquals(Object[].class, rowPhysType.fieldClass(0));
    }
}

/**
 * End PhysTypeTest.java
 */
