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
import ParseHttpRequest.Method.GET;
import ParseRESTQueryCommand.KEY_COUNT;
import ParseRESTQueryCommand.KEY_INCLUDE;
import ParseRESTQueryCommand.KEY_KEYS;
import ParseRESTQueryCommand.KEY_LIMIT;
import ParseRESTQueryCommand.KEY_ORDER;
import ParseRESTQueryCommand.KEY_SKIP;
import ParseRESTQueryCommand.KEY_TRACE;
import ParseRESTQueryCommand.KEY_WHERE;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


// endregion
public class ParseRESTQueryCommandTest {
    // region testEncode
    @Test
    public void testEncodeWithNoCount() throws Exception {
        ParseQuery.State<ParseObject> state = new ParseQuery.State.Builder<>("TestObject").orderByAscending("orderKey").addCondition("inKey", "$in", Arrays.asList("inValue", "inValueAgain")).selectKeys(Collections.singletonList("selectedKey, selectedKeyAgain")).include("includeKey").setLimit(5).setSkip(6).redirectClassNameForKey("extraKey").setTracingEnabled(true).build();
        Map<String, String> encoded = ParseRESTQueryCommand.encode(state, false);
        Assert.assertEquals("orderKey", encoded.get(KEY_ORDER));
        JSONObject conditionJson = new JSONObject(encoded.get(KEY_WHERE));
        JSONArray conditionWhereJsonArray = new JSONArray().put("inValue").put("inValueAgain");
        Assert.assertEquals(conditionWhereJsonArray, conditionJson.getJSONObject("inKey").getJSONArray("$in"), NON_EXTENSIBLE);
        Assert.assertTrue(encoded.get(KEY_KEYS).contains("selectedKey"));
        Assert.assertTrue(encoded.get(KEY_KEYS).contains("selectedKeyAgain"));
        Assert.assertEquals("includeKey", encoded.get(KEY_INCLUDE));
        Assert.assertEquals("5", encoded.get(KEY_LIMIT));
        Assert.assertEquals("6", encoded.get(KEY_SKIP));
        Assert.assertEquals("extraKey", encoded.get("redirectClassNameForKey"));
        Assert.assertEquals("1", encoded.get(KEY_TRACE));
    }

    @Test
    public void testEncodeWithCount() {
        ParseQuery.State<ParseObject> state = new ParseQuery.State.Builder<>("TestObject").setSkip(6).setLimit(3).build();
        Map<String, String> encoded = ParseRESTQueryCommand.encode(state, true);
        // Limit should not be stripped out from count queries
        Assert.assertTrue(encoded.containsKey(KEY_LIMIT));
        Assert.assertFalse(encoded.containsKey(KEY_SKIP));
        Assert.assertEquals("1", encoded.get(KEY_COUNT));
    }

    // endregion
    // region testConstruct
    @Test
    public void testFindCommand() throws Exception {
        ParseQuery.State<ParseObject> state = new ParseQuery.State.Builder<>("TestObject").selectKeys(Arrays.asList("key", "kayAgain")).build();
        ParseRESTQueryCommand command = ParseRESTQueryCommand.findCommand(state, "sessionToken");
        Assert.assertEquals("classes/TestObject", command.httpPath);
        Assert.assertEquals(GET, command.method);
        Assert.assertEquals("sessionToken", command.getSessionToken());
        Map<String, String> parameters = ParseRESTQueryCommand.encode(state, false);
        JSONObject jsonParameters = ((JSONObject) (NoObjectsEncoder.get().encode(parameters)));
        Assert.assertEquals(jsonParameters, command.jsonParameters, NON_EXTENSIBLE);
    }

    @Test
    public void testCountCommand() throws Exception {
        ParseQuery.State<ParseObject> state = new ParseQuery.State.Builder<>("TestObject").selectKeys(Arrays.asList("key", "kayAgain")).build();
        ParseRESTQueryCommand command = ParseRESTQueryCommand.countCommand(state, "sessionToken");
        Assert.assertEquals("classes/TestObject", command.httpPath);
        Assert.assertEquals(GET, command.method);
        Assert.assertEquals("sessionToken", command.getSessionToken());
        Map<String, String> parameters = ParseRESTQueryCommand.encode(state, true);
        JSONObject jsonParameters = ((JSONObject) (NoObjectsEncoder.get().encode(parameters)));
        Assert.assertEquals(jsonParameters, command.jsonParameters, NON_EXTENSIBLE);
    }
}

