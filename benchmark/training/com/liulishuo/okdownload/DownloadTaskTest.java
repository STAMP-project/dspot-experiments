/**
 * Copyright (c) 2017 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liulishuo.okdownload;


import ContentResolver.SCHEME_FILE;
import DownloadStrategy.FilenameHolder;
import DownloadTask.Builder;
import DownloadTask.TaskHideWrapper;
import IdentifiedTask.EMPTY_FILE;
import IdentifiedTask.EMPTY_URL;
import android.net.Uri;
import com.liulishuo.okdownload.core.IdentifiedTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointStore;
import com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnCache;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.liulishuo.okdownload.core.download.DownloadStrategy;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.eq;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class DownloadTaskTest {
    @Test
    public void addHeader() throws Exception {
        final String url = "mock url";
        final Uri mockFileUri = Mockito.mock(Uri.class);
        Mockito.when(mockFileUri.getScheme()).thenReturn(SCHEME_FILE);
        Mockito.when(mockFileUri.getPath()).thenReturn("mock path");
        DownloadTask.Builder builder = new DownloadTask.Builder(url, mockFileUri);
        final String mockKey1 = "mock key1";
        final String mockKey2 = "mock key2";
        final String mockValue1 = "mock value1";
        final String mockValue2 = "mock value2";
        builder.addHeader(mockKey1, mockValue1);
        builder.addHeader(mockKey1, mockValue2);
        builder.addHeader(mockKey2, mockValue2);
        final Map<String, List<String>> headerMap = builder.build().getHeaderMapFields();
        assertThat(headerMap).isNotNull();
        assertThat(headerMap).containsKey(mockKey1).containsKey(mockKey2);
        final List<String> key1Values = headerMap.get(mockKey1);
        assertThat(key1Values).containsOnly(mockValue1, mockValue2);
        final List<String> key2Values = headerMap.get(mockKey2);
        assertThat(key2Values).containsOnly(mockValue2);
    }

    @Test
    public void setHeaderMapFields() {
        final String url = "mock url";
        final Uri mockFileUri = Mockito.mock(Uri.class);
        Mockito.when(mockFileUri.getScheme()).thenReturn(SCHEME_FILE);
        Mockito.when(mockFileUri.getPath()).thenReturn("mock path");
        DownloadTask.Builder builder = new DownloadTask.Builder(url, mockFileUri);
        final Map<String, List<String>> headerMap = new HashMap<>();
        builder.setHeaderMapFields(headerMap);
        assertThat(builder.build().getHeaderMapFields()).isEqualTo(headerMap);
    }

    private final String parentPath = "./p-path/";

    private final String filename = "filename";

    @Test
    public void enqueue() {
        final DownloadTask[] tasks = new DownloadTask[2];
        tasks[0] = new DownloadTask.Builder("url1", "path", "filename1").build();
        tasks[1] = new DownloadTask.Builder("url2", "path", "filename1").build();
        final DownloadListener listener = Mockito.mock(DownloadListener.class);
        DownloadTask.enqueue(tasks, listener);
        assertThat(tasks[0].getListener()).isEqualTo(listener);
        assertThat(tasks[1].getListener()).isEqualTo(listener);
        Mockito.verify(OkDownload.with().downloadDispatcher()).enqueue(eq(tasks));
    }

    @Test
    public void equal() throws IOException {
        // for id
        Mockito.when(OkDownload.with().breakpointStore()).thenReturn(Mockito.spy(new BreakpointStoreOnCache()));
        final Uri uri = Mockito.mock(Uri.class);
        Mockito.when(uri.getPath()).thenReturn(parentPath);
        Mockito.when(uri.getScheme()).thenReturn(SCHEME_FILE);
        // origin is:
        // 1. uri is directory
        // 2. filename is provided
        DownloadTask task = build();
        // compare to:
        // 1. uri is not directory
        // 2. filename is provided by uri.
        final Uri anotherUri = Mockito.mock(Uri.class);
        Mockito.when(anotherUri.getPath()).thenReturn(((parentPath) + (filename)));
        Mockito.when(anotherUri.getScheme()).thenReturn(SCHEME_FILE);
        DownloadTask anotherTask = build();
        assertThat(task.equals(anotherTask)).isTrue();
        // compare to:
        // 1. uri is directory
        // 2. filename is not provided
        anotherTask = new DownloadTask.Builder("url", uri).build();
        // expect: not same
        assertThat(task.equals(anotherTask)).isFalse();
        // compare to:
        // 1. uri is directory
        // 2. filename is provided and different
        anotherTask = setFilename("another-filename").build();
        // expect: not same
        assertThat(task.equals(anotherTask)).isFalse();
        // origin is:
        // 1. uri is directory
        // 2. filename is not provided
        DownloadTask noFilenameTask = build();
        // filename is enabled by response
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        Mockito.when(info.getFilenameHolder()).thenReturn(Mockito.mock(FilenameHolder.class));
        new DownloadStrategy().validFilenameFromResponse("response-filename", noFilenameTask, info);
        // compare to:
        // 1. uri is directory
        // 2. filename is provided
        anotherTask = setFilename("another-filename").build();
        assertThat(noFilenameTask.equals(anotherTask)).isFalse();
        // compare to:
        // 1. uri is directory
        // 2. filename is not provided
        anotherTask = new DownloadTask.Builder("url", uri).build();
        assertThat(noFilenameTask.equals(anotherTask)).isTrue();
        // compare to:
        // 1. uri is directory
        // 2. filename is provided and the same to the response-filename
        anotherTask = setFilename("response-filename").build();
        assertThat(noFilenameTask.equals(anotherTask)).isTrue();
    }

    @Test
    public void toBuilder() {
        // filename is provided specially => set filename
        final Uri uri = Mockito.mock(Uri.class);
        Mockito.when(uri.getPath()).thenReturn(parentPath);
        Mockito.when(uri.getScheme()).thenReturn(SCHEME_FILE);
        DownloadTask task = build();
        DownloadTask buildTask = build();
        assertThat(buildTask.getUrl()).isEqualTo("url");
        assertThat(buildTask.getUri()).isEqualTo(uri);
        assertThat(buildTask.getFilename()).isEqualTo("filename1");
        // another uri is file, use new filename
        final Uri anotherUri = Mockito.mock(Uri.class);
        Mockito.when(anotherUri.getScheme()).thenReturn(SCHEME_FILE);
        Mockito.when(anotherUri.getPath()).thenReturn(((parentPath) + (filename)));
        buildTask = task.toBuilder("anotherUrl", anotherUri).build();
        assertThat(buildTask.getUrl()).isEqualTo("anotherUrl");
        assertThat(buildTask.getUri()).isEqualTo(anotherUri);
        assertThat(buildTask.getFilename()).isEqualTo(filename);
        // same uri provided filename => same file
        Mockito.when(uri.getPath()).thenReturn(((parentPath) + (filename)));
        task = new DownloadTask.Builder("url", uri).build();
        buildTask = task.toBuilder("anotherUrl", uri).build();
        TestUtils.assertFile(buildTask.getFile()).isEqualTo(task.getFile());
    }

    @Test
    public void profile() {
        final String url = "url";
        final Uri uri = Mockito.mock(Uri.class);
        Mockito.when(uri.getPath()).thenReturn("~/path");
        Mockito.when(uri.getScheme()).thenReturn(SCHEME_FILE);
        // basic profile
        DownloadTask task = build();
        assertThat(task.getReadBufferSize()).isEqualTo(1);
        assertThat(task.getFlushBufferSize()).isEqualTo(2);
        assertThat(task.getSyncBufferSize()).isEqualTo(3);
        assertThat(task.getSyncBufferIntervalMills()).isEqualTo(4);
        assertThat(task.getMinIntervalMillisCallbackProcess()).isEqualTo(5);
        assertThat(task.isAutoCallbackToUIThread()).isTrue();
        assertThat(task.isWifiRequired()).isTrue();
        // setTag
        task.setTag("tag");
        assertThat(task.getTag()).isEqualTo("tag");
        task.removeTag();
        assertThat(task.getTag()).isNull();
        // addTag
        task.addTag(1, "tag1");
        task.addTag(2, "tag2");
        assertThat(task.getTag(1)).isEqualTo("tag1");
        assertThat(task.getTag(2)).isEqualTo("tag2");
        task.removeTag(1);
        assertThat(task.getTag(1)).isNull();
        // callback process timestamp
        task.setLastCallbackProcessTs(1L);
        assertThat(task.getLastCallbackProcessTs()).isEqualTo(1L);
        // setTags
        DownloadTask oldTask = build();
        DownloadTask newTask = build();
        oldTask.setTag("tag");
        oldTask.addTag(0, "tag0");
        newTask.setTags(oldTask);
        assertThat(newTask.getTag()).isEqualTo("tag");
        assertThat(newTask.getTag(0)).isEqualTo("tag0");
    }

    @Test
    public void operation() {
        final DownloadDispatcher dispatcher = OkDownload.with().downloadDispatcher();
        final String url = "url";
        final Uri uri = Mockito.mock(Uri.class);
        Mockito.when(uri.getScheme()).thenReturn(SCHEME_FILE);
        Mockito.when(uri.getPath()).thenReturn("~/path");
        DownloadTask task = build();
        // enqueue
        final DownloadListener listener = Mockito.mock(DownloadListener.class);
        task.enqueue(listener);
        assertThat(task.getListener()).isEqualTo(listener);
        Mockito.verify(dispatcher).enqueue(eq(task));
        // cancel
        task.cancel();
        Mockito.verify(dispatcher).cancel(eq(task));
        // execute
        task.execute(listener);
        assertThat(task.getListener()).isEqualTo(listener);
        Mockito.verify(dispatcher).execute(eq(task));
    }

    @Test
    public void taskBuilder_constructWithFile() {
        final String url = "https://jacksgong.com";
        final File noExistFile = new File(parentPath, "no-exist");
        DownloadTask task = new DownloadTask.Builder(url, noExistFile).build();
        assertThat(task.getFilename()).isEqualTo(noExistFile.getName());
        assertThat(task.getFile().getAbsolutePath()).isEqualTo(noExistFile.getAbsolutePath());
        final File existFile = new File(parentPath, filename);
        task = new DownloadTask.Builder(url, existFile).build();
        assertThat(task.getFilename()).isEqualTo(existFile.getName());
        assertThat(task.getFile().getAbsolutePath()).isEqualTo(existFile.getAbsolutePath());
        final File existParentFile = new File(parentPath);
        task = new DownloadTask.Builder(url, existParentFile).build();
        assertThat(task.getFilename()).isNull();
        assertThat(task.getFile()).isNull();
        TestUtils.assertFile(task.getParentFile()).isEqualTo(existParentFile);
        final File onlyFile = new File("/path");
        task = new DownloadTask.Builder(url, onlyFile).build();
        assertThat(task.getFilename()).isEqualTo("path");
        TestUtils.assertFile(task.getFile()).isEqualTo(onlyFile);
        TestUtils.assertFile(task.getParentFile()).isEqualTo(new File("/"));
    }

    @Test
    public void taskHideWrapper() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        TaskHideWrapper.setLastCallbackProcessTs(task, 10L);
        Mockito.verify(task).setLastCallbackProcessTs(ArgumentMatchers.eq(10L));
        TaskHideWrapper.getLastCallbackProcessTs(task);
        Mockito.verify(task).getLastCallbackProcessTs();
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        TaskHideWrapper.setBreakpointInfo(task, info);
        Mockito.verify(task).setBreakpointInfo(eq(info));
    }

    @Test
    public void getInfo() throws IOException {
        TestUtils.mockOkDownload();
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        final BreakpointStore store = OkDownload.with().breakpointStore();
        Mockito.when(store.get(1)).thenReturn(info);
        Mockito.when(store.findOrCreateId(ArgumentMatchers.any(DownloadTask.class))).thenReturn(1);
        final DownloadTask task = new DownloadTask.Builder("https://jacksgong.com", new File(parentPath)).build();
        assertThat(task.getInfo()).isEqualTo(info);
    }

    @Test
    public void mockTaskForCompare() {
        DownloadTask task = new DownloadTask.Builder("https://jacksgong.com", parentPath, filename).build();
        IdentifiedTask identifiedTask = task.mock(0);
        assertThat(identifiedTask.compareIgnoreId(task)).isTrue();
        task = new DownloadTask.Builder("https://www.jacksgong.com", new File(parentPath)).build();
        identifiedTask = task.mock(0);
        assertThat(identifiedTask.compareIgnoreId(task)).isTrue();
        task = new DownloadTask.Builder("https://jacksgong.com", "non-exist-parent", "non-exist").build();
        identifiedTask = task.mock(0);
        assertThat(identifiedTask.compareIgnoreId(task)).isTrue();
    }

    @Test
    public void constructor_path() {
        // exist[filename and parent path]
        DownloadTask task = new DownloadTask.Builder("https://jacksgong.com", parentPath, filename).build();
        assertThat(task.getFilename()).isEqualTo(filename);
        assertThat(task.getFile().getAbsolutePath()).isEqualTo(new File(parentPath, filename).getAbsolutePath());
        // exist[filename and parent path] but force filename from response
        task = new DownloadTask.Builder("https://jacksgong.com", parentPath, filename).setFilenameFromResponse(true).build();
        assertThat(task.getFilename()).isNull();
        assertThat(task.getFile()).isNull();
        TestUtils.assertFile(task.getParentFile()).isEqualTo(new File(parentPath));
        // exist[parent path] and provide[filename through setFilename]
        task = new DownloadTask.Builder("https://jacksgong.com", new File(parentPath)).setFilename(filename).build();
        assertThat(task.getFilename()).isEqualTo(filename);
        TestUtils.assertFile(task.getFile()).isEqualTo(new File(parentPath, filename));
        TestUtils.assertFile(task.getParentFile()).isEqualTo(new File(parentPath));
        // exist[parent path] but not provide[filename]
        task = new DownloadTask.Builder("https://jacksgong.com", new File(parentPath)).build();
        assertThat(task.getFilename()).isNull();
        assertThat(task.getFile()).isNull();
        TestUtils.assertFile(task.getParentFile()).isEqualTo(new File(parentPath));
        // unknown filename or parent path
        task = new DownloadTask.Builder("https://jacksgong.com", new File("/not-exist")).build();
        assertThat(task.getFilename()).isEqualTo("not-exist");
        TestUtils.assertFile(task.getFile()).isEqualTo(new File("/not-exist"));
        TestUtils.assertFile(task.getParentFile()).isEqualTo(new File("/"));
        // unknown filename or parent path but set filename from response
        task = new DownloadTask.Builder("https://jacksgong.com", new File("not-exist")).setFilenameFromResponse(true).build();
        assertThat(task.getFilename()).isNull();
        assertThat(task.getFile()).isNull();
        TestUtils.assertFile(task.getParentFile()).isEqualTo(new File("not-exist"));
        // there is filename and parent path but all not exist
        task = new DownloadTask.Builder("https://jacksgong.com", new File("not-exist/filename")).build();
        assertThat(task.getFilename()).isEqualTo("filename");
        TestUtils.assertFile(task.getFile()).isEqualTo(new File("not-exist/filename"));
        TestUtils.assertFile(task.getParentFile()).isEqualTo(new File("not-exist"));
        task = new DownloadTask.Builder("https://jacksgong.com", "not-exist", "filename").build();
        assertThat(task.getFilename()).isEqualTo("filename");
        TestUtils.assertFile(task.getFile()).isEqualTo(new File("not-exist", "filename"));
        TestUtils.assertFile(task.getParentFile()).isEqualTo(new File("not-exist"));
        // there is filename and parent path but all not exist and set filename from response
        task = new DownloadTask.Builder("https://jacksgong.com", "not-exist", "filename").setFilenameFromResponse(true).build();
        assertThat(task.getFilename()).isNull();
        assertThat(task.getFile()).isNull();
        TestUtils.assertFile(task.getParentFile()).isEqualTo(new File("not-exist"));
        // there is filename and parent path but all not exist and set filename not from response
        task = new DownloadTask.Builder("https://jacksgong.com", "not-exist", "filename").setFilenameFromResponse(false).build();
        assertThat(task.getFilename()).isEqualTo("filename");
        TestUtils.assertFile(task.getFile()).isEqualTo(new File("not-exist/filename"));
        TestUtils.assertFile(task.getParentFile()).isEqualTo(new File("not-exist"));
        task = new DownloadTask.Builder("https://jacksgong.com", new File(parentPath, "unknown-filename")).setFilenameFromResponse(false).build();
        assertThat(task.getFilename()).isEqualTo("unknown-filename");
        TestUtils.assertFile(task.getFile()).isEqualTo(new File(parentPath, "unknown-filename"));
        TestUtils.assertFile(task.getParentFile()).isEqualTo(new File(parentPath));
        // provide null filename.
        task = new DownloadTask.Builder("https://jacksgong.com", "not-exist", null).build();
        assertThat(task.getFilename()).isNull();
        assertThat(task.getFile()).isNull();
        TestUtils.assertFile(task.getParentFile()).isEqualTo(new File("not-exist"));
        // provide is not directory but force filename from response.
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage((("If you want filename from response please make sure you " + "provide path is directory ") + (new File(parentPath, filename).getAbsolutePath())));
        new DownloadTask.Builder("https://jacksgong.com", new File(parentPath, filename)).setFilenameFromResponse(true).build();
        // no valid filename but force filename not from response
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage((("If you don't want filename from response please make sure" + " you have already provided valid filename or not directory path ") + (new File(parentPath).getAbsolutePath())));
        new DownloadTask.Builder("https://jacksgong.com", new File(parentPath)).setFilenameFromResponse(false).build();
        // connection count.
        assertThat(task.getSetConnectionCount()).isNull();
        task = new DownloadTask.Builder("https://jacksgong.com", "not-exist", null).setConnectionCount(2).build();
        assertThat(task.getSetConnectionCount()).isEqualTo(2);
        // pre-allocate-length
        assertThat(task.getSetPreAllocateLength()).isNull();
        task = new DownloadTask.Builder("https://jacksgong.com", "not-exist", null).setPreAllocateLength(true).build();
        assertThat(task.getSetPreAllocateLength()).isTrue();
    }

    @Test
    public void taskToString() {
        DownloadTask task = new DownloadTask.Builder("https://jacksgong.com", new File(parentPath, filename)).build();
        assertThat(task.toString()).endsWith(((("@" + (task.getId())) + "@https://jacksgong.com@") + (new File(parentPath, filename).getAbsolutePath())));
    }

    @Test
    public void replaceListener() {
        DownloadTask task = new DownloadTask.Builder("https://jacksgong.com", new File(parentPath, filename)).build();
        final DownloadListener listener = Mockito.mock(DownloadListener.class);
        task.enqueue(listener);
        assertThat(task.getListener()).isEqualTo(listener);
        final DownloadListener another = Mockito.mock(DownloadListener.class);
        task.replaceListener(another);
        assertThat(task.getListener()).isEqualTo(another);
    }

    @Test
    public void mockTaskForCompare_justId() {
        final IdentifiedTask task = DownloadTask.mockTaskForCompare(1);
        assertThat(task.getId()).isEqualTo(1);
        assertThat(task.getUrl()).isEqualTo(EMPTY_URL);
        assertThat(task.getFilename()).isNull();
        assertThat(task.getParentFile()).isEqualTo(EMPTY_FILE);
    }

    @Test
    public void redirectLocation() {
        final String redirectLocation = "http://redirect";
        DownloadTask task = new DownloadTask.Builder("https://jacksgong.com", new File(parentPath, filename)).build();
        task.setRedirectLocation(redirectLocation);
        assertThat(task.getRedirectLocation()).isEqualTo(redirectLocation);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();
}

