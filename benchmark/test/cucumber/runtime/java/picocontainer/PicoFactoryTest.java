package cucumber.runtime.java.picocontainer;


import cucumber.api.java.ObjectFactory;
import org.junit.Assert;
import org.junit.Test;


public class PicoFactoryTest {
    @Test
    public void shouldGiveUsNewInstancesForEachScenario() {
        ObjectFactory factory = new PicoFactory();
        factory.addClass(StepDefs.class);
        // Scenario 1
        factory.start();
        StepDefs o1 = factory.getInstance(StepDefs.class);
        factory.stop();
        // Scenario 2
        factory.start();
        StepDefs o2 = factory.getInstance(StepDefs.class);
        factory.stop();
        Assert.assertNotNull(o1);
        Assert.assertNotSame(o1, o2);
    }

    @Test
    public void shouldDisposeOnStop() {
        // Given
        ObjectFactory factory = new PicoFactory();
        factory.addClass(StepDefs.class);
        // When
        factory.start();
        StepDefs steps = factory.getInstance(StepDefs.class);
        // Then
        Assert.assertFalse(steps.getBelly().isDisposed());
        // When
        factory.stop();
        // Then
        Assert.assertTrue(steps.getBelly().isDisposed());
    }
}

