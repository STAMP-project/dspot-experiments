package net.bytebuddy.build.maven;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import net.bytebuddy.test.utility.MockitoRule;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.graph.DependencyNode;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class ByteBuddyMojoTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String TEMP = "tmp";

    private static final String JAR = "jar";

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private RepositorySystem repositorySystem;

    @Mock
    private DependencyNode root;

    private File project;

    @Test
    public void testEmptyTransformation() throws Exception {
        execute("transform", "empty");
    }

    @Test
    public void testSimpleTransformation() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        files.addAll(addClass("foo.Qux"));
        try {
            execute("transform", "simple");
            ClassLoader classLoader = new URLClassLoader(new URL[]{ project.toURI().toURL() });
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.QUX);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.FOO);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(project, ByteBuddyMojoTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test
    public void testSimpleTransformationWithSuffix() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        files.addAll(addClass("foo.Qux"));
        try {
            execute("transform", "suffix");
            ClassLoader classLoader = new URLClassLoader(new URL[]{ project.toURI().toURL() });
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.QUX);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
            MatcherAssert.assertThat(classLoader.loadClass("foo.Bar").getDeclaredMethod((((ByteBuddyMojoTest.FOO) + "$") + (ByteBuddyMojoTest.QUX))), CoreMatchers.notNullValue(Method.class));
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.FOO);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(project, ByteBuddyMojoTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test
    public void testSimpleTransformationWithArgument() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        files.addAll(addClass("foo.Qux"));
        try {
            execute("transform", "argument");
            ClassLoader classLoader = new URLClassLoader(new URL[]{ project.toURI().toURL() });
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.FOO, "42");
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.FOO);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(project, ByteBuddyMojoTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test(expected = MojoExecutionException.class)
    public void testLiveInitializer() throws Exception {
        Set<File> files = new HashSet<File>(addClass("foo.Bar"));
        try {
            execute("transform", "live");
            ClassLoader classLoader = new URLClassLoader(new URL[]{ project.toURI().toURL() });
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.QUX);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(project, ByteBuddyMojoTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test
    public void testLiveInitializerAllowed() throws Exception {
        Set<File> files = new HashSet<File>(addClass("foo.Bar"));
        try {
            execute("transform", "live.allowed");
            ClassLoader classLoader = new URLClassLoader(new URL[]{ project.toURI().toURL() });
            try {
                ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.QUX);
                TestCase.fail();
            } catch (InvocationTargetException exception) {
                MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(NullPointerException.class));
            }
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(project, ByteBuddyMojoTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test(expected = MojoExecutionException.class)
    public void testIllegalTransformer() throws Exception {
        Set<File> files = new HashSet<File>(addClass("foo.Bar"));
        try {
            execute("transform", "illegal");
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(project, ByteBuddyMojoTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test(expected = MojoExecutionException.class)
    public void testIllegalTransformation() throws Exception {
        Set<File> files = new HashSet<File>(addClass("foo.Bar"));
        try {
            execute("transform", "illegal.apply");
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(project, ByteBuddyMojoTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test
    public void testTestTransformation() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        files.addAll(addClass("foo.Qux"));
        try {
            execute("transform-test", "simple");
            ClassLoader classLoader = new URLClassLoader(new URL[]{ project.toURI().toURL() });
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.QUX);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.FOO);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(project, ByteBuddyMojoTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test
    public void testSimpleEntry() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        files.addAll(addClass("foo.Qux"));
        try {
            execute("transform", "entry");
            ClassLoader classLoader = new URLClassLoader(new URL[]{ project.toURI().toURL() });
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.QUX);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.FOO);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(project, ByteBuddyMojoTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test(expected = MojoExecutionException.class)
    public void testIllegalByteBuddy() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        files.addAll(addClass("foo.Qux"));
        try {
            execute("transform", "entry.illegal");
            ClassLoader classLoader = new URLClassLoader(new URL[]{ project.toURI().toURL() });
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.QUX);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.FOO);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(project, ByteBuddyMojoTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test(expected = MojoExecutionException.class)
    public void testIllegalTransform() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        files.addAll(addClass("foo.Qux"));
        try {
            execute("transform", "entry.illegal.transform");
            ClassLoader classLoader = new URLClassLoader(new URL[]{ project.toURI().toURL() });
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.QUX);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Bar"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.FOO, ByteBuddyMojoTest.FOO);
            ByteBuddyMojoTest.assertMethod(classLoader.loadClass("foo.Qux"), ByteBuddyMojoTest.BAR, ByteBuddyMojoTest.BAR);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(project, ByteBuddyMojoTest.FOO).delete(), CoreMatchers.is(true));
        }
    }
}

