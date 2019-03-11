package net.bytebuddy.pool;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;
import static net.bytebuddy.dynamic.ClassFileLocator.Resolution.Explicit.<init>;
import static net.bytebuddy.pool.TypePool.Default.ReaderMode.FAST;


public class TypePoolDefaultHierarchyTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypePool parentPool;

    @Mock
    private TypePool.CacheProvider cacheProvider;

    @Mock
    private ClassFileLocator classFileLocator;

    @Mock
    private TypePool.Resolution resolution;

    @Test
    public void testParentFirst() throws Exception {
        TypePool typePool = new TypePool.Default(cacheProvider, classFileLocator, FAST, parentPool);
        Mockito.when(parentPool.describe(TypePoolDefaultHierarchyTest.FOO)).thenReturn(resolution);
        Mockito.when(resolution.isResolved()).thenReturn(true);
        MatcherAssert.assertThat(typePool.describe(TypePoolDefaultHierarchyTest.FOO), CoreMatchers.is(resolution));
        Mockito.verifyZeroInteractions(cacheProvider);
        Mockito.verifyZeroInteractions(classFileLocator);
        Mockito.verify(parentPool).describe(TypePoolDefaultHierarchyTest.FOO);
        Mockito.verifyNoMoreInteractions(parentPool);
        Mockito.verify(resolution).isResolved();
        Mockito.verifyNoMoreInteractions(resolution);
    }

    @Test
    public void testChildSecond() throws Exception {
        TypePool typePool = new TypePool.Default(cacheProvider, classFileLocator, FAST, parentPool);
        Mockito.when(parentPool.describe(TypePoolDefaultHierarchyTest.FOO)).thenReturn(resolution);
        Mockito.when(resolution.isResolved()).thenReturn(false);
        Mockito.when(classFileLocator.locate(TypePoolDefaultHierarchyTest.FOO)).thenReturn(new ClassFileLocator.Resolution.Explicit(read(TypePoolDefaultHierarchyTest.Foo.class)));
        Mockito.when(cacheProvider.register(ArgumentMatchers.eq(TypePoolDefaultHierarchyTest.FOO), ArgumentMatchers.any(TypePool.Resolution.class))).then(new Answer<TypePool.Resolution>() {
            public TypePool.Resolution answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ((TypePool.Resolution) (invocationOnMock.getArguments()[1]));
            }
        });
        TypePool.Resolution resolution = typePool.describe(TypePoolDefaultHierarchyTest.FOO);
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(((TypeDescription) (of(TypePoolDefaultHierarchyTest.Foo.class)))));
        Mockito.verify(cacheProvider).find(TypePoolDefaultHierarchyTest.FOO);
        Mockito.verify(cacheProvider).register(TypePoolDefaultHierarchyTest.FOO, resolution);
        Mockito.verifyZeroInteractions(cacheProvider);
        Mockito.verify(classFileLocator).locate(TypePoolDefaultHierarchyTest.FOO);
        Mockito.verifyNoMoreInteractions(classFileLocator);
        Mockito.verify(parentPool).describe(TypePoolDefaultHierarchyTest.FOO);
        Mockito.verifyNoMoreInteractions(parentPool);
        Mockito.verify(this.resolution).isResolved();
        Mockito.verifyNoMoreInteractions(this.resolution);
    }

    @Test
    public void testClear() throws Exception {
        TypePool typePool = new TypePool.Default(cacheProvider, classFileLocator, FAST, parentPool);
        typePool.clear();
        Mockito.verify(cacheProvider).clear();
        Mockito.verifyNoMoreInteractions(cacheProvider);
        Mockito.verify(parentPool).clear();
        Mockito.verifyNoMoreInteractions(parentPool);
    }

    /* empty */
    private static class Foo {}
}

