package net.bytebuddy.dynamic.loading;


import java.io.InputStream;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import junit.framework.TestCase;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.OpenedClassReader;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;


@RunWith(Parameterized.class)
public class ByteArrayClassLoaderChildFirstTest {
    private static final String BAR = "bar";

    private static final String CLASS_FILE = ".class";

    private static final ProtectionDomain DEFAULT_PROTECTION_DOMAIN = null;

    private final ByteArrayClassLoader.PersistenceHandler persistenceHandler;

    private final boolean expectedResourceLookup;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    private ClassLoader classLoader;

    @Mock
    private PackageDefinitionStrategy packageDefinitionStrategy;

    public ByteArrayClassLoaderChildFirstTest(ByteArrayClassLoader.PersistenceHandler persistenceHandler, boolean expectedResourceLookup) {
        this.persistenceHandler = persistenceHandler;
        this.expectedResourceLookup = expectedResourceLookup;
    }

    @Test
    public void testLoading() throws Exception {
        Class<?> type = classLoader.loadClass(ByteArrayClassLoaderChildFirstTest.Foo.class.getName());
        MatcherAssert.assertThat(type.getClassLoader(), CoreMatchers.is(classLoader));
        TestCase.assertEquals(classLoader.loadClass(ByteArrayClassLoaderChildFirstTest.Foo.class.getName()), type);
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.<Class<?>>is(ByteArrayClassLoaderChildFirstTest.Foo.class)));
        MatcherAssert.assertThat(type.getPackage(), CoreMatchers.notNullValue(Package.class));
        // Due to change in API in Java 9 where package identity is no longer bound by hierarchy.
        MatcherAssert.assertThat(type.getPackage(), (ClassFileVersion.ofThisVm().isAtLeast(ClassFileVersion.JAVA_V9) ? CoreMatchers.not(CoreMatchers.is(ByteArrayClassLoaderChildFirstTest.Foo.class.getPackage())) : CoreMatchers.is(ByteArrayClassLoaderChildFirstTest.Foo.class.getPackage())));
    }

    @Test
    public void testResourceStreamLookupBeforeLoading() throws Exception {
        InputStream inputStream = classLoader.getResourceAsStream(((ByteArrayClassLoaderChildFirstTest.Foo.class.getName().replace('.', '/')) + (ByteArrayClassLoaderChildFirstTest.CLASS_FILE)));
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
        MatcherAssert.assertThat(classLoader.loadClass(ByteArrayClassLoaderChildFirstTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(classLoader));
        InputStream inputStream = classLoader.getResourceAsStream(((ByteArrayClassLoaderChildFirstTest.Foo.class.getName().replace('.', '/')) + (ByteArrayClassLoaderChildFirstTest.CLASS_FILE)));
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
        MatcherAssert.assertThat(classLoader.getResource(((ByteArrayClassLoaderChildFirstTest.Foo.class.getName().replace('.', '/')) + (ByteArrayClassLoaderChildFirstTest.CLASS_FILE))), (expectedResourceLookup ? CoreMatchers.notNullValue(URL.class) : CoreMatchers.nullValue(URL.class)));
    }

    @Test
    public void testResourceLookupAfterLoading() throws Exception {
        MatcherAssert.assertThat(classLoader.loadClass(ByteArrayClassLoaderChildFirstTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(classLoader.getResource(((ByteArrayClassLoaderChildFirstTest.Foo.class.getName().replace('.', '/')) + (ByteArrayClassLoaderChildFirstTest.CLASS_FILE))), (expectedResourceLookup ? CoreMatchers.notNullValue(URL.class) : CoreMatchers.nullValue(URL.class)));
    }

    @Test
    public void testResourcesLookupBeforeLoading() throws Exception {
        Enumeration<URL> enumeration = classLoader.getResources(((ByteArrayClassLoaderChildFirstTest.Foo.class.getName().replace('.', '/')) + (ByteArrayClassLoaderChildFirstTest.CLASS_FILE)));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(true));
        MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.notNullValue(URL.class));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(expectedResourceLookup));
        if (expectedResourceLookup) {
            MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.notNullValue(URL.class));
            MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(false));
        }
    }

    @Test
    public void testResourcesLookupAfterLoading() throws Exception {
        MatcherAssert.assertThat(classLoader.loadClass(ByteArrayClassLoaderChildFirstTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(classLoader));
        Enumeration<URL> enumeration = classLoader.getResources(((ByteArrayClassLoaderChildFirstTest.Foo.class.getName().replace('.', '/')) + (ByteArrayClassLoaderChildFirstTest.CLASS_FILE)));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(true));
        MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.notNullValue(URL.class));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(expectedResourceLookup));
        if (expectedResourceLookup) {
            MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.notNullValue(URL.class));
            MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(false));
        }
    }

    @Test
    public void testResourceLookupWithPrefixBeforeLoading() throws Exception {
        MatcherAssert.assertThat(classLoader.getResource((("/" + (ByteArrayClassLoaderChildFirstTest.Foo.class.getName().replace('.', '/'))) + (ByteArrayClassLoaderChildFirstTest.CLASS_FILE))), (expectedResourceLookup ? CoreMatchers.notNullValue(URL.class) : CoreMatchers.nullValue(URL.class)));
    }

    @Test
    public void testResourceLookupWithPrefixAfterLoading() throws Exception {
        MatcherAssert.assertThat(classLoader.loadClass(ByteArrayClassLoaderChildFirstTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(classLoader.getResource((("/" + (ByteArrayClassLoaderChildFirstTest.Foo.class.getName().replace('.', '/'))) + (ByteArrayClassLoaderChildFirstTest.CLASS_FILE))), (expectedResourceLookup ? CoreMatchers.notNullValue(URL.class) : CoreMatchers.nullValue(URL.class)));
    }

    @Test
    public void testResourcesLookupWithPrefixBeforeLoading() throws Exception {
        Enumeration<URL> enumeration = classLoader.getResources((("/" + (ByteArrayClassLoaderChildFirstTest.Foo.class.getName().replace('.', '/'))) + (ByteArrayClassLoaderChildFirstTest.CLASS_FILE)));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(expectedResourceLookup));
        if (expectedResourceLookup) {
            MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.notNullValue(URL.class));
            MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(false));
        }
    }

    @Test
    public void testResourcesLookupWithPrefixAfterLoading() throws Exception {
        MatcherAssert.assertThat(classLoader.loadClass(ByteArrayClassLoaderChildFirstTest.Foo.class.getName()).getClassLoader(), CoreMatchers.is(classLoader));
        Enumeration<URL> enumeration = classLoader.getResources((("/" + (ByteArrayClassLoaderChildFirstTest.Foo.class.getName().replace('.', '/'))) + (ByteArrayClassLoaderChildFirstTest.CLASS_FILE)));
        MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(expectedResourceLookup));
        if (expectedResourceLookup) {
            MatcherAssert.assertThat(enumeration.nextElement(), CoreMatchers.notNullValue(URL.class));
            MatcherAssert.assertThat(enumeration.hasMoreElements(), CoreMatchers.is(false));
        }
    }

    @Test(expected = ClassNotFoundException.class)
    public void testNotFoundException() throws Exception {
        // Note: Will throw a class format error instead targeting not found exception targeting loader attempts.
        classLoader.loadClass(ByteArrayClassLoaderChildFirstTest.BAR);
    }

    /* empty */
    public static class Foo {}

    /* empty */
    public static class Bar {}

    private static class RenamingWrapper implements AsmVisitorWrapper {
        private final String oldName;

        private final String newName;

        private RenamingWrapper(String oldName, String newName) {
            this.oldName = oldName;
            this.newName = newName;
        }

        public int mergeWriter(int flags) {
            return flags;
        }

        public int mergeReader(int flags) {
            return flags;
        }

        public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
            return new ClassRemapper(OpenedClassReader.ASM_API, classVisitor, new SimpleRemapper(oldName, newName)) {};
        }
    }
}

