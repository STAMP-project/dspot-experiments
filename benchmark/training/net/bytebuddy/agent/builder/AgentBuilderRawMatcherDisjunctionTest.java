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


public class AgentBuilderRawMatcherDisjunctionTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private AgentBuilder.RawMatcher left;

    @Mock
    private AgentBuilder.RawMatcher right;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private JavaModule module;

    @Mock
    private ProtectionDomain protectionDomain;

    @Test
    public void testMatches() throws Exception {
        Mockito.when(left.matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain)).thenReturn(true);
        Mockito.when(right.matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain)).thenReturn(true);
        AgentBuilder.RawMatcher rawMatcher = new AgentBuilder.RawMatcher.Disjunction(left, right);
        Assert.assertThat(rawMatcher.matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain), CoreMatchers.is(true));
        Mockito.verify(left).matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain);
        Mockito.verifyNoMoreInteractions(left);
        Mockito.verifyZeroInteractions(right);
    }

    @Test
    public void testNotMatchesLeft() throws Exception {
        Mockito.when(left.matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain)).thenReturn(true);
        Mockito.when(right.matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain)).thenReturn(false);
        AgentBuilder.RawMatcher rawMatcher = new AgentBuilder.RawMatcher.Disjunction(left, right);
        Assert.assertThat(rawMatcher.matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain), CoreMatchers.is(true));
        Mockito.verify(left).matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain);
        Mockito.verifyNoMoreInteractions(left);
        Mockito.verifyZeroInteractions(right);
    }

    @Test
    public void testNotMatchesRight() throws Exception {
        Mockito.when(left.matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain)).thenReturn(false);
        Mockito.when(right.matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain)).thenReturn(true);
        AgentBuilder.RawMatcher rawMatcher = new AgentBuilder.RawMatcher.Disjunction(left, right);
        Assert.assertThat(rawMatcher.matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain), CoreMatchers.is(true));
        Mockito.verify(left).matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain);
        Mockito.verifyNoMoreInteractions(left);
        Mockito.verify(right).matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain);
        Mockito.verifyNoMoreInteractions(right);
    }

    @Test
    public void testNotMatchesEither() throws Exception {
        Mockito.when(left.matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain)).thenReturn(false);
        Mockito.when(right.matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain)).thenReturn(false);
        AgentBuilder.RawMatcher rawMatcher = new AgentBuilder.RawMatcher.Disjunction(left, right);
        Assert.assertThat(rawMatcher.matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain), CoreMatchers.is(false));
        Mockito.verify(left).matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain);
        Mockito.verifyNoMoreInteractions(left);
        Mockito.verify(right).matches(typeDescription, classLoader, module, AgentBuilderRawMatcherDisjunctionTest.Foo.class, protectionDomain);
        Mockito.verifyNoMoreInteractions(right);
    }

    /* empty */
    private static class Foo {}
}

