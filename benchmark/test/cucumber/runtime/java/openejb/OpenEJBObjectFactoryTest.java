package cucumber.runtime.java.openejb;


import cucumber.api.java.ObjectFactory;
import org.junit.Assert;
import org.junit.Test;


public class OpenEJBObjectFactoryTest {
    @Test
    public void shouldGiveUsNewInstancesForEachScenario() {
        ObjectFactory factory = new OpenEJBObjectFactory();
        factory.addClass(BellyStepdefs.class);
        // Scenario 1
        factory.start();
        BellyStepdefs o1 = factory.getInstance(BellyStepdefs.class);
        factory.stop();
        // Scenario 2
        factory.start();
        BellyStepdefs o2 = factory.getInstance(BellyStepdefs.class);
        factory.stop();
        Assert.assertNotNull(o1);
        Assert.assertNotSame(o1, o2);
    }
}

