package org.robolectric.shadows;


import android.content.pm.ResolveInfo;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowResolveInfoTest {
    private ResolveInfo mResolveInfo;

    @Test
    public void testNewResolveInfoWithActivity() {
        assertThat(mResolveInfo.loadLabel(null).toString()).isEqualTo("name");
        assertThat(mResolveInfo.activityInfo.packageName).isEqualTo("package");
        assertThat(mResolveInfo.activityInfo.applicationInfo.packageName).isEqualTo("package");
        assertThat(mResolveInfo.activityInfo.name).isEqualTo("fragmentActivity");
    }
}

