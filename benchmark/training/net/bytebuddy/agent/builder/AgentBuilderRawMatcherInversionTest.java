package net.bytebuddy.agent.builder;


import java.security.ProtectionDomain;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AgentBuilderRawMatcherInversionTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private AgentBuilder.RawMatcher rawMatcher;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private JavaModule module;

    @Mock
    private ProtectionDomain protectionDomain;

    @Test
    public void testInversionTrue() throws Exception {
        Mockito.when(rawMatcher.matches(typeDescription, classLoader, module, Object.class, protectionDomain)).thenReturn(true);
        Assert.assertThat(new AgentBuilder.RawMatcher.Inversion(rawMatcher).matches(typeDescription, classLoader, module, Object.class, protectionDomain), CoreMatchers.is(false));
    }

    @Test
    public void testInversionFalse() throws Exception {
        Mockito.when(rawMatcher.matches(typeDescription, classLoader, module, Object.class, protectionDomain)).thenReturn(false);
        Assert.assertThat(new AgentBuilder.RawMatcher.Inversion(rawMatcher).matches(typeDescription, classLoader, module, Object.class, protectionDomain), CoreMatchers.is(true));
    }
}

