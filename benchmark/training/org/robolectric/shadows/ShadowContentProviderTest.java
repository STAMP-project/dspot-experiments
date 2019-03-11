package org.robolectric.shadows;


import android.content.ContentProvider;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.testing.TestContentProvider1;


@RunWith(AndroidJUnit4.class)
public class ShadowContentProviderTest {
    @Config(minSdk = VERSION_CODES.KITKAT)
    @Test
    public void testSetCallingPackage() throws Exception {
        ContentProvider provider = new TestContentProvider1();
        Shadows.shadowOf(provider).setCallingPackage("calling-package");
        assertThat(provider.getCallingPackage()).isEqualTo("calling-package");
    }
}

