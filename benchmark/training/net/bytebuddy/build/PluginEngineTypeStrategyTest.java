package net.bytebuddy.build;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PluginEngineTypeStrategyTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ByteBuddy byteBuddy;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ClassFileLocator classFileLocator;

    @Mock
    private EntryPoint entryPoint;

    @Mock
    private MethodNameTransformer methodNameTransformer;

    @Test
    public void testRebase() {
        MatcherAssert.assertThat(Engine.builder(byteBuddy, typeDescription, classFileLocator), CoreMatchers.nullValue());
        Mockito.verify(byteBuddy).rebase(typeDescription, classFileLocator);
    }

    @Test
    public void testRedefine() {
        MatcherAssert.assertThat(Engine.builder(byteBuddy, typeDescription, classFileLocator), CoreMatchers.nullValue());
        Mockito.verify(byteBuddy).redefine(typeDescription, classFileLocator);
    }

    @Test
    public void testDecorate() {
        MatcherAssert.assertThat(Engine.builder(byteBuddy, typeDescription, classFileLocator), CoreMatchers.nullValue());
        decorate(typeDescription, classFileLocator);
    }

    @Test
    public void testForEntryPorint() {
        MatcherAssert.assertThat(new Plugin.Engine.TypeStrategy.ForEntryPoint(entryPoint, methodNameTransformer).builder(byteBuddy, typeDescription, classFileLocator), CoreMatchers.nullValue());
        Mockito.verify(entryPoint).transform(typeDescription, byteBuddy, classFileLocator, methodNameTransformer);
        Mockito.verifyNoMoreInteractions(entryPoint);
    }
}

