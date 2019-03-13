package com.baeldung.cassandra.java.client.repository;


import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KeyspaceRepositoryIntegrationTest {
    private KeyspaceRepository schemaRepository;

    private Session session;

    @Test
    public void whenCreatingAKeyspace_thenCreated() {
        String keyspaceName = "testBaeldungKeyspace";
        schemaRepository.createKeyspace(keyspaceName, "SimpleStrategy", 1);
        // ResultSet result = session.execute("SELECT * FROM system_schema.keyspaces WHERE keyspace_name = 'testBaeldungKeyspace';");
        ResultSet result = session.execute("SELECT * FROM system_schema.keyspaces;");
        // Check if the Keyspace exists in the returned keyspaces.
        List<String> matchedKeyspaces = result.all().stream().filter(( r) -> r.getString(0).equals(keyspaceName.toLowerCase())).map(( r) -> r.getString(0)).collect(Collectors.toList());
        Assert.assertEquals(matchedKeyspaces.size(), 1);
        Assert.assertTrue(matchedKeyspaces.get(0).equals(keyspaceName.toLowerCase()));
    }

    @Test
    public void whenDeletingAKeyspace_thenDoesNotExist() {
        String keyspaceName = "testBaeldungKeyspace";
        // schemaRepository.createKeyspace(keyspaceName, "SimpleStrategy", 1);
        schemaRepository.deleteKeyspace(keyspaceName);
        ResultSet result = session.execute("SELECT * FROM system_schema.keyspaces;");
        boolean isKeyspaceCreated = result.all().stream().anyMatch(( r) -> r.getString(0).equals(keyspaceName.toLowerCase()));
        Assert.assertFalse(isKeyspaceCreated);
    }
}

