package cucumber.runtime.java;


import cucumber.api.java.ObjectFactory;
import org.junit.Assert;
import org.junit.Test;


public class JavaObjectFactoryTest {
    @Test
    public void shouldGiveUsNewInstancesForEachScenario() {
        ObjectFactory factory = new DefaultJavaObjectFactory();
        factory.addClass(JavaObjectFactoryTest.SteDef.class);
        // Scenario 1
        factory.start();
        JavaObjectFactoryTest.SteDef o1 = factory.getInstance(JavaObjectFactoryTest.SteDef.class);
        factory.stop();
        // Scenario 2
        factory.start();
        JavaObjectFactoryTest.SteDef o2 = factory.getInstance(JavaObjectFactoryTest.SteDef.class);
        factory.stop();
        Assert.assertNotNull(o1);
        Assert.assertNotSame(o1, o2);
    }

    // we just test the instances
    public static class SteDef {}
}

