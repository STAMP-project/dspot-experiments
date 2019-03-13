package net.bytebuddy.agent;


import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ByteBuddyAgentTest {
    private static final String INSTRUMENTATION = "instrumentation";

    private static final Object STATIC_FIELD = null;

    private Instrumentation actualInstrumentation;

    @Test
    public void testInstrumentationExtraction() throws Exception {
        Field field = Installer.class.getDeclaredField(ByteBuddyAgentTest.INSTRUMENTATION);
        field.setAccessible(true);
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        field.set(ByteBuddyAgentTest.STATIC_FIELD, instrumentation);
        MatcherAssert.assertThat(ByteBuddyAgent.getInstrumentation(), CoreMatchers.is(instrumentation));
    }

    @Test(expected = IllegalStateException.class)
    public void testMissingInstrumentationThrowsException() throws Exception {
        Field field = Installer.class.getDeclaredField(ByteBuddyAgentTest.INSTRUMENTATION);
        field.setAccessible(true);
        field.set(ByteBuddyAgentTest.STATIC_FIELD, null);
        ByteBuddyAgent.getInstrumentation();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testConstructorThrowsException() throws Exception {
        Constructor<?> constructor = ByteBuddyAgent.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            Assert.fail();
        } catch (InvocationTargetException exception) {
            throw ((Exception) (exception.getCause()));
        }
    }
}

