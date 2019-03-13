package com.bumptech.glide.load.data.mediastore;


import MediaStore.Video.Thumbnails;
import RuntimeEnvironment.application;
import ThumbFetcher.ImageThumbnailQuery;
import ThumbFetcher.VideoThumbnailQuery;
import android.database.MatrixCursor;
import android.net.Uri;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool;
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboCursor;

import static MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class ThumbnailStreamOpenerTest {
    private ThumbnailStreamOpenerTest.Harness harness;

    @Test
    public void testReturnsNullIfCursorIsNull() throws FileNotFoundException {
        Mockito.when(harness.query.query(ArgumentMatchers.eq(harness.uri))).thenReturn(null);
        Assert.assertNull(harness.get().open(harness.uri));
    }

    @Test
    public void testReturnsNullIfCursorIsEmpty() throws FileNotFoundException {
        Mockito.when(harness.query.query(ArgumentMatchers.eq(harness.uri))).thenReturn(new MatrixCursor(new String[1]));
        Assert.assertNull(harness.get().open(harness.uri));
    }

    @Test
    public void testReturnsNullIfCursorHasEmptyPath() throws FileNotFoundException {
        MatrixCursor cursor = new MatrixCursor(new String[1]);
        cursor.addRow(new Object[]{ "" });
        Mockito.when(harness.query.query(ArgumentMatchers.eq(harness.uri))).thenReturn(cursor);
        Assert.assertNull(harness.get().open(harness.uri));
    }

    @Test
    public void testReturnsNullIfFileDoesNotExist() throws FileNotFoundException {
        Mockito.when(harness.service.get(ArgumentMatchers.anyString())).thenReturn(harness.file);
        Mockito.when(harness.service.exists(ArgumentMatchers.eq(harness.file))).thenReturn(false);
        Assert.assertNull(harness.get().open(harness.uri));
    }

    @Test
    public void testReturnNullIfFileLengthIsZero() throws FileNotFoundException {
        Mockito.when(harness.service.get(ArgumentMatchers.anyString())).thenReturn(harness.file);
        Mockito.when(harness.service.length(ArgumentMatchers.eq(harness.file))).thenReturn(0L);
        Assert.assertNull(harness.get().open(harness.uri));
    }

    @Test
    public void testClosesCursor() throws FileNotFoundException {
        harness.get().open(harness.uri);
        Assert.assertTrue(harness.cursor.isClosed());
    }

    @Test
    public void testReturnsOpenedInputStreamWhenFileFound() throws FileNotFoundException {
        InputStream expected = new ByteArrayInputStream(new byte[0]);
        Shadows.shadowOf(application.getContentResolver()).registerInputStream(harness.uri, expected);
        Assert.assertEquals(expected, harness.get().open(harness.uri));
    }

    @Test
    public void open_returnsNull_whenQueryThrowsSecurityException() throws FileNotFoundException {
        Mockito.when(harness.query.query(ArgumentMatchers.any(Uri.class))).thenThrow(new SecurityException());
        assertThat(harness.get().open(harness.uri)).isNull();
    }

    @Test
    public void testVideoQueryReturnsVideoCursor() {
        Uri queryUri = Thumbnails.EXTERNAL_CONTENT_URI;
        ThumbFetcher.VideoThumbnailQuery query = new ThumbFetcher.VideoThumbnailQuery(ThumbnailStreamOpenerTest.getContentResolver());
        RoboCursor testCursor = new RoboCursor();
        Shadows.shadowOf(application.getContentResolver()).setCursor(queryUri, testCursor);
        Assert.assertEquals(testCursor, query.query(harness.uri));
    }

    @Test
    public void testImageQueryReturnsImageCursor() {
        Uri queryUri = EXTERNAL_CONTENT_URI;
        ThumbFetcher.ImageThumbnailQuery query = new ThumbFetcher.ImageThumbnailQuery(ThumbnailStreamOpenerTest.getContentResolver());
        RoboCursor testCursor = new RoboCursor();
        Shadows.shadowOf(application.getContentResolver()).setCursor(queryUri, testCursor);
        Assert.assertEquals(testCursor, query.query(harness.uri));
    }

    private static class Harness {
        final MatrixCursor cursor = new MatrixCursor(new String[1]);

        final File file = new File("fake/uri");

        final Uri uri = Uri.fromFile(file);

        final ThumbnailQuery query = Mockito.mock(ThumbnailQuery.class);

        final FileService service = Mockito.mock(FileService.class);

        final ArrayPool byteArrayPool = new LruArrayPool();

        Harness() {
            cursor.addRow(new String[]{ file.getAbsolutePath() });
            Mockito.when(query.query(ArgumentMatchers.eq(uri))).thenReturn(cursor);
            Mockito.when(service.get(ArgumentMatchers.eq(file.getAbsolutePath()))).thenReturn(file);
            Mockito.when(service.exists(ArgumentMatchers.eq(file))).thenReturn(true);
            Mockito.when(service.length(ArgumentMatchers.eq(file))).thenReturn(1L);
        }

        public ThumbnailStreamOpener get() {
            List<ImageHeaderParser> parsers = new ArrayList<>();
            parsers.add(new DefaultImageHeaderParser());
            return new ThumbnailStreamOpener(parsers, service, query, byteArrayPool, ThumbnailStreamOpenerTest.getContentResolver());
        }
    }
}

