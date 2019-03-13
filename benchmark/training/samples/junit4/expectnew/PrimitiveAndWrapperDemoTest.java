package samples.junit4.expectnew;


import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.exceptions.TooManyConstructorsFoundException;
import samples.expectnew.PrimitiveAndWrapperDemo;


/**
 * Unit test for the {@link PrimitiveAndWrapperDemo} class.
 */
@RunWith(PowerMockRunner.class)
public class PrimitiveAndWrapperDemoTest {
    @Test
    public void testWhenConstructorCannotBeDetermined() throws Exception {
        try {
            PowerMock.createMockAndExpectNew(PrimitiveAndWrapperDemo.class, 2);
            Assert.fail("Should throw TooManyConstructorsFoundException");
        } catch (TooManyConstructorsFoundException e) {
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString(("Several matching constructors found, please specify the argument parameter types so that PowerMock can determine which method you're referring to." + "\nMatching constructors in class samples.expectnew.PrimitiveAndWrapperDemo were:\n")));
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("samples.expectnew.PrimitiveAndWrapperDemo( java.lang.Integer.class )\n"));
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("samples.expectnew.PrimitiveAndWrapperDemo( int.class )\n"));
        }
    }

    @Test
    public void testWrapperConstructor() throws Exception {
        PowerMock.createMockAndExpectNew(PrimitiveAndWrapperDemo.class, new Class<?>[]{ Integer.class }, 2);
    }

    @Test
    public void testPrimitiveConstructor() throws Exception {
        PowerMock.createMockAndExpectNew(PrimitiveAndWrapperDemo.class, new Class<?>[]{ int.class }, 2);
    }
}

