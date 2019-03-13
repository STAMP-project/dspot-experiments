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


public class QBeanTest {
    public static class Entity {
        private String name;

        private String name2;

        private int age;

        private boolean married;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public boolean isMarried() {
            return married;
        }

        public void setMarried(boolean married) {
            this.married = married;
        }

        public String getName2() {
            return name2;
        }

        public void setName2(String name2) {
            this.name2 = name2;
        }
    }

    public static class SubEntity extends QBeanTest.Entity {}

    private PathBuilder<QBeanTest.Entity> entity;

    private StringPath name;

    private StringPath name2;

    private NumberPath<Integer> age;

    private BooleanPath married;

    @Test
    public void with_class_and_exprs() {
        QBean<QBeanTest.Entity> beanProjection = new QBean<QBeanTest.Entity>(QBeanTest.Entity.class, name, age, married);
        QBeanTest.Entity bean = beanProjection.newInstance("Fritz", 30, true);
        Assert.assertEquals("Fritz", bean.getName());
        Assert.assertEquals(30, bean.getAge());
        Assert.assertEquals(true, bean.isMarried());
    }

    @Test
    public void with_path_and_exprs() {
        QBean<QBeanTest.Entity> beanProjection = Projections.bean(entity, name, age, married);
        QBeanTest.Entity bean = beanProjection.newInstance("Fritz", 30, true);
        Assert.assertEquals("Fritz", bean.getName());
        Assert.assertEquals(30, bean.getAge());
        Assert.assertEquals(true, bean.isMarried());
    }

    @Test
    public void with_unknown_properties() {
        QBean<QBeanTest.Entity> beanProjection = Projections.bean(entity, name, age, Expressions.booleanPath("unknown"));
        QBeanTest.Entity bean = beanProjection.newInstance("Fritz", 30, true);
        Assert.assertEquals("Fritz", bean.getName());
        Assert.assertEquals(30, bean.getAge());
    }

    @Test
    public void with_class_and_map() {
        Map<String, Expression<?>> bindings = new LinkedHashMap<String, Expression<?>>();
        bindings.put("name", name);
        bindings.put("age", age);
        bindings.put("married", married);
        QBean<QBeanTest.Entity> beanProjection = new QBean<QBeanTest.Entity>(QBeanTest.Entity.class, bindings);
        QBeanTest.Entity bean = beanProjection.newInstance("Fritz", 30, true);
        Assert.assertEquals("Fritz", bean.getName());
        Assert.assertEquals(30, bean.getAge());
        Assert.assertEquals(true, bean.isMarried());
    }

    @Test
    public void with_class_and_alias() {
        StringPath name2 = Expressions.stringPath("name2");
        QBean<QBeanTest.Entity> beanProjection = new QBean<QBeanTest.Entity>(QBeanTest.Entity.class, name.as(name2), age, married);
        QBeanTest.Entity bean = beanProjection.newInstance("Fritz", 30, true);
        Assert.assertNull(bean.getName());
        Assert.assertEquals("Fritz", bean.getName2());
        Assert.assertEquals(30, bean.getAge());
        Assert.assertEquals(true, bean.isMarried());
    }

    @Test
    public void with_nested_factoryExpression() {
        Map<String, Expression<?>> bindings = new LinkedHashMap<String, Expression<?>>();
        bindings.put("age", age);
        bindings.put("name", new Concatenation(name, name2));
        QBean<QBeanTest.Entity> beanProjection = new QBean<QBeanTest.Entity>(QBeanTest.Entity.class, bindings);
        FactoryExpression<QBeanTest.Entity> wrappedProjection = FactoryExpressionUtils.wrap(beanProjection);
        QBeanTest.Entity bean = wrappedProjection.newInstance(30, "Fri", "tz");
        Assert.assertEquals("Fritz", bean.getName());
    }

    @Test
    public void with_nested_factoryExpression2() {
        QBean<QBeanTest.Entity> beanProjection = new QBean<QBeanTest.Entity>(QBeanTest.Entity.class, age, ExpressionUtils.as(new Concatenation(name, name2), "name"));
        FactoryExpression<QBeanTest.Entity> wrappedProjection = FactoryExpressionUtils.wrap(beanProjection);
        QBeanTest.Entity bean = wrappedProjection.newInstance(30, "Fri", "tz");
        Assert.assertEquals("Fritz", bean.getName());
    }

    @Test
    public void supertype_population() {
        QBean<QBeanTest.SubEntity> beanProjection = new QBean<QBeanTest.SubEntity>(QBeanTest.SubEntity.class, true, name, age, married);
        QBeanTest.SubEntity bean = beanProjection.newInstance("Fritz", 30, true);
        Assert.assertEquals("Fritz", bean.getName());
        Assert.assertEquals(30, bean.getAge());
        Assert.assertEquals(true, bean.isMarried());
    }

    @Test
    public void skipNulls() {
        QBean<Object> bean = Projections.bean(Object.class);
        Assert.assertEquals(bean, bean);
        Assert.assertEquals(bean.skipNulls(), bean.skipNulls());
        Assert.assertFalse(bean.skipNulls().equals(bean));
        Assert.assertFalse(bean.equals(bean.skipNulls()));
    }

    @Test
    public void alias() {
        QBean<QBeanTest.Entity> beanProjection = new QBean<QBeanTest.Entity>(QBeanTest.Entity.class, name.as("name2"));
        Assert.assertEquals(name.as("name2"), beanProjection.getArgs().get(0));
    }
}

