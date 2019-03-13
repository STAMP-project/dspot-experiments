package roboguice.event;


import java.lang.reflect.Method;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test class verifying eventManager functionality
 *
 * @author John Ericksen
 */
public class EventManagerTest {
    private EventManager eventManager;

    private ContextObserverTesterImpl tester;

    private List<Method> eventOneMethods;

    private List<Method> eventTwoMethods;

    private EventOne event;

    @Test
    public void testRegistrationLifeCycle() {
        for (Method method : eventOneMethods) {
            eventManager.registerObserver(EventOne.class, new roboguice.event.eventListener.ObserverMethodListener<EventOne>(tester, method));
        }
        for (Method method : eventTwoMethods) {
            eventManager.registerObserver(EventTwo.class, new roboguice.event.eventListener.ObserverMethodListener<EventTwo>(tester, method));
        }
        eventManager.fire(event);
        tester.verifyCallCount(eventOneMethods, EventOne.class, 1);
        tester.verifyCallCount(eventTwoMethods, EventTwo.class, 0);
        // reset
        tester.reset();
        eventManager.unregisterObserver(tester, EventOne.class);
        eventManager.unregisterObserver(tester, EventTwo.class);
        eventManager.fire(event);
        tester.verifyCallCount(eventOneMethods, EventOne.class, 0);
        tester.verifyCallCount(eventTwoMethods, EventTwo.class, 0);
    }

    @Test
    public void testShouldNotFailIfObserverIsRemovedInsideFire() throws Exception {
        eventManager.registerObserver(EventOne.class, new EventListener<EventOne>() {
            @Override
            public void onEvent(EventOne event) {
                // unregister self from manager
                eventManager.unregisterObserver(EventOne.class, this);
            }
        });
        eventManager.registerObserver(EventOne.class, Mockito.mock(EventListener.class));
        eventManager.fire(event);
    }
}

