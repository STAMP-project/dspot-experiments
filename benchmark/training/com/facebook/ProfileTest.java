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


import RuntimeEnvironment.application;
import android.net.Uri;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public final class ProfileTest extends FacebookTestCase {
    static final String ID = "ID";

    static final String ANOTHER_ID = "ANOTHER_ID";

    static final String FIRST_NAME = "FIRST_NAME";

    static final String MIDDLE_NAME = "MIDDLE_NAME";

    static final String LAST_NAME = "LAST_NAME";

    static final String NAME = "NAME";

    static final Uri LINK_URI = Uri.parse("https://www.facebook.com/name");

    @Test
    public void testProfileCtorAndGetters() {
        Profile profile = ProfileTest.createDefaultProfile();
        ProfileTest.assertDefaultObjectGetters(profile);
        profile = ProfileTest.createMostlyNullsProfile();
        ProfileTest.assertMostlyNullsObjectGetters(profile);
    }

    @Test
    public void testHashCode() {
        Profile profile1 = ProfileTest.createDefaultProfile();
        Profile profile2 = ProfileTest.createDefaultProfile();
        Assert.assertEquals(profile1.hashCode(), profile2.hashCode());
        Profile profile3 = ProfileTest.createMostlyNullsProfile();
        Assert.assertNotEquals(profile1.hashCode(), profile3.hashCode());
    }

    @Test
    public void testEquals() {
        Profile profile1 = ProfileTest.createDefaultProfile();
        Profile profile2 = ProfileTest.createDefaultProfile();
        Assert.assertEquals(profile1, profile2);
        Profile profile3 = ProfileTest.createMostlyNullsProfile();
        Assert.assertNotEquals(profile1, profile3);
    }

    @Test
    public void testJsonSerialization() {
        Profile profile1 = ProfileTest.createDefaultProfile();
        JSONObject jsonObject = profile1.toJSONObject();
        Profile profile2 = new Profile(jsonObject);
        ProfileTest.assertDefaultObjectGetters(profile2);
        Assert.assertEquals(profile1, profile2);
        // Check with nulls
        profile1 = ProfileTest.createMostlyNullsProfile();
        jsonObject = profile1.toJSONObject();
        profile2 = new Profile(jsonObject);
        ProfileTest.assertMostlyNullsObjectGetters(profile2);
        Assert.assertEquals(profile1, profile2);
    }

    @Test
    public void testParcelSerialization() {
        Profile profile1 = ProfileTest.createDefaultProfile();
        Profile profile2 = TestUtils.parcelAndUnparcel(profile1);
        ProfileTest.assertDefaultObjectGetters(profile2);
        Assert.assertEquals(profile1, profile2);
        // Check with nulls
        profile1 = ProfileTest.createMostlyNullsProfile();
        profile2 = TestUtils.parcelAndUnparcel(profile1);
        ProfileTest.assertMostlyNullsObjectGetters(profile2);
        Assert.assertEquals(profile1, profile2);
    }

    @Test
    public void testGetSetCurrentProfile() {
        FacebookSdk.setApplicationId("123456789");
        FacebookSdk.setAutoLogAppEventsEnabled(false);
        FacebookSdk.sdkInitialize(application);
        Profile profile1 = ProfileTest.createDefaultProfile();
        Profile.setCurrentProfile(profile1);
        Assert.assertEquals(ProfileManager.getInstance().getCurrentProfile(), profile1);
        Assert.assertEquals(profile1, Profile.getCurrentProfile());
        Profile.setCurrentProfile(null);
        Assert.assertNull(ProfileManager.getInstance().getCurrentProfile());
        Assert.assertNull(Profile.getCurrentProfile());
    }
}

