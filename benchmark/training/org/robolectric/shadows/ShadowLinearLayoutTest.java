package org.robolectric.shadows;


import Gravity.CENTER_VERTICAL;
import LinearLayout.HORIZONTAL;
import LinearLayout.LayoutParams;
import LinearLayout.VERTICAL;
import android.view.Gravity;
import android.widget.LinearLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowLinearLayoutTest {
    private LinearLayout linearLayout;

    private ShadowLinearLayout shadow;

    @Test
    public void getLayoutParams_shouldReturnTheSameLinearLayoutParamsFromTheSetter() throws Exception {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1, 2);
        linearLayout.setLayoutParams(params);
        Assert.assertSame(params, linearLayout.getLayoutParams());
    }

    @Test
    public void canAnswerOrientation() throws Exception {
        assertThat(linearLayout.getOrientation()).isEqualTo(HORIZONTAL);
        linearLayout.setOrientation(VERTICAL);
        assertThat(linearLayout.getOrientation()).isEqualTo(VERTICAL);
        linearLayout.setOrientation(HORIZONTAL);
        assertThat(linearLayout.getOrientation()).isEqualTo(HORIZONTAL);
    }

    @Test
    public void canAnswerGravity() throws Exception {
        assertThat(shadow.getGravity()).isEqualTo(((Gravity.TOP) | (Gravity.START)));
        linearLayout.setGravity(CENTER_VERTICAL);// Only affects horizontal.

        assertThat(shadow.getGravity()).isEqualTo(((Gravity.CENTER_VERTICAL) | (Gravity.START)));
        linearLayout.setGravity(((Gravity.BOTTOM) | (Gravity.CENTER_HORIZONTAL)));// Affects both directions.

        assertThat(shadow.getGravity()).isEqualTo(((Gravity.BOTTOM) | (Gravity.CENTER_HORIZONTAL)));
    }
}

