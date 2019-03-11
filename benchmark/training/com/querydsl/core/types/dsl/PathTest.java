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
package com.querydsl.core.types.dsl;


import ToStringVisitor.DEFAULT;
import com.querydsl.core.alias.Alias;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryTransient;
import com.querydsl.core.util.Annotations;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;


public class PathTest {
    enum ExampleEnum {

        A,
        B;}

    public static class Superclass {
        @Nullable
        public String getProperty4() {
            return null;
        }
    }

    @QueryEntity
    public static class Entity extends PathTest.Superclass {
        @Nullable
        private String property1;

        private String property2;

        @QueryTransient
        private String property3;

        public String getProperty1() {
            return property1;
        }

        @Nonnull
        public String getProperty2() {
            return property2;
        }

        @Nonnull
        public String getProperty3() {
            return property3;
        }
    }

    @Test
    public void getAnnotatedElement() {
        PathTest.Entity entity = Alias.alias(PathTest.Entity.class);
        AnnotatedElement element = Alias.$(entity).getAnnotatedElement();
        // type
        Assert.assertEquals(PathTest.Entity.class, element);
    }

    @Test
    public void getAnnotatedElement_for_property() {
        PathTest.Entity entity = Alias.alias(PathTest.Entity.class);
        AnnotatedElement property1 = Alias.$(entity.getProperty1()).getAnnotatedElement();
        AnnotatedElement property2 = Alias.$(entity.getProperty2()).getAnnotatedElement();
        AnnotatedElement property3 = Alias.$(entity.getProperty3()).getAnnotatedElement();
        AnnotatedElement property4 = Alias.$(entity.getProperty4()).getAnnotatedElement();
        // property (field)
        Assert.assertEquals(Field.class, property1.getClass());
        Assert.assertTrue(property1.isAnnotationPresent(Nullable.class));
        Assert.assertNotNull(property1.getAnnotation(Nullable.class));
        Assert.assertFalse(property1.isAnnotationPresent(Nonnull.class));
        Assert.assertNull(property1.getAnnotation(Nonnull.class));
        // property2 (method)
        Assert.assertEquals(Method.class, property2.getClass());
        Assert.assertTrue(property2.isAnnotationPresent(Nonnull.class));
        Assert.assertNotNull(property2.getAnnotation(Nonnull.class));
        Assert.assertFalse(property2.isAnnotationPresent(Nullable.class));
        Assert.assertNull(property2.getAnnotation(Nullable.class));
        // property3 (both)
        Assert.assertEquals(Annotations.class, property3.getClass());
        Assert.assertTrue(property3.isAnnotationPresent(QueryTransient.class));
        Assert.assertNotNull(property3.getAnnotation(QueryTransient.class));
        Assert.assertTrue(property3.isAnnotationPresent(Nonnull.class));
        Assert.assertNotNull(property3.getAnnotation(Nonnull.class));
        Assert.assertFalse(property3.isAnnotationPresent(Nullable.class));
        Assert.assertNull(property3.getAnnotation(Nullable.class));
        // property 4 (superclass)
        Assert.assertEquals(Method.class, property4.getClass());
        Assert.assertTrue(property4.isAnnotationPresent(Nullable.class));
        Assert.assertNotNull(property4.getAnnotation(Nullable.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void equals() {
        Assert.assertEquals(new StringPath("s"), new StringPath("s"));
        Assert.assertEquals(new BooleanPath("b"), new BooleanPath("b"));
        Assert.assertEquals(new NumberPath<Integer>(Integer.class, "n"), new NumberPath<Integer>(Integer.class, "n"));
        Assert.assertEquals(new ArrayPath(String[].class, "p"), ExpressionUtils.path(String.class, "p"));
        Assert.assertEquals(new BooleanPath("p"), ExpressionUtils.path(Boolean.class, "p"));
        Assert.assertEquals(new ComparablePath(String.class, "p"), ExpressionUtils.path(String.class, "p"));
        Assert.assertEquals(new DatePath(Date.class, "p"), ExpressionUtils.path(Date.class, "p"));
        Assert.assertEquals(new DateTimePath(Date.class, "p"), ExpressionUtils.path(Date.class, "p"));
        Assert.assertEquals(new EnumPath(PathTest.ExampleEnum.class, "p"), ExpressionUtils.path(PathTest.ExampleEnum.class, "p"));
        Assert.assertEquals(new NumberPath(Integer.class, "p"), ExpressionUtils.path(Integer.class, "p"));
        Assert.assertEquals(new StringPath("p"), ExpressionUtils.path(String.class, "p"));
        Assert.assertEquals(new TimePath(Time.class, "p"), ExpressionUtils.path(Time.class, "p"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void various_properties() {
        Path<?> parent = ExpressionUtils.path(Object.class, "parent");
        List<Path<?>> paths = new ArrayList<Path<?>>();
        paths.add(new ArrayPath(String[].class, parent, "p"));
        paths.add(new BeanPath(Object.class, parent, "p"));
        paths.add(new BooleanPath(parent, "p"));
        paths.add(new CollectionPath(String.class, com.querydsl.core.types.StringPath.class, parent, "p"));
        paths.add(new ComparablePath(String.class, parent, "p"));
        paths.add(new DatePath(Date.class, parent, "p"));
        paths.add(new DateTimePath(Date.class, parent, "p"));
        paths.add(new EnumPath(PathTest.ExampleEnum.class, parent, "p"));
        paths.add(new ListPath(String.class, com.querydsl.core.types.StringPath.class, parent, "p"));
        paths.add(new MapPath(String.class, String.class, com.querydsl.core.types.StringPath.class, parent, "p"));
        paths.add(new NumberPath(Integer.class, parent, "p"));
        paths.add(new SetPath(String.class, com.querydsl.core.types.StringPath.class, parent, "p"));
        paths.add(new SimplePath(String.class, parent, "p"));
        paths.add(new StringPath(parent, "p"));
        paths.add(new TimePath(Time.class, parent, "p"));
        for (Path<?> path : paths) {
            Path other = ExpressionUtils.path(path.getType(), PathMetadataFactory.forProperty(parent, "p"));
            Assert.assertEquals(path.toString(), path.accept(DEFAULT, Templates.DEFAULT));
            Assert.assertEquals(path.hashCode(), other.hashCode());
            Assert.assertEquals(path, other);
            Assert.assertNotNull(path.getMetadata());
            Assert.assertNotNull(path.getType());
            Assert.assertEquals(parent, path.getRoot());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void various() {
        List<Path<?>> paths = new ArrayList<Path<?>>();
        paths.add(new ArrayPath(String[].class, "p"));
        paths.add(new BeanPath(Object.class, "p"));
        paths.add(new BooleanPath("p"));
        paths.add(new CollectionPath(String.class, com.querydsl.core.types.StringPath.class, "p"));
        paths.add(new ComparablePath(String.class, "p"));
        paths.add(new DatePath(Date.class, "p"));
        paths.add(new DateTimePath(Date.class, "p"));
        paths.add(new EnumPath(PathTest.ExampleEnum.class, "p"));
        paths.add(new ListPath(String.class, com.querydsl.core.types.StringPath.class, "p"));
        paths.add(new MapPath(String.class, String.class, com.querydsl.core.types.StringPath.class, "p"));
        paths.add(new NumberPath(Integer.class, "p"));
        paths.add(new SetPath(String.class, com.querydsl.core.types.StringPath.class, "p"));
        paths.add(new SimplePath(String.class, "p"));
        paths.add(new StringPath("p"));
        paths.add(new TimePath(Time.class, "p"));
        for (Path<?> path : paths) {
            Path other = ExpressionUtils.path(path.getType(), "p");
            Assert.assertEquals(path.toString(), path.accept(DEFAULT, null));
            Assert.assertEquals(path.hashCode(), other.hashCode());
            Assert.assertEquals(path, other);
            Assert.assertNotNull(path.getMetadata());
            Assert.assertNotNull(path.getType());
            Assert.assertEquals(path, path.getRoot());
        }
    }

    @Test
    public void parent_path() {
        Path<Object> person = ExpressionUtils.path(Object.class, "person");
        Path<String> name = ExpressionUtils.path(String.class, person, "name");
        Assert.assertEquals("person.name", name.toString());
    }
}

