package org.testcontainers.containers;


import java.util.Collections;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.testcontainers.utility.MountableFile;


/**
 * Tests of functionality special to the Neo4jContainer.
 *
 * @author Michael J. Simons
 */
public class Neo4jContainerTest {
    // See org.testcontainers.utility.LicenseAcceptance#ACCEPTANCE_FILE_NAME
    private static final String ACCEPTANCE_FILE_LOCATION = "/container-license-acceptance.txt";

    @Test
    public void shouldDisableAuthentication() {
        try (Neo4jContainer neo4jContainer = new Neo4jContainer().withoutAuthentication()) {
            neo4jContainer.start();
            try (Driver driver = Neo4jContainerTest.getDriver(neo4jContainer);Session session = driver.session()) {
                long one = session.run("RETURN 1", Collections.emptyMap()).next().get(0).asLong();
                assertThat(one).isEqualTo(1L);
            }
        }
    }

    @Test
    public void shouldCopyDatabase() {
        try (Neo4jContainer neo4jContainer = new Neo4jContainer().withDatabase(MountableFile.forClasspathResource("/test-graph.db"))) {
            neo4jContainer.start();
            try (Driver driver = Neo4jContainerTest.getDriver(neo4jContainer);Session session = driver.session()) {
                StatementResult result = session.run("MATCH (t:Thing) RETURN t");
                assertThat(result.list().stream().map(( r) -> r.get("t").get("name").asString())).containsExactlyInAnyOrder("Thing", "Thing 2", "Thing 3", "A box");
            }
        }
    }

    @Test
    public void shouldCopyPlugins() {
        try (Neo4jContainer neo4jContainer = new Neo4jContainer().withPlugins(MountableFile.forClasspathResource("/custom-plugins"))) {
            neo4jContainer.start();
            try (Driver driver = Neo4jContainerTest.getDriver(neo4jContainer);Session session = driver.session()) {
                Neo4jContainerTest.assertThatCustomPluginWasCopied(session);
            }
        }
    }

    @Test
    public void shouldCopyPlugin() {
        try (Neo4jContainer neo4jContainer = new Neo4jContainer().withPlugins(MountableFile.forClasspathResource("/custom-plugins/hello-world.jar"))) {
            neo4jContainer.start();
            try (Driver driver = Neo4jContainerTest.getDriver(neo4jContainer);Session session = driver.session()) {
                Neo4jContainerTest.assertThatCustomPluginWasCopied(session);
            }
        }
    }

    @Test
    public void shouldCheckEnterpriseLicense() {
        assumeThat(Neo4jContainerTest.class.getResource(Neo4jContainerTest.ACCEPTANCE_FILE_LOCATION)).isNull();
        String expectedImageName = "neo4j:3.5.0-enterprise";
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> new Neo4jContainer().withEnterpriseEdition()).withMessageContaining((("The image " + expectedImageName) + " requires you to accept a license agreement."));
    }

    @Test
    public void shouldRunEnterprise() {
        assumeThat(Neo4jContainerTest.class.getResource(Neo4jContainerTest.ACCEPTANCE_FILE_LOCATION)).isNotNull();
        try (Neo4jContainer neo4jContainer = new Neo4jContainer().withEnterpriseEdition().withAdminPassword("Picard123")) {
            neo4jContainer.start();
            try (Driver driver = Neo4jContainerTest.getDriver(neo4jContainer);Session session = driver.session()) {
                String edition = session.run("CALL dbms.components() YIELD edition RETURN edition", Collections.emptyMap()).next().get(0).asString();
                assertThat(edition).isEqualTo("enterprise");
            }
        }
    }

    @Test
    public void shouldAddConfigToEnvironment() {
        Neo4jContainer neo4jContainer = new Neo4jContainer().withNeo4jConfig("dbms.security.procedures.unrestricted", "apoc.*,algo.*").withNeo4jConfig("dbms.tx_log.rotation.size", "42M");
        assertThat(neo4jContainer.getEnvMap()).containsEntry("NEO4J_dbms_security_procedures_unrestricted", "apoc.*,algo.*");
        assertThat(neo4jContainer.getEnvMap()).containsEntry("NEO4J_dbms_tx__log_rotation_size", "42M");
    }
}

