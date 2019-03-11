package org.apereo.cas.cosmosdb;


import lombok.val;
import org.apereo.cas.configuration.model.support.cosmosdb.CosmosDbServiceRegistryProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ApplicationContext;


/**
 * This is {@link CosmosDbObjectFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class })
public class CosmosDbObjectFactoryTests {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void verifyDatabaseLinkCollections() {
        Assertions.assertNotNull(CosmosDbObjectFactory.getDatabaseLink("databaseName"));
        Assertions.assertNotNull(CosmosDbObjectFactory.getDocumentLink("databaseName", "document", "id"));
        Assertions.assertNotNull(CosmosDbObjectFactory.getCollectionLink("databaseName", "collection"));
    }

    @Test
    public void verifyClient() {
        val factory = new CosmosDbObjectFactory(applicationContext);
        val properties = new CosmosDbServiceRegistryProperties();
        properties.setUri("http://localhost:1234");
        properties.setKey("123456");
        Assertions.assertNotNull(factory.createDocumentClient(properties));
    }

    @Test
    public void verifyDocumentDb() {
        val factory = new CosmosDbObjectFactory(applicationContext);
        val properties = new CosmosDbServiceRegistryProperties();
        properties.setUri("http://localhost:1234");
        properties.setKey("123456");
        Assertions.assertNotNull(factory.createDocumentDbFactory(properties));
    }

    @Test
    public void verifyDocumentDbTemplate() {
        val factory = new CosmosDbObjectFactory(applicationContext);
        val properties = new CosmosDbServiceRegistryProperties();
        properties.setUri("http://localhost:1234");
        properties.setKey("123456");
        Assertions.assertNotNull(factory.createDocumentDbTemplate(factory.createDocumentDbFactory(properties), properties));
    }
}

