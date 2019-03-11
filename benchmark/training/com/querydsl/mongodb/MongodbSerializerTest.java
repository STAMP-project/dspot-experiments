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
package com.querydsl.mongodb;


import QUser.user.addresses;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.querydsl.mongodb.domain.QAddress;
import com.querydsl.mongodb.domain.QDummyEntity;
import com.querydsl.mongodb.domain.QPerson;
import com.querydsl.mongodb.domain.QUser;
import java.sql.Timestamp;
import java.util.Date;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class MongodbSerializerTest {
    private PathBuilder<Object> entityPath;

    private StringPath title;

    private NumberPath<Integer> year;

    private NumberPath<Double> gross;

    private NumberPath<Long> longField;

    private NumberPath<Short> shortField;

    private NumberPath<Byte> byteField;

    private NumberPath<Float> floatField;

    private DatePath<Date> date;

    private final Date dateVal = new Date();

    private DateTimePath<Timestamp> dateTime;

    private final Timestamp dateTimeVal = new Timestamp(System.currentTimeMillis());

    private MongodbSerializer serializer;

    @Test
    public void paths() {
        QUser user = QUser.user;
        Assert.assertEquals("user", serializer.visit(user, null));
        Assert.assertEquals("addresses", serializer.visit(user.addresses, null));
        Assert.assertEquals("addresses", serializer.visit(user.addresses.any(), null));
        Assert.assertEquals("addresses.street", serializer.visit(user.addresses.any().street, null));
        Assert.assertEquals("firstName", serializer.visit(user.firstName, null));
    }

    @Test
    public void propertyAnnotation() {
        QDummyEntity entity = QDummyEntity.dummyEntity;
        Assert.assertEquals("prop", serializer.visit(entity.property, null));
    }

    @Test
    public void indexedAccess() {
        QUser user = QUser.user;
        Assert.assertEquals("addresses.0.street", serializer.visit(user.addresses.get(0).street, null));
    }

    @Test
    public void collectionAny() {
        QUser user = QUser.user;
        assertQuery(user.addresses.any().street.eq("Aakatu"), MongodbSerializerTest.dbo("addresses.street", "Aakatu"));
    }

    @Test
    public void collectionIsEmpty() {
        BasicDBObject expected = MongodbSerializerTest.dbo("$or", MongodbSerializerTest.dblist(MongodbSerializerTest.dbo("addresses", MongodbSerializerTest.dblist()), MongodbSerializerTest.dbo("addresses", MongodbSerializerTest.dbo("$exists", false))));
        assertQuery(addresses.isEmpty(), expected);
    }

    @Test
    public void collectionIsNotEmpty() {
        BasicDBObject expected = MongodbSerializerTest.dbo("$nor", MongodbSerializerTest.dblist(MongodbSerializerTest.dbo("addresses", MongodbSerializerTest.dblist()), MongodbSerializerTest.dbo("addresses", MongodbSerializerTest.dbo("$exists", false))));
        assertQuery(addresses.isNotEmpty(), expected);
    }

    @Test
    public void equals() {
        assertQuery(title.eq("A"), MongodbSerializerTest.dbo("title", "A"));
        assertQuery(year.eq(1), MongodbSerializerTest.dbo("year", 1));
        assertQuery(gross.eq(1.0), MongodbSerializerTest.dbo("gross", 1.0));
        assertQuery(longField.eq(1L), MongodbSerializerTest.dbo("longField", 1L));
        assertQuery(shortField.eq(((short) (1))), MongodbSerializerTest.dbo("shortField", 1));
        assertQuery(byteField.eq(((byte) (1))), MongodbSerializerTest.dbo("byteField", 1L));
        assertQuery(floatField.eq(1.0F), MongodbSerializerTest.dbo("floatField", 1.0F));
        assertQuery(date.eq(dateVal), MongodbSerializerTest.dbo("date", dateVal));
        assertQuery(dateTime.eq(dateTimeVal), MongodbSerializerTest.dbo("dateTime", dateTimeVal));
    }

    @Test
    public void eqAndEq() {
        assertQuery(title.eq("A").and(year.eq(1)), MongodbSerializerTest.dbo("title", "A").append("year", 1));
        assertQuery(title.eq("A").and(year.eq(1).and(gross.eq(1.0))), MongodbSerializerTest.dbo("title", "A").append("year", 1).append("gross", 1.0));
    }

    @Test
    public void notEq() {
        assertQuery(title.ne("A"), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$ne", "A")));
    }

    @Test
    public void between() {
        assertQuery(year.between(1, 10), MongodbSerializerTest.dbo("year", MongodbSerializerTest.dbo("$gte", 1).append("$lte", 10)));
    }

    @Test
    public void lessAndGreaterAndBetween() {
        assertQuery(title.lt("A"), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$lt", "A")));
        assertQuery(year.gt(1), MongodbSerializerTest.dbo("year", MongodbSerializerTest.dbo("$gt", 1)));
        assertQuery(title.loe("A"), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$lte", "A")));
        assertQuery(year.goe(1), MongodbSerializerTest.dbo("year", MongodbSerializerTest.dbo("$gte", 1)));
        assertQuery(year.gt(1).and(year.lt(10)), MongodbSerializerTest.dbo("$and", MongodbSerializerTest.dblist(MongodbSerializerTest.dbo("year", MongodbSerializerTest.dbo("$gt", 1)), MongodbSerializerTest.dbo("year", MongodbSerializerTest.dbo("$lt", 10)))));
        assertQuery(year.between(1, 10), MongodbSerializerTest.dbo("year", MongodbSerializerTest.dbo("$gte", 1).append("$lte", 10)));
    }

    @Test
    public void in() {
        assertQuery(year.in(1, 2, 3), MongodbSerializerTest.dbo("year", MongodbSerializerTest.dbo("$in", 1, 2, 3)));
    }

    @Test
    public void notIn() {
        assertQuery(year.in(1, 2, 3).not(), MongodbSerializerTest.dbo("year", MongodbSerializerTest.dbo("$nin", 1, 2, 3)));
        assertQuery(year.notIn(1, 2, 3), MongodbSerializerTest.dbo("year", MongodbSerializerTest.dbo("$nin", 1, 2, 3)));
    }

    @Test
    public void orderBy() {
        DBObject orderBy = serializer.toSort(sortList(year.asc()));
        Assert.assertEquals(MongodbSerializerTest.dbo("year", 1), orderBy);
        orderBy = serializer.toSort(sortList(year.desc()));
        Assert.assertEquals(MongodbSerializerTest.dbo("year", (-1)), orderBy);
        orderBy = serializer.toSort(sortList(year.desc(), title.asc()));
        Assert.assertEquals(MongodbSerializerTest.dbo("year", (-1)).append("title", 1), orderBy);
    }

    @Test
    public void regexCases() {
        assertQuery(title.startsWith("A"), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$regex", "^\\QA\\E")));
        assertQuery(title.startsWithIgnoreCase("A"), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$regex", "^\\QA\\E").append("$options", "i")));
        assertQuery(title.endsWith("A"), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$regex", "\\QA\\E$")));
        assertQuery(title.endsWithIgnoreCase("A"), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$regex", "\\QA\\E$").append("$options", "i")));
        assertQuery(title.equalsIgnoreCase("A"), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$regex", "^\\QA\\E$").append("$options", "i")));
        assertQuery(title.contains("A"), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$regex", ".*\\QA\\E.*")));
        assertQuery(title.containsIgnoreCase("A"), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$regex", ".*\\QA\\E.*").append("$options", "i")));
        assertQuery(title.matches(".*A^"), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$regex", ".*A^")));
    }

    @Test
    public void and() {
        assertQuery(title.startsWithIgnoreCase("a").and(title.endsWithIgnoreCase("b")), MongodbSerializerTest.dbo("$and", MongodbSerializerTest.dblist(MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$regex", "^\\Qa\\E").append("$options", "i")), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$regex", "\\Qb\\E$").append("$options", "i")))));
    }

    @Test
    public void near() {
        assertQuery(MongodbExpressions.near(new Point("point"), 1.0, 2.0), MongodbSerializerTest.dbo("point", MongodbSerializerTest.dbo("$near", MongodbSerializerTest.dblist(1.0, 2.0))));
    }

    @Test
    public void near_sphere() {
        assertQuery(MongodbExpressions.nearSphere(new Point("point"), 1.0, 2.0), MongodbSerializerTest.dbo("point", MongodbSerializerTest.dbo("$nearSphere", MongodbSerializerTest.dblist(1.0, 2.0))));
    }

    @Test
    public void not() {
        assertQuery(title.eq("A").not(), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$ne", "A")));
        assertQuery(title.lt("A").not().and(year.ne(1800)), MongodbSerializerTest.dbo("title", MongodbSerializerTest.dbo("$not", MongodbSerializerTest.dbo("$lt", "A"))).append("year", MongodbSerializerTest.dbo("$ne", 1800)));
    }

    @Test
    public void objectId() {
        ObjectId id = new ObjectId();
        QPerson person = QPerson.person;
        assertQuery(person.id.eq(id), MongodbSerializerTest.dbo("_id", id));
        assertQuery(person.addressId.eq(id), MongodbSerializerTest.dbo("addressId", id));
    }

    @Test
    public void path() {
        QUser user = QUser.user;
        Assert.assertEquals("firstName", serializer.visit(user.firstName, null));
        Assert.assertEquals("firstName", serializer.visit(user.as(QUser.class).firstName, null));
        Assert.assertEquals("mainAddress.street", serializer.visit(user.mainAddress().street, null));
        Assert.assertEquals("mainAddress.street", serializer.visit(user.mainAddress().as(QAddress.class).street, null));
    }
}

