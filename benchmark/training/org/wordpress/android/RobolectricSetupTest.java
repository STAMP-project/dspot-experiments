package org.wordpress.android;


import Build.VERSION_CODES;
import R.string.app_title;
import RuntimeEnvironment.application;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, sdk = VERSION_CODES.JELLY_BEAN)
public class RobolectricSetupTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void appNameTest() {
        // this test does nothing fancy but it helps make sure the Robolectric setup is working OK.
        // If running this via AndroidStudio, make sure the run configuration's working directory is set to $MODULE_DIR$
        // and the VM options to `-ea`
        Assert.assertEquals("WordPress for Android", application.getString(app_title));
    }
}

