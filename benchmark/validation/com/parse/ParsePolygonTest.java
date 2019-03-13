/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParsePolygon.CREATOR;
import android.os.Parcel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParsePolygonTest {
    @Test
    public void testConstructors() {
        List<ParseGeoPoint> arrayPoints = Arrays.asList(new ParseGeoPoint(0, 0), new ParseGeoPoint(0, 1), new ParseGeoPoint(1, 1), new ParseGeoPoint(1, 0));
        List<ParseGeoPoint> listPoints = new ArrayList<>();
        listPoints.add(new ParseGeoPoint(0, 0));
        listPoints.add(new ParseGeoPoint(0, 1));
        listPoints.add(new ParseGeoPoint(1, 1));
        listPoints.add(new ParseGeoPoint(1, 0));
        ParsePolygon polygonList = new ParsePolygon(listPoints);
        Assert.assertEquals(listPoints, polygonList.getCoordinates());
        ParsePolygon polygonArray = new ParsePolygon(arrayPoints);
        Assert.assertEquals(arrayPoints, polygonArray.getCoordinates());
        ParsePolygon copyList = new ParsePolygon(polygonList);
        Assert.assertEquals(polygonList.getCoordinates(), copyList.getCoordinates());
        ParsePolygon copyArray = new ParsePolygon(polygonArray);
        Assert.assertEquals(polygonArray.getCoordinates(), copyArray.getCoordinates());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThreePointMinimum() {
        ParseGeoPoint p1 = new ParseGeoPoint(0, 0);
        ParseGeoPoint p2 = new ParseGeoPoint(0, 1);
        List<ParseGeoPoint> points = Arrays.asList(p1, p2);
        ParsePolygon polygon = new ParsePolygon(points);
    }

    @Test
    public void testEquality() {
        List<ParseGeoPoint> points = new ArrayList<>();
        points.add(new ParseGeoPoint(0, 0));
        points.add(new ParseGeoPoint(0, 1));
        points.add(new ParseGeoPoint(1, 1));
        points.add(new ParseGeoPoint(1, 0));
        List<ParseGeoPoint> diff = new ArrayList<>();
        diff.add(new ParseGeoPoint(0, 0));
        diff.add(new ParseGeoPoint(0, 10));
        diff.add(new ParseGeoPoint(10, 10));
        diff.add(new ParseGeoPoint(10, 0));
        diff.add(new ParseGeoPoint(0, 0));
        ParsePolygon polygonA = new ParsePolygon(points);
        ParsePolygon polygonB = new ParsePolygon(points);
        ParsePolygon polygonC = new ParsePolygon(diff);
        Assert.assertTrue(polygonA.equals(polygonB));
        Assert.assertTrue(polygonA.equals(polygonA));
        Assert.assertTrue(polygonB.equals(polygonA));
        Assert.assertFalse(polygonA.equals(null));
        Assert.assertFalse(polygonA.equals(true));
        Assert.assertFalse(polygonA.equals(polygonC));
    }

    @Test
    public void testContainsPoint() {
        List<ParseGeoPoint> points = new ArrayList<>();
        points.add(new ParseGeoPoint(0, 0));
        points.add(new ParseGeoPoint(0, 1));
        points.add(new ParseGeoPoint(1, 1));
        points.add(new ParseGeoPoint(1, 0));
        ParseGeoPoint inside = new ParseGeoPoint(0.5, 0.5);
        ParseGeoPoint outside = new ParseGeoPoint(10, 10);
        ParsePolygon polygon = new ParsePolygon(points);
        Assert.assertTrue(polygon.containsPoint(inside));
        Assert.assertFalse(polygon.containsPoint(outside));
    }

    @Test
    public void testParcelable() {
        List<ParseGeoPoint> points = new ArrayList<>();
        points.add(new ParseGeoPoint(0, 0));
        points.add(new ParseGeoPoint(0, 1));
        points.add(new ParseGeoPoint(1, 1));
        points.add(new ParseGeoPoint(1, 0));
        ParsePolygon polygon = new ParsePolygon(points);
        Parcel parcel = Parcel.obtain();
        polygon.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        polygon = CREATOR.createFromParcel(parcel);
        Assert.assertEquals(polygon.getCoordinates(), points);
    }
}

