package com.liulishuo.okdownload;


import DownloadUrlConnection.Factory;
import OkDownload.Builder;
import android.content.Context;
import com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnCache;
import com.liulishuo.okdownload.core.breakpoint.DownloadStore;
import com.liulishuo.okdownload.core.connection.DownloadConnection;
import com.liulishuo.okdownload.core.dispatcher.CallbackDispatcher;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.liulishuo.okdownload.core.download.DownloadStrategy;
import com.liulishuo.okdownload.core.file.DownloadOutputStream;
import com.liulishuo.okdownload.core.file.ProcessFileStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static OkDownload.singleton;
import static OkDownloadProvider.context;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class OkDownloadTest {
    @Test(expected = IllegalArgumentException.class)
    public void setSingleInstance_onlyOnceValid() {
        OkDownload okDownload = Mockito.mock(OkDownload.class);
        OkDownload.setSingletonInstance(okDownload);
        assertThat(OkDownload.with()).isEqualTo(okDownload);
        // second time
        OkDownload.setSingletonInstance(Mockito.mock(OkDownload.class));
    }

    @Test
    public void create_NoParam() {
        OkDownload.Builder builder = new OkDownload.Builder(application);
        OkDownload okDownload = builder.build();
        assertThat(okDownload.downloadDispatcher()).isInstanceOf(DownloadDispatcher.class);
        assertThat(okDownload.callbackDispatcher()).isInstanceOf(CallbackDispatcher.class);
        assertThat(okDownload.breakpointStore()).isInstanceOf(BreakpointStoreOnCache.class);
        assertThat(okDownload.connectionFactory()).isInstanceOf(Factory.class);
        assertThat(okDownload.outputStreamFactory()).isInstanceOf(DownloadOutputStream.Factory.class);
        assertThat(okDownload.processFileStrategy()).isInstanceOf(ProcessFileStrategy.class);
        assertThat(okDownload.downloadStrategy()).isInstanceOf(DownloadStrategy.class);
    }

    @Test
    public void create_WithParam() {
        final DownloadDispatcher mockDownloadDispatcher = Mockito.mock(DownloadDispatcher.class);
        final CallbackDispatcher mockCallbackDispatcher = Mockito.mock(CallbackDispatcher.class);
        final DownloadStore mockDownloadStore = Mockito.mock(DownloadStore.class);
        final DownloadConnection.Factory mockConnectionFactory = Mockito.mock(DownloadConnection.Factory.class);
        final DownloadOutputStream.Factory mockOutputFactory = Mockito.mock(DownloadOutputStream.Factory.class);
        final ProcessFileStrategy mockProcessFileStrategy = Mockito.mock(ProcessFileStrategy.class);
        final DownloadStrategy mockDownloadStrategy = Mockito.mock(DownloadStrategy.class);
        final DownloadMonitor mockMonitor = Mockito.mock(DownloadMonitor.class);
        OkDownload.Builder builder = new OkDownload.Builder(application).downloadDispatcher(mockDownloadDispatcher).callbackDispatcher(mockCallbackDispatcher).downloadStore(mockDownloadStore).connectionFactory(mockConnectionFactory).outputStreamFactory(mockOutputFactory).processFileStrategy(mockProcessFileStrategy).downloadStrategy(mockDownloadStrategy).monitor(mockMonitor);
        OkDownload okDownload = builder.build();
        assertThat(okDownload.downloadDispatcher()).isEqualTo(mockDownloadDispatcher);
        assertThat(okDownload.callbackDispatcher()).isEqualTo(mockCallbackDispatcher);
        assertThat(okDownload.breakpointStore()).isEqualTo(mockDownloadStore);
        assertThat(okDownload.connectionFactory()).isEqualTo(mockConnectionFactory);
        assertThat(okDownload.outputStreamFactory()).isEqualTo(mockOutputFactory);
        assertThat(okDownload.processFileStrategy()).isEqualTo(mockProcessFileStrategy);
        assertThat(okDownload.downloadStrategy()).isEqualTo(mockDownloadStrategy);
        assertThat(okDownload.monitor).isEqualTo(mockMonitor);
    }

    @Test(expected = IllegalStateException.class)
    public void with_noValidContext() {
        context = null;
        singleton = null;
        OkDownload.with();
    }

    @Test
    public void with_valid() {
        context = Mockito.mock(Context.class);
        singleton = null;
        assertThat(OkDownload.with()).isNotNull();
    }
}

