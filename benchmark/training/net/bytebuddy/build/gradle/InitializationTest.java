package net.bytebuddy.build.gradle;


import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.EntryPoint;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.test.utility.MockitoRule;
import org.gradle.api.GradleException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.build.EntryPoint.Default.REBASE;
import static net.bytebuddy.build.EntryPoint.Default.REDEFINE;
import static net.bytebuddy.build.EntryPoint.Default.REDEFINE_LOCAL;


public class InitializationTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ClassLoaderResolver classLoaderResolver;

    @Mock
    private File file;

    @Mock
    private File explicit;

    @Mock
    private File other;

    @Test
    public void testRebase() throws Exception {
        Initialization initialization = new Initialization();
        initialization.setEntryPoint(REBASE.name());
        MatcherAssert.assertThat(initialization.getEntryPoint(classLoaderResolver, explicit, Collections.singleton(other)), CoreMatchers.is(((EntryPoint) (REBASE))));
        Mockito.verifyZeroInteractions(classLoaderResolver);
    }

    @Test
    public void testRedefine() throws Exception {
        Initialization initialization = new Initialization();
        initialization.setEntryPoint(REDEFINE.name());
        MatcherAssert.assertThat(initialization.getEntryPoint(classLoaderResolver, explicit, Collections.singleton(other)), CoreMatchers.is(((EntryPoint) (REDEFINE))));
        Mockito.verifyZeroInteractions(classLoaderResolver);
    }

    @Test
    public void testRedefineLocal() throws Exception {
        Initialization initialization = new Initialization();
        initialization.setEntryPoint(REDEFINE_LOCAL.name());
        MatcherAssert.assertThat(initialization.getEntryPoint(classLoaderResolver, explicit, Collections.singleton(other)), CoreMatchers.is(((EntryPoint) (REDEFINE_LOCAL))));
        Mockito.verifyZeroInteractions(classLoaderResolver);
    }

    @Test
    public void testExplicitClassPath() throws Exception {
        Initialization initialization = new Initialization();
        initialization.setClassPath(Collections.singleton(file));
        Iterator<? extends File> iterator = initialization.getClassPath(explicit, Collections.singleton(other)).iterator();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(file));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }

    @Test
    public void testCustom() throws Exception {
        Initialization initialization = new Initialization();
        initialization.setEntryPoint(InitializationTest.Foo.class.getName());
        initialization.setClassPath(Collections.singleton(file));
        Mockito.when(classLoaderResolver.resolve(Collections.singleton(file))).thenReturn(InitializationTest.Foo.class.getClassLoader());
        MatcherAssert.assertThat(initialization.getEntryPoint(classLoaderResolver, explicit, Collections.singleton(other)), CoreMatchers.instanceOf(InitializationTest.Foo.class));
        Mockito.verify(classLoaderResolver).resolve(Collections.singleton(file));
        Mockito.verifyNoMoreInteractions(classLoaderResolver);
    }

    @Test(expected = GradleException.class)
    public void testCustomFailed() throws Exception {
        Initialization initialization = new Initialization();
        initialization.setClassPath(Collections.singleton(file));
        initialization.setEntryPoint(InitializationTest.FOO);
        Mockito.when(classLoaderResolver.resolve(Collections.singleton(file)));
        initialization.getEntryPoint(classLoaderResolver, explicit, Collections.singleton(other));
    }

    @Test(expected = GradleException.class)
    public void testEmpty() throws Exception {
        Initialization initialization = new Initialization();
        initialization.setEntryPoint("");
        initialization.getEntryPoint(classLoaderResolver, explicit, Collections.singleton(other));
    }

    @Test(expected = GradleException.class)
    public void testNull() throws Exception {
        new Initialization().getEntryPoint(classLoaderResolver, explicit, Collections.singleton(other));
    }

    @Test
    public void testImplicitClassPath() throws Exception {
        Initialization initialization = new Initialization();
        Iterator<? extends File> iterator = initialization.getClassPath(explicit, Collections.singleton(other)).iterator();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(explicit));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(other));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }

    public static class Foo implements EntryPoint {
        public ByteBuddy byteBuddy(ClassFileVersion classFileVersion) {
            throw new AssertionError();
        }

        public DynamicType.Builder<?> transform(TypeDescription typeDescription, ByteBuddy byteBuddy, ClassFileLocator classFileLocator, MethodNameTransformer methodNameTransformer) {
            throw new AssertionError();
        }
    }
}

