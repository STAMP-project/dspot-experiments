/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook.places;


import HttpMethod.GET;
import HttpMethod.POST;
import PlaceSearchRequestParams.Builder;
import android.location.Location;
import android.os.Bundle;
import com.facebook.AccessToken;
import com.facebook.FacebookPowerMockTestCase;
import com.facebook.GraphRequest;
import com.facebook.places.model.CurrentPlaceFeedbackRequestParams;
import com.facebook.places.model.PlaceInfoRequestParams;
import com.facebook.places.model.PlaceSearchRequestParams;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ AccessToken.class })
public class PlaceManagerTest extends FacebookPowerMockTestCase {
    @Test
    public void testSearchPlaceForLocationRequest() {
        PlaceSearchRequestParams.Builder builder = new PlaceSearchRequestParams.Builder();
        builder.setSearchText("search text");
        builder.setLimit(5);
        builder.addCategory("category1");
        builder.addCategory("category2");
        builder.addField("field1");
        builder.addField("field2");
        builder.setDistance(500);
        PlaceSearchRequestParams params = builder.build();
        Location location = new Location("dummy");
        location.setLatitude(1);
        location.setLongitude(2);
        GraphRequest request = PlaceManager.newPlaceSearchRequestForLocation(params, location);
        Assert.assertEquals("search", request.getGraphPath());
        Assert.assertEquals(GET, request.getHttpMethod());
        Bundle requestParams = request.getParameters();
        Assert.assertEquals("search text", requestParams.get("q"));
        Assert.assertEquals(500, requestParams.get("distance"));
        Assert.assertEquals(5, requestParams.get("limit"));
        Assert.assertEquals("1.000000,2.000000", requestParams.get("center"));
        Assert.assertEquals("field1,field2", requestParams.get("fields"));
        Assert.assertEquals("place", requestParams.get("type"));
        Assert.assertEquals("[\"category2\",\"category1\"]", requestParams.get("categories"));
    }

    @Test
    public void testPlaceInfoRequest() {
        PlaceInfoRequestParams.Builder builder = new PlaceInfoRequestParams.Builder();
        builder.setPlaceId("12345");
        builder.addField("field1");
        builder.addFields(new String[]{ "field2", "field3" });
        PlaceInfoRequestParams params = builder.build();
        GraphRequest request = PlaceManager.newPlaceInfoRequest(params);
        Assert.assertEquals("12345", request.getGraphPath());
        Assert.assertEquals(GET, request.getHttpMethod());
        Bundle requestParams = request.getParameters();
        Assert.assertEquals("field1,field3,field2", requestParams.get("fields"));
    }

    @Test
    public void testCurrentPlaceFeedbackRequest() {
        CurrentPlaceFeedbackRequestParams.Builder builder = new CurrentPlaceFeedbackRequestParams.Builder();
        builder.setPlaceId("12345");
        builder.setTracking("trackingid");
        builder.setWasHere(true);
        CurrentPlaceFeedbackRequestParams params = builder.build();
        GraphRequest request = PlaceManager.newCurrentPlaceFeedbackRequest(params);
        Assert.assertEquals("current_place/feedback", request.getGraphPath());
        Assert.assertEquals(POST, request.getHttpMethod());
        Bundle requestParams = request.getParameters();
        Assert.assertEquals("12345", requestParams.get("place_id"));
        Assert.assertEquals("trackingid", requestParams.get("tracking"));
        Assert.assertEquals(true, requestParams.get("was_here"));
    }
}

