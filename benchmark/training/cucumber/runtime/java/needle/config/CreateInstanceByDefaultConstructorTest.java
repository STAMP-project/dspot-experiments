package cucumber.runtime.java.needle.config;


import org.junit.Assert;
import org.junit.Test;

import static CreateInstanceByDefaultConstructor.INSTANCE;


public class CreateInstanceByDefaultConstructorTest {
    // empty
    public static class HasDefaultConstructor {}

    public static class DoesNotHaveDefaultConstructor {
        public DoesNotHaveDefaultConstructor(final String name) {
            // empty
        }
    }

    private final CreateInstanceByDefaultConstructor createInstanceByDefaultConstructor = INSTANCE;

    @Test
    public void shouldCreateNewInstance() {
        Assert.assertNotNull(createInstanceByDefaultConstructor.apply(CreateInstanceByDefaultConstructorTest.HasDefaultConstructor.class));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotCreateNewInstanceWhenConstructorIsMissing() {
        createInstanceByDefaultConstructor.apply(CreateInstanceByDefaultConstructorTest.DoesNotHaveDefaultConstructor.class);
    }
}

