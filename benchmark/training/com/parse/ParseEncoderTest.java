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
import ParseFile.State.Builder;
import ParseQuery.RelationConstraint;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// For android.util.Base64
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseEncoderTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    ParseEncoderTest.ParseEncoderTestClass testClassObject = null;

    @Test
    public void testQueryStateBuilder() throws JSONException {
        ParseQuery.State.Builder<ParseObject> stateBuilder = new ParseQuery.State.Builder<>("TestObject");
        JSONObject stateJSON = ((JSONObject) (testClassObject.encode(stateBuilder)));
        Assert.assertNotNull(stateJSON);
        Assert.assertEquals("TestObject", stateJSON.getString("className"));
    }

    @Test
    public void testQueryState() throws JSONException {
        ParseQuery.State<ParseObject> state = new ParseQuery.State.Builder<>("TestObject").build();
        JSONObject stateJSON = ((JSONObject) (testClassObject.encode(state)));
        Assert.assertNotNull(stateJSON);
        Assert.assertEquals("TestObject", stateJSON.getString("className"));
    }

    @Test
    public void testDate() throws JSONException {
        Date date = ParseDateFormat.getInstance().parse("2011-08-21T18:02:52.249Z");
        JSONObject dateJSON = ((JSONObject) (testClassObject.encode(date)));
        Assert.assertNotNull(dateJSON);
        Assert.assertEquals("Date", dateJSON.getString("__type"));
        Assert.assertEquals("2011-08-21T18:02:52.249Z", dateJSON.getString("iso"));
    }

    @Test
    public void testBytes() throws JSONException {
        byte[] byteArr = "This is an encoded string".getBytes();
        JSONObject bytesJSON = ((JSONObject) (testClassObject.encode(byteArr)));
        Assert.assertNotNull(bytesJSON);
        Assert.assertEquals("Bytes", bytesJSON.getString("__type"));
        Assert.assertEquals("VGhpcyBpcyBhbiBlbmNvZGVkIHN0cmluZw==", bytesJSON.getString("base64"));
    }

    @Test
    public void testParseFile() throws JSONException {
        ParseFile.State.Builder parseFileStateBuilder = new ParseFile.State.Builder();
        parseFileStateBuilder.name("image-file.png");
        parseFileStateBuilder.url("http://folder/image-file.png");
        ParseFile parseFile = new ParseFile(parseFileStateBuilder.build());
        JSONObject fileJSON = ((JSONObject) (testClassObject.encode(parseFile)));
        Assert.assertNotNull(fileJSON);
        Assert.assertEquals("File", fileJSON.getString("__type"));
        Assert.assertEquals("image-file.png", fileJSON.getString("name"));
        Assert.assertEquals("http://folder/image-file.png", fileJSON.getString("url"));
    }

    @Test
    public void testParseGeoPoint() throws JSONException {
        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(30, (-20));
        JSONObject geoPointJSON = ((JSONObject) (testClassObject.encode(parseGeoPoint)));
        Assert.assertNotNull(geoPointJSON);
        final double DELTA = 1.0E-5;
        Assert.assertEquals("GeoPoint", geoPointJSON.getString("__type"));
        Assert.assertEquals(30, geoPointJSON.getDouble("latitude"), DELTA);
        Assert.assertEquals((-20), geoPointJSON.getDouble("longitude"), DELTA);
    }

    @Test
    public void testParsePolygon() throws JSONException {
        List<ParseGeoPoint> points = new ArrayList<>();
        points.add(new ParseGeoPoint(0, 0));
        points.add(new ParseGeoPoint(0, 1));
        points.add(new ParseGeoPoint(1, 1));
        points.add(new ParseGeoPoint(1, 0));
        ParsePolygon parsePolygon = new ParsePolygon(points);
        JSONObject polygonJSON = ((JSONObject) (testClassObject.encode(parsePolygon)));
        Assert.assertNotNull(polygonJSON);
        Assert.assertEquals("Polygon", polygonJSON.getString("__type"));
        Assert.assertEquals(parsePolygon.coordinatesToJSONArray(), polygonJSON.getJSONArray("coordinates"));
    }

    @Test
    public void testParseACL() {
        ParseACL parseACL = new ParseACL();
        JSONObject aclJSON = ((JSONObject) (testClassObject.encode(parseACL)));
        Assert.assertNotNull(aclJSON);
    }

    @Test
    public void testMap() throws JSONException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("key1", "object1");
        map.put("key2", "object2");
        JSONObject mapJSON = ((JSONObject) (testClassObject.encode(map)));
        Assert.assertNotNull(mapJSON);
        Assert.assertEquals(2, mapJSON.length());
        Assert.assertEquals("object1", mapJSON.getString("key1"));
        Assert.assertEquals("object2", mapJSON.getString("key2"));
    }

    @Test
    public void testCollection() throws JSONException {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        JSONArray jsonArray = ((JSONArray) (testClassObject.encode(list)));
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(2, jsonArray.length());
        Assert.assertEquals(1, jsonArray.get(0));
        Assert.assertEquals(2, jsonArray.get(1));
    }

    @Test
    public void testRelation() throws JSONException {
        ParseRelation parseRelation = new ParseRelation("TestObject");
        JSONObject relationJSON = ((JSONObject) (testClassObject.encode(parseRelation)));
        Assert.assertNotNull(relationJSON);
        Assert.assertEquals("Relation", relationJSON.getString("__type"));
        Assert.assertEquals("TestObject", relationJSON.getString("className"));
    }

    @Test
    public void testParseFieldOperations() throws JSONException {
        ParseIncrementOperation incrementOperation = new ParseIncrementOperation(2);
        JSONObject incrementJSON = ((JSONObject) (testClassObject.encode(incrementOperation)));
        Assert.assertNotNull(incrementJSON);
        Assert.assertEquals("Increment", incrementJSON.getString("__op"));
        Assert.assertEquals(2, incrementJSON.getInt("amount"));
    }

    @Test
    public void testRelationContraint() throws JSONException {
        ParseObject parseObject = new ParseObject("TestObject");
        ParseQuery.RelationConstraint relationConstraint = new ParseQuery.RelationConstraint(">", parseObject);
        JSONObject relationConstraintJSON = ((JSONObject) (testClassObject.encode(relationConstraint)));
        Assert.assertNotNull(relationConstraintJSON);
        Assert.assertEquals(">", relationConstraintJSON.getString("key"));
    }

    @Test
    public void testNull() {
        Object object = testClassObject.encode(null);
        Assert.assertEquals(object, NULL);
    }

    @Test
    public void testPrimitives() {
        String encodedStr = ((String) (testClassObject.encode("String")));
        Assert.assertEquals(encodedStr, "String");
        int encodedInteger = ((Integer) (testClassObject.encode(5)));
        Assert.assertEquals(5, encodedInteger);
        boolean encodedBoolean = ((Boolean) (testClassObject.encode(true)));
        Assert.assertTrue(encodedBoolean);
        final double DELTA = 1.0E-5;
        double encodedDouble = ((Double) (encode(5.5)));
        Assert.assertEquals(5.5, encodedDouble, DELTA);
    }

    @Test
    public void testIllegalArgument() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(("invalid type for ParseObject: " + (ParseDecoder.class.toString())));
        testClassObject.encode(ParseDecoder.get());
    }

    private static class ParseEncoderTestClass extends ParseEncoder {
        @Override
        protected JSONObject encodeRelatedObject(ParseObject object) {
            return null;
        }
    }
}

