package hudson.model;


import ParametersAction.KEEP_UNDEFINED_PARAMETERS_SYSTEM_PROPERTY_NAME;
import ParametersAction.SAFE_PARAMETERS_SYSTEM_PROPERTY_NAME;
import hudson.Functions;
import hudson.Launcher;
import hudson.tasks.Builder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.recipes.LocalData;


public class ParametersAction2Test {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public LoggerRule logs = new LoggerRule().record("", Level.WARNING).capture(100);

    @Test
    @Issue("SECURITY-170")
    public void undefinedParameters() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(Arrays.asList(new ParameterDefinition[]{ new StringParameterDefinition("foo", "foo"), new StringParameterDefinition("bar", "bar") })));
        ParametersAction2Test.ParametersCheckBuilder b = new ParametersAction2Test.ParametersCheckBuilder(false);
        p.getBuildersList().add(b);
        p.save();
        j.assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new StringParameterValue("foo", "baz"), new StringParameterValue("undef", "undef"))));
    }

    @Test
    @Issue("SECURITY-170")
    public void undefinedParametersOverride() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(Arrays.asList(new ParameterDefinition[]{ new StringParameterDefinition("foo", "foo"), new StringParameterDefinition("bar", "bar") })));
        ParametersAction2Test.ParametersCheckBuilder b = new ParametersAction2Test.ParametersCheckBuilder(true);
        p.getBuildersList().add(b);
        p.save();
        try {
            System.setProperty(KEEP_UNDEFINED_PARAMETERS_SYSTEM_PROPERTY_NAME, "true");
            j.assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new StringParameterValue("foo", "baz"), new StringParameterValue("undef", "undef"))));
        } finally {
            System.clearProperty(KEEP_UNDEFINED_PARAMETERS_SYSTEM_PROPERTY_NAME);
        }
    }

    @Test
    @Issue("SECURITY-170")
    @LocalData
    public void backwardCompatibility() throws Exception {
        // Local data contains a parameterized job with two parameters (FOO and BAR) and one build
        // with pre-fix format (generated with 1.609.3) with FOO, BAR and UNDEF.
        FreeStyleProject p = j.jenkins.getItemByFullName((Functions.isWindows() ? "parameterized-windows" : "parameterized"), FreeStyleProject.class);
        FreeStyleBuild b1 = p.getBuildByNumber(1);
        ParametersAction pa = b1.getAction(ParametersAction.class);
        ParametersAction2Test.hasParameterWithName(pa, "FOO");
        ParametersAction2Test.hasParameterWithName(pa, "BAR");
        // legacy behaviour expected (UNDEF returned by getParameters())
        ParametersAction2Test.hasParameterWithName(pa, "UNDEF");
        // A new build should work as expected (undef is not published to env)
        ParametersAction2Test.ParametersCheckBuilder b = new ParametersAction2Test.ParametersCheckBuilder(false);
        p.getBuildersList().add(b);
        p.save();
        j.assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new StringParameterValue("foo", "baz"), new StringParameterValue("undef", "undef"))));
    }

    @Test
    @Issue("SECURITY-170")
    public void parametersDefinitionChange() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(Arrays.<ParameterDefinition>asList(new StringParameterDefinition("foo", "foo"), new StringParameterDefinition("bar", "bar"))));
        FreeStyleBuild build = j.assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new StringParameterValue("foo", "baz"), new StringParameterValue("bar", "bar"), new StringParameterValue("undef", "undef"))));
        Assert.assertTrue("undef parameter is not listed in getParameters", (!(ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "undef"))));
        p.removeProperty(ParametersDefinitionProperty.class);
        p.addProperty(new ParametersDefinitionProperty(Arrays.<ParameterDefinition>asList(new StringParameterDefinition("foo", "foo"), new StringParameterDefinition("bar", "bar"), new StringParameterDefinition("undef", "undef"))));
        // undef is still not listed even after being added to the job parameters definition
        Assert.assertTrue("undef parameter is not listed in getParameters", (!(ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "undef"))));
        // remove bar and undef from parameters definition
        p.removeProperty(ParametersDefinitionProperty.class);
        p.addProperty(new ParametersDefinitionProperty(Arrays.<ParameterDefinition>asList(new StringParameterDefinition("foo", "foo"))));
        Assert.assertTrue("the build still have 2 parameters", ((build.getAction(ParametersAction.class).getParameters().size()) == 2));
        p.removeProperty(ParametersDefinitionProperty.class);
        Assert.assertTrue("the build still have 2 parameters", ((build.getAction(ParametersAction.class).getParameters().size()) == 2));
    }

    @Test
    @Issue("SECURITY-170")
    public void whitelistedParameter() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(Arrays.<ParameterDefinition>asList(new StringParameterDefinition("foo", "foo"), new StringParameterDefinition("bar", "bar"))));
        try {
            System.setProperty(SAFE_PARAMETERS_SYSTEM_PROPERTY_NAME, "whitelisted1,whitelisted2");
            FreeStyleBuild build = j.assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new StringParameterValue("foo", "baz"), new StringParameterValue("bar", "bar"), new StringParameterValue("whitelisted1", "x"), new StringParameterValue("whitelisted2", "y"))));
            Assert.assertTrue("whitelisted1 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted1"));
            Assert.assertTrue("whitelisted2 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted2"));
        } finally {
            System.clearProperty(SAFE_PARAMETERS_SYSTEM_PROPERTY_NAME);
        }
    }

    @Test
    @Issue("SECURITY-170")
    public void whitelistedParameterByOverride() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        String name = p.getFullName();
        p.addProperty(new ParametersDefinitionProperty(Arrays.<ParameterDefinition>asList(new StringParameterDefinition("foo", "foo"), new StringParameterDefinition("bar", "bar"))));
        try {
            ParametersAction action = new ParametersAction(Arrays.<ParameterValue>asList(new StringParameterValue("foo", "baz"), new StringParameterValue("bar", "bar"), new StringParameterValue("whitelisted1", "x"), new StringParameterValue("whitelisted2", "y"), new StringParameterValue("whitelisted3", "y")), Arrays.asList("whitelisted1", "whitelisted2"));
            FreeStyleBuild build = j.assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserIdCause(), action));
            Assert.assertTrue("whitelisted1 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted1"));
            Assert.assertTrue("whitelisted2 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted2"));
            Assert.assertFalse("whitelisted3 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted3"));
            p = null;
            build = null;
            j.jenkins.reload();
            // Test again after reload
            p = j.jenkins.getItemByFullName(name, FreeStyleProject.class);
            build = p.getLastBuild();
            Assert.assertTrue("whitelisted1 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted1"));
            Assert.assertTrue("whitelisted2 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted2"));
            Assert.assertFalse("whitelisted3 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted3"));
        } finally {
            System.clearProperty(SAFE_PARAMETERS_SYSTEM_PROPERTY_NAME);
        }
    }

    @Test
    @Issue("SECURITY-170")
    public void whitelistedParameterSameAfterChange() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        String name = p.getFullName();
        p.addProperty(new ParametersDefinitionProperty(Arrays.<ParameterDefinition>asList(new StringParameterDefinition("foo", "foo"), new StringParameterDefinition("bar", "bar"))));
        try {
            System.setProperty(SAFE_PARAMETERS_SYSTEM_PROPERTY_NAME, "whitelisted1,whitelisted2");
            FreeStyleBuild build = j.assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new StringParameterValue("foo", "baz"), new StringParameterValue("bar", "bar"), new StringParameterValue("whitelisted1", "x"), new StringParameterValue("whitelisted2", "y"), new StringParameterValue("whitelisted3", "z"), new StringParameterValue("whitelisted4", "w"))));
            Assert.assertTrue("whitelisted1 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted1"));
            Assert.assertTrue("whitelisted2 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted2"));
            Assert.assertFalse("whitelisted3 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted3"));
            Assert.assertFalse("whitelisted4 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted4"));
            System.setProperty(SAFE_PARAMETERS_SYSTEM_PROPERTY_NAME, "whitelisted3,whitelisted4");
            p = null;
            build = null;
            j.jenkins.reload();
            p = j.jenkins.getItemByFullName(name, FreeStyleProject.class);
            build = p.getLastBuild();
            Assert.assertTrue("whitelisted1 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted1"));
            Assert.assertTrue("whitelisted2 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted2"));
            Assert.assertFalse("whitelisted3 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted3"));
            Assert.assertFalse("whitelisted4 parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "whitelisted4"));
        } finally {
            System.clearProperty(SAFE_PARAMETERS_SYSTEM_PROPERTY_NAME);
        }
    }

    @Test
    @Issue("SECURITY-170")
    public void nonParameterizedJob() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        FreeStyleBuild build = j.assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new StringParameterValue("foo", "baz"), new StringParameterValue("bar", "bar"))));
        Assert.assertTrue("foo parameter is not listed in getParameters", (!(ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "foo"))));
        Assert.assertTrue("bar parameter is not listed in getParameters", (!(ParametersAction2Test.hasParameterWithName(build.getAction(ParametersAction.class), "bar"))));
    }

    @Test
    @Issue("SECURITY-170")
    public void nonParameterizedJobButWhitelisted() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        try {
            System.setProperty(SAFE_PARAMETERS_SYSTEM_PROPERTY_NAME, "foo,bar");
            FreeStyleBuild build2 = j.assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new StringParameterValue("foo", "baz"), new StringParameterValue("bar", "bar"))));
            Assert.assertTrue("foo parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build2.getAction(ParametersAction.class), "foo"));
            Assert.assertTrue("bar parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(build2.getAction(ParametersAction.class), "bar"));
        } finally {
            System.clearProperty(SAFE_PARAMETERS_SYSTEM_PROPERTY_NAME);
        }
    }

    @Test
    @Issue("JENKINS-45472")
    public void ensureNoListReuse() throws Exception {
        FreeStyleProject p1 = j.createFreeStyleProject();
        p1.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("foo", "")));
        FreeStyleProject p2 = j.createFreeStyleProject();
        p2.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("foo", "")));
        List<ParameterValue> params = new ArrayList<>();
        params.add(new StringParameterValue("foo", "for p1"));
        p1.scheduleBuild2(1, new ParametersAction(params));
        params.clear();
        params.add(new StringParameterValue("foo", "for p2"));
        p2.scheduleBuild2(0, new ParametersAction(params));
        j.waitUntilNoActivity();
        Assert.assertEquals(1, p1.getLastBuild().getAction(ParametersAction.class).getParameters().size());
        Assert.assertEquals(1, p2.getLastBuild().getAction(ParametersAction.class).getParameters().size());
        Assert.assertEquals(p1.getLastBuild().getAction(ParametersAction.class).getParameter("foo").getValue(), "for p1");
        Assert.assertEquals(p2.getLastBuild().getAction(ParametersAction.class).getParameter("foo").getValue(), "for p2");
    }

    @Issue("JENKINS-49573")
    @Test
    public void noInnerClasses() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("key", "sensible-default")));
        FreeStyleBuild b = j.assertBuildStatusSuccess(p.scheduleBuild2(0, new ParametersAction(new StringParameterValue("key", "value"))));
        Assert.assertThat(asString(), CoreMatchers.not(CoreMatchers.containsString("sensible-default")));
        Assert.assertEquals(Collections.emptyList(), logs.getMessages());
    }

    public static class ParametersCheckBuilder extends Builder {
        private final boolean expectLegacyBehavior;

        public ParametersCheckBuilder(boolean expectLegacyBehavior) {
            this.expectLegacyBehavior = expectLegacyBehavior;
        }

        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
            ParametersAction pa = build.getAction(ParametersAction.class);
            Assert.assertEquals("foo value expected changed", "baz", pa.getParameter("foo").getValue());
            if (expectLegacyBehavior) {
                Assert.assertTrue("undef parameter is listed in getParameters", ParametersAction2Test.hasParameterWithName(pa.getParameters(), "undef"));
                Assert.assertTrue("undef parameter is listed in iterator", ParametersAction2Test.hasParameterWithName(pa, "undef"));
                Assert.assertTrue("undef in environment", build.getEnvironment(listener).keySet().contains("undef"));
                Assert.assertTrue("UNDEF in environment", build.getEnvironment(listener).keySet().contains("UNDEF"));
            } else {
                Assert.assertFalse("undef parameter is not listed in getParameters", ParametersAction2Test.hasParameterWithName(pa.getParameters(), "undef"));
                Assert.assertFalse("undef parameter is not listed in iterator", ParametersAction2Test.hasParameterWithName(pa, "undef"));
                Assert.assertFalse("undef not in environment", build.getEnvironment(listener).keySet().contains("undef"));
                Assert.assertFalse("UNDEF not in environment", build.getEnvironment(listener).keySet().contains("UNDEF"));
            }
            Assert.assertTrue("undef parameter is listed in getAllParameters", ParametersAction2Test.hasParameterWithName(pa.getAllParameters(), "undef"));
            Assert.assertEquals("undef parameter direct access expected to work", "undef", pa.getParameter("undef").getValue());
            return true;
        }
    }
}

