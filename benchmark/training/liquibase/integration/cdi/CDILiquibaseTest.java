package liquibase.integration.cdi;


import org.junit.Test;


/**
 * Unit tests for CDILiquibaseTest
 *
 * @author Aaron Walker (http://github.com/aaronwalker)
 */
public class CDILiquibaseTest {
    @Test
    public void shouldntRunWhenShouldRunIsFalse() {
        System.setProperty("liquibase.shouldRun", "false");
        validateRunningState(false);
    }

    @Test
    public void shouldRunWhenShouldRunIsTrue() {
        System.setProperty("liquibase.shouldRun", "true");
        validateRunningState(true);
    }

    @Test
    public void shouldntRunWhenConfigShouldRunIsFalse() {
        System.setProperty("liquibase.config.shouldRun", "false");
        validateRunningState(false);
    }

    @Test
    public void shouldRunWhenConfigShouldRunIsTrue() {
        System.setProperty("liquibase.config.shouldRun", "true");
        validateRunningState(true);
    }
}

