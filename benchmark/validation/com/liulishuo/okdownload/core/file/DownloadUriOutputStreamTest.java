/**
 * Copyright (c) 2018 LingoChamp Inc.
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
package com.liulishuo.okdownload.core.file;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.ParcelFileDescriptor;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SyncFailedException;
import java.nio.channels.FileChannel;
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
@Config(manifest = NONE, sdk = VERSION_CODES.LOLLIPOP)
public class DownloadUriOutputStreamTest {
    @Mock
    private FileChannel channel;

    @Mock
    private ParcelFileDescriptor pdf;

    @Mock
    private BufferedOutputStream out;

    @Mock
    private FileOutputStream fos;

    @Mock
    private FileDescriptor fd;

    private DownloadUriOutputStream outputStream;

    @Test(expected = FileNotFoundException.class)
    public void constructor_nullParcelFileDescriptor() throws FileNotFoundException {
        final Context context = Mockito.mock(Context.class);
        final ContentResolver resolver = Mockito.mock(ContentResolver.class);
        final Uri uri = Mockito.mock(Uri.class);
        Mockito.when(context.getContentResolver()).thenReturn(resolver);
        Mockito.when(resolver.openFileDescriptor(uri, "rw")).thenReturn(null);
        new DownloadUriOutputStream(context, uri, 1);
    }

    @Test
    public void constructor() throws IOException {
        final Context context = Mockito.mock(Context.class);
        final ContentResolver resolver = Mockito.mock(ContentResolver.class);
        final ParcelFileDescriptor pdf = Mockito.mock(ParcelFileDescriptor.class);
        final Uri uri = Mockito.mock(Uri.class);
        final FileDescriptor fd = Mockito.mock(FileDescriptor.class);
        Mockito.when(context.getContentResolver()).thenReturn(resolver);
        Mockito.when(resolver.openFileDescriptor(uri, "rw")).thenReturn(pdf);
        Mockito.when(pdf.getFileDescriptor()).thenReturn(fd);
        final DownloadUriOutputStream outputStream = new DownloadUriOutputStream(context, uri, 1);
        assertThat(outputStream.pdf).isEqualTo(pdf);
        assertThat(outputStream.out).isNotNull();
        assertThat(outputStream.fos.getFD()).isEqualTo(fd);
    }

    @Test
    public void write() throws Exception {
        byte[] bytes = new byte[2];
        outputStream.write(bytes, 0, 1);
        Mockito.verify(out).write(ArgumentMatchers.eq(bytes), ArgumentMatchers.eq(0), ArgumentMatchers.eq(1));
    }

    @Test
    public void close() throws Exception {
        outputStream.close();
        Mockito.verify(out).close();
        Mockito.verify(fos).close();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // because of we invoke the native method, so this expected is means to invoked fd.sync
    @Test
    public void flushAndSync() throws Exception {
        Mockito.when(pdf.getFileDescriptor()).thenReturn(fd);
        thrown.expect(SyncFailedException.class);
        thrown.expectMessage("sync failed");
        outputStream.flushAndSync();
        Mockito.verify(out).flush();
    }

    @Test
    public void seek() throws Exception {
        outputStream.seek(1);
        Mockito.verify(channel).position(ArgumentMatchers.eq(1L));
    }

    @Test
    public void setLength() {
        outputStream.setLength(1);
        Mockito.verify(pdf).getFileDescriptor();
    }

    @Test
    public void factory() throws IOException {
        assertThat(new DownloadUriOutputStream.Factory().supportSeek()).isTrue();
        final Context context = Mockito.mock(Context.class);
        final ContentResolver resolver = Mockito.mock(ContentResolver.class);
        final ParcelFileDescriptor pdf = Mockito.mock(ParcelFileDescriptor.class);
        final FileDescriptor fd = Mockito.mock(FileDescriptor.class);
        final Uri uri = Mockito.mock(Uri.class);
        Mockito.when(context.getContentResolver()).thenReturn(resolver);
        Mockito.when(resolver.openFileDescriptor(ArgumentMatchers.any(Uri.class), ArgumentMatchers.eq("rw"))).thenReturn(pdf);
        Mockito.when(pdf.getFileDescriptor()).thenReturn(fd);
        final File file = new File("/test");
        DownloadUriOutputStream outputStream = ((DownloadUriOutputStream) (new DownloadUriOutputStream.Factory().create(context, file, 1)));
        assertThat(outputStream.pdf).isEqualTo(pdf);
        assertThat(outputStream.out).isNotNull();
        assertThat(outputStream.fos.getFD()).isEqualTo(fd);
        outputStream = ((DownloadUriOutputStream) (new DownloadUriOutputStream.Factory().create(context, uri, 1)));
        assertThat(outputStream.pdf).isEqualTo(pdf);
        assertThat(outputStream.out).isNotNull();
        assertThat(outputStream.fos.getFD()).isEqualTo(fd);
    }
}

