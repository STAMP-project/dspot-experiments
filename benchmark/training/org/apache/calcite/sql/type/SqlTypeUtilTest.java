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


import com.google.common.collect.ImmutableList;
import org.apache.calcite.rel.type.RelDataType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of {@link org.apache.calcite.sql.type.SqlTypeUtil}.
 */
public class SqlTypeUtilTest {
    private final SqlTypeFixture f = new SqlTypeFixture();

    @Test
    public void testTypesIsSameFamilyWithNumberTypes() {
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(f.sqlBigInt, f.sqlBigInt)), CoreMatchers.is(true));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(f.sqlInt, f.sqlBigInt)), CoreMatchers.is(true));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(f.sqlFloat, f.sqlBigInt)), CoreMatchers.is(true));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(f.sqlInt, f.sqlBigIntNullable)), CoreMatchers.is(true));
    }

    @Test
    public void testTypesIsSameFamilyWithCharTypes() {
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(f.sqlVarchar, f.sqlVarchar)), CoreMatchers.is(true));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(f.sqlVarchar, f.sqlChar)), CoreMatchers.is(true));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(f.sqlVarchar, f.sqlVarcharNullable)), CoreMatchers.is(true));
    }

    @Test
    public void testTypesIsSameFamilyWithInconvertibleTypes() {
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(f.sqlBoolean, f.sqlBigInt)), CoreMatchers.is(false));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(f.sqlFloat, f.sqlBoolean)), CoreMatchers.is(false));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(f.sqlInt, f.sqlDate)), CoreMatchers.is(false));
    }

    @Test
    public void testTypesIsSameFamilyWithNumberStructTypes() {
        final RelDataType bigIntAndFloat = struct(f.sqlBigInt, f.sqlFloat);
        final RelDataType floatAndBigInt = struct(f.sqlFloat, f.sqlBigInt);
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(bigIntAndFloat, floatAndBigInt)), CoreMatchers.is(true));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(bigIntAndFloat, bigIntAndFloat)), CoreMatchers.is(true));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(bigIntAndFloat, bigIntAndFloat)), CoreMatchers.is(true));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(floatAndBigInt, floatAndBigInt)), CoreMatchers.is(true));
    }

    @Test
    public void testTypesIsSameFamilyWithCharStructTypes() {
        final RelDataType varCharStruct = struct(f.sqlVarchar);
        final RelDataType charStruct = struct(f.sqlChar);
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(varCharStruct, charStruct)), CoreMatchers.is(true));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(charStruct, varCharStruct)), CoreMatchers.is(true));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(varCharStruct, varCharStruct)), CoreMatchers.is(true));
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(charStruct, charStruct)), CoreMatchers.is(true));
    }

    @Test
    public void testTypesIsSameFamilyWithInconvertibleStructTypes() {
        final RelDataType dateStruct = struct(f.sqlDate);
        final RelDataType boolStruct = struct(f.sqlBoolean);
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(dateStruct, boolStruct)), CoreMatchers.is(false));
        final RelDataType charIntStruct = struct(f.sqlChar, f.sqlInt);
        final RelDataType charDateStruct = struct(f.sqlChar, f.sqlDate);
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(charIntStruct, charDateStruct)), CoreMatchers.is(false));
        final RelDataType boolDateStruct = struct(f.sqlBoolean, f.sqlDate);
        final RelDataType floatIntStruct = struct(f.sqlInt, f.sqlFloat);
        Assert.assertThat(SqlTypeUtil.areSameFamily(ImmutableList.of(boolDateStruct, floatIntStruct)), CoreMatchers.is(false));
    }
}

/**
 * End SqlTypeUtilTest.java
 */
