package com.vaadin.tests.elements.progressbar;


import com.vaadin.testbench.elements.ProgressBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ProgressBarValueTest extends MultiBrowserTest {
    @Test
    public void progressBar_differentValues_valuesFetchedCorrectly() {
        Assert.assertEquals(1, $(ProgressBarElement.class).id("complete").getValue(), 0);
        Assert.assertEquals(0.5, $(ProgressBarElement.class).id("halfComplete").getValue(), 0);
        Assert.assertEquals(0, $(ProgressBarElement.class).id("notStarted").getValue(), 0);
    }
}

