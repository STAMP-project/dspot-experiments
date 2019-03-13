/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseObject.State.Builder;
import android.os.Parcel;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseObjectStateTest {
    @Test
    public void testDefaults() {
        ParseObject.State state = new ParseObject.State.Builder("TestObject").build();
        Assert.assertEquals("TestObject", state.className());
        Assert.assertNull(state.objectId());
        Assert.assertEquals((-1), state.createdAt());
        Assert.assertEquals((-1), state.updatedAt());
        Assert.assertFalse(state.isComplete());
        Assert.assertTrue(state.keySet().isEmpty());
        Assert.assertTrue(state.availableKeys().isEmpty());
    }

    @Test
    public void testProperties() {
        long updatedAt = System.currentTimeMillis();
        long createdAt = updatedAt + 10;
        ParseObject.State state = build();
        Assert.assertEquals("TestObject", state.className());
        Assert.assertEquals("fake", state.objectId());
        Assert.assertEquals(createdAt, state.createdAt());
        Assert.assertEquals(updatedAt, state.updatedAt());
        Assert.assertTrue(state.isComplete());
    }

    @Test
    public void testCopy() {
        long updatedAt = System.currentTimeMillis();
        long createdAt = updatedAt + 10;
        ParseObject.State state = build();
        ParseObject.State copy = build();
        Assert.assertEquals(state.className(), copy.className());
        Assert.assertEquals(state.objectId(), copy.objectId());
        Assert.assertEquals(state.createdAt(), copy.createdAt());
        Assert.assertEquals(state.updatedAt(), copy.updatedAt());
        Assert.assertEquals(state.isComplete(), copy.isComplete());
        Assert.assertEquals(state.keySet().size(), copy.keySet().size());
        Assert.assertEquals(state.get("foo"), copy.get("foo"));
        Assert.assertEquals(state.get("baz"), copy.get("baz"));
        Assert.assertEquals(state.availableKeys().size(), copy.availableKeys().size());
        Assert.assertTrue(state.availableKeys().containsAll(copy.availableKeys()));
        Assert.assertTrue(copy.availableKeys().containsAll(state.availableKeys()));
    }

    @Test
    public void testParcelable() {
        long updatedAt = System.currentTimeMillis();
        long createdAt = updatedAt + 10;
        ParseObject.State state = build();
        Parcel parcel = Parcel.obtain();
        state.writeToParcel(parcel, ParseParcelEncoder.get());
        parcel.setDataPosition(0);
        ParseObject.State copy = ParseObject.State.createFromParcel(parcel, ParseParcelDecoder.get());
        Assert.assertEquals(state.className(), copy.className());
        Assert.assertEquals(state.objectId(), copy.objectId());
        Assert.assertEquals(state.createdAt(), copy.createdAt());
        Assert.assertEquals(state.updatedAt(), copy.updatedAt());
        Assert.assertEquals(state.isComplete(), copy.isComplete());
        Assert.assertEquals(state.keySet().size(), copy.keySet().size());
        Assert.assertEquals(state.get("foo"), copy.get("foo"));
        Assert.assertEquals(state.get("baz"), copy.get("baz"));
        Assert.assertEquals(state.availableKeys().size(), copy.availableKeys().size());
        Assert.assertTrue(state.availableKeys().containsAll(copy.availableKeys()));
        Assert.assertTrue(copy.availableKeys().containsAll(state.availableKeys()));
    }

    @Test
    public void testAutomaticUpdatedAt() {
        long createdAt = System.currentTimeMillis();
        ParseObject.State state = build();
        Assert.assertEquals(createdAt, state.createdAt());
        Assert.assertEquals(createdAt, state.updatedAt());
    }

    @Test
    public void testServerData() {
        ParseObject.State.Builder builder = new ParseObject.State.Builder("TestObject");
        ParseObject.State state = builder.build();
        Assert.assertTrue(state.keySet().isEmpty());
        builder.put("foo", "bar").put("baz", "qux");
        state = builder.build();
        Assert.assertEquals(2, state.keySet().size());
        Assert.assertEquals("bar", state.get("foo"));
        Assert.assertEquals("qux", state.get("baz"));
        builder.remove("foo");
        state = builder.build();
        Assert.assertEquals(1, state.keySet().size());
        Assert.assertNull(state.get("foo"));
        Assert.assertEquals("qux", state.get("baz"));
        builder.clear();
        state = builder.build();
        Assert.assertTrue(state.keySet().isEmpty());
        Assert.assertNull(state.get("foo"));
        Assert.assertNull(state.get("baz"));
    }

    @Test
    public void testToString() {
        String string = new ParseObject.State.Builder("TestObject").build().toString();
        Assert.assertTrue(string.contains("com.parse.ParseObject$State"));
        Assert.assertTrue(string.contains("className"));
        Assert.assertTrue(string.contains("objectId"));
        Assert.assertTrue(string.contains("createdAt"));
        Assert.assertTrue(string.contains("updatedAt"));
        Assert.assertTrue(string.contains("isComplete"));
        Assert.assertTrue(string.contains("serverData"));
        Assert.assertTrue(string.contains("availableKeys"));
    }
}

