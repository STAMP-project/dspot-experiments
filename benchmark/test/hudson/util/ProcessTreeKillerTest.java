package hudson.util;


import com.google.common.collect.ImmutableMap;
import hudson.EnvVars;
import hudson.Functions;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Slave;
import hudson.tasks.Maven;
import hudson.tasks.Shell;
import hudson.util.ProcessTreeRemoting.IOSProcess;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.ExtractResourceSCM;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;
import org.jvnet.hudson.test.TestExtension;

import static ProcessTree.DEFAULT;
import static ProcessTree.enabled;


public class ProcessTreeKillerTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    private Process process;

    @Test
    public void manualAbortProcess() throws Exception {
        enabled = true;
        FreeStyleProject project = j.createFreeStyleProject();
        // this contains a maven project with a single test that sleeps 5s.
        project.setScm(new ExtractResourceSCM(getClass().getResource("ProcessTreeKiller-test-project.jar")));
        project.getBuildersList().add(new Maven("install", "maven"));
        // build the project, wait until tests are running, then cancel.
        project.scheduleBuild2(0).waitForStart();
        FreeStyleBuild b = project.getLastBuild();
        b.doStop();
        Thread.sleep(1000);
        // will fail (at least on windows) if test process is still running
        b.getWorkspace().deleteRecursive();
    }

    @Test
    @Issue("JENKINS-22641")
    public void processProperlyKilledUnix() throws Exception {
        enabled = true;
        Assume.assumeFalse("This test does not involve windows", Functions.isWindows());
        FreeStyleProject sleepProject = j.createFreeStyleProject();
        FreeStyleProject processJob = j.createFreeStyleProject();
        sleepProject.getBuildersList().add(new Shell("nohup sleep 100000 &"));
        j.assertBuildStatusSuccess(sleepProject.scheduleBuild2(0).get());
        processJob.getBuildersList().add(new Shell("ps -ef | grep sleep"));
        j.assertLogNotContains("sleep 100000", processJob.scheduleBuild2(0).get());
    }

    @Test
    public void doNotKillProcessWithCookie() throws Exception {
        Assume.assumeFalse("This test does not involve windows", Functions.isWindows());
        enabled = true;
        ProcessTreeKillerTest.SpawnBuilder spawner = new ProcessTreeKillerTest.SpawnBuilder();
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(spawner);
        try {
            j.buildAndAssertSuccess(p);
            Assert.assertTrue("Process should be running", spawner.proc.isAlive());
        } finally {
            spawner.proc.kill();
            Assert.assertTrue("Process should be dead", (!(spawner.proc.isAlive())));
        }
    }

    public static final class SpawnBuilder extends TestBuilder {
        private Proc proc;

        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
            EnvVars envvars = build.getEnvironment(listener);
            envvars.addLine("BUILD_ID=dontKillMe");
            proc = launcher.launch().envs(envvars).cmds("nohup", "sleep", "100000").start();
            return true;
        }
    }

    @Test
    @Issue("JENKINS-9104")
    public void considersKillingVetos() throws Exception {
        // on some platforms where we fail to list any processes, this test will
        // just not work
        Assume.assumeTrue(((ProcessTree.get()) != (DEFAULT)));
        // kick off a process we (shouldn't) kill
        ProcessBuilder pb = new ProcessBuilder();
        pb.environment().put("cookie", "testKeepDaemonsAlive");
        if ((File.pathSeparatorChar) == ';') {
            pb.command("cmd");
        } else {
            pb.command("sleep", "5m");
        }
        process = pb.start();
        ProcessTree processTree = ProcessTree.get();
        processTree.killAll(ImmutableMap.of("cookie", "testKeepDaemonsAlive"));
        try {
            process.exitValue();
            Assert.fail("Process should have been excluded from the killing");
        } catch (IllegalThreadStateException e) {
            // Means the process is still running
        }
    }

    @Test
    @Issue("JENKINS-9104")
    public void considersKillingVetosOnSlave() throws Exception {
        // on some platforms where we fail to list any processes, this test will
        // just not work
        Assume.assumeTrue(((ProcessTree.get()) != (DEFAULT)));
        // Define a process we (shouldn't) kill
        ProcessBuilder pb = new ProcessBuilder();
        pb.environment().put("cookie", "testKeepDaemonsAlive");
        if ((File.pathSeparatorChar) == ';') {
            pb.command("cmd");
        } else {
            pb.command("sleep", "5m");
        }
        // Create an agent so we can tell it to kill the process
        Slave s = j.createSlave();
        s.toComputer().connect(false).get();
        // Start the process
        process = pb.start();
        // Call killall (somewhat roundabout though) to (not) kill it
        StringWriter out = new StringWriter();
        s.createLauncher(new StreamTaskListener(out)).kill(ImmutableMap.of("cookie", "testKeepDaemonsAlive"));
        try {
            process.exitValue();
            Assert.fail("Process should have been excluded from the killing");
        } catch (IllegalThreadStateException e) {
            // Means the process is still running
        }
    }

    @TestExtension({ "considersKillingVetos", "considersKillingVetosOnSlave" })
    public static class VetoAllKilling extends ProcessKillingVeto {
        @Override
        public VetoCause vetoProcessKilling(IOSProcess p) {
            return new VetoCause("Peace on earth");
        }
    }
}

