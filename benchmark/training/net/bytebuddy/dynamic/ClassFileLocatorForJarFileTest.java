package net.bytebuddy.dynamic;


import java.io.File;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;
import org.objectweb.asm.ClassVisitor;

import static net.bytebuddy.dynamic.ClassFileLocator.ForJarFile.ofClassPath;
import static net.bytebuddy.dynamic.ClassFileLocator.ForJarFile.ofRuntimeJar;


public class ClassFileLocatorForJarFileTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final int VALUE = 42;

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private File file;

    @Test
    public void testSuccessfulLocation() throws Exception {
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file));
        try {
            JarEntry jarEntry = new JarEntry(((((ClassFileLocatorForJarFileTest.FOO) + "/") + (ClassFileLocatorForJarFileTest.BAR)) + ".class"));
            jarOutputStream.putNextEntry(jarEntry);
            jarOutputStream.write(ClassFileLocatorForJarFileTest.VALUE);
            jarOutputStream.write(((ClassFileLocatorForJarFileTest.VALUE) * 2));
            jarOutputStream.closeEntry();
        } finally {
            jarOutputStream.close();
        }
        JarFile jarFile = new JarFile(file);
        try {
            ClassFileLocator classFileLocator = new ClassFileLocator.ForJarFile(jarFile);
            ClassFileLocator.Resolution resolution = classFileLocator.locate((((ClassFileLocatorForJarFileTest.FOO) + ".") + (ClassFileLocatorForJarFileTest.BAR)));
            MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
            MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(new byte[]{ ClassFileLocatorForJarFileTest.VALUE, (ClassFileLocatorForJarFileTest.VALUE) * 2 }));
        } finally {
            jarFile.close();
        }
    }

    @Test
    public void testClassPath() throws Exception {
        ClassFileLocator classFileLocator = ofClassPath();
        try {
            MatcherAssert.assertThat(classFileLocator.locate(ByteBuddy.class.getName()).isResolved(), CoreMatchers.is(true));// As file.

            MatcherAssert.assertThat(classFileLocator.locate(ClassVisitor.class.getName()).isResolved(), CoreMatchers.is(true));// On path.

        } finally {
            classFileLocator.close();
        }
    }

    @Test
    @JavaVersionRule.Enforce(atMost = 8)
    public void testRuntimeJar() throws Exception {
        ClassFileLocator classFileLocator = ofRuntimeJar();
        try {
            // java.lang.Object is not contained in the rt.jar for some JVMs.
            MatcherAssert.assertThat(classFileLocator.locate(Void.class.getName()).isResolved(), CoreMatchers.is(true));
        } finally {
            classFileLocator.close();
        }
    }

    @Test
    public void testNonSuccessfulLocation() throws Exception {
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file));
        try {
            JarEntry jarEntry = new JarEntry("noop.class");
            jarOutputStream.putNextEntry(jarEntry);
            jarOutputStream.write(ClassFileLocatorForJarFileTest.VALUE);
            jarOutputStream.closeEntry();
        } finally {
            jarOutputStream.close();
        }
        JarFile jarFile = new JarFile(file);
        try {
            ClassFileLocator classFileLocator = new ClassFileLocator.ForJarFile(jarFile);
            ClassFileLocator.Resolution resolution = classFileLocator.locate((((ClassFileLocatorForJarFileTest.FOO) + ".") + (ClassFileLocatorForJarFileTest.BAR)));
            MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(false));
        } finally {
            jarFile.close();
        }
    }

    @Test
    public void testClose() throws Exception {
        JarFile jarFile = Mockito.mock(JarFile.class);
        new ClassFileLocator.ForJarFile(jarFile).close();
        Mockito.verify(jarFile).close();
        Mockito.verifyNoMoreInteractions(jarFile);
    }
}

