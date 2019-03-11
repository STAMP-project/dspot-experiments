package net.bytebuddy.agent.builder;


import java.security.ProtectionDomain;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AgentBuilderRawMatcherForElementMatchersTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ElementMatcher<TypeDescription> typeMatcher;

    @Mock
    private ElementMatcher<ClassLoader> classLoaderMatcher;

    @Mock
    private ElementMatcher<JavaModule> moduleMatcher;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private JavaModule module;

    @Mock
    private ProtectionDomain protectionDomain;

    @Test
    public void testNoneMatches() throws Exception {
        MatcherAssert.assertThat(new AgentBuilder.RawMatcher.ForElementMatchers(typeMatcher, classLoaderMatcher, moduleMatcher).matches(typeDescription, classLoader, module, Object.class, protectionDomain), CoreMatchers.is(false));
        Mockito.verify(moduleMatcher).matches(module);
        Mockito.verifyNoMoreInteractions(moduleMatcher);
        Mockito.verifyNoMoreInteractions(classLoaderMatcher);
        Mockito.verifyZeroInteractions(typeMatcher);
    }

    @Test
    public void testModuleMatches() throws Exception {
        Mockito.when(moduleMatcher.matches(module)).thenReturn(true);
        MatcherAssert.assertThat(new AgentBuilder.RawMatcher.ForElementMatchers(typeMatcher, classLoaderMatcher, moduleMatcher).matches(typeDescription, classLoader, module, Object.class, protectionDomain), CoreMatchers.is(false));
        Mockito.verify(moduleMatcher).matches(module);
        Mockito.verifyNoMoreInteractions(moduleMatcher);
        Mockito.verify(classLoaderMatcher).matches(classLoader);
        Mockito.verifyNoMoreInteractions(classLoaderMatcher);
        Mockito.verifyZeroInteractions(typeMatcher);
    }

    @Test
    public void testClassLoaderMatches() throws Exception {
        Mockito.when(classLoaderMatcher.matches(classLoader)).thenReturn(true);
        MatcherAssert.assertThat(new AgentBuilder.RawMatcher.ForElementMatchers(typeMatcher, classLoaderMatcher, moduleMatcher).matches(typeDescription, classLoader, module, Object.class, protectionDomain), CoreMatchers.is(false));
        Mockito.verify(moduleMatcher).matches(module);
        Mockito.verifyNoMoreInteractions(moduleMatcher);
        Mockito.verifyZeroInteractions(classLoaderMatcher);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testModuleAndClassLoaderMatches() throws Exception {
        Mockito.when(moduleMatcher.matches(module)).thenReturn(true);
        Mockito.when(classLoaderMatcher.matches(classLoader)).thenReturn(true);
        MatcherAssert.assertThat(new AgentBuilder.RawMatcher.ForElementMatchers(typeMatcher, classLoaderMatcher, moduleMatcher).matches(typeDescription, classLoader, module, Object.class, protectionDomain), CoreMatchers.is(false));
        Mockito.verify(moduleMatcher).matches(module);
        Mockito.verifyNoMoreInteractions(moduleMatcher);
        Mockito.verify(classLoaderMatcher).matches(classLoader);
        Mockito.verifyNoMoreInteractions(classLoaderMatcher);
        Mockito.verify(typeMatcher).matches(typeDescription);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testModuleAndTypeMatches() throws Exception {
        Mockito.when(moduleMatcher.matches(module)).thenReturn(true);
        Mockito.when(typeMatcher.matches(typeDescription)).thenReturn(true);
        MatcherAssert.assertThat(new AgentBuilder.RawMatcher.ForElementMatchers(typeMatcher, classLoaderMatcher, moduleMatcher).matches(typeDescription, classLoader, module, Object.class, protectionDomain), CoreMatchers.is(false));
        Mockito.verify(moduleMatcher).matches(module);
        Mockito.verifyNoMoreInteractions(moduleMatcher);
        Mockito.verify(classLoaderMatcher).matches(classLoader);
        Mockito.verifyNoMoreInteractions(classLoaderMatcher);
        Mockito.verifyZeroInteractions(typeMatcher);
    }

    @Test
    public void testClassLoaderAndTypeMatches() throws Exception {
        Mockito.when(classLoaderMatcher.matches(classLoader)).thenReturn(true);
        Mockito.when(typeMatcher.matches(typeDescription)).thenReturn(true);
        MatcherAssert.assertThat(new AgentBuilder.RawMatcher.ForElementMatchers(typeMatcher, classLoaderMatcher, moduleMatcher).matches(typeDescription, classLoader, module, Object.class, protectionDomain), CoreMatchers.is(false));
        Mockito.verify(moduleMatcher).matches(module);
        Mockito.verifyNoMoreInteractions(moduleMatcher);
        Mockito.verifyZeroInteractions(classLoaderMatcher);
        Mockito.verifyZeroInteractions(typeMatcher);
    }

    @Test
    public void testTypeMatches() throws Exception {
        Mockito.when(typeMatcher.matches(typeDescription)).thenReturn(true);
        MatcherAssert.assertThat(new AgentBuilder.RawMatcher.ForElementMatchers(typeMatcher, classLoaderMatcher, moduleMatcher).matches(typeDescription, classLoader, module, Object.class, protectionDomain), CoreMatchers.is(false));
        Mockito.verify(moduleMatcher).matches(module);
        Mockito.verifyNoMoreInteractions(moduleMatcher);
        Mockito.verifyZeroInteractions(classLoaderMatcher);
        Mockito.verifyZeroInteractions(typeMatcher);
    }

    @Test
    public void testAllMatches() throws Exception {
        Mockito.when(moduleMatcher.matches(module)).thenReturn(true);
        Mockito.when(classLoaderMatcher.matches(classLoader)).thenReturn(true);
        Mockito.when(typeMatcher.matches(typeDescription)).thenReturn(true);
        MatcherAssert.assertThat(new AgentBuilder.RawMatcher.ForElementMatchers(typeMatcher, classLoaderMatcher, moduleMatcher).matches(typeDescription, classLoader, module, Object.class, protectionDomain), CoreMatchers.is(true));
        Mockito.verify(classLoaderMatcher).matches(classLoader);
        Mockito.verifyNoMoreInteractions(classLoaderMatcher);
        Mockito.verify(typeMatcher).matches(typeDescription);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }
}

