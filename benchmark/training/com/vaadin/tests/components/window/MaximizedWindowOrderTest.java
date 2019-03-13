package com.vaadin.tests.components.window;


import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MaximizedWindowOrderTest extends MultiBrowserTest {
    @Test
    public void newWindowOpensOnTopOfMaximizedWindow() {
        WindowElement maximizedWindow = openMaximizedWindow();
        WindowElement anotherWindow = openAnotherWindow();
        Assert.assertThat(anotherWindow.getCssValue("z-index"), CoreMatchers.is(Matchers.greaterThan(maximizedWindow.getCssValue("z-index"))));
        Assert.assertThat(getMaximizedWindow().getCssValue("z-index"), CoreMatchers.is("10000"));
        Assert.assertThat(getAnotherWindow().getCssValue("z-index"), CoreMatchers.is("10001"));
    }

    @Test
    public void backgroundWindowIsBroughtOnTopWhenMaximized() {
        WindowElement maximizedWindow = openMaximizedWindow();
        maximizedWindow.restore();
        // the new window is opened on top of the original.
        WindowElement anotherWindow = openAnotherWindow();
        // move the window to make the maximize button visible.
        moveWindow(anotherWindow, 10, 20);
        maximizedWindow.maximize();
        Assert.assertThat(maximizedWindow.getCssValue("z-index"), CoreMatchers.is(Matchers.greaterThan(anotherWindow.getCssValue("z-index"))));
    }
}

