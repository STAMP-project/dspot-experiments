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
import DownloadContext.Builder;
import DownloadContext.QueueAttachListener;
import DownloadContext.QueueSet;
import android.net.Uri;
import android.os.Handler;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.liulishuo.okdownload.core.listener.DownloadListenerBunch;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class DownloadContextTest {
    @Mock
    private DownloadListener listener;

    @Mock
    private DownloadContextListener queueListener;

    private DownloadContext context;

    private DownloadTask[] tasks;

    private Builder builder;

    private QueueSet queueSet;

    private String filePath = "./exist-file";

    @Test
    public void startOnSerial() {
        Mockito.doNothing().when(context).start(ArgumentMatchers.eq(listener), ArgumentMatchers.anyBoolean());
        context.startOnSerial(listener);
        Mockito.verify(context).start(ArgumentMatchers.eq(listener), ArgumentMatchers.eq(true));
    }

    @Test
    public void startOnParallel() {
        Mockito.doNothing().when(context).start(ArgumentMatchers.eq(listener), ArgumentMatchers.anyBoolean());
        context.startOnParallel(listener);
        Mockito.verify(context).start(ArgumentMatchers.eq(listener), ArgumentMatchers.eq(false));
    }

    @Test
    public void start_serialNonStartedCallbackToUI() throws IOException {
        TestUtils.mockOkDownload();
        final DownloadTask[] tasks = new DownloadTask[2];
        tasks[0] = Mockito.spy(new DownloadTask.Builder("url1", "path", "filename1").build());
        tasks[1] = Mockito.spy(new DownloadTask.Builder("url2", "path", "filename1").build());
        DownloadTask task = tasks[0];
        Mockito.when(task.isAutoCallbackToUIThread()).thenReturn(true);
        task = tasks[1];
        Mockito.when(task.isAutoCallbackToUIThread()).thenReturn(false);
        final Handler handler = Mockito.mock(Handler.class);
        Mockito.when(handler.post(ArgumentMatchers.any(Runnable.class))).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                final Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }
        });
        context = Mockito.spy(new DownloadContext(tasks, queueListener, queueSet, handler));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                final Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }
        }).when(context).executeOnSerialExecutor(ArgumentMatchers.any(Runnable.class));
        Mockito.doNothing().when(tasks[0]).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.doNothing().when(tasks[1]).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.when(context.isStarted()).thenReturn(false);
        context.start(listener, true);
        Mockito.verify(context).executeOnSerialExecutor(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(tasks[0], Mockito.never()).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.verify(tasks[1], Mockito.never()).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.verify(handler).post(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(queueListener).queueEnd(ArgumentMatchers.eq(context));
    }

    @Test
    public void start_serialNonStartedCallbackDirectly() throws IOException {
        TestUtils.mockOkDownload();
        final DownloadTask[] tasks = new DownloadTask[2];
        tasks[0] = Mockito.spy(new DownloadTask.Builder("url1", "path", "filename1").build());
        tasks[1] = Mockito.spy(new DownloadTask.Builder("url2", "path", "filename1").build());
        DownloadTask task = tasks[0];
        Mockito.when(task.isAutoCallbackToUIThread()).thenReturn(true);
        task = tasks[1];
        Mockito.when(task.isAutoCallbackToUIThread()).thenReturn(false);
        final Handler handler = Mockito.mock(Handler.class);
        Mockito.when(handler.post(ArgumentMatchers.any(Runnable.class))).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                final Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }
        });
        context = Mockito.spy(new DownloadContext(tasks, queueListener, queueSet, handler));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                final Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }
        }).when(context).executeOnSerialExecutor(ArgumentMatchers.any(Runnable.class));
        Mockito.doNothing().when(tasks[0]).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.doNothing().when(tasks[1]).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.when(context.isStarted()).thenReturn(true, false);
        context.start(listener, true);
        Mockito.verify(context).executeOnSerialExecutor(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(tasks[0]).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.verify(tasks[1], Mockito.never()).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.verify(handler, Mockito.never()).post(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(queueListener).queueEnd(ArgumentMatchers.eq(context));
    }

    @Test
    public void start_withoutQueueListener() throws IOException {
        TestUtils.mockOkDownload();
        // without queue listener
        final DownloadTask[] tasks = new DownloadTask[2];
        tasks[0] = Mockito.spy(new DownloadTask.Builder("url1", "path", "filename1").build());
        tasks[1] = Mockito.spy(new DownloadTask.Builder("url2", "path", "filename1").build());
        context = Mockito.spy(new DownloadContext(tasks, null, queueSet));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                final Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }
        }).when(context).executeOnSerialExecutor(ArgumentMatchers.any(Runnable.class));
        Mockito.doNothing().when(tasks[0]).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.doNothing().when(tasks[1]).execute(ArgumentMatchers.any(DownloadListener.class));
        assertThat(context.isStarted()).isFalse();
        context.start(listener, true);
        Mockito.verify(context).executeOnSerialExecutor(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(tasks[0]).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.verify(tasks[1]).execute(ArgumentMatchers.any(DownloadListener.class));
        assertThat(context.isStarted()).isTrue();
        context.start(listener, false);
        final DownloadDispatcher dispatcher = OkDownload.with().downloadDispatcher();
        Mockito.verify(dispatcher).enqueue(tasks);
        assertThat(tasks[0].getListener()).isEqualTo(listener);
        assertThat(tasks[1].getListener()).isEqualTo(listener);
    }

    @Test
    public void start_withQueueListener() throws IOException {
        TestUtils.mockOkDownload();
        // with queue listener
        final DownloadTask[] tasks = new DownloadTask[2];
        tasks[0] = new DownloadTask.Builder("url1", "path", "filename1").build();
        tasks[1] = new DownloadTask.Builder("url2", "path", "filename1").build();
        context = Mockito.spy(new DownloadContext(tasks, queueListener, queueSet));
        Mockito.doNothing().when(context).executeOnSerialExecutor(ArgumentMatchers.any(Runnable.class));
        context.start(listener, false);
        final DownloadDispatcher dispatcher = OkDownload.with().downloadDispatcher();
        Mockito.verify(dispatcher).enqueue(tasks);
        assertThat(tasks[0].getListener()).isEqualTo(tasks[1].getListener());
        final DownloadListener taskListener = tasks[0].getListener();
        assertThat(taskListener).isExactlyInstanceOf(DownloadListenerBunch.class);
        assertThat(((DownloadListenerBunch) (taskListener)).contain(listener)).isTrue();
        // provided listener must callback before queue-attached-listener.
        assertThat(((DownloadListenerBunch) (taskListener)).indexOf(listener)).isZero();
    }

    @Test
    public void stop() {
        context.started = true;
        context.stop();
        assertThat(context.isStarted()).isFalse();
        Mockito.verify(OkDownload.with().downloadDispatcher()).cancel(tasks);
    }

    @Test
    public void builder_bindSetTask() {
        final DownloadTask mockTask = Mockito.mock(DownloadTask.class);
        builder.bindSetTask(mockTask);
        assertThat(builder.boundTaskList).containsExactly(mockTask);
    }

    @Test
    public void setListener() {
        final DownloadContextListener listener = Mockito.mock(DownloadContextListener.class);
        builder.setListener(listener);
        final DownloadContext context = builder.build();
        assertThat(context.contextListener).isEqualTo(listener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void builder_bind_noSetUri() {
        builder.bind("url");
    }

    @Test
    public void builder_bind() throws IOException {
        TestUtils.mockOkDownload();
        final String url = "url";
        final Uri uri = Mockito.mock(Uri.class);
        Mockito.when(uri.getScheme()).thenReturn(SCHEME_FILE);
        Mockito.when(uri.getPath()).thenReturn("");
        queueSet.setParentPathUri(uri);
        assertThat(queueSet.getDirUri()).isEqualTo(uri);
        builder.bind(url);
        final DownloadTask addedTask = builder.boundTaskList.get(0);
        assertThat(addedTask.getUrl()).isEqualTo(url);
        assertThat(addedTask.getUri()).isEqualTo(uri);
        final DownloadTask.Builder taskBuilder = Mockito.mock(DownloadTask.Builder.class);
        final HashMap headerMap = Mockito.mock(HashMap.class);
        queueSet.setHeaderMapFields(headerMap);
        builder.bind(taskBuilder);
        Mockito.verify(taskBuilder).setHeaderMapFields(headerMap);
        final int readBufferSize = 1;
        queueSet.setReadBufferSize(readBufferSize);
        assertThat(queueSet.getReadBufferSize()).isEqualTo(readBufferSize);
        builder.bind(taskBuilder);
        Mockito.verify(taskBuilder).setReadBufferSize(ArgumentMatchers.eq(readBufferSize));
        final int flushBufferSize = 2;
        queueSet.setFlushBufferSize(flushBufferSize);
        assertThat(queueSet.getFlushBufferSize()).isEqualTo(flushBufferSize);
        builder.bind(taskBuilder);
        Mockito.verify(taskBuilder).setFlushBufferSize(ArgumentMatchers.eq(flushBufferSize));
        final int syncBufferSize = 3;
        queueSet.setSyncBufferSize(syncBufferSize);
        assertThat(queueSet.getSyncBufferSize()).isEqualTo(syncBufferSize);
        builder.bind(taskBuilder);
        Mockito.verify(taskBuilder).setSyncBufferSize(ArgumentMatchers.eq(syncBufferSize));
        final int syncBufferIntervalMillis = 4;
        queueSet.setSyncBufferIntervalMillis(syncBufferIntervalMillis);
        assertThat(queueSet.getSyncBufferIntervalMillis()).isEqualTo(syncBufferIntervalMillis);
        builder.bind(taskBuilder);
        Mockito.verify(taskBuilder).setSyncBufferIntervalMillis(ArgumentMatchers.eq(syncBufferIntervalMillis));
        final boolean autoCallbackToUIThread = false;
        queueSet.setAutoCallbackToUIThread(autoCallbackToUIThread);
        assertThat(queueSet.isAutoCallbackToUIThread()).isEqualTo(autoCallbackToUIThread);
        builder.bind(taskBuilder);
        Mockito.verify(taskBuilder).setAutoCallbackToUIThread(ArgumentMatchers.eq(autoCallbackToUIThread));
        final int minIntervalMillisCallbackProgress = 5;
        queueSet.setMinIntervalMillisCallbackProcess(minIntervalMillisCallbackProgress);
        assertThat(queueSet.getMinIntervalMillisCallbackProcess()).isEqualTo(minIntervalMillisCallbackProgress);
        builder.bind(taskBuilder);
        Mockito.verify(taskBuilder).setMinIntervalMillisCallbackProcess(ArgumentMatchers.eq(minIntervalMillisCallbackProgress));
        final Object tag = Mockito.mock(Object.class);
        queueSet.setTag(tag);
        assertThat(queueSet.getTag()).isEqualTo(tag);
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.doReturn(task).when(taskBuilder).build();
        builder.bind(taskBuilder);
        Mockito.verify(task).setTag(tag);
        queueSet.setPassIfAlreadyCompleted(true);
        assertThat(queueSet.isPassIfAlreadyCompleted()).isTrue();
        builder.bind(taskBuilder);
        Mockito.verify(taskBuilder).setPassIfAlreadyCompleted(ArgumentMatchers.eq(true));
        queueSet.setWifiRequired(true);
        assertThat(queueSet.isWifiRequired()).isTrue();
        builder.bind(taskBuilder);
        Mockito.verify(taskBuilder).setWifiRequired(ArgumentMatchers.eq(true));
        queueSet.commit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void setParentFile() throws IOException {
        final String parentPath = "./parent";
        final File parentPathFile = new File(parentPath);
        queueSet.setParentPath(parentPath);
        assertThat(queueSet.getDirUri().getPath()).isEqualTo(parentPathFile.getAbsolutePath());
        queueSet.setParentPathFile(parentPathFile);
        assertThat(queueSet.getDirUri().getPath()).isEqualTo(parentPathFile.getAbsolutePath());
        File file = new File(filePath);
        file.createNewFile();
        queueSet.setParentPathFile(file);
    }

    @Test
    public void unbind() {
        final DownloadTask mockTask = Mockito.mock(DownloadTask.class);
        final int id = 1;
        Mockito.when(mockTask.getId()).thenReturn(id);
        builder.boundTaskList.add(mockTask);
        builder.unbind(id);
        assertThat(builder.boundTaskList).isEmpty();
        builder.boundTaskList.add(mockTask);
        builder.unbind(mockTask);
        assertThat(builder.boundTaskList).isEmpty();
    }

    @Test
    public void replaceTask() {
        final DownloadTask oldTask = tasks[0];
        final DownloadTask newTask = Mockito.mock(DownloadTask.class);
        context.alter().replaceTask(oldTask, newTask);
        assertThat(tasks[0]).isEqualTo(newTask);
    }

    @Test
    public void queueAttachListener() {
        final DownloadContextListener contextListener = Mockito.mock(DownloadContextListener.class);
        final DownloadContext.QueueAttachListener attachListener = new DownloadContext.QueueAttachListener(context, contextListener, 2);
        final DownloadTask task1 = Mockito.mock(DownloadTask.class);
        final DownloadTask task2 = Mockito.mock(DownloadTask.class);
        attachListener.taskStart(task1);
        attachListener.taskStart(task2);
        final EndCause endCause = Mockito.mock(EndCause.class);
        final Exception realCause = Mockito.mock(Exception.class);
        attachListener.taskEnd(task1, endCause, realCause);
        Mockito.verify(contextListener).taskEnd(ArgumentMatchers.eq(context), ArgumentMatchers.eq(task1), ArgumentMatchers.eq(endCause), ArgumentMatchers.eq(realCause), ArgumentMatchers.eq(1));
        Mockito.verify(contextListener, Mockito.never()).queueEnd(ArgumentMatchers.eq(context));
        attachListener.taskEnd(task2, endCause, realCause);
        Mockito.verify(contextListener).taskEnd(ArgumentMatchers.eq(context), ArgumentMatchers.eq(task2), ArgumentMatchers.eq(endCause), ArgumentMatchers.eq(realCause), ArgumentMatchers.eq(0));
        Mockito.verify(contextListener).queueEnd(ArgumentMatchers.eq(context));
    }
}

