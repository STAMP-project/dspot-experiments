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


import QRelationTest_RelationType.relationType;
import QRelationTest_RelationType.relationType.list;
import QRelationTest_RelationType.relationType.set;
import com.querydsl.apt.domain.rel.RelationType2;
import com.querydsl.core.annotations.Config;
import com.querydsl.core.annotations.QueryEntity;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import org.junit.Assert;
import org.junit.Test;


public class RelationTest extends AbstractTest {
    public enum MyEnum {

        VAR1,
        VAR2;}

    @QueryEntity
    public static class Reference {}

    @QueryEntity
    public static class GenericRelations {
        Collection<Collection<RelationTest.Reference>> col1;

        Collection<List<RelationTest.Reference>> col2;

        Collection<Collection<? extends RelationTest.Reference>> col3;

        Collection<List<? extends RelationTest.Reference>> col4;

        Set<List<RelationTest.Reference>> set1;

        Set<Collection<RelationTest.Reference>> set2;

        Set<List<? extends RelationTest.Reference>> set3;

        Set<Collection<? extends RelationTest.Reference>> set4;

        Map<String, List<String>> map1;

        Map<List<String>, String> map2;

        Map<String, List<? extends String>> map3;

        Map<List<? extends String>, String> map4;
    }

    @QueryEntity
    @Config(listAccessors = true, mapAccessors = true)
    public static class RelationType {
        RelationTest.MyEnum enumProperty;

        List<RelationTest.MyEnum> enumList;

        Map<String, RelationTest.MyEnum> enumMap1;

        Map<RelationTest.MyEnum, String> enumMap;

        // list
        List<RelationTest.RelationType> list;

        List<? extends RelationTest.RelationType> list2;

        List<String> list3;

        List<RelationType2<?>> list4;

        List<RelationTest.Reference> list5;

        List<List<RelationTest.Reference>> list6;

        List<Collection<RelationTest.Reference>> list7;

        // set
        Set<RelationTest.RelationType> set;

        SortedSet<RelationTest.RelationType> sortedSet;

        Set<String> set2;

        Set<RelationType2<?>> set3;

        Set<RelationTest.Reference> set4;

        // .. of Object
        List<Object> listOfObjects;

        Set<Object> setOfObjects;

        Set<RelationTest.Reference> setOfObjects2;

        // collection
        Collection<RelationTest.RelationType> collection;

        Collection<RelationType2<?>> collection2;

        Collection<String> collection3;

        Collection<RelationTest.Reference> collection4;

        // map
        Map<String, RelationTest.RelationType> map;

        Map<RelationTest.RelationType, RelationTest.RelationType> map2;

        Map<RelationTest.RelationType, String> map3;

        Map<String, RelationType2<?>> map4;

        Map<RelationType2<?>, RelationType2<?>> map5;

        Map<RelationType2<?>, String> map6;

        Map<String, RelationTest.Reference> map7;

        Map<RelationTest.Reference, RelationTest.Reference> map8;

        Map<RelationTest.Reference, String> map9;
    }

    @Test
    public void test() throws NoSuchFieldException, SecurityException {
        start(QRelationTest_RelationType.class, relationType);
        match(EnumPath.class, "enumProperty");
        match(ListPath.class, "enumList");
        match(MapPath.class, "enumMap1");
        match(MapPath.class, "enumMap");
        match(ListPath.class, "list");
        match(ListPath.class, "list2");
        match(ListPath.class, "list3");
        match(ListPath.class, "list4");
        match(ListPath.class, "list5");
        match(SetPath.class, "set");
        match(SetPath.class, "sortedSet");
        match(SetPath.class, "set2");
        match(SetPath.class, "set3");
        match(SetPath.class, "set4");
        match(ListPath.class, "listOfObjects");
        match(SetPath.class, "setOfObjects");
        match(SetPath.class, "setOfObjects2");
        match(CollectionPath.class, "collection");
        match(CollectionPath.class, "collection2");
        match(CollectionPath.class, "collection3");
        match(CollectionPath.class, "collection4");
        match(MapPath.class, "map");
        match(MapPath.class, "map2");
        match(MapPath.class, "map3");
        match(MapPath.class, "map4");
        match(MapPath.class, "map5");
        match(MapPath.class, "map6");
        match(MapPath.class, "map7");
        match(MapPath.class, "map8");
        match(MapPath.class, "map9");
    }

    @Test
    public void list_usage() {
        String expected = "relationType.list.get(0).set";
        Assert.assertEquals(expected, list.get(0).set.toString());
        // assertEquals(expected, QRelationTest_RelationType.relationType.getList(0).set.toString());
        Assert.assertEquals(List.class, list.getType());
        Assert.assertEquals(Set.class, set.getType());
    }
}

