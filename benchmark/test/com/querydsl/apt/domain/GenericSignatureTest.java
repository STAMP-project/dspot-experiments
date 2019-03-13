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
package com.querydsl.apt.domain;


import com.querydsl.core.annotations.QueryEntity;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static QGenericSignatureTest_Entity.entity;


public class GenericSignatureTest {
    @QueryEntity
    @SuppressWarnings("unchecked")
    public static class Entity<T extends GenericSignatureTest.Entity<T>> {
        // collection
        Collection<GenericSignatureTest.Entity> rawCollection;

        Collection<GenericSignatureTest.Entity<T>> genericCollection;

        Collection<T> genericCollection2;

        // list
        List<GenericSignatureTest.Entity> rawList;

        List<GenericSignatureTest.Entity<T>> genericList;

        List<T> genericList2;

        // set
        Set<GenericSignatureTest.Entity> rawSet;

        Set<GenericSignatureTest.Entity<T>> genericSet;

        Set<T> genericSet2;

        // map
        Map<String, GenericSignatureTest.Entity> rawMap;

        Map<String, GenericSignatureTest.Entity<T>> genericMap;

        Map<String, T> genericMap2;
    }

    @Test
    public void test() {
        QGenericSignatureTest_Entity entity = entity;
        // collection
        Assert.assertEquals(GenericSignatureTest.Entity.class, entity.rawCollection.getParameter(0));
        Assert.assertEquals(GenericSignatureTest.Entity.class, entity.genericCollection.getParameter(0));
        Assert.assertEquals(GenericSignatureTest.Entity.class, entity.genericCollection2.getParameter(0));
        // list
        Assert.assertEquals(GenericSignatureTest.Entity.class, entity.rawList.getParameter(0));
        Assert.assertEquals(GenericSignatureTest.Entity.class, entity.genericList.getParameter(0));
        Assert.assertEquals(GenericSignatureTest.Entity.class, entity.genericList2.getParameter(0));
        // set
        Assert.assertEquals(GenericSignatureTest.Entity.class, entity.rawSet.getParameter(0));
        Assert.assertEquals(GenericSignatureTest.Entity.class, entity.genericSet.getParameter(0));
        Assert.assertEquals(GenericSignatureTest.Entity.class, entity.genericSet2.getParameter(0));
        // map
        Assert.assertEquals(GenericSignatureTest.Entity.class, entity.rawMap.getParameter(1));
        Assert.assertEquals(GenericSignatureTest.Entity.class, entity.genericMap.getParameter(1));
        Assert.assertEquals(GenericSignatureTest.Entity.class, entity.genericMap2.getParameter(1));
    }
}

