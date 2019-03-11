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


import QCollectionTest_Classes.classes.list1;
import QCollectionTest_Classes.classes.list2;
import QCollectionTest_Classes.classes.list3;
import QCollectionTest_Classes.classes.map1;
import QCollectionTest_Classes.classes.map2;
import QCollectionTest_Classes.classes.map3;
import QCollectionTest_Classes.classes.set1;
import QCollectionTest_Classes.classes.set2;
import QCollectionTest_Classes.classes.set3;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.SetPath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class CollectionTest {
    @QueryEntity
    public static class Person {
        Map<String, ?> map1;

        Map<?, String> map2;

        Map<?, ?> map3;

        Map map4;

        List<?> list1;

        List list2;

        Collection<?> collection1;

        Collection collection2;

        Collection<Collection<CollectionTest.Person>> collectionOfCollection;

        Collection<Set<String>> collectionOfSet;

        Set<?> set1;

        Set set2;
    }

    @QueryEntity
    public static class Classes {
        HashMap map1;

        HashMap<?, ?> map2;

        HashMap<String, String> map3;

        ArrayList list1;

        ArrayList<?> list2;

        ArrayList<String> list3;

        HashSet set1;

        HashSet<?> set2;

        HashSet<String> set3;
    }

    @Test
    public void test() {
        // assertEquals(String.class, QMapWithUndefinedValueTest_Person.person.appData.getParameter(1));
        // assertEquals(Object.class, QMapWithUndefinedValueTest_Person.person.appData.getParameter(1));
        Assert.assertEquals(MapPath.class, map1.getClass());
        Assert.assertEquals(MapPath.class, map2.getClass());
        Assert.assertEquals(MapPath.class, map3.getClass());
        Assert.assertEquals(ListPath.class, list1.getClass());
        Assert.assertEquals(ListPath.class, list2.getClass());
        Assert.assertEquals(ListPath.class, list3.getClass());
        Assert.assertEquals(SetPath.class, set1.getClass());
        Assert.assertEquals(SetPath.class, set2.getClass());
        Assert.assertEquals(SetPath.class, set3.getClass());
    }
}

