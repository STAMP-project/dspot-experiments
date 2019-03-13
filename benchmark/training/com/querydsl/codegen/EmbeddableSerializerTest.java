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
package com.querydsl.codegen;


import SimpleSerializerConfig.DEFAULT;
import TypeCategory.BOOLEAN;
import TypeCategory.COMPARABLE;
import TypeCategory.DATE;
import TypeCategory.DATETIME;
import TypeCategory.ENUM;
import TypeCategory.NUMERIC;
import TypeCategory.STRING;
import TypeCategory.TIME;
import com.mysema.codegen.JavaWriter;
import com.querydsl.core.annotations.PropertyType;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Time;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static TypeCategory.BOOLEAN;
import static TypeCategory.COMPARABLE;
import static TypeCategory.DATE;
import static TypeCategory.DATETIME;
import static TypeCategory.ENTITY;
import static TypeCategory.ENUM;
import static TypeCategory.NUMERIC;
import static TypeCategory.STRING;
import static TypeCategory.TIME;


public class EmbeddableSerializerTest {
    private final QueryTypeFactory queryTypeFactory = new QueryTypeFactoryImpl("Q", "", "");

    private final TypeMappings typeMappings = new JavaTypeMappings();

    private final EntitySerializer serializer = new EmbeddableSerializer(typeMappings, Collections.<String>emptySet());

    private final StringWriter writer = new StringWriter();

    @Test
    public void properties() throws IOException {
        SimpleType type = new SimpleType(ENTITY, "Entity", "", "Entity", false, false);
        EntityType entityType = new EntityType(type);
        entityType.addProperty(new Property(entityType, "b", new ClassType(BOOLEAN, Boolean.class)));
        entityType.addProperty(new Property(entityType, "c", new ClassType(COMPARABLE, String.class)));
        // entityType.addProperty(new Property(entityType, "cu", new ClassType(TypeCategory.CUSTOM, PropertyType.class)));
        entityType.addProperty(new Property(entityType, "d", new ClassType(DATE, Date.class)));
        entityType.addProperty(new Property(entityType, "e", new ClassType(ENUM, PropertyType.class)));
        entityType.addProperty(new Property(entityType, "dt", new ClassType(DATETIME, Date.class)));
        entityType.addProperty(new Property(entityType, "i", new ClassType(NUMERIC, Integer.class)));
        entityType.addProperty(new Property(entityType, "s", new ClassType(STRING, String.class)));
        entityType.addProperty(new Property(entityType, "t", new ClassType(TIME, Time.class)));
        typeMappings.register(entityType, queryTypeFactory.create(entityType));
        serializer.serialize(entityType, DEFAULT, new JavaWriter(writer));
        CompileUtils.assertCompiles("QEntity", writer.toString());
    }

    @Test
    public void originalCategory() throws IOException {
        Map<TypeCategory, String> categoryToSuperClass = new EnumMap<TypeCategory, String>(TypeCategory.class);
        categoryToSuperClass.put(COMPARABLE, "ComparablePath<Entity>");
        categoryToSuperClass.put(ENUM, "EnumPath<Entity>");
        categoryToSuperClass.put(DATE, "DatePath<Entity>");
        categoryToSuperClass.put(DATETIME, "DateTimePath<Entity>");
        categoryToSuperClass.put(TIME, "TimePath<Entity>");
        categoryToSuperClass.put(NUMERIC, "NumberPath<Entity>");
        categoryToSuperClass.put(STRING, "StringPath");
        categoryToSuperClass.put(BOOLEAN, "BooleanPath");
        for (Map.Entry<TypeCategory, String> entry : categoryToSuperClass.entrySet()) {
            StringWriter w = new StringWriter();
            SimpleType type = new SimpleType(entry.getKey(), "Entity", "", "Entity", false, false);
            EntityType entityType = new EntityType(type);
            typeMappings.register(entityType, queryTypeFactory.create(entityType));
            serializer.serialize(entityType, DEFAULT, new JavaWriter(w));
            Assert.assertTrue((((entry.getValue()) + " is missing from ") + w), w.toString().contains((("public class QEntity extends " + (entry.getValue())) + " {")));
        }
    }

    @Test
    public void empty() throws IOException {
        SimpleType type = new SimpleType(ENTITY, "Entity", "", "Entity", false, false);
        EntityType entityType = new EntityType(type);
        typeMappings.register(entityType, queryTypeFactory.create(entityType));
        serializer.serialize(entityType, DEFAULT, new JavaWriter(writer));
        CompileUtils.assertCompiles("QEntity", writer.toString());
    }

    @Test
    public void no_package() throws IOException {
        SimpleType type = new SimpleType(ENTITY, "Entity", "", "Entity", false, false);
        EntityType entityType = new EntityType(type);
        typeMappings.register(entityType, queryTypeFactory.create(entityType));
        serializer.serialize(entityType, DEFAULT, new JavaWriter(writer));
        Assert.assertTrue(writer.toString().contains("public class QEntity extends BeanPath<Entity> {"));
        CompileUtils.assertCompiles("QEntity", writer.toString());
    }

    @Test
    public void correct_superclass() throws IOException {
        SimpleType type = new SimpleType(ENTITY, "java.util.Locale", "java.util", "Locale", false, false);
        EntityType entityType = new EntityType(type);
        typeMappings.register(entityType, queryTypeFactory.create(entityType));
        serializer.serialize(entityType, DEFAULT, new JavaWriter(writer));
        Assert.assertTrue(writer.toString().contains("public class QLocale extends BeanPath<Locale> {"));
        CompileUtils.assertCompiles("QLocale", writer.toString());
    }

    @Test
    public void primitive_array() throws IOException {
        SimpleType type = new SimpleType(ENTITY, "Entity", "", "Entity", false, false);
        EntityType entityType = new EntityType(type);
        entityType.addProperty(new Property(entityType, "bytes", new ClassType(byte[].class)));
        typeMappings.register(entityType, queryTypeFactory.create(entityType));
        serializer.serialize(entityType, DEFAULT, new JavaWriter(writer));
        Assert.assertTrue(writer.toString().contains("public final SimplePath<byte[]> bytes"));
        CompileUtils.assertCompiles("QEntity", writer.toString());
    }

    @Test
    public void include() throws IOException {
        SimpleType type = new SimpleType(ENTITY, "Entity", "", "Entity", false, false);
        EntityType entityType = new EntityType(type);
        entityType.addProperty(new Property(entityType, "b", new ClassType(BOOLEAN, Boolean.class)));
        entityType.addProperty(new Property(entityType, "c", new ClassType(COMPARABLE, String.class)));
        // entityType.addProperty(new Property(entityType, "cu", new ClassType(TypeCategory.CUSTOM, PropertyType.class)));
        entityType.addProperty(new Property(entityType, "d", new ClassType(DATE, Date.class)));
        entityType.addProperty(new Property(entityType, "e", new ClassType(ENUM, PropertyType.class)));
        entityType.addProperty(new Property(entityType, "dt", new ClassType(DATETIME, Date.class)));
        entityType.addProperty(new Property(entityType, "i", new ClassType(NUMERIC, Integer.class)));
        entityType.addProperty(new Property(entityType, "s", new ClassType(STRING, String.class)));
        entityType.addProperty(new Property(entityType, "t", new ClassType(TIME, Time.class)));
        EntityType subType = new EntityType(new SimpleType(ENTITY, "Entity2", "", "Entity2", false, false));
        subType.include(new Supertype(type, entityType));
        typeMappings.register(entityType, queryTypeFactory.create(entityType));
        typeMappings.register(subType, queryTypeFactory.create(subType));
        serializer.serialize(subType, DEFAULT, new JavaWriter(writer));
        CompileUtils.assertCompiles("QEntity2", writer.toString());
    }

    @Test
    public void superType() throws IOException {
        EntityType superType = new EntityType(new SimpleType(ENTITY, "Entity2", "", "Entity2", false, false));
        SimpleType type = new SimpleType(ENTITY, "Entity", "", "Entity", false, false);
        EntityType entityType = new EntityType(type, Collections.singleton(new Supertype(superType, superType)));
        typeMappings.register(superType, queryTypeFactory.create(superType));
        typeMappings.register(entityType, queryTypeFactory.create(entityType));
        serializer.serialize(entityType, DEFAULT, new JavaWriter(writer));
        Assert.assertTrue(writer.toString().contains("public final QEntity2 _super = new QEntity2(this);"));
        // CompileUtils.assertCompiles("QEntity", writer.toString());
    }

    @Test
    public void delegates() throws IOException {
        SimpleType type = new SimpleType(ENTITY, "Entity", "", "Entity", false, false);
        EntityType entityType = new EntityType(type);
        Delegate delegate = new Delegate(type, type, "test", Collections.<Parameter>emptyList(), Types.STRING);
        entityType.addDelegate(delegate);
        typeMappings.register(entityType, queryTypeFactory.create(entityType));
        serializer.serialize(entityType, DEFAULT, new JavaWriter(writer));
        Assert.assertTrue(writer.toString().contains("return Entity.test(this);"));
        CompileUtils.assertCompiles("QEntity", writer.toString());
    }
}

