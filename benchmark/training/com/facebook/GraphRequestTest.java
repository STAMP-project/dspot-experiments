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
package com.facebook;


import Bitmap.Config.ALPHA_8;
import HttpMethod.GET;
import HttpMethod.POST;
import ShareInternalUtility.MY_PHOTOS;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import java.net.HttpURLConnection;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ FacebookSdk.class, AccessTokenManager.class })
public class GraphRequestTest extends FacebookPowerMockTestCase {
    @Test
    public void testCreateRequest() {
        GraphRequest request = new GraphRequest();
        Assert.assertTrue((request != null));
        Assert.assertEquals(GET, request.getHttpMethod());
    }

    @Test
    public void testCreatePostRequest() {
        JSONObject graphObject = new JSONObject();
        GraphRequest request = GraphRequest.newPostRequest(null, "me/statuses", graphObject, null);
        Assert.assertTrue((request != null));
        Assert.assertEquals(POST, request.getHttpMethod());
        Assert.assertEquals("me/statuses", request.getGraphPath());
        Assert.assertEquals(graphObject, request.getGraphObject());
    }

    @Test
    public void testCreateMeRequest() {
        GraphRequest request = GraphRequest.newMeRequest(null, null);
        Assert.assertTrue((request != null));
        Assert.assertEquals(GET, request.getHttpMethod());
        Assert.assertEquals("me", request.getGraphPath());
    }

    @Test
    public void testCreateMyFriendsRequest() {
        GraphRequest request = GraphRequest.newMyFriendsRequest(null, null);
        Assert.assertTrue((request != null));
        Assert.assertEquals(GET, request.getHttpMethod());
        Assert.assertEquals("me/friends", request.getGraphPath());
    }

    @Test
    public void testCreateUploadPhotoRequest() {
        Bitmap image = Bitmap.createBitmap(128, 128, ALPHA_8);
        GraphRequest request = GraphRequest.newUploadPhotoRequest(null, MY_PHOTOS, image, null, null, null);
        Assert.assertTrue((request != null));
        Bundle parameters = request.getParameters();
        Assert.assertTrue((parameters != null));
        Assert.assertTrue(parameters.containsKey("picture"));
        Assert.assertEquals(image, parameters.getParcelable("picture"));
        Assert.assertEquals("me/photos", request.getGraphPath());
    }

    @Test
    public void testCreatePlacesSearchRequestWithLocation() {
        Location location = new Location("");
        location.setLatitude(47.6204);
        location.setLongitude((-122.3491));
        GraphRequest request = GraphRequest.newPlacesSearchRequest(null, location, 1000, 50, null, null);
        Assert.assertTrue((request != null));
        Assert.assertEquals(GET, request.getHttpMethod());
        Assert.assertEquals("search", request.getGraphPath());
    }

    @Test
    public void testCreatePlacesSearchRequestWithSearchText() {
        GraphRequest request = GraphRequest.newPlacesSearchRequest(null, null, 1000, 50, "Starbucks", null);
        Assert.assertTrue((request != null));
        Assert.assertEquals(GET, request.getHttpMethod());
        Assert.assertEquals("search", request.getGraphPath());
    }

    @Test
    public void testCreatePlacesSearchRequestRequiresLocationOrSearchText() {
        try {
            GraphRequest.newPlacesSearchRequest(null, null, 1000, 50, null, null);
            Assert.fail("expected exception");
        } catch (FacebookException exception) {
            // Success
        }
    }

    @Test
    public void testSetHttpMethodToNilGivesDefault() {
        GraphRequest request = new GraphRequest();
        Assert.assertEquals(GET, request.getHttpMethod());
        request.setHttpMethod(null);
        Assert.assertEquals(GET, request.getHttpMethod());
    }

    @Test
    public void testExecuteBatchWithNullRequestsThrows() {
        try {
            GraphRequest.executeBatchAndWait(((GraphRequest[]) (null)));
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException exception) {
        }
    }

    @Test
    public void testExecuteBatchWithZeroRequestsThrows() {
        try {
            GraphRequest.executeBatchAndWait(new GraphRequest[]{  });
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException exception) {
        }
    }

    @Test
    public void testExecuteBatchWithNullRequestThrows() {
        try {
            GraphRequest.executeBatchAndWait(new GraphRequest[]{ null });
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException exception) {
        }
    }

    @Test
    public void testToHttpConnectionWithNullRequestsThrows() {
        try {
            GraphRequest.toHttpConnection(((GraphRequest[]) (null)));
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException exception) {
        }
    }

    @Test
    public void testToHttpConnectionWithZeroRequestsThrows() {
        try {
            GraphRequest.toHttpConnection(new GraphRequest[]{  });
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException exception) {
        }
    }

    @Test
    public void testToHttpConnectionWithNullRequestThrows() {
        try {
            GraphRequest.toHttpConnection(new GraphRequest[]{ null });
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException exception) {
        }
    }

    @Test
    public void testSingleGetToHttpRequest() throws Exception {
        GraphRequest requestMe = new GraphRequest(null, "TourEiffel");
        HttpURLConnection connection = GraphRequest.toHttpConnection(requestMe);
        Assert.assertTrue((connection != null));
        Assert.assertEquals("GET", connection.getRequestMethod());
        Assert.assertEquals((("/" + (FacebookSdk.getGraphApiVersion())) + "/TourEiffel"), connection.getURL().getPath());
        Assert.assertTrue(connection.getRequestProperty("User-Agent").startsWith("FBAndroidSDK"));
        Uri uri = Uri.parse(connection.getURL().toString());
        Assert.assertEquals("android", uri.getQueryParameter("sdk"));
        Assert.assertEquals("json", uri.getQueryParameter("format"));
    }

    @Test
    public void testBuildsClientTokenIfNeeded() throws Exception {
        GraphRequest requestMe = new GraphRequest(null, "TourEiffel");
        HttpURLConnection connection = GraphRequest.toHttpConnection(requestMe);
        Assert.assertTrue((connection != null));
        Uri uri = Uri.parse(connection.getURL().toString());
        String accessToken = uri.getQueryParameter("access_token");
        Assert.assertNotNull(accessToken);
        Assert.assertTrue(accessToken.contains(FacebookSdk.getApplicationId()));
        Assert.assertTrue(accessToken.contains(FacebookSdk.getClientToken()));
    }
}

