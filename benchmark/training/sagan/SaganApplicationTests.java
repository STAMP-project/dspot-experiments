package sagan;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.env.Environment;


public class SaganApplicationTests {
    @Test
    public void unknownProfileSpecified() {
        activateProfiles("bogus");
        runApp();
        Assert.assertThat(runApp().getEnvironment().acceptsProfiles(STANDALONE), CoreMatchers.is(true));
        Assert.assertThat(runApp().getEnvironment().acceptsProfiles("bogus"), CoreMatchers.is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void bothStagingAndProductionSpecified() {
        activateProfiles(STAGING, PRODUCTION);
        runApp();
    }

    @Test
    public void stagingSpecified() {
        activateProfiles(STAGING);
        Assert.assertThat(runApp().getEnvironment().acceptsProfiles(CLOUDFOUNDRY), CoreMatchers.is(true));
    }

    @Test
    public void productionSpecified() {
        activateProfiles(PRODUCTION);
        Assert.assertThat(runApp().getEnvironment().acceptsProfiles(CLOUDFOUNDRY), CoreMatchers.is(true));
    }

    @Test
    public void noProfileSpecified() {
        // activateProfiles(...);
        Environment env = runApp().getEnvironment();
        Assert.assertThat(env.acceptsProfiles(env.getDefaultProfiles()), CoreMatchers.is(false));
        Assert.assertThat(env.acceptsProfiles(CLOUDFOUNDRY), CoreMatchers.is(false));
        Assert.assertThat(env.acceptsProfiles(STANDALONE), CoreMatchers.is(true));
    }
}

