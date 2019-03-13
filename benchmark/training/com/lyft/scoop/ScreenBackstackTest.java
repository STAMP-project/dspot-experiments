package com.lyft.scoop;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ScreenBackstackTest {
    private ScreenBackstack backStack;

    @Test
    public void asList() {
        ScreenBackstackTest.Screen1 screen1 = new ScreenBackstackTest.Screen1();
        ScreenBackstackTest.Screen2 screen2 = new ScreenBackstackTest.Screen2();
        backStack.push(screen1);
        backStack.push(screen2);
        List<Screen> list = backStack.asList();
        Assert.assertEquals(screen1, list.get(0));
        Assert.assertEquals(screen2, list.get(1));
    }

    @Test
    public void asListWithEmptyBackstack() {
        List<Screen> list = backStack.asList();
        Assert.assertTrue(list.isEmpty());
    }

    static class Screen1 extends Screen {}

    static class Screen2 extends Screen {}
}

