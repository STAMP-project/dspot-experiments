package org.robolectric.shadows;


import android.content.Context;
import android.content.RestrictionEntry;
import android.content.RestrictionsManager;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.common.collect.Iterables;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.LOLLIPOP)
public final class ShadowRestrictionsManagerTest {
    private RestrictionsManager restrictionsManager;

    private Context context;

    @Test
    public void getApplicationRestrictions() {
        assertThat(restrictionsManager.getApplicationRestrictions()).isNull();
        Bundle bundle = new Bundle();
        bundle.putCharSequence("test_key", "test_value");
        Shadows.shadowOf(restrictionsManager).setApplicationRestrictions(bundle);
        assertThat(restrictionsManager.getApplicationRestrictions().getCharSequence("test_key")).isEqualTo("test_value");
    }

    @Test
    public void getManifestRestrictions() {
        RestrictionEntry restrictionEntry = Iterables.getOnlyElement(restrictionsManager.getManifestRestrictions(context.getPackageName()));
        assertThat(restrictionEntry.getKey()).isEqualTo("restrictionKey");
    }
}

