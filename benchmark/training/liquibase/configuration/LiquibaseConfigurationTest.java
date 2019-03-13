package liquibase.configuration;


import org.junit.Assert;
import org.junit.Test;


public class LiquibaseConfigurationTest {
    @Test
    public void getContext_defaultSetup() {
        LiquibaseConfiguration liquibaseConfiguration = LiquibaseConfiguration.getInstance();
        GlobalConfiguration globalConfiguration = liquibaseConfiguration.getConfiguration(GlobalConfiguration.class);
        Assert.assertNotNull(globalConfiguration);
        Assert.assertSame("Multiple calls to getConfiguration should return the same instance", globalConfiguration, liquibaseConfiguration.getConfiguration(GlobalConfiguration.class));
    }
}

