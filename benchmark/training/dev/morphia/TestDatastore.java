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


import CollationStrength.SECONDARY;
import WriteConcern.UNACKNOWLEDGED;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.client.model.Collation;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.EntityListeners;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import dev.morphia.annotations.PostPersist;
import dev.morphia.annotations.PreLoad;
import dev.morphia.annotations.PrePersist;
import dev.morphia.annotations.Reference;
import dev.morphia.annotations.Transient;
import dev.morphia.generics.model.ChildEmbedded;
import dev.morphia.generics.model.ChildEntity;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.UpdateException;
import dev.morphia.query.UpdateOperations;
import dev.morphia.query.UpdateResults;
import dev.morphia.testmodel.Address;
import dev.morphia.testmodel.Hotel;
import dev.morphia.testmodel.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import static dev.morphia.testmodel.Hotel.Type.LEISURE;


/**
 *
 *
 * @author Scott Hernandez
 */
// @RunWith(Parameterized.class)
public class TestDatastore extends TestBase {
    @Test(expected = UpdateException.class)
    public void saveNull() {
        getDs().save(((Hotel) (null)));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void saveVarargs() {
        Iterable<Key<TestDatastore.FacebookUser>> keys = getDs().save(new TestDatastore.FacebookUser(1, "user 1"), new TestDatastore.FacebookUser(2, "user 2"), new TestDatastore.FacebookUser(3, "user 3"), new TestDatastore.FacebookUser(4, "user 4"));
        long id = 1;
        for (final Key<TestDatastore.FacebookUser> key : keys) {
            Assert.assertEquals((id++), key.getId());
        }
        Assert.assertEquals(5, id);
        Assert.assertEquals(4, getDs().getCount(TestDatastore.FacebookUser.class));
    }

    @Test
    public void shouldSaveGenericTypeVariables() throws Exception {
        // given
        ChildEntity child = new ChildEntity();
        child.setEmbeddedList(Collections.singletonList(new ChildEmbedded()));
        // when
        Key<ChildEntity> saveResult = getDs().save(child);
        // then
        Assert.assertNotEquals(null, saveResult);
    }

    @Test
    public void testCollectionNames() {
        Assert.assertEquals("facebook_users", getMorphia().getMapper().getCollectionName(TestDatastore.FacebookUser.class));
    }

    @Test
    public void testDoesNotExistAfterDelete() {
        // given
        long id = System.currentTimeMillis();
        final Key<TestDatastore.FacebookUser> key = getDs().save(new TestDatastore.FacebookUser(id, "user 1"));
        // when
        getDs().delete(getDs().find(TestDatastore.FacebookUser.class));
        // then
        Assert.assertNull("Shouldn't exist after delete", getDs().exists(key));
    }

    @Test
    public void testEmbedded() {
        getDs().delete(getDs().find(Hotel.class));
        final Hotel borg = new Hotel();
        borg.setName("Hotel Borg");
        borg.setStars(4);
        borg.setTakesCreditCards(true);
        borg.setStartDate(new Date());
        borg.setType(LEISURE);
        final Address address = new Address();
        address.setStreet("Posthusstraeti 11");
        address.setPostCode("101");
        borg.setAddress(address);
        getDs().save(borg);
        Assert.assertEquals(1, getDs().getCount(Hotel.class));
        Assert.assertNotNull(borg.getId());
        final Hotel hotelLoaded = getDs().get(Hotel.class, borg.getId());
        Assert.assertEquals(borg.getName(), hotelLoaded.getName());
        Assert.assertEquals(borg.getAddress().getPostCode(), hotelLoaded.getAddress().getPostCode());
    }

    @Test
    public void testExistsWhenItemSaved() {
        // given
        long id = System.currentTimeMillis();
        final Key<TestDatastore.FacebookUser> key = getDs().save(new TestDatastore.FacebookUser(id, "user 1"));
        // expect
        Assert.assertNotNull(getDs().get(TestDatastore.FacebookUser.class, id));
        Assert.assertNotNull(getDs().exists(key));
    }

    @Test
    public void testExistsWithEntity() {
        final TestDatastore.FacebookUser facebookUser = new TestDatastore.FacebookUser(1, "user one");
        getDs().save(facebookUser);
        Assert.assertEquals(1, getDs().getCount(TestDatastore.FacebookUser.class));
        Assert.assertNotNull(getDs().get(TestDatastore.FacebookUser.class, 1));
        Assert.assertNotNull(getDs().exists(facebookUser));
        getDs().delete(getDs().find(TestDatastore.FacebookUser.class));
        Assert.assertEquals(0, getDs().getCount(TestDatastore.FacebookUser.class));
        Assert.assertNull(getDs().exists(facebookUser));
    }

    @Test
    public void testGet() {
        getMorphia().map(TestDatastore.FacebookUser.class);
        List<TestDatastore.FacebookUser> fbUsers = new ArrayList<TestDatastore.FacebookUser>();
        fbUsers.add(new TestDatastore.FacebookUser(1, "user 1"));
        fbUsers.add(new TestDatastore.FacebookUser(2, "user 2"));
        fbUsers.add(new TestDatastore.FacebookUser(3, "user 3"));
        fbUsers.add(new TestDatastore.FacebookUser(4, "user 4"));
        getDs().save(fbUsers);
        Assert.assertEquals(4, getDs().getCount(TestDatastore.FacebookUser.class));
        Assert.assertNotNull(getDs().get(TestDatastore.FacebookUser.class, 1));
        List<TestDatastore.FacebookUser> res = TestBase.toList(getDs().get(TestDatastore.FacebookUser.class, Arrays.asList(1L, 2L)).find());
        Assert.assertEquals(2, res.size());
        Assert.assertNotNull(res.get(0));
        Assert.assertNotNull(res.get(0).id);
        Assert.assertNotNull(res.get(1));
        Assert.assertNotNull(res.get(1).username);
        getDs().getCollection(TestDatastore.FacebookUser.class).remove(new BasicDBObject());
        getAds().insert(fbUsers);
        Assert.assertEquals(4, getDs().getCount(TestDatastore.FacebookUser.class));
        Assert.assertNotNull(getDs().get(TestDatastore.FacebookUser.class, 1));
        res = TestBase.toList(getDs().get(TestDatastore.FacebookUser.class, Arrays.asList(1L, 2L)).find());
        Assert.assertEquals(2, res.size());
        Assert.assertNotNull(res.get(0));
        Assert.assertNotNull(res.get(0).id);
        Assert.assertNotNull(res.get(1));
        Assert.assertNotNull(res.get(1).username);
    }

    @Test
    public void testIdUpdatedOnSave() {
        final Rectangle rect = new Rectangle(10, 10);
        getDs().save(rect);
        Assert.assertNotNull(rect.getId());
    }

    @Test
    public void testLifecycle() {
        final TestDatastore.LifecycleTestObj life1 = new TestDatastore.LifecycleTestObj();
        getMorphia().getMapper().addMappedClass(TestDatastore.LifecycleTestObj.class);
        getDs().save(life1);
        Assert.assertTrue(life1.prePersist);
        Assert.assertTrue(life1.prePersistWithParam);
        Assert.assertTrue(life1.prePersistWithParamAndReturn);
        Assert.assertTrue(life1.postPersist);
        Assert.assertTrue(life1.postPersistWithParam);
        final TestDatastore.LifecycleTestObj loaded = getDs().get(life1);
        Assert.assertTrue(loaded.preLoad);
        Assert.assertTrue(loaded.preLoadWithParam);
        Assert.assertTrue(loaded.preLoadWithParamAndReturn);
        Assert.assertTrue(loaded.postLoad);
        Assert.assertTrue(loaded.postLoadWithParam);
    }

    @Test
    public void testLifecycleListeners() {
        final TestDatastore.LifecycleTestObj life1 = new TestDatastore.LifecycleTestObj();
        getMorphia().getMapper().addMappedClass(TestDatastore.LifecycleTestObj.class);
        getDs().save(life1);
        Assert.assertTrue(TestDatastore.LifecycleListener.prePersist);
        Assert.assertTrue(TestDatastore.LifecycleListener.prePersistWithEntity);
    }

    @Test
    public void testMorphiaDS() {
        new Morphia().createDatastore(getMongoClient(), "test");
    }

    @Test
    public void testMultipleDatabasesSingleThreaded() {
        getMorphia().map(TestDatastore.FacebookUser.class);
        final Datastore ds1 = getMorphia().createDatastore(getMongoClient(), "db1");
        final Datastore ds2 = getMorphia().createDatastore(getMongoClient(), "db2");
        final TestDatastore.FacebookUser db1Friend = new TestDatastore.FacebookUser(3, "DB1 FaceBook Friend");
        ds1.save(db1Friend);
        final TestDatastore.FacebookUser db1User = new TestDatastore.FacebookUser(1, "DB1 FaceBook User");
        db1User.friends.add(db1Friend);
        ds1.save(db1User);
        final TestDatastore.FacebookUser db2Friend = new TestDatastore.FacebookUser(4, "DB2 FaceBook Friend");
        ds2.save(db2Friend);
        final TestDatastore.FacebookUser db2User = new TestDatastore.FacebookUser(2, "DB2 FaceBook User");
        db2User.friends.add(db2Friend);
        ds2.save(db2User);
        testFirstDatastore(ds1);
        testSecondDatastore(ds2);
        testFirstDatastore(ds1);
        testSecondDatastore(ds2);
        testFirstDatastore(ds1);
        testSecondDatastore(ds2);
        testFirstDatastore(ds1);
        testSecondDatastore(ds2);
        testStandardDatastore();
    }

    @Test
    public void testSaveAndDelete() {
        getDs().getCollection(Rectangle.class).drop();
        final Rectangle rect = new Rectangle(10, 10);
        ObjectId id = new ObjectId();
        rect.setId(id);
        // test delete(entity)
        getDs().save(rect);
        Assert.assertEquals(1, getDs().getCount(rect));
        getDs().delete(rect);
        Assert.assertEquals(0, getDs().getCount(rect));
        // test delete(entity, id)
        getDs().save(rect);
        Assert.assertEquals(1, getDs().getCount(rect));
        getDs().delete(rect.getClass(), 1);
        Assert.assertEquals(1, getDs().getCount(rect));
        getDs().delete(rect.getClass(), id);
        Assert.assertEquals(0, getDs().getCount(rect));
        // test delete(entity, {id})
        getDs().save(rect);
        Assert.assertEquals(1, getDs().getCount(rect));
        getDs().delete(rect.getClass(), Collections.singletonList(rect.getId()));
        Assert.assertEquals(0, getDs().getCount(rect));
        // test delete(entity, {id,id})
        ObjectId id1 = ((ObjectId) (getDs().save(new Rectangle(10, 10)).getId()));
        ObjectId id2 = ((ObjectId) (getDs().save(new Rectangle(10, 10)).getId()));
        Assert.assertEquals(2, getDs().getCount(rect));
        getDs().delete(rect.getClass(), Arrays.asList(id1, id2));
        Assert.assertEquals(0, getDs().getCount(rect));
        // test delete(Class, {id,id})
        id1 = ((ObjectId) (getDs().save(new Rectangle(20, 20)).getId()));
        id2 = ((ObjectId) (getDs().save(new Rectangle(20, 20)).getId()));
        Assert.assertEquals("datastore should have saved two entities with autogenerated ids", 2, getDs().getCount(rect));
        getDs().delete(rect.getClass(), Arrays.asList(id1, id2));
        Assert.assertEquals("datastore should have deleted two entities with autogenerated ids", 0, getDs().getCount(rect));
        // test delete(entity, {id}) with one left
        id1 = ((ObjectId) (getDs().save(new Rectangle(20, 20)).getId()));
        getDs().save(new Rectangle(20, 20));
        Assert.assertEquals(2, getDs().getCount(rect));
        getDs().delete(rect.getClass(), Collections.singletonList(id1));
        Assert.assertEquals(1, getDs().getCount(rect));
        getDs().getCollection(Rectangle.class).drop();
        // test delete(Class, {id}) with one left
        id1 = ((ObjectId) (getDs().save(new Rectangle(20, 20)).getId()));
        getDs().save(new Rectangle(20, 20));
        Assert.assertEquals(2, getDs().getCount(rect));
        getDs().delete(Rectangle.class, Collections.singletonList(id1));
        Assert.assertEquals(1, getDs().getCount(rect));
    }

    @Test
    public void testUpdateWithCollation() {
        checkMinServerVersion(3.4);
        getDs().getCollection(TestDatastore.FacebookUser.class).drop();
        getDs().save(Arrays.asList(new TestDatastore.FacebookUser(1, "John Doe"), new TestDatastore.FacebookUser(2, "john doe")));
        Query<TestDatastore.FacebookUser> query = getDs().find(TestDatastore.FacebookUser.class).field("username").equal("john doe");
        UpdateOperations<TestDatastore.FacebookUser> updateOperations = getDs().createUpdateOperations(TestDatastore.FacebookUser.class).inc("loginCount");
        UpdateResults results = getDs().update(query, updateOperations);
        Assert.assertEquals(1, results.getUpdatedCount());
        Assert.assertEquals(0, getDs().find(TestDatastore.FacebookUser.class).filter("id", 1).find(new FindOptions().limit(1)).next().loginCount);
        Assert.assertEquals(1, getDs().find(TestDatastore.FacebookUser.class).filter("id", 2).find(new FindOptions().limit(1)).next().loginCount);
        results = getDs().update(query, updateOperations, new UpdateOptions().multi(true).collation(Collation.builder().locale("en").collationStrength(SECONDARY).build()));
        Assert.assertEquals(2, results.getUpdatedCount());
        Assert.assertEquals(1, getDs().find(TestDatastore.FacebookUser.class).filter("id", 1).find(new FindOptions().limit(1)).next().loginCount);
        Assert.assertEquals(2, getDs().find(TestDatastore.FacebookUser.class).filter("id", 2).find(new FindOptions().limit(1)).next().loginCount);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testFindAndModifyOld() {
        getDs().getCollection(TestDatastore.FacebookUser.class).drop();
        getDs().save(Arrays.asList(new TestDatastore.FacebookUser(1, "John Doe"), new TestDatastore.FacebookUser(2, "john doe")));
        Query<TestDatastore.FacebookUser> query = getDs().find(TestDatastore.FacebookUser.class).field("username").equal("john doe");
        UpdateOperations<TestDatastore.FacebookUser> updateOperations = getDs().createUpdateOperations(TestDatastore.FacebookUser.class).inc("loginCount");
        TestDatastore.FacebookUser results = getDs().findAndModify(query, updateOperations);
        Assert.assertEquals(0, getDs().find(TestDatastore.FacebookUser.class).filter("id", 1).get().loginCount);
        Assert.assertEquals(1, getDs().find(TestDatastore.FacebookUser.class).filter("id", 2).get().loginCount);
        Assert.assertEquals(1, results.loginCount);
        results = getDs().findAndModify(query, updateOperations, true);
        Assert.assertEquals(0, getDs().find(TestDatastore.FacebookUser.class).filter("id", 1).get().loginCount);
        Assert.assertEquals(2, getDs().find(TestDatastore.FacebookUser.class).filter("id", 2).get().loginCount);
        Assert.assertEquals(1, results.loginCount);
        results = getDs().findAndModify(getDs().find(TestDatastore.FacebookUser.class).field("id").equal(3L).field("username").equal("Jon Snow"), updateOperations, true, true);
        Assert.assertNull(results);
        TestDatastore.FacebookUser user = getDs().find(TestDatastore.FacebookUser.class).filter("id", 3).get();
        Assert.assertEquals(1, user.loginCount);
        Assert.assertEquals("Jon Snow", user.username);
        results = getDs().findAndModify(getDs().find(TestDatastore.FacebookUser.class).field("id").equal(4L).field("username").equal("Ron Swanson"), updateOperations, false, true);
        Assert.assertNotNull(results);
        user = getDs().find(TestDatastore.FacebookUser.class).filter("id", 4).get();
        Assert.assertEquals(1, results.loginCount);
        Assert.assertEquals("Ron Swanson", results.username);
        Assert.assertEquals(1, user.loginCount);
        Assert.assertEquals("Ron Swanson", user.username);
    }

    @Test
    public void testFindAndModify() {
        getDs().getCollection(TestDatastore.FacebookUser.class).drop();
        getDs().save(Arrays.asList(new TestDatastore.FacebookUser(1, "John Doe"), new TestDatastore.FacebookUser(2, "john doe")));
        Query<TestDatastore.FacebookUser> query = getDs().find(TestDatastore.FacebookUser.class).field("username").equal("john doe");
        UpdateOperations<TestDatastore.FacebookUser> updateOperations = getDs().createUpdateOperations(TestDatastore.FacebookUser.class).inc("loginCount");
        TestDatastore.FacebookUser results = getDs().findAndModify(query, updateOperations);
        Assert.assertEquals(0, getDs().find(TestDatastore.FacebookUser.class).filter("id", 1).find(new FindOptions().limit(1)).next().loginCount);
        Assert.assertEquals(1, getDs().find(TestDatastore.FacebookUser.class).filter("id", 2).find(new FindOptions().limit(1)).next().loginCount);
        Assert.assertEquals(1, results.loginCount);
        results = getDs().findAndModify(query, updateOperations, new FindAndModifyOptions().returnNew(false));
        Assert.assertEquals(0, getDs().find(TestDatastore.FacebookUser.class).filter("id", 1).find(new FindOptions().limit(1)).next().loginCount);
        Assert.assertEquals(2, getDs().find(TestDatastore.FacebookUser.class).filter("id", 2).find(new FindOptions().limit(1)).next().loginCount);
        Assert.assertEquals(1, results.loginCount);
        results = getDs().findAndModify(getDs().find(TestDatastore.FacebookUser.class).field("id").equal(3L).field("username").equal("Jon Snow"), updateOperations, new FindAndModifyOptions().returnNew(false).upsert(true));
        Assert.assertNull(results);
        TestDatastore.FacebookUser user = getDs().find(TestDatastore.FacebookUser.class).filter("id", 3).find(new FindOptions().limit(1)).next();
        Assert.assertEquals(1, user.loginCount);
        Assert.assertEquals("Jon Snow", user.username);
        results = getDs().findAndModify(getDs().find(TestDatastore.FacebookUser.class).field("id").equal(4L).field("username").equal("Ron Swanson"), updateOperations, new FindAndModifyOptions().returnNew(true).upsert(true));
        Assert.assertNotNull(results);
        user = getDs().find(TestDatastore.FacebookUser.class).filter("id", 4).find(new FindOptions().limit(1)).next();
        Assert.assertEquals(1, results.loginCount);
        Assert.assertEquals("Ron Swanson", results.username);
        Assert.assertEquals(1, user.loginCount);
        Assert.assertEquals("Ron Swanson", user.username);
    }

    @Test
    public void testFindAndModifyWithOptions() {
        checkMinServerVersion(3.4);
        getDs().getCollection(TestDatastore.FacebookUser.class).drop();
        getDs().save(Arrays.asList(new TestDatastore.FacebookUser(1, "John Doe"), new TestDatastore.FacebookUser(2, "john doe")));
        Query<TestDatastore.FacebookUser> query = getDs().find(TestDatastore.FacebookUser.class).field("username").equal("john doe");
        UpdateOperations<TestDatastore.FacebookUser> updateOperations = getDs().createUpdateOperations(TestDatastore.FacebookUser.class).inc("loginCount");
        TestDatastore.FacebookUser results = getDs().findAndModify(query, updateOperations, new FindAndModifyOptions());
        Assert.assertEquals(0, getDs().find(TestDatastore.FacebookUser.class).filter("id", 1).find(new FindOptions().limit(1)).next().loginCount);
        Assert.assertEquals(1, getDs().find(TestDatastore.FacebookUser.class).filter("id", 2).find(new FindOptions().limit(1)).next().loginCount);
        Assert.assertEquals(1, results.loginCount);
        results = getDs().findAndModify(query, updateOperations, new FindAndModifyOptions().returnNew(false).collation(Collation.builder().locale("en").collationStrength(SECONDARY).build()));
        Assert.assertEquals(1, getDs().find(TestDatastore.FacebookUser.class).filter("id", 1).find(new FindOptions().limit(1)).next().loginCount);
        Assert.assertEquals(0, results.loginCount);
        Assert.assertEquals(1, getDs().find(TestDatastore.FacebookUser.class).filter("id", 2).find(new FindOptions().limit(1)).next().loginCount);
        results = getDs().findAndModify(getDs().find(TestDatastore.FacebookUser.class).field("id").equal(3L).field("username").equal("Jon Snow"), updateOperations, new FindAndModifyOptions().returnNew(false).upsert(true));
        Assert.assertNull(results);
        TestDatastore.FacebookUser user = getDs().find(TestDatastore.FacebookUser.class).filter("id", 3).find(new FindOptions().limit(1)).next();
        Assert.assertEquals(1, user.loginCount);
        Assert.assertEquals("Jon Snow", user.username);
        results = getDs().findAndModify(getDs().find(TestDatastore.FacebookUser.class).field("id").equal(4L).field("username").equal("Ron Swanson"), updateOperations, new FindAndModifyOptions().upsert(true));
        Assert.assertNotNull(results);
        user = getDs().find(TestDatastore.FacebookUser.class).filter("id", 4).find(new FindOptions().limit(1)).next();
        Assert.assertEquals(1, results.loginCount);
        Assert.assertEquals("Ron Swanson", results.username);
        Assert.assertEquals(1, user.loginCount);
        Assert.assertEquals("Ron Swanson", user.username);
    }

    @Test
    public void testDeleteWithCollation() {
        checkMinServerVersion(3.4);
        getDs().getCollection(TestDatastore.FacebookUser.class).drop();
        getDs().save(Arrays.asList(new TestDatastore.FacebookUser(1, "John Doe"), new TestDatastore.FacebookUser(2, "john doe")));
        Query<TestDatastore.FacebookUser> query = getDs().find(TestDatastore.FacebookUser.class).field("username").equal("john doe");
        Assert.assertEquals(1, getDs().delete(query).getN());
        Assert.assertEquals(1, getDs().delete(query, new DeleteOptions().collation(Collation.builder().locale("en").collationStrength(SECONDARY).build())).getN());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testEnforceWriteConcern() {
        DatastoreImpl ds = ((DatastoreImpl) (getDs()));
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        Assert.assertNull(findAndModifyOptions.getWriteConcern());
        Assert.assertEquals(WriteConcern.ACKNOWLEDGED, ds.enforceWriteConcern(findAndModifyOptions, TestDatastore.FacebookUser.class).getWriteConcern());
        Assert.assertEquals(WriteConcern.MAJORITY, ds.enforceWriteConcern(findAndModifyOptions.writeConcern(WriteConcern.MAJORITY), TestDatastore.FacebookUser.class).getWriteConcern());
        InsertOptions insertOptions = new InsertOptions();
        Assert.assertNull(insertOptions.getWriteConcern());
        Assert.assertEquals(WriteConcern.ACKNOWLEDGED, ds.enforceWriteConcern(insertOptions, TestDatastore.FacebookUser.class).getWriteConcern());
        Assert.assertEquals(WriteConcern.MAJORITY, ds.enforceWriteConcern(insertOptions.writeConcern(WriteConcern.MAJORITY), TestDatastore.FacebookUser.class).getWriteConcern());
        UpdateOptions updateOptions = new UpdateOptions();
        Assert.assertNull(updateOptions.getWriteConcern());
        Assert.assertEquals(WriteConcern.ACKNOWLEDGED, ds.enforceWriteConcern(updateOptions, TestDatastore.FacebookUser.class).getWriteConcern());
        Assert.assertEquals(WriteConcern.MAJORITY, ds.enforceWriteConcern(updateOptions.writeConcern(WriteConcern.MAJORITY), TestDatastore.FacebookUser.class).getWriteConcern());
        DeleteOptions deleteOptions = new DeleteOptions();
        Assert.assertNull(deleteOptions.getWriteConcern());
        Assert.assertEquals(WriteConcern.ACKNOWLEDGED, ds.enforceWriteConcern(deleteOptions, TestDatastore.FacebookUser.class).getWriteConcern());
        Assert.assertEquals(WriteConcern.MAJORITY, ds.enforceWriteConcern(deleteOptions.writeConcern(WriteConcern.MAJORITY), TestDatastore.FacebookUser.class).getWriteConcern());
    }

    @Test
    public void entityWriteConcern() {
        ensureEntityWriteConcern();
        getDs().setDefaultWriteConcern(UNACKNOWLEDGED);
        ensureEntityWriteConcern();
    }

    @Test
    public void testFindAndDeleteWithCollation() {
        checkMinServerVersion(3.4);
        getDs().getCollection(TestDatastore.FacebookUser.class).drop();
        getDs().save(Arrays.asList(new TestDatastore.FacebookUser(1, "John Doe"), new TestDatastore.FacebookUser(2, "john doe")));
        Query<TestDatastore.FacebookUser> query = getDs().find(TestDatastore.FacebookUser.class).field("username").equal("john doe");
        Assert.assertNotNull(getDs().findAndDelete(query));
        Assert.assertNull(getDs().findAndDelete(query));
        FindAndModifyOptions options = new FindAndModifyOptions().collation(Collation.builder().locale("en").collationStrength(SECONDARY).build());
        Assert.assertNotNull(getDs().findAndDelete(query, options));
        Assert.assertTrue("Options should not be modified by the datastore", options.isReturnNew());
        Assert.assertFalse("Options should not be modified by the datastore", options.isRemove());
    }

    @Test
    public void testFindAndDeleteWithNoQueryMatch() {
        Assert.assertNull(getDs().findAndDelete(getDs().find(TestDatastore.FacebookUser.class).field("username").equal("David S. Pumpkins")));
    }

    @Entity("facebook_users")
    public static class FacebookUser {
        @Id
        private long id;

        private String username;

        private int loginCount;

        @Reference
        private List<TestDatastore.FacebookUser> friends = new ArrayList<TestDatastore.FacebookUser>();

        public FacebookUser(final long id, final String name) {
            this();
            this.id = id;
            username = name;
        }

        public FacebookUser() {
        }

        public long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public int getLoginCount() {
            return loginCount;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class LifecycleListener {
        private static boolean prePersist;

        private static boolean prePersistWithEntity;

        @PrePersist
        void prePersist() {
            TestDatastore.LifecycleListener.prePersist = true;
        }

        @PrePersist
        void prePersist(final TestDatastore.LifecycleTestObj obj) {
            if (obj == null) {
                throw new RuntimeException();
            }
            TestDatastore.LifecycleListener.prePersistWithEntity = true;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @EntityListeners(TestDatastore.LifecycleListener.class)
    public static class LifecycleTestObj {
        @Id
        private ObjectId id;

        @Transient
        private boolean prePersist;

        @Transient
        private boolean postPersist;

        @Transient
        private boolean preLoad;

        @Transient
        private boolean postLoad;

        @Transient
        private boolean postLoadWithParam;

        private boolean prePersistWithParamAndReturn;

        private boolean prePersistWithParam;

        private boolean postPersistWithParam;

        private boolean preLoadWithParamAndReturn;

        private boolean preLoadWithParam;

        @PrePersist
        public DBObject prePersistWithParamAndReturn(final DBObject dbObj) {
            if (prePersistWithParamAndReturn) {
                throw new RuntimeException("already called");
            }
            prePersistWithParamAndReturn = true;
            return null;
        }

        @PrePersist
        protected void prePersistWithParam(final DBObject dbObj) {
            if (prePersistWithParam) {
                throw new RuntimeException("already called");
            }
            prePersistWithParam = true;
        }

        @PostPersist
        private void postPersistPersist() {
            if (postPersist) {
                throw new RuntimeException("already called");
            }
            postPersist = true;
        }

        @PrePersist
        void prePersist() {
            if (prePersist) {
                throw new RuntimeException("already called");
            }
            prePersist = true;
        }

        @PostPersist
        void postPersistWithParam(final DBObject dbObj) {
            postPersistWithParam = true;
            if (!(dbObj.containsField("_id"))) {
                throw new RuntimeException(("missing " + "_id"));
            }
        }

        @PreLoad
        void preLoad() {
            if (preLoad) {
                throw new RuntimeException("already called");
            }
            preLoad = true;
        }

        @PreLoad
        void preLoadWithParam(final DBObject dbObj) {
            dbObj.put("preLoadWithParam", true);
        }

        @PreLoad
        DBObject preLoadWithParamAndReturn(final DBObject dbObj) {
            final BasicDBObject retObj = new BasicDBObject();
            retObj.putAll(dbObj);
            retObj.put("preLoadWithParamAndReturn", true);
            return retObj;
        }

        @PostLoad
        void postLoad() {
            if (postLoad) {
                throw new RuntimeException("already called");
            }
            postLoad = true;
        }

        @PostLoad
        void postLoadWithParam(final DBObject dbObj) {
            if (postLoadWithParam) {
                throw new RuntimeException("already called");
            }
            postLoadWithParam = true;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class KeysKeysKeys {
        @Id
        private ObjectId id;

        private List<Key<TestDatastore.FacebookUser>> users;

        private Key<Rectangle> rect;

        private KeysKeysKeys() {
        }

        public KeysKeysKeys(final Key<Rectangle> rectKey, final List<Key<TestDatastore.FacebookUser>> users) {
            rect = rectKey;
            this.users = users;
        }

        public ObjectId getId() {
            return id;
        }

        public Key<Rectangle> getRect() {
            return rect;
        }

        public List<Key<TestDatastore.FacebookUser>> getUsers() {
            return users;
        }
    }

    @Entity(concern = "ACKNOWLEDGED")
    static class Simple {
        @Id
        private String id;

        Simple(final String id) {
            this();
            this.id = id;
        }

        private Simple() {
        }
    }
}

