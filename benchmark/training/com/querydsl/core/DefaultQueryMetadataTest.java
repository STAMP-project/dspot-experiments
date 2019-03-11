/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core;


import JoinType.DEFAULT;
import com.google.common.collect.ImmutableSet;
import com.querydsl.core.QueryFlag.Position;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

import static JoinType.DEFAULT;


public class DefaultQueryMetadataTest {
    private final QueryMetadata metadata = new DefaultQueryMetadata();

    public DefaultQueryMetadataTest() {
        metadata.setValidate(true);
    }

    private final StringPath str = Expressions.stringPath("str");

    private final StringPath str2 = Expressions.stringPath("str2");

    @Test
    public void addWhere_with_null() {
        metadata.addWhere(null);
    }

    @Test
    public void addWhere_with_booleanBuilder() {
        metadata.addWhere(new BooleanBuilder());
    }

    @Test
    public void addHaving_with_null() {
        metadata.addHaving(null);
    }

    @Test
    public void addHaving_with_booleanBuilder() {
        metadata.addHaving(new BooleanBuilder());
    }

    @Test(expected = IllegalArgumentException.class)
    public void validation() {
        metadata.addWhere(str.isNull());
    }

    @Test
    public void validation_no_error_for_groupBy() {
        metadata.addGroupBy(str);
    }

    @Test
    public void validation_no_error_for_having() {
        metadata.addHaving(str.isNull());
    }

    @Test
    public void getGroupBy() {
        metadata.addJoin(DEFAULT, str);
        metadata.addGroupBy(str);
        Assert.assertEquals(Arrays.asList(str), metadata.getGroupBy());
    }

    @Test
    public void getHaving() {
        metadata.addJoin(DEFAULT, str);
        metadata.addHaving(str.isNotNull());
        Assert.assertEquals(str.isNotNull(), metadata.getHaving());
    }

    @Test
    public void getJoins() {
        metadata.addJoin(DEFAULT, str);
        Assert.assertEquals(Arrays.asList(new JoinExpression(DEFAULT, str)), metadata.getJoins());
    }

    @Test
    public void getJoins2() {
        metadata.addJoin(DEFAULT, str);
        Assert.assertEquals(Arrays.asList(new JoinExpression(DEFAULT, str)), metadata.getJoins());
    }

    @Test
    public void getJoins3() {
        metadata.addJoin(DEFAULT, str);
        Assert.assertEquals(Arrays.asList(new JoinExpression(DEFAULT, str)), metadata.getJoins());
        metadata.addJoinCondition(str.isNull());
        Assert.assertEquals(Arrays.asList(new JoinExpression(DEFAULT, str, str.isNull(), ImmutableSet.<JoinFlag>of())), metadata.getJoins());
        metadata.addJoin(DEFAULT, str2);
        Assert.assertEquals(Arrays.asList(new JoinExpression(DEFAULT, str, str.isNull(), ImmutableSet.<JoinFlag>of()), new JoinExpression(DEFAULT, str2)), metadata.getJoins());
    }

    @Test
    public void getModifiers() {
        QueryModifiers modifiers = new QueryModifiers(1L, 2L);
        metadata.setModifiers(modifiers);
        Assert.assertEquals(modifiers, metadata.getModifiers());
    }

    @Test
    public void setLimit() {
        QueryModifiers modifiers = new QueryModifiers(1L, 2L);
        metadata.setModifiers(modifiers);
        metadata.setLimit(3L);
        Assert.assertEquals(Long.valueOf(3L), metadata.getModifiers().getLimit());
        Assert.assertEquals(Long.valueOf(2L), metadata.getModifiers().getOffset());
    }

    @Test
    public void setOffset() {
        QueryModifiers modifiers = new QueryModifiers(1L, 1L);
        metadata.setModifiers(modifiers);
        metadata.setOffset(2L);
        Assert.assertEquals(Long.valueOf(1L), metadata.getModifiers().getLimit());
        Assert.assertEquals(Long.valueOf(2L), metadata.getModifiers().getOffset());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getOrderBy() {
        metadata.addJoin(DEFAULT, str);
        metadata.addOrderBy(str.asc());
        metadata.addOrderBy(str.desc());
        Assert.assertEquals(Arrays.asList(str.asc(), str.desc()), metadata.getOrderBy());
    }

    @Test
    public void getProjection() {
        metadata.addJoin(DEFAULT, str);
        metadata.setProjection(str.append("abc"));
        Assert.assertEquals(str.append("abc"), metadata.getProjection());
    }

    @Test
    public void getWhere() {
        metadata.addJoin(DEFAULT, str);
        metadata.addWhere(str.eq("b"));
        metadata.addWhere(str.isNotEmpty());
        Assert.assertEquals(str.eq("b").and(str.isNotEmpty()), metadata.getWhere());
    }

    @Test
    public void isDistinct() {
        Assert.assertFalse(metadata.isDistinct());
        metadata.setDistinct(true);
        Assert.assertTrue(metadata.isDistinct());
    }

    @Test
    public void isUnique() {
        Assert.assertFalse(metadata.isUnique());
        metadata.setUnique(true);
        Assert.assertTrue(metadata.isUnique());
    }

    @Test
    public void joinShouldBeCommitted() {
        DefaultQueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(DEFAULT, str);
        DefaultQueryMetadata emptyMetadata = new DefaultQueryMetadata();
        Assert.assertFalse(md.equals(emptyMetadata));
    }

    @Test
    public void clone_() {
        metadata.addJoin(DEFAULT, str);
        metadata.addGroupBy(str);
        metadata.addHaving(str.isNotNull());
        metadata.addJoin(DEFAULT, str2);
        QueryModifiers modifiers = new QueryModifiers(1L, 2L);
        metadata.setModifiers(modifiers);
        metadata.addOrderBy(str.asc());
        metadata.setProjection(str.append("abc"));
        metadata.addWhere(str.eq("b"));
        metadata.addWhere(str.isNotEmpty());
        QueryMetadata clone = metadata.clone();
        Assert.assertEquals(metadata.getGroupBy(), clone.getGroupBy());
        Assert.assertEquals(metadata.getHaving(), clone.getHaving());
        Assert.assertEquals(metadata.getJoins(), clone.getJoins());
        Assert.assertEquals(metadata.getModifiers(), clone.getModifiers());
        Assert.assertEquals(metadata.getOrderBy(), clone.getOrderBy());
        Assert.assertEquals(metadata.getProjection(), clone.getProjection());
        Assert.assertEquals(metadata.getWhere(), clone.getWhere());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void setParam() {
        metadata.setParam(new Param(String.class, "str"), ConstantImpl.create("X"));
        Assert.assertEquals(1, metadata.getParams().size());
        Assert.assertTrue(metadata.getParams().get(new Param(String.class, "str")).equals(ConstantImpl.create("X")));
    }

    @Test
    public void addFlag() {
        QueryFlag flag = new QueryFlag(Position.START, "X");
        metadata.addFlag(flag);
        Assert.assertTrue(metadata.hasFlag(flag));
    }

    @Test
    public void equals() {
        metadata.addJoin(DEFAULT, str);
        metadata.addGroupBy(str);
        metadata.addHaving(str.isNotNull());
        metadata.addJoin(DEFAULT, str2);
        QueryModifiers modifiers = new QueryModifiers(1L, 2L);
        metadata.setModifiers(modifiers);
        metadata.addOrderBy(str.asc());
        metadata.setProjection(str.append("abc"));
        metadata.addWhere(str.eq("b"));
        metadata.addWhere(str.isNotEmpty());
        QueryMetadata metadata2 = new DefaultQueryMetadata();
        Assert.assertFalse(metadata.equals(metadata2));
        metadata2.addJoin(DEFAULT, str);
        Assert.assertFalse(metadata.equals(metadata2));
        metadata2.addGroupBy(str);
        Assert.assertFalse(metadata.equals(metadata2));
        metadata2.addHaving(str.isNotNull());
        Assert.assertFalse(metadata.equals(metadata2));
        metadata2.addJoin(DEFAULT, str2);
        Assert.assertFalse(metadata.equals(metadata2));
        metadata2.setModifiers(modifiers);
        Assert.assertFalse(metadata.equals(metadata2));
        metadata2.addOrderBy(str.asc());
        Assert.assertFalse(metadata.equals(metadata2));
        metadata2.setProjection(str.append("abc"));
        Assert.assertFalse(metadata.equals(metadata2));
        metadata2.addWhere(str.eq("b"));
        metadata2.addWhere(str.isNotEmpty());
        Assert.assertTrue(metadata.equals(metadata2));
    }

    @Test
    public void hashCode_() {
        metadata.addJoin(DEFAULT, str);
        metadata.addGroupBy(str);
        metadata.addHaving(str.isNotNull());
        metadata.addJoin(DEFAULT, str2);
        QueryModifiers modifiers = new QueryModifiers(1L, 2L);
        metadata.setModifiers(modifiers);
        metadata.addOrderBy(str.asc());
        metadata.setProjection(str.append("abc"));
        metadata.addWhere(str.eq("b"));
        metadata.addWhere(str.isNotEmpty());
        metadata.hashCode();
    }

    @Test
    public void hashCode_empty_metadata() {
        metadata.hashCode();
    }
}

