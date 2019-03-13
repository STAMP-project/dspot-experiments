package org.robolectric;


import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Parameterized tests using an Android class originally created outside of the Robolectric classloader.
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
public final class ParameterizedRobolectricTestRunnerClassLoaderTest {
    private final Uri uri;

    public ParameterizedRobolectricTestRunnerClassLoaderTest(Uri uri) {
        this.uri = uri;
    }

    @Test
    @Config(manifest = Config.NONE)
    public void parse() {
        Uri currentUri = Uri.parse("http://host/");
        assertThat(currentUri).isEqualTo(uri);
    }
}

