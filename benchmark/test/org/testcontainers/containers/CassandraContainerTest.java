package org.testcontainers.containers;


import com.datastax.driver.core.ResultSet;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.testcontainers.containers.wait.CassandraQueryWaitStrategy;


/**
 *
 *
 * @author Eugeny Karpov
 */
@Slf4j
public class CassandraContainerTest {
    private static final String TEST_CLUSTER_NAME_IN_CONF = "Test Cluster Integration Test";

    @Test
    public void testSimple() {
        try (CassandraContainer cassandraContainer = new CassandraContainer()) {
            cassandraContainer.start();
            ResultSet resultSet = performQuery(cassandraContainer, "SELECT release_version FROM system.local");
            Assert.assertTrue("Query was not applied", resultSet.wasApplied());
            Assert.assertNotNull("Result set has no release_version", resultSet.one().getString(0));
        }
    }

    @Test
    public void testSpecificVersion() {
        String cassandraVersion = "3.0.15";
        try (CassandraContainer cassandraContainer = new CassandraContainer(("cassandra:" + cassandraVersion))) {
            cassandraContainer.start();
            ResultSet resultSet = performQuery(cassandraContainer, "SELECT release_version FROM system.local");
            Assert.assertTrue("Query was not applied", resultSet.wasApplied());
            Assert.assertEquals("Cassandra has wrong version", cassandraVersion, resultSet.one().getString(0));
        }
    }

    @Test
    public void testConfigurationOverride() {
        try (CassandraContainer cassandraContainer = new CassandraContainer().withConfigurationOverride("cassandra-test-configuration-example")) {
            cassandraContainer.start();
            ResultSet resultSet = performQuery(cassandraContainer, "SELECT cluster_name FROM system.local");
            Assert.assertTrue("Query was not applied", resultSet.wasApplied());
            Assert.assertEquals("Cassandra configuration is not overridden", CassandraContainerTest.TEST_CLUSTER_NAME_IN_CONF, resultSet.one().getString(0));
        }
    }

    @Test(expected = ContainerLaunchException.class)
    public void testEmptyConfigurationOverride() {
        try (CassandraContainer cassandraContainer = new CassandraContainer().withConfigurationOverride("cassandra-empty-configuration")) {
            cassandraContainer.start();
        }
    }

    @Test
    public void testInitScript() {
        try (CassandraContainer cassandraContainer = new CassandraContainer().withInitScript("initial.cql")) {
            cassandraContainer.start();
            testInitScript(cassandraContainer);
        }
    }

    @Test
    public void testInitScriptWithLegacyCassandra() {
        try (CassandraContainer cassandraContainer = new CassandraContainer("cassandra:2.2.11").withInitScript("initial.cql")) {
            cassandraContainer.start();
            testInitScript(cassandraContainer);
        }
    }

    @Test
    public void testCassandraQueryWaitStrategy() {
        try (CassandraContainer cassandraContainer = new CassandraContainer().waitingFor(new CassandraQueryWaitStrategy())) {
            cassandraContainer.start();
            ResultSet resultSet = performQuery(cassandraContainer, "SELECT release_version FROM system.local");
            Assert.assertTrue("Query was not applied", resultSet.wasApplied());
        }
    }

    @Test
    public void testCassandraGetCluster() {
        try (CassandraContainer cassandraContainer = new CassandraContainer()) {
            cassandraContainer.start();
            ResultSet resultSet = performQuery(cassandraContainer.getCluster(), "SELECT release_version FROM system.local");
            Assert.assertTrue("Query was not applied", resultSet.wasApplied());
            Assert.assertNotNull("Result set has no release_version", resultSet.one().getString(0));
        }
    }
}

