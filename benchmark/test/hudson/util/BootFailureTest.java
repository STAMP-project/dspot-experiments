package hudson.util;


import WebAppMain.FileAndDescription;
import hudson.WebAppMain;
import hudson.model.Hudson;
import hudson.model.listeners.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.HudsonHomeLoader;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.WebApp;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class BootFailureTest {
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    static boolean makeBootFail = true;

    static WebAppMain wa;

    static class CustomRule extends JenkinsRule {
        @Override
        public void before() throws Throwable {
            env = new org.jvnet.hudson.test.TestEnvironment(testDescription);
            env.pin();
            // don't let Jenkins start automatically
        }

        @Override
        public Hudson newHudson() throws Exception {
            ServletContext ws = createWebServer();
            BootFailureTest.wa = new WebAppMain() {
                @Override
                public FileAndDescription getHomeDir(ServletContextEvent event) {
                    try {
                        return new WebAppMain.FileAndDescription(homeLoader.allocate(), "test");
                    } catch (Exception x) {
                        throw new AssertionError(x);
                    }
                }
            };
            BootFailureTest.wa.contextInitialized(new ServletContextEvent(ws));
            BootFailureTest.wa.joinInit();
            Object a = WebApp.get(ws).getApp();
            if (a instanceof Hudson) {
                return ((Hudson) (a));
            }
            return null;// didn't boot

        }
    }

    @Rule
    public BootFailureTest.CustomRule j = new BootFailureTest.CustomRule();

    public static class SeriousError extends Error {}

    @TestExtension("runBootFailureScript")
    public static class InduceBootFailure extends ItemListener {
        @Override
        public void onLoaded() {
            if (BootFailureTest.makeBootFail)
                throw new BootFailureTest.SeriousError();

        }
    }

    @Test
    public void runBootFailureScript() throws Exception {
        final File home = tmpDir.newFolder();
        j.with(new HudsonHomeLoader() {
            @Override
            public File allocate() throws Exception {
                return home;
            }
        });
        // creates a script
        FileUtils.write(new File(home, "boot-failure.groovy"), "hudson.util.BootFailureTest.problem = exception");
        File d = new File(home, "boot-failure.groovy.d");
        d.mkdirs();
        FileUtils.write(new File(d, "1.groovy"), "hudson.util.BootFailureTest.runRecord << '1'");
        FileUtils.write(new File(d, "2.groovy"), "hudson.util.BootFailureTest.runRecord << '2'");
        // first failed boot
        BootFailureTest.makeBootFail = true;
        Assert.assertNull(j.newHudson());
        Assert.assertEquals(1, BootFailureTest.bootFailures(home));
        // second failed boot
        BootFailureTest.problem = null;
        BootFailureTest.runRecord = new ArrayList<String>();
        Assert.assertNull(j.newHudson());
        Assert.assertEquals(2, BootFailureTest.bootFailures(home));
        Assert.assertEquals(Arrays.asList("1", "2"), BootFailureTest.runRecord);
        // make sure the script has actually run
        Assert.assertEquals(BootFailureTest.SeriousError.class, BootFailureTest.problem.getCause().getClass());
        // if it boots well, the failure record should be gone
        BootFailureTest.makeBootFail = false;
        Assert.assertNotNull(j.newHudson());
        Assert.assertFalse(BootFailure.getBootFailureFile(home).exists());
    }

    @Issue("JENKINS-24696")
    @Test
    public void interruptedStartup() throws Exception {
        final File home = tmpDir.newFolder();
        with(new HudsonHomeLoader() {
            @Override
            public File allocate() throws Exception {
                return home;
            }
        });
        File d = new File(home, "boot-failure.groovy.d");
        d.mkdirs();
        FileUtils.write(new File(d, "1.groovy"), "hudson.util.BootFailureTest.runRecord << '1'");
        j.newHudson();
        Assert.assertEquals(Collections.singletonList("1"), BootFailureTest.runRecord);
    }

    @TestExtension("interruptedStartup")
    public static class PauseBoot extends ItemListener {
        @Override
        public void onLoaded() {
            BootFailureTest.wa.contextDestroyed(null);
        }
    }

    // to be set by the script
    public static Exception problem;

    public static List<String> runRecord = new ArrayList<String>();
}

