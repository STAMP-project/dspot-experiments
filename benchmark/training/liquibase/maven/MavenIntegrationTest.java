package liquibase.maven;


import org.junit.Test;


/**
 * Maven integration test. Run an update executing maven as if it was ran by the user
 *
 * @author lujop
 */
public class MavenIntegrationTest {
    private static final String URL = "jdbc:hsqldb:file:target/test-classes/maven/liquibase;shutdown=true";

    @Test
    public void nothing() {
        // tests fail when not running a maven based build. need to figure out how to determine that
    }
}

