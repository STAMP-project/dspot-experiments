/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseException.CONNECTION_FAILED;
import bolts.Task;
import com.parse.http.ParseHttpRequest;
import com.parse.http.ParseHttpResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// For org.json
// endregion
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseFileControllerTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testGetCacheFile() {
        File root = temporaryFolder.getRoot();
        ParseFileController controller = new ParseFileController(null, root);
        ParseFile.State state = new ParseFile.State.Builder().name("test_file").build();
        File cacheFile = controller.getCacheFile(state);
        Assert.assertEquals(new File(root, "test_file"), cacheFile);
    }

    @Test
    public void testIsDataAvailable() throws IOException {
        File root = temporaryFolder.getRoot();
        ParseFileController controller = new ParseFileController(null, root);
        temporaryFolder.newFile("test_file");
        ParseFile.State state = new ParseFile.State.Builder().name("test_file").build();
        Assert.assertTrue(controller.isDataAvailable(state));
    }

    @Test
    public void testClearCache() throws IOException {
        File root = temporaryFolder.getRoot();
        ParseFileController controller = new ParseFileController(null, root);
        File file1 = temporaryFolder.newFile("test_file_1");
        File file2 = temporaryFolder.newFile("test_file_2");
        controller.clearCache();
        Assert.assertFalse(file1.exists());
        Assert.assertFalse(file2.exists());
    }

    // region testSaveAsync
    @Test
    public void testSaveAsyncRequest() {
        // TODO(grantland): Verify proper command is constructed
    }

    @Test
    public void testSaveAsyncNotDirty() throws Exception {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParseFileController controller = new ParseFileController(restClient, null);
        ParseFile.State state = new ParseFile.State.Builder().url("http://example.com").build();
        Task<ParseFile.State> task = controller.saveAsync(state, ((byte[]) (null)), null, null, null);
        task.waitForCompletion();
        Mockito.verify(restClient, Mockito.times(0)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertFalse(task.isFaulted());
        Assert.assertFalse(task.isCancelled());
        Assert.assertSame(state, task.getResult());
    }

    @Test
    public void testSaveAsyncAlreadyCancelled() throws Exception {
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        ParseFileController controller = new ParseFileController(restClient, null);
        ParseFile.State state = new ParseFile.State.Builder().build();
        Task<Void> cancellationToken = Task.cancelled();
        Task<ParseFile.State> task = controller.saveAsync(state, ((byte[]) (null)), null, null, cancellationToken);
        task.waitForCompletion();
        Mockito.verify(restClient, Mockito.times(0)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertTrue(task.isCancelled());
    }

    @Test
    public void testSaveAsyncSuccessWithByteArray() throws Exception {
        JSONObject json = new JSONObject();
        json.put("name", "new_file_name");
        json.put("url", "http://example.com");
        String content = json.toString();
        ParseHttpResponse mockResponse = new ParseHttpResponse.Builder().setStatusCode(200).setTotalSize(((long) (content.length()))).setContent(new ByteArrayInputStream(content.getBytes())).build();
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        Mockito.when(restClient.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenReturn(mockResponse);
        File root = temporaryFolder.getRoot();
        ParseFileController controller = new ParseFileController(restClient, root);
        byte[] data = "hello".getBytes();
        ParseFile.State state = new ParseFile.State.Builder().name("file_name").mimeType("mime_type").build();
        Task<ParseFile.State> task = controller.saveAsync(state, data, null, null, null);
        ParseFile.State result = ParseTaskUtils.wait(task);
        Mockito.verify(restClient, Mockito.times(1)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertEquals("new_file_name", result.name());
        Assert.assertEquals("http://example.com", result.url());
        File file = new File(root, "new_file_name");
        Assert.assertTrue(file.exists());
        Assert.assertEquals("hello", ParseFileUtils.readFileToString(file, "UTF-8"));
    }

    @Test
    public void testSaveAsyncSuccessWithFile() throws Exception {
        JSONObject json = new JSONObject();
        json.put("name", "new_file_name");
        json.put("url", "http://example.com");
        String content = json.toString();
        ParseHttpResponse mockResponse = new ParseHttpResponse.Builder().setStatusCode(200).setTotalSize(((long) (content.length()))).setContent(new ByteArrayInputStream(content.getBytes())).build();
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        Mockito.when(restClient.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenReturn(mockResponse);
        File root = temporaryFolder.getRoot();
        ParseFileController controller = new ParseFileController(restClient, root);
        File file = new File(root, "test");
        ParseFileUtils.writeStringToFile(file, "content", "UTF-8");
        ParseFile.State state = new ParseFile.State.Builder().name("file_name").mimeType("mime_type").build();
        Task<ParseFile.State> task = controller.saveAsync(state, file, null, null, null);
        ParseFile.State result = ParseTaskUtils.wait(task);
        Mockito.verify(restClient, Mockito.times(1)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertEquals("new_file_name", result.name());
        Assert.assertEquals("http://example.com", result.url());
        File cachedFile = new File(root, "new_file_name");
        Assert.assertTrue(cachedFile.exists());
        Assert.assertTrue(file.exists());
        Assert.assertEquals("content", ParseFileUtils.readFileToString(cachedFile, "UTF-8"));
    }

    @Test
    public void testSaveAsyncFailureWithByteArray() throws Exception {
        // TODO(grantland): Remove once we no longer rely on retry logic.
        ParseRequest.setDefaultInitialRetryDelay(1L);
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        Mockito.when(restClient.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenThrow(new IOException());
        File root = temporaryFolder.getRoot();
        ParseFileController controller = new ParseFileController(restClient, root);
        byte[] data = "hello".getBytes();
        ParseFile.State state = new ParseFile.State.Builder().build();
        Task<ParseFile.State> task = controller.saveAsync(state, data, null, null, null);
        task.waitForCompletion();
        // TODO(grantland): Abstract out command runner so we don't have to account for retries.
        Mockito.verify(restClient, Mockito.times(5)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertTrue(task.isFaulted());
        Exception error = task.getError();
        Assert.assertThat(error, CoreMatchers.instanceOf(ParseException.class));
        Assert.assertEquals(CONNECTION_FAILED, getCode());
        Assert.assertEquals(0, root.listFiles().length);
    }

    @Test
    public void testSaveAsyncFailureWithFile() throws Exception {
        // TODO(grantland): Remove once we no longer rely on retry logic.
        ParseRequest.setDefaultInitialRetryDelay(1L);
        ParseHttpClient restClient = Mockito.mock(ParseHttpClient.class);
        Mockito.when(restClient.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenThrow(new IOException());
        File root = temporaryFolder.getRoot();
        ParseFileController controller = new ParseFileController(restClient, root);
        File file = temporaryFolder.newFile("test");
        ParseFile.State state = new ParseFile.State.Builder().build();
        Task<ParseFile.State> task = controller.saveAsync(state, file, null, null, null);
        task.waitForCompletion();
        // TODO(grantland): Abstract out command runner so we don't have to account for retries.
        Mockito.verify(restClient, Mockito.times(5)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertTrue(task.isFaulted());
        Exception error = task.getError();
        Assert.assertThat(error, CoreMatchers.instanceOf(ParseException.class));
        Assert.assertEquals(CONNECTION_FAILED, getCode());
        // Make sure the original file is not deleted and there is no cache file in the folder
        Assert.assertEquals(1, root.listFiles().length);
        Assert.assertTrue(file.exists());
    }

    // endregion
    // region testFetchAsync
    @Test
    public void testFetchAsyncRequest() {
        // TODO(grantland): Verify proper command is constructed
    }

    @Test
    public void testFetchAsyncAlreadyCancelled() throws Exception {
        ParseHttpClient fileClient = Mockito.mock(ParseHttpClient.class);
        ParseFileController controller = new ParseFileController(null, null).fileClient(fileClient);
        ParseFile.State state = new ParseFile.State.Builder().build();
        Task<Void> cancellationToken = Task.cancelled();
        Task<File> task = controller.fetchAsync(state, null, null, cancellationToken);
        task.waitForCompletion();
        Mockito.verify(fileClient, Mockito.times(0)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertTrue(task.isCancelled());
    }

    @Test
    public void testFetchAsyncCached() throws Exception {
        ParseHttpClient fileClient = Mockito.mock(ParseHttpClient.class);
        File root = temporaryFolder.getRoot();
        ParseFileController controller = new ParseFileController(null, root).fileClient(fileClient);
        File file = new File(root, "cached_file_name");
        ParseFileUtils.writeStringToFile(file, "hello", "UTF-8");
        ParseFile.State state = new ParseFile.State.Builder().name("cached_file_name").build();
        Task<File> task = controller.fetchAsync(state, null, null, null);
        File result = ParseTaskUtils.wait(task);
        Mockito.verify(fileClient, Mockito.times(0)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertEquals(file, result);
        Assert.assertEquals("hello", ParseFileUtils.readFileToString(result, "UTF-8"));
    }

    @Test
    public void testFetchAsyncSuccess() throws Exception {
        byte[] data = "hello".getBytes();
        ParseHttpResponse mockResponse = new ParseHttpResponse.Builder().setStatusCode(200).setTotalSize(((long) (data.length))).setContent(new ByteArrayInputStream(data)).build();
        ParseHttpClient fileClient = Mockito.mock(ParseHttpClient.class);
        Mockito.when(fileClient.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenReturn(mockResponse);
        // Make sure cache dir does not exist
        File root = new File(temporaryFolder.getRoot(), "cache");
        Assert.assertFalse(root.exists());
        ParseFileController controller = new ParseFileController(null, root).fileClient(fileClient);
        ParseFile.State state = new ParseFile.State.Builder().name("file_name").url("url").build();
        Task<File> task = controller.fetchAsync(state, null, null, null);
        File result = ParseTaskUtils.wait(task);
        Mockito.verify(fileClient, Mockito.times(1)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertTrue(result.exists());
        Assert.assertEquals("hello", ParseFileUtils.readFileToString(result, "UTF-8"));
        Assert.assertFalse(controller.getTempFile(state).exists());
    }

    @Test
    public void testFetchAsyncFailure() throws Exception {
        // TODO(grantland): Remove once we no longer rely on retry logic.
        ParseRequest.setDefaultInitialRetryDelay(1L);
        ParseHttpClient fileClient = Mockito.mock(ParseHttpClient.class);
        Mockito.when(fileClient.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenThrow(new IOException());
        File root = temporaryFolder.getRoot();
        ParseFileController controller = new ParseFileController(null, root).fileClient(fileClient);
        // We need to set url to make getTempFile() work and check it
        ParseFile.State state = new ParseFile.State.Builder().url("test").build();
        Task<File> task = controller.fetchAsync(state, null, null, null);
        task.waitForCompletion();
        // TODO(grantland): Abstract out command runner so we don't have to account for retries.
        Mockito.verify(fileClient, Mockito.times(5)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
        Assert.assertTrue(task.isFaulted());
        Exception error = task.getError();
        Assert.assertThat(error, CoreMatchers.instanceOf(ParseException.class));
        Assert.assertEquals(CONNECTION_FAILED, getCode());
        Assert.assertEquals(0, root.listFiles().length);
        Assert.assertFalse(controller.getTempFile(state).exists());
    }
}

