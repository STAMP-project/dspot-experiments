package com.reactnativenavigation.views;


import android.app.Activity;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.anim.TopBarAnimator;
import com.reactnativenavigation.parse.AnimationOptions;
import com.reactnativenavigation.utils.UiUtils;
import com.reactnativenavigation.views.topbar.TopBar;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;


@Config(qualifiers = "xxhdpi")
public class TopBarTest extends BaseTest {
    private TopBar uut;

    private TopBarAnimator animator;

    private Activity activity;

    @Test
    public void title() {
        assertThat(uut.getTitle()).isEmpty();
        uut.setTitle("new title");
        assertThat(uut.getTitle()).isEqualTo("new title");
    }

    @Test
    public void hide_animate() {
        AnimationOptions options = new AnimationOptions();
        uut.hideAnimate(options);
        Mockito.verify(animator, Mockito.times(1)).hide(ArgumentMatchers.eq(options), ArgumentMatchers.any());
    }

    @Test
    public void show_animate() {
        AnimationOptions options = new AnimationOptions();
        uut.hide();
        uut.showAnimate(options);
        Mockito.verify(animator, Mockito.times(1)).show(options);
    }

    @Test
    public void setElevation_ignoreValuesNotSetByNavigation() {
        float initialElevation = uut.getElevation();
        uut.setElevation(1.0F);
        assertThat(uut.getElevation()).isEqualTo(initialElevation);
        uut.setElevation(Double.valueOf(2));
        assertThat(uut.getElevation()).isEqualTo(UiUtils.dpToPx(activity, 2));
    }
}

