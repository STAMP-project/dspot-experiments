/**
 * Copyright 2009-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.parsing;


public class AmplPropertyParserTest {
    @org.junit.Test
    public void replaceToVariableValue() {
        java.util.Properties props = new java.util.Properties();
        props.setProperty(org.apache.ibatis.parsing.PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
        props.setProperty("key", "value");
        props.setProperty("tableName", "members");
        props.setProperty("orderColumn", "member_id");
        props.setProperty("a:b", "c");
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${key}", props), org.hamcrest.core.Is.is("value"));
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${key:aaaa}", props), org.hamcrest.core.Is.is("value"));
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("SELECT * FROM ${tableName:users} ORDER BY ${orderColumn:id}", props), org.hamcrest.core.Is.is("SELECT * FROM members ORDER BY member_id"));
        props.setProperty(org.apache.ibatis.parsing.PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "false");
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${a:b}", props), org.hamcrest.core.Is.is("c"));
        props.remove(org.apache.ibatis.parsing.PropertyParser.KEY_ENABLE_DEFAULT_VALUE);
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${a:b}", props), org.hamcrest.core.Is.is("c"));
    }

    @org.junit.Test
    public void notReplace() {
        java.util.Properties props = new java.util.Properties();
        props.setProperty(org.apache.ibatis.parsing.PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${key}", props), org.hamcrest.core.Is.is("${key}"));
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${key}", null), org.hamcrest.core.Is.is("${key}"));
        props.setProperty(org.apache.ibatis.parsing.PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "false");
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${a:b}", props), org.hamcrest.core.Is.is("${a:b}"));
        props.remove(org.apache.ibatis.parsing.PropertyParser.KEY_ENABLE_DEFAULT_VALUE);
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${a:b}", props), org.hamcrest.core.Is.is("${a:b}"));
    }

    @org.junit.Test
    public void applyDefaultValue() {
        java.util.Properties props = new java.util.Properties();
        props.setProperty(org.apache.ibatis.parsing.PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${key:default}", props), org.hamcrest.core.Is.is("default"));
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("SELECT * FROM ${tableName:users} ORDER BY ${orderColumn:id}", props), org.hamcrest.core.Is.is("SELECT * FROM users ORDER BY id"));
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${key:}", props), org.hamcrest.core.Is.is(""));
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${key: }", props), org.hamcrest.core.Is.is(" "));
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${key::}", props), org.hamcrest.core.Is.is(":"));
    }

    @org.junit.Test
    public void applyCustomSeparator() {
        java.util.Properties props = new java.util.Properties();
        props.setProperty(org.apache.ibatis.parsing.PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");
        props.setProperty(org.apache.ibatis.parsing.PropertyParser.KEY_DEFAULT_VALUE_SEPARATOR, "?:");
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${key?:default}", props), org.hamcrest.core.Is.is("default"));
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("SELECT * FROM ${schema?:prod}.${tableName == null ? 'users' : tableName} ORDER BY ${orderColumn}", props), org.hamcrest.core.Is.is("SELECT * FROM prod.${tableName == null ? 'users' : tableName} ORDER BY ${orderColumn}"));
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${key?:}", props), org.hamcrest.core.Is.is(""));
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${key?: }", props), org.hamcrest.core.Is.is(" "));
        org.junit.Assert.assertThat(org.apache.ibatis.parsing.PropertyParser.parse("${key?::}", props), org.hamcrest.core.Is.is(":"));
    }
}

