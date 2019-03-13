package org.robolectric.shadows;


import android.app.Activity;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import libcore.icu.ICU;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowICUTest {
    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void addLikelySubtags_afterN_shouldReturnExpandedLocale() {
        assertThat(ICU.addLikelySubtags("zh-HK")).isEqualTo("zh-Hant-HK");
    }

    @Test
    @Config(maxSdk = VERSION_CODES.M)
    public void addLikelySubtags_preN_shouldReturnInputLocale() {
        assertThat(ICU.addLikelySubtags("zh-HK")).isEqualTo("zh-HK");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getBestDateTimePattern_returnsReasonableValue() {
        assertThat(ICU.getBestDateTimePattern("hm", null)).isEqualTo("hm");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getBestDateTimePattern_returns_jmm_US() {
        assertThat(ICU.getBestDateTimePattern("jmm", Locale.US)).isEqualTo("h:mm a");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getBestDateTimePattern_returns_jmm_UK() {
        assertThat(ICU.getBestDateTimePattern("jmm", Locale.UK)).isEqualTo("H:mm");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getBestDateTimePattern_returns_jmm_ptBR() {
        assertThat(ICU.getBestDateTimePattern("jmm", new Locale("pt", "BR"))).isEqualTo("H:mm");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void datePickerShouldNotCrashWhenAskingForBestDateTimePattern() {
        ActivityController<ShadowICUTest.DatePickerActivity> activityController = Robolectric.buildActivity(ShadowICUTest.DatePickerActivity.class);
        activityController.setup();
    }

    private static class DatePickerActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LinearLayout view = new LinearLayout(this);
            view.setId(1);
            DatePicker datePicker = new DatePicker(this);
            view.addView(datePicker);
            setContentView(view);
        }
    }
}

