package org.robolectric.shadows;


import Bitmap.Config.ARGB_8888;
import android.R.drawable.ic_delete;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.M)
public class ShadowIconTest {
    public static final int TYPE_BITMAP = 1;

    public static final int TYPE_RESOURCE = 2;

    public static final int TYPE_DATA = 3;

    public static final int TYPE_URI = 4;

    @Test
    public void testGetRes() {
        Icon icon = Icon.createWithResource(ApplicationProvider.getApplicationContext(), ic_delete);
        assertThat(Shadows.shadowOf(icon).getType()).isEqualTo(ShadowIconTest.TYPE_RESOURCE);
        assertThat(Shadows.shadowOf(icon).getResId()).isEqualTo(ic_delete);
    }

    @Test
    public void testGetBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        Icon icon = Icon.createWithBitmap(bitmap);
        assertThat(Shadows.shadowOf(icon).getType()).isEqualTo(ShadowIconTest.TYPE_BITMAP);
        assertThat(Shadows.shadowOf(icon).getBitmap()).isEqualTo(bitmap);
    }

    @Test
    public void testGetData() {
        byte[] data = new byte[1000];
        Icon icon = Icon.createWithData(data, 100, 200);
        assertThat(Shadows.shadowOf(icon).getType()).isEqualTo(ShadowIconTest.TYPE_DATA);
        assertThat(Shadows.shadowOf(icon).getDataBytes()).isEqualTo(data);
        assertThat(Shadows.shadowOf(icon).getDataOffset()).isEqualTo(100);
        assertThat(Shadows.shadowOf(icon).getDataLength()).isEqualTo(200);
    }

    @Test
    public void testGetUri() {
        Uri uri = Uri.parse("content://icons/icon");
        Icon icon = Icon.createWithContentUri(uri);
        assertThat(Shadows.shadowOf(icon).getType()).isEqualTo(ShadowIconTest.TYPE_URI);
        assertThat(Shadows.shadowOf(icon).getUri()).isEqualTo(uri);
    }
}

