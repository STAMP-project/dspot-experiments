package net.bytebuddy.dynamic;


import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.util.Map;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.utility.StreamDrainer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of;
import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class ClassFileLocatorForClassLoaderTest {
    private static final String FOOBAR = "foo/bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ClassFileLocatorForClassLoaderTest.ClosableClassLoader classLoader;

    @Test
    public void testCreation() throws Exception {
        MatcherAssert.assertThat(of(classLoader), FieldByFieldComparison.hasPrototype(((ClassFileLocator) (new ClassFileLocator.ForClassLoader(classLoader)))));
        MatcherAssert.assertThat(of(null), CoreMatchers.not(ofSystemLoader()));
    }

    @Test
    public void testLocatable() throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{ 1, 2, 3 });
        Mockito.when(classLoader.getResourceAsStream(((ClassFileLocatorForClassLoaderTest.FOOBAR) + ".class"))).thenReturn(inputStream);
        ClassFileLocator.Resolution resolution = new ClassFileLocator.ForClassLoader(classLoader).locate(ClassFileLocatorForClassLoaderTest.FOOBAR);
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(new byte[]{ 1, 2, 3 }));
        Mockito.verify(classLoader).getResourceAsStream(((ClassFileLocatorForClassLoaderTest.FOOBAR) + ".class"));
        Mockito.verifyNoMoreInteractions(classLoader);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonLocatable() throws Exception {
        ClassFileLocator.Resolution resolution = new ClassFileLocator.ForClassLoader(classLoader).locate(ClassFileLocatorForClassLoaderTest.FOOBAR);
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(false));
        Mockito.verify(classLoader).getResourceAsStream(((ClassFileLocatorForClassLoaderTest.FOOBAR) + ".class"));
        Mockito.verifyNoMoreInteractions(classLoader);
        resolution.resolve();
        Assert.fail();
    }

    @Test
    public void testReadTypeBootstrapClassLoader() throws Exception {
        byte[] binaryRepresentation = read(Object.class);
        JavaModule module = JavaModule.ofType(Object.class);
        InputStream inputStream = (module == null) ? Object.class.getResourceAsStream(((Object.class.getSimpleName()) + ".class")) : module.getResourceAsStream(((Object.class.getName().replace('.', '/')) + ".class"));
        try {
            MatcherAssert.assertThat(binaryRepresentation, CoreMatchers.is(StreamDrainer.DEFAULT.drain(inputStream)));
        } finally {
            inputStream.close();
        }
    }

    @Test
    public void testReadTypeNonBootstrapClassLoader() throws Exception {
        byte[] binaryRepresentation = read(ClassFileLocatorForClassLoaderTest.Foo.class);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(((ClassFileLocatorForClassLoaderTest.Foo.class.getName().replace('.', '/')) + ".class"));
        try {
            MatcherAssert.assertThat(binaryRepresentation, CoreMatchers.is(StreamDrainer.DEFAULT.drain(inputStream)));
        } finally {
            inputStream.close();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testReadTypeIllegal() throws Exception {
        Class<?> nonClassFileType = new ByteBuddy().subclass(Object.class).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        read(nonClassFileType);
    }

    @Test
    public void testReadTypesMultiple() throws Exception {
        Map<Class<?>, byte[]> binaryRepresentations = read(Object.class, ClassFileLocatorForClassLoaderTest.Foo.class);
        MatcherAssert.assertThat(binaryRepresentations.size(), CoreMatchers.is(2));
        JavaModule module = JavaModule.ofType(Object.class);
        InputStream objectStream = (module == null) ? Object.class.getResourceAsStream(((Object.class.getSimpleName()) + ".class")) : module.getResourceAsStream(((Object.class.getName().replace('.', '/')) + ".class"));
        try {
            MatcherAssert.assertThat(binaryRepresentations.get(Object.class), CoreMatchers.is(StreamDrainer.DEFAULT.drain(objectStream)));
        } finally {
            objectStream.close();
        }
        InputStream fooStream = getClass().getClassLoader().getResourceAsStream(((ClassFileLocatorForClassLoaderTest.Foo.class.getName().replace('.', '/')) + ".class"));
        try {
            MatcherAssert.assertThat(binaryRepresentations.get(ClassFileLocatorForClassLoaderTest.Foo.class), CoreMatchers.is(StreamDrainer.DEFAULT.drain(fooStream)));
        } finally {
            fooStream.close();
        }
    }

    @Test
    public void testReadTypesToNames() throws Exception {
        Map<String, byte[]> binaryRepresentations = readToNames(Object.class, ClassFileLocatorForClassLoaderTest.Foo.class);
        MatcherAssert.assertThat(binaryRepresentations.size(), CoreMatchers.is(2));
        JavaModule module = JavaModule.ofType(Object.class);
        InputStream objectStream = (module == null) ? Object.class.getResourceAsStream(((Object.class.getSimpleName()) + ".class")) : module.getResourceAsStream(((Object.class.getName().replace('.', '/')) + ".class"));
        try {
            MatcherAssert.assertThat(binaryRepresentations.get(Object.class.getName()), CoreMatchers.is(StreamDrainer.DEFAULT.drain(objectStream)));
        } finally {
            objectStream.close();
        }
        InputStream fooStream = getClass().getClassLoader().getResourceAsStream(((ClassFileLocatorForClassLoaderTest.Foo.class.getName().replace('.', '/')) + ".class"));
        try {
            MatcherAssert.assertThat(binaryRepresentations.get(ClassFileLocatorForClassLoaderTest.Foo.class.getName()), CoreMatchers.is(StreamDrainer.DEFAULT.drain(fooStream)));
        } finally {
            fooStream.close();
        }
    }

    @Test
    public void testClose() throws Exception {
        of(classLoader).close();
        Mockito.verifyZeroInteractions(classLoader);
    }

    @Test
    public void testSystemClassLoader() throws Exception {
        ClassFileLocator classFileLocator = ClassFileLocator.ForClassLoader.ofSystemLoader();
        MatcherAssert.assertThat(classFileLocator.locate(getClass().getName()).isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(classFileLocator.locate(Object.class.getName()).isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(classFileLocator.locate("foo.Bar").isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testPlatformLoader() throws Exception {
        ClassFileLocator classFileLocator = ofPlatformLoader();
        MatcherAssert.assertThat(classFileLocator.locate(getClass().getName()).isResolved(), CoreMatchers.is(false));
        MatcherAssert.assertThat(classFileLocator.locate(Object.class.getName()).isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(classFileLocator.locate("foo.Bar").isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testBootLoader() throws Exception {
        ClassFileLocator classFileLocator = ofBootLoader();
        MatcherAssert.assertThat(classFileLocator.locate(getClass().getName()).isResolved(), CoreMatchers.is(false));
        MatcherAssert.assertThat(classFileLocator.locate(Object.class.getName()).isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(classFileLocator.locate("foo.Bar").isResolved(), CoreMatchers.is(false));
    }

    /* empty */
    private static class Foo {}

    /* empty */
    private abstract static class ClosableClassLoader extends ClassLoader implements Closeable {}
}

