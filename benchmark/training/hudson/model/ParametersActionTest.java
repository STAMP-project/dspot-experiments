package hudson.model;


import hudson.EnvVars;
import hudson.model.queue.SubTask;
import hudson.tasks.BuildWrapper;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.Issue;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class ParametersActionTest {
    private ParametersAction baseParamsAB;

    private StringParameterValue baseA;

    @Test
    public void mergeShouldOverrideParameters() {
        StringParameterValue overrideB = new StringParameterValue("b", "override-b");
        ParametersAction extraParams = new ParametersAction(overrideB);
        ParametersAction params = baseParamsAB.merge(extraParams);
        StringParameterValue a = ((StringParameterValue) (params.getParameter("a")));
        StringParameterValue b = ((StringParameterValue) (params.getParameter("b")));
        Assert.assertEquals(baseA, a);
        Assert.assertEquals(overrideB, b);
    }

    @Test
    public void mergeShouldCombineDisparateParameters() {
        StringParameterValue overrideB = new StringParameterValue("b", "override-b");
        ParametersAction extraParams = new ParametersAction(overrideB);
        ParametersAction params = baseParamsAB.merge(extraParams);
        StringParameterValue a = ((StringParameterValue) (params.getParameter("a")));
        StringParameterValue b = ((StringParameterValue) (params.getParameter("b")));
        Assert.assertEquals(baseA, a);
        Assert.assertEquals(overrideB, b);
    }

    @Test
    public void mergeShouldHandleEmptyOverrides() {
        ParametersAction params = baseParamsAB.merge(new ParametersAction());
        StringParameterValue a = ((StringParameterValue) (params.getParameter("a")));
        Assert.assertEquals(baseA, a);
    }

    @Test
    public void mergeShouldHandleNullOverrides() {
        ParametersAction params = baseParamsAB.merge(null);
        StringParameterValue a = ((StringParameterValue) (params.getParameter("a")));
        Assert.assertEquals(baseA, a);
    }

    @Test
    public void mergeShouldReturnNewInstanceWithOverride() {
        StringParameterValue overrideA = new StringParameterValue("a", "override-a");
        ParametersAction overrideParams = new ParametersAction(overrideA);
        ParametersAction params = baseParamsAB.merge(overrideParams);
        Assert.assertNotSame(baseParamsAB, params);
    }

    @Test
    public void createUpdatedShouldReturnNewInstanceWithNullOverride() {
        ParametersAction params = baseParamsAB.createUpdated(null);
        Assert.assertNotSame(baseParamsAB, params);
    }

    @Test
    @Issue("JENKINS-15094")
    public void checkNullParameterValues() {
        SubTask subtask = mock(SubTask.class);
        Build build = mock(Build.class);
        // Prepare parameters Action
        StringParameterValue A = new StringParameterValue("A", "foo");
        StringParameterValue B = new StringParameterValue("B", "bar");
        ParametersAction parametersAction = new ParametersAction(A, null, B);
        ParametersAction parametersAction2 = new ParametersAction(A, null);
        // Non existent parameter
        Assert.assertNull(parametersAction.getParameter("C"));
        Assert.assertNull(parametersAction.getAssignedLabel(subtask));
        // Interaction with build
        EnvVars vars = new EnvVars();
        parametersAction.buildEnvironment(build, vars);
        Assert.assertEquals(2, vars.size());
        parametersAction.createVariableResolver(build);
        LinkedList<BuildWrapper> wrappers = new LinkedList<BuildWrapper>();
        parametersAction.createBuildWrappers(build, wrappers);
        Assert.assertEquals(0, wrappers.size());
        // Merges and overrides
        Assert.assertEquals(3, parametersAction.createUpdated(parametersAction2.getParameters()).getParameters().size());
        Assert.assertEquals(3, parametersAction.merge(parametersAction2).getParameters().size());
    }
}

