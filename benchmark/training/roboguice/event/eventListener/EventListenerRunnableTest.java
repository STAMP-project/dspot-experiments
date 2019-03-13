package roboguice.event.eventListener;


import org.junit.Test;
import org.mockito.Mockito;
import roboguice.event.EventListener;
import roboguice.event.EventOne;


/**
 * Tests for the EventListenerRunnable class
 *
 * @author John Ericksen
 */
public class EventListenerRunnableTest {
    protected EventOne event;

    protected EventListener<EventOne> eventListener;

    @SuppressWarnings("rawtypes")
    protected EventListenerRunnable eventListenerRunnable;

    @Test
    public void runTest() {
        Mockito.reset(eventListener);
        eventListenerRunnable.run();
        Mockito.verify(eventListener).onEvent(event);
    }
}

