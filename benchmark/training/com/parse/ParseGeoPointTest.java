/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseGeoPoint.CREATOR;
import android.os.Parcel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseGeoPointTest {
    @Test
    public void testConstructors() {
        ParseGeoPoint point = new ParseGeoPoint();
        Assert.assertEquals(0, point.getLatitude(), 0);
        Assert.assertEquals(0, point.getLongitude(), 0);
        double lat = 1.0;
        double lng = 2.0;
        point = new ParseGeoPoint(lat, lng);
        Assert.assertEquals(lat, point.getLatitude(), 0);
        Assert.assertEquals(lng, point.getLongitude(), 0);
        ParseGeoPoint copy = new ParseGeoPoint(point);
        Assert.assertEquals(lat, copy.getLatitude(), 0);
        Assert.assertEquals(lng, copy.getLongitude(), 0);
    }

    @Test
    public void testEquals() {
        ParseGeoPoint pointA = new ParseGeoPoint(30.0, 50.0);
        ParseGeoPoint pointB = new ParseGeoPoint(30.0, 50.0);
        ParseGeoPoint pointC = new ParseGeoPoint(45.0, 45.0);
        Assert.assertTrue(pointA.equals(pointB));
        Assert.assertTrue(pointA.equals(pointA));
        Assert.assertTrue(pointB.equals(pointA));
        Assert.assertFalse(pointA.equals(null));
        Assert.assertFalse(pointA.equals(true));
        Assert.assertFalse(pointA.equals(pointC));
    }

    @Test
    public void testParcelable() {
        ParseGeoPoint point = new ParseGeoPoint(30.0, 50.0);
        Parcel parcel = Parcel.obtain();
        point.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        point = CREATOR.createFromParcel(parcel);
        Assert.assertEquals(point.getLatitude(), 30.0, 0);
        Assert.assertEquals(point.getLongitude(), 50.0, 0);
    }
}

