package net.bytebuddy.dynamic.loading;


import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import junit.framework.TestCase;
import net.bytebuddy.test.utility.IntegrationRule;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;

import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;


@RunWith(Parameterized.class)
public class ByteArrayClassLoaderTest {
    private static final ProtectionDomain DEFAULT_PROTECTION_DOMAIN = null;

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String CLASS_FILE = ".class";

    private final ByteArrayClassLoader.PersistenceHandler persistenceHandler;

    private final boolean expectedResourceLookup;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Rule
    public MethodRule integrationRule = new IntegrationRule();

    private InjectionClassLoader classLoader;

    private URL sealBase;

    @Mock
    private PackageDefinitionStrategy packageDefinitionStrategy;

    @Mock
    private ClassFileTransformer classFileTransformer;

    public ByteArrayClassLoaderTest(ByteArrayClassLoader.PersistenceHandler persistenceHandler, boolean expectedResourceLookup) {
        this.persistenceHandler = persistenceHandler;
        this.expectedResourceLookup = expectedResourceLookup;
    }

    @Test
    public void testLoading() throws Exception {
        Class<?> type = classLoader.loadClass(ByteArrayClassLoaderTest.Foo.class.getName());
        MatcherAssert.assertThat(type.getClassLoader(), CoreMatchers.is(((ClassLoader) (classLoader))));
        TestCase.assertEquals(classLoader.loadClass(ByteArrayClassLoaderTest.Foo.class.getName()), type);
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.<Class<?>>is(ByteArrayClassLoaderTest.Foo.class)));
    }

    @Test
    @IntegrationRule.Enforce
    public void testPackageDefinition() throws Exception {
        Class<?> type = classLoader.loadClass(ByteArrayClassLoaderTest.Foo.class.getName());
        MatcherAssert.assertThat(type.getPackage(), CoreMatchers.notNullValue(Package.class));
        MatcherAssert.assertThat(type.getPackage(), CoreMatchers.not(ByteArrayClassLoaderTest.Foo.class.getPackage()));
        MatcherAssert.assertThat(type.getPackage().getName(), CoreMatchers.is(ByteArrayClassLoaderTest.Foo.class.getPackage().getName()));
        MatcherAssert.assertThat(type.getPackage().getSpecificationTitle(), CoreMatchers.is(ByteArrayClassLoaderTest.FOO));
        MatcherAssert.assertThat(type.getPackage().getSpecificationVersion(), CoreMatchers.is(ByteArrayClassLoaderTest.BAR));
        MatcherAssert.assertThat(type.getPackage().getSpecificationVendor(), CoreMatchers.is(ByteArrayClassLoaderTest.QUX));
        MatcherAssert.assertThat(type.getPackage().getImplementationTitle(), CoreMatchers.is(ByteArrayClassLoaderTest.QUX));
        MatcherAssert.assertThat(type.getPackage().getImplementationVersion(), CoreMatchers.is(ByteArrayClassLoaderTest.FOO));
        MatcherAssert.assertThat(type.getPackage().getImplementationVendor(), CoreMatchers.is(ByteArrayClassLoaderTest.BAR));
        MatcherAssert.assertThat(type.getPackage().isSealed(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.getPackage().isSealed(sealBase), CoreMatchers.is(true));
    }

    @Test
    public void testResourceStreamLookupBeforeLoading() throws Exception {
        InputStream inputStream = classLoader.getResourceAsStream(((ByteArrayClassLoaderTest.Foo.class.getName().replace('.', '/')) + (ByteArrayClassLoaderTest.CLASS_FILE)));
        try {
            MatcherAssert.assertThat(inputStream, (expectedResourceLookup ? CoreMatchers.notNullValue(InputStream.class) : CoreMatchers.nullValue(InputStream.class)));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Test
    public void testResourceStreamLookupAfterLoading() throws Exception {
        MatcherAssert.assertThat(classLoader.loadClass(ByteArrayClassLoaderTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(((ClassLoader) (classLoader))));
        InputStream inputStream = classLoader.getResourceAsStream(((ByteArrayClassLoaderTest.Foo.class.getName().replace('.', '/')) + (ByteArrayClassLoaderTest.CLASS_FILE)));
        try {
            MatcherAssert.assertThat(inputStream, (expectedResourceLookup ? CoreMatchers.notNullValue(InputStream.class) : CoreMatchers.nullValue(InputStream.class)));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Test
    public void testResourceLookupBeforeLoading() throws Exception {
        MatcherAssert.assertThat(classLoader.getResource(((ByteArrayClassLoaderTest.Foo.class.getName().replace('.', '/')) + (ByteArrayClassLoaderTest.CLASS_FILE))), (expectedResourceLookup ? CoreMatchers.notNullValue(URL.class) : CoreMatchers.nullValue(URL.class)));
    }

    @Test
    public void testResourceLookupAfterLoading() throws Exception {
        MatcherAssert.assertThat(classLoader.loadClass(ByteArrayClassLoaderTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(((ClassLoader) (classLoader))));
        MatcherAssert.assertThat(classLoader.getResource(((ByteArrayClassLoaderTest.Foo.class.getName().replace('.', '/')) + (ByteArrayClassLoaderTest.CLASS_FILE))), (expectedResourceLookup ? CoreMatchers.notNullValue(URL.class) : CoreMatchers.nullValue(URL.class)));
    }

    @Test
    public void testResourcesLookupBeforeLoading() throws Exception {
        Enumeration<URL> enumeration = classLoader.getResources(((ByteArrayClassLoaderTest.Foo.class.getName().replace('.', '/')) + (ByteArrayClassLoaderTest.CLASS_FILE)));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(expectedResourceLookup));
        if (expectedResourceLookup) {
            MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.notNullValue(URL.class));
            MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(false));
        }
    }

    @Test
    public void testResourcesLookupAfterLoading() throws Exception {
        MatcherAssert.assertThat(classLoader.loadClass(ByteArrayClassLoaderTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(((ClassLoader) (classLoader))));
        Enumeration<URL> enumeration = classLoader.getResources(((ByteArrayClassLoaderTest.Foo.class.getName().replace('.', '/')) + (ByteArrayClassLoaderTest.CLASS_FILE)));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(expectedResourceLookup));
        if (expectedResourceLookup) {
            MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.notNullValue(URL.class));
            MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(false));
        }
    }

    @Test
    public void testResourceLookupWithPrefixBeforeLoading() throws Exception {
        MatcherAssert.assertThat(classLoader.getResource((("/" + (ByteArrayClassLoaderTest.Foo.class.getName().replace('.', '/'))) + (ByteArrayClassLoaderTest.CLASS_FILE))), (expectedResourceLookup ? CoreMatchers.notNullValue(URL.class) : CoreMatchers.nullValue(URL.class)));
    }

    @Test
    public void testResourceLookupWithPrefixAfterLoading() throws Exception {
        MatcherAssert.assertThat(classLoader.loadClass(ByteArrayClassLoaderTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(((ClassLoader) (classLoader))));
        MatcherAssert.assertThat(classLoader.getResource((("/" + (ByteArrayClassLoaderTest.Foo.class.getName().replace('.', '/'))) + (ByteArrayClassLoaderTest.CLASS_FILE))), (expectedResourceLookup ? CoreMatchers.notNullValue(URL.class) : CoreMatchers.nullValue(URL.class)));
    }

    @Test
    public void testResourcesLookupWithPrefixBeforeLoading() throws Exception {
        Enumeration<URL> enumeration = classLoader.getResources((("/" + (ByteArrayClassLoaderTest.Foo.class.getName().replace('.', '/'))) + (ByteArrayClassLoaderTest.CLASS_FILE)));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(expectedResourceLookup));
        if (expectedResourceLookup) {
            MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.notNullValue(URL.class));
            MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(false));
        }
    }

    @Test
    public void testResourcesLookupWithPrefixAfterLoading() throws Exception {
        MatcherAssert.assertThat(classLoader.loadClass(ByteArrayClassLoaderTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(((ClassLoader) (classLoader))));
        Enumeration<URL> enumeration = classLoader.getResources((("/" + (ByteArrayClassLoaderTest.Foo.class.getName().replace('.', '/'))) + (ByteArrayClassLoaderTest.CLASS_FILE)));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(expectedResourceLookup));
        if (expectedResourceLookup) {
            MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.notNullValue(URL.class));
            MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(false));
        }
    }

    @Test(expected = ClassNotFoundException.class)
    public void testNotFoundException() throws Exception {
        // Note: Will throw a class format error instead targeting not found exception targeting loader attempts.
        classLoader.loadClass(ByteArrayClassLoaderTest.BAR);
    }

    @Test
    public void testPackage() throws Exception {
        MatcherAssert.assertThat(classLoader.loadClass(ByteArrayClassLoaderTest.Foo.class.getName()).getPackage().getName(), CoreMatchers.is(ByteArrayClassLoaderTest.Foo.class.getPackage().getName()));
        MatcherAssert.assertThat(classLoader.loadClass(ByteArrayClassLoaderTest.Foo.class.getName()).getPackage(), CoreMatchers.not(ByteArrayClassLoaderTest.Foo.class.getPackage()));
    }

    @Test
    public void testInjection() throws Exception {
        MatcherAssert.assertThat(classLoader.defineClass(ByteArrayClassLoaderTest.Bar.class.getName(), read(ByteArrayClassLoaderTest.Bar.class)).getName(), CoreMatchers.is(ByteArrayClassLoaderTest.Bar.class.getName()));
    }

    @Test
    public void testDuplicateInjection() throws Exception {
        Class<?> type = classLoader.defineClass(ByteArrayClassLoaderTest.Bar.class.getName(), read(ByteArrayClassLoaderTest.Bar.class));
        MatcherAssert.assertThat(classLoader.defineClass(ByteArrayClassLoaderTest.Bar.class.getName(), read(ByteArrayClassLoaderTest.Bar.class)), CoreMatchers.is(((Object) (type))));
    }

    @Test
    public void testPredefinedInjection() throws Exception {
        Class<?> type = classLoader.defineClass(ByteArrayClassLoaderTest.Foo.class.getName(), read(ByteArrayClassLoaderTest.Foo.class));
        MatcherAssert.assertThat(type, CoreMatchers.is(((Object) (classLoader.loadClass(ByteArrayClassLoaderTest.Foo.class.getName())))));
    }

    /* Note: Foo is know to the system class loader but not to the bootstrap class loader */
    private static class Foo {}

    /* Note: Bar is know to the system class loader but not to the bootstrap class loader */
    private static class Bar {}
}

