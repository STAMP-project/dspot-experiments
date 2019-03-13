/**
 * Copyright (C) 2010 Olafur Gauti Gudmundsson
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package dev.morphia;


import WriteConcern.ACKNOWLEDGED;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.PreLoad;
import dev.morphia.query.FindOptions;
import dev.morphia.query.PushOptions;
import dev.morphia.query.Query;
import dev.morphia.query.TestQuery;
import dev.morphia.query.UpdateOperations;
import dev.morphia.query.UpdateResults;
import dev.morphia.query.ValidationException;
import dev.morphia.testmodel.Article;
import dev.morphia.testmodel.Circle;
import dev.morphia.testmodel.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.bson.types.ObjectId;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Scott Hernandez
 */
@SuppressWarnings("UnusedDeclaration")
public class TestUpdateOps extends TestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestUpdateOps.class);

    @Test
    public void shouldUpdateAnArrayElement() {
        // given
        ObjectId parentId = new ObjectId();
        String childName = "Bob";
        String updatedLastName = "updatedLastName";
        TestUpdateOps.Parent parent = new TestUpdateOps.Parent();
        parent.id = parentId;
        parent.children.add(new TestUpdateOps.Child("Anthony", "Child"));
        parent.children.add(new TestUpdateOps.Child(childName, "originalLastName"));
        getDs().save(parent);
        // when
        Query<TestUpdateOps.Parent> query = getDs().find(TestUpdateOps.Parent.class).field("_id").equal(parentId).field("children.first").equal(childName);
        UpdateOperations<TestUpdateOps.Parent> updateOps = getDs().createUpdateOperations(TestUpdateOps.Parent.class).set("children.$.last", updatedLastName);
        UpdateResults updateResults = getDs().update(query, updateOps);
        // then
        Assert.assertThat(updateResults.getUpdatedCount(), CoreMatchers.is(1));
        Assert.assertThat(getDs().find(TestUpdateOps.Parent.class).filter("id", parentId).find(new FindOptions().limit(1)).next().children, Matchers.hasItem(new TestUpdateOps.Child(childName, updatedLastName)));
    }

    @Test
    public void testDisableValidation() {
        TestUpdateOps.Child child1 = new TestUpdateOps.Child("James", "Rigney");
        validateClassName("children", getDs().createUpdateOperations(TestUpdateOps.Parent.class).removeAll("children", child1), false);
        validateClassName("children", getDs().createUpdateOperations(TestUpdateOps.Parent.class).disableValidation().removeAll("children", child1), false);
        validateClassName("c", getDs().createUpdateOperations(TestUpdateOps.Parent.class).disableValidation().removeAll("c", child1), true);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testAdd() {
        checkMinServerVersion(2.6);
        TestUpdateOps.ContainsIntArray cIntArray = new TestUpdateOps.ContainsIntArray();
        Datastore ds = getDs();
        ds.save(cIntArray);
        Assert.assertThat(ds.get(cIntArray).values, CoreMatchers.is(new TestUpdateOps.ContainsIntArray().values));
        // add 4 to array
        assertUpdated(ds.update(ds.createQuery(TestUpdateOps.ContainsIntArray.class), ds.createUpdateOperations(TestUpdateOps.ContainsIntArray.class).add("values", 4, false)), 1);
        Assert.assertThat(ds.get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 4 }));
        // add unique (4) -- noop
        assertUpdated(ds.update(ds.createQuery(TestUpdateOps.ContainsIntArray.class), ds.createUpdateOperations(TestUpdateOps.ContainsIntArray.class).add("values", 4, false)), 1);
        Assert.assertThat(ds.get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 4 }));
        // add dup 4
        assertUpdated(ds.update(ds.createQuery(TestUpdateOps.ContainsIntArray.class), ds.createUpdateOperations(TestUpdateOps.ContainsIntArray.class).add("values", 4, true)), 1);
        Assert.assertThat(ds.get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 4, 4 }));
        // cleanup for next tests
        ds.delete(ds.find(TestUpdateOps.ContainsIntArray.class));
        cIntArray = ds.getByKey(TestUpdateOps.ContainsIntArray.class, ds.save(new TestUpdateOps.ContainsIntArray()));
        // add [4,5]
        final List<Integer> newValues = new ArrayList<Integer>();
        newValues.add(4);
        newValues.add(5);
        assertUpdated(ds.update(ds.createQuery(TestUpdateOps.ContainsIntArray.class), ds.createUpdateOperations(TestUpdateOps.ContainsIntArray.class).addAll("values", newValues, false)), 1);
        Assert.assertThat(ds.get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 4, 5 }));
        // add them again... noop
        assertUpdated(ds.update(ds.createQuery(TestUpdateOps.ContainsIntArray.class), ds.createUpdateOperations(TestUpdateOps.ContainsIntArray.class).addAll("values", newValues, false)), 1);
        Assert.assertThat(ds.get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 4, 5 }));
        // add dups [4,5]
        assertUpdated(ds.update(ds.createQuery(TestUpdateOps.ContainsIntArray.class), ds.createUpdateOperations(TestUpdateOps.ContainsIntArray.class).addAll("values", newValues, true)), 1);
        Assert.assertThat(ds.get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 4, 5, 4, 5 }));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testAddAll() {
        getMorphia().map(TestUpdateOps.EntityLogs.class, TestUpdateOps.EntityLog.class);
        String uuid = "4ec6ada9-081a-424f-bee0-934c0bc4fab7";
        TestUpdateOps.EntityLogs logs = new TestUpdateOps.EntityLogs();
        logs.uuid = uuid;
        getDs().save(logs);
        Query<TestUpdateOps.EntityLogs> finder = getDs().find(TestUpdateOps.EntityLogs.class).field("uuid").equal(uuid);
        // both of these entries will have a className attribute
        List<TestUpdateOps.EntityLog> latestLogs = Arrays.asList(new TestUpdateOps.EntityLog("whatever1", new Date()), new TestUpdateOps.EntityLog("whatever2", new Date()));
        UpdateOperations<TestUpdateOps.EntityLogs> updateOperationsAll = getDs().createUpdateOperations(TestUpdateOps.EntityLogs.class).addAll("logs", latestLogs, false);
        getDs().update(finder, updateOperationsAll, true);
        validateNoClassName(finder.get());
        // this entry will NOT have a className attribute
        UpdateOperations<TestUpdateOps.EntityLogs> updateOperations3 = getDs().createUpdateOperations(TestUpdateOps.EntityLogs.class).add("logs", new TestUpdateOps.EntityLog("whatever3", new Date()), false);
        getDs().update(finder, updateOperations3, true);
        validateNoClassName(finder.get());
        // this entry will NOT have a className attribute
        UpdateOperations<TestUpdateOps.EntityLogs> updateOperations4 = getDs().createUpdateOperations(TestUpdateOps.EntityLogs.class).add("logs", new TestUpdateOps.EntityLog("whatever4", new Date()), false);
        getDs().update(finder, updateOperations4, true);
        validateNoClassName(finder.get());
    }

    @Test
    public void testAddToSet() {
        TestUpdateOps.ContainsIntArray cIntArray = new TestUpdateOps.ContainsIntArray();
        getDs().save(cIntArray);
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new TestUpdateOps.ContainsIntArray().values));
        assertUpdated(getDs().update(getDs().find(TestUpdateOps.ContainsIntArray.class), getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).addToSet("values", 5)), 1);
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 5 }));
        assertUpdated(getDs().update(getDs().find(TestUpdateOps.ContainsIntArray.class), getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).addToSet("values", 4)), 1);
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 5, 4 }));
        assertUpdated(getDs().update(getDs().find(TestUpdateOps.ContainsIntArray.class), getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).addToSet("values", Arrays.asList(8, 9))), 1);
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 5, 4, 8, 9 }));
        assertUpdated(getDs().update(getDs().find(TestUpdateOps.ContainsIntArray.class), getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).addToSet("values", Arrays.asList(4, 5))), 1);
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 5, 4, 8, 9 }));
        assertUpdated(getDs().update(getDs().find(TestUpdateOps.ContainsIntArray.class), getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).addToSet("values", new HashSet<Integer>(Arrays.asList(10, 11)))), 1);
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 5, 4, 8, 9, 10, 11 }));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testUpdateFirst() {
        TestUpdateOps.ContainsIntArray cIntArray = new TestUpdateOps.ContainsIntArray();
        TestUpdateOps.ContainsIntArray control = new TestUpdateOps.ContainsIntArray();
        Datastore ds = getDs();
        ds.save(cIntArray, control);
        Assert.assertThat(ds.get(cIntArray).values, CoreMatchers.is(new TestUpdateOps.ContainsIntArray().values));
        Query<TestUpdateOps.ContainsIntArray> query = ds.find(TestUpdateOps.ContainsIntArray.class);
        doUpdates(cIntArray, control, query, ds.createUpdateOperations(TestUpdateOps.ContainsIntArray.class).addToSet("values", 4), new Integer[]{ 1, 2, 3, 4 });
        doUpdates(cIntArray, control, query, ds.createUpdateOperations(TestUpdateOps.ContainsIntArray.class).addToSet("values", Arrays.asList(4, 5)), new Integer[]{ 1, 2, 3, 4, 5 });
        assertInserted(ds.updateFirst(ds.find(TestUpdateOps.ContainsIntArray.class).filter("values", new Integer[]{ 4, 5, 7 }), ds.createUpdateOperations(TestUpdateOps.ContainsIntArray.class).addToSet("values", 6), true));
        Assert.assertNotNull(ds.find(TestUpdateOps.ContainsIntArray.class).filter("values", new Integer[]{ 4, 5, 7, 6 }));
    }

    @Test
    public void testExistingUpdates() {
        Circle c = new Circle(100.0);
        getDs().save(c);
        c = new Circle(12.0);
        getDs().save(c);
        assertUpdated(getDs().update(getDs().find(Circle.class), getDs().createUpdateOperations(Circle.class).inc("radius", 1.0), new UpdateOptions()), 1);
        assertUpdated(getDs().update(getDs().find(Circle.class), getDs().createUpdateOperations(Circle.class).inc("radius")), 2);
        // test possible data type change.
        final Circle updatedCircle = getDs().find(Circle.class).filter("radius", 13).find(new FindOptions().limit(1)).next();
        Assert.assertThat(updatedCircle, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(updatedCircle.getRadius(), CoreMatchers.is(13.0));
    }

    @Test
    public void testIncDec() {
        final Rectangle[] array = new Rectangle[]{ new Rectangle(1, 10), new Rectangle(1, 10), new Rectangle(1, 10), new Rectangle(10, 10), new Rectangle(10, 10) };
        for (final Rectangle rect : array) {
            getDs().save(rect);
        }
        final Query<Rectangle> heightOf1 = getDs().find(Rectangle.class).filter("height", 1.0);
        final Query<Rectangle> heightOf2 = getDs().find(Rectangle.class).filter("height", 2.0);
        final Query<Rectangle> heightOf35 = getDs().find(Rectangle.class).filter("height", 3.5);
        Assert.assertThat(getDs().getCount(heightOf1), CoreMatchers.is(3L));
        Assert.assertThat(getDs().getCount(heightOf2), CoreMatchers.is(0L));
        final UpdateResults results = getDs().update(heightOf1, getDs().createUpdateOperations(Rectangle.class).inc("height"));
        assertUpdated(results, 3);
        Assert.assertThat(getDs().getCount(heightOf1), CoreMatchers.is(0L));
        Assert.assertThat(getDs().getCount(heightOf2), CoreMatchers.is(3L));
        getDs().update(heightOf2, getDs().createUpdateOperations(Rectangle.class).dec("height"));
        Assert.assertThat(getDs().getCount(heightOf1), CoreMatchers.is(3L));
        Assert.assertThat(getDs().getCount(heightOf2), CoreMatchers.is(0L));
        getDs().update(heightOf1, getDs().createUpdateOperations(Rectangle.class).inc("height", 2.5));
        Assert.assertThat(getDs().getCount(heightOf1), CoreMatchers.is(0L));
        Assert.assertThat(getDs().getCount(heightOf35), CoreMatchers.is(3L));
        getDs().update(heightOf35, getDs().createUpdateOperations(Rectangle.class).dec("height", 2.5));
        Assert.assertThat(getDs().getCount(heightOf1), CoreMatchers.is(3L));
        Assert.assertThat(getDs().getCount(heightOf35), CoreMatchers.is(0L));
        getDs().update(getDs().find(Rectangle.class).filter("height", 1.0), getDs().createUpdateOperations(Rectangle.class).set("height", 1.0).inc("width", 20.0));
        Assert.assertThat(getDs().getCount(Rectangle.class), CoreMatchers.is(5L));
        Assert.assertThat(getDs().find(Rectangle.class).filter("height", 1.0).find(new FindOptions().limit(1)).next(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(getDs().find(Rectangle.class).filter("width", 30.0).find(new FindOptions().limit(1)).next(), CoreMatchers.is(CoreMatchers.notNullValue()));
        getDs().update(getDs().find(Rectangle.class).filter("width", 30.0), getDs().createUpdateOperations(Rectangle.class).set("height", 2.0).set("width", 2.0));
        Assert.assertThat(getDs().find(Rectangle.class).filter("width", 1.0).find(new FindOptions().limit(1)).tryNext(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(getDs().find(Rectangle.class).filter("width", 2.0).find(new FindOptions().limit(1)).next(), CoreMatchers.is(CoreMatchers.notNullValue()));
        getDs().update(heightOf35, getDs().createUpdateOperations(Rectangle.class).dec("height", 1));
        getDs().update(heightOf35, getDs().createUpdateOperations(Rectangle.class).dec("height", Long.MAX_VALUE));
        getDs().update(heightOf35, getDs().createUpdateOperations(Rectangle.class).dec("height", 1.5F));
        getDs().update(heightOf35, getDs().createUpdateOperations(Rectangle.class).dec("height", Double.MAX_VALUE));
        try {
            getDs().update(heightOf35, getDs().createUpdateOperations(Rectangle.class).dec("height", new AtomicInteger(1)));
            Assert.fail("Wrong data type not recognized.");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testInsertUpdate() {
        assertInserted(getDs().update(getDs().find(Circle.class).field("radius").equal(0), getDs().createUpdateOperations(Circle.class).inc("radius", 1.0), true));
        assertInserted(getDs().update(getDs().find(Circle.class).field("radius").equal(0), getDs().createUpdateOperations(Circle.class).inc("radius", 1.0), new UpdateOptions().upsert(true)));
    }

    @Test
    public void testInsertWithRef() {
        final TestQuery.Pic pic = new TestQuery.Pic();
        pic.setName("fist");
        final Key<TestQuery.Pic> picKey = getDs().save(pic);
        assertInserted(getDs().update(getDs().find(TestQuery.ContainsPic.class).filter("name", "first").filter("pic", picKey), getDs().createUpdateOperations(TestQuery.ContainsPic.class).set("name", "A"), new UpdateOptions().upsert(true)));
        Assert.assertThat(getDs().find(TestQuery.ContainsPic.class).count(), CoreMatchers.is(1L));
        getDs().delete(getDs().find(TestQuery.ContainsPic.class));
        assertInserted(getDs().update(getDs().find(TestQuery.ContainsPic.class).filter("name", "first").filter("pic", pic), getDs().createUpdateOperations(TestQuery.ContainsPic.class).set("name", "second"), new UpdateOptions().upsert(true)));
        Assert.assertThat(getDs().find(TestQuery.ContainsPic.class).count(), CoreMatchers.is(1L));
        // test reading the object.
        final TestQuery.ContainsPic cp = getDs().find(TestQuery.ContainsPic.class).find(new FindOptions().limit(1)).next();
        Assert.assertThat(cp, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(cp.getName(), CoreMatchers.is("second"));
        Assert.assertThat(cp.getPic(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(cp.getPic().getName(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(cp.getPic().getName(), CoreMatchers.is("fist"));
    }

    @Test
    public void testMaxKeepsCurrentDocumentValueWhenThisIsLargerThanSuppliedValue() {
        checkMinServerVersion(2.6);
        final ObjectId id = new ObjectId();
        final double originalValue = 2.0;
        Datastore ds = getDs();
        assertInserted(ds.update(ds.find(Circle.class).field("id").equal(id), ds.createUpdateOperations(Circle.class).setOnInsert("radius", originalValue), new UpdateOptions().upsert(true)));
        assertUpdated(ds.update(ds.find(Circle.class).field("id").equal(id), ds.createUpdateOperations(Circle.class).max("radius", 1.0), new UpdateOptions().upsert(true)), 1);
        Assert.assertThat(ds.get(Circle.class, id).getRadius(), CoreMatchers.is(originalValue));
    }

    @Test
    public void testMinKeepsCurrentDocumentValueWhenThisIsSmallerThanSuppliedValue() {
        checkMinServerVersion(2.6);
        final ObjectId id = new ObjectId();
        final double originalValue = 3.0;
        assertInserted(getDs().update(getDs().find(Circle.class).field("id").equal(id), getDs().createUpdateOperations(Circle.class).setOnInsert("radius", originalValue), new UpdateOptions().upsert(true)));
        assertUpdated(getDs().update(getDs().find(Circle.class).field("id").equal(id), getDs().createUpdateOperations(Circle.class).min("radius", 5.0), new UpdateOptions().upsert(true)), 1);
        final Circle updatedCircle = getDs().get(Circle.class, id);
        Assert.assertThat(updatedCircle, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(updatedCircle.getRadius(), CoreMatchers.is(originalValue));
    }

    @Test
    public void testMinUsesSuppliedValueWhenThisIsSmallerThanCurrentDocumentValue() {
        checkMinServerVersion(2.6);
        final ObjectId id = new ObjectId();
        final double newLowerValue = 2.0;
        assertInserted(getDs().update(getDs().find(Circle.class).field("id").equal(id), getDs().createUpdateOperations(Circle.class).setOnInsert("radius", 3.0), new UpdateOptions().upsert(true)));
        assertUpdated(getDs().update(getDs().find(Circle.class).field("id").equal(id), getDs().createUpdateOperations(Circle.class).min("radius", newLowerValue), new UpdateOptions().upsert(true)), 1);
        final Circle updatedCircle = getDs().get(Circle.class, id);
        Assert.assertThat(updatedCircle, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(updatedCircle.getRadius(), CoreMatchers.is(newLowerValue));
    }

    @Test
    public void testPush() {
        checkMinServerVersion(2.6);
        TestUpdateOps.ContainsIntArray cIntArray = new TestUpdateOps.ContainsIntArray();
        getDs().save(cIntArray);
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new TestUpdateOps.ContainsIntArray().values));
        getDs().update(getDs().find(TestUpdateOps.ContainsIntArray.class), getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).push("values", 4), new UpdateOptions().multi(false));
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 4 }));
        getDs().update(getDs().find(TestUpdateOps.ContainsIntArray.class), getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).push("values", 4), new UpdateOptions().multi(false));
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 4, 4 }));
        getDs().update(getDs().find(TestUpdateOps.ContainsIntArray.class), getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).push("values", Arrays.asList(5, 6)), new UpdateOptions().multi(false));
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 3, 4, 4, 5, 6 }));
        getDs().update(getDs().find(TestUpdateOps.ContainsIntArray.class), getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).push("values", 12, PushOptions.options().position(2)), new UpdateOptions().multi(false));
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 12, 3, 4, 4, 5, 6 }));
        getDs().update(getDs().find(TestUpdateOps.ContainsIntArray.class), getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).push("values", Arrays.asList(99, 98, 97), PushOptions.options().position(4)), new UpdateOptions().multi(false));
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new Integer[]{ 1, 2, 12, 3, 99, 98, 97, 4, 4, 5, 6 }));
    }

    @Test
    public void testRemoveAllSingleValue() {
        TestUpdateOps.EntityLogs logs = new TestUpdateOps.EntityLogs();
        Date date = new Date();
        logs.logs.addAll(Arrays.asList(new TestUpdateOps.EntityLog("log1", date), new TestUpdateOps.EntityLog("log2", date), new TestUpdateOps.EntityLog("log3", date), new TestUpdateOps.EntityLog("log1", date), new TestUpdateOps.EntityLog("log2", date), new TestUpdateOps.EntityLog("log3", date)));
        Datastore ds = getDs();
        ds.save(logs);
        UpdateOperations<TestUpdateOps.EntityLogs> operations = ds.createUpdateOperations(TestUpdateOps.EntityLogs.class).removeAll("logs", new TestUpdateOps.EntityLog("log3", date));
        UpdateResults results = ds.update(ds.find(TestUpdateOps.EntityLogs.class), operations);
        Assert.assertEquals(1, results.getUpdatedCount());
        TestUpdateOps.EntityLogs updated = ds.find(TestUpdateOps.EntityLogs.class).find(new FindOptions().limit(1)).next();
        Assert.assertEquals(4, updated.logs.size());
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(new TestUpdateOps.EntityLog(("log" + ((i % 2) + 1)), date), updated.logs.get(i));
        }
    }

    @Test
    public void testRemoveAllList() {
        TestUpdateOps.EntityLogs logs = new TestUpdateOps.EntityLogs();
        Date date = new Date();
        logs.logs.addAll(Arrays.asList(new TestUpdateOps.EntityLog("log1", date), new TestUpdateOps.EntityLog("log2", date), new TestUpdateOps.EntityLog("log3", date), new TestUpdateOps.EntityLog("log1", date), new TestUpdateOps.EntityLog("log2", date), new TestUpdateOps.EntityLog("log3", date)));
        Datastore ds = getDs();
        ds.save(logs);
        UpdateOperations<TestUpdateOps.EntityLogs> operations = ds.createUpdateOperations(TestUpdateOps.EntityLogs.class).removeAll("logs", Collections.singletonList(new TestUpdateOps.EntityLog("log3", date)));
        UpdateResults results = ds.update(ds.find(TestUpdateOps.EntityLogs.class), operations);
        Assert.assertEquals(1, results.getUpdatedCount());
        TestUpdateOps.EntityLogs updated = ds.find(TestUpdateOps.EntityLogs.class).find(new FindOptions().limit(1)).next();
        Assert.assertEquals(4, updated.logs.size());
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(new TestUpdateOps.EntityLog(("log" + ((i % 2) + 1)), date), updated.logs.get(i));
        }
    }

    @Test
    public void testElemMatchUpdate() {
        // setUp
        Object id = getDs().save(new TestUpdateOps.ContainsIntArray()).getId();
        Assert.assertThat(getDs().get(TestUpdateOps.ContainsIntArray.class, id).values, Matchers.arrayContaining(1, 2, 3));
        // do patch
        Query<TestUpdateOps.ContainsIntArray> q = getDs().createQuery(TestUpdateOps.ContainsIntArray.class).filter("id", id).filter("values", 2);
        UpdateOperations<TestUpdateOps.ContainsIntArray> ops = getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).set("values.$", 5);
        getDs().update(q, ops);
        // expected
        Assert.assertThat(getDs().get(TestUpdateOps.ContainsIntArray.class, id).values, Matchers.arrayContaining(1, 5, 3));
    }

    @Test
    public void testRemoveFirst() {
        final TestUpdateOps.ContainsIntArray cIntArray = new TestUpdateOps.ContainsIntArray();
        getDs().save(cIntArray);
        TestUpdateOps.ContainsIntArray cIALoaded = getDs().get(cIntArray);
        Assert.assertThat(cIALoaded.values.length, CoreMatchers.is(3));
        Assert.assertThat(cIALoaded.values, CoreMatchers.is(new TestUpdateOps.ContainsIntArray().values));
        assertUpdated(getDs().update(getDs().find(TestUpdateOps.ContainsIntArray.class), getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).removeFirst("values"), new UpdateOptions().multi(false)), 1);
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new Integer[]{ 2, 3 }));
        assertUpdated(getDs().update(getDs().find(TestUpdateOps.ContainsIntArray.class), getDs().createUpdateOperations(TestUpdateOps.ContainsIntArray.class).removeLast("values"), new UpdateOptions().multi(false)), 1);
        Assert.assertThat(getDs().get(cIntArray).values, CoreMatchers.is(new Integer[]{ 2 }));
    }

    @Test
    public void testSetOnInsertWhenInserting() {
        checkMinServerVersion(2.4);
        ObjectId id = new ObjectId();
        assertInserted(getDs().update(getDs().find(Circle.class).field("id").equal(id), getDs().createUpdateOperations(Circle.class).setOnInsert("radius", 2.0), new UpdateOptions().upsert(true)));
        final Circle updatedCircle = getDs().get(Circle.class, id);
        Assert.assertThat(updatedCircle, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(updatedCircle.getRadius(), CoreMatchers.is(2.0));
    }

    @Test
    public void testSetOnInsertWhenUpdating() {
        checkMinServerVersion(2.4);
        ObjectId id = new ObjectId();
        assertInserted(getDs().update(getDs().find(Circle.class).field("id").equal(id), getDs().createUpdateOperations(Circle.class).setOnInsert("radius", 1.0), new UpdateOptions().upsert(true)));
        assertUpdated(getDs().update(getDs().find(Circle.class).field("id").equal(id), getDs().createUpdateOperations(Circle.class).setOnInsert("radius", 2.0), new UpdateOptions().upsert(true)), 1);
        final Circle updatedCircle = getDs().get(Circle.class, id);
        Assert.assertThat(updatedCircle, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(updatedCircle.getRadius(), CoreMatchers.is(1.0));
    }

    @Test
    public void testSetUnset() {
        Datastore ds = getDs();
        final Key<Circle> key = ds.save(new Circle(1));
        assertUpdated(ds.update(ds.find(Circle.class).filter("radius", 1.0), ds.createUpdateOperations(Circle.class).set("radius", 2.0), new UpdateOptions().multi(false)), 1);
        Assert.assertThat(ds.getByKey(Circle.class, key).getRadius(), CoreMatchers.is(2.0));
        assertUpdated(ds.update(ds.find(Circle.class).filter("radius", 2.0), ds.createUpdateOperations(Circle.class).unset("radius"), new UpdateOptions().multi(false)), 1);
        Assert.assertThat(ds.getByKey(Circle.class, key).getRadius(), CoreMatchers.is(0.0));
        Article article = new Article();
        ds.save(article);
        ds.update(ds.find(Article.class), ds.createUpdateOperations(Article.class).set("translations", new HashMap<String, dev.morphia.testmodel.Translation>()));
        ds.update(ds.find(Article.class), ds.createUpdateOperations(Article.class).unset("translations"));
    }

    @Test
    public void testUpdateFirstNoCreate() {
        getDs().delete(getDs().find(TestUpdateOps.EntityLogs.class));
        List<TestUpdateOps.EntityLogs> logs = new ArrayList<TestUpdateOps.EntityLogs>();
        for (int i = 0; i < 100; i++) {
            logs.add(createEntryLogs("name", ("logs" + i)));
        }
        TestUpdateOps.EntityLogs logs1 = logs.get(0);
        Query<TestUpdateOps.EntityLogs> query = getDs().find(TestUpdateOps.EntityLogs.class);
        UpdateOperations<TestUpdateOps.EntityLogs> updateOperations = getDs().createUpdateOperations(TestUpdateOps.EntityLogs.class);
        BasicDBObject object = new BasicDBObject("new", "value");
        updateOperations.set("raw", object);
        getDs().update(query, updateOperations, new UpdateOptions());
        List<TestUpdateOps.EntityLogs> list = TestBase.toList(getDs().find(TestUpdateOps.EntityLogs.class).find());
        for (int i = 0; i < (list.size()); i++) {
            final TestUpdateOps.EntityLogs entityLogs = list.get(i);
            Assert.assertEquals((entityLogs.id.equals(logs1.id) ? object : logs.get(i).raw), entityLogs.raw);
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testUpdateFirstNoCreateWithEntity() {
        List<TestUpdateOps.EntityLogs> logs = new ArrayList<TestUpdateOps.EntityLogs>();
        for (int i = 0; i < 100; i++) {
            logs.add(createEntryLogs("name", ("logs" + i)));
        }
        TestUpdateOps.EntityLogs logs1 = logs.get(0);
        Query<TestUpdateOps.EntityLogs> query = getDs().find(TestUpdateOps.EntityLogs.class);
        BasicDBObject object = new BasicDBObject("new", "value");
        TestUpdateOps.EntityLogs newLogs = new TestUpdateOps.EntityLogs();
        newLogs.raw = object;
        getDs().updateFirst(query, newLogs, false);
        List<TestUpdateOps.EntityLogs> list = TestBase.toList(getDs().find(TestUpdateOps.EntityLogs.class).find());
        for (int i = 0; i < (list.size()); i++) {
            final TestUpdateOps.EntityLogs entityLogs = list.get(i);
            Assert.assertEquals((entityLogs.id.equals(logs1.id) ? object : logs.get(i).raw), entityLogs.raw);
        }
    }

    @Test
    public void testUpdateFirstNoCreateWithWriteConcern() {
        List<TestUpdateOps.EntityLogs> logs = new ArrayList<TestUpdateOps.EntityLogs>();
        for (int i = 0; i < 100; i++) {
            logs.add(createEntryLogs("name", ("logs" + i)));
        }
        TestUpdateOps.EntityLogs logs1 = logs.get(0);
        getDs().update(getDs().find(TestUpdateOps.EntityLogs.class), getDs().createUpdateOperations(TestUpdateOps.EntityLogs.class).set("raw", new BasicDBObject("new", "value")), new UpdateOptions());
        List<TestUpdateOps.EntityLogs> list = TestBase.toList(getDs().find(TestUpdateOps.EntityLogs.class).find());
        for (int i = 0; i < (list.size()); i++) {
            final TestUpdateOps.EntityLogs entityLogs = list.get(i);
            Assert.assertEquals((entityLogs.id.equals(logs1.id) ? new BasicDBObject("new", "value") : logs.get(i).raw), entityLogs.raw);
        }
    }

    @Test
    public void testUpdateKeyRef() {
        final TestUpdateOps.ContainsPicKey cpk = new TestUpdateOps.ContainsPicKey();
        cpk.name = "cpk one";
        Datastore ds = getDs();
        ds.save(cpk);
        final TestQuery.Pic pic = new TestQuery.Pic();
        pic.setName("fist again");
        final Key<TestQuery.Pic> picKey = ds.save(pic);
        // picKey = getDs().getKey(pic);
        // test with Key<Pic>
        Assert.assertThat(ds.update(ds.find(TestUpdateOps.ContainsPicKey.class).filter("name", cpk.name), ds.createUpdateOperations(TestUpdateOps.ContainsPicKey.class).set("pic", pic), new UpdateOptions()).getUpdatedCount(), CoreMatchers.is(1));
        // test reading the object.
        final TestUpdateOps.ContainsPicKey cpk2 = ds.find(TestUpdateOps.ContainsPicKey.class).find(new FindOptions().limit(1)).next();
        Assert.assertThat(cpk2, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(cpk.name, CoreMatchers.is(cpk2.name));
        Assert.assertThat(cpk2.pic, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(picKey, CoreMatchers.is(cpk2.pic));
        ds.update(ds.find(TestUpdateOps.ContainsPicKey.class).filter("name", cpk.name), ds.createUpdateOperations(TestUpdateOps.ContainsPicKey.class).set("pic", picKey), new UpdateOptions());
        // test reading the object.
        final TestUpdateOps.ContainsPicKey cpk3 = ds.find(TestUpdateOps.ContainsPicKey.class).find(new FindOptions().limit(1)).next();
        Assert.assertThat(cpk3, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(cpk.name, CoreMatchers.is(cpk3.name));
        Assert.assertThat(cpk3.pic, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(picKey, CoreMatchers.is(cpk3.pic));
    }

    @Test
    public void testUpdateKeyList() {
        final TestUpdateOps.ContainsPicKey cpk = new TestUpdateOps.ContainsPicKey();
        cpk.name = "cpk one";
        Datastore ds = getDs();
        ds.save(cpk);
        final TestQuery.Pic pic = new TestQuery.Pic();
        pic.setName("fist again");
        final Key<TestQuery.Pic> picKey = ds.save(pic);
        cpk.keys = Collections.singletonList(picKey);
        // test with Key<Pic>
        final UpdateResults res = ds.update(ds.find(TestUpdateOps.ContainsPicKey.class).filter("name", cpk.name), ds.createUpdateOperations(TestUpdateOps.ContainsPicKey.class).set("keys", cpk.keys), new UpdateOptions());
        Assert.assertThat(res.getUpdatedCount(), CoreMatchers.is(1));
        // test reading the object.
        final TestUpdateOps.ContainsPicKey cpk2 = ds.find(TestUpdateOps.ContainsPicKey.class).find(new FindOptions().limit(1)).next();
        Assert.assertThat(cpk2, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(cpk.name, CoreMatchers.is(cpk2.name));
        Assert.assertThat(cpk2.keys, Matchers.hasItem(picKey));
    }

    @Test
    public void testUpdateRef() {
        final TestQuery.ContainsPic cp = new TestQuery.ContainsPic();
        cp.setName("cp one");
        getDs().save(cp);
        final TestQuery.Pic pic = new TestQuery.Pic();
        pic.setName("fist");
        final Key<TestQuery.Pic> picKey = getDs().save(pic);
        // test with Key<Pic>
        Assert.assertThat(getDs().update(getDs().find(TestQuery.ContainsPic.class).filter("name", cp.getName()), getDs().createUpdateOperations(TestQuery.ContainsPic.class).set("pic", pic), new UpdateOptions()).getUpdatedCount(), CoreMatchers.is(1));
        // test reading the object.
        final TestQuery.ContainsPic cp2 = getDs().find(TestQuery.ContainsPic.class).find(new FindOptions().limit(1)).next();
        Assert.assertThat(cp2, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(cp.getName(), CoreMatchers.is(cp2.getName()));
        Assert.assertThat(cp2.getPic(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(cp2.getPic().getName(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(pic.getName(), CoreMatchers.is(cp2.getPic().getName()));
        getDs().update(getDs().find(TestQuery.ContainsPic.class).filter("name", cp.getName()), getDs().createUpdateOperations(TestQuery.ContainsPic.class).set("pic", picKey), new UpdateOptions());
        // test reading the object.
        final TestQuery.ContainsPic cp3 = getDs().find(TestQuery.ContainsPic.class).find(new FindOptions().limit(1)).next();
        Assert.assertThat(cp3, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(cp.getName(), CoreMatchers.is(cp3.getName()));
        Assert.assertThat(cp3.getPic(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(cp3.getPic().getName(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(pic.getName(), CoreMatchers.is(cp3.getPic().getName()));
    }

    @Test
    public void testUpdateWithDifferentType() {
        final TestUpdateOps.ContainsInt cInt = new TestUpdateOps.ContainsInt();
        cInt.val = 21;
        getDs().save(cInt);
        final UpdateResults res = getDs().update(getDs().find(TestUpdateOps.ContainsInt.class), getDs().createUpdateOperations(TestUpdateOps.ContainsInt.class).inc("val", 1.1), new UpdateOptions());
        assertUpdated(res, 1);
        Assert.assertThat(getDs().find(TestUpdateOps.ContainsInt.class).find(new FindOptions().limit(1)).next().val, CoreMatchers.is(22));
    }

    @Test(expected = ValidationException.class)
    public void testValidationBadFieldName() {
        getDs().update(getDs().find(Circle.class).field("radius").equal(0), getDs().createUpdateOperations(Circle.class).inc("r", 1.0));
    }

    @Test
    public void isolated() {
        Assume.assumeTrue(serverIsAtMostVersion(3.6));
        UpdateOperations<Circle> updates = getDs().createUpdateOperations(Circle.class).inc("radius", 1.0);
        Assert.assertFalse(updates.isIsolated());
        updates.isolated();
        Assert.assertTrue(updates.isIsolated());
        getDs().update(getDs().find(Circle.class).field("radius").equal(0), updates, new UpdateOptions().upsert(true).writeConcern(ACKNOWLEDGED));
    }

    private static class ContainsIntArray {
        private final Integer[] values = new Integer[]{ 1, 2, 3 };

        @Id
        private ObjectId id;
    }

    private static class ContainsInt {
        @Id
        private ObjectId id;

        private int val;
    }

    @Entity
    private static class ContainsPicKey {
        @Id
        private ObjectId id;

        private String name = "test";

        private Key<TestQuery.Pic> pic;

        private List<Key<TestQuery.Pic>> keys;
    }

    @Entity(noClassnameStored = true)
    public static class EntityLogs {
        @Id
        private ObjectId id;

        @Indexed
        private String uuid;

        @Embedded
        private List<TestUpdateOps.EntityLog> logs = new ArrayList<TestUpdateOps.EntityLog>();

        private DBObject raw;

        @PreLoad
        public void preload(final DBObject raw) {
            this.raw = raw;
        }
    }

    @Embedded
    public static class EntityLog {
        private Date receivedTs;

        private String value;

        public EntityLog() {
        }

        EntityLog(final String value, final Date date) {
            this.value = value;
            receivedTs = date;
        }

        @Override
        public int hashCode() {
            int result = ((receivedTs) != null) ? receivedTs.hashCode() : 0;
            result = (31 * result) + ((value) != null ? value.hashCode() : 0);
            return result;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof TestUpdateOps.EntityLog)) {
                return false;
            }
            final TestUpdateOps.EntityLog entityLog = ((TestUpdateOps.EntityLog) (o));
            return (receivedTs) != null ? receivedTs.equals(entityLog.receivedTs) : ((entityLog.receivedTs) == null) && ((value) != null ? value.equals(entityLog.value) : (entityLog.value) == null);
        }

        @Override
        public String toString() {
            return String.format("EntityLog{receivedTs=%s, value='%s'}", receivedTs, value);
        }
    }

    private static final class Parent {
        @Embedded
        private final Set<TestUpdateOps.Child> children = new HashSet<TestUpdateOps.Child>();

        @Id
        private ObjectId id;
    }

    private static final class Child {
        private String first;

        private String last;

        private Child(final String first, final String last) {
            this.first = first;
            this.last = last;
        }

        private Child() {
        }

        @Override
        public int hashCode() {
            int result = ((first) != null) ? first.hashCode() : 0;
            result = (31 * result) + ((last) != null ? last.hashCode() : 0);
            return result;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            TestUpdateOps.Child child = ((TestUpdateOps.Child) (o));
            return (first) != null ? first.equals(child.first) : ((child.first) == null) && ((last) != null ? last.equals(child.last) : (child.last) == null);
        }
    }

    private static final class DumbColl {
        private String opaqueId;

        private List<TestUpdateOps.DumbArrayElement> fromArray;

        private DumbColl() {
        }

        private DumbColl(final String opaqueId) {
            this.opaqueId = opaqueId;
        }
    }

    private static final class DumbArrayElement {
        private String whereId;

        private DumbArrayElement(final String whereId) {
            this.whereId = whereId;
        }
    }
}

