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


public class QueryTypeFactoryTest {
    private Type type = new ClassType(Point.class);

    @Test
    public void prefix_only() {
        QueryTypeFactory factory = new QueryTypeFactoryImpl("Q", "", "");
        Assert.assertEquals("com.querydsl.codegen.QPoint", factory.create(type).getFullName());
    }

    @Test
    public void prefix_and_suffix() {
        QueryTypeFactory factory = new QueryTypeFactoryImpl("Q", "Type", "");
        Assert.assertEquals("com.querydsl.codegen.QPointType", factory.create(type).getFullName());
    }

    @Test
    public void suffix_only() {
        QueryTypeFactory factory = new QueryTypeFactoryImpl("", "Type", "");
        Assert.assertEquals("com.querydsl.codegen.PointType", factory.create(type).getFullName());
    }

    @Test
    public void prefix_and_package_suffix() {
        QueryTypeFactory factory = new QueryTypeFactoryImpl("Q", "", ".query");
        Assert.assertEquals("com.querydsl.codegen.query.QPoint", factory.create(type).getFullName());
    }
}

