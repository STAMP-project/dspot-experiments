package net.bytebuddy.build.gradle;


import groovy.lang.Closure;
import java.io.File;
import java.util.Collections;
import net.bytebuddy.build.EntryPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static net.bytebuddy.build.EntryPoint.Default.REBASE;


public class ByteBuddyExtensionTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Project project;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private Closure<?> closure;

    @Mock
    private Task task;

    @Test
    public void testLiveInitializer() throws Exception {
        ByteBuddyExtension byteBuddyExtension = new ByteBuddyExtension(project);
        byteBuddyExtension.setFailOnLiveInitializer(false);
        MatcherAssert.assertThat(byteBuddyExtension.isFailOnLiveInitializer(), CoreMatchers.is(false));
    }

    @Test
    public void testLiveInitializerDefault() throws Exception {
        MatcherAssert.assertThat(isFailOnLiveInitializer(), CoreMatchers.is(true));
    }

    @Test
    public void testSuffix() throws Exception {
        Mockito.when(methodDescription.getName()).thenReturn(ByteBuddyExtensionTest.BAR);
        ByteBuddyExtension byteBuddyExtension = new ByteBuddyExtension(project);
        byteBuddyExtension.setSuffix(ByteBuddyExtensionTest.FOO);
        MatcherAssert.assertThat(byteBuddyExtension.getMethodNameTransformer().transform(methodDescription), CoreMatchers.endsWith(ByteBuddyExtensionTest.FOO));
    }

    @Test
    public void testSuffixEmpty() throws Exception {
        Mockito.when(methodDescription.getName()).thenReturn(ByteBuddyExtensionTest.BAR);
        ByteBuddyExtension byteBuddyExtension = new ByteBuddyExtension(project);
        byteBuddyExtension.setSuffix("");
        MatcherAssert.assertThat(byteBuddyExtension.getMethodNameTransformer().transform(methodDescription), CoreMatchers.not(ByteBuddyExtensionTest.BAR));
    }

    @Test
    public void testSuffixDefault() throws Exception {
        Mockito.when(methodDescription.getName()).thenReturn(ByteBuddyExtensionTest.BAR);
        MatcherAssert.assertThat(getMethodNameTransformer().transform(methodDescription), CoreMatchers.not(ByteBuddyExtensionTest.BAR));
    }

    @Test
    public void testTransformation() throws Exception {
        Mockito.when(project.configure(ArgumentMatchers.any(Transformation.class), ArgumentMatchers.eq(closure))).then(new Answer<Transformation>() {
            public Transformation answer(InvocationOnMock invocationOnMock) throws Throwable {
                return invocationOnMock.getArgument(0);
            }
        });
        ByteBuddyExtension byteBuddyExtension = new ByteBuddyExtension(project);
        byteBuddyExtension.transformation(closure);
        MatcherAssert.assertThat(byteBuddyExtension.getTransformations().size(), CoreMatchers.is(1));
    }

    @Test
    public void testInitialization() throws Exception {
        Mockito.when(project.configure(ArgumentMatchers.any(Initialization.class), ArgumentMatchers.eq(closure))).then(new Answer<Initialization>() {
            public Initialization answer(InvocationOnMock invocationOnMock) throws Throwable {
                return invocationOnMock.getArgument(0);
            }
        });
        ByteBuddyExtension byteBuddyExtension = new ByteBuddyExtension(project);
        byteBuddyExtension.initialization(closure);
        MatcherAssert.assertThat(byteBuddyExtension.getInitialization(), CoreMatchers.notNullValue(Initialization.class));
    }

    @Test
    public void testInitializationDefault() throws Exception {
        MatcherAssert.assertThat(getInitialization().getEntryPoint(Mockito.mock(ClassLoaderResolver.class), Mockito.mock(File.class), Collections.<File>emptySet()), CoreMatchers.is(((EntryPoint) (REBASE))));
    }

    @Test(expected = GradleException.class)
    public void testInitializationDuplicate() throws Exception {
        Mockito.when(project.configure(ArgumentMatchers.any(Initialization.class), ArgumentMatchers.eq(closure))).then(new Answer<Initialization>() {
            public Initialization answer(InvocationOnMock invocationOnMock) throws Throwable {
                return invocationOnMock.getArgument(0);
            }
        });
        ByteBuddyExtension byteBuddyExtension = new ByteBuddyExtension(project);
        byteBuddyExtension.initialization(closure);
        byteBuddyExtension.initialization(closure);
    }

    @Test
    public void testTasks() throws Exception {
        MatcherAssert.assertThat(new ByteBuddyExtension(project).implies(task), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(task);
    }

    @Test
    public void testTaskExplicitIncluded() throws Exception {
        ByteBuddyExtension byteBuddyExtension = new ByteBuddyExtension(project);
        byteBuddyExtension.setTasks(Collections.singleton(ByteBuddyExtensionTest.FOO));
        Mockito.when(task.getName()).thenReturn(ByteBuddyExtensionTest.FOO);
        MatcherAssert.assertThat(byteBuddyExtension.implies(task), CoreMatchers.is(true));
    }

    @Test
    public void testTaskExplicitExcluded() throws Exception {
        ByteBuddyExtension byteBuddyExtension = new ByteBuddyExtension(project);
        byteBuddyExtension.setTasks(Collections.singleton(ByteBuddyExtensionTest.FOO));
        Mockito.when(task.getName()).thenReturn(ByteBuddyExtensionTest.BAR);
        MatcherAssert.assertThat(byteBuddyExtension.implies(task), CoreMatchers.is(false));
    }
}

