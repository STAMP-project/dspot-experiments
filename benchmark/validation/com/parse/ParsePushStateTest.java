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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.skyscreamer.jsonassert.JSONAssert;


// endregion
public class ParsePushStateTest {
    // region testDefaults
    @Test(expected = IllegalArgumentException.class)
    public void testDefaultsWithoutData() {
        // We have to set data to a state otherwise it will throw an exception
        JSONObject data = new JSONObject();
        ParsePush.State state = new ParsePush.State.Builder().build();
    }

    @Test
    public void testDefaultsWithData() throws Exception {
        // We have to set data to a state otherwise it will throw an exception
        JSONObject data = new JSONObject();
        ParsePush.State state = build();
        Assert.assertEquals(null, state.expirationTime());
        Assert.assertEquals(null, state.expirationTimeInterval());
        Assert.assertEquals(null, state.pushTime());
        Assert.assertEquals(null, state.channelSet());
        JSONAssert.assertEquals(data, state.data(), NON_EXTENSIBLE);
        Assert.assertEquals(null, state.queryState());
    }

    // endregion
    @Test
    public void testCopy() throws JSONException {
        ParsePush.State state = Mockito.mock(ParsePush.State.class);
        Mockito.when(state.expirationTime()).thenReturn(1L);
        Mockito.when(state.expirationTimeInterval()).thenReturn(2L);
        Mockito.when(state.pushTime()).thenReturn(3L);
        Set channelSet = Sets.newSet("one", "two");
        Mockito.when(state.channelSet()).thenReturn(channelSet);
        JSONObject data = new JSONObject();
        data.put("foo", "bar");
        Mockito.when(state.data()).thenReturn(data);
        ParseQuery.State<ParseInstallation> queryState = new ParseQuery.State.Builder<>(.class).build();
        Mockito.when(state.queryState()).thenReturn(queryState);
        ParsePush.State copy = build();
        Assert.assertSame(1L, copy.expirationTime());
        Assert.assertSame(2L, copy.expirationTimeInterval());
        Assert.assertSame(3L, copy.pushTime());
        Set channelSetCopy = copy.channelSet();
        Assert.assertNotSame(channelSet, channelSetCopy);
        Assert.assertTrue((((channelSetCopy.size()) == 2) && (channelSetCopy.contains("one"))));
        JSONObject dataCopy = copy.data();
        Assert.assertNotSame(data, dataCopy);
        Assert.assertEquals("bar", dataCopy.get("foo"));
        ParseQuery.State<ParseInstallation> queryStateCopy = copy.queryState();
        Assert.assertNotSame(queryState, queryStateCopy);
        Assert.assertEquals("_Installation", queryStateCopy.className());
    }

    // region testExpirationTime
    @Test
    public void testExpirationTimeNullTime() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        ParsePush.State state = build();
        Assert.assertEquals(null, state.expirationTime());
    }

    @Test
    public void testExpirationTimeNormalTime() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        ParsePush.State state = build();
        Assert.assertEquals(100L, state.expirationTime().longValue());
    }

    // endregion
    // region testExpirationTimeInterval
    @Test
    public void testExpirationTimeIntervalNullInterval() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        ParsePush.State state = build();
        Assert.assertEquals(null, state.expirationTimeInterval());
    }

    @Test
    public void testExpirationTimeIntervalNormalInterval() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        ParsePush.State state = build();
        Assert.assertEquals(100L, state.expirationTimeInterval().longValue());
    }

    // endregion
    // region testPushTime
    @Test
    public void testPushTimeNullTime() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        ParsePush.State state = build();
        Assert.assertEquals(null, state.pushTime());
    }

    @Test
    public void testPushTimeNormalTime() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        long time = ((System.currentTimeMillis()) / 1000) + 1000;
        ParsePush.State state = build();
        Assert.assertEquals(time, state.pushTime().longValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPushTimeInThePast() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        ParsePush.State state = build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPushTimeTwoWeeksFromNow() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        ParsePush.State state = build();
    }

    // endregion
    // region testChannelSet
    @Test(expected = IllegalArgumentException.class)
    public void testChannelSetNullChannelSet() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        ParsePush.State state = build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChannelSetNormalChannelSetWithNullChannel() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        Set<String> channelSet = new HashSet<>();
        channelSet.add(null);
        ParsePush.State state = build();
    }

    @Test
    public void testChannelSetNormalChannelSet() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        Set<String> channelSet = new HashSet<>();
        channelSet.add("foo");
        channelSet.add("bar");
        ParsePush.State state = build();
        Assert.assertEquals(2, state.channelSet().size());
        Assert.assertTrue(state.channelSet().contains("foo"));
        Assert.assertTrue(state.channelSet().contains("bar"));
    }

    @Test
    public void testChannelSetOverwrite() {
        Set<String> channelSet = new HashSet<>();
        channelSet.add("foo");
        Set<String> channelSetAgain = new HashSet<>();
        channelSetAgain.add("bar");
        ParsePush.State state = build();
        Assert.assertEquals(1, state.channelSet().size());
        Assert.assertTrue(state.channelSet().contains("bar"));
    }

    @Test
    public void testChannelSetDuplicateChannel() {
        final List<String> channelSet = new ArrayList<String>() {
            {
                add("foo");
                add("foo");
            }
        };
        ParsePush.State state = build();
        Assert.assertEquals(1, state.channelSet().size());
        Assert.assertTrue(state.channelSet().contains("foo"));
    }

    // endregion
    // region testData
    @Test(expected = IllegalArgumentException.class)
    public void testDataNullData() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        ParsePush.State state = build();
    }

    @Test
    public void testDataNormalData() throws Exception {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        JSONObject data = new JSONObject();
        data.put("name", "value");
        ParsePush.State state = build();
        JSONObject dataAgain = state.data();
        Assert.assertEquals(1, dataAgain.length());
        Assert.assertEquals("value", dataAgain.get("name"));
    }

    // endregion
    // region testQuery
    @Test(expected = IllegalArgumentException.class)
    public void testQueryNullQuery() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        ParsePush.State state = build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testQueryNotInstallationQuery() {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        ParsePush.State state = build();
    }

    @Test
    public void testQueryNormalQuery() throws Exception {
        ParsePush.State.Builder builder = new ParsePush.State.Builder();
        // Normal query
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        // Make test ParseQuery state
        ParseQuery.State.Builder<ParseObject> subQueryState = new ParseQuery.State.Builder<>("TestObject");
        query.getBuilder().whereEqualTo("foo", "bar").whereMatchesQuery("subquery", subQueryState).setLimit(12).setSkip(34).orderByAscending("foo").addDescendingOrder("bar").include("name").selectKeys(Arrays.asList("name", "blah")).setTracingEnabled(true).redirectClassNameForKey("what");
        ParsePush.State state = build();
        ParseQuery.State queryStateAgain = state.queryState();
        JSONObject queryStateAgainJson = queryStateAgain.toJSON(PointerEncoder.get());
        Assert.assertEquals("_Installation", queryStateAgainJson.getString("className"));
        JSONAssert.assertEquals(("{" + (("\"foo\":\"bar\"," + "\"subquery\":{\"$inQuery\":{\"className\":\"TestObject\",\"where\":{}}}") + "}")), queryStateAgainJson.getJSONObject("where"), NON_EXTENSIBLE);
        Assert.assertEquals(12, queryStateAgainJson.getInt("limit"));
        Assert.assertEquals(34, queryStateAgainJson.getInt("skip"));
        Assert.assertEquals("foo,-bar", queryStateAgainJson.getString("order"));
        Assert.assertEquals("name", queryStateAgainJson.getString("include"));
        Assert.assertEquals("name,blah", queryStateAgainJson.getString("fields"));
        Assert.assertEquals(1, queryStateAgainJson.getInt("trace"));
        Assert.assertEquals("what", queryStateAgainJson.getString("redirectClassNameForKey"));
    }

    // endregion
    // region testStateImmutable
    @Test
    public void testStateImmutable() throws Exception {
        JSONObject data = new JSONObject();
        data.put("name", "value");
        Set<String> channelSet = new HashSet<>();
        channelSet.add("foo");
        channelSet.add("bar");
        ParsePush.State state = build();
        // Verify channelSet immutable
        Set<String> stateChannelSet = state.channelSet();
        try {
            stateChannelSet.add("test");
            Assert.fail("Should throw an exception");
        } catch (UnsupportedOperationException e) {
            // do nothing
        }
        channelSet.add("test");
        Assert.assertEquals(2, state.channelSet().size());
        Assert.assertTrue(state.channelSet().contains("foo"));
        Assert.assertTrue(state.channelSet().contains("bar"));
        // Verify data immutable
        JSONObject stateData = state.data();
        stateData.put("foo", "bar");
        JSONObject stateDataAgain = state.data();
        Assert.assertEquals(1, stateDataAgain.length());
        Assert.assertEquals("value", stateDataAgain.get("name"));
        // Verify queryState immutable
        // TODO(mengyan) add test after t6941155(Convert mutable parameter to immutable)
    }
}

