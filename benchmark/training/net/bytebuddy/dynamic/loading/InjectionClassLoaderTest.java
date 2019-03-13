package net.bytebuddy.dynamic.loading;


import java.util.Collections;
import java.util.Map;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.loading.InjectionClassLoader.Strategy.INSTANCE;


public class InjectionClassLoaderTest {
    private static final String FOO = "foo";

    @Test(expected = IllegalArgumentException.class)
    public void testBootstrap() throws Exception {
        INSTANCE.load(null, Collections.<TypeDescription, byte[]>emptyMap());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInjection() throws Exception {
        InjectionClassLoader classLoader = Mockito.mock(InjectionClassLoader.class);
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        byte[] binaryRepresentation = new byte[0];
        Mockito.when(typeDescription.getName()).thenReturn(InjectionClassLoaderTest.FOO);
        Mockito.when(defineClasses(Collections.singletonMap(InjectionClassLoaderTest.FOO, binaryRepresentation))).thenReturn(((Map) (Collections.singletonMap(InjectionClassLoaderTest.FOO, Object.class))));
        MatcherAssert.assertThat(INSTANCE.load(classLoader, Collections.singletonMap(typeDescription, binaryRepresentation)), CoreMatchers.is(Collections.<TypeDescription, Class<?>>singletonMap(typeDescription, Object.class)));
        defineClasses(Collections.singletonMap(InjectionClassLoaderTest.FOO, binaryRepresentation));
        Mockito.verifyNoMoreInteractions(classLoader);
    }

    @Test(expected = IllegalStateException.class)
    public void testInjectionException() throws Exception {
        InjectionClassLoader classLoader = Mockito.mock(InjectionClassLoader.class);
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        byte[] binaryRepresentation = new byte[0];
        Mockito.when(typeDescription.getName()).thenReturn(InjectionClassLoaderTest.FOO);
        Mockito.when(defineClasses(Collections.singletonMap(InjectionClassLoaderTest.FOO, binaryRepresentation))).thenThrow(new ClassNotFoundException(InjectionClassLoaderTest.FOO));
        INSTANCE.load(classLoader, Collections.singletonMap(typeDescription, binaryRepresentation));
    }
}

