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
package com.liulishuo.okdownload.core.download;


import ConnectivityManager.TYPE_WIFI;
import ContentResolver.SCHEME_CONTENT;
import Context.CONNECTIVITY_SERVICE;
import DownloadConnection.Connected;
import DownloadStrategy.FilenameHolder;
import DownloadStrategy.ResumeAvailableResponseCheck;
import PackageManager.PERMISSION_DENIED;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.TestUtils;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointStore;
import com.liulishuo.okdownload.core.breakpoint.DownloadStore;
import com.liulishuo.okdownload.core.exception.NetworkPolicyException;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class DownloadStrategyTest {
    private DownloadStrategy strategy;

    @Mock
    private DownloadTask task;

    @Mock
    private Connected connected;

    @Mock
    private BreakpointInfo info;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void resumeAvailableResponseCheck_PreconditionFailed() throws IOException {
        TestUtils.mockOkDownload();
        Mockito.doReturn(Mockito.spy(DownloadStrategy.class)).when(OkDownload.with()).downloadStrategy();
        final DownloadStrategy.ResumeAvailableResponseCheck responseCheck = resumeAvailableResponseCheck();
        Mockito.when(info.getBlock(0)).thenReturn(Mockito.mock(BlockInfo.class));
        Mockito.when(connected.getResponseCode()).thenReturn(HttpURLConnection.HTTP_PRECON_FAILED);
        expectResumeFailed(RESPONSE_PRECONDITION_FAILED);
        responseCheck.inspect();
    }

    @Test
    public void resumeAvailableResponseCheck_EtagChangedFromNone() throws IOException {
        final DownloadStrategy.ResumeAvailableResponseCheck responseCheck = resumeAvailableResponseCheck();
        Mockito.when(connected.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        Mockito.when(connected.getResponseHeaderField("Etag")).thenReturn("new-etag");
        Mockito.when(info.getBlock(0)).thenReturn(Mockito.mock(BlockInfo.class));
        responseCheck.inspect();
    }

    @Test
    public void resumeAvailableResponseCheck_EtagChanged() throws IOException {
        TestUtils.mockOkDownload();
        Mockito.doReturn(Mockito.spy(DownloadStrategy.class)).when(OkDownload.with()).downloadStrategy();
        final DownloadStrategy.ResumeAvailableResponseCheck responseCheck = resumeAvailableResponseCheck();
        Mockito.when(info.getBlock(0)).thenReturn(Mockito.mock(BlockInfo.class));
        Mockito.when(connected.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        Mockito.when(connected.getResponseHeaderField("Etag")).thenReturn("new-etag");
        Mockito.when(info.getEtag()).thenReturn("old-etag");
        expectResumeFailed(RESPONSE_ETAG_CHANGED);
        responseCheck.inspect();
    }

    @Test
    public void resumeAvailableResponseCheck_CreatedWithoutFrom0() throws IOException {
        TestUtils.mockOkDownload();
        Mockito.doReturn(Mockito.spy(DownloadStrategy.class)).when(OkDownload.with()).downloadStrategy();
        final DownloadStrategy.ResumeAvailableResponseCheck responseCheck = resumeAvailableResponseCheck();
        Mockito.when(connected.getResponseCode()).thenReturn(HttpURLConnection.HTTP_CREATED);
        final BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
        Mockito.when(blockInfo.getCurrentOffset()).thenReturn(100L);
        Mockito.when(info.getBlock(0)).thenReturn(blockInfo);
        expectResumeFailed(RESPONSE_CREATED_RANGE_NOT_FROM_0);
        responseCheck.inspect();
    }

    @Test
    public void resumeAvailableResponseCheck_ResetWithoutFrom0() throws IOException {
        TestUtils.mockOkDownload();
        Mockito.doReturn(Mockito.spy(DownloadStrategy.class)).when(OkDownload.with()).downloadStrategy();
        final DownloadStrategy.ResumeAvailableResponseCheck responseCheck = resumeAvailableResponseCheck();
        Mockito.when(connected.getResponseCode()).thenReturn(HttpURLConnection.HTTP_RESET);
        final BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
        Mockito.when(blockInfo.getCurrentOffset()).thenReturn(100L);
        Mockito.when(info.getBlock(0)).thenReturn(blockInfo);
        expectResumeFailed(RESPONSE_RESET_RANGE_NOT_FROM_0);
        responseCheck.inspect();
    }

    @Test
    public void resumeAvailableResponseCheck_notPartialAndOk() throws IOException {
        TestUtils.mockOkDownload();
        Mockito.doReturn(Mockito.spy(DownloadStrategy.class)).when(OkDownload.with()).downloadStrategy();
        final DownloadStrategy.ResumeAvailableResponseCheck responseCheck = resumeAvailableResponseCheck();
        Mockito.when(connected.getResponseCode()).thenReturn(501);
        Mockito.when(info.getBlock(0)).thenReturn(Mockito.mock(BlockInfo.class));
        expectServerCancelled(501, 0);
        responseCheck.inspect();
    }

    @Test
    public void resumeAvailableResponseCheck_okNotFrom0() throws IOException {
        TestUtils.mockOkDownload();
        Mockito.doReturn(Mockito.spy(DownloadStrategy.class)).when(OkDownload.with()).downloadStrategy();
        final DownloadStrategy.ResumeAvailableResponseCheck responseCheck = resumeAvailableResponseCheck();
        Mockito.when(connected.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        final BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
        Mockito.when(blockInfo.getCurrentOffset()).thenReturn(100L);
        Mockito.when(info.getBlock(0)).thenReturn(blockInfo);
        expectServerCancelled(HttpURLConnection.HTTP_OK, 100L);
        responseCheck.inspect();
    }

    @Test
    public void determineBlockCount() {
        Mockito.when(task.getSetConnectionCount()).thenReturn(null);
        // less than 1M
        assertThat(strategy.determineBlockCount(task, 500)).isEqualTo(1);
        assertThat(strategy.determineBlockCount(task, (900 * 1024))).isEqualTo(1);
        // less than 5M
        assertThat(strategy.determineBlockCount(task, ((2 * 1024) * 1024))).isEqualTo(2);
        assertThat(strategy.determineBlockCount(task, ((long) ((4.9 * 1024) * 1024)))).isEqualTo(2);
        // less than 50M
        assertThat(strategy.determineBlockCount(task, ((18 * 1024) * 1024))).isEqualTo(3);
        assertThat(strategy.determineBlockCount(task, ((49 * 1024) * 1024))).isEqualTo(3);
        // less than 100M
        assertThat(strategy.determineBlockCount(task, ((66 * 1024) * 1024))).isEqualTo(4);
        assertThat(strategy.determineBlockCount(task, ((99 * 1024) * 1024))).isEqualTo(4);
        // more than 100M
        assertThat(strategy.determineBlockCount(task, ((1000 * 1024) * 1024))).isEqualTo(5);
        assertThat(strategy.determineBlockCount(task, ((5323L * 1024) * 1024))).isEqualTo(5);
        // task has connection count
        Mockito.when(task.getSetConnectionCount()).thenReturn(100);
        assertThat(strategy.determineBlockCount(task, 500)).isEqualTo(100);
    }

    @Test
    public void isUseMultiBlock() throws IOException {
        TestUtils.mockOkDownload();
        Mockito.when(OkDownload.with().outputStreamFactory().supportSeek()).thenReturn(false);
        assertThat(strategy.isUseMultiBlock(false)).isFalse();
        assertThat(strategy.isUseMultiBlock(true)).isFalse();
        Mockito.when(OkDownload.with().outputStreamFactory().supportSeek()).thenReturn(true);
        assertThat(strategy.isUseMultiBlock(true)).isTrue();
    }

    @Test
    public void validFilenameResume() {
        final String taskFilename = "task-filename";
        Mockito.when(task.getFilename()).thenReturn(taskFilename);
        final DownloadStrategy.FilenameHolder filenameHolder = Mockito.mock(FilenameHolder.class);
        Mockito.when(task.getFilenameHolder()).thenReturn(filenameHolder);
        final String storeFilename = "store-filename";
        strategy.inspectFilenameFromResume(storeFilename, task);
        Mockito.verify(filenameHolder, Mockito.never()).set(ArgumentMatchers.anyString());
        Mockito.when(task.getFilename()).thenReturn(null);
        strategy.inspectFilenameFromResume(storeFilename, task);
        Mockito.verify(filenameHolder).set(storeFilename);
    }

    @Test
    public void validFilenameFromStore() {
        final DownloadStrategy.FilenameHolder holder = new DownloadStrategy.FilenameHolder();
        Mockito.when(task.getUrl()).thenReturn("url");
        Mockito.when(task.getFilenameHolder()).thenReturn(holder);
        final BreakpointStore store = OkDownload.with().breakpointStore();
        Mockito.doReturn(null).when(store).getResponseFilename("url");
        assertThat(strategy.validFilenameFromStore(task)).isFalse();
        assertThat(holder.get()).isNull();
        Mockito.doReturn("filename").when(store).getResponseFilename("url");
        assertThat(strategy.validFilenameFromStore(task)).isTrue();
        assertThat(holder.get()).isEqualTo("filename");
    }

    @Test
    public void validInfoOnCompleted_storeValid() {
        final DownloadStore store = Mockito.mock(DownloadStore.class);
        final DownloadTask task = new DownloadTask.Builder("https://jacksgong.com", "path", "name").build();
        Mockito.when(store.getAfterCompleted(task.getId())).thenReturn(info);
        strategy.validInfoOnCompleted(task, store);
        assertThat(task.getInfo()).isEqualTo(info);
    }

    @Test
    public void validInfoOnCompleted_InfoNotOnStore_UriScheme() {
        final Uri contentUri = Mockito.mock(Uri.class);
        Mockito.when(contentUri.getScheme()).thenReturn(SCHEME_CONTENT);
        Mockito.when(contentUri.getPath()).thenReturn("content://1");
        final ContentResolver resolver = Mockito.mock(ContentResolver.class);
        final OkDownload okDownload = OkDownload.with();
        final Context context = Mockito.mock(Context.class);
        Mockito.when(okDownload.context()).thenReturn(context);
        Mockito.when(context.getContentResolver()).thenReturn(resolver);
        final Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(resolver.query(contentUri, null, null, null, null)).thenReturn(cursor);
        Mockito.when(cursor.getLong(ArgumentMatchers.anyInt())).thenReturn(1L);
        final DownloadTask task = build();
        strategy.validInfoOnCompleted(task, Mockito.mock(DownloadStore.class));
        final BreakpointInfo info = task.getInfo();
        assertThat(info.getId()).isEqualTo(task.getId());
        assertThat(info.getTotalLength()).isEqualTo(1L);
        assertThat(info.getTotalOffset()).isEqualTo(1L);
    }

    @Test
    public void validInfoOnCompleted_InfoNotOnStore_FileScheme() throws IOException {
        TestUtils.mockOkDownload();
        final DownloadTask task = Mockito.spy(new DownloadTask.Builder("https://jacksgong.com", "path", "name").build());
        // null file
        Mockito.doReturn(null).when(task).getFile();
        strategy.validInfoOnCompleted(task, Mockito.mock(DownloadStore.class));
        BreakpointInfo info = task.getInfo();
        assertThat(info.getId()).isEqualTo(task.getId());
        assertThat(info.getTotalLength()).isEqualTo(0);
        assertThat(info.getTotalOffset()).isEqualTo(0);
        // valid file
        final File file = Mockito.mock(File.class);
        Mockito.doReturn(file).when(task).getFile();
        Mockito.doReturn(1L).when(file).length();
        strategy.validInfoOnCompleted(task, Mockito.mock(DownloadStore.class));
        info = task.getInfo();
        assertThat(info.getId()).isEqualTo(task.getId());
        assertThat(info.getTotalLength()).isEqualTo(1L);
        assertThat(info.getTotalOffset()).isEqualTo(1L);
    }

    @Test
    public void validFilenameFromResponse() throws IOException {
        final String taskFilename = "task-filename";
        Mockito.when(task.getFilename()).thenReturn(taskFilename);
        final DownloadStrategy.FilenameHolder filenameHolder = Mockito.mock(FilenameHolder.class);
        Mockito.when(task.getFilenameHolder()).thenReturn(filenameHolder);
        final String responseFilename = "response-filename";
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        final DownloadStrategy.FilenameHolder infoFilenameHolder = Mockito.mock(FilenameHolder.class);
        Mockito.when(info.getFilenameHolder()).thenReturn(infoFilenameHolder);
        final String determineFilename = "determine-filename";
        Mockito.doReturn(determineFilename).when(strategy).determineFilename(responseFilename, task);
        strategy.validFilenameFromResponse(responseFilename, task, info);
        Mockito.verify(filenameHolder, Mockito.never()).set(ArgumentMatchers.anyString());
        Mockito.when(task.getFilename()).thenReturn(null);
        strategy.validFilenameFromResponse(responseFilename, task, info);
        Mockito.verify(filenameHolder).set(determineFilename);
        Mockito.verify(infoFilenameHolder).set(determineFilename);
    }

    @Test
    public void determineFilename_tmpFilenameValid() throws IOException {
        final String validResponseFilename = "file name";
        String result = strategy.determineFilename(validResponseFilename, task);
        assertThat(result).isEqualTo(validResponseFilename);
        Mockito.when(task.getUrl()).thenReturn("https://jacksgong.com/okdownload.3_1.apk?abc&ddd");
        result = strategy.determineFilename(null, task);
        assertThat(result).isEqualTo("okdownload.3_1.apk");
        Mockito.when(task.getUrl()).thenReturn("https://jacksgong.com/dreamtobe.cn");
        result = strategy.determineFilename(null, task);
        assertThat(result).isEqualTo("dreamtobe.cn");
        Mockito.when(task.getUrl()).thenReturn("https://jacksgong.com/?abc");
        result = strategy.determineFilename(null, task);
        assertThat(result).isNotEmpty();
        Mockito.when(task.getUrl()).thenReturn("https://jacksgong.com/android-studio-ide-171.4408382-mac.dmg");
        result = strategy.determineFilename(null, task);
        assertThat(result).isEqualTo("android-studio-ide-171.4408382-mac.dmg");
    }

    @Test
    public void inspectNetworkOnWifi() throws IOException {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.isWifiRequired()).thenReturn(true);
        final Context context = Mockito.mock(Context.class);
        final OkDownload okDownload = OkDownload.with();
        Mockito.doReturn(context).when(okDownload).context();
        Mockito.doReturn(PERMISSION_DENIED).when(context).checkCallingOrSelfPermission(ArgumentMatchers.anyString());
        thrown.expect(IOException.class);
        thrown.expectMessage(("required for access network state but don't have the " + (("permission of Manifest.permission.ACCESS_NETWORK_STATE, please declare this " + "permission first on your AndroidManifest, so we can handle the case of ") + "downloading required wifi state.")));
        strategy.inspectNetworkOnWifi(task);
        strategy.isHasAccessNetworkStatePermission = true;
        final ConnectivityManager manager = Mockito.mock(ConnectivityManager.class);
        Mockito.doReturn(manager).when(context).getSystemService(ArgumentMatchers.eq(CONNECTIVITY_SERVICE));
        Mockito.doReturn(null).when(manager).getActiveNetworkInfo();
        thrown.expect(NetworkPolicyException.class);
        thrown.expectMessage("Only allows downloading this task on the wifi network type!");
        strategy.inspectNetworkOnWifi(task);
        final NetworkInfo info = Mockito.mock(NetworkInfo.class);
        Mockito.doReturn(info).when(manager).getActiveNetworkInfo();
        Mockito.doReturn(TYPE_WIFI).when(info).getType();
        strategy.inspectNetworkOnWifi(task);
    }

    @Test
    public void inspectNetworkAvailable_noPermission() throws UnknownHostException {
        final Context context = Mockito.mock(Context.class);
        final OkDownload okDownload = OkDownload.with();
        Mockito.doReturn(context).when(okDownload).context();
        final ConnectivityManager manager = Mockito.mock(ConnectivityManager.class);
        Mockito.doReturn(manager).when(context).getSystemService(ArgumentMatchers.eq(CONNECTIVITY_SERVICE));
        final NetworkInfo info = Mockito.mock(NetworkInfo.class);
        Mockito.doReturn(info).when(manager).getActiveNetworkInfo();
        Mockito.doReturn(true).when(info).isConnected();
        strategy.isHasAccessNetworkStatePermission = false;
        strategy.inspectNetworkAvailable();
    }

    @Test
    public void inspectNetworkAvailable_withPermission() throws UnknownHostException {
        final Context context = Mockito.mock(Context.class);
        final OkDownload okDownload = OkDownload.with();
        Mockito.doReturn(context).when(okDownload).context();
        final ConnectivityManager manager = Mockito.mock(ConnectivityManager.class);
        Mockito.doReturn(manager).when(context).getSystemService(ArgumentMatchers.eq(CONNECTIVITY_SERVICE));
        final NetworkInfo info = Mockito.mock(NetworkInfo.class);
        Mockito.doReturn(info).when(manager).getActiveNetworkInfo();
        Mockito.doReturn(true).when(info).isConnected();
        strategy.isHasAccessNetworkStatePermission = true;
        // no throw
        strategy.inspectNetworkAvailable();
        Mockito.doReturn(null).when(context).getSystemService(ArgumentMatchers.eq(CONNECTIVITY_SERVICE));
        // no throw
        strategy.inspectNetworkAvailable();
        Mockito.doReturn(manager).when(context).getSystemService(ArgumentMatchers.eq(CONNECTIVITY_SERVICE));
        Mockito.doReturn(null).when(manager).getActiveNetworkInfo();
        thrown.expect(UnknownHostException.class);
        thrown.expectMessage("network is not available!");
        strategy.inspectNetworkAvailable();
        Mockito.doReturn(false).when(info).isConnected();
        thrown.expect(UnknownHostException.class);
        thrown.expectMessage("network is not available!");
        strategy.inspectNetworkAvailable();
    }
}

