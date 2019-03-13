package com.vaadin.tests.application;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DetachOldUIOnReloadTest extends MultiBrowserTest {
    private static final String RELOAD = "Reload page";

    private static final String READ_LOG = "Read log messages from session";

    @Test
    public void testDetachesUIOnReload() throws InterruptedException {
        openTestURL();
        List<LabelElement> labels = $(LabelElement.class).all();
        Assert.assertEquals("initial label incorrect", "This is UI 0", lastLabelText(labels));
        Assert.assertFalse("reloading button not found", $(ButtonElement.class).caption(DetachOldUIOnReloadTest.RELOAD).all().isEmpty());
        openTestURL();
        click(DetachOldUIOnReloadTest.READ_LOG);
        checkLabels("first", 1);
        click(DetachOldUIOnReloadTest.RELOAD);
        click(DetachOldUIOnReloadTest.READ_LOG);
        checkLabels("second", 2);
        openTestURL();
        click(DetachOldUIOnReloadTest.READ_LOG);
        checkLabels("third", 3);
        // restarting reverts to 0
        openTestURL("restartApplication");
        checkLabels("final", 0);
    }
}

