package net.bytebuddy.agent;


import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class InstallerTest {
    private static final String FOO = "foo";

    private static final String INSTRUMENTATION = "instrumentation";

    private static final Object STATIC_FIELD = null;

    @Rule
    public final TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Instrumentation instrumentation;

    private Instrumentation actualInstrumentation;

    @Test
    public void testPreMain() throws Exception {
        Installer.premain(InstallerTest.FOO, instrumentation);
        MatcherAssert.assertThat(ByteBuddyAgent.getInstrumentation(), CoreMatchers.is(instrumentation));
    }

    @Test
    public void testAgentMain() throws Exception {
        Installer.agentmain(InstallerTest.FOO, instrumentation);
        MatcherAssert.assertThat(ByteBuddyAgent.getInstrumentation(), CoreMatchers.is(instrumentation));
    }

    @Test
    public void testAgentInstallerIsPublic() throws Exception {
        Class<?> type = Installer.class;
        MatcherAssert.assertThat(Modifier.isPublic(type.getModifiers()), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.getDeclaringClass(), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(type.getDeclaredClasses().length, CoreMatchers.is(0));
    }

    @Test
    public void testAgentInstallerStoreIsPrivate() throws Exception {
        Field field = Installer.class.getDeclaredField("instrumentation");
        MatcherAssert.assertThat(Modifier.isPrivate(field.getModifiers()), CoreMatchers.is(true));
    }

    @Test
    public void testAgentInstallerGetterIsPublic() throws Exception {
        Method method = Installer.class.getDeclaredMethod("getInstrumentation");
        MatcherAssert.assertThat(Modifier.isPublic(method.getModifiers()), CoreMatchers.is(true));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testConstructorThrowsException() throws Exception {
        Constructor<?> constructor = Installer.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            Assert.fail();
        } catch (InvocationTargetException exception) {
            throw ((Exception) (exception.getCause()));
        }
    }
}

