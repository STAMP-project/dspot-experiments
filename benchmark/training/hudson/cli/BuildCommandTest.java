/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.cli;


import CLICommandInvoker.Matcher;
import CLICommandInvoker.Result;
import hudson.Extension;
import hudson.Functions;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Executor;
import hudson.model.FileParameterDefinition;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.ParameterDefinition.ParameterDescriptor;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.Queue.QueueDecisionHandler;
import hudson.model.Queue.Task;
import hudson.model.SimpleParameterDefinition;
import hudson.model.StringParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.model.TopLevelItem;
import hudson.slaves.DumbSlave;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import hudson.util.OneShotEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.CaptureEnvironmentBuilder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SmokeTest;
import org.jvnet.hudson.test.TestBuilder;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.StaplerRequest;


/**
 * {@link BuildCommand} test.
 */
public class BuildCommandTest {
    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();

    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Just schedules a build and return.
     */
    @Test
    public void async() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        OneShotEvent started = new OneShotEvent();
        OneShotEvent completed = new OneShotEvent();
        p.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                started.signal();
                completed.block();
                return true;
            }
        });
        Assert.assertThat(new CLICommandInvoker(j, new BuildCommand()).invokeWithArgs(p.getName()), Matcher.succeeded());
        started.block();
        Assert.assertTrue(p.getBuildByNumber(1).isBuilding());
        completed.signal();
    }

    /**
     * Tests synchronous execution.
     */
    @Test
    @Category(SmokeTest.class)
    public void sync() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add((Functions.isWindows() ? new BatchFile("ping 127.0.0.1") : new Shell("sleep 3")));
        Assert.assertThat(new CLICommandInvoker(j, new BuildCommand()).invokeWithArgs("-s", p.getName()), Matcher.succeeded());
        Assert.assertFalse(p.getBuildByNumber(1).isBuilding());
    }

    /**
     * Tests synchronous execution with retried verbose output
     */
    @Test
    public void syncWOutputStreaming() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add((Functions.isWindows() ? new BatchFile("ping 127.0.0.1") : new Shell("sleep 3")));
        Assert.assertThat(new CLICommandInvoker(j, new BuildCommand()).invokeWithArgs("-s", "-v", "-r", "5", p.getName()), Matcher.succeeded());
        Assert.assertFalse(p.getBuildByNumber(1).isBuilding());
    }

    @Test
    public void parameters() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("key", null)));
        Assert.assertThat(new CLICommandInvoker(j, new BuildCommand()).invokeWithArgs("-s", "-p", "key=foobar", p.getName()), Matcher.succeeded());
        FreeStyleBuild b = j.assertBuildStatusSuccess(p.getBuildByNumber(1));
        Assert.assertEquals("foobar", b.getAction(ParametersAction.class).getParameter("key").getValue());
    }

    @Test
    public void defaultParameters() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("key", "default"), new StringParameterDefinition("key2", "default2")));
        Assert.assertThat(new CLICommandInvoker(j, new BuildCommand()).invokeWithArgs("-s", "-p", "key=foobar", p.getName()), Matcher.succeeded());
        FreeStyleBuild b = j.assertBuildStatusSuccess(p.getBuildByNumber(1));
        Assert.assertEquals("foobar", b.getAction(ParametersAction.class).getParameter("key").getValue());
        Assert.assertEquals("default2", b.getAction(ParametersAction.class).getParameter("key2").getValue());
    }

    // TODO randomly fails: Started test0 #1
    @Test
    public void consoleOutput() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        CLICommandInvoker.Result r = new CLICommandInvoker(j, new BuildCommand()).invokeWithArgs("-s", "-v", p.getName());
        Assert.assertThat(r, Matcher.succeeded());
        j.assertBuildStatusSuccess(p.getBuildByNumber(1));
        Assert.assertThat(r.stdout(), allOf(containsString("Started from command line by anonymous"), containsString("Finished: SUCCESS")));
    }

    // TODO randomly fails: Started test0 #1
    @Test
    public void consoleOutputWhenBuildSchedulingRefused() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        CLICommandInvoker.Result r = new CLICommandInvoker(j, new BuildCommand()).invokeWithArgs("-s", "-v", p.getName());
        Assert.assertThat(r, Matcher.failedWith(4));
        Assert.assertThat(r.stderr(), containsString(BuildCommand.BUILD_SCHEDULING_REFUSED));
    }

    // <=>
    @TestExtension("consoleOutputWhenBuildSchedulingRefused")
    public static class UnschedulingVetoer extends QueueDecisionHandler {
        @Override
        public boolean shouldSchedule(Task task, List<Action> actions) {
            return false;
        }
    }

    @Test
    public void refuseToBuildDisabledProject() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("the-project");
        project.disable();
        CLICommandInvoker invoker = new CLICommandInvoker(j, new BuildCommand());
        CLICommandInvoker.Result result = invoker.invokeWithArgs("the-project");
        Assert.assertThat(result, failedWith(4));
        Assert.assertThat(result.stderr(), containsString("ERROR: Cannot build the-project because it is disabled."));
        Assert.assertNull("Project should not be built", project.getBuildByNumber(1));
    }

    @Test
    public void refuseToBuildNewlyCopiedProject() throws Exception {
        FreeStyleProject original = j.createFreeStyleProject("original");
        FreeStyleProject newOne = ((FreeStyleProject) (j.jenkins.<TopLevelItem>copy(original, "new-one")));
        CLICommandInvoker invoker = new CLICommandInvoker(j, new BuildCommand());
        CLICommandInvoker.Result result = invoker.invokeWithArgs("new-one");
        Assert.assertThat(result, failedWith(4));
        Assert.assertThat(result.stderr(), containsString("ERROR: Cannot build new-one because its configuration has not been saved."));
        Assert.assertNull("Project should not be built", newOne.getBuildByNumber(1));
    }

    @Test
    public void correctlyParseMapValuesContainingEqualsSign() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("the-project");
        project.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("expr", null)));
        CLICommandInvoker invoker = new CLICommandInvoker(j, new BuildCommand());
        CLICommandInvoker.Result result = invoker.invokeWithArgs("the-project", "-p", "expr=a=b", "-s");
        Assert.assertThat(result, succeeded());
        Assert.assertEquals("a=b", project.getBuildByNumber(1).getBuildVariables().get("expr"));
    }

    @Issue("JENKINS-15094")
    @Test
    public void executorsAliveOnParameterWithNullDefaultValue() throws Exception {
        DumbSlave slave = j.createSlave();
        FreeStyleProject project = j.createFreeStyleProject("foo");
        project.setAssignedNode(slave);
        // Create test parameter with Null default value
        BuildCommandTest.NullDefaultValueParameterDefinition nullDefaultDefinition = new BuildCommandTest.NullDefaultValueParameterDefinition();
        ParametersDefinitionProperty pdp = new ParametersDefinitionProperty(new StringParameterDefinition("string", "defaultValue", "description"), nullDefaultDefinition);
        project.addProperty(pdp);
        CaptureEnvironmentBuilder builder = new CaptureEnvironmentBuilder();
        project.getBuildersList().add(builder);
        // Warmup
        j.buildAndAssertSuccess(project);
        for (Executor exec : slave.toComputer().getExecutors()) {
            Assert.assertTrue(("Executor has died before the test start: " + exec), exec.isActive());
        }
        // Create CLI & run command
        CLICommandInvoker invoker = new CLICommandInvoker(j, new BuildCommand());
        CLICommandInvoker.Result result = invoker.invokeWithArgs("foo", "-p", "string=value");
        Assert.assertThat(result, failedWith(2));
        Assert.assertThat(result.stderr(), containsString("ERROR: No default value for the parameter \'FOO\'."));
        Thread.sleep(5000);// Give the job 5 seconds to be submitted

        Assert.assertNull("Build should not be scheduled", j.jenkins.getQueue().getItem(project));
        Assert.assertNull("Build should not be scheduled", project.getBuildByNumber(2));
        // Check executors health after a timeout
        for (Executor exec : slave.toComputer().getExecutors()) {
            Assert.assertTrue(("Executor is dead: " + exec), exec.isActive());
        }
    }

    public static final class NullDefaultValueParameterDefinition extends SimpleParameterDefinition {
        /* package */
        NullDefaultValueParameterDefinition() {
            super("FOO", "Always null default value");
        }

        @Override
        public ParameterValue createValue(String value) {
            return new StringParameterValue("FOO", "BAR");
        }

        @Override
        public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
            return createValue("BAR");
        }

        @Override
        public ParameterValue getDefaultParameterValue() {
            return null;// Equals to super.getDefaultParameterValue();

        }

        @Extension
        public static class DescriptorImpl extends ParameterDescriptor {}
    }

    @Issue("JENKINS-41745")
    @Test
    public void fileParameter() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("myjob");
        p.addProperty(new ParametersDefinitionProperty(new FileParameterDefinition("file", null)));
        p.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                listener.getLogger().println(("Found in my workspace: " + (build.getWorkspace().child("file").readToString())));
                return true;
            }
        });
        Assert.assertThat(withStdin(new ByteArrayInputStream("uploaded content here".getBytes())).invokeWithArgs("-f", "-p", "file=", "myjob"), Matcher.succeeded());
        FreeStyleBuild b = p.getBuildByNumber(1);
        Assert.assertNotNull(b);
        j.assertLogContains("uploaded content here", b);
    }
}

