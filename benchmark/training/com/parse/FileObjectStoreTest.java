/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import JSONCompareMode.STRICT;
import ParseUser.State;
import ParseUser.State.Builder;
import java.io.File;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FileObjectStoreTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testSetAsync() throws Exception {
        File file = new File(temporaryFolder.getRoot(), "test");
        ParseUser.State state = Mockito.mock(State.class);
        JSONObject json = new JSONObject();
        json.put("foo", "bar");
        ParseUserCurrentCoder coder = Mockito.mock(ParseUserCurrentCoder.class);
        Mockito.when(coder.encode(ArgumentMatchers.eq(state), ((ParseOperationSet) (ArgumentMatchers.isNull())), ArgumentMatchers.any(PointerEncoder.class))).thenReturn(json);
        FileObjectStore<ParseUser> store = new FileObjectStore(ParseUser.class, file, coder);
        ParseUser user = Mockito.mock(ParseUser.class);
        Mockito.when(user.getState()).thenReturn(state);
        ParseTaskUtils.wait(store.setAsync(user));
        JSONObject jsonAgain = ParseFileUtils.readFileToJSONObject(file);
        Assert.assertEquals(json, jsonAgain, STRICT);
    }

    @Test
    public void testGetAsync() throws Exception {
        File file = new File(temporaryFolder.getRoot(), "test");
        JSONObject json = new JSONObject();
        ParseFileUtils.writeJSONObjectToFile(file, json);
        ParseUser.State.Builder builder = new ParseUser.State.Builder();
        builder.put("foo", "bar");
        ParseUserCurrentCoder coder = Mockito.mock(ParseUserCurrentCoder.class);
        Mockito.when(coder.decode(ArgumentMatchers.any(Builder.class), ArgumentMatchers.any(JSONObject.class), ArgumentMatchers.any(ParseDecoder.class))).thenReturn(builder);
        FileObjectStore<ParseUser> store = new FileObjectStore(ParseUser.class, file, coder);
        ParseUser user = ParseTaskUtils.wait(store.getAsync());
        Assert.assertEquals("bar", user.getState().get("foo"));
    }

    @Test
    public void testExistsAsync() throws Exception {
        File file = temporaryFolder.newFile("test");
        FileObjectStore<ParseUser> store = new FileObjectStore(ParseUser.class, file, null);
        Assert.assertTrue(ParseTaskUtils.wait(store.existsAsync()));
        temporaryFolder.delete();
        Assert.assertFalse(ParseTaskUtils.wait(store.existsAsync()));
    }

    @Test
    public void testDeleteAsync() throws Exception {
        File file = temporaryFolder.newFile("test");
        FileObjectStore<ParseUser> store = new FileObjectStore(ParseUser.class, file, null);
        Assert.assertTrue(file.exists());
        ParseTaskUtils.wait(store.deleteAsync());
        Assert.assertFalse(file.exists());
    }
}

