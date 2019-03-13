/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseRESTAnalyticsCommand.EVENT_APP_OPENED;
import bolts.Task;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// For android.net.Uri
// endregion
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseAnalyticsControllerTest {
    // region testConstructor
    @Test
    public void testConstructor() {
        ParseEventuallyQueue queue = Mockito.mock(ParseEventuallyQueue.class);
        ParseAnalyticsController controller = new ParseAnalyticsController(queue);
        Assert.assertSame(queue, controller.eventuallyQueue);
    }

    // endregion
    // region trackEventInBackground
    @Test
    public void testTrackEvent() throws Exception {
        // Mock eventually queue
        ParseEventuallyQueue queue = Mockito.mock(ParseEventuallyQueue.class);
        Mockito.when(queue.enqueueEventuallyAsync(ArgumentMatchers.any(ParseRESTCommand.class), ArgumentMatchers.any(ParseObject.class))).thenReturn(Task.forResult(new JSONObject()));
        // Execute
        ParseAnalyticsController controller = new ParseAnalyticsController(queue);
        Map<String, String> dimensions = new HashMap<>();
        dimensions.put("event", "close");
        ParseTaskUtils.wait(controller.trackEventInBackground("name", dimensions, "sessionToken"));
        // Verify eventuallyQueue.enqueueEventuallyAsync
        ArgumentCaptor<ParseRESTCommand> command = ArgumentCaptor.forClass(ParseRESTCommand.class);
        ArgumentCaptor<ParseObject> object = ArgumentCaptor.forClass(ParseObject.class);
        Mockito.verify(queue, Mockito.times(1)).enqueueEventuallyAsync(command.capture(), object.capture());
        // Verify eventuallyQueue.enqueueEventuallyAsync object parameter
        Assert.assertNull(object.getValue());
        // Verify eventuallyQueue.enqueueEventuallyAsync command parameter
        Assert.assertTrue(((command.getValue()) instanceof ParseRESTAnalyticsCommand));
        Assert.assertTrue(command.getValue().httpPath.contains("name"));
        Assert.assertEquals("sessionToken", command.getValue().getSessionToken());
        JSONObject jsonDimensions = command.getValue().jsonParameters.getJSONObject("dimensions");
        Assert.assertEquals("close", jsonDimensions.get("event"));
        Assert.assertEquals(1, jsonDimensions.length());
    }

    // endregion
    // region trackAppOpenedInBackground
    @Test
    public void testTrackAppOpened() throws Exception {
        // Mock eventually queue
        ParseEventuallyQueue queue = Mockito.mock(ParseEventuallyQueue.class);
        Mockito.when(queue.enqueueEventuallyAsync(ArgumentMatchers.any(ParseRESTCommand.class), ArgumentMatchers.any(ParseObject.class))).thenReturn(Task.forResult(new JSONObject()));
        // Execute
        ParseAnalyticsController controller = new ParseAnalyticsController(queue);
        ParseTaskUtils.wait(controller.trackAppOpenedInBackground("pushHash", "sessionToken"));
        // Verify eventuallyQueue.enqueueEventuallyAsync
        ArgumentCaptor<ParseRESTCommand> command = ArgumentCaptor.forClass(ParseRESTCommand.class);
        ArgumentCaptor<ParseObject> object = ArgumentCaptor.forClass(ParseObject.class);
        Mockito.verify(queue, Mockito.times(1)).enqueueEventuallyAsync(command.capture(), object.capture());
        // Verify eventuallyQueue.enqueueEventuallyAsync object parameter
        Assert.assertNull(object.getValue());
        // Verify eventuallyQueue.enqueueEventuallyAsync command parameter
        Assert.assertTrue(((command.getValue()) instanceof ParseRESTAnalyticsCommand));
        Assert.assertTrue(command.getValue().httpPath.contains(EVENT_APP_OPENED));
        Assert.assertEquals("sessionToken", command.getValue().getSessionToken());
        Assert.assertEquals("pushHash", command.getValue().jsonParameters.get("push_hash"));
    }
}

