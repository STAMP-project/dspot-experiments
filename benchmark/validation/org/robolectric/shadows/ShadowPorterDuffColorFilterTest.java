package org.robolectric.shadows;


import Color.RED;
import PorterDuff.Mode;
import PorterDuff.Mode.ADD;
import android.graphics.Color;
import android.graphics.PorterDuffColorFilter;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.LOLLIPOP)
public class ShadowPorterDuffColorFilterTest {
    @Test
    public void constructor_shouldWork() {
        final PorterDuffColorFilter filter = new PorterDuffColorFilter(Color.RED, Mode.ADD);
        assertThat(filter.getColor()).isEqualTo(RED);
        assertThat(filter.getMode()).isEqualTo(ADD);
    }

    @Config(minSdk = VERSION_CODES.O)
    @Test
    public void createNativeInstance_shouldWork() {
        final PorterDuffColorFilter filter = new PorterDuffColorFilter(Color.RED, Mode.ADD);
        assertThat(filter.getNativeInstance()).isEqualTo(0L);
    }

    @Test
    public void hashCode_returnsDifferentValuesForDifferentModes() {
        PorterDuffColorFilter addFilter = new PorterDuffColorFilter(Color.RED, Mode.ADD);
        PorterDuffColorFilter dstFilter = new PorterDuffColorFilter(Color.RED, Mode.DST);
        assertThat(addFilter.hashCode()).isNotEqualTo(dstFilter.hashCode());
    }

    @Test
    public void hashCode_returnsDifferentValuesForDifferentColors() {
        PorterDuffColorFilter blueFilter = new PorterDuffColorFilter(Color.BLUE, Mode.ADD);
        PorterDuffColorFilter redFilter = new PorterDuffColorFilter(Color.RED, Mode.ADD);
        assertThat(blueFilter.hashCode()).isNotEqualTo(redFilter.hashCode());
    }

    @Test
    public void equals_returnsTrueForEqualObjects() {
        PorterDuffColorFilter filter1 = new PorterDuffColorFilter(Color.RED, Mode.ADD);
        PorterDuffColorFilter filter2 = new PorterDuffColorFilter(Color.RED, Mode.ADD);
        assertThat(filter1).isEqualTo(filter2);
    }

    @Test
    public void equals_returnsFalseForDifferentModes() {
        PorterDuffColorFilter addFilter = new PorterDuffColorFilter(Color.RED, Mode.ADD);
        PorterDuffColorFilter dstFilter = new PorterDuffColorFilter(Color.RED, Mode.DST);
        assertThat(addFilter).isNotEqualTo(dstFilter);
    }

    @Test
    public void equals_returnsFalseForDifferentColors() {
        PorterDuffColorFilter blueFilter = new PorterDuffColorFilter(Color.BLUE, Mode.ADD);
        PorterDuffColorFilter redFilter = new PorterDuffColorFilter(Color.RED, Mode.ADD);
        assertThat(blueFilter).isNotEqualTo(redFilter);
    }
}

