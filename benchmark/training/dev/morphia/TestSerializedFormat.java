/**
 * Copyright 2016 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.morphia;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import dev.morphia.converters.DefaultConverters;
import dev.morphia.query.Query;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


@SuppressWarnings("Since15")
public class TestSerializedFormat extends TestBase {
    @Test
    @SuppressWarnings("deprecation")
    public void testQueryFormat() {
        Assume.assumeTrue("This test requires Java 8", DefaultConverters.JAVA_8);
        Query<ReferenceType> query = getDs().find(ReferenceType.class).field("id").equal(new ObjectId(0, 0, ((short) (0)), 0)).field("referenceType").equal(new ReferenceType(2, "far")).field("embeddedType").equal(new EmbeddedReferenceType(3, "strikes")).field("string").equal("some value").field("embeddedArray").elemMatch(getDs().find(EmbeddedReferenceType.class).filter("number", 3).filter("text", "strikes")).field("embeddedSet").elemMatch(getDs().find(EmbeddedReferenceType.class).filter("number", 3).filter("text", "strikes")).field("embeddedList").elemMatch(getDs().find(EmbeddedReferenceType.class).filter("number", 3).filter("text", "strikes")).field("map.bar").equal(new EmbeddedReferenceType(1, "chance")).field("mapOfList.bar").in(Collections.singletonList(new EmbeddedReferenceType(1, "chance"))).field("mapOfList.foo").elemMatch(getDs().find(EmbeddedReferenceType.class).filter("number", 1).filter("text", "chance")).field("selfReference").equal(new ReferenceType(1, "blah")).field("mixedTypeList").elemMatch(getDs().find(EmbeddedReferenceType.class).filter("number", 3).filter("text", "strikes")).field("mixedTypeList").in(Collections.singletonList(new EmbeddedReferenceType(1, "chance"))).field("mixedTypeMap.foo").equal(new ReferenceType(3, "strikes")).field("mixedTypeMap.bar").equal(new EmbeddedReferenceType(3, "strikes")).field("mixedTypeMapOfList.bar").in(Collections.singletonList(new EmbeddedReferenceType(1, "chance"))).field("mixedTypeMapOfList.foo").elemMatch(getDs().find(EmbeddedReferenceType.class).filter("number", 3).filter("text", "strikes")).field("referenceMap.foo").equal(new ReferenceType(1, "chance")).field("referenceMap.bar").equal(new EmbeddedReferenceType(1, "chance"));
        DBObject dbObject = query.getQueryObject();
        final DBObject parse = BasicDBObject.parse(readFully("/QueryStructure.json"));
        Assert.assertEquals(parse, dbObject);
    }

    @Test
    public void testSavedEntityFormat() {
        Assume.assumeTrue("This test requires Java 8", DefaultConverters.JAVA_8);
        ReferenceType entity = new ReferenceType(1, "I'm a field value");
        entity.setReferenceType(new ReferenceType(42, "reference"));
        entity.setEmbeddedType(new EmbeddedReferenceType(18, "embedded"));
        entity.setEmbeddedSet(new HashSet<EmbeddedReferenceType>(Arrays.asList(new EmbeddedReferenceType(42, "Douglas Adams"), new EmbeddedReferenceType(1, "Love"))));
        entity.setEmbeddedList(Arrays.asList(new EmbeddedReferenceType(42, "Douglas Adams"), new EmbeddedReferenceType(1, "Love")));
        entity.setEmbeddedArray(new EmbeddedReferenceType[]{ new EmbeddedReferenceType(42, "Douglas Adams"), new EmbeddedReferenceType(1, "Love") });
        entity.getMap().put("first", new EmbeddedReferenceType(42, "Douglas Adams"));
        entity.getMap().put("second", new EmbeddedReferenceType(1, "Love"));
        entity.getMapOfList().put("first", Arrays.asList(new EmbeddedReferenceType(42, "Douglas Adams"), new EmbeddedReferenceType(1, "Love")));
        entity.getMapOfList().put("second", Arrays.asList(new EmbeddedReferenceType(1, "Love"), new EmbeddedReferenceType(42, "Douglas Adams")));
        entity.getMapOfSet().put("first", new HashSet<EmbeddedReferenceType>(Arrays.asList(new EmbeddedReferenceType(42, "Douglas Adams"), new EmbeddedReferenceType(1, "Love"))));
        entity.getMapOfSet().put("second", new HashSet<EmbeddedReferenceType>(Arrays.asList(new EmbeddedReferenceType(42, "Douglas Adams"), new EmbeddedReferenceType(1, "Love"))));
        entity.setSelfReference(entity);
        entity.setIdOnly(entity);
        entity.setReferenceArray(new ReferenceType[]{ new ReferenceType(2, "text 2"), new ReferenceType(3, "text 3") });
        entity.setReferenceList(Arrays.asList(new ReferenceType(2, "text 2"), new ReferenceType(3, "text 3")));
        entity.setReferenceSet(new HashSet<ReferenceType>(Arrays.asList(new ReferenceType(2, "text 2"), new ReferenceType(3, "text 3"))));
        entity.getReferenceMap().put("first", new ReferenceType(2, "text 2"));
        entity.getReferenceMap().put("second", new ReferenceType(3, "text 3"));
        entity.getReferenceMapOfList().put("first", Arrays.asList(new ReferenceType(2, "text 2"), new ReferenceType(3, "text 3")));
        entity.getReferenceMapOfList().put("second", Collections.singletonList(new ReferenceType(3, "text 3")));
        entity.setMixedTypeArray(new ReferenceType[]{ new ReferenceType(2, "text 2"), new ClassNameReferenceType(3, "text 3") });
        entity.setMixedTypeList(Arrays.asList(new ReferenceType(2, "text 2"), new ClassNameReferenceType(3, "text 3")));
        entity.setMixedTypeSet(new HashSet<ReferenceType>(Arrays.asList(new ReferenceType(2, "text 2"), new ClassNameReferenceType(3, "text 3"))));
        entity.getMixedTypeMap().put("first", new ReferenceType(2, "text 2"));
        entity.getMixedTypeMap().put("second", new ClassNameReferenceType(3, "text 3"));
        entity.getMixedTypeMapOfList().put("first", Arrays.asList(new ReferenceType(2, "text 2"), new ClassNameReferenceType(3, "text 3")));
        entity.getMixedTypeMapOfList().put("second", Collections.singletonList(new ClassNameReferenceType(3, "text 3")));
        getDs().save(entity);
        DBObject dbObject = getDs().getCollection(ReferenceType.class).findOne();
        Assert.assertEquals(BasicDBObject.parse(readFully("/ReferenceType.json")), dbObject);
        verifyCoverage(dbObject);
    }
}

