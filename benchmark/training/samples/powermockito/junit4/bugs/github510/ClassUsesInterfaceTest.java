package samples.powermockito.junit4.bugs.github510;


import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;


/**
 *
 */
@PrepareForTest(InterfaceWithStatic.class)
public class ClassUsesInterfaceTest {
    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    public ClassUsesInterface classUsesInterface;

    @Test
    public void testSaySomething() throws Exception {
        final String value = "Hi Man";
        when(InterfaceWithStatic.sayHello()).thenReturn(value);
        assertThat(classUsesInterface.saySomething()).isEqualTo(value);
    }
}

