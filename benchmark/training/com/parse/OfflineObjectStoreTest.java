/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseQuery.State;
import bolts.Task;
import java.util.Arrays;
import java.util.Collections;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


// endregion
public class OfflineObjectStoreTest {
    private static final String PIN_NAME = "test";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSetAsync() throws Exception {
        OfflineStore lds = Mockito.mock(OfflineStore.class);
        Mockito.when(lds.unpinAllObjectsAsync(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        Mockito.when(lds.pinAllObjectsAsync(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(null));
        Parse.setLocalDatastore(lds);
        OfflineObjectStore<ParseUser> store = new OfflineObjectStore(ParseUser.class, OfflineObjectStoreTest.PIN_NAME, null);
        ParseUser user = Mockito.mock(ParseUser.class);
        ParseTaskUtils.wait(store.setAsync(user));
        Mockito.verify(lds, Mockito.times(1)).unpinAllObjectsAsync(OfflineObjectStoreTest.PIN_NAME);
        Mockito.verify(user, Mockito.times(1)).pinInBackground(OfflineObjectStoreTest.PIN_NAME, false);
    }

    // region getAsync
    @Test
    public void testGetAsyncFromLDS() throws Exception {
        Parse.enableLocalDatastore(null);
        ParseUser user = Mockito.mock(ParseUser.class);
        ParseQueryController queryController = Mockito.mock(ParseQueryController.class);
        // noinspection unchecked
        Mockito.when(queryController.findAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseUser.class), ArgumentMatchers.any(Task.class))).thenReturn(Task.forResult(Collections.singletonList(user)));
        ParseCorePlugins.getInstance().registerQueryController(queryController);
        OfflineObjectStore<ParseUser> store = new OfflineObjectStore(ParseUser.class, OfflineObjectStoreTest.PIN_NAME, null);
        ParseUser userAgain = ParseTaskUtils.wait(store.getAsync());
        // noinspection unchecked
        Mockito.verify(queryController, Mockito.times(1)).findAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseUser.class), ArgumentMatchers.any(Task.class));
        Assert.assertSame(user, userAgain);
    }

    @Test
    public void testGetAsyncFromLDSWithTooManyObjects() throws Exception {
        Parse.enableLocalDatastore(null);
        ParseQueryController queryController = Mockito.mock(ParseQueryController.class);
        // noinspection unchecked
        Mockito.when(queryController.findAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseUser.class), ArgumentMatchers.any(Task.class))).thenReturn(Task.forResult(Arrays.asList(Mockito.mock(ParseUser.class), Mockito.mock(ParseUser.class))));
        ParseCorePlugins.getInstance().registerQueryController(queryController);
        OfflineStore lds = Mockito.mock(OfflineStore.class);
        Mockito.when(lds.unpinAllObjectsAsync(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        Parse.setLocalDatastore(lds);
        @SuppressWarnings("unchecked")
        ParseObjectStore<ParseUser> legacy = Mockito.mock(ParseObjectStore.class);
        Mockito.when(legacy.getAsync()).thenReturn(Task.<ParseUser>forResult(null));
        OfflineObjectStore<ParseUser> store = new OfflineObjectStore(ParseUser.class, OfflineObjectStoreTest.PIN_NAME, legacy);
        ParseUser user = ParseTaskUtils.wait(store.getAsync());
        // noinspection unchecked
        Mockito.verify(queryController, Mockito.times(1)).findAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseUser.class), ArgumentMatchers.any(Task.class));
        Mockito.verify(lds, Mockito.times(1)).unpinAllObjectsAsync(OfflineObjectStoreTest.PIN_NAME);
        assertNull(user);
    }

    @Test
    public void testGetAsyncMigrate() throws Exception {
        Parse.enableLocalDatastore(null);
        ParseQueryController queryController = Mockito.mock(ParseQueryController.class);
        // noinspection unchecked
        Mockito.when(queryController.findAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseUser.class), ArgumentMatchers.any(Task.class))).thenReturn(Task.forResult(null));
        ParseCorePlugins.getInstance().registerQueryController(queryController);
        OfflineStore lds = Mockito.mock(OfflineStore.class);
        Mockito.when(lds.pinAllObjectsAsync(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), ArgumentMatchers.anyBoolean())).thenReturn(Task.forResult(null));
        Mockito.when(lds.unpinAllObjectsAsync(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        Mockito.when(lds.pinAllObjectsAsync(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), ArgumentMatchers.anyBoolean())).thenReturn(Task.<Void>forResult(null));
        Parse.setLocalDatastore(lds);
        ParseUser user = Mockito.mock(ParseUser.class);
        Mockito.when(user.pinInBackground(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(Task.<Void>forResult(null));
        @SuppressWarnings("unchecked")
        ParseObjectStore<ParseUser> legacy = Mockito.mock(ParseObjectStore.class);
        Mockito.when(legacy.getAsync()).thenReturn(Task.forResult(user));
        Mockito.when(legacy.deleteAsync()).thenReturn(Task.<Void>forResult(null));
        OfflineObjectStore<ParseUser> store = new OfflineObjectStore(ParseUser.class, OfflineObjectStoreTest.PIN_NAME, legacy);
        ParseUser userAgain = ParseTaskUtils.wait(store.getAsync());
        // noinspection unchecked
        Mockito.verify(queryController, Mockito.times(1)).findAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseUser.class), ArgumentMatchers.any(Task.class));
        Mockito.verify(legacy, Mockito.times(1)).getAsync();
        Mockito.verify(legacy, Mockito.times(1)).deleteAsync();
        Mockito.verify(lds, Mockito.times(1)).unpinAllObjectsAsync(OfflineObjectStoreTest.PIN_NAME);
        Mockito.verify(user, Mockito.times(1)).pinInBackground(OfflineObjectStoreTest.PIN_NAME, false);
        Assert.assertNotNull(userAgain);
        Assert.assertSame(user, userAgain);
    }

    // endregion
    // region existsAsync
    @Test
    public void testExistsAsyncLDS() throws Exception {
        Parse.enableLocalDatastore(null);
        ParseQueryController queryController = Mockito.mock(ParseQueryController.class);
        // noinspection unchecked
        Mockito.when(queryController.countAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseUser.class), ArgumentMatchers.any(Task.class))).thenReturn(Task.forResult(1));
        ParseCorePlugins.getInstance().registerQueryController(queryController);
        OfflineObjectStore<ParseUser> store = new OfflineObjectStore(ParseUser.class, OfflineObjectStoreTest.PIN_NAME, null);
        assertTrue(ParseTaskUtils.wait(store.existsAsync()));
        // noinspection unchecked
        Mockito.verify(queryController, Mockito.times(1)).countAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseUser.class), ArgumentMatchers.any(Task.class));
    }

    @Test
    public void testExistsAsyncLegacy() throws Exception {
        Parse.enableLocalDatastore(null);
        ParseQueryController queryController = Mockito.mock(ParseQueryController.class);
        // noinspection unchecked
        Mockito.when(queryController.countAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseUser.class), ArgumentMatchers.any(Task.class))).thenReturn(Task.forResult(0));
        ParseCorePlugins.getInstance().registerQueryController(queryController);
        @SuppressWarnings("unchecked")
        ParseObjectStore<ParseUser> legacy = Mockito.mock(ParseObjectStore.class);
        Mockito.when(legacy.existsAsync()).thenReturn(Task.forResult(true));
        OfflineObjectStore<ParseUser> store = new OfflineObjectStore(ParseUser.class, OfflineObjectStoreTest.PIN_NAME, legacy);
        assertTrue(ParseTaskUtils.wait(store.existsAsync()));
        // noinspection unchecked
        Mockito.verify(queryController, Mockito.times(1)).countAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseUser.class), ArgumentMatchers.any(Task.class));
    }

    // endregion
    // region deleteAsync
    @Test
    public void testDeleteAsync() throws Exception {
        OfflineStore lds = Mockito.mock(OfflineStore.class);
        Mockito.when(lds.unpinAllObjectsAsync(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        Parse.setLocalDatastore(lds);
        @SuppressWarnings("unchecked")
        ParseObjectStore<ParseUser> legacy = Mockito.mock(ParseObjectStore.class);
        Mockito.when(legacy.deleteAsync()).thenReturn(Task.<Void>forResult(null));
        OfflineObjectStore<ParseUser> store = new OfflineObjectStore(ParseUser.class, OfflineObjectStoreTest.PIN_NAME, legacy);
        ParseTaskUtils.wait(store.deleteAsync());
        Mockito.verify(legacy, Mockito.times(1)).deleteAsync();
        Mockito.verify(lds, Mockito.times(1)).unpinAllObjectsAsync(OfflineObjectStoreTest.PIN_NAME);
    }

    @Test
    public void testDeleteAsyncFailure() throws Exception {
        OfflineStore lds = Mockito.mock(OfflineStore.class);
        Mockito.when(lds.unpinAllObjectsAsync(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forError(new RuntimeException("failure")));
        Parse.setLocalDatastore(lds);
        @SuppressWarnings("unchecked")
        ParseObjectStore<ParseUser> legacy = Mockito.mock(ParseObjectStore.class);
        Mockito.when(legacy.deleteAsync()).thenReturn(Task.<Void>forResult(null));
        OfflineObjectStore<ParseUser> store = new OfflineObjectStore(ParseUser.class, OfflineObjectStoreTest.PIN_NAME, legacy);
        thrown.expect(RuntimeException.class);
        ParseTaskUtils.wait(store.deleteAsync());
        Mockito.verify(legacy, Mockito.times(1)).deleteAsync();
    }
}

