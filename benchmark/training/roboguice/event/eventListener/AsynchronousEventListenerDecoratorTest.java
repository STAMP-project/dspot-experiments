package roboguice.event.eventListener;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import roboguice.event.EventListener;


/**
 * Tests for the AsynchronousEventListenerDecorator class
 *
 * @author John Ericksen
 */
@SuppressWarnings("unchecked")
public class AsynchronousEventListenerDecoratorTest {
    protected EventListener<Object> eventListener;

    protected AsynchronousEventListenerDecorator<Object> decorator;

    // Mike doesn't really understand what this test is doing
    @SuppressWarnings("deprecation")
    @Test
    public void onEventTest() {
        Mockito.reset(eventListener);
        decorator.onEvent(new Object());
        Mockito.verify(eventListener, Mockito.never()).onEvent(Mockito.anyObject());
    }
}

