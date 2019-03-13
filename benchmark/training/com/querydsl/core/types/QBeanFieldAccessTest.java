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


import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class QBeanFieldAccessTest {
    public static class Entity {
        String name;

        String name2;

        int age;

        boolean married;
    }

    private PathBuilder<QBeanFieldAccessTest.Entity> entity;

    private StringPath name;

    private StringPath name2;

    private NumberPath<Integer> age;

    private BooleanPath married;

    @Test
    public void with_class_and_exprs_using_fields() {
        QBean<QBeanFieldAccessTest.Entity> beanProjection = new QBean<QBeanFieldAccessTest.Entity>(QBeanFieldAccessTest.Entity.class, true, name, age, married);
        QBeanFieldAccessTest.Entity bean = beanProjection.newInstance("Fritz", 30, true);
        Assert.assertEquals("Fritz", bean.name);
        Assert.assertEquals(30, bean.age);
        Assert.assertEquals(true, bean.married);
    }

    @Test
    public void with_path_and_exprs_using_fields() {
        QBean<QBeanFieldAccessTest.Entity> beanProjection = Projections.fields(entity, name, age, married);
        QBeanFieldAccessTest.Entity bean = beanProjection.newInstance("Fritz", 30, true);
        Assert.assertEquals("Fritz", bean.name);
        Assert.assertEquals(30, bean.age);
        Assert.assertEquals(true, bean.married);
    }

    @Test
    public void with_class_and_map_using_fields() {
        Map<String, Expression<?>> bindings = new LinkedHashMap<String, Expression<?>>();
        bindings.put("name", name);
        bindings.put("age", age);
        bindings.put("married", married);
        QBean<QBeanFieldAccessTest.Entity> beanProjection = new QBean<QBeanFieldAccessTest.Entity>(QBeanFieldAccessTest.Entity.class, true, bindings);
        QBeanFieldAccessTest.Entity bean = beanProjection.newInstance("Fritz", 30, true);
        Assert.assertEquals("Fritz", bean.name);
        Assert.assertEquals(30, bean.age);
        Assert.assertEquals(true, bean.married);
    }

    @Test
    public void with_class_and_alias_using_fields() {
        StringPath name2 = Expressions.stringPath("name2");
        QBean<QBeanFieldAccessTest.Entity> beanProjection = new QBean<QBeanFieldAccessTest.Entity>(QBeanFieldAccessTest.Entity.class, true, name.as(name2), age, married);
        QBeanFieldAccessTest.Entity bean = beanProjection.newInstance("Fritz", 30, true);
        Assert.assertNull(bean.name);
        Assert.assertEquals("Fritz", bean.name2);
        Assert.assertEquals(30, bean.age);
        Assert.assertEquals(true, bean.married);
    }

    @Test
    public void with_nested_factoryExpression() {
        Map<String, Expression<?>> bindings = new LinkedHashMap<String, Expression<?>>();
        bindings.put("age", age);
        bindings.put("name", new Concatenation(name, name2));
        QBean<QBeanFieldAccessTest.Entity> beanProjection = new QBean<QBeanFieldAccessTest.Entity>(QBeanFieldAccessTest.Entity.class, true, bindings);
        FactoryExpression<QBeanFieldAccessTest.Entity> wrappedProjection = FactoryExpressionUtils.wrap(beanProjection);
        QBeanFieldAccessTest.Entity bean = wrappedProjection.newInstance(30, "Fri", "tz");
        Assert.assertEquals("Fritz", bean.name);
    }
}

