package me.ele.amigo.release;


import ApkReleaseActivity.LAYOUT_ID;
import ApkReleaseActivity.PATCH_CHECKSUM;
import ApkReleaseActivity.THEME_ID;
import Build.VERSION_CODES;
import android.R.layout.simple_list_item_1;
import android.R.style.TextAppearance;
import android.content.Intent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = { VERSION_CODES.KITKAT })
public class ApkReleaseActivityTest {
    @Test
    public void testOnCreate() {
        try {
            Robolectric.buildActivity(ApkReleaseActivity.class).get();
        } catch (RuntimeException e) {
            Assert.assertEquals(true, e.getMessage().contains("patch apk checksum must not be empty"));
        }
        Robolectric.buildActivity(ApkReleaseActivity.class).newIntent(new Intent().putExtra(PATCH_CHECKSUM, "dummy").putExtra(LAYOUT_ID, simple_list_item_1).putExtra(THEME_ID, TextAppearance)).get();
    }

    public interface Task {
        void onRun() throws Exception;
    }
}

