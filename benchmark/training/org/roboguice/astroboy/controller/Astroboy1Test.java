package org.roboguice.astroboy.controller;


import android.content.Context;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * A simple testcase that tests the {@link Astroboy} pojo.
 *
 * This test has no particularly complicated activity or context dependencies,
 * so we don't bother initializing the activity or really doing anything with it
 * at all.
 */
@RunWith(RobolectricTestRunner.class)
public class Astroboy1Test {
    protected Context context;

    protected Astroboy astroboy;

    @Test
    public void stringShouldEndInExclamationMark() {
        Assert.assertTrue(astroboy.punch().endsWith("!"));
    }
}

