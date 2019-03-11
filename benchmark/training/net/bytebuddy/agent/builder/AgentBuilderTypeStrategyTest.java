package net.bytebuddy.agent.builder;


import VisibilityBridgeStrategy.Default.NEVER;
import java.security.ProtectionDomain;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy.Default.REBASE;
import static net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy.Default.REDEFINE;
import static net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy.Default.REDEFINE_FROZEN;
import static net.bytebuddy.dynamic.scaffold.InstrumentedType.Factory.Default.FROZEN;
import static net.bytebuddy.matcher.LatentMatcher.ForSelfDeclaredMethod.NOT_DECLARED;


public class AgentBuilderTypeStrategyTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ByteBuddy byteBuddy;

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
    private DynamicType.Builder<?> dynamicTypeBuilder;

    @Test
    @SuppressWarnings("unchecked")
    public void testRebase() throws Exception {
        Mockito.when(byteBuddy.rebase(typeDescription, classFileLocator, methodNameTransformer)).thenReturn(((DynamicType.Builder) (dynamicTypeBuilder)));
        MatcherAssert.assertThat(REBASE.builder(typeDescription, byteBuddy, classFileLocator, methodNameTransformer, classLoader, module, protectionDomain), CoreMatchers.is(((DynamicType.Builder) (dynamicTypeBuilder))));
        Mockito.verify(byteBuddy).rebase(typeDescription, classFileLocator, methodNameTransformer);
        Mockito.verifyNoMoreInteractions(byteBuddy);
        Mockito.verifyZeroInteractions(dynamicTypeBuilder);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefine() throws Exception {
        Mockito.when(byteBuddy.redefine(typeDescription, classFileLocator)).thenReturn(((DynamicType.Builder) (dynamicTypeBuilder)));
        MatcherAssert.assertThat(REDEFINE.builder(typeDescription, byteBuddy, classFileLocator, methodNameTransformer, classLoader, module, protectionDomain), CoreMatchers.is(((DynamicType.Builder) (dynamicTypeBuilder))));
        Mockito.verify(byteBuddy).redefine(typeDescription, classFileLocator);
        Mockito.verifyNoMoreInteractions(byteBuddy);
        Mockito.verifyZeroInteractions(dynamicTypeBuilder);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefineFrozen() throws Exception {
        Mockito.when(byteBuddy.with(FROZEN)).thenReturn(byteBuddy);
        Mockito.when(byteBuddy.with(NEVER)).thenReturn(byteBuddy);
        Mockito.when(byteBuddy.redefine(typeDescription, classFileLocator)).thenReturn(((DynamicType.Builder) (dynamicTypeBuilder)));
        Mockito.when(dynamicTypeBuilder.ignoreAlso(NOT_DECLARED)).thenReturn(((DynamicType.Builder) (dynamicTypeBuilder)));
        MatcherAssert.assertThat(REDEFINE_FROZEN.builder(typeDescription, byteBuddy, classFileLocator, methodNameTransformer, classLoader, module, protectionDomain), CoreMatchers.is(((DynamicType.Builder) (dynamicTypeBuilder))));
        Mockito.verify(byteBuddy).with(FROZEN);
        Mockito.verify(byteBuddy).with(NEVER);
        Mockito.verify(byteBuddy).redefine(typeDescription, classFileLocator);
        Mockito.verifyNoMoreInteractions(byteBuddy);
        Mockito.verify(dynamicTypeBuilder).ignoreAlso(NOT_DECLARED);
        Mockito.verifyNoMoreInteractions(dynamicTypeBuilder);
    }
}

