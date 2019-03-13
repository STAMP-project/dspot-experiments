package net.bytebuddy.dynamic;


import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.ClassFileLocator.ForModuleFile.ofBootPath;


public class ClassFileLocatorForModuleFileTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final int VALUE = 42;

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private File file;

    @Test
    public void testSuccessfulLocation() throws Exception {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
        try {
            ZipEntry zipEntry = new ZipEntry((((("classes/" + (ClassFileLocatorForModuleFileTest.FOO)) + "/") + (ClassFileLocatorForModuleFileTest.BAR)) + ".class"));
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(ClassFileLocatorForModuleFileTest.VALUE);
            zipOutputStream.write(((ClassFileLocatorForModuleFileTest.VALUE) * 2));
            zipOutputStream.closeEntry();
        } finally {
            zipOutputStream.close();
        }
        ZipFile zipFile = new ZipFile(file);
        try {
            ClassFileLocator classFileLocator = new ClassFileLocator.ForModuleFile(zipFile);
            ClassFileLocator.Resolution resolution = classFileLocator.locate((((ClassFileLocatorForModuleFileTest.FOO) + ".") + (ClassFileLocatorForModuleFileTest.BAR)));
            MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
            MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(new byte[]{ ClassFileLocatorForModuleFileTest.VALUE, (ClassFileLocatorForModuleFileTest.VALUE) * 2 }));
        } finally {
            zipFile.close();
        }
    }

    @Test
    public void testZipFileClosable() throws Exception {
        ZipFile zipFile = Mockito.mock(ZipFile.class);
        Closeable classFileLocator = new ClassFileLocator.ForModuleFile(zipFile);
        classFileLocator.close();
        Mockito.verify(zipFile).close();
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testBootJar() throws Exception {
        ClassFileLocator classFileLocator = ofBootPath();
        try {
            MatcherAssert.assertThat(classFileLocator.locate(Object.class.getName()).isResolved(), CoreMatchers.is(true));
        } finally {
            classFileLocator.close();
        }
    }

    @Test
    public void testNonSuccessfulLocation() throws Exception {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
        try {
            ZipEntry zipEntry = new ZipEntry("noop.class");
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(ClassFileLocatorForModuleFileTest.VALUE);
            zipOutputStream.closeEntry();
        } finally {
            zipOutputStream.close();
        }
        ZipFile zipFile = new ZipFile(file);
        try {
            ClassFileLocator classFileLocator = new ClassFileLocator.ForModuleFile(zipFile);
            ClassFileLocator.Resolution resolution = classFileLocator.locate((((ClassFileLocatorForModuleFileTest.FOO) + ".") + (ClassFileLocatorForModuleFileTest.BAR)));
            MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(false));
        } finally {
            zipFile.close();
        }
    }

    @Test
    public void testClose() throws Exception {
        ZipFile zipFile = Mockito.mock(ZipFile.class);
        new ClassFileLocator.ForModuleFile(zipFile).close();
        Mockito.verify(zipFile).close();
        Mockito.verifyNoMoreInteractions(zipFile);
    }
}

