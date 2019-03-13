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


import com.mysema.codegen.model.ClassType;
import com.mysema.codegen.model.Type;
import org.junit.Assert;
import org.junit.Test;


public class TypeMappingsTest {
    static class Entity {}

    @Test
    public void getPathType_of_innerClass() {
        TypeMappings typeMappings = new JavaTypeMappings();
        EntityType model = new EntityType(new ClassType(TypeMappingsTest.class));
        EntityType type = new EntityType(new ClassType(TypeMappingsTest.Entity.class));
        typeMappings.register(type, new QueryTypeFactoryImpl("Q", "", "").create(type));
        Type pathType = typeMappings.getPathType(type, model, false);
        Assert.assertEquals("QTypeMappingsTest_Entity", pathType.getSimpleName());
    }

    @Test
    public void isRegistered() {
        TypeMappings typeMappings = new JavaTypeMappings();
        typeMappings.register(new ClassType(Double[].class), new ClassType(Point.class));
        Assert.assertTrue(typeMappings.isRegistered(new ClassType(Double[].class)));
    }
}

