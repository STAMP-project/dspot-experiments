package org.robolectric.shadows;


import Build.VERSION_CODES;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Tests for robolectric functionality around {@link MediaSession}.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.LOLLIPOP)
public class ShadowMediaSessionTest {
    @Test
    public void mediaSessionCompat_creation() throws Exception {
        // Should not result in an exception.
        new android.media.session.MediaSession(ApplicationProvider.getApplicationContext(), "test");
    }
}

