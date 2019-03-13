/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseObject.State;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import bolts.Task;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static ParseException.MISSING_REQUIRED_FIELD_ERROR;
import static ParseException.OBJECT_NOT_FOUND;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseInstallationTest extends ResetPluginsParseTest {
    private static final String KEY_INSTALLATION_ID = "installationId";

    private static final String KEY_DEVICE_TYPE = "deviceType";

    private static final String KEY_APP_NAME = "appName";

    private static final String KEY_APP_IDENTIFIER = "appIdentifier";

    private static final String KEY_TIME_ZONE = "timeZone";

    private static final String KEY_LOCALE_IDENTIFIER = "localeIdentifier";

    private static final String KEY_APP_VERSION = "appVersion";

    private Locale defaultLocale;

    @Test
    public void testImmutableKeys() {
        String[] immutableKeys = new String[]{ "installationId", "deviceType", "appName", "appIdentifier", "parseVersion", "deviceToken", "deviceTokenLastModified", "pushType", "timeZone", "localeIdentifier", "appVersion" };
        ParseInstallation installation = new ParseInstallation();
        installation.put("foo", "bar");
        for (String immutableKey : immutableKeys) {
            try {
                installation.put(immutableKey, "blah");
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("Cannot modify"));
            }
            try {
                installation.remove(immutableKey);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("Cannot modify"));
            }
            try {
                installation.removeAll(immutableKey, Collections.emptyList());
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("Cannot modify"));
            }
        }
    }

    @Test(expected = RuntimeException.class)
    public void testInstallationObjectIdCannotBeChanged() {
        boolean hasException = false;
        ParseInstallation installation = new ParseInstallation();
        try {
            installation.put("objectId", "abc");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Cannot modify"));
            hasException = true;
        }
        Assert.assertTrue(hasException);
        installation.setObjectId("abc");
    }

    @Test
    public void testMissingRequiredFieldWhenSaveAsync() throws Exception {
        String sessionToken = "sessionToken";
        Task<Void> toAwait = Task.forResult(null);
        ParseCurrentInstallationController controller = mockCurrentInstallationController();
        ParseObjectController objController = Mockito.mock(ParseObjectController.class);
        // mock return task when Installation was deleted on the server
        Task<ParseObject.State> taskError = Task.forError(new ParseException(MISSING_REQUIRED_FIELD_ERROR, ""));
        // mock return task when Installation was re-saved to the server
        Task<ParseObject.State> task = Task.forResult(null);
        Mockito.when(objController.saveAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseOperationSet.class), ArgumentMatchers.eq(sessionToken), ArgumentMatchers.any(ParseDecoder.class))).thenReturn(taskError).thenReturn(task);
        ParseCorePlugins.getInstance().registerObjectController(objController);
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        Assert.assertNotNull(installation);
        installation.put("key", "value");
        ParseTaskUtils.wait(installation.saveAsync(sessionToken, toAwait));
        Mockito.verify(controller).getAsync();
        Mockito.verify(objController, Mockito.times(2)).saveAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseOperationSet.class), ArgumentMatchers.eq(sessionToken), ArgumentMatchers.any(ParseDecoder.class));
    }

    @Test
    public void testObjectNotFoundWhenSaveAsync() throws Exception {
        OfflineStore lds = new OfflineStore(RuntimeEnvironment.application);
        Parse.setLocalDatastore(lds);
        String sessionToken = "sessionToken";
        Task<Void> toAwait = Task.forResult(null);
        ParseCurrentInstallationController controller = mockCurrentInstallationController();
        ParseObjectController objController = Mockito.mock(ParseObjectController.class);
        // mock return task when Installation was deleted on the server
        Task<ParseObject.State> taskError = Task.forError(new ParseException(OBJECT_NOT_FOUND, ""));
        // mock return task when Installation was re-saved to the server
        Task<ParseObject.State> task = Task.forResult(null);
        Mockito.when(objController.saveAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseOperationSet.class), ArgumentMatchers.eq(sessionToken), ArgumentMatchers.any(ParseDecoder.class))).thenReturn(taskError).thenReturn(task);
        ParseCorePlugins.getInstance().registerObjectController(objController);
        ParseObject.State state = new ParseObject.State.Builder("_Installation").objectId("oldId").put("deviceToken", "deviceToken").build();
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        Assert.assertNotNull(installation);
        installation.setState(state);
        installation.put("key", "value");
        ParseTaskUtils.wait(installation.saveAsync(sessionToken, toAwait));
        Mockito.verify(controller).getAsync();
        Mockito.verify(objController, Mockito.times(2)).saveAsync(ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseOperationSet.class), ArgumentMatchers.eq(sessionToken), ArgumentMatchers.any(ParseDecoder.class));
        Parse.setLocalDatastore(null);
    }

    @Test
    public void testHandleSaveResultAsync() throws Exception {
        // Mock currentInstallationController to make setAsync work
        ParseCurrentInstallationController controller = Mockito.mock(ParseCurrentInstallationController.class);
        Mockito.when(controller.setAsync(ArgumentMatchers.any(ParseInstallation.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentInstallationController(controller);
        // Mock return state
        ParseInstallation.State state = new ParseInstallation.State.Builder("_Installation").put("key", "value").build();
        ParseInstallation installation = new ParseInstallation();
        installation.put("keyAgain", "valueAgain");
        ParseOperationSet operationSet = installation.startSave();
        ParseTaskUtils.wait(installation.handleSaveResultAsync(state, operationSet));
        // Make sure the installation data is correct
        Assert.assertEquals("value", installation.get("key"));
        Assert.assertEquals("valueAgain", installation.get("keyAgain"));
        // Make sure we set the currentInstallation
        Mockito.verify(controller, Mockito.times(1)).setAsync(installation);
    }

    @Test
    public void testHandleFetchResultAsync() throws Exception {
        // Mock currentInstallationController to make setAsync work
        ParseCurrentInstallationController controller = Mockito.mock(ParseCurrentInstallationController.class);
        Mockito.when(controller.setAsync(ArgumentMatchers.any(ParseInstallation.class))).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerCurrentInstallationController(controller);
        // Mock return state
        ParseInstallation.State state = new ParseInstallation.State.Builder("_Installation").put("key", "value").isComplete(true).build();
        ParseInstallation installation = new ParseInstallation();
        ParseTaskUtils.wait(installation.handleFetchResultAsync(state));
        // Make sure the installation data is correct
        Assert.assertEquals("value", installation.get("key"));
        // Make sure we set the currentInstallation
        Mockito.verify(controller, Mockito.times(1)).setAsync(installation);
    }

    // TODO(mengyan): Add other testUpdateBeforeSave cases to cover all branches
    @Test
    public void testUpdateBeforeSave() throws Exception {
        ParseInstallationTest.mocksForUpdateBeforeSave();
        Locale.setDefault(new Locale("en", "US"));
        ParseInstallation installation = new ParseInstallation();
        installation.updateBeforeSave();
        // Make sure we update timezone
        String zone = installation.getString(ParseInstallationTest.KEY_TIME_ZONE);
        String deviceZone = TimeZone.getDefault().getID();
        if (zone != null) {
            Assert.assertEquals(zone, deviceZone);
        } else {
            // If it's not updated it's because it was not acceptable.
            Assert.assertFalse(deviceZone.equals("GMT"));
            Assert.assertFalse(((deviceZone.indexOf("/")) > 0));
        }
        // Make sure we update version info
        Context context = Parse.getApplicationContext();
        String packageName = context.getPackageName();
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageInfo(packageName, 0);
        String appVersion = pkgInfo.versionName;
        String appName = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString();
        Assert.assertEquals(packageName, installation.getString(ParseInstallationTest.KEY_APP_IDENTIFIER));
        Assert.assertEquals(appName, installation.getString(ParseInstallationTest.KEY_APP_NAME));
        Assert.assertEquals(appVersion, installation.getString(ParseInstallationTest.KEY_APP_VERSION));
        // Make sure we update device info
        Assert.assertEquals("android", installation.getString(ParseInstallationTest.KEY_DEVICE_TYPE));
        Assert.assertEquals("installationId", installation.getString(ParseInstallationTest.KEY_INSTALLATION_ID));
        // Make sure we update the locale identifier
        Assert.assertEquals("en-US", installation.getString(ParseInstallationTest.KEY_LOCALE_IDENTIFIER));
    }

    @Test
    public void testDeviceToken() {
        ParseInstallation installation = new ParseInstallation();
        installation.setDeviceToken("deviceToken");
        Assert.assertEquals("deviceToken", installation.getDeviceToken());
        installation.removeDeviceToken();
        Assert.assertNull(installation.getDeviceToken());
        // Make sure we add the pushType to operationSetQueue instead of serverData
        Assert.assertEquals(1, installation.operationSetQueue.getLast().size());
    }

    @Test
    public void testDeviceTokenWithNullDeviceToken() {
        ParseInstallation installation = new ParseInstallation();
        installation.setDeviceToken("deviceToken");
        Assert.assertEquals("deviceToken", installation.getDeviceToken());
        installation.setDeviceToken(null);
        Assert.assertEquals("deviceToken", installation.getDeviceToken());
    }

    @Test
    public void testGetCurrentInstallation() {
        // Mock currentInstallationController to make setAsync work
        ParseCurrentInstallationController controller = Mockito.mock(ParseCurrentInstallationController.class);
        ParseInstallation currentInstallation = new ParseInstallation();
        Mockito.when(controller.getAsync()).thenReturn(Task.forResult(currentInstallation));
        ParseCorePlugins.getInstance().registerCurrentInstallationController(controller);
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        Assert.assertEquals(currentInstallation, installation);
        Mockito.verify(controller, Mockito.times(1)).getAsync();
    }

    // TODO(mengyan): Add testFetchAsync, right now we can not test super methods inside
    // testFetchAsync
    @Test
    public void testLocaleIdentifierSpecialCases() {
        ParseInstallationTest.mocksForUpdateBeforeSave();
        ParseInstallation installation = new ParseInstallation();
        // Deprecated two-letter codes (Java issue).
        Locale.setDefault(new Locale("iw", "US"));
        installation.updateBeforeSave();
        Assert.assertEquals("he-US", installation.getString(ParseInstallationTest.KEY_LOCALE_IDENTIFIER));
        Locale.setDefault(new Locale("in", "US"));
        installation.updateBeforeSave();
        Assert.assertEquals("id-US", installation.getString(ParseInstallationTest.KEY_LOCALE_IDENTIFIER));
        Locale.setDefault(new Locale("ji", "US"));
        installation.updateBeforeSave();
        Assert.assertEquals("yi-US", installation.getString(ParseInstallationTest.KEY_LOCALE_IDENTIFIER));
        // No country code.
        Locale.setDefault(new Locale("en"));
        installation.updateBeforeSave();
        Assert.assertEquals("en", installation.getString(ParseInstallationTest.KEY_LOCALE_IDENTIFIER));
    }
}

