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
import com.mysema.codegen.JavaWriter;
import com.mysema.codegen.StringUtils;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Time;
import java.util.Arrays;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

import static TypeCategory.ARRAY;
import static Types.COLLECTION;
import static Types.LIST;
import static Types.MAP;
import static Types.SET;


public class BeanSerializerTest {
    private Type typeModel;

    private EntityType type;

    private final Writer writer = new StringWriter();

    @Test
    public void annotations() throws IOException {
        type.addAnnotation(new QueryEntityImpl());
        BeanSerializer serializer = new BeanSerializer();
        serializer.serialize(type, DEFAULT, new JavaWriter(writer));
        String str = writer.toString();
        Assert.assertTrue(str.contains("import com.querydsl.core.annotations.QueryEntity;"));
        Assert.assertTrue(str.contains("@QueryEntity"));
    }

    @Test
    public void annotated_property() throws IOException {
        Property property = new Property(type, "entityField", type);
        property.addAnnotation(new QueryEntityImpl());
        type.addProperty(property);
        BeanSerializer serializer = new BeanSerializer();
        serializer.serialize(type, DEFAULT, new JavaWriter(writer));
        String str = writer.toString();
        Assert.assertTrue(str.contains("import com.querydsl.core.annotations.QueryEntity;"));
        Assert.assertTrue(str.contains("@QueryEntity"));
    }

    @Test
    public void annotated_property_not_serialized() throws IOException {
        Property property = new Property(type, "entityField", type);
        property.addAnnotation(new QueryEntityImpl());
        type.addProperty(property);
        BeanSerializer serializer = new BeanSerializer(false);
        serializer.serialize(type, DEFAULT, new JavaWriter(writer));
        String str = writer.toString();
        Assert.assertFalse(str.contains("import com.querydsl.core.annotations.QueryEntity;"));
        Assert.assertFalse(str.contains("@QueryEntity"));
    }

    @Test
    public void capitalization() throws IOException {
        // property
        type.addProperty(new Property(type, "cId", type));
        BeanSerializer serializer = new BeanSerializer();
        serializer.serialize(type, DEFAULT, new JavaWriter(writer));
        Assert.assertTrue(writer.toString().contains("public DomainClass getcId() {"));
    }

    @Test
    public void interfaces() throws IOException {
        BeanSerializer serializer = new BeanSerializer();
        serializer.addInterface(new ClassType(Serializable.class));
        serializer.serialize(type, DEFAULT, new JavaWriter(writer));
        Assert.assertTrue(writer.toString().contains("public class DomainClass implements Serializable {"));
    }

    @Test
    public void interfaces2() throws IOException {
        BeanSerializer serializer = new BeanSerializer();
        serializer.addInterface(Serializable.class);
        serializer.serialize(type, DEFAULT, new JavaWriter(writer));
        Assert.assertTrue(writer.toString().contains("public class DomainClass implements Serializable {"));
    }

    @Test
    public void toString_() throws IOException {
        // property
        type.addProperty(new Property(type, "entityField", type));
        type.addProperty(new Property(type, "collection", new SimpleType(COLLECTION, typeModel)));
        type.addProperty(new Property(type, "listField", new SimpleType(LIST, typeModel)));
        type.addProperty(new Property(type, "setField", new SimpleType(SET, typeModel)));
        type.addProperty(new Property(type, "arrayField", new ClassType(ARRAY, String[].class)));
        type.addProperty(new Property(type, "mapField", new SimpleType(MAP, typeModel, typeModel)));
        BeanSerializer serializer = new BeanSerializer();
        serializer.setAddToString(true);
        serializer.serialize(type, DEFAULT, new JavaWriter(writer));
        Assert.assertTrue(String.valueOf(writer).contains(("    @Override\n" + "    public String toString()")));
    }

    @Test
    public void fullConstructor() throws IOException {
        // property
        type.addProperty(new Property(type, "entityField", type));
        type.addProperty(new Property(type, "collection", new SimpleType(COLLECTION, typeModel)));
        type.addProperty(new Property(type, "listField", new SimpleType(LIST, typeModel)));
        type.addProperty(new Property(type, "setField", new SimpleType(SET, typeModel)));
        type.addProperty(new Property(type, "arrayField", new ClassType(ARRAY, String[].class)));
        type.addProperty(new Property(type, "mapField", new SimpleType(MAP, typeModel, typeModel)));
        BeanSerializer serializer = new BeanSerializer();
        serializer.setAddFullConstructor(true);
        serializer.serialize(type, DEFAULT, new JavaWriter(writer));
        // System.out.println(writer.toString());
    }

    @Test
    public void properties() throws IOException {
        // property
        type.addProperty(new Property(type, "entityField", type));
        type.addProperty(new Property(type, "collection", new SimpleType(COLLECTION, typeModel)));
        type.addProperty(new Property(type, "listField", new SimpleType(LIST, typeModel)));
        type.addProperty(new Property(type, "setField", new SimpleType(SET, typeModel)));
        type.addProperty(new Property(type, "arrayField", new ClassType(ARRAY, String[].class)));
        type.addProperty(new Property(type, "mapField", new SimpleType(MAP, typeModel, typeModel)));
        for (Class<?> cl : Arrays.<Class<?>>asList(Boolean.class, Comparable.class, Integer.class, Date.class, java.sql.Date.class, Time.class)) {
            Type classType = new ClassType(TypeCategory.get(cl.getName()), cl);
            type.addProperty(new Property(type, StringUtils.uncapitalize(cl.getSimpleName()), classType));
        }
        BeanSerializer serializer = new BeanSerializer();
        serializer.serialize(type, DEFAULT, new JavaWriter(writer));
        String str = writer.toString();
        // System.err.println(str);
        for (String prop : Arrays.asList("String[] arrayField;", "Boolean boolean$;", "Collection<DomainClass> collection;", "Comparable comparable;", "java.util.Date date;", "DomainClass entityField;", "Integer integer;", "List<DomainClass> listField;", "Map<DomainClass, DomainClass> mapField;", "Set<DomainClass> setField;", "java.sql.Time time;")) {
            Assert.assertTrue((prop + " was not contained"), str.contains(prop));
        }
    }
}

