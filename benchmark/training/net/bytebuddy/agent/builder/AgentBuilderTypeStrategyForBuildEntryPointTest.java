package net.bytebuddy.agent.builder;


import java.security.ProtectionDomain;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.build.EntryPoint;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AgentBuilderTypeStrategyForBuildEntryPointTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private EntryPoint entryPoint;

    @Mock
    private ByteBuddy byteBuddy;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ClassFileLocator classFileLocator;

    @Mock
    private MethodNameTransformer methodNameTransformer;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private JavaModule module;

    @Mock
    private ProtectionDomain protectionDomain;

    @Mock
    private DynamicType.Builder<?> builder;

    @Test
    @SuppressWarnings("unchecked")
    public void testApplication() throws Exception {
        Mockito.when(entryPoint.transform(typeDescription, byteBuddy, classFileLocator, methodNameTransformer)).thenReturn(((DynamicType.Builder) (builder)));
        MatcherAssert.assertThat(new AgentBuilder.TypeStrategy.ForBuildEntryPoint(entryPoint).builder(typeDescription, byteBuddy, classFileLocator, methodNameTransformer, classLoader, module, protectionDomain), CoreMatchers.is(((DynamicType.Builder) (builder))));
        Mockito.verify(entryPoint).transform(typeDescription, byteBuddy, classFileLocator, methodNameTransformer);
        Mockito.verifyNoMoreInteractions(entryPoint);
    }
}

