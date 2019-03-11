package hudson.cli;


import Jenkins.ADMINISTER;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;


public class CLIEnvVarTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File home;

    private File jar;

    @Test
    public void testSOptionWithoutJENKINS_URL() throws Exception {
        Assert.assertEquals(0, launch("java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", r.getURL().toString(), "who-am-i"));
    }

    @Test
    public void testWithoutSOptionAndWithoutJENKINS_URL() throws Exception {
        Assert.assertNotEquals(0, launch("java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "who-am-i"));
    }

    @Test
    public void testJENKINS_URLWithoutSOption() throws Exception {
        // Valid URL
        Map<String, String> envars = new HashMap<>();
        envars.put("JENKINS_URL", r.getURL().toString());
        Assert.assertEquals(0, launch(envars, "java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "who-am-i"));
        // Invalid URL
        envars = new HashMap<>();
        envars.put("JENKINS_URL", "http://invalid-url");
        Assert.assertNotEquals(0, launch(envars, "java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "who-am-i"));
    }

    @Test
    public void testSOptionOverridesJENKINS_URL() throws Exception {
        Map<String, String> envars = new HashMap<>();
        envars.put("JENKINS_URL", "http://invalid-url");
        Assert.assertEquals(0, launch(envars, "java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", r.getURL().toString(), "who-am-i"));
    }

    @Test
    public void testAuthOptionWithoutEnvVars() throws Exception {
        String token = getToken();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Assert.assertEquals(0, launch(Collections.emptyMap(), baos, null, "java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", r.getURL().toString(), "-auth", String.format("%s:%s", "admin", token), "who-am-i"));
            Assert.assertThat(baos.toString(), Matchers.containsString("Authenticated as: admin"));
        }
    }

    @Test
    public void testWithoutEnvVarsAndWithoutAuthOption() throws Exception {
        r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
        r.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to("admin"));
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Assert.assertEquals(0, launch(Collections.emptyMap(), baos, null, "java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", r.getURL().toString(), "who-am-i"));
            Assert.assertThat(baos.toString(), Matchers.containsString("Authenticated as: anonymous"));
        }
    }

    @Test
    public void testEnvVarsWithoutAuthOption() throws Exception {
        String token = getToken();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Map<String, String> envars = new HashMap<>();
            envars.put("JENKINS_USER_ID", "admin");
            envars.put("JENKINS_API_TOKEN", token);
            Assert.assertEquals(0, launch(envars, baos, null, "java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", r.getURL().toString(), "who-am-i"));
            Assert.assertThat(baos.toString(), Matchers.containsString("Authenticated as: admin"));
        }
    }

    @Test
    public void testOnlyOneEnvVar() throws Exception {
        String token = getToken();
        // only JENKINS_USER_ID
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Map<String, String> envars = new HashMap<>();
            envars.put("JENKINS_USER_ID", "admin");
            Assert.assertNotEquals(0, launch(envars, "java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", r.getURL().toString(), "who-am-i"));
        }
        // only JENKINS_API_TOKEN
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Map<String, String> envars = new HashMap<>();
            envars.put("JENKINS_API_TOKEN", token);
            Assert.assertNotEquals(0, launch(envars, "java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", r.getURL().toString(), "who-am-i"));
        }
    }

    @Test
    public void testAuthOptionOverridesEnvVars() throws Exception {
        String token = getToken();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Map<String, String> envars = new HashMap<>();
            envars.put("JENKINS_USER_ID", "other-user");
            envars.put("JENKINS_API_TOKEN", "other-user");
            Assert.assertEquals(0, launch(envars, baos, null, "java", ("-Duser.home=" + (home)), "-jar", jar.getAbsolutePath(), "-s", r.getURL().toString(), "-auth", String.format("%s:%s", "admin", token), "who-am-i"));
            Assert.assertThat(baos.toString(), Matchers.containsString("Authenticated as: admin"));
        }
    }
}

