package org.robolectric;


import android.os.Build.VERSION_CODES;
import android.view.View;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowView;


// running on all SDKs is unnecessary and can cause OOM GC overhead issues
@RunWith(AndroidJUnit4.class)
@Config(sdk = VERSION_CODES.O)
public class TemporaryBindingsTest {
    @Test
    @Config(shadows = TemporaryBindingsTest.TemporaryShadowView.class)
    public void overridingShadowBindingsShouldNotAffectBindingsInLaterTests() throws Exception {
        TemporaryBindingsTest.TemporaryShadowView shadowView = Shadow.extract(new View(ApplicationProvider.getApplicationContext()));
        assertThat(shadowView.getClass().getSimpleName()).isEqualTo(TemporaryBindingsTest.TemporaryShadowView.class.getSimpleName());
    }

    @Test
    public void overridingShadowBindingsShouldNotAffectBindingsInLaterTestsAgain() throws Exception {
        assertThat(Shadows.shadowOf(new View(ApplicationProvider.getApplicationContext())).getClass().getSimpleName()).isEqualTo(ShadowView.class.getSimpleName());
    }

    @Implements(View.class)
    public static class TemporaryShadowView {}
}

