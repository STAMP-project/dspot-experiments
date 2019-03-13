package net.bytebuddy.dynamic;


import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;


public class ClassFileLocatorForUrlTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final int VALUE = 42;

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private File file;

    // Avoid leak since class loader cannot be closed
    @Test
    @JavaVersionRule.Enforce(7)
    public void testSuccessfulLocation() throws Exception {
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file));
        try {
            JarEntry jarEntry = new JarEntry(((((ClassFileLocatorForUrlTest.FOO) + "/") + (ClassFileLocatorForUrlTest.BAR)) + ".class"));
            jarOutputStream.putNextEntry(jarEntry);
            jarOutputStream.write(ClassFileLocatorForUrlTest.VALUE);
            jarOutputStream.write(((ClassFileLocatorForUrlTest.VALUE) * 2));
            jarOutputStream.closeEntry();
        } finally {
            jarOutputStream.close();
        }
        URL url = file.toURI().toURL();
        ClassFileLocator classFileLocator = new ClassFileLocator.ForUrl(Collections.singleton(url));
        try {
            ClassFileLocator.Resolution resolution = classFileLocator.locate((((ClassFileLocatorForUrlTest.FOO) + ".") + (ClassFileLocatorForUrlTest.BAR)));
            MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
            MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(new byte[]{ ClassFileLocatorForUrlTest.VALUE, (ClassFileLocatorForUrlTest.VALUE) * 2 }));
        } finally {
            classFileLocator.close();
        }
    }

    // Avoid leak since class loader cannot be closed
    @Test
    @JavaVersionRule.Enforce(7)
    public void testJarFileClosable() throws Exception {
        URL url = new URL("http://localhost:123");
        Closeable classFileLocator = new ClassFileLocator.ForUrl(url);
        classFileLocator.close();
    }

    // Avoid leak since class loader cannot be closed
    @Test
    @JavaVersionRule.Enforce(7)
    public void testNonSuccessfulLocation() throws Exception {
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file));
        try {
            JarEntry jarEntry = new JarEntry("noop.class");
            jarOutputStream.putNextEntry(jarEntry);
            jarOutputStream.write(ClassFileLocatorForUrlTest.VALUE);
            jarOutputStream.closeEntry();
        } finally {
            jarOutputStream.close();
        }
        URL url = file.toURI().toURL();
        ClassFileLocator classFileLocator = new ClassFileLocator.ForUrl(url);
        try {
            ClassFileLocator.Resolution resolution = classFileLocator.locate((((ClassFileLocatorForUrlTest.FOO) + ".") + (ClassFileLocatorForUrlTest.BAR)));
            MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(false));
        } finally {
            classFileLocator.close();
        }
    }
}

