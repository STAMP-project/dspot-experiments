package com.vaadin.tests.themes.valo;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class WindowTitleOverflowTest extends MultiBrowserTest {
    @Test
    public void headerMarginIsCorrectForResizable() {
        openWindow("Open Resizable");
        Assert.assertThat(getWindowHeaderMarginRight(), CoreMatchers.is("74px"));
    }

    @Test
    public void headerMarginIsCorrectForClosable() {
        openWindow("Open Closable");
        Assert.assertThat(getWindowHeaderMarginRight(), CoreMatchers.is("37px"));
    }

    @Test
    public void headerMarginIsCorrectForResizableAndClosable() {
        openWindow("Open Resizable and Closable");
        Assert.assertThat(getWindowHeaderMarginRight(), CoreMatchers.is("74px"));
    }

    @Test
    public void headerMarginIsCorrectForNonResizableAndNonClosable() {
        openWindow("Open Non-Resizable and Non-Closable");
        Assert.assertThat(getWindowHeaderMarginRight(), CoreMatchers.is("12px"));
    }
}

