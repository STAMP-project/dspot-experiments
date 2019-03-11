package net.bytebuddy.agent.builder;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.Transformer.NoOp.INSTANCE;


public class AgentBuilderTransformerTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private DynamicType.Builder<?> builder;

    @Mock
    private AgentBuilder.Transformer first;

    @Mock
    private AgentBuilder.Transformer second;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private JavaModule module;

    @Test
    @SuppressWarnings("unchecked")
    public void testNoOp() throws Exception {
        MatcherAssert.assertThat(INSTANCE.transform(builder, typeDescription, classLoader, module), CoreMatchers.sameInstance(((DynamicType.Builder) (builder))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompound() throws Exception {
        Mockito.when(first.transform(builder, typeDescription, classLoader, module)).thenReturn(((DynamicType.Builder) (builder)));
        Mockito.when(second.transform(builder, typeDescription, classLoader, module)).thenReturn(((DynamicType.Builder) (builder)));
        MatcherAssert.assertThat(new AgentBuilder.Transformer.Compound(first, second).transform(builder, typeDescription, classLoader, module), CoreMatchers.sameInstance(((DynamicType.Builder) (builder))));
        Mockito.verify(first).transform(builder, typeDescription, classLoader, module);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).transform(builder, typeDescription, classLoader, module);
        Mockito.verifyNoMoreInteractions(second);
    }
}

