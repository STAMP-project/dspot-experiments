package com.lyft.scoop;


import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

import static TransitionDirection.ENTER;


public class RouteChangeTest {
    private static final TransitionDirection ENTER_TRANSITION = ENTER;

    @Test
    public void screenEmptyPath() {
        List<Screen> fromPath = Arrays.<Screen>asList();
        List<Screen> toPath = Arrays.<Screen>asList();
        RouteChange routeChange = new RouteChange(fromPath, toPath, RouteChangeTest.ENTER_TRANSITION);
        ScreenSwap screenSwap = routeChange.toScreenSwap();
        Assert.assertNull(screenSwap.next);
        Assert.assertNull(screenSwap.previous);
        Assert.assertEquals(RouteChangeTest.ENTER_TRANSITION, screenSwap.direction);
    }

    @Test
    public void screenOneElementPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new RouteChangeTest.ScreenA());
        List<Screen> toPath = Arrays.<Screen>asList(new RouteChangeTest.ScreenA());
        RouteChange routeChange = new RouteChange(fromPath, toPath, RouteChangeTest.ENTER_TRANSITION);
        ScreenSwap screenSwap = routeChange.toScreenSwap();
        Assert.assertEquals(new RouteChangeTest.ScreenA(), screenSwap.next);
        Assert.assertEquals(new RouteChangeTest.ScreenA(), screenSwap.previous);
        Assert.assertEquals(RouteChangeTest.ENTER_TRANSITION, screenSwap.direction);
    }

    @Test
    public void screenMultipleElementPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new RouteChangeTest.ScreenA(), new RouteChangeTest.ScreenB());
        List<Screen> toPath = Arrays.<Screen>asList(new RouteChangeTest.ScreenA(), new RouteChangeTest.ScreenB());
        RouteChange routeChange = new RouteChange(fromPath, toPath, RouteChangeTest.ENTER_TRANSITION);
        ScreenSwap screenSwap = routeChange.toScreenSwap();
        Assert.assertEquals(new RouteChangeTest.ScreenB(), screenSwap.next);
        Assert.assertEquals(new RouteChangeTest.ScreenB(), screenSwap.previous);
        Assert.assertEquals(RouteChangeTest.ENTER_TRANSITION, screenSwap.direction);
    }

    static class ScreenA extends Screen {}

    static class ScreenB extends Screen {}
}

