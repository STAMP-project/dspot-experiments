package net.bytebuddy.dynamic;


import java.io.File;
import java.io.FileOutputStream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ClassFileLocatorForFolderTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final int VALUE = 42;

    private File folder;

    @Test
    public void testSuccessfulLocation() throws Exception {
        File packageFolder = new File(folder, ClassFileLocatorForFolderTest.FOO);
        MatcherAssert.assertThat(packageFolder.mkdir(), CoreMatchers.is(true));
        File file = new File(packageFolder, ((ClassFileLocatorForFolderTest.BAR) + ".class"));
        MatcherAssert.assertThat(file.createNewFile(), CoreMatchers.is(true));
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            fileOutputStream.write(ClassFileLocatorForFolderTest.VALUE);
            fileOutputStream.write(((ClassFileLocatorForFolderTest.VALUE) * 2));
        } finally {
            fileOutputStream.close();
        }
        ClassFileLocator classFileLocator = new ClassFileLocator.ForFolder(folder);
        ClassFileLocator.Resolution resolution = classFileLocator.locate((((ClassFileLocatorForFolderTest.FOO) + ".") + (ClassFileLocatorForFolderTest.BAR)));
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(new byte[]{ ClassFileLocatorForFolderTest.VALUE, (ClassFileLocatorForFolderTest.VALUE) * 2 }));
        MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
        MatcherAssert.assertThat(packageFolder.delete(), CoreMatchers.is(true));
    }

    @Test
    public void testNonSuccessfulLocation() throws Exception {
        ClassFileLocator classFileLocator = new ClassFileLocator.ForFolder(folder);
        ClassFileLocator.Resolution resolution = classFileLocator.locate((((ClassFileLocatorForFolderTest.FOO) + ".") + (ClassFileLocatorForFolderTest.BAR)));
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testClose() throws Exception {
        new ClassFileLocator.ForFolder(folder).close();
    }
}

