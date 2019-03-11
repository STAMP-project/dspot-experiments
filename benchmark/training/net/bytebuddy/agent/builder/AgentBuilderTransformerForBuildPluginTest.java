package net.bytebuddy.agent.builder;


import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AgentBuilderTransformerForBuildPluginTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Plugin plugin;

    @Mock
    private DynamicType.Builder<?> builder;

    @Mock
    private DynamicType.Builder<?> result;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private JavaModule module;

    @Test
    @SuppressWarnings("unchecked")
    public void testApplication() throws Exception {
        Mockito.when(plugin.apply(ArgumentMatchers.eq(builder), ArgumentMatchers.eq(typeDescription), ArgumentMatchers.any(ClassFileLocator.ForClassLoader.class))).thenReturn(((DynamicType.Builder) (result)));
        MatcherAssert.assertThat(new AgentBuilder.Transformer.ForBuildPlugin(plugin).transform(builder, typeDescription, classLoader, module), CoreMatchers.is(((DynamicType.Builder) (result))));
        Mockito.verify(plugin).apply(ArgumentMatchers.eq(builder), ArgumentMatchers.eq(typeDescription), ArgumentMatchers.any(ClassFileLocator.ForClassLoader.class));
        Mockito.verifyNoMoreInteractions(plugin);
    }
}

