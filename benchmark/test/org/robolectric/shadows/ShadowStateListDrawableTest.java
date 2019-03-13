package org.robolectric.shadows;


import R.attr;
import StateSet.WILD_CARD;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowStateListDrawableTest {
    @Test
    public void testAddStateWithDrawable() {
        Drawable drawable = ShadowDrawable.createFromPath("/foo");
        StateListDrawable stateListDrawable = new StateListDrawable();
        int[] states = new int[]{ attr.state_pressed };
        stateListDrawable.addState(states, drawable);
        ShadowStateListDrawable shadow = Shadows.shadowOf(stateListDrawable);
        Drawable drawableForState = shadow.getDrawableForState(states);
        Assert.assertNotNull(drawableForState);
        assertThat(getPath()).isEqualTo("/foo");
    }

    @Test
    public void testAddDrawableWithWildCardState() {
        Drawable drawable = ShadowDrawable.createFromPath("/foo");
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(WILD_CARD, drawable);
        ShadowStateListDrawable shadow = Shadows.shadowOf(stateListDrawable);
        Drawable drawableForState = shadow.getDrawableForState(WILD_CARD);
        Assert.assertNotNull(drawableForState);
        assertThat(getPath()).isEqualTo("/foo");
    }
}

