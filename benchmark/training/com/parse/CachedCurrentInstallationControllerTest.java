/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import bolts.Task;
import bolts.TaskCompletionSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class CachedCurrentInstallationControllerTest {
    private static final String KEY_DEVICE_TYPE = "deviceType";

    // region testSetAsync
    @Test
    public void testSetAsyncWithNotCurrentInstallation() throws Exception {
        CachedCurrentInstallationController controller = new CachedCurrentInstallationController(null, null);
        ParseInstallation currentInstallationInMemory = Mockito.mock(ParseInstallation.class);
        controller.currentInstallation = currentInstallationInMemory;
        ParseInstallation testInstallation = Mockito.mock(ParseInstallation.class);
        ParseTaskUtils.wait(controller.setAsync(testInstallation));
        // Make sure the in memory currentInstallation not change
        Assert.assertSame(currentInstallationInMemory, controller.currentInstallation);
        Assert.assertNotSame(controller.currentInstallation, testInstallation);
    }

    @Test
    public void testSetAsyncWithCurrentInstallation() throws Exception {
        InstallationId installationId = Mockito.mock(InstallationId.class);
        // noinspection unchecked
        ParseObjectStore<ParseInstallation> store = Mockito.mock(ParseObjectStore.class);
        // Create test controller
        CachedCurrentInstallationController controller = new CachedCurrentInstallationController(store, installationId);
        ParseInstallation currentInstallation = Mockito.mock(ParseInstallation.class);
        Mockito.when(currentInstallation.getInstallationId()).thenReturn("testInstallationId");
        controller.currentInstallation = currentInstallation;
        ParseTaskUtils.wait(controller.setAsync(currentInstallation));
        // Verify that we persist it
        Mockito.verify(store, Mockito.times(1)).setAsync(currentInstallation);
        // Make sure installationId is updated
        Mockito.verify(installationId, Mockito.times(1)).set("testInstallationId");
    }

    // endregion
    // region testGetAsync
    @Test
    public void testGetAsyncFromMemory() throws Exception {
        // Create test controller
        CachedCurrentInstallationController controller = new CachedCurrentInstallationController(null, null);
        ParseInstallation currentInstallationInMemory = new ParseInstallation();
        controller.currentInstallation = currentInstallationInMemory;
        ParseInstallation currentInstallation = ParseTaskUtils.wait(controller.getAsync());
        Assert.assertSame(currentInstallationInMemory, currentInstallation);
    }

    @Test
    public void testGetAsyncFromStore() throws Exception {
        // Mock installationId
        InstallationId installationId = Mockito.mock(InstallationId.class);
        // noinspection unchecked
        ParseObjectStore<ParseInstallation> store = Mockito.mock(ParseObjectStore.class);
        ParseInstallation installation = Mockito.mock(ParseInstallation.class);
        Mockito.when(installation.getInstallationId()).thenReturn("testInstallationId");
        Mockito.when(store.getAsync()).thenReturn(Task.forResult(installation));
        // Create test controller
        CachedCurrentInstallationController controller = new CachedCurrentInstallationController(store, installationId);
        ParseInstallation currentInstallation = ParseTaskUtils.wait(controller.getAsync());
        Mockito.verify(store, Mockito.times(1)).getAsync();
        // Make sure installationId is updated
        Mockito.verify(installationId, Mockito.times(1)).set("testInstallationId");
        // Make sure controller state is update to date
        Assert.assertSame(installation, controller.currentInstallation);
        // Make sure the installation we get is correct
        Assert.assertSame(installation, currentInstallation);
    }

    @Test
    public void testGetAsyncWithNoInstallation() throws Exception {
        // Mock installationId
        InstallationId installationId = Mockito.mock(InstallationId.class);
        Mockito.when(installationId.get()).thenReturn("testInstallationId");
        // noinspection unchecked
        ParseObjectStore<ParseInstallation> store = Mockito.mock(ParseObjectStore.class);
        Mockito.when(store.getAsync()).thenReturn(Task.<ParseInstallation>forResult(null));
        // Create test controller
        CachedCurrentInstallationController controller = new CachedCurrentInstallationController(store, installationId);
        ParseInstallation currentInstallation = ParseTaskUtils.wait(controller.getAsync());
        Mockito.verify(store, Mockito.times(1)).getAsync();
        // Make sure controller state is update to date
        Assert.assertSame(controller.currentInstallation, currentInstallation);
        // Make sure device info is updated
        Assert.assertEquals("testInstallationId", currentInstallation.getInstallationId());
        Assert.assertEquals("android", currentInstallation.get(CachedCurrentInstallationControllerTest.KEY_DEVICE_TYPE));
    }

    @Test
    public void testGetAsyncWithNoInstallationRaceCondition() throws ParseException {
        // Mock installationId
        InstallationId installationId = Mockito.mock(InstallationId.class);
        Mockito.when(installationId.get()).thenReturn("testInstallationId");
        // noinspection unchecked
        ParseObjectStore<ParseInstallation> store = Mockito.mock(ParseObjectStore.class);
        TaskCompletionSource<ParseInstallation> tcs = new TaskCompletionSource();
        Mockito.when(store.getAsync()).thenReturn(tcs.getTask());
        // Create test controller
        CachedCurrentInstallationController controller = new CachedCurrentInstallationController(store, installationId);
        Task<ParseInstallation> taskA = controller.getAsync();
        Task<ParseInstallation> taskB = controller.getAsync();
        tcs.setResult(null);
        ParseInstallation installationA = ParseTaskUtils.wait(taskA);
        ParseInstallation installationB = ParseTaskUtils.wait(taskB);
        Mockito.verify(store, Mockito.times(1)).getAsync();
        Assert.assertSame(controller.currentInstallation, installationA);
        Assert.assertSame(controller.currentInstallation, installationB);
        // Make sure device info is updated
        Assert.assertEquals("testInstallationId", installationA.getInstallationId());
        Assert.assertEquals("android", installationA.get(CachedCurrentInstallationControllerTest.KEY_DEVICE_TYPE));
    }

    // endregion
    // region testExistsAsync
    @Test
    public void testExistAsyncFromMemory() throws Exception {
        // Create test controller
        CachedCurrentInstallationController controller = new CachedCurrentInstallationController(null, null);
        controller.currentInstallation = Mockito.mock(ParseInstallation.class);
        Assert.assertTrue(ParseTaskUtils.wait(controller.existsAsync()));
    }

    @Test
    public void testExistAsyncFromStore() throws Exception {
        // noinspection unchecked
        ParseObjectStore<ParseInstallation> store = Mockito.mock(ParseObjectStore.class);
        Mockito.when(store.existsAsync()).thenReturn(Task.forResult(true));
        // Create test controller
        CachedCurrentInstallationController controller = new CachedCurrentInstallationController(store, null);
        Assert.assertTrue(ParseTaskUtils.wait(controller.existsAsync()));
        Mockito.verify(store, Mockito.times(1)).existsAsync();
    }

    // endregion
    @Test
    public void testClearFromMemory() {
        CachedCurrentInstallationController controller = new CachedCurrentInstallationController(null, null);
        controller.currentInstallation = Mockito.mock(ParseInstallation.class);
        controller.clearFromMemory();
        Assert.assertNull(controller.currentInstallation);
    }

    @Test
    public void testClearFromDisk() {
        // Mock installationId
        InstallationId installationId = Mockito.mock(InstallationId.class);
        // noinspection unchecked
        ParseObjectStore<ParseInstallation> store = Mockito.mock(ParseObjectStore.class);
        Mockito.when(store.deleteAsync()).thenReturn(Task.<Void>forResult(null));
        CachedCurrentInstallationController controller = new CachedCurrentInstallationController(store, installationId);
        controller.currentInstallation = Mockito.mock(ParseInstallation.class);
        controller.clearFromDisk();
        Assert.assertNull(controller.currentInstallation);
        // Make sure the in LDS currentInstallation is cleared
        Mockito.verify(store, Mockito.times(1)).deleteAsync();
        // Make sure installationId is cleared
        Mockito.verify(installationId, Mockito.times(1)).clear();
    }

    @Test
    public void testIsCurrent() {
        // Create test controller
        CachedCurrentInstallationController controller = new CachedCurrentInstallationController(null, null);
        ParseInstallation installation = Mockito.mock(ParseInstallation.class);
        controller.currentInstallation = installation;
        Assert.assertTrue(controller.isCurrent(installation));
        Assert.assertFalse(controller.isCurrent(new ParseInstallation()));
    }
}

