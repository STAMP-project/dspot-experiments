package org.robolectric;


import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Parameterized tests using an Android class.
 *
 * @author John Ferlisi
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
public final class ParameterizedRobolectricTestRunnerUriTest {
    private final String basePath;

    private final String resourcePath;

    private final Uri expectedUri;

    public ParameterizedRobolectricTestRunnerUriTest(String basePath, String resourcePath, String expectedUri) {
        this.basePath = basePath;
        this.resourcePath = resourcePath;
        this.expectedUri = Uri.parse(expectedUri);
    }

    @Test
    @Config(manifest = Config.NONE)
    public void parse() {
        assertThat(Uri.parse(basePath).buildUpon().path(resourcePath).build()).isEqualTo(expectedUri);
    }
}

