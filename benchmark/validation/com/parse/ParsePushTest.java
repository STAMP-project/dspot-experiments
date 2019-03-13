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
import ParsePush.KEY_DATA_MESSAGE;
import ParsePush.State;
import bolts.Capture;
import bolts.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static ParseException.OTHER_CAUSE;


// endregion
// TODO(mengyan): Add testSetEnable after we test PushRouter and PushService
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParsePushTest {
    // region testSetChannel
    // We only test a basic case here to make sure logic in ParsePush is correct, more comprehensive
    // builder test cases should be in ParsePushState test
    @Test
    public void testSetChannel() {
        ParsePush push = new ParsePush();
        push.setChannel("test");
        // Right now it is hard for us to test a builder, so we build a state to test the builder is
        // set correctly
        // We have to set message otherwise build() will throw an exception
        push.setMessage("message");
        ParsePush.State state = push.builder.build();
        Assert.assertEquals(1, state.channelSet().size());
        Assert.assertTrue(state.channelSet().contains("test"));
    }

    // endregion
    // region testSetChannels
    // We only test a basic case here to make sure logic in ParsePush is correct, more comprehensive
    // builder test cases should be in ParsePushState test
    @Test
    public void testSetChannels() {
        ParsePush push = new ParsePush();
        List<String> channels = new ArrayList<>();
        channels.add("test");
        channels.add("testAgain");
        push.setChannels(channels);
        // Right now it is hard for us to test a builder, so we build a state to test the builder is
        // set correctly
        // We have to set message otherwise build() will throw an exception
        push.setMessage("message");
        ParsePush.State state = push.builder.build();
        Assert.assertEquals(2, state.channelSet().size());
        Assert.assertTrue(state.channelSet().contains("test"));
        Assert.assertTrue(state.channelSet().contains("testAgain"));
    }

    // endregion
    // region testSetData
    // We only test a basic case here to make sure logic in ParsePush is correct, more comprehensive
    // builder test cases should be in ParsePushState test
    @Test
    public void testSetData() throws Exception {
        ParsePush push = new ParsePush();
        JSONObject data = new JSONObject();
        data.put("key", "value");
        data.put("keyAgain", "valueAgain");
        push.setData(data);
        // Right now it is hard for us to test a builder, so we build a state to test the builder is
        // set correctly
        ParsePush.State state = push.builder.build();
        Assert.assertEquals(data, state.data(), NON_EXTENSIBLE);
    }

    // endregion
    // region testSetData
    // We only test a basic case here to make sure logic in ParsePush is correct, more comprehensive
    // builder test cases should be in ParsePushState test
    @Test
    public void testSetMessage() throws Exception {
        ParsePush push = new ParsePush();
        push.setMessage("test");
        // Right now it is hard for us to test a builder, so we build a state to test the builder is
        // set correctly
        ParsePush.State state = push.builder.build();
        JSONObject data = state.data();
        Assert.assertEquals("test", data.getString(KEY_DATA_MESSAGE));
    }

    // endregion
    // region testSetExpirationTime
    // We only test a basic case here to make sure logic in ParsePush is correct, more comprehensive
    // builder test cases should be in ParsePushState test
    @Test
    public void testSetExpirationTime() {
        ParsePush push = new ParsePush();
        push.setExpirationTime(10000);
        // Right now it is hard for us to test a builder, so we build a state to test the builder is
        // set correctly
        // We have to set message otherwise build() will throw an exception
        push.setMessage("message");
        ParsePush.State state = push.builder.build();
        Assert.assertEquals(10000, state.expirationTime().longValue());
    }

    // endregion
    // region testSetExpirationTimeInterval
    // We only test a basic case here to make sure logic in ParsePush is correct, more comprehensive
    // builder test cases should be in ParsePushState test
    @Test
    public void testSetExpirationTimeInterval() {
        ParsePush push = new ParsePush();
        push.setExpirationTimeInterval(10000);
        // Right now it is hard for us to test a builder, so we build a state to test the builder is
        // set correctly
        // We have to set message otherwise build() will throw an exception
        push.setMessage("message");
        ParsePush.State state = push.builder.build();
        Assert.assertEquals(10000, state.expirationTimeInterval().longValue());
    }

    // endregion
    // region testClearExpiration
    @Test
    public void testClearExpiration() {
        ParsePush push = new ParsePush();
        push.setExpirationTimeInterval(10000);
        // Right now it is hard for us to test a builder, so we build a state to test the builder is
        // set correctly
        // We have to set message otherwise build() will throw an exception
        push.setMessage("message");
        // Make sure interval has value before clear
        ParsePush.State state = push.builder.build();
        Assert.assertEquals(10000, state.expirationTimeInterval().longValue());
        // Make sure interval is empty after clear
        push.clearExpiration();
        state = push.builder.build();
        Assert.assertNull(state.expirationTimeInterval());
        push.setExpirationTime(200);
        // Make sure expiration time has value before clear
        state = push.builder.build();
        Assert.assertEquals(200, state.expirationTime().longValue());
        // Make sure interval is empty after clear
        push.clearExpiration();
        state = push.builder.build();
        Assert.assertNull(state.expirationTime());
    }

    // endregion
    // region testSetPushTime
    // We only test a basic case here to make sure logic in ParsePush is correct, more comprehensive
    // builder test cases should be in ParsePushState test
    @Test
    public void testSetPushTime() {
        ParsePush push = new ParsePush();
        long time = ((System.currentTimeMillis()) / 1000) + 1000;
        push.setPushTime(time);
        // Right now it is hard for us to test a builder, so we build a state to test the builder is
        // set correctly
        // We have to set message otherwise build() will throw an exception
        push.setMessage("message");
        ParsePush.State state = push.builder.build();
        Assert.assertEquals(time, state.pushTime().longValue());
    }

    // endregion
    // region testSetQuery
    // We only test a basic case here to make sure logic in ParsePush is correct, more comprehensive
    // builder test cases should be in ParsePushState test
    @Test
    public void testSetQuery() throws Exception {
        ParsePush push = new ParsePush();
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.getBuilder().whereEqualTo("foo", "bar");
        push.setQuery(query);
        // Right now it is hard for us to test a builder, so we build a state to test the builder is
        // set correctly
        // We have to set message otherwise build() will throw an exception
        push.setMessage("message");
        ParsePush.State state = push.builder.build();
        ParseQuery.State<ParseInstallation> queryState = state.queryState();
        JSONObject queryStateJson = queryState.toJSON(PointerEncoder.get());
        Assert.assertEquals("bar", queryStateJson.getJSONObject("where").getString("foo"));
    }

    // endregion
    // region testSubscribeInBackground
    @Test
    public void testSubscribeInBackgroundSuccess() throws Exception {
        ParsePushChannelsController controller = Mockito.mock(ParsePushChannelsController.class);
        Mockito.when(controller.subscribeInBackground(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);
        ParseTaskUtils.wait(ParsePush.subscribeInBackground("test"));
        Mockito.verify(controller, Mockito.times(1)).subscribeInBackground("test");
    }

    @Test
    public void testSubscribeInBackgroundWithCallbackSuccess() throws Exception {
        final ParsePushChannelsController controller = Mockito.mock(ParsePushChannelsController.class);
        Mockito.when(controller.subscribeInBackground(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);
        ParsePush push = new ParsePush();
        final Semaphore done = new Semaphore(0);
        final Capture<Exception> exceptionCapture = new Capture();
        ParsePush.subscribeInBackground("test", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                exceptionCapture.set(e);
                done.release();
            }
        });
        Assert.assertNull(exceptionCapture.get());
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        Mockito.verify(controller, Mockito.times(1)).subscribeInBackground("test");
    }

    @Test
    public void testSubscribeInBackgroundFail() throws Exception {
        ParsePushChannelsController controller = Mockito.mock(ParsePushChannelsController.class);
        ParseException exception = new ParseException(OTHER_CAUSE, "error");
        Mockito.when(controller.subscribeInBackground(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forError(exception));
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);
        Task<Void> pushTask = ParsePush.subscribeInBackground("test");
        pushTask.waitForCompletion();
        Mockito.verify(controller, Mockito.times(1)).subscribeInBackground("test");
        Assert.assertTrue(pushTask.isFaulted());
        Assert.assertSame(exception, pushTask.getError());
    }

    @Test
    public void testSubscribeInBackgroundWithCallbackFail() throws Exception {
        ParsePushChannelsController controller = Mockito.mock(ParsePushChannelsController.class);
        final ParseException exception = new ParseException(OTHER_CAUSE, "error");
        Mockito.when(controller.subscribeInBackground(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forError(exception));
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);
        ParsePush push = new ParsePush();
        final Semaphore done = new Semaphore(0);
        final Capture<Exception> exceptionCapture = new Capture();
        ParsePush.subscribeInBackground("test", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                exceptionCapture.set(e);
                done.release();
            }
        });
        Assert.assertSame(exception, exceptionCapture.get());
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        Mockito.verify(controller, Mockito.times(1)).subscribeInBackground("test");
    }

    // endregion
    // region testUnsubscribeInBackground
    @Test
    public void testUnsubscribeInBackgroundSuccess() throws Exception {
        ParsePushChannelsController controller = Mockito.mock(ParsePushChannelsController.class);
        Mockito.when(controller.unsubscribeInBackground(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);
        ParseTaskUtils.wait(ParsePush.unsubscribeInBackground("test"));
        Mockito.verify(controller, Mockito.times(1)).unsubscribeInBackground("test");
    }

    @Test
    public void testUnsubscribeInBackgroundWithCallbackSuccess() throws Exception {
        final ParsePushChannelsController controller = Mockito.mock(ParsePushChannelsController.class);
        Mockito.when(controller.unsubscribeInBackground(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);
        final Semaphore done = new Semaphore(0);
        final Capture<Exception> exceptionCapture = new Capture();
        ParsePush.unsubscribeInBackground("test", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                exceptionCapture.set(e);
                done.release();
            }
        });
        Assert.assertNull(exceptionCapture.get());
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        Mockito.verify(controller, Mockito.times(1)).unsubscribeInBackground("test");
    }

    @Test
    public void testUnsubscribeInBackgroundFail() throws Exception {
        ParsePushChannelsController controller = Mockito.mock(ParsePushChannelsController.class);
        ParseException exception = new ParseException(OTHER_CAUSE, "error");
        Mockito.when(controller.unsubscribeInBackground(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forError(exception));
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);
        Task<Void> pushTask = ParsePush.unsubscribeInBackground("test");
        pushTask.waitForCompletion();
        Mockito.verify(controller, Mockito.times(1)).unsubscribeInBackground("test");
        Assert.assertTrue(pushTask.isFaulted());
        Assert.assertSame(exception, pushTask.getError());
    }

    @Test
    public void testUnsubscribeInBackgroundWithCallbackFail() throws Exception {
        ParsePushChannelsController controller = Mockito.mock(ParsePushChannelsController.class);
        final ParseException exception = new ParseException(OTHER_CAUSE, "error");
        Mockito.when(controller.unsubscribeInBackground(ArgumentMatchers.anyString())).thenReturn(Task.<Void>forError(exception));
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);
        ParsePush push = new ParsePush();
        final Semaphore done = new Semaphore(0);
        final Capture<Exception> exceptionCapture = new Capture();
        ParsePush.unsubscribeInBackground("test", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                exceptionCapture.set(e);
                done.release();
            }
        });
        Assert.assertSame(exception, exceptionCapture.get());
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        Mockito.verify(controller, Mockito.times(1)).unsubscribeInBackground("test");
    }

    // endregion
    // region testGetPushChannelsController
    @Test
    public void testGetPushChannelsController() {
        ParsePushChannelsController controller = Mockito.mock(ParsePushChannelsController.class);
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);
        Assert.assertSame(controller, ParsePush.getPushChannelsController());
    }

    // endregion
    // region testGetPushController
    @Test
    public void testGetPushController() {
        ParsePushController controller = Mockito.mock(ParsePushController.class);
        ParseCorePlugins.getInstance().registerPushController(controller);
        Assert.assertSame(controller, ParsePush.getPushController());
    }

    // endregion
    // region testSendInBackground
    @Test
    public void testSendInBackgroundSuccess() throws Exception {
        // Mock controller
        ParsePushController controller = Mockito.mock(ParsePushController.class);
        Mockito.when(controller.sendInBackground(ArgumentMatchers.any(State.class), ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerPushController(controller);
        // Make sample ParsePush data and call method
        ParsePush push = new ParsePush();
        JSONObject data = new JSONObject();
        data.put("key", "value");
        List<String> channels = new ArrayList<>();
        channels.add("test");
        channels.add("testAgain");
        push.builder.expirationTime(((long) (1000))).data(data).channelSet(channels);
        ParseTaskUtils.wait(push.sendInBackground());
        // Make sure controller is executed and state parameter is correct
        ArgumentCaptor<ParsePush.State> stateCaptor = ArgumentCaptor.forClass(State.class);
        Mockito.verify(controller, Mockito.times(1)).sendInBackground(stateCaptor.capture(), ArgumentMatchers.anyString());
        ParsePush.State state = stateCaptor.getValue();
        Assert.assertEquals(data, state.data(), NON_EXTENSIBLE);
        Assert.assertEquals(2, state.channelSet().size());
        Assert.assertTrue(state.channelSet().contains("test"));
        Assert.assertTrue(state.channelSet().contains("testAgain"));
    }

    @Test
    public void testSendInBackgroundWithCallbackSuccess() throws Exception {
        // Mock controller
        ParsePushController controller = Mockito.mock(ParsePushController.class);
        Mockito.when(controller.sendInBackground(ArgumentMatchers.any(State.class), ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerPushController(controller);
        // Make sample ParsePush data and call method
        ParsePush push = new ParsePush();
        JSONObject data = new JSONObject();
        data.put("key", "value");
        List<String> channels = new ArrayList<>();
        channels.add("test");
        channels.add("testAgain");
        push.builder.expirationTime(((long) (1000))).data(data).channelSet(channels);
        final Semaphore done = new Semaphore(0);
        final Capture<Exception> exceptionCapture = new Capture();
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                exceptionCapture.set(e);
                done.release();
            }
        });
        // Make sure controller is executed and state parameter is correct
        Assert.assertNull(exceptionCapture.get());
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        ArgumentCaptor<ParsePush.State> stateCaptor = ArgumentCaptor.forClass(State.class);
        Mockito.verify(controller, Mockito.times(1)).sendInBackground(stateCaptor.capture(), ArgumentMatchers.anyString());
        ParsePush.State state = stateCaptor.getValue();
        Assert.assertEquals(data, state.data(), NON_EXTENSIBLE);
        Assert.assertEquals(2, state.channelSet().size());
        Assert.assertTrue(state.channelSet().contains("test"));
        Assert.assertTrue(state.channelSet().contains("testAgain"));
    }

    @Test
    public void testSendInBackgroundFail() throws Exception {
        // Mock controller
        ParsePushController controller = Mockito.mock(ParsePushController.class);
        ParseException exception = new ParseException(OTHER_CAUSE, "error");
        Mockito.when(controller.sendInBackground(ArgumentMatchers.any(State.class), ArgumentMatchers.anyString())).thenReturn(Task.<Void>forError(exception));
        ParseCorePlugins.getInstance().registerPushController(controller);
        // Make sample ParsePush data and call method
        ParsePush push = new ParsePush();
        JSONObject data = new JSONObject();
        data.put("key", "value");
        List<String> channels = new ArrayList<>();
        channels.add("test");
        channels.add("testAgain");
        push.builder.expirationTime(((long) (1000))).data(data).channelSet(channels);
        Task<Void> pushTask = push.sendInBackground();
        pushTask.waitForCompletion();
        // Make sure controller is executed and state parameter is correct
        ArgumentCaptor<ParsePush.State> stateCaptor = ArgumentCaptor.forClass(State.class);
        Mockito.verify(controller, Mockito.times(1)).sendInBackground(stateCaptor.capture(), ArgumentMatchers.anyString());
        ParsePush.State state = stateCaptor.getValue();
        Assert.assertEquals(data, state.data(), NON_EXTENSIBLE);
        Assert.assertEquals(2, state.channelSet().size());
        Assert.assertTrue(state.channelSet().contains("test"));
        Assert.assertTrue(state.channelSet().contains("testAgain"));
        // Make sure task is failed
        Assert.assertTrue(pushTask.isFaulted());
        Assert.assertSame(exception, pushTask.getError());
    }

    @Test
    public void testSendInBackgroundWithCallbackFail() throws Exception {
        // Mock controller
        ParsePushController controller = Mockito.mock(ParsePushController.class);
        final ParseException exception = new ParseException(OTHER_CAUSE, "error");
        Mockito.when(controller.sendInBackground(ArgumentMatchers.any(State.class), ArgumentMatchers.anyString())).thenReturn(Task.<Void>forError(exception));
        ParseCorePlugins.getInstance().registerPushController(controller);
        // Make sample ParsePush data and call method
        ParsePush push = new ParsePush();
        JSONObject data = new JSONObject();
        data.put("key", "value");
        List<String> channels = new ArrayList<>();
        channels.add("test");
        channels.add("testAgain");
        push.builder.expirationTime(((long) (1000))).data(data).channelSet(channels);
        final Semaphore done = new Semaphore(0);
        final Capture<Exception> exceptionCapture = new Capture();
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                exceptionCapture.set(e);
                done.release();
            }
        });
        // Make sure controller is executed and state parameter is correct
        Assert.assertSame(exception, exceptionCapture.get());
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        ArgumentCaptor<ParsePush.State> stateCaptor = ArgumentCaptor.forClass(State.class);
        Mockito.verify(controller, Mockito.times(1)).sendInBackground(stateCaptor.capture(), ArgumentMatchers.anyString());
        ParsePush.State state = stateCaptor.getValue();
        Assert.assertEquals(data, state.data(), NON_EXTENSIBLE);
        Assert.assertEquals(2, state.channelSet().size());
        Assert.assertTrue(state.channelSet().contains("test"));
        Assert.assertTrue(state.channelSet().contains("testAgain"));
    }

    @Test
    public void testSendSuccess() throws Exception {
        // Mock controller
        ParsePushController controller = Mockito.mock(ParsePushController.class);
        Mockito.when(controller.sendInBackground(ArgumentMatchers.any(State.class), ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerPushController(controller);
        // Make sample ParsePush data and call method
        ParsePush push = new ParsePush();
        JSONObject data = new JSONObject();
        data.put("key", "value");
        List<String> channels = new ArrayList<>();
        channels.add("test");
        channels.add("testAgain");
        push.builder.expirationTime(((long) (1000))).data(data).channelSet(channels);
        push.send();
        // Make sure controller is executed and state parameter is correct
        ArgumentCaptor<ParsePush.State> stateCaptor = ArgumentCaptor.forClass(State.class);
        Mockito.verify(controller, Mockito.times(1)).sendInBackground(stateCaptor.capture(), ArgumentMatchers.anyString());
        ParsePush.State state = stateCaptor.getValue();
        Assert.assertEquals(data, state.data(), NON_EXTENSIBLE);
        Assert.assertEquals(2, state.channelSet().size());
        Assert.assertTrue(state.channelSet().contains("test"));
        Assert.assertTrue(state.channelSet().contains("testAgain"));
    }

    @Test
    public void testSendFail() throws Exception {
        // Mock controller
        ParsePushController controller = Mockito.mock(ParsePushController.class);
        final ParseException exception = new ParseException(OTHER_CAUSE, "error");
        Mockito.when(controller.sendInBackground(ArgumentMatchers.any(State.class), ArgumentMatchers.anyString())).thenReturn(Task.<Void>forError(exception));
        ParseCorePlugins.getInstance().registerPushController(controller);
        // Make sample ParsePush data and call method
        ParsePush push = new ParsePush();
        JSONObject data = new JSONObject();
        data.put("key", "value");
        List<String> channels = new ArrayList<>();
        channels.add("test");
        channels.add("testAgain");
        push.builder.expirationTime(((long) (1000))).data(data).channelSet(channels);
        try {
            push.send();
        } catch (ParseException e) {
            Assert.assertSame(exception, e);
        }
        // Make sure controller is executed and state parameter is correct
        ArgumentCaptor<ParsePush.State> stateCaptor = ArgumentCaptor.forClass(State.class);
        Mockito.verify(controller, Mockito.times(1)).sendInBackground(stateCaptor.capture(), ArgumentMatchers.anyString());
        ParsePush.State state = stateCaptor.getValue();
        Assert.assertEquals(data, state.data(), NON_EXTENSIBLE);
        Assert.assertEquals(2, state.channelSet().size());
        Assert.assertTrue(state.channelSet().contains("test"));
        Assert.assertTrue(state.channelSet().contains("testAgain"));
    }

    // endregion
    // region testSendMessageInBackground
    @Test
    public void testSendMessageInBackground() throws Exception {
        // Mock controller
        ParsePushController controller = Mockito.mock(ParsePushController.class);
        Mockito.when(controller.sendInBackground(ArgumentMatchers.any(State.class), ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerPushController(controller);
        // Make sample ParsePush data and call method
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.getBuilder().whereEqualTo("foo", "bar");
        ParseTaskUtils.wait(ParsePush.sendMessageInBackground("test", query));
        // Make sure controller is executed and state parameter is correct
        ArgumentCaptor<ParsePush.State> stateCaptor = ArgumentCaptor.forClass(State.class);
        Mockito.verify(controller, Mockito.times(1)).sendInBackground(stateCaptor.capture(), ArgumentMatchers.anyString());
        ParsePush.State state = stateCaptor.getValue();
        // Verify query state
        ParseQuery.State<ParseInstallation> queryState = state.queryState();
        JSONObject queryStateJson = queryState.toJSON(PointerEncoder.get());
        Assert.assertEquals("bar", queryStateJson.getJSONObject("where").getString("foo"));
        // Verify message
        Assert.assertEquals("test", state.data().getString(KEY_DATA_MESSAGE));
    }

    @Test
    public void testSendMessageInBackgroundWithCallback() throws Exception {
        // Mock controller
        ParsePushController controller = Mockito.mock(ParsePushController.class);
        Mockito.when(controller.sendInBackground(ArgumentMatchers.any(State.class), ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerPushController(controller);
        // Make sample ParsePush data and call method
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.getBuilder().whereEqualTo("foo", "bar");
        final Semaphore done = new Semaphore(0);
        final Capture<Exception> exceptionCapture = new Capture();
        ParsePush.sendMessageInBackground("test", query, new SendCallback() {
            @Override
            public void done(ParseException e) {
                exceptionCapture.set(e);
                done.release();
            }
        });
        // Make sure controller is executed and state parameter is correct
        Assert.assertNull(exceptionCapture.get());
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        ArgumentCaptor<ParsePush.State> stateCaptor = ArgumentCaptor.forClass(State.class);
        Mockito.verify(controller, Mockito.times(1)).sendInBackground(stateCaptor.capture(), ArgumentMatchers.anyString());
        ParsePush.State state = stateCaptor.getValue();
        // Verify query state
        ParseQuery.State<ParseInstallation> queryState = state.queryState();
        JSONObject queryStateJson = queryState.toJSON(PointerEncoder.get());
        Assert.assertEquals("bar", queryStateJson.getJSONObject("where").getString("foo"));
        // Verify message
        Assert.assertEquals("test", state.data().getString(KEY_DATA_MESSAGE));
    }

    // endregion
    // region testSendDataInBackground
    @Test
    public void testSendDataInBackground() throws Exception {
        // Mock controller
        ParsePushController controller = Mockito.mock(ParsePushController.class);
        Mockito.when(controller.sendInBackground(ArgumentMatchers.any(State.class), ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerPushController(controller);
        // Make sample ParsePush data and call method
        JSONObject data = new JSONObject();
        data.put("key", "value");
        data.put("keyAgain", "valueAgain");
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.getBuilder().whereEqualTo("foo", "bar");
        ParsePush.sendDataInBackground(data, query);
        // Make sure controller is executed and state parameter is correct
        ArgumentCaptor<ParsePush.State> stateCaptor = ArgumentCaptor.forClass(State.class);
        Mockito.verify(controller, Mockito.times(1)).sendInBackground(stateCaptor.capture(), ArgumentMatchers.anyString());
        ParsePush.State state = stateCaptor.getValue();
        // Verify query state
        ParseQuery.State<ParseInstallation> queryState = state.queryState();
        JSONObject queryStateJson = queryState.toJSON(PointerEncoder.get());
        Assert.assertEquals("bar", queryStateJson.getJSONObject("where").getString("foo"));
        // Verify data
        Assert.assertEquals(data, state.data(), NON_EXTENSIBLE);
    }

    @Test
    public void testSendDataInBackgroundWithCallback() throws Exception {
        // Mock controller
        ParsePushController controller = Mockito.mock(ParsePushController.class);
        Mockito.when(controller.sendInBackground(ArgumentMatchers.any(State.class), ArgumentMatchers.anyString())).thenReturn(Task.<Void>forResult(null));
        ParseCorePlugins.getInstance().registerPushController(controller);
        // Make sample ParsePush data and call method
        JSONObject data = new JSONObject();
        data.put("key", "value");
        data.put("keyAgain", "valueAgain");
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.getBuilder().whereEqualTo("foo", "bar");
        final Semaphore done = new Semaphore(0);
        final Capture<Exception> exceptionCapture = new Capture();
        ParsePush.sendDataInBackground(data, query, new SendCallback() {
            @Override
            public void done(ParseException e) {
                exceptionCapture.set(e);
                done.release();
            }
        });
        // Make sure controller is executed and state parameter is correct
        Assert.assertNull(exceptionCapture.get());
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        ArgumentCaptor<ParsePush.State> stateCaptor = ArgumentCaptor.forClass(State.class);
        Mockito.verify(controller, Mockito.times(1)).sendInBackground(stateCaptor.capture(), ArgumentMatchers.anyString());
        ParsePush.State state = stateCaptor.getValue();
        // Verify query state
        ParseQuery.State<ParseInstallation> queryState = state.queryState();
        JSONObject queryStateJson = queryState.toJSON(PointerEncoder.get());
        Assert.assertEquals("bar", queryStateJson.getJSONObject("where").getString("foo"));
        // Verify data
        Assert.assertEquals(data, state.data(), NON_EXTENSIBLE);
    }
}

