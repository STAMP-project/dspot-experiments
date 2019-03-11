package cucumber.runtime.java.weld;


import WeldFactory.START_EXCEPTION_MESSAGE;
import WeldFactory.STOP_EXCEPTION_MESSAGE;
import cucumber.api.java.ObjectFactory;
import cucumber.runtime.CucumberException;
import io.cucumber.core.logging.LogRecordListener;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.jboss.weld.environment.se.Weld;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class WeldFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private LogRecordListener logRecordListener;

    @Test
    public void shouldGiveUsNewInstancesForEachScenario() {
        final ObjectFactory factory = new WeldFactory();
        factory.addClass(BellyStepdefs.class);
        // Scenario 1
        factory.start();
        final BellyStepdefs o1 = factory.getInstance(BellyStepdefs.class);
        factory.stop();
        // Scenario 2
        factory.start();
        final BellyStepdefs o2 = factory.getInstance(BellyStepdefs.class);
        factory.stop();
        Assert.assertNotNull(o1);
        Assert.assertNotSame(o1, o2);
    }

    @Test
    public void startStopCalledWithoutStart() {
        final Weld weld = Mockito.mock(Weld.class);
        Mockito.when(weld.initialize()).thenThrow(new IllegalArgumentException());
        final WeldFactory factory = new WeldFactory();
        this.expectedException.expect(CucumberException.class);
        this.expectedException.expectMessage(Is.is(IsEqual.equalTo(START_EXCEPTION_MESSAGE)));
        factory.start(weld);
    }

    @Test
    public void stopCalledWithoutStart() {
        ObjectFactory factory = new WeldFactory();
        factory.stop();
        Assert.assertThat(logRecordListener.getLogRecords().get(0).getMessage(), CoreMatchers.containsString(STOP_EXCEPTION_MESSAGE));
    }
}

