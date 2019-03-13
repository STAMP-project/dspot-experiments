package hudson.model;


import hudson.EnvVars;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class EnvironmentContributingActionTest {
    class OverrideRun extends InvisibleAction implements EnvironmentContributingAction {
        private boolean wasCalled = false;

        @Override
        public void buildEnvironment(Run<?, ?> run, EnvVars env) {
            wasCalled = true;
        }

        boolean wasNewMethodCalled() {
            return wasCalled;
        }
    }

    class OverrideAbstractBuild extends InvisibleAction implements EnvironmentContributingAction {
        private boolean wasCalled = false;

        @Override
        @SuppressWarnings("deprecation")
        public void buildEnvVars(AbstractBuild<?, ?> abstractBuild, EnvVars envVars) {
            wasCalled = true;
        }

        boolean wasDeprecatedMethodCalled() {
            return wasCalled;
        }
    }

    class OverrideBoth extends InvisibleAction implements EnvironmentContributingAction {
        private boolean wasCalledAstractBuild = false;

        private boolean wasCalledRun = false;

        @SuppressWarnings("deprecation")
        @Override
        public void buildEnvVars(AbstractBuild<?, ?> abstractBuild, EnvVars envVars) {
            wasCalledAstractBuild = true;
        }

        @Override
        public void buildEnvironment(Run<?, ?> run, EnvVars env) {
            wasCalledRun = true;
        }

        boolean wasDeprecatedMethodCalled() {
            return wasCalledAstractBuild;
        }

        boolean wasRunCalled() {
            return wasCalledRun;
        }
    }

    private final EnvVars envVars = Mockito.mock(EnvVars.class);

    @Test
    public void testOverrideRunMethodAndCallNewMethod() throws Exception {
        Run run = Mockito.mock(Run.class);
        Node node = Mockito.mock(Node.class);
        EnvironmentContributingActionTest.OverrideRun overrideRun = new EnvironmentContributingActionTest.OverrideRun();
        overrideRun.buildEnvironment(run, envVars);
        Assert.assertTrue(overrideRun.wasNewMethodCalled());
    }

    /**
     * If only non-deprecated method was overridden it would be executed even if someone would call deprecated method.
     *
     * @throws Exception
     * 		if happens.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testOverrideRunMethodAndCallDeprecatedMethod() throws Exception {
        AbstractBuild abstractBuild = Mockito.mock(AbstractBuild.class);
        Mockito.when(abstractBuild.getBuiltOn()).thenReturn(Mockito.mock(Node.class));
        EnvironmentContributingActionTest.OverrideRun overrideRun = new EnvironmentContributingActionTest.OverrideRun();
        overrideRun.buildEnvVars(abstractBuild, envVars);
        Assert.assertTrue(overrideRun.wasNewMethodCalled());
    }

    /**
     * {@link AbstractBuild} should work as before.
     *
     * @throws Exception
     * 		if happens.
     */
    @Test
    public void testOverrideAbstractBuildAndCallNewMethodWithAbstractBuild() throws Exception {
        AbstractBuild abstractBuild = Mockito.mock(AbstractBuild.class);
        EnvironmentContributingActionTest.OverrideAbstractBuild action = new EnvironmentContributingActionTest.OverrideAbstractBuild();
        action.buildEnvironment(abstractBuild, envVars);
        Assert.assertTrue(action.wasDeprecatedMethodCalled());
    }

    /**
     * {@link Run} should not execute method that was overridden for {@link AbstractBuild}.
     *
     * @throws Exception
     * 		if happens.
     */
    @Test
    public void testOverrideAbstractBuildAndCallNewMethodWithRun() throws Exception {
        Run run = Mockito.mock(Run.class);
        EnvironmentContributingActionTest.OverrideAbstractBuild action = new EnvironmentContributingActionTest.OverrideAbstractBuild();
        action.buildEnvironment(run, envVars);
        Assert.assertFalse(action.wasDeprecatedMethodCalled());
    }

    /**
     * If someone wants to use overridden deprecated method, it would still work.
     *
     * @throws Exception
     * 		if happens.
     */
    @Test
    public void testOverrideAbstractBuildAndCallDeprecatedMethod() throws Exception {
        AbstractBuild abstractBuild = Mockito.mock(AbstractBuild.class);
        EnvironmentContributingActionTest.OverrideAbstractBuild overrideRun = new EnvironmentContributingActionTest.OverrideAbstractBuild();
        overrideRun.buildEnvVars(abstractBuild, envVars);
        Assert.assertTrue(overrideRun.wasDeprecatedMethodCalled());
    }

    @Test
    public void testOverrideBothAndCallNewMethod() throws Exception {
        Run run = Mockito.mock(Run.class);
        EnvironmentContributingActionTest.OverrideBoth overrideRun = new EnvironmentContributingActionTest.OverrideBoth();
        overrideRun.buildEnvironment(run, envVars);
        Assert.assertTrue(overrideRun.wasRunCalled());
    }

    @Test
    public void testOverrideBothAndCallDeprecatedMethod() throws Exception {
        AbstractBuild abstractBuild = Mockito.mock(AbstractBuild.class);
        EnvironmentContributingActionTest.OverrideBoth overrideRun = new EnvironmentContributingActionTest.OverrideBoth();
        overrideRun.buildEnvVars(abstractBuild, envVars);
        Assert.assertTrue(overrideRun.wasDeprecatedMethodCalled());
    }
}

