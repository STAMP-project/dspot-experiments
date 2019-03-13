package net.bytebuddy.build.gradle;


import net.bytebuddy.test.utility.MockitoRule;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PostCompilationActionTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Project project;

    @Mock
    private Logger logger;

    @Mock
    private ByteBuddyExtension byteBuddyExtension;

    @Mock
    private AbstractCompile task;

    @Test
    public void testApplication() throws Exception {
        Mockito.when(byteBuddyExtension.implies(task)).thenReturn(true);
        new PostCompilationAction(project, byteBuddyExtension).execute(task);
        Mockito.verify(task).doLast(ArgumentMatchers.any(TransformationAction.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoApplication() throws Exception {
        Mockito.when(byteBuddyExtension.implies(task)).thenReturn(false);
        new PostCompilationAction(project, byteBuddyExtension).execute(task);
        Mockito.verify(task, Mockito.never()).doLast(ArgumentMatchers.any(Action.class));
    }
}

