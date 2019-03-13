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


import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.PathMetadataFactory;
import org.junit.Assert;
import org.junit.Test;


public class ListPathTest {
    private ListPath<String, StringPath> stringPath = new ListPath<String, StringPath>(String.class, StringPath.class, PathMetadataFactory.forVariable("stringPath"));

    @Test
    public void toString_() {
        Assert.assertEquals("stringPath", stringPath.toString());
        Assert.assertEquals("any(stringPath)", stringPath.any().toString());
        Assert.assertEquals("eqIc(stringPath.get(0),X)", stringPath.get(0).equalsIgnoreCase("X").toString());
        Assert.assertEquals("eqIc(any(stringPath),X)", stringPath.any().equalsIgnoreCase("X").toString());
        Assert.assertEquals("stringPath.get(0)", stringPath.get(ConstantImpl.create(0)).toString());
    }

    @Test
    public void getElementType() {
        Assert.assertEquals(String.class, stringPath.getElementType());
    }

    @Test
    public void getParameter() {
        Assert.assertEquals(String.class, stringPath.getParameter(0));
    }
}

