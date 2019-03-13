package org.robolectric.shadows;


import Images.Media.EXTERNAL_CONTENT_URI;
import Images.Media.INTERNAL_CONTENT_URI;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowMediaStoreTest {
    @Test
    public void shouldInitializeFields() throws Exception {
        assertThat(EXTERNAL_CONTENT_URI.toString()).isEqualTo("content://media/external/images/media");
        assertThat(INTERNAL_CONTENT_URI.toString()).isEqualTo("content://media/internal/images/media");
        assertThat(Video.Media.EXTERNAL_CONTENT_URI.toString()).isEqualTo("content://media/external/video/media");
        assertThat(Video.Media.INTERNAL_CONTENT_URI.toString()).isEqualTo("content://media/internal/video/media");
    }
}

