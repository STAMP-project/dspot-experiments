package com.vaadin.tests.server.component.button;


import com.vaadin.ui.Button;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the public click() method.
 */
public class ButtonClickTest {
    private boolean clicked = false;

    @Test
    public void clickDetachedButton() {
        Button b = new Button();
        AtomicInteger counter = new AtomicInteger(0);
        b.addClickListener(( event) -> counter.incrementAndGet());
        b.click();
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testClick() {
        getButton().click();
        Assert.assertTrue("Button doesn't fire clicks", clicked);
    }

    @Test
    public void testClickDisabled() {
        Button b = getButton();
        b.setEnabled(false);
        b.click();
        Assert.assertFalse("Disabled button fires click events", clicked);
    }
}

