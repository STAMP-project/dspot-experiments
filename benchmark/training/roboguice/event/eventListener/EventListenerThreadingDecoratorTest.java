package roboguice.event.eventListener;


import EventThread.BACKGROUND;
import EventThread.CURRENT;
import EventThread.UI;
import org.junit.Assert;
import org.junit.Test;
import roboguice.event.EventListener;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;


/**
 * Tests for the EventListenerThreadingDecorator class
 *
 * @author John Ericksen
 */
public class EventListenerThreadingDecoratorTest {
    protected EventListenerThreadingDecorator eventListenerDecorator;

    protected EventListener<Void> eventListener;

    @SuppressWarnings("rawtypes")
    @Test
    public void buildCurrentThreadObserverTest() {
        final EventListener outputListener = eventListenerDecorator.decorate(CURRENT, eventListener);
        Assert.assertEquals(eventListener, outputListener);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void buildUIThreadObserverTest() {
        final EventListener outputListener = eventListenerDecorator.decorate(UI, eventListener);
        Assert.assertEquals(eventListener, ((UIThreadEventListenerDecorator) (outputListener)).eventListener);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void buildAsyncThreadObserverTest() {
        final EventListener outputListener = eventListenerDecorator.decorate(BACKGROUND, eventListener);
        Assert.assertEquals(eventListener, ((AsynchronousEventListenerDecorator) (outputListener)).eventListener);
    }
}

