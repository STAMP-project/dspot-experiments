package org.robolectric.shadows;


import Context.NETWORK_SCORE_SERVICE;
import android.content.Context;
import android.net.NetworkScoreManager;
import android.os.Build.VERSION_CODES;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;


/**
 * ShadowNetworkScoreManagerTest tests {@link ShadowNetworkScoreManager}.
 */
@RunWith(AndroidJUnit4.class)
public final class ShadowNetworkScoreManagerTest {
    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void testGetActiveScorerPackage() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        NetworkScoreManager networkScoreManager = ((NetworkScoreManager) (context.getSystemService(NETWORK_SCORE_SERVICE)));
        String testPackage = "com.package.test";
        networkScoreManager.setActiveScorer(testPackage);
        assertThat(networkScoreManager.getActiveScorerPackage()).isEqualTo(testPackage);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void testIsScoringEnabled() {
        Context context = ApplicationProvider.getApplicationContext();
        NetworkScoreManager networkScoreManager = ((NetworkScoreManager) (context.getSystemService(NETWORK_SCORE_SERVICE)));
        networkScoreManager.disableScoring();
        ShadowNetworkScoreManager m = Shadow.extract(networkScoreManager);
        assertThat(m.isScoringEnabled()).isFalse();
    }
}

