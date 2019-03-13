package com.vaadin.ui;


import Unit.PERCENTAGE;
import Unit.PIXELS;
import com.vaadin.ui.AbstractSplitPanel.SplitPositionChangeEvent;
import com.vaadin.ui.AbstractSplitPanel.SplitPositionChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test for {@link SplitPositionChangeListener}
 *
 * @author Vaadin Ltd
 */
public class SplitPositionChangeListenerTest {
    @Test
    public void testSplitPositionListenerIsTriggered() throws Exception {
        final HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        SplitPositionChangeListener splitPositionChangeListener = Mockito.mock(SplitPositionChangeListener.class);
        splitPanel.addSplitPositionChangeListener(splitPositionChangeListener);
        splitPanel.setSplitPosition(50, PERCENTAGE);
        Mockito.verify(splitPositionChangeListener).onSplitPositionChanged(ArgumentMatchers.any(SplitPositionChangeEvent.class));
    }

    @Test
    public void testSplitPositionListenerContainsOldValues() throws Exception {
        final HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        float previousPosition = 50.0F;
        float newPosition = 125.0F;
        AtomicBoolean executed = new AtomicBoolean(false);
        splitPanel.setSplitPosition(previousPosition, PERCENTAGE);
        splitPanel.addSplitPositionChangeListener(( event) -> {
            assertFalse(event.isUserOriginated());
            assertTrue((previousPosition == (event.getOldSplitPosition())));
            assertEquals(Unit.PERCENTAGE, event.getOldSplitPositionUnit());
            assertTrue((newPosition == (event.getSplitPosition())));
            assertEquals(Unit.PIXELS, event.getSplitPositionUnit());
            executed.set(true);
        });
        splitPanel.setSplitPosition(newPosition, PIXELS);
        Assert.assertTrue(executed.get());
    }
}

