package androidx.core.content.res;


import R.font.vt323_regular;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.internal.DoNotInstrument;


/**
 * Compatibility test for {@link ResourcesCompat}
 */
@DoNotInstrument
@RunWith(AndroidJUnit4.class)
public class ResourcesCompatTest {
    @Test
    public void getFont() {
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), vt323_regular);
        assertThat(typeface).isNotNull();
    }
}

