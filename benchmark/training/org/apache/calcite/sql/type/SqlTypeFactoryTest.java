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
package org.apache.calcite.sql.type;


import SqlTypeName.ANY;
import SqlTypeName.BIGINT;
import SqlTypeName.INTEGER;
import SqlTypeName.NULL;
import SqlTypeName.VARCHAR;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link SqlTypeFactoryImpl}.
 */
public class SqlTypeFactoryTest {
    @Test
    public void testLeastRestrictiveWithAny() {
        SqlTypeFixture f = new SqlTypeFixture();
        RelDataType leastRestrictive = f.typeFactory.leastRestrictive(Lists.newArrayList(f.sqlBigInt, f.sqlAny));
        Assert.assertThat(leastRestrictive.getSqlTypeName(), Is.is(ANY));
    }

    @Test
    public void testLeastRestrictiveWithNumbers() {
        SqlTypeFixture f = new SqlTypeFixture();
        RelDataType leastRestrictive = f.typeFactory.leastRestrictive(Lists.newArrayList(f.sqlBigInt, f.sqlInt));
        Assert.assertThat(leastRestrictive.getSqlTypeName(), Is.is(BIGINT));
    }

    @Test
    public void testLeastRestrictiveWithNullability() {
        SqlTypeFixture f = new SqlTypeFixture();
        RelDataType leastRestrictive = f.typeFactory.leastRestrictive(Lists.newArrayList(f.sqlVarcharNullable, f.sqlAny));
        Assert.assertThat(leastRestrictive.getSqlTypeName(), Is.is(ANY));
        Assert.assertThat(leastRestrictive.isNullable(), Is.is(true));
    }

    @Test
    public void testLeastRestrictiveWithNull() {
        SqlTypeFixture f = new SqlTypeFixture();
        RelDataType leastRestrictive = f.typeFactory.leastRestrictive(Lists.newArrayList(f.sqlNull, f.sqlNull));
        Assert.assertThat(leastRestrictive.getSqlTypeName(), Is.is(NULL));
        Assert.assertThat(leastRestrictive.isNullable(), Is.is(true));
    }

    /**
     * Unit test for {@link SqlTypeUtil#comparePrecision(int, int)}
     * and  {@link SqlTypeUtil#maxPrecision(int, int)}.
     */
    @Test
    public void testMaxPrecision() {
        final int un = RelDataType.PRECISION_NOT_SPECIFIED;
        checkPrecision(1, 1, 1, 0);
        checkPrecision(2, 1, 2, 1);
        checkPrecision(2, 100, 100, (-1));
        checkPrecision(2, un, un, (-1));
        checkPrecision(un, 2, un, 1);
        checkPrecision(un, un, un, 0);
    }

    /**
     * Unit test for {@link ArraySqlType#getPrecedenceList()}.
     */
    @Test
    public void testArrayPrecedenceList() {
        SqlTypeFixture f = new SqlTypeFixture();
        Assert.assertThat(checkPrecendenceList(f.arrayBigInt, f.arrayBigInt, f.arrayFloat), Is.is(3));
        Assert.assertThat(checkPrecendenceList(f.arrayOfArrayBigInt, f.arrayOfArrayBigInt, f.arrayOfArrayFloat), Is.is(3));
        Assert.assertThat(checkPrecendenceList(f.sqlBigInt, f.sqlBigInt, f.sqlFloat), Is.is(3));
        Assert.assertThat(checkPrecendenceList(f.multisetBigInt, f.multisetBigInt, f.multisetFloat), Is.is(3));
        Assert.assertThat(checkPrecendenceList(f.arrayBigInt, f.arrayBigInt, f.arrayBigIntNullable), Is.is(0));
        try {
            int i = checkPrecendenceList(f.arrayBigInt, f.sqlBigInt, f.sqlInt);
            Assert.fail(("Expected assert, got " + i));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Is.is("must contain type: BIGINT"));
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2464">[CALCITE-2464]
     * Allow to set nullability for columns of structured types</a>.
     */
    @Test
    public void createStructTypeWithNullability() {
        SqlTypeFixture f = new SqlTypeFixture();
        RelDataTypeFactory typeFactory = f.typeFactory;
        List<RelDataTypeField> fields = new ArrayList<>();
        RelDataTypeField field0 = new org.apache.calcite.rel.type.RelDataTypeFieldImpl("i", 0, typeFactory.createSqlType(INTEGER));
        RelDataTypeField field1 = new org.apache.calcite.rel.type.RelDataTypeFieldImpl("s", 1, typeFactory.createSqlType(VARCHAR));
        fields.add(field0);
        fields.add(field1);
        final RelDataType recordType = new org.apache.calcite.rel.type.RelRecordType(fields);// nullable false by default

        final RelDataType copyRecordType = typeFactory.createTypeWithNullability(recordType, true);
        Assert.assertFalse(recordType.isNullable());
        Assert.assertTrue(copyRecordType.isNullable());
    }
}

/**
 * End SqlTypeFactoryTest.java
 */
