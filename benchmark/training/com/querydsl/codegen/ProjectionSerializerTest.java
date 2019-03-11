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
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

import static TypeCategory.ENTITY;
import static Types.INTEGER;
import static Types.STRING;


public class ProjectionSerializerTest {
    @Test
    public void constructors() throws IOException {
        Type typeModel = new SimpleType(ENTITY, "com.querydsl.DomainClass", "com.querydsl", "DomainClass", false, false);
        EntityType type = new EntityType(typeModel);
        // constructor
        Parameter firstName = new Parameter("firstName", STRING);
        Parameter lastName = new Parameter("lastName", STRING);
        Parameter age = new Parameter("age", INTEGER);
        type.addConstructor(new Constructor(Arrays.asList(firstName, lastName, age)));
        Writer writer = new StringWriter();
        ProjectionSerializer serializer = new ProjectionSerializer(new JavaTypeMappings());
        serializer.serialize(type, DEFAULT, new JavaWriter(writer));
        Assert.assertTrue(writer.toString().contains("Expression<String> firstName"));
        Assert.assertTrue(writer.toString().contains("Expression<String> lastName"));
        Assert.assertTrue(writer.toString().contains("Expression<Integer> age"));
    }
}

