package github.nisrulz.unittestingwithroboelectric;


import Build.VERSION_CODES;
import R.id.tvHelloWorld;
import android.widget.TextView;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = VERSION_CODES.LOLLIPOP)
public class MainActivityTest {
    private MainActivity activity;

    @Test
    public void validateTextViewHasText() {
        TextView tvHelloWorld = ((TextView) (activity.findViewById(tvHelloWorld)));
        Assert.assertNotNull("TextView could not be found", tvHelloWorld);
        Assert.assertTrue("TextView contains incorrect text", "Hello World!".equals(tvHelloWorld.getText().toString()));
        Assert.assertNotNull(activity);
    }
}

