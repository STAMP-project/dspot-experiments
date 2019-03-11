package org.robolectric.shadows;


import DateUtils.FORMAT_NUMERIC_DATE;
import android.app.Application;
import android.os.Build.VERSION_CODES;
import android.text.format.DateUtils;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Calendar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowDateUtilsTest {
    private Application context;

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT, maxSdk = VERSION_CODES.LOLLIPOP_MR1)
    public void formatDateTime_withCurrentYear_worksSinceKitKat() {
        final long millisAtStartOfYear = getMillisAtStartOfYear();
        String actual = DateUtils.formatDateTime(context, millisAtStartOfYear, FORMAT_NUMERIC_DATE);
        assertThat(actual).isEqualTo("1/1");
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void formatDateTime_withCurrentYear_worksSinceM() {
        final long millisAtStartOfYear = getMillisAtStartOfYear();
        // starting with M, sometimes the year is there, sometimes it's missing, unless you specify
        // FORMAT_SHOW_YEAR
        String actual = DateUtils.formatDateTime(context, millisAtStartOfYear, ((DateUtils.FORMAT_SHOW_YEAR) | (DateUtils.FORMAT_NUMERIC_DATE)));
        final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        assertThat(actual).isEqualTo(("1/1/" + currentYear));
    }

    @Test
    @Config(maxSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void formatDateTime_withCurrentYear_worksPreKitKat() {
        Calendar calendar = Calendar.getInstance();
        final int currentYear = calendar.get(Calendar.YEAR);
        final long millisAtStartOfYear = getMillisAtStartOfYear();
        String actual = DateUtils.formatDateTime(context, millisAtStartOfYear, FORMAT_NUMERIC_DATE);
        assertThat(actual).isEqualTo(("1/1/" + currentYear));
    }

    @Test
    public void formatDateTime_withPastYear() {
        String actual = DateUtils.formatDateTime(context, 1420099200000L, FORMAT_NUMERIC_DATE);
        assertThat(actual).isEqualTo("1/1/2015");
    }

    @Test
    public void isToday_shouldReturnFalseForNotToday() {
        long today = Calendar.getInstance().getTimeInMillis();
        ShadowSystemClock.setCurrentTimeMillis(today);
        assertThat(DateUtils.isToday(today)).isTrue();
        assertThat(/* 24 hours */
        DateUtils.isToday((today + (86400 * 1000)))).isFalse();
        assertThat(/* 240 hours */
        DateUtils.isToday((today + (86400 * 10000)))).isFalse();
    }
}

