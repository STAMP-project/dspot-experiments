package com.bruce.pickerview;


import android.graphics.Canvas;
import com.brucetoo.pickview.BuildConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "app/src/main/AndroidManifest.xml")
public class LoopViewTest {
    private LoopView loopView;

    @Test
    public void testInitialSelectedItem() throws Exception {
        // when
        Whitebox.invokeMethod(loopView, "onDraw", new Canvas());
        // then
        Assert.assertEquals(3, loopView.getSelectedItem());
    }

    @Test
    public void testChangeOfSelectedItemDown() throws Exception {
        // given
        Whitebox.setInternalState(loopView, "totalScrollY", 20);
        // when
        Whitebox.invokeMethod(loopView, "onDraw", new Canvas());
        // then
        Assert.assertEquals(4, loopView.getSelectedItem());
    }

    @Test
    public void testChangeOfSelectedItemUp() throws Exception {
        // given
        Whitebox.setInternalState(loopView, "totalScrollY", (-20));
        // when
        Whitebox.invokeMethod(loopView, "onDraw", new Canvas());
        // then
        Assert.assertEquals(2, loopView.getSelectedItem());
    }

    @Test
    public void testChangeOfSelectedItemMaxUp() throws Exception {
        // given
        Whitebox.setInternalState(loopView, "totalScrollY", 150);
        // when
        Whitebox.invokeMethod(loopView, "onDraw", new Canvas());
        // then
        Assert.assertEquals(0, loopView.getSelectedItem());
    }

    @Test
    public void testChangeOfSelectedItemMaxDown() throws Exception {
        // given
        Whitebox.setInternalState(loopView, "totalScrollY", (-150));
        // when
        Whitebox.invokeMethod(loopView, "onDraw", new Canvas());
        // then
        Assert.assertEquals(0, loopView.getSelectedItem());
    }
}

