package com.lyft.scoop;


import TransitionDirection.ENTER;
import TransitionDirection.EXIT;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class RouterTest {
    private RouterTest.TestRouter router;

    @Test
    public void defaultRouter() {
        RouterTest.TestRouter defaultRouter = new RouterTest.TestRouter();
        Screen screenA = new RouterTest.ScreenA();
        Screen screenB = new RouterTest.ScreenB();
        defaultRouter.goTo(screenA);
        defaultRouter.goTo(screenB);
        Assert.assertTrue(defaultRouter.goBack());
        Assert.assertEquals(screenA, defaultRouter.fromPath.get(0));
        Assert.assertEquals(screenB, defaultRouter.fromPath.get(1));
        Assert.assertEquals(screenA, defaultRouter.toPath.get(0));
        Assert.assertEquals(EXIT, defaultRouter.direction);
    }

    @Test
    public void goTo() {
        router = new RouterTest.TestRouter(false);
        Screen screenA = new RouterTest.ScreenA();
        router.goTo(screenA);
        Assert.assertTrue(router.fromPath.isEmpty());
        Assert.assertEquals(screenA, router.toPath.get(0));
        Assert.assertEquals(ENTER, router.direction);
        Assert.assertEquals(1, router.routeChangeCount);
    }

    @Test
    public void goToSameScreen() {
        router = new RouterTest.TestRouter(false);
        Screen screenA = new RouterTest.ScreenA();
        Screen screenB = new RouterTest.ScreenA();
        router.goTo(screenA);
        Assert.assertEquals(screenA, router.toPath.get(0));
        router.goTo(screenB);
        Assert.assertEquals(screenA, router.toPath.get(0));
        Assert.assertEquals(1, router.routeChangeCount);
    }

    @Test
    public void goBack() {
        router = new RouterTest.TestRouter(false);
        Screen screenA = new RouterTest.ScreenA();
        Screen screenB = new RouterTest.ScreenB();
        router.goTo(screenA);
        router.goTo(screenB);
        Assert.assertTrue(router.goBack());
        Assert.assertEquals(screenA, router.fromPath.get(0));
        Assert.assertEquals(screenB, router.fromPath.get(1));
        Assert.assertEquals(screenA, router.toPath.get(0));
        Assert.assertEquals(EXIT, router.direction);
        Assert.assertEquals(3, router.routeChangeCount);
    }

    @Test
    public void goBackAllowEmptyStack() {
        router = new RouterTest.TestRouter(true);
        Screen screenA = new RouterTest.ScreenA();
        router.goTo(screenA);
        Assert.assertTrue(router.goBack());
        Assert.assertEquals(screenA, router.fromPath.get(0));
        Assert.assertTrue(router.toPath.isEmpty());
        Assert.assertEquals(EXIT, router.direction);
        Assert.assertEquals(2, router.routeChangeCount);
    }

    @Test
    public void goBackNoScreens() {
        router = new RouterTest.TestRouter(true);
        Assert.assertFalse(router.goBack());
        Assert.assertEquals(0, router.routeChangeCount);
    }

    @Test
    public void resetToExisting() {
        router = new RouterTest.TestRouter(false);
        Screen screenA = new RouterTest.ScreenA();
        Screen screenB = new RouterTest.ScreenB();
        router.goTo(screenA);
        router.goTo(screenB);
        router.resetTo(screenA);
        Assert.assertEquals(screenA, router.fromPath.get(0));
        Assert.assertEquals(screenB, router.fromPath.get(1));
        Assert.assertEquals(screenA, router.toPath.get(0));
        Assert.assertEquals(EXIT, router.direction);
        checkIfRouterBackstackIsEmpty();
        Assert.assertEquals(4, router.routeChangeCount);
    }

    @Test
    public void resetToNew() {
        router = new RouterTest.TestRouter(false);
        Screen screenA = new RouterTest.ScreenA();
        Screen screenB = new RouterTest.ScreenB();
        router.goTo(screenB);
        router.resetTo(screenA);
        Assert.assertEquals(screenB, router.fromPath.get(0));
        Assert.assertEquals(screenA, router.toPath.get(0));
        Assert.assertEquals(EXIT, router.direction);
        checkIfRouterBackstackIsEmpty();
        Assert.assertEquals(3, router.routeChangeCount);
    }

    @Test
    public void replaceWith() {
        router = new RouterTest.TestRouter(false);
        Screen screenA = new RouterTest.ScreenA();
        Screen screenB = new RouterTest.ScreenB();
        router.goTo(screenA);
        router.replaceWith(screenB);
        Assert.assertEquals(screenA, router.fromPath.get(0));
        Assert.assertEquals(screenB, router.toPath.get(0));
        Assert.assertEquals(ENTER, router.direction);
        checkIfRouterBackstackIsEmpty();
        Assert.assertEquals(3, router.routeChangeCount);
    }

    @Test
    public void replaceAllWith() {
        router = new RouterTest.TestRouter(false);
        Screen screenA = new RouterTest.ScreenA();
        Screen screenB = new RouterTest.ScreenB();
        router.goTo(screenA);
        router.replaceAllWith(screenA, screenB);
        Assert.assertEquals(screenA, router.fromPath.get(0));
        Assert.assertEquals(screenA, router.toPath.get(0));
        Assert.assertEquals(screenB, router.toPath.get(1));
        Assert.assertEquals(2, router.routeChangeCount);
    }

    @Test
    public void emptyBackstackGoTo() {
        router = new RouterTest.TestRouter(false);
        Screen screenA = new RouterTest.ScreenA();
        router.goTo(screenA);
        Assert.assertTrue(router.fromPath.isEmpty());
        Assert.assertEquals(screenA, router.toPath.get(0));
        Assert.assertEquals(1, router.routeChangeCount);
    }

    @Test
    public void emptyBackstackReplaceWith() {
        router = new RouterTest.TestRouter(false);
        Screen screenA = new RouterTest.ScreenA();
        router.replaceWith(screenA);
        Assert.assertTrue(router.fromPath.isEmpty());
        Assert.assertEquals(screenA, router.toPath.get(0));
        Assert.assertEquals(1, router.routeChangeCount);
    }

    @Test
    public void emptyBackstackResetTo() {
        router = new RouterTest.TestRouter(false);
        Screen screenA = new RouterTest.ScreenA();
        router.resetTo(screenA);
        Assert.assertTrue(router.fromPath.isEmpty());
        Assert.assertEquals(screenA, router.toPath.get(0));
        Assert.assertEquals(1, router.routeChangeCount);
    }

    @Test
    public void replaceToSameScreen() {
        router = new RouterTest.TestRouter(false);
        Screen screenA = new RouterTest.ScreenA();
        Screen screenB = new RouterTest.ScreenA();
        router.replaceWith(screenA);
        router.replaceWith(screenB);
        Assert.assertTrue(router.fromPath.isEmpty());
        Assert.assertEquals(screenA, router.toPath.get(0));
        Assert.assertEquals(1, router.routeChangeCount);
    }

    @Test
    public void hasActiveScreen() {
        router = new RouterTest.TestRouter(false);
        Screen screenA = new RouterTest.ScreenA();
        router.goTo(screenA);
        Assert.assertTrue(router.hasActiveScreen());
        router.goBack();
        Assert.assertFalse(router.hasActiveScreen());
    }

    @Test
    public void replaceAllWithEmptyListOnDisallowedEmptyStack() {
        router = new RouterTest.TestRouter(true);
        router.replaceAllWith(Collections.<Screen>emptyList());
        Assert.assertFalse(router.hasActiveScreen());
        Assert.assertEquals(1, router.routeChangeCount);
    }

    static class ScreenA extends Screen {}

    static class ScreenB extends Screen {}

    static class TestRouter extends Router {
        List<Screen> fromPath;

        List<Screen> toPath;

        TransitionDirection direction;

        int routeChangeCount;

        public TestRouter(boolean allowEmptyStack) {
            super(allowEmptyStack);
        }

        public TestRouter() {
            super();
        }

        @Override
        protected void onRouteChanged(RouteChange routeChange) {
            (routeChangeCount)++;
            fromPath = routeChange.fromPath;
            toPath = routeChange.toPath;
            direction = routeChange.direction;
        }
    }
}

