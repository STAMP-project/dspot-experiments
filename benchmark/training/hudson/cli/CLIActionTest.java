package hudson.cli;


import Jenkins.ADMINISTER;
import Jenkins.READ;
import JenkinsRule.WebClient;
import hudson.Proc;
import hudson.model.AllView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import jenkins.security.apitoken.ApiTokenTestHelper;
import jenkins.util.FullDuplexHttpService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.recipes.PresetData;
import org.jvnet.hudson.test.recipes.PresetData.DataSet;


public class CLIActionTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Rule
    public LoggerRule logging = new LoggerRule();

    private ExecutorService pool;

    @Test
    @PresetData(DataSet.NO_ANONYMOUS_READACCESS)
    @Issue("SECURITY-192")
    public void serveCliActionToAnonymousUserWithoutPermissions() throws Exception {
        JenkinsRule.WebClient wc = j.createWebClient();
        // The behavior changed due to SECURITY-192. index page is no longer accessible to anonymous
        wc.assertFails("cli", HttpURLConnection.HTTP_FORBIDDEN);
    }

    @Test
    public void serveCliActionToAnonymousUserWithAnonymousUserWithPermissions() throws Exception {
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.goTo("cli");
    }

    @Issue({ "JENKINS-12543", "JENKINS-41745" })
    @Test
    public void authentication() throws Exception {
        ApiTokenTestHelper.enableLegacyBehavior();
        logging.record(PlainCLIProtocol.class, Level.FINE);
        File jar = tmp.newFile("jenkins-cli.jar");
        FileUtils.copyURLToFile(j.jenkins.getJnlpJars("jenkins-cli.jar").getURL(), jar);
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to(CLIActionTest.ADMIN));
        j.createFreeStyleProject("p");
        // CLICommand with @Argument:
        assertExitCode(6, false, jar, "get-job", "p");// SECURITY-754 requires Overall/Read for nearly all CLICommands.

        assertExitCode(0, true, jar, "get-job", "p");// but API tokens do work under HTTP protocol

        // @CLIMethod:
        assertExitCode(6, false, jar, "disable-job", "p");// AccessDeniedException from CLIRegisterer?

        assertExitCode(0, true, jar, "disable-job", "p");
        // If we have anonymous read access, then the situation is simpler.
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to(CLIActionTest.ADMIN).grant(READ, Item.READ).everywhere().toEveryone());
        assertExitCode(6, false, jar, "get-job", "p");// AccessDeniedException from AbstractItem.writeConfigDotXml

        assertExitCode(0, true, jar, "get-job", "p");// works with API tokens

        assertExitCode(6, false, jar, "disable-job", "p");// AccessDeniedException from AbstractProject.doDisable

        assertExitCode(0, true, jar, "disable-job", "p");
    }

    private static final String ADMIN = "admin@mycorp.com";

    @Issue("JENKINS-41745")
    @Test
    public void encodingAndLocale() throws Exception {
        File jar = tmp.newFile("jenkins-cli.jar");
        FileUtils.copyURLToFile(j.jenkins.getJnlpJars("jenkins-cli.jar").getURL(), jar);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Assert.assertEquals(0, launch().cmds("java", "-Dfile.encoding=ISO-8859-2", "-Duser.language=cs", "-Duser.country=CZ", "-jar", jar.getAbsolutePath(), "-s", /* just checking */
        j.getURL().toString().replaceFirst("/$", ""), "-noKeyAuth", "test-diagnostic").stdout(baos).stderr(System.err).join());
        Assert.assertEquals("encoding=ISO-8859-2 locale=cs_CZ", baos.toString().trim());
        // TODO test that stdout/stderr are in expected encoding (not true of -remoting mode!)
        // -ssh mode does not pass client locale or encoding
    }

    @Issue("JENKINS-41745")
    @Test
    public void interleavedStdio() throws Exception {
        logging.record(PlainCLIProtocol.class, Level.FINE).record(FullDuplexHttpService.class, Level.FINE);
        File jar = tmp.newFile("jenkins-cli.jar");
        FileUtils.copyURLToFile(j.jenkins.getJnlpJars("jenkins-cli.jar").getURL(), jar);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PipedInputStream pis = new PipedInputStream();
        PipedOutputStream pos = new PipedOutputStream(pis);
        PrintWriter pw = new PrintWriter(new TeeOutputStream(pos, System.err), true);
        Proc proc = launch().cmds("java", "-jar", jar.getAbsolutePath(), "-s", j.getURL().toString(), "-noKeyAuth", "groovysh").stdout(new TeeOutputStream(baos, System.out)).stderr(System.err).stdin(pis).start();
        while (!(baos.toString().contains("000"))) {
            // cannot just search for, say, "groovy:000> " since there are ANSI escapes there (cf. StringEscapeUtils.escapeJava)
            Thread.sleep(100);
        } 
        pw.println("11 * 11");
        while (!(baos.toString().contains("121"))) {
            // ditto not "===> 121"
            Thread.sleep(100);
        } 
        Thread.sleep(31000);// aggravate org.eclipse.jetty.io.IdleTimeout (cf. AbstractConnector._idleTimeout)

        pw.println("11 * 11 * 11");
        while (!(baos.toString().contains("1331"))) {
            Thread.sleep(100);
        } 
        pw.println(":q");
        Assert.assertEquals(0, proc.join());
    }

    @Issue("SECURITY-754")
    @Test
    public void noPreAuthOptionHandlerInfoLeak() throws Exception {
        File jar = tmp.newFile("jenkins-cli.jar");
        FileUtils.copyURLToFile(j.jenkins.getJnlpJars("jenkins-cli.jar").getURL(), jar);
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.addView(new AllView("v1"));
        j.jenkins.addNode(j.createSlave("n1", null, null));
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to(CLIActionTest.ADMIN));
        // No anonymous read access
        assertExitCode(6, false, jar, "get-view", "v1");
        assertExitCode(6, false, jar, "get-view", "v2");// Error code 3 before SECURITY-754

        assertExitCode(6, false, jar, "get-node", "n1");
        assertExitCode(6, false, jar, "get-node", "n2");// Error code 3 before SECURITY-754

        // Authenticated with no read access
        assertExitCode(6, false, jar, "-auth", "user:user", "get-view", "v1");
        assertExitCode(6, false, jar, "-auth", "user:user", "get-view", "v2");// Error code 3 before SECURITY-754

        assertExitCode(6, false, jar, "-auth", "user:user", "get-node", "n1");
        assertExitCode(6, false, jar, "-auth", "user:user", "get-node", "n2");// Error code 3 before SECURITY-754

        // Anonymous read access
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to(CLIActionTest.ADMIN).grant(READ, Item.READ).everywhere().toEveryone());
        assertExitCode(6, false, jar, "get-view", "v1");
        assertExitCode(6, false, jar, "get-view", "v2");// Error code 3 before SECURITY-754

    }

    @TestExtension("encodingAndLocale")
    public static class TestDiagnosticCommand extends CLICommand {
        @Override
        public String getShortDescription() {
            return "Print information about the command environment.";
        }

        @Override
        protected int run() throws Exception {
            stdout.println(((("encoding=" + (getClientCharset())) + " locale=") + (locale)));
            return 0;
        }
    }
}

