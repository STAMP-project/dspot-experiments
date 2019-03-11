package org.robolectric.shadows;


import Bitmap.Config;
import Bitmap.Config.ALPHA_8;
import Bitmap.Config.ARGB_8888;
import BitmapFactory.Options;
import MediaStore.Images.Media;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Shadows;

import static org.robolectric.R.drawable.an_image;


@RunWith(AndroidJUnit4.class)
public class ShadowBitmapFactoryTest {
    private Application context;

    @Test
    public void decodeResource_shouldSetDescriptionAndCreatedFrom() {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), an_image);
        ShadowBitmap shadowBitmap = Shadows.shadowOf(bitmap);
        Assert.assertEquals("Bitmap for resource:org.robolectric:drawable/an_image", shadowBitmap.getDescription());
        Assert.assertEquals(an_image, shadowBitmap.getCreatedFromResId());
        Assert.assertEquals(64, bitmap.getWidth());
        Assert.assertEquals(53, bitmap.getHeight());
    }

    @Test
    public void decodeResource_shouldSetDefaultBitmapConfig() {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), an_image);
        assertThat(bitmap.getConfig()).isEqualTo(ARGB_8888);
        assertThat(bitmap.getRowBytes()).isNotEqualTo(0);
    }

    @Test
    public void withResId0_decodeResource_shouldReturnNull() {
        assertThat(BitmapFactory.decodeResource(context.getResources(), 0)).isNull();
    }

    @Test
    public void decodeResource_shouldPassABitmapConfig() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ALPHA_8;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), an_image, options);
        assertThat(bitmap.getConfig()).isEqualTo(ALPHA_8);
    }

    @Test
    public void decodeFile_shouldSetDescriptionAndCreatedFrom() {
        Bitmap bitmap = BitmapFactory.decodeFile("/some/file.jpg");
        ShadowBitmap shadowBitmap = Shadows.shadowOf(bitmap);
        Assert.assertEquals("Bitmap for file:/some/file.jpg", shadowBitmap.getDescription());
        Assert.assertEquals("/some/file.jpg", shadowBitmap.getCreatedFromPath());
        Assert.assertEquals(100, bitmap.getWidth());
        Assert.assertEquals(100, bitmap.getHeight());
    }

    @Test
    public void decodeStream_shouldSetDescriptionAndCreatedFrom() throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse("content:/path"));
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        ShadowBitmap shadowBitmap = Shadows.shadowOf(bitmap);
        Assert.assertEquals("Bitmap for content:/path", shadowBitmap.getDescription());
        Assert.assertEquals(inputStream, shadowBitmap.getCreatedFromStream());
        Assert.assertEquals(100, bitmap.getWidth());
        Assert.assertEquals(100, bitmap.getHeight());
        bitmap.getPixels(new int[(bitmap.getHeight()) * (bitmap.getWidth())], 0, 0, 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    @Test
    public void decodeBytes_shouldSetDescriptionAndCreatedFrom() throws Exception {
        byte[] yummyBites = "Hi!".getBytes("UTF-8");
        Bitmap bitmap = BitmapFactory.decodeByteArray(yummyBites, 100, 100);
        ShadowBitmap shadowBitmap = Shadows.shadowOf(bitmap);
        Assert.assertEquals("Bitmap for Hi! bytes 100..100", shadowBitmap.getDescription());
        Assert.assertEquals(yummyBites, shadowBitmap.getCreatedFromBytes());
        Assert.assertEquals(100, bitmap.getWidth());
        Assert.assertEquals(100, bitmap.getHeight());
    }

    @Test
    public void decodeStream_shouldSetDescriptionWithNullOptions() throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse("content:/path"));
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, null);
        Assert.assertEquals("Bitmap for content:/path", Shadows.shadowOf(bitmap).getDescription());
        Assert.assertEquals(100, bitmap.getWidth());
        Assert.assertEquals(100, bitmap.getHeight());
    }

    @Test
    public void decodeResource_shouldGetWidthAndHeightFromHints() {
        ShadowBitmapFactory.provideWidthAndHeightHints(an_image, 123, 456);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), an_image);
        Assert.assertEquals("Bitmap for resource:org.robolectric:drawable/an_image", Shadows.shadowOf(bitmap).getDescription());
        Assert.assertEquals(123, bitmap.getWidth());
        Assert.assertEquals(456, bitmap.getHeight());
    }

    @Test
    public void decodeResource_canTakeOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 100;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), an_image, options);
        Assert.assertEquals(true, Shadows.shadowOf(bitmap).getDescription().contains("inSampleSize=100"));
    }

    @Test
    public void decodeResourceStream_canTakeOptions() throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse("content:/path"));
        options.inSampleSize = 100;
        Bitmap bitmap = BitmapFactory.decodeResourceStream(context.getResources(), null, inputStream, null, options);
        Assert.assertEquals(true, Shadows.shadowOf(bitmap).getDescription().contains("inSampleSize=100"));
    }

    @Test
    public void decodeFile_shouldGetWidthAndHeightFromHints() {
        ShadowBitmapFactory.provideWidthAndHeightHints("/some/file.jpg", 123, 456);
        Bitmap bitmap = BitmapFactory.decodeFile("/some/file.jpg");
        Assert.assertEquals("Bitmap for file:/some/file.jpg", Shadows.shadowOf(bitmap).getDescription());
        Assert.assertEquals(123, bitmap.getWidth());
        Assert.assertEquals(456, bitmap.getHeight());
    }

    @Test
    public void decodeFileEtc_shouldSetOptionsOutWidthAndOutHeightFromHints() {
        ShadowBitmapFactory.provideWidthAndHeightHints("/some/file.jpg", 123, 456);
        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeFile("/some/file.jpg", options);
        Assert.assertEquals(123, options.outWidth);
        Assert.assertEquals(456, options.outHeight);
    }

    @Test
    public void decodeUri_shouldGetWidthAndHeightFromHints() throws Exception {
        ShadowBitmapFactory.provideWidthAndHeightHints(Uri.parse("content:/path"), 123, 456);
        Bitmap bitmap = Media.getBitmap(context.getContentResolver(), Uri.parse("content:/path"));
        Assert.assertEquals("Bitmap for content:/path", Shadows.shadowOf(bitmap).getDescription());
        Assert.assertEquals(123, bitmap.getWidth());
        Assert.assertEquals(456, bitmap.getHeight());
    }

    @SuppressWarnings("ObjectToString")
    @Test
    public void decodeFileDescriptor_shouldGetWidthAndHeightFromHints() throws Exception {
        File tmpFile = File.createTempFile("BitmapFactoryTest", null);
        try {
            tmpFile.deleteOnExit();
            try (FileInputStream is = new FileInputStream(tmpFile)) {
                FileDescriptor fd = is.getFD();
                ShadowBitmapFactory.provideWidthAndHeightHints(fd, 123, 456);
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd);
                Assert.assertEquals(("Bitmap for fd:" + fd), Shadows.shadowOf(bitmap).getDescription());
                Assert.assertEquals(123, bitmap.getWidth());
                Assert.assertEquals(456, bitmap.getHeight());
            }
        } finally {
            tmpFile.delete();
        }
    }

    @Test
    public void decodeByteArray_shouldGetWidthAndHeightFromHints() {
        String data = "arbitrary bytes";
        ShadowBitmapFactory.provideWidthAndHeightHints(Uri.parse(data), 123, 456);
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Assert.assertEquals(("Bitmap for " + data), Shadows.shadowOf(bitmap).getDescription());
        Assert.assertEquals(123, bitmap.getWidth());
        Assert.assertEquals(456, bitmap.getHeight());
    }

    @Test
    public void decodeByteArray_shouldIncludeOffsets() {
        String data = "arbitrary bytes";
        ShadowBitmapFactory.provideWidthAndHeightHints(Uri.parse(data), 123, 456);
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 1, ((bytes.length) - 2));
        Assert.assertEquals((("Bitmap for " + data) + " bytes 1..13"), Shadows.shadowOf(bitmap).getDescription());
    }

    @Test
    public void decodeStream_shouldGetWidthAndHeightFromHints() throws Exception {
        ShadowBitmapFactory.provideWidthAndHeightHints(Uri.parse("content:/path"), 123, 456);
        InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse("content:/path"));
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        Assert.assertEquals("Bitmap for content:/path", Shadows.shadowOf(bitmap).getDescription());
        Assert.assertEquals(123, bitmap.getWidth());
        Assert.assertEquals(456, bitmap.getHeight());
    }

    @Test
    public void decodeStream_shouldGetWidthAndHeightFromActualImage() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("res/drawable/fourth_image.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        Assert.assertEquals("Bitmap", Shadows.shadowOf(bitmap).getDescription());
        Assert.assertEquals(160, bitmap.getWidth());
        Assert.assertEquals(107, bitmap.getHeight());
    }

    @Test
    public void decodeWithDifferentSampleSize() {
        String name = "test";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 0;
        Bitmap bm = ShadowBitmapFactory.create(name, options);
        assertThat(bm.getWidth()).isEqualTo(100);
        assertThat(bm.getHeight()).isEqualTo(100);
        options.inSampleSize = 2;
        bm = ShadowBitmapFactory.create(name, options);
        assertThat(bm.getWidth()).isEqualTo(50);
        assertThat(bm.getHeight()).isEqualTo(50);
        options.inSampleSize = 101;
        bm = ShadowBitmapFactory.create(name, options);
        assertThat(bm.getWidth()).isEqualTo(1);
        assertThat(bm.getHeight()).isEqualTo(1);
    }

    @Test
    public void createShouldSetSizeToValueFromMapAsFirstPriority() {
        ShadowBitmapFactory.provideWidthAndHeightHints("image.png", 111, 222);
        final Bitmap bitmap = ShadowBitmapFactory.create("file:image.png", null, new Point(50, 60));
        assertThat(bitmap.getWidth()).isEqualTo(111);
        assertThat(bitmap.getHeight()).isEqualTo(222);
    }

    @Test
    public void createShouldSetSizeToParameterAsSecondPriority() {
        final Bitmap bitmap = ShadowBitmapFactory.create(null, null, new Point(70, 80));
        assertThat(bitmap.getWidth()).isEqualTo(70);
        assertThat(bitmap.getHeight()).isEqualTo(80);
    }

    @Test
    public void createShouldSetSizeToHardcodedValueAsLastPriority() {
        final Bitmap bitmap = ShadowBitmapFactory.create(null, null, null);
        assertThat(bitmap.getWidth()).isEqualTo(100);
        assertThat(bitmap.getHeight()).isEqualTo(100);
    }
}

