package jonathanfinerty.once;


import Context.MODE_PRIVATE;
import RuntimeEnvironment.application;
import android.content.SharedPreferences;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class BackwardsCompatibilityTests {
    @Test
    public void backwardsCompatibilityWithPre1Versions() {
        String tag = "version 0.5 tag";
        SharedPreferences sharedPreferences = application.getSharedPreferences(((PersistedMap.class.getSimpleName()) + "TagLastSeenMap"), MODE_PRIVATE);
        sharedPreferences.edit().putLong(tag, 1234L).apply();
        Once.initialise(application);
        Once.markDone(tag);
        Assert.assertTrue(Once.beenDone(tag, Amount.exactly(2)));
    }
}

