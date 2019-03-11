package roboguice.event;


import android.app.Activity;
import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 *
 *
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
public class ObservesTypeListenerTest {
    protected EventManager eventManager;

    protected Injector injector;

    protected List<Method> eventOneMethods;

    protected List<Method> eventTwoMethods;

    protected Context context = new Activity();

    @Test
    public void simulateInjection() {
        final ObservesTypeListenerTest.InjectedTestClass testClass = new ObservesTypeListenerTest.InjectedTestClass();
        injector.injectMembers(testClass);
        eventManager.fire(new EventOne());
        testClass.tester.verifyCallCount(eventOneMethods, EventOne.class, 1);
        testClass.tester.verifyCallCount(eventTwoMethods, EventTwo.class, 0);
    }

    @Test(expected = RuntimeException.class)
    public void invalidObservesMethodSignature() {
        injector.getInstance(ObservesTypeListenerTest.MalformedObserves.class);
    }

    // CHECKSTYLE:ON
    public static class InjectedTestClass {
        // CHECKSTYLE:OFF
        @Inject
        protected ContextObserverTesterImpl tester;
    }

    public class MalformedObserves {
        public void malformedObserves(int val, @Observes
        EventOne event) {
        }
    }
}

