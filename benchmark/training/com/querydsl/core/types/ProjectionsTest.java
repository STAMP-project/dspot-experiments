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
package com.querydsl.core.types;


import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Assert;
import org.junit.Test;


public class ProjectionsTest {
    public static class VarArgs {
        String[] args;

        public VarArgs(String... strs) {
            args = strs;
        }
    }

    public static class VarArgs2 {
        String arg;

        String[] args;

        public VarArgs2(String s, String... strs) {
            arg = s;
            args = strs;
        }
    }

    public static class Entity1 {
        String arg1;

        String arg2;

        public Entity1(String arg1, String arg2) {
            this.arg1 = arg1;
            this.arg2 = arg2;
        }
    }

    public static class Entity2 {
        String arg1;

        ProjectionsTest.Entity1 entity;

        public Entity2(String arg1, ProjectionsTest.Entity1 entity) {
            this.arg1 = arg1;
            this.entity = entity;
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void array() {
        FactoryExpression<String[]> expr = Projections.array(String[].class, ExpressionUtils.path(String.class, "p1"), ExpressionUtils.path(String.class, "p2"));
        Assert.assertEquals(String[].class, expr.newInstance("1", "2").getClass());
    }

    @Test
    public void beanClassOfTExpressionOfQArray() {
        PathBuilder<QBeanPropertyTest.Entity> entity = new PathBuilder<QBeanPropertyTest.Entity>(QBeanPropertyTest.Entity.class, "entity");
        QBean<QBeanPropertyTest.Entity> beanProjection = Projections.bean(QBeanPropertyTest.Entity.class, entity.getNumber("cId", Integer.class), entity.getNumber("eId", Integer.class));
        Assert.assertEquals(QBeanPropertyTest.Entity.class, beanProjection.newInstance(1, 2).getClass());
    }

    @Test
    public void constructor() {
        Expression<Long> longVal = ConstantImpl.create(1L);
        Expression<String> stringVal = ConstantImpl.create("");
        Assert.assertEquals(ProjectionExample.class, Projections.constructor(ProjectionExample.class, longVal, stringVal).newInstance(0L, "").getClass());
    }

    @Test
    public void constructor_varArgs() {
        Expression<String> stringVal = ConstantImpl.create("");
        ProjectionsTest.VarArgs instance = Projections.constructor(ProjectionsTest.VarArgs.class, stringVal, stringVal).newInstance("X", "Y");
        Assert.assertArrayEquals(new String[]{ "X", "Y" }, instance.args);
    }

    @Test
    public void constructor_varArgs2() {
        Expression<String> stringVal = ConstantImpl.create("");
        ProjectionsTest.VarArgs2 instance = Projections.constructor(ProjectionsTest.VarArgs2.class, stringVal, stringVal, stringVal).newInstance("X", "Y", "Z");
        Assert.assertEquals("X", instance.arg);
        Assert.assertArrayEquals(new String[]{ "Y", "Z" }, instance.args);
    }

    @Test
    public void constructor_varArgs3() {
        Constant<Long> longVal = ConstantImpl.create(1L);
        Constant<Character> charVal = ConstantImpl.create('\u0000');
        ProjectionExample instance = Projections.constructor(ProjectionExample.class, longVal, charVal, charVal, charVal, charVal, charVal, charVal, charVal, charVal, charVal, charVal).newInstance(null, 'm', 'y', 's', 'e', 'm', 'a', null, 'l', 't', 'd');
        Assert.assertEquals(0L, ((long) (instance.id)));
        // null character cannot be inserted, so a literal String can't be used.
        String expectedText = String.valueOf(new char[]{ 'm', 'y', 's', 'e', 'm', 'a', '\u0000', 'l', 't', 'd' });
        Assert.assertEquals(expectedText, instance.text);
    }

    @Test
    public void fieldsClassOfTExpressionOfQArray() {
        PathBuilder<QBeanPropertyTest.Entity> entity = new PathBuilder<QBeanPropertyTest.Entity>(QBeanPropertyTest.Entity.class, "entity");
        QBean<QBeanPropertyTest.Entity> beanProjection = Projections.fields(QBeanPropertyTest.Entity.class, entity.getNumber("cId", Integer.class), entity.getNumber("eId", Integer.class));
        Assert.assertEquals(QBeanPropertyTest.Entity.class, beanProjection.newInstance(1, 2).getClass());
    }

    @Test
    public void nested() {
        StringPath str1 = Expressions.stringPath("str1");
        StringPath str2 = Expressions.stringPath("str2");
        StringPath str3 = Expressions.stringPath("str3");
        FactoryExpression<ProjectionsTest.Entity1> entity = Projections.constructor(ProjectionsTest.Entity1.class, str1, str2);
        FactoryExpression<ProjectionsTest.Entity2> wrapper = Projections.constructor(ProjectionsTest.Entity2.class, str3, entity);
        FactoryExpression<ProjectionsTest.Entity2> wrapped = FactoryExpressionUtils.wrap(wrapper);
        ProjectionsTest.Entity2 w = wrapped.newInstance("a", "b", "c");
        Assert.assertEquals("a", w.arg1);
        Assert.assertEquals("b", w.entity.arg1);
        Assert.assertEquals("c", w.entity.arg2);
        w = wrapped.newInstance("a", null, null);
        Assert.assertEquals("a", w.arg1);
        Assert.assertNotNull(w.entity);
        w = wrapped.newInstance(null, null, null);
        Assert.assertNotNull(w.entity);
    }

    @Test
    public void nestedSkipNulls() {
        StringPath str1 = Expressions.stringPath("str1");
        StringPath str2 = Expressions.stringPath("str2");
        StringPath str3 = Expressions.stringPath("str3");
        FactoryExpression<ProjectionsTest.Entity1> entity = Projections.constructor(ProjectionsTest.Entity1.class, str1, str2).skipNulls();
        FactoryExpression<ProjectionsTest.Entity2> wrapper = Projections.constructor(ProjectionsTest.Entity2.class, str3, entity);
        FactoryExpression<ProjectionsTest.Entity2> wrapped = FactoryExpressionUtils.wrap(wrapper);
        ProjectionsTest.Entity2 w = wrapped.newInstance("a", "b", "c");
        Assert.assertEquals("a", w.arg1);
        Assert.assertEquals("b", w.entity.arg1);
        Assert.assertEquals("c", w.entity.arg2);
        w = wrapped.newInstance("a", null, null);
        Assert.assertEquals("a", w.arg1);
        Assert.assertNull(w.entity);
        w = wrapped.newInstance(null, null, null);
        Assert.assertNull(w.entity);
    }

    @Test
    public void nestedSkipNulls2() {
        StringPath str1 = Expressions.stringPath("str1");
        StringPath str2 = Expressions.stringPath("str2");
        StringPath str3 = Expressions.stringPath("str3");
        FactoryExpression<ProjectionsTest.Entity1> entity = Projections.constructor(ProjectionsTest.Entity1.class, str1, str2).skipNulls();
        FactoryExpression<ProjectionsTest.Entity2> wrapper = Projections.constructor(ProjectionsTest.Entity2.class, str3, entity).skipNulls();
        FactoryExpression<ProjectionsTest.Entity2> wrapped = FactoryExpressionUtils.wrap(wrapper);
        ProjectionsTest.Entity2 w = wrapped.newInstance("a", "b", "c");
        Assert.assertEquals("a", w.arg1);
        Assert.assertEquals("b", w.entity.arg1);
        Assert.assertEquals("c", w.entity.arg2);
        w = wrapped.newInstance("a", null, null);
        Assert.assertEquals("a", w.arg1);
        Assert.assertNull(w.entity);
        w = wrapped.newInstance(null, null, null);
        Assert.assertNull(w);
    }
}

