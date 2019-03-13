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
import ParseQuery.CachePolicy.IGNORE_CACHE;
import ParseQuery.QueryConstraints;
import ParseQuery.State.Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.skyscreamer.jsonassert.JSONAssert;

import static java.util.Collections.singletonList;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseQueryStateTest extends ResetPluginsParseTest {
    @Test
    public void testDefaults() {
        ParseQuery.State.Builder<ParseObject> builder = new ParseQuery.State.Builder<>("TestObject");
        ParseQuery.State<ParseObject> state = builder.build();
        Assert.assertEquals("TestObject", state.className());
        Assert.assertTrue(state.constraints().isEmpty());
        Assert.assertTrue(state.includes().isEmpty());
        Assert.assertNull(state.selectedKeys());
        Assert.assertEquals((-1), state.limit());
        Assert.assertEquals(0, state.skip());
        Assert.assertTrue(state.order().isEmpty());
        Assert.assertTrue(state.extraOptions().isEmpty());
        Assert.assertFalse(state.isTracingEnabled());
        Assert.assertEquals(IGNORE_CACHE, state.cachePolicy());
        Assert.assertEquals(Long.MAX_VALUE, state.maxCacheAge());
        Assert.assertFalse(state.isFromLocalDatastore());
        Assert.assertNull(state.pinName());
        Assert.assertFalse(state.ignoreACLs());
    }

    @Test
    public void testClassName() {
        ParseQuery.State<ParseObject> stateA = new ParseQuery.State.Builder<>("TestObject").build();
        Assert.assertEquals("TestObject", stateA.className());
        ParseQuery.State<ParseUser> stateB = new ParseQuery.State.Builder<>(.class).build();
        Assert.assertEquals("_User", stateB.className());
    }

    @Test
    public void testConstraints() {
        ParseQuery.QueryConstraints constraints;
        ParseQuery.State.Builder<ParseObject> builder = new ParseQuery.State.Builder<>("TestObject");
        constraints = // Object
        // Collection (overwrite)
        // Collection
        // Should overwrite since same key
        builder.whereEqualTo("foo", "bar").whereEqualTo("foo", "baz").addCondition("people", "$in", singletonList("stanley")).addCondition("people", "$in", singletonList("grantland")).addCondition("something", "$exists", false).build().constraints();
        Assert.assertEquals(3, constraints.size());
        Assert.assertEquals("baz", constraints.get("foo"));
        Collection<?> in = ((Collection<?>) (get("$in")));
        Assert.assertEquals(1, in.size());
        Assert.assertEquals("grantland", new ArrayList<>(in).get(0));
        Assert.assertEquals(false, get("$exists"));
    }

    @Test
    public void testConstraintsWithSubqueries() {
        // TODO
    }

    @Test
    public void testParseRelation() {
        // TODO whereRelatedTo, redirectClassNameForKey
    }

    @Test
    public void testOrder() {
        ParseQuery.State<ParseObject> state;
        ParseQuery.State.Builder<ParseObject> builder = new ParseQuery.State.Builder<>("TestObject");
        // Ascending adds
        builder.orderByAscending("foo");
        state = builder.build();
        Assert.assertEquals(1, state.order().size());
        Assert.assertEquals("foo", state.order().get(0));
        // Descending clears and add
        builder.orderByDescending("foo");
        state = builder.build();
        Assert.assertEquals(1, state.order().size());
        Assert.assertEquals("-foo", state.order().get(0));
        // Add ascending/descending adds
        builder.addAscendingOrder("bar");
        builder.addDescendingOrder("baz");
        state = builder.build();
        Assert.assertEquals(3, state.order().size());
        Assert.assertEquals("-foo", state.order().get(0));
        Assert.assertEquals("bar", state.order().get(1));
        Assert.assertEquals("-baz", state.order().get(2));
        // Ascending clears and adds
        builder.orderByAscending("foo");
        state = builder.build();
        Assert.assertEquals(1, state.order().size());
    }

    @Test
    public void testMisc() {
        // Include, SelectKeys, Limit, Skip
        ParseQuery.State.Builder<ParseObject> builder = new ParseQuery.State.Builder<>("TestObject");
        builder.include("foo").include("bar");
        Assert.assertEquals(2, builder.build().includes().size());
        builder.selectKeys(singletonList("foo")).selectKeys(Arrays.asList("bar", "baz", "qux"));
        Assert.assertEquals(4, builder.build().selectedKeys().size());
        builder.setLimit(42);
        Assert.assertEquals(42, builder.getLimit());
        Assert.assertEquals(42, builder.build().limit());
        builder.setSkip(48);
        Assert.assertEquals(48, builder.getSkip());
        Assert.assertEquals(48, builder.build().skip());
    }

    @Test
    public void testTrace() {
        Assert.assertTrue(new ParseQuery.State.Builder<>("TestObject").setTracingEnabled(true).build().isTracingEnabled());
    }

    @Test
    public void testCachePolicy() {
        // TODO
    }

    // TODO(grantland): Add tests for LDS and throwing for LDS/CachePolicy once we remove OfflineStore
    // global t6942994
    @Test(expected = IllegalStateException.class)
    public void testThrowIfNotLDSAndIgnoreACLs() {
        new ParseQuery.State.Builder<>("TestObject").fromNetwork().ignoreACLs().build();
    }

    // region Or Tests
    @Test
    public void testOr() {
        List<ParseQuery.State.Builder<ParseObject>> subqueries = new ArrayList<>();
        subqueries.add(new ParseQuery.State.Builder<>("TestObject").whereEqualTo("name", "grantland"));
        subqueries.add(new ParseQuery.State.Builder<>("TestObject").whereEqualTo("name", "stanley"));
        ParseQuery.State<ParseObject> state = Builder.or(subqueries).build();
        Assert.assertEquals("TestObject", state.className());
        ParseQuery.QueryConstraints constraints = state.constraints();
        Assert.assertEquals(1, constraints.size());
        @SuppressWarnings("unchecked")
        List<ParseQuery.QueryConstraints> or = ((List<ParseQuery.QueryConstraints>) (constraints.get("$or")));
        Assert.assertEquals(2, or.size());
        Assert.assertEquals("grantland", get("name"));
        Assert.assertEquals("stanley", get("name"));
    }

    @Test
    public void testOrIsMutable() {
        List<ParseQuery.State.Builder<ParseObject>> subqueries = new ArrayList<>();
        ParseQuery.State.Builder<ParseObject> builderA = new ParseQuery.State.Builder<>("TestObject");
        subqueries.add(builderA);
        ParseQuery.State.Builder<ParseObject> builderB = new ParseQuery.State.Builder<>("TestObject");
        subqueries.add(builderB);
        ParseQuery.State.Builder<ParseObject> builder = Builder.or(subqueries);
        // Mutate subquery after `or`
        builderA.whereEqualTo("name", "grantland");
        ParseQuery.State<ParseObject> state = builder.build();
        ParseQuery.QueryConstraints constraints = state.constraints();
        @SuppressWarnings("unchecked")
        List<ParseQuery.QueryConstraints> or = ((List<ParseQuery.QueryConstraints>) (constraints.get("$or")));
        Assert.assertEquals("grantland", get("name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOrThrowsWithEmptyList() {
        Builder.or(new ArrayList<ParseQuery.State.Builder<ParseObject>>()).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOrThrowsWithDifferentClassName() {
        List<ParseQuery.State.Builder<ParseObject>> subqueries = new ArrayList<>();
        subqueries.add(new ParseQuery.State.Builder<>("TestObjectA"));
        subqueries.add(new ParseQuery.State.Builder<>("TestObjectB"));
        Builder.or(subqueries).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOrThrowsWithLimit() {
        List<ParseQuery.State.Builder<ParseObject>> subqueries = new ArrayList<>();
        subqueries.add(new ParseQuery.State.Builder<>("TestObjectA"));
        subqueries.add(new ParseQuery.State.Builder<>("TestObjectB").setLimit(1));
        Builder.or(subqueries).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOrThrowsWithSkip() {
        List<ParseQuery.State.Builder<ParseObject>> subqueries = new ArrayList<>();
        subqueries.add(new ParseQuery.State.Builder<>("TestObjectA"));
        subqueries.add(new ParseQuery.State.Builder<>("TestObjectB").setSkip(1));
        Builder.or(subqueries).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOrThrowsWithOrder() {
        List<ParseQuery.State.Builder<ParseObject>> subqueries = new ArrayList<>();
        subqueries.add(new ParseQuery.State.Builder<>("TestObjectA"));
        subqueries.add(new ParseQuery.State.Builder<>("TestObjectB").orderByAscending("blah"));
        Builder.or(subqueries).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOrThrowsWithIncludes() {
        List<ParseQuery.State.Builder<ParseObject>> subqueries = new ArrayList<>();
        subqueries.add(new ParseQuery.State.Builder<>("TestObjectA"));
        subqueries.add(new ParseQuery.State.Builder<>("TestObjectB").include("blah"));
        Builder.or(subqueries).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOrThrowsWithSelectedKeys() {
        List<ParseQuery.State.Builder<ParseObject>> subqueries = new ArrayList<>();
        subqueries.add(new ParseQuery.State.Builder<>("TestObjectA"));
        subqueries.add(new ParseQuery.State.Builder<>("TestObjectB").selectKeys(singletonList("blah")));
        Builder.or(subqueries).build();
    }

    // endregion
    @Test
    public void testSubqueryToJSON() throws JSONException {
        ParseEncoder encoder = PointerEncoder.get();
        ParseQuery.State.Builder<ParseObject> builder = new ParseQuery.State.Builder<>("TestObject");
        JSONObject json = builder.build().toJSON(encoder);
        Assert.assertEquals("TestObject", json.getString("className"));
        Assert.assertEquals("{}", json.getString("where"));
        int count = 0;
        Iterator<String> i = json.keys();
        while (i.hasNext()) {
            i.next();
            count++;
        } 
        Assert.assertEquals(2, count);
        ParseQuery.State.Builder<ParseObject> subbuilder = new ParseQuery.State.Builder<>("TestObject");
        json = builder.whereEqualTo("foo", "bar").whereMatchesQuery("subquery", subbuilder).setLimit(12).setSkip(34).orderByAscending("foo").addDescendingOrder("bar").include("name").selectKeys(Arrays.asList("name", "blah")).setTracingEnabled(true).redirectClassNameForKey("what").build().toJSON(encoder);
        Assert.assertEquals("TestObject", json.getString("className"));
        JSONAssert.assertEquals(("{" + (("\"foo\":\"bar\"," + "\"subquery\":{\"$inQuery\":{\"className\":\"TestObject\",\"where\":{}}}") + "}")), json.getJSONObject("where"), NON_EXTENSIBLE);
        Assert.assertEquals(12, json.getInt("limit"));
        Assert.assertEquals(34, json.getInt("skip"));
        Assert.assertEquals("foo,-bar", json.getString("order"));
        Assert.assertEquals("name", json.getString("include"));
        Assert.assertEquals("name,blah", json.getString("fields"));
        Assert.assertEquals(1, json.getInt("trace"));
        Assert.assertEquals("what", json.getString("redirectClassNameForKey"));
    }
}

