package net.bytebuddy.build.gradle;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import net.bytebuddy.build.EntryPoint;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.test.ArgumentPlugin;
import net.bytebuddy.test.IllegalEntryPoint;
import net.bytebuddy.test.IllegalPlugin;
import net.bytebuddy.test.IllegalTransformEntryPoint;
import net.bytebuddy.test.IllegalTransformPlugin;
import net.bytebuddy.test.LiveInitializerPlugin;
import net.bytebuddy.test.SimpleEntryPoint;
import net.bytebuddy.test.SimplePlugin;
import net.bytebuddy.test.utility.MockitoRule;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.Convention;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.build.EntryPoint.Default.REBASE;


public class TransformationActionTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String TEMP = ".tmp";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Project project;

    @Mock
    private Convention convention;

    @Mock
    private Logger logger;

    @Mock
    private ByteBuddyExtension byteBuddyExtension;

    @Mock
    private AbstractCompile parent;

    @Mock
    private Task task;

    @Mock
    private Transformation transformation;

    @Mock
    private Initialization initialization;

    @Mock
    private FileCollection fileCollection;

    private File target;

    private TransformationAction transformationAction;

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleTransformation() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        files.addAll(addClass("foo.Qux"));
        try {
            Mockito.when(transformation.getPlugin()).thenReturn(SimplePlugin.class.getName());
            Mockito.when(initialization.getEntryPoint(ArgumentMatchers.any(ClassLoaderResolver.class), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Iterable.class))).thenReturn(REBASE);
            transformationAction.execute(task);
            ClassLoader classLoader = new URLClassLoader(new URL[]{ target.toURI().toURL() });
            assertMethod(classLoader.loadClass("foo.Bar"), TransformationActionTest.FOO, TransformationActionTest.QUX);
            assertMethod(classLoader.loadClass("foo.Bar"), TransformationActionTest.BAR, TransformationActionTest.BAR);
            assertMethod(classLoader.loadClass("foo.Qux"), TransformationActionTest.FOO, TransformationActionTest.FOO);
            assertMethod(classLoader.loadClass("foo.Qux"), TransformationActionTest.BAR, TransformationActionTest.BAR);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(target, TransformationActionTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleTransformationWithArgument() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        files.addAll(addClass("foo.Qux"));
        try {
            Mockito.when(transformation.getPlugin()).thenReturn(ArgumentPlugin.class.getName());
            Mockito.when(transformation.makeArgumentResolvers()).thenReturn(Collections.singletonList(Factory.of(int.class, 42)));
            Mockito.when(initialization.getEntryPoint(ArgumentMatchers.any(ClassLoaderResolver.class), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Iterable.class))).thenReturn(REBASE);
            transformationAction.execute(task);
            ClassLoader classLoader = new URLClassLoader(new URL[]{ target.toURI().toURL() });
            assertMethod(classLoader.loadClass("foo.Bar"), TransformationActionTest.FOO, "42");
            assertMethod(classLoader.loadClass("foo.Bar"), TransformationActionTest.BAR, TransformationActionTest.BAR);
            assertMethod(classLoader.loadClass("foo.Qux"), TransformationActionTest.FOO, TransformationActionTest.FOO);
            assertMethod(classLoader.loadClass("foo.Qux"), TransformationActionTest.BAR, TransformationActionTest.BAR);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(target, TransformationActionTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLiveInitializer() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        try {
            Mockito.when(transformation.getPlugin()).thenReturn(LiveInitializerPlugin.class.getName());
            Mockito.when(initialization.getEntryPoint(ArgumentMatchers.any(ClassLoaderResolver.class), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Iterable.class))).thenReturn(REBASE);
            transformationAction.execute(task);
            ClassLoader classLoader = new URLClassLoader(new URL[]{ target.toURI().toURL() });
            try {
                assertMethod(classLoader.loadClass("foo.Bar"), TransformationActionTest.FOO, TransformationActionTest.QUX);
                TestCase.fail();
            } catch (InvocationTargetException exception) {
                MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(NullPointerException.class));
            }
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(target, TransformationActionTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLiveInitializerAllowed() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        try {
            Mockito.when(transformation.getPlugin()).thenReturn(LiveInitializerPlugin.class.getName());
            Mockito.when(byteBuddyExtension.isFailOnLiveInitializer()).thenReturn(false);
            Mockito.when(initialization.getEntryPoint(ArgumentMatchers.any(ClassLoaderResolver.class), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Iterable.class))).thenReturn(REBASE);
            transformationAction.execute(task);
            ClassLoader classLoader = new URLClassLoader(new URL[]{ target.toURI().toURL() });
            try {
                assertMethod(classLoader.loadClass("foo.Bar"), TransformationActionTest.FOO, TransformationActionTest.QUX);
                TestCase.fail();
            } catch (InvocationTargetException exception) {
                MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(NullPointerException.class));
            }
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(target, TransformationActionTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test(expected = GradleException.class)
    @SuppressWarnings("unchecked")
    public void testIllegalTransformer() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        try {
            Mockito.when(transformation.getPlugin()).thenReturn(IllegalTransformPlugin.class.getName());
            Mockito.when(initialization.getEntryPoint(ArgumentMatchers.any(ClassLoaderResolver.class), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Iterable.class))).thenReturn(REBASE);
            transformationAction.execute(task);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(target, TransformationActionTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test(expected = GradleException.class)
    @SuppressWarnings("unchecked")
    public void testIllegalTransformation() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        try {
            Mockito.when(transformation.getPlugin()).thenReturn(IllegalPlugin.class.getName());
            Mockito.when(initialization.getEntryPoint(ArgumentMatchers.any(ClassLoaderResolver.class), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Iterable.class))).thenReturn(REBASE);
            transformationAction.execute(task);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(target, TransformationActionTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleEntry() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        files.addAll(addClass("foo.Qux"));
        try {
            Mockito.when(transformation.getPlugin()).thenReturn(SimplePlugin.class.getName());
            Mockito.when(initialization.getEntryPoint(ArgumentMatchers.any(ClassLoaderResolver.class), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Iterable.class))).thenReturn(new SimpleEntryPoint());
            transformationAction.execute(task);
            ClassLoader classLoader = new URLClassLoader(new URL[]{ target.toURI().toURL() });
            assertMethod(classLoader.loadClass("foo.Bar"), TransformationActionTest.FOO, TransformationActionTest.QUX);
            assertMethod(classLoader.loadClass("foo.Bar"), TransformationActionTest.BAR, TransformationActionTest.BAR);
            assertMethod(classLoader.loadClass("foo.Qux"), TransformationActionTest.FOO, TransformationActionTest.FOO);
            assertMethod(classLoader.loadClass("foo.Qux"), TransformationActionTest.BAR, TransformationActionTest.BAR);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(target, TransformationActionTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test(expected = GradleException.class)
    @SuppressWarnings("unchecked")
    public void testIllegalByteBuddy() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        files.addAll(addClass("foo.Qux"));
        try {
            Mockito.when(transformation.getPlugin()).thenReturn(SimplePlugin.class.getName());
            Mockito.when(initialization.getEntryPoint(ArgumentMatchers.any(ClassLoaderResolver.class), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Iterable.class))).thenReturn(new IllegalEntryPoint());
            transformationAction.execute(task);
            ClassLoader classLoader = new URLClassLoader(new URL[]{ target.toURI().toURL() });
            assertMethod(classLoader.loadClass("foo.Bar"), TransformationActionTest.FOO, TransformationActionTest.QUX);
            assertMethod(classLoader.loadClass("foo.Bar"), TransformationActionTest.BAR, TransformationActionTest.BAR);
            assertMethod(classLoader.loadClass("foo.Qux"), TransformationActionTest.FOO, TransformationActionTest.FOO);
            assertMethod(classLoader.loadClass("foo.Qux"), TransformationActionTest.BAR, TransformationActionTest.BAR);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(target, TransformationActionTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test(expected = GradleException.class)
    @SuppressWarnings("unchecked")
    public void testIllegalTransform() throws Exception {
        Set<File> files = new HashSet<File>();
        files.addAll(addClass("foo.Bar"));
        files.addAll(addClass("foo.Qux"));
        try {
            Mockito.when(transformation.getPlugin()).thenReturn(SimplePlugin.class.getName());
            Mockito.when(initialization.getEntryPoint(ArgumentMatchers.any(ClassLoaderResolver.class), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Iterable.class))).thenReturn(new IllegalTransformEntryPoint());
            transformationAction.execute(task);
            ClassLoader classLoader = new URLClassLoader(new URL[]{ target.toURI().toURL() });
            assertMethod(classLoader.loadClass("foo.Bar"), TransformationActionTest.FOO, TransformationActionTest.QUX);
            assertMethod(classLoader.loadClass("foo.Bar"), TransformationActionTest.BAR, TransformationActionTest.BAR);
            assertMethod(classLoader.loadClass("foo.Qux"), TransformationActionTest.FOO, TransformationActionTest.FOO);
            assertMethod(classLoader.loadClass("foo.Qux"), TransformationActionTest.BAR, TransformationActionTest.BAR);
        } finally {
            for (File file : files) {
                MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
            }
            MatcherAssert.assertThat(new File(target, TransformationActionTest.FOO).delete(), CoreMatchers.is(true));
        }
    }

    @Test(expected = GradleException.class)
    public void testNoDirectory() throws Exception {
        Mockito.when(parent.getDestinationDir()).thenReturn(Mockito.mock(File.class));
        transformationAction.execute(task);
    }
}

