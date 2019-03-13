package org.robolectric.shadows.support.v4;


import android.support.v4.content.Loader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.TestRunnerWithManifest;


/**
 * Tests for support loaders.
 */
@RunWith(TestRunnerWithManifest.class)
public class ShadowLoaderTest {
    private Loader<String> loader;

    private boolean onForceLoadCalled;

    @Test
    public void shouldCallOnForceLoad() {
        loader.forceLoad();
        assertThat(onForceLoadCalled).isTrue();
    }
}

