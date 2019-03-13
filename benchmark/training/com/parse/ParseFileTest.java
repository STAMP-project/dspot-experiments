/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseFile.CREATOR;
import android.os.Parcel;
import bolts.Task;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// TODO(grantland): testEncode
// TODO(grantland): testDecode
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseFileTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testConstructor() throws Exception {
        String name = "name";
        byte[] data = "hello".getBytes();
        String contentType = "content_type";
        File file = temporaryFolder.newFile(name);
        // TODO(mengyan): After we have proper staging strategy, we should verify the staging file's
        // content is the same with the original file.
        ParseFile parseFile = new ParseFile(name, data, contentType);
        Assert.assertEquals("name", parseFile.getName());
        Assert.assertEquals("content_type", parseFile.getState().mimeType());
        Assert.assertTrue(parseFile.isDirty());
        parseFile = new ParseFile(data);
        Assert.assertEquals("file", parseFile.getName());// Default

        Assert.assertEquals(null, parseFile.getState().mimeType());
        Assert.assertTrue(parseFile.isDirty());
        parseFile = new ParseFile(name, data);
        Assert.assertEquals("name", parseFile.getName());
        Assert.assertEquals(null, parseFile.getState().mimeType());
        Assert.assertTrue(parseFile.isDirty());
        parseFile = new ParseFile(data, contentType);
        Assert.assertEquals("file", parseFile.getName());// Default

        Assert.assertEquals("content_type", parseFile.getState().mimeType());
        Assert.assertTrue(parseFile.isDirty());
        parseFile = new ParseFile(file);
        Assert.assertEquals(name, parseFile.getName());// Default

        Assert.assertEquals(null, parseFile.getState().mimeType());
        Assert.assertTrue(parseFile.isDirty());
        parseFile = new ParseFile(file, contentType);
        Assert.assertEquals(name, parseFile.getName());// Default

        Assert.assertEquals("content_type", parseFile.getState().mimeType());
    }

    @Test
    public void testGetters() {
        ParseFile file = new ParseFile(new ParseFile.State.Builder().url("http://example.com").build());
        Assert.assertEquals("http://example.com", file.getUrl());
        Assert.assertFalse(file.isDirty());
        // Note: rest of the getters are tested in `testConstructor`
    }

    @Test
    public void testIsDataAvailableCachedInMemory() {
        ParseFile file = new ParseFile(new ParseFile.State.Builder().build());
        file.data = "hello".getBytes();
        Assert.assertTrue(file.isDataAvailable());
    }

    @Test
    public void testIsDataAvailableCachedInController() {
        ParseFileController controller = Mockito.mock(ParseFileController.class);
        Mockito.when(controller.isDataAvailable(ArgumentMatchers.any(ParseFile.State.class))).thenReturn(true);
        ParseCorePlugins.getInstance().registerFileController(controller);
        ParseFile.State state = new ParseFile.State.Builder().build();
        ParseFile file = new ParseFile(state);
        Assert.assertTrue(file.isDataAvailable());
        Mockito.verify(controller).isDataAvailable(state);
    }

    // region testSaveAsync
    @Test
    public void testSaveAsyncNotDirty() throws Exception {
        ParseFileController controller = Mockito.mock(ParseFileController.class);
        Mockito.when(controller.isDataAvailable(ArgumentMatchers.any(ParseFile.State.class))).thenReturn(true);
        ParseCorePlugins.getInstance().registerFileController(controller);
        ParseFile.State state = new ParseFile.State.Builder().url("http://example.com").build();
        ParseFile file = new ParseFile(state);
        Task<Void> task = file.saveAsync(null, null, null);
        ParseTaskUtils.wait(task);
        Mockito.verify(controller, Mockito.never()).saveAsync(ArgumentMatchers.any(ParseFile.State.class), ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any());
    }

    @Test
    public void testSaveAsyncCancelled() throws Exception {
        ParseFileController controller = Mockito.mock(ParseFileController.class);
        Mockito.when(controller.isDataAvailable(ArgumentMatchers.any(ParseFile.State.class))).thenReturn(true);
        ParseCorePlugins.getInstance().registerFileController(controller);
        ParseFile.State state = new ParseFile.State.Builder().build();
        ParseFile file = new ParseFile(state);
        Task<Void> task = file.saveAsync(null, null, Task.<Void>cancelled());
        task.waitForCompletion();
        Assert.assertTrue(task.isCancelled());
        Mockito.verify(controller, Mockito.never()).saveAsync(ArgumentMatchers.any(ParseFile.State.class), ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any());
    }

    @Test
    public void testSaveAsyncSuccessWithData() throws Exception {
        String name = "name";
        byte[] data = "hello".getBytes();
        String contentType = "content_type";
        String url = "url";
        ParseFile.State state = new ParseFile.State.Builder().url(url).build();
        ParseFileController controller = Mockito.mock(ParseFileController.class);
        Mockito.when(controller.saveAsync(ArgumentMatchers.any(ParseFile.State.class), ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any())).thenReturn(Task.forResult(state));
        ParseCorePlugins.getInstance().registerFileController(controller);
        ParseFile parseFile = new ParseFile(name, data, contentType);
        ParseTaskUtils.wait(parseFile.saveAsync(null, null, null));
        // Verify controller get the correct data
        ArgumentCaptor<ParseFile.State> stateCaptor = ArgumentCaptor.forClass(ParseFile.State.class);
        ArgumentCaptor<byte[]> dataCaptor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(controller, Mockito.times(1)).saveAsync(stateCaptor.capture(), dataCaptor.capture(), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any());
        Assert.assertNull(stateCaptor.getValue().url());
        Assert.assertEquals(name, stateCaptor.getValue().name());
        Assert.assertEquals(contentType, stateCaptor.getValue().mimeType());
        Assert.assertArrayEquals(data, dataCaptor.getValue());
        // Verify the state of ParseFile has been updated
        Assert.assertEquals(url, parseFile.getUrl());
    }

    @Test
    public void testSaveAsyncSuccessWithFile() throws Exception {
        String name = "name";
        File file = temporaryFolder.newFile(name);
        String contentType = "content_type";
        String url = "url";
        ParseFile.State state = new ParseFile.State.Builder().url(url).build();
        ParseFileController controller = Mockito.mock(ParseFileController.class);
        Mockito.when(controller.saveAsync(ArgumentMatchers.any(ParseFile.State.class), ArgumentMatchers.any(File.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any())).thenReturn(Task.forResult(state));
        ParseCorePlugins.getInstance().registerFileController(controller);
        ParseFile parseFile = new ParseFile(file, contentType);
        ParseTaskUtils.wait(parseFile.saveAsync(null, null, null));
        // Verify controller get the correct data
        ArgumentCaptor<ParseFile.State> stateCaptor = ArgumentCaptor.forClass(ParseFile.State.class);
        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
        Mockito.verify(controller, Mockito.times(1)).saveAsync(stateCaptor.capture(), fileCaptor.capture(), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any());
        Assert.assertNull(stateCaptor.getValue().url());
        Assert.assertEquals(name, stateCaptor.getValue().name());
        Assert.assertEquals(contentType, stateCaptor.getValue().mimeType());
        Assert.assertEquals(file, fileCaptor.getValue());
        // Verify the state of ParseFile has been updated
        Assert.assertEquals(url, parseFile.getUrl());
    }

    // TODO(grantland): testSaveAsyncNotDirtyAfterQueueAwait
    // TODO(grantland): testSaveAsyncSuccess
    // TODO(grantland): testSaveAsyncFailure
    // endregion
    // region testGetDataAsync
    @Test
    public void testGetDataAsyncSuccess() throws Exception {
        String content = "content";
        File file = temporaryFolder.newFile("test");
        ParseFileUtils.writeStringToFile(file, content, "UTF-8");
        ParseFileController controller = Mockito.mock(ParseFileController.class);
        Mockito.when(controller.fetchAsync(ArgumentMatchers.any(ParseFile.State.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any())).thenReturn(Task.forResult(file));
        ParseCorePlugins.getInstance().registerFileController(controller);
        String url = "url";
        ParseFile.State state = new ParseFile.State.Builder().url(url).build();
        ParseFile parseFile = new ParseFile(state);
        byte[] data = ParseTaskUtils.wait(parseFile.getDataInBackground());
        // Verify controller get the correct data
        ArgumentCaptor<ParseFile.State> stateCaptor = ArgumentCaptor.forClass(ParseFile.State.class);
        Mockito.verify(controller, Mockito.times(1)).fetchAsync(stateCaptor.capture(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any());
        Assert.assertEquals(url, stateCaptor.getValue().url());
        // Verify the data we get is correct
        Assert.assertArrayEquals(content.getBytes(), data);
        // Make sure we always get the data from network
        byte[] dataAgain = ParseTaskUtils.wait(parseFile.getDataInBackground());
        // Verify controller get the correct data
        ArgumentCaptor<ParseFile.State> stateCaptorAgain = ArgumentCaptor.forClass(ParseFile.State.class);
        Mockito.verify(controller, Mockito.times(2)).fetchAsync(stateCaptorAgain.capture(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any());
        Assert.assertEquals(url, stateCaptorAgain.getValue().url());
        // Verify the data we get is correct
        Assert.assertArrayEquals(content.getBytes(), dataAgain);
    }

    @Test
    public void testGetDataStreamAsyncSuccess() throws Exception {
        String content = "content";
        File file = temporaryFolder.newFile("test");
        ParseFileUtils.writeStringToFile(file, content, "UTF-8");
        ParseFileController controller = Mockito.mock(ParseFileController.class);
        Mockito.when(controller.fetchAsync(ArgumentMatchers.any(ParseFile.State.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any())).thenReturn(Task.forResult(file));
        ParseCorePlugins.getInstance().registerFileController(controller);
        String url = "url";
        ParseFile.State state = new ParseFile.State.Builder().url(url).build();
        ParseFile parseFile = new ParseFile(state);
        InputStream dataStream = ParseTaskUtils.wait(parseFile.getDataStreamInBackground());
        // Verify controller get the correct data
        ArgumentCaptor<ParseFile.State> stateCaptor = ArgumentCaptor.forClass(ParseFile.State.class);
        Mockito.verify(controller, Mockito.times(1)).fetchAsync(stateCaptor.capture(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any());
        Assert.assertEquals(url, stateCaptor.getValue().url());
        // Verify the data we get is correct
        Assert.assertArrayEquals(content.getBytes(), ParseIOUtils.toByteArray(dataStream));
        // Make sure we always get the data from network
        InputStream dataStreamAgain = ParseTaskUtils.wait(parseFile.getDataStreamInBackground());
        // Verify controller get the correct data
        ArgumentCaptor<ParseFile.State> stateCaptorAgain = ArgumentCaptor.forClass(ParseFile.State.class);
        Mockito.verify(controller, Mockito.times(2)).fetchAsync(stateCaptorAgain.capture(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any());
        Assert.assertEquals(url, stateCaptorAgain.getValue().url());
        // Verify the data we get is correct
        Assert.assertArrayEquals(content.getBytes(), ParseIOUtils.toByteArray(dataStreamAgain));
    }

    @Test
    public void testGetFileAsyncSuccess() throws Exception {
        String content = "content";
        File file = temporaryFolder.newFile("test");
        ParseFileUtils.writeStringToFile(file, content, "UTF-8");
        ParseFileController controller = Mockito.mock(ParseFileController.class);
        Mockito.when(controller.fetchAsync(ArgumentMatchers.any(ParseFile.State.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any())).thenReturn(Task.forResult(file));
        ParseCorePlugins.getInstance().registerFileController(controller);
        String url = "url";
        ParseFile.State state = new ParseFile.State.Builder().url(url).build();
        ParseFile parseFile = new ParseFile(state);
        File fetchedFile = ParseTaskUtils.wait(parseFile.getFileInBackground());
        // Verify controller get the correct data
        ArgumentCaptor<ParseFile.State> stateCaptor = ArgumentCaptor.forClass(ParseFile.State.class);
        Mockito.verify(controller, Mockito.times(1)).fetchAsync(stateCaptor.capture(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any());
        Assert.assertEquals(url, stateCaptor.getValue().url());
        // Verify the data we get is correct
        Assert.assertArrayEquals(content.getBytes(), ParseFileUtils.readFileToByteArray(fetchedFile));
        // Make sure we always get the data from network
        File fetchedFileAgain = ParseTaskUtils.wait(parseFile.getFileInBackground());
        // Verify controller get the correct data
        ArgumentCaptor<ParseFile.State> stateCaptorAgain = ArgumentCaptor.forClass(ParseFile.State.class);
        Mockito.verify(controller, Mockito.times(2)).fetchAsync(stateCaptorAgain.capture(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any());
        Assert.assertEquals(url, stateCaptorAgain.getValue().url());
        // Verify the data we get is correct
        Assert.assertArrayEquals(content.getBytes(), ParseFileUtils.readFileToByteArray(fetchedFileAgain));
    }

    // endregion
    @Test
    public void testTaskQueuedMethods() throws Exception {
        ParseFile.State state = new ParseFile.State.Builder().build();
        File cachedFile = temporaryFolder.newFile("temp");
        ParseFileController controller = Mockito.mock(ParseFileController.class);
        Mockito.when(controller.saveAsync(ArgumentMatchers.any(ParseFile.State.class), ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any())).thenReturn(Task.forResult(state));
        Mockito.when(controller.saveAsync(ArgumentMatchers.any(ParseFile.State.class), ArgumentMatchers.any(File.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any())).thenReturn(Task.forResult(state));
        Mockito.when(controller.fetchAsync(ArgumentMatchers.any(ParseFile.State.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ProgressCallback.class), Matchers.<Task<Void>>any())).thenReturn(Task.forResult(cachedFile));
        ParseCorePlugins.getInstance().registerFileController(controller);
        ParseFile file = new ParseFile(state);
        TaskQueueTestHelper queueHelper = new TaskQueueTestHelper(file.taskQueue);
        queueHelper.enqueue();
        Task<Void> saveTaskA = file.saveAsync(null, null, null);
        queueHelper.enqueue();
        Task<byte[]> getDataTaskA = file.getDataInBackground();
        queueHelper.enqueue();
        Task<Void> saveTaskB = file.saveAsync(null, null, null);
        queueHelper.enqueue();
        Task<byte[]> getDataTaskB = file.getDataInBackground();
        Thread.sleep(50);
        Assert.assertFalse(saveTaskA.isCompleted());
        queueHelper.dequeue();
        ParseTaskUtils.wait(saveTaskA);
        Thread.sleep(50);
        Assert.assertFalse(getDataTaskA.isCompleted());
        queueHelper.dequeue();
        ParseTaskUtils.wait(getDataTaskA);
        Thread.sleep(50);
        Assert.assertFalse(saveTaskB.isCompleted());
        queueHelper.dequeue();
        ParseTaskUtils.wait(saveTaskB);
        Thread.sleep(50);
        Assert.assertFalse(getDataTaskB.isCompleted());
        queueHelper.dequeue();
        ParseTaskUtils.wait(getDataTaskB);
    }

    @Test
    public void testCancel() {
        ParseFile file = new ParseFile(new ParseFile.State.Builder().build());
        TaskQueueTestHelper queueHelper = new TaskQueueTestHelper(file.taskQueue);
        queueHelper.enqueue();
        List<Task<Void>> saveTasks = Arrays.asList(file.saveInBackground(), file.saveInBackground(), file.saveInBackground());
        List<Task<byte[]>> getDataTasks = Arrays.asList(file.getDataInBackground(), file.getDataInBackground(), file.getDataInBackground());
        file.cancel();
        queueHelper.dequeue();
        for (int i = 0; i < (saveTasks.size()); i++) {
            Assert.assertTrue((("Task #" + i) + " was not cancelled"), saveTasks.get(i).isCancelled());
        }
        for (int i = 0; i < (getDataTasks.size()); i++) {
            Assert.assertTrue((("Task #" + i) + " was not cancelled"), getDataTasks.get(i).isCancelled());
        }
    }

    @Test
    public void testParcelable() {
        String mime = "mime";
        String name = "name";
        String url = "url";
        ParseFile file = new ParseFile(new ParseFile.State.Builder().name(name).mimeType(mime).url(url).build());
        Parcel parcel = Parcel.obtain();
        file.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        file = CREATOR.createFromParcel(parcel);
        Assert.assertEquals(file.getName(), name);
        Assert.assertEquals(file.getUrl(), url);
        Assert.assertEquals(file.getState().mimeType(), mime);
        Assert.assertFalse(file.isDirty());
    }

    @Test(expected = RuntimeException.class)
    public void testDontParcelIfDirty() {
        ParseFile file = new ParseFile(new ParseFile.State.Builder().build());
        Parcel parcel = Parcel.obtain();
        file.writeToParcel(parcel, 0);
    }
}

