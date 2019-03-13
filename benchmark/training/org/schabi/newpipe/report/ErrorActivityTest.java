package org.schabi.newpipe.report;


import android.app.Activity;
import org.junit.Assert;
import org.junit.Test;
import org.schabi.newpipe.MainActivity;
import org.schabi.newpipe.RouterActivity;
import org.schabi.newpipe.fragments.detail.VideoDetailFragment;


/**
 * Unit tests for {@link ErrorActivity}
 */
public class ErrorActivityTest {
    @Test
    public void getReturnActivity() {
        Class<? extends Activity> returnActivity;
        returnActivity = ErrorActivity.getReturnActivity(MainActivity.class);
        Assert.assertEquals(MainActivity.class, returnActivity);
        returnActivity = ErrorActivity.getReturnActivity(RouterActivity.class);
        Assert.assertEquals(RouterActivity.class, returnActivity);
        returnActivity = ErrorActivity.getReturnActivity(null);
        Assert.assertNull(returnActivity);
        returnActivity = ErrorActivity.getReturnActivity(Integer.class);
        Assert.assertEquals(MainActivity.class, returnActivity);
        returnActivity = ErrorActivity.getReturnActivity(VideoDetailFragment.class);
        Assert.assertEquals(MainActivity.class, returnActivity);
    }
}

