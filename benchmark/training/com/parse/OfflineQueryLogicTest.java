/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import JSONObject.NULL;
import ParseException.INVALID_QUERY;
import ParseQuery.State.Builder;
import bolts.Task;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for OfflineQueryLogic.
 */
public class OfflineQueryLogicTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testHasReadAccessWithSameObject() {
        ParseUser user = Mockito.mock(ParseUser.class);
        Assert.assertTrue(OfflineQueryLogic.hasReadAccess(user, user));
        Mockito.verify(user, Mockito.never()).getACL();
    }

    @Test
    public void testHasReadAccessWithNoACL() {
        ParseObject object = Mockito.mock(ParseObject.class);
        Mockito.when(object.getACL()).thenReturn(null);
        Assert.assertTrue(OfflineQueryLogic.hasReadAccess(null, object));
    }

    // endregion
    // region hasWriteAccess
    @Test
    public void testHasReadAccessWithPublicReadAccess() {
        ParseACL acl = Mockito.mock(ParseACL.class);
        Mockito.when(acl.getPublicReadAccess()).thenReturn(true);
        ParseObject object = Mockito.mock(ParseObject.class);
        Mockito.when(object.getACL()).thenReturn(acl);
        Assert.assertTrue(OfflineQueryLogic.hasReadAccess(null, object));
    }

    @Test
    public void testHasReadAccessWithReadAccess() {
        ParseUser user = Mockito.mock(ParseUser.class);
        Mockito.when(user.getObjectId()).thenReturn("test");
        ParseACL acl = Mockito.mock(ParseACL.class);
        Mockito.when(acl.getReadAccess(user)).thenReturn(true);
        ParseObject object = Mockito.mock(ParseObject.class);
        Mockito.when(object.getACL()).thenReturn(acl);
        Assert.assertTrue(OfflineQueryLogic.hasReadAccess(user, object));
    }

    @Test
    public void testHasReadAccessWithNoReadAccess() {
        ParseACL acl = Mockito.mock(ParseACL.class);
        Mockito.when(acl.getPublicReadAccess()).thenReturn(false);
        Mockito.when(acl.getReadAccess(ArgumentMatchers.any(ParseUser.class))).thenReturn(false);
        ParseObject object = Mockito.mock(ParseObject.class);
        Mockito.when(object.getACL()).thenReturn(acl);
        Assert.assertFalse(OfflineQueryLogic.hasReadAccess(null, object));
    }

    @Test
    public void testHasWriteAccessWithSameObject() {
        ParseUser user = Mockito.mock(ParseUser.class);
        Assert.assertTrue(OfflineQueryLogic.hasWriteAccess(user, user));
        Mockito.verify(user, Mockito.never()).getACL();
    }

    @Test
    public void testHasWriteAccessWithNoACL() {
        ParseObject object = Mockito.mock(ParseObject.class);
        Mockito.when(object.getACL()).thenReturn(null);
        Assert.assertTrue(OfflineQueryLogic.hasWriteAccess(null, object));
    }

    // endregion
    // region createMatcher
    @Test
    public void testHasWriteAccessWithPublicWriteAccess() {
        ParseACL acl = Mockito.mock(ParseACL.class);
        Mockito.when(acl.getPublicWriteAccess()).thenReturn(true);
        ParseObject object = Mockito.mock(ParseObject.class);
        Mockito.when(object.getACL()).thenReturn(acl);
        Assert.assertTrue(OfflineQueryLogic.hasWriteAccess(null, object));
    }

    @Test
    public void testHasWriteAccessWithWriteAccess() {
        ParseUser user = Mockito.mock(ParseUser.class);
        ParseACL acl = Mockito.mock(ParseACL.class);
        Mockito.when(acl.getWriteAccess(user)).thenReturn(true);
        ParseObject object = Mockito.mock(ParseObject.class);
        Mockito.when(object.getACL()).thenReturn(acl);
        Assert.assertTrue(OfflineQueryLogic.hasWriteAccess(user, object));
    }

    @Test
    public void testHasWriteAccessWithNoWriteAccess() {
        ParseACL acl = Mockito.mock(ParseACL.class);
        Mockito.when(acl.getPublicReadAccess()).thenReturn(false);
        ParseObject object = Mockito.mock(ParseObject.class);
        Mockito.when(object.getACL()).thenReturn(acl);
        Assert.assertFalse(OfflineQueryLogic.hasWriteAccess(null, object));
    }

    @Test
    public void testMatcherWithNoReadAccess() throws ParseException {
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").build();
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(false);
        ParseObject object = new ParseObject("TestObject");
        object.setACL(acl);
        ParseUser user = Mockito.mock(ParseUser.class);
        Mockito.when(user.getObjectId()).thenReturn("test");
        Assert.assertFalse(matches(logic, query, object, user));
    }

    // TODO(grantland): testRelationMatcher()
    // endregion
    // region matchesEquals
    @Test
    public void testSimpleMatcher() throws ParseException {
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        ParseObject objectA = new ParseObject("TestObject");
        objectA.put("value", "A");
        objectA.put("foo", "bar");
        ParseObject objectB = new ParseObject("TestObject");
        objectB.put("value", "B");
        objectB.put("foo", "bar");
        ParseQuery.State<ParseObject> queryA = new ParseQuery.State.Builder<>("TestObject").whereEqualTo("value", "A").whereEqualTo("foo", "bar").build();
        Assert.assertTrue(matches(logic, queryA, objectA));
        Assert.assertFalse(matches(logic, queryA, objectB));
    }

    @Test
    public void testOrMatcher() throws Exception {
        ParseObject objectA = new ParseObject("TestObject");
        objectA.put("value", "A");
        ParseObject objectB = new ParseObject("TestObject");
        objectB.put("value", "B");
        ParseQuery.State<ParseObject> query = Builder.or(Arrays.asList(new ParseQuery.State.Builder<>("TestObject").whereEqualTo("value", "A"), new ParseQuery.State.Builder<>("TestObject").whereEqualTo("value", "B"))).build();
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        Assert.assertTrue(matches(logic, query, objectA));
        Assert.assertTrue(matches(logic, query, objectB));
    }

    @Test
    public void testAndMatcher() throws Exception {
        ParseObject objectA = new ParseObject("TestObject");
        objectA.put("foo", "bar");
        ParseObject objectB = new ParseObject("TestObject");
        objectB.put("baz", "qux");
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").addCondition("foo", "$ne", "bar").addCondition("baz", "$ne", "qux").build();
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        Assert.assertFalse(matches(logic, query, objectA));
        Assert.assertFalse(matches(logic, query, objectB));
    }

    @Test
    public void testMatchesEqualsWithGeoPoint() throws Exception {
        ParseGeoPoint point = new ParseGeoPoint(37.77493F, (-122.41942F));// SF

        ParseObject object = new ParseObject("TestObject");
        object.put("point", point);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = new ParseQuery.State.Builder<>("TestObject").whereEqualTo("point", point).build();
        Assert.assertTrue(matches(logic, query, object));
        // Test lat
        query = new ParseQuery.State.Builder<>("TestObject").whereEqualTo("point", new ParseGeoPoint(37.77493F, (-74.00594F))).build();
        Assert.assertFalse(matches(logic, query, object));
        // Test lng
        query = new ParseQuery.State.Builder<>("TestObject").whereEqualTo("point", new ParseGeoPoint(40.712784F, (-122.41942F))).build();
        Assert.assertFalse(matches(logic, query, object));
        // Not GeoPoint
        object = new ParseObject("TestObject");
        object.put("point", "A");
        Assert.assertFalse(matches(logic, query, object));
    }

    // endregion
    @Test
    public void testMatchesEqualsWithPolygon() throws Exception {
        List<ParseGeoPoint> points = new ArrayList<>();
        points.add(new ParseGeoPoint(0, 0));
        points.add(new ParseGeoPoint(0, 1));
        points.add(new ParseGeoPoint(1, 1));
        points.add(new ParseGeoPoint(1, 0));
        ParsePolygon polygon = new ParsePolygon(points);
        ParseObject object = new ParseObject("TestObject");
        object.put("polygon", polygon);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = new ParseQuery.State.Builder<>("TestObject").whereEqualTo("polygon", polygon).build();
        Assert.assertTrue(matches(logic, query, object));
        List<ParseGeoPoint> diff = new ArrayList<>();
        diff.add(new ParseGeoPoint(0, 0));
        diff.add(new ParseGeoPoint(0, 10));
        diff.add(new ParseGeoPoint(10, 10));
        diff.add(new ParseGeoPoint(10, 0));
        diff.add(new ParseGeoPoint(0, 0));
        query = new ParseQuery.State.Builder<>("TestObject").whereEqualTo("polygon", new ParsePolygon(diff)).build();
        Assert.assertFalse(matches(logic, query, object));
        // Not Polygon
        object = new ParseObject("TestObject");
        object.put("polygon", "A");
        Assert.assertFalse(matches(logic, query, object));
    }

    @Test
    public void testMatchesEqualsWithNumbers() throws ParseException {
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        ParseQuery.State<ParseObject> iQuery = /* (int) */
        new ParseQuery.State.Builder<>("TestObject").whereEqualTo("value", 5).build();
        ParseObject iObject = new ParseObject("TestObject");
        /* (int) */
        iObject.put("value", 5);
        Assert.assertTrue(matches(logic, iQuery, iObject));
        ParseObject object = new ParseObject("TestObject");
        object.put("value", "string");
        Assert.assertFalse(matches(logic, iQuery, object));
        ParseObject noMatch = new ParseObject("TestObject");
        noMatch.put("value", 6);
        Assert.assertFalse(matches(logic, iQuery, noMatch));
    }

    @Test
    public void testMatchesEqualsNull() throws ParseException {
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        ParseObject object = new ParseObject("TestObject");
        object.put("value", "test");
        ParseObject nullObject = new ParseObject("TestObject");
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").whereEqualTo("value", "test").build();
        ParseQuery.State<ParseObject> nullQuery = new ParseQuery.State.Builder<>("TestObject").whereEqualTo("value", null).build();
        Assert.assertTrue(matches(logic, query, object));
        Assert.assertFalse(matches(logic, query, nullObject));
        Assert.assertFalse(matches(logic, nullQuery, object));
        Assert.assertTrue(matches(logic, nullQuery, nullObject));
    }

    @Test
    public void testMatchesIn() throws ParseException {
        ParseObject object = new ParseObject("TestObject");
        object.put("foo", "bar");
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("foo", "$in", Arrays.asList("bar", "baz")).build();
        Assert.assertTrue(matches(logic, query, object));
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("foo", "$in", Collections.singletonList("qux")).build();
        Assert.assertFalse(matches(logic, query, object));
        // Non-existant key
        object = new ParseObject("TestObject");
        Assert.assertFalse(matches(logic, query, object));
        object.put("foo", NULL);
        Assert.assertFalse(matches(logic, query, object));
    }

    @Test
    public void testMatchesAll() throws Exception {
        ParseObject object = new ParseObject("TestObject");
        object.put("foo", Arrays.asList("foo", "bar"));
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("foo", "$all", Arrays.asList("foo", "bar")).build();
        Assert.assertTrue(matches(logic, query, object));
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("foo", "$all", Arrays.asList("foo", "bar", "qux")).build();
        Assert.assertFalse(matches(logic, query, object));
        // Non-existant key
        object = new ParseObject("TestObject");
        Assert.assertFalse(matches(logic, query, object));
        object.put("foo", NULL);
        Assert.assertFalse(matches(logic, query, object));
        thrown.expect(IllegalArgumentException.class);
        object.put("foo", "bar");
        Assert.assertFalse(matches(logic, query, object));
    }

    @Test
    public void testMatchesAllStartingWith() throws Exception {
        ParseObject object = new ParseObject("TestObject");
        object.put("foo", Arrays.asList("foo", "bar"));
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("foo", "$all", Arrays.asList(buildStartsWithRegexKeyConstraint("foo"), buildStartsWithRegexKeyConstraint("bar"))).build();
        Assert.assertTrue(matches(logic, query, object));
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("foo", "$all", Arrays.asList(buildStartsWithRegexKeyConstraint("fo"), buildStartsWithRegexKeyConstraint("b"))).build();
        Assert.assertTrue(matches(logic, query, object));
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("foo", "$all", Arrays.asList(buildStartsWithRegexKeyConstraint("foo"), buildStartsWithRegexKeyConstraint("bar"), buildStartsWithRegexKeyConstraint("qux"))).build();
        Assert.assertFalse(matches(logic, query, object));
        // Non-existant key
        object = new ParseObject("TestObject");
        Assert.assertFalse(matches(logic, query, object));
        object.put("foo", NULL);
        Assert.assertFalse(matches(logic, query, object));
        thrown.expect(IllegalArgumentException.class);
        object.put("foo", "bar");
        Assert.assertFalse(matches(logic, query, object));
    }

    @Test
    public void testMatchesAllStartingWithParameters() throws Exception {
        ParseObject object = new ParseObject("TestObject");
        object.put("foo", Arrays.asList("foo", "bar"));
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("foo", "$all", Arrays.asList(buildStartsWithRegexKeyConstraint("foo"), buildStartsWithRegexKeyConstraint("bar"))).build();
        Assert.assertTrue(matches(logic, query, object));
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("foo", "$all", Arrays.asList(buildStartsWithRegexKeyConstraint("fo"), buildStartsWithRegex("ba"), "b")).build();
        thrown.expect(IllegalArgumentException.class);
        Assert.assertFalse(matches(logic, query, object));
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("foo", "$all", Arrays.asList(buildStartsWithRegexKeyConstraint("fo"), "b")).build();
        thrown.expect(IllegalArgumentException.class);
        Assert.assertFalse(matches(logic, query, object));
    }

    @Test
    public void testMatchesNearSphere() throws Exception {
        ParseGeoPoint fb = new ParseGeoPoint(37.48169F, (-122.154945F));
        ParseGeoPoint sf = new ParseGeoPoint(37.77493F, (-122.41942F));
        ParseObject object = new ParseObject("TestObject");
        object.put("point", fb);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = new ParseQuery.State.Builder<>("TestObject").whereNear("point", fb).build();
        Assert.assertTrue(matches(logic, query, object));
        query = new ParseQuery.State.Builder<>("TestObject").whereNear("point", sf).maxDistance("point", 0.00628).build();
        Assert.assertFalse(matches(logic, query, object));
        query = new ParseQuery.State.Builder<>("TestObject").whereNear("point", sf).maxDistance("point", 0.00629).build();
        Assert.assertTrue(matches(logic, query, object));
        // Non-existant key
        object = new ParseObject("TestObject");
        Assert.assertFalse(matches(logic, query, object));
    }

    @Test
    public void testMatchesWithinFailureInternationalDateLine() throws ParseException {
        ParseGeoPoint fb = new ParseGeoPoint(37.48169F, (-122.154945F));
        ParseGeoPoint sf = new ParseGeoPoint(37.77493F, (-122.41942F));
        ParseGeoPoint sj = new ParseGeoPoint(37.338207F, (-121.88633F));
        ParseObject object = new ParseObject("TestObject");
        object.put("point", fb);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        thrown.expect(ParseException.class);
        thrown.expect(ParseMatchers.hasParseErrorCode(INVALID_QUERY));
        thrown.expectMessage("whereWithinGeoBox queries cannot cross the International Date Line.");
        query = new ParseQuery.State.Builder<>("TestObject").whereWithin("point", sj, sf).build();
        matches(logic, query, object);
    }

    @Test
    public void testMatchesWithinFailureSwapped() throws Exception {
        ParseGeoPoint fb = new ParseGeoPoint(37.48169F, (-122.154945F));
        ParseGeoPoint sf = new ParseGeoPoint(37.77493F, (-122.41942F));
        ParseGeoPoint sj = new ParseGeoPoint(37.338207F, (-121.88633F));
        ParseObject object = new ParseObject("TestObject");
        object.put("point", fb);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        thrown.expect(ParseException.class);
        thrown.expect(ParseMatchers.hasParseErrorCode(INVALID_QUERY));
        thrown.expectMessage("The southwest corner of a geo box must be south of the northeast corner.");
        query = new ParseQuery.State.Builder<>("TestObject").whereWithin("point", sf, sj).build();
        matches(logic, query, object);
    }

    @Test
    public void testMatchesWithinFailure180() throws Exception {
        ParseGeoPoint fb = new ParseGeoPoint(37.48169F, (-122.154945F));
        ParseGeoPoint sf = new ParseGeoPoint(37.77493F, (-122.41942F));
        ParseGeoPoint beijing = new ParseGeoPoint(39.90421F, 116.407394F);
        ParseObject object = new ParseObject("TestObject");
        object.put("point", fb);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        thrown.expect(ParseException.class);
        thrown.expect(ParseMatchers.hasParseErrorCode(INVALID_QUERY));
        thrown.expectMessage(("Geo box queries larger than 180 degrees in longitude are not supported. " + "Please check point order."));
        query = new ParseQuery.State.Builder<>("TestObject").whereWithin("point", sf, beijing).build();
        matches(logic, query, object);
    }

    // endregion
    // region compare
    @Test
    public void testMatchesWithin() throws ParseException {
        ParseGeoPoint fb = new ParseGeoPoint(37.48169F, (-122.154945F));
        ParseGeoPoint sunset = new ParseGeoPoint(37.74673F, (-122.48635F));
        ParseGeoPoint soma = new ParseGeoPoint(37.77852F, (-122.40564F));
        ParseGeoPoint twinPeaks = new ParseGeoPoint(37.754406F, (-122.447685F));
        ParseObject object = new ParseObject("TestObject");
        object.put("point", fb);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        object.put("point", twinPeaks);
        query = new ParseQuery.State.Builder<>("TestObject").whereWithin("point", sunset, soma).build();
        Assert.assertTrue(matches(logic, query, object));
        object.put("point", fb);
        Assert.assertFalse(matches(logic, query, object));
        // Non-existant key
        object = new ParseObject("TestObject");
        Assert.assertFalse(matches(logic, query, object));
    }

    @Test
    public void testMatchesGeoIntersects() throws ParseException {
        List<ParseGeoPoint> points = new ArrayList<>();
        points.add(new ParseGeoPoint(0, 0));
        points.add(new ParseGeoPoint(0, 1));
        points.add(new ParseGeoPoint(1, 1));
        points.add(new ParseGeoPoint(1, 0));
        ParseGeoPoint inside = new ParseGeoPoint(0.5, 0.5);
        ParseGeoPoint outside = new ParseGeoPoint(10, 10);
        ParsePolygon polygon = new ParsePolygon(points);
        ParseObject object = new ParseObject("TestObject");
        object.put("polygon", polygon);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = new ParseQuery.State.Builder<>("TestObject").whereGeoIntersects("polygon", inside).build();
        Assert.assertTrue(matches(logic, query, object));
        query = new ParseQuery.State.Builder<>("TestObject").whereGeoIntersects("polygon", outside).build();
        Assert.assertFalse(matches(logic, query, object));
        // Non-existant key
        object = new ParseObject("TestObject");
        Assert.assertFalse(matches(logic, query, object));
    }

    // endregion
    // region compareTo
    @Test
    public void testMatchesGeoWithin() throws ParseException {
        List<ParseGeoPoint> smallBox = new ArrayList<>();
        smallBox.add(new ParseGeoPoint(0, 0));
        smallBox.add(new ParseGeoPoint(0, 1));
        smallBox.add(new ParseGeoPoint(1, 1));
        smallBox.add(new ParseGeoPoint(1, 0));
        List<ParseGeoPoint> largeBox = new ArrayList<>();
        largeBox.add(new ParseGeoPoint(0, 0));
        largeBox.add(new ParseGeoPoint(0, 10));
        largeBox.add(new ParseGeoPoint(10, 10));
        largeBox.add(new ParseGeoPoint(10, 0));
        ParseGeoPoint point = new ParseGeoPoint(5, 5);
        // ParsePolygon polygon = new ParsePolygon(points);
        ParseObject object = new ParseObject("TestObject");
        object.put("point", point);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = new ParseQuery.State.Builder<>("TestObject").whereGeoWithin("point", largeBox).build();
        Assert.assertTrue(matches(logic, query, object));
        query = new ParseQuery.State.Builder<>("TestObject").whereGeoWithin("point", smallBox).build();
        Assert.assertFalse(matches(logic, query, object));
        // Non-existant key
        object = new ParseObject("TestObject");
        Assert.assertFalse(matches(logic, query, object));
    }

    @Test
    public void testCompareList() throws Exception {
        ParseObject object = new ParseObject("SomeObject");
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        object.put("list", list);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = new ParseQuery.State.Builder<>("SomeObject").whereEqualTo("list", 2).build();
        Assert.assertTrue(matches(logic, query, object));
        query = new ParseQuery.State.Builder<>("SomeObject").whereEqualTo("list", 4).build();
        Assert.assertFalse(matches(logic, query, object));
    }

    // endregion
    // region Sort
    @Test
    public void testCompareJSONArray() throws Exception {
        ParseObject object = new ParseObject("SomeObject");
        JSONArray array = new JSONArray();
        array.put(1);
        array.put(2);
        array.put(3);
        object.put("array", array);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = new ParseQuery.State.Builder<>("SomeObject").whereEqualTo("array", 2).build();
        Assert.assertTrue(matches(logic, query, object));
        query = new ParseQuery.State.Builder<>("SomeObject").whereEqualTo("array", 4).build();
        Assert.assertFalse(matches(logic, query, object));
    }

    @Test
    public void testCompareToNumber() throws Exception {
        ParseObject object = new ParseObject("TestObject");
        object.put("value", 5);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = /* (int) */
        new ParseQuery.State.Builder<>("TestObject").whereEqualTo("value", 5).build();
        Assert.assertTrue(matches(logic, query, object));
        object.put("value", 6);
        Assert.assertFalse(matches(logic, query, object));
        object.put("value", 5);
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("value", "$lt", 6).build();
        Assert.assertTrue(matches(logic, query, object));
        object.put("value", 6);
        Assert.assertFalse(matches(logic, query, object));
        object.put("value", 5);
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("value", "$gt", 4).build();
        Assert.assertTrue(matches(logic, query, object));
        object.put("value", 4);
        Assert.assertFalse(matches(logic, query, object));
        object.put("value", 5);
        // TODO(grantland): Move below to NumbersTest
        ParseObject iObject = new ParseObject("TestObject");
        /* (int) */
        iObject.put("value", 5);
        ParseObject dObject = new ParseObject("TestObject");
        /* (double) */
        dObject.put("value", 5.0);
        ParseObject fObject = new ParseObject("TestObject");
        /* (float) */
        fObject.put("value", 5.0F);
        ParseObject lObject = new ParseObject("TestObject");
        lObject.put("value", ((long) (5)));
        ParseQuery.State<ParseObject> iQuery = /* (int) */
        new ParseQuery.State.Builder<>("TestObject").whereEqualTo("value", 5).build();
        ParseQuery.State<ParseObject> dQuery = /* (double) */
        new ParseQuery.State.Builder<>("TestObject").whereEqualTo("value", 5.0).build();
        ParseQuery.State<ParseObject> fQuery = /* (float) */
        new ParseQuery.State.Builder<>("TestObject").whereEqualTo("value", 5.0F).build();
        ParseQuery.State<ParseObject> lQuery = new ParseQuery.State.Builder<>("TestObject").whereEqualTo("value", ((long) (5))).build();
        Assert.assertTrue(matches(logic, iQuery, iObject));
        Assert.assertTrue(matches(logic, iQuery, dObject));
        Assert.assertTrue(matches(logic, iQuery, fObject));
        Assert.assertTrue(matches(logic, iQuery, lObject));
        Assert.assertTrue(matches(logic, dQuery, iObject));
        Assert.assertTrue(matches(logic, dQuery, dObject));
        Assert.assertTrue(matches(logic, dQuery, fObject));
        Assert.assertTrue(matches(logic, dQuery, lObject));
        Assert.assertTrue(matches(logic, fQuery, iObject));
        Assert.assertTrue(matches(logic, fQuery, dObject));
        Assert.assertTrue(matches(logic, fQuery, fObject));
        Assert.assertTrue(matches(logic, fQuery, lObject));
        Assert.assertTrue(matches(logic, lQuery, iObject));
        Assert.assertTrue(matches(logic, lQuery, dObject));
        Assert.assertTrue(matches(logic, lQuery, fObject));
        Assert.assertTrue(matches(logic, lQuery, lObject));
    }

    @Test
    public void testCompareToDate() throws Exception {
        Date date = new Date();
        Date before = new Date(((date.getTime()) - 20));
        Date after = new Date(((date.getTime()) + 20));
        ParseObject object = new ParseObject("TestObject");
        object.put("date", date);
        ParseQuery.State<ParseObject> query;
        OfflineQueryLogic logic = new OfflineQueryLogic(null);
        query = new ParseQuery.State.Builder<>("TestObject").whereEqualTo("date", date).build();
        Assert.assertTrue(matches(logic, query, object));
        object.put("date", after);
        Assert.assertFalse(matches(logic, query, object));
        object.put("date", date);
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("date", "$lt", after).build();
        Assert.assertTrue(matches(logic, query, object));
        object.put("date", after);
        Assert.assertFalse(matches(logic, query, object));
        object.put("date", date);
        query = new ParseQuery.State.Builder<>("TestObject").addCondition("date", "$gt", before).build();
        Assert.assertTrue(matches(logic, query, object));
        object.put("date", before);
        Assert.assertFalse(matches(logic, query, object));
        object.put("date", after);
    }

    @Test
    public void testSortInvalidKey() throws ParseException {
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").addAscendingOrder("_test").build();
        thrown.expect(ParseException.class);
        OfflineQueryLogic.sort(null, query);
    }

    @Test
    public void testSortWithNoOrder() throws ParseException {
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").build();
        OfflineQueryLogic.sort(null, query);
    }

    @Test
    public void testSortWithGeoQuery() throws ParseException {
        ParseGeoPoint fb = new ParseGeoPoint(37.48169F, (-122.154945F));
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").whereNear("point", fb).build();
        List<ParseObject> objects = new ArrayList<>();
        ParseObject object;
        object = new ParseObject("TestObject");
        object.put("name", "sf");
        object.put("point", new ParseGeoPoint(37.77493F, (-122.41942F)));
        objects.add(object);
        object = new ParseObject("TestObject");
        object.put("name", "ny");
        object.put("point", new ParseGeoPoint(40.712784F, (-74.00594F)));
        objects.add(object);
        object = new ParseObject("TestObject");
        object.put("name", "mpk");
        object.put("point", new ParseGeoPoint(37.45296F, (-122.181725F)));
        objects.add(object);
        OfflineQueryLogic.sort(objects, query);
        Assert.assertEquals("mpk", objects.get(0).getString("name"));
        Assert.assertEquals("sf", objects.get(1).getString("name"));
        Assert.assertEquals("ny", objects.get(2).getString("name"));
    }

    @Test
    public void testSortDescending() throws ParseException {
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").addDescendingOrder("name").build();
        List<ParseObject> objects = new ArrayList<>();
        ParseObject object;
        object = new ParseObject("TestObject");
        object.put("name", "grantland");
        objects.add(object);
        object = new ParseObject("TestObject");
        object.put("name", "nikita");
        objects.add(object);
        object = new ParseObject("TestObject");
        object.put("name", "listiarso");
        objects.add(object);
        OfflineQueryLogic.sort(objects, query);
        Assert.assertEquals("nikita", objects.get(0).getString("name"));
        Assert.assertEquals("listiarso", objects.get(1).getString("name"));
        Assert.assertEquals("grantland", objects.get(2).getString("name"));
    }

    // endregion
    // region fetchIncludes
    @Test
    public void testQuerySortNumber() throws ParseException {
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").addAscendingOrder("key").build();
        List<ParseObject> results = OfflineQueryLogicTest.generateParseObjects("key", new Object[]{ 8, 7.0, 6.0F, ((long) (5)) });
        OfflineQueryLogic.sort(results, query);
        int last = 0;
        for (ParseObject result : results) {
            int current = result.getInt("key");
            Assert.assertTrue((current > last));
            last = current;
        }
    }

    @Test
    public void testQuerySortNull() throws ParseException {
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").addAscendingOrder("key").build();
        List<ParseObject> results = OfflineQueryLogicTest.generateParseObjects("key", new Object[]{ null, "value", null });
        OfflineQueryLogic.sort(results, query);
        Assert.assertEquals(0, results.get(0).getInt("id"));
        Assert.assertEquals(2, results.get(1).getInt("id"));
        Assert.assertEquals(1, results.get(2).getInt("id"));
    }

    @Test
    public void testQuerySortDifferentTypes() throws ParseException {
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").addAscendingOrder("key").build();
        List<ParseObject> results = OfflineQueryLogicTest.generateParseObjects("key", new Object[]{ "string", 5 });
        thrown.expect(IllegalArgumentException.class);
        OfflineQueryLogic.sort(results, query);
    }

    @Test
    public void testFetchIncludesParseObject() throws ParseException {
        OfflineStore store = Mockito.mock(OfflineStore.class);
        Mockito.when(store.fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class))).thenReturn(Task.<ParseObject>forResult(null));
        ParseSQLiteDatabase db = Mockito.mock(ParseSQLiteDatabase.class);
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").include("foo").build();
        ParseObject object = new ParseObject("TestObject");
        ParseObject unfetchedObject = new ParseObject("TestObject");
        object.put("foo", unfetchedObject);
        ParseTaskUtils.wait(OfflineQueryLogic.fetchIncludesAsync(store, object, query, db));
        Mockito.verify(store).fetchLocallyAsync(object, db);
        Mockito.verify(store).fetchLocallyAsync(unfetchedObject, db);
        Mockito.verifyNoMoreInteractions(store);
    }

    @Test
    public void testFetchIncludesCollection() throws ParseException {
        OfflineStore store = Mockito.mock(OfflineStore.class);
        Mockito.when(store.fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class))).thenReturn(Task.<ParseObject>forResult(null));
        ParseSQLiteDatabase db = Mockito.mock(ParseSQLiteDatabase.class);
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").include("foo").build();
        ParseObject object = Mockito.mock(ParseObject.class);
        ParseObject unfetchedObject = Mockito.mock(ParseObject.class);
        Collection<ParseObject> objects = new ArrayList<>();
        objects.add(unfetchedObject);
        Mockito.when(object.get("foo")).thenReturn(objects);
        ParseTaskUtils.wait(OfflineQueryLogic.fetchIncludesAsync(store, object, query, db));
        Mockito.verify(store).fetchLocallyAsync(object, db);
        Mockito.verify(store).fetchLocallyAsync(unfetchedObject, db);
        Mockito.verifyNoMoreInteractions(store);
    }

    @Test
    public void testFetchIncludesJSONArray() throws ParseException {
        OfflineStore store = Mockito.mock(OfflineStore.class);
        Mockito.when(store.fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class))).thenReturn(Task.<ParseObject>forResult(null));
        ParseSQLiteDatabase db = Mockito.mock(ParseSQLiteDatabase.class);
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").include("foo").build();
        ParseObject object = Mockito.mock(ParseObject.class);
        ParseObject unfetchedObject = Mockito.mock(ParseObject.class);
        JSONArray objects = new JSONArray();
        objects.put(unfetchedObject);
        Mockito.when(object.get("foo")).thenReturn(objects);
        ParseTaskUtils.wait(OfflineQueryLogic.fetchIncludesAsync(store, object, query, db));
        Mockito.verify(store).fetchLocallyAsync(object, db);
        Mockito.verify(store).fetchLocallyAsync(unfetchedObject, db);
        Mockito.verifyNoMoreInteractions(store);
    }

    @Test
    public void testFetchIncludesMap() throws ParseException {
        OfflineStore store = Mockito.mock(OfflineStore.class);
        Mockito.when(store.fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class))).thenReturn(Task.<ParseObject>forResult(null));
        ParseSQLiteDatabase db = Mockito.mock(ParseSQLiteDatabase.class);
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").include("foo.bar").build();
        ParseObject object = Mockito.mock(ParseObject.class);
        ParseObject unfetchedObject = Mockito.mock(ParseObject.class);
        Map<String, ParseObject> objects = new HashMap<>();
        objects.put("bar", unfetchedObject);
        Mockito.when(object.get("foo")).thenReturn(objects);
        ParseTaskUtils.wait(OfflineQueryLogic.fetchIncludesAsync(store, object, query, db));
        Mockito.verify(store).fetchLocallyAsync(object, db);
        Mockito.verify(store).fetchLocallyAsync(unfetchedObject, db);
        Mockito.verifyNoMoreInteractions(store);
    }

    @Test
    public void testFetchIncludesJSONObject() throws Exception {
        OfflineStore store = Mockito.mock(OfflineStore.class);
        Mockito.when(store.fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class))).thenReturn(Task.<ParseObject>forResult(null));
        ParseSQLiteDatabase db = Mockito.mock(ParseSQLiteDatabase.class);
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").include("foo.bar").build();
        ParseObject object = Mockito.mock(ParseObject.class);
        ParseObject unfetchedObject = Mockito.mock(ParseObject.class);
        JSONObject objects = new JSONObject();
        objects.put("bar", unfetchedObject);
        Mockito.when(object.get("foo")).thenReturn(objects);
        ParseTaskUtils.wait(OfflineQueryLogic.fetchIncludesAsync(store, object, query, db));
        Mockito.verify(store).fetchLocallyAsync(object, db);
        Mockito.verify(store).fetchLocallyAsync(unfetchedObject, db);
        Mockito.verifyNoMoreInteractions(store);
    }

    @Test
    public void testFetchIncludesNull() throws ParseException {
        OfflineStore store = Mockito.mock(OfflineStore.class);
        Mockito.when(store.fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class))).thenReturn(Task.<ParseObject>forResult(null));
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").include("foo").build();
        ParseObject object = new ParseObject("TestObject");
        object.put("foo", NULL);
        ParseTaskUtils.wait(OfflineQueryLogic.fetchIncludesAsync(store, object, query, null));
        // only itself
        Mockito.verify(store, Mockito.times(1)).fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class));
    }

    @Test
    public void testFetchIncludesNonParseObject() throws ParseException {
        OfflineStore store = Mockito.mock(OfflineStore.class);
        Mockito.when(store.fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class))).thenReturn(Task.<ParseObject>forResult(null));
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").include("foo").build();
        ParseObject object = new ParseObject("TestObject");
        object.put("foo", 1);
        thrown.expect(ParseException.class);
        ParseTaskUtils.wait(OfflineQueryLogic.fetchIncludesAsync(store, object, query, null));
        // only itself
        Mockito.verify(store, Mockito.times(1)).fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class));
    }

    // endregion
    @Test
    public void testFetchIncludesDoesNotExist() throws ParseException {
        OfflineStore store = Mockito.mock(OfflineStore.class);
        Mockito.when(store.fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class))).thenReturn(Task.<ParseObject>forResult(null));
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").include("foo").build();
        ParseObject object = new ParseObject("TestObject");
        ParseTaskUtils.wait(OfflineQueryLogic.fetchIncludesAsync(store, object, query, null));
        // only itself
        Mockito.verify(store, Mockito.times(1)).fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class));
    }

    @Test
    public void testFetchIncludesNestedNull() throws Exception {
        OfflineStore store = Mockito.mock(OfflineStore.class);
        Mockito.when(store.fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class))).thenReturn(Task.<ParseObject>forResult(null));
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").include("foo.bar").build();
        ParseObject object = new ParseObject("TestObject");
        object.put("foo", NULL);
        ParseTaskUtils.wait(OfflineQueryLogic.fetchIncludesAsync(store, object, query, null));
        // only itself
        Mockito.verify(store, Mockito.times(1)).fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class));
    }

    @Test
    public void testFetchIncludesNestedNonParseObject() throws Exception {
        OfflineStore store = Mockito.mock(OfflineStore.class);
        Mockito.when(store.fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class))).thenReturn(Task.<ParseObject>forResult(null));
        ParseQuery.State<ParseObject> query = new ParseQuery.State.Builder<>("TestObject").include("foo.bar").build();
        ParseObject object = new ParseObject("TestObject");
        object.put("foo", 1);
        thrown.expect(IllegalStateException.class);
        ParseTaskUtils.wait(OfflineQueryLogic.fetchIncludesAsync(store, object, query, null));
        // only itself
        Mockito.verify(store, Mockito.times(1)).fetchLocallyAsync(ArgumentMatchers.any(ParseObject.class), ArgumentMatchers.any(ParseSQLiteDatabase.class));
    }
}

