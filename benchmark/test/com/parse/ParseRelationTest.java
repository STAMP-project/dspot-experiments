/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import JSONCompareMode.NON_EXTENSIBLE;
import ParseQuery.RelationConstraint;
import ParseQuery.State;
import ParseRelation.CREATOR;
import android.os.Parcel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// endregion
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseRelationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // region testConstructor
    @Test
    public void testConstructorWithParentAndKey() {
        ParseObject parent = Mockito.mock(ParseObject.class);
        String key = "test";
        ParseRelation relation = new ParseRelation(parent, key);
        Assert.assertEquals(key, relation.getKey());
        Assert.assertSame(parent, relation.getParent());
        Assert.assertNull(relation.getTargetClass());
    }

    @Test
    public void testConstructorWithTargetClass() {
        String targetClass = "Test";
        ParseRelation relation = new ParseRelation(targetClass);
        Assert.assertNull(relation.getKey());
        Assert.assertNull(relation.getParent());
        Assert.assertEquals(targetClass, relation.getTargetClass());
    }

    @Test
    public void testConstructorWithJSONAndDecoder() throws Exception {
        // Make ParseRelation JSONArray
        ParseObject object = Mockito.mock(ParseObject.class);
        Mockito.when(object.getClassName()).thenReturn("Test");
        Mockito.when(object.getObjectId()).thenReturn("objectId");
        object.setObjectId("objectId");
        JSONArray objectJSONArray = new JSONArray();
        objectJSONArray.put(PointerEncoder.get().encode(object));
        JSONObject relationJSON = new JSONObject();
        relationJSON.put("className", "Test");
        relationJSON.put("objects", objectJSONArray);
        ParseRelation relationFromJSON = new ParseRelation(relationJSON, ParseDecoder.get());
        Assert.assertEquals("Test", relationFromJSON.getTargetClass());
        Assert.assertEquals(1, relationFromJSON.getKnownObjects().size());
        Object[] objects = relationFromJSON.getKnownObjects().toArray();
        Assert.assertEquals("objectId", getObjectId());
    }

    // endregion
    // region testParcelable
    @Test
    public void testParcelable() {
        ParseFieldOperations.registerDefaultDecoders();
        ParseRelation<ParseObject> relation = new ParseRelation("Test");
        ParseObject parent = new ParseObject("Parent");
        parent.setObjectId("parentId");
        relation.ensureParentAndKey(parent, "key");
        ParseObject inner = new ParseObject("Test");
        inner.setObjectId("innerId");
        relation.add(inner);
        Parcel parcel = Parcel.obtain();
        relation.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        // noinspection unchecked
        ParseRelation<ParseObject> newRelation = CREATOR.createFromParcel(parcel);
        Assert.assertEquals(newRelation.getTargetClass(), "Test");
        Assert.assertEquals(newRelation.getKey(), "key");
        Assert.assertEquals(newRelation.getParent().getClassName(), "Parent");
        Assert.assertEquals(getObjectId(), "parentId");
        Assert.assertEquals(newRelation.getKnownObjects().size(), 1);
        // This would fail assertTrue(newRelation.hasKnownObject(inner)).
        // That is because ParseRelation uses == to check for known objects.
    }

    // endregion
    // region testEnsureParentAndKey
    @Test
    public void testEnsureParentAndKey() {
        ParseRelation relation = new ParseRelation("Test");
        ParseObject parent = Mockito.mock(ParseObject.class);
        relation.ensureParentAndKey(parent, "key");
        Assert.assertEquals(parent, relation.getParent());
        Assert.assertEquals("key", relation.getKey());
    }

    @Test
    public void testEnsureParentAndKeyWithDifferentParent() {
        ParseRelation relation = new ParseRelation(Mockito.mock(ParseObject.class), "key");
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Internal error. One ParseRelation retrieved from two different ParseObjects.");
        relation.ensureParentAndKey(new ParseObject("Parent"), "key");
    }

    @Test
    public void testEnsureParentAndKeyWithDifferentKey() {
        ParseObject parent = Mockito.mock(ParseObject.class);
        ParseRelation relation = new ParseRelation(parent, "key");
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Internal error. One ParseRelation retrieved from two different keys.");
        relation.ensureParentAndKey(parent, "keyAgain");
    }

    // endregion
    // region testAdd
    @Test
    public void testAdd() {
        ParseObject parent = new ParseObject("Parent");
        ParseRelation relation = new ParseRelation(parent, "key");
        ParseObject object = new ParseObject("Test");
        relation.add(object);
        // Make sure targetClass is updated
        Assert.assertEquals("Test", relation.getTargetClass());
        // Make sure object is added to knownObjects
        Assert.assertTrue(relation.hasKnownObject(object));
        // Make sure parent is updated
        ParseRelation relationInParent = parent.getRelation("key");
        Assert.assertEquals("Test", relationInParent.getTargetClass());
        Assert.assertTrue(relationInParent.hasKnownObject(object));
    }

    // endregion
    // region testRemove
    @Test
    public void testRemove() {
        ParseObject parent = new ParseObject("Parent");
        ParseRelation relation = new ParseRelation(parent, "key");
        ParseObject object = new ParseObject("Test");
        relation.add(object);
        relation.remove(object);
        // Make sure targetClass does not change
        Assert.assertEquals("Test", relation.getTargetClass());
        // Make sure object is removed from knownObjects
        Assert.assertFalse(relation.hasKnownObject(object));
        // Make sure parent is updated
        ParseRelation relationInParent = parent.getRelation("key");
        Assert.assertEquals("Test", relationInParent.getTargetClass());
        Assert.assertFalse(relation.hasKnownObject(object));
    }

    // endregion
    // region testGetQuery
    @Test
    public void testGetQueryWithNoTargetClass() {
        ParseObject parent = new ParseObject("Parent");
        ParseRelation relation = new ParseRelation(parent, "key");
        ParseQuery query = relation.getQuery();
        // Make sure className is correct
        Assert.assertEquals("Parent", query.getClassName());
        ParseQuery.State state = query.getBuilder().build();
        // Make sure redirectClassNameForKey is set
        Assert.assertEquals("key", state.extraOptions().get("redirectClassNameForKey"));
        // Make sure where condition is set
        ParseQuery.RelationConstraint relationConstraint = ((ParseQuery.RelationConstraint) (state.constraints().get("$relatedTo")));
        Assert.assertEquals("key", relationConstraint.getKey());
        Assert.assertSame(parent, relationConstraint.getObject());
    }

    @Test
    public void testGetQueryWithTargetClass() {
        ParseObject parent = new ParseObject("Parent");
        ParseRelation relation = new ParseRelation(parent, "key");
        relation.setTargetClass("targetClass");
        ParseQuery query = relation.getQuery();
        // Make sure className is correct
        Assert.assertEquals("targetClass", query.getClassName());
        ParseQuery.State state = query.getBuilder().build();
        // Make sure where condition is set
        ParseQuery.RelationConstraint relationConstraint = ((ParseQuery.RelationConstraint) (state.constraints().get("$relatedTo")));
        Assert.assertEquals("key", relationConstraint.getKey());
        Assert.assertSame(parent, relationConstraint.getObject());
    }

    // endregion
    // region testToJSON
    @Test
    public void testEncodeToJSON() throws Exception {
        ParseObject parent = new ParseObject("Parent");
        ParseRelation relation = new ParseRelation(parent, "key");
        relation.setTargetClass("Test");
        ParseObject object = new ParseObject("Test");
        object.setObjectId("objectId");
        relation.addKnownObject(object);
        JSONObject json = relation.encodeToJSON(PointerEncoder.get());
        Assert.assertEquals("Relation", json.getString("__type"));
        Assert.assertEquals("Test", json.getString("className"));
        JSONArray knownObjectsArray = json.getJSONArray("objects");
        Assert.assertEquals(((JSONObject) (PointerEncoder.get().encode(object))), knownObjectsArray.getJSONObject(0), NON_EXTENSIBLE);
    }
}

