package org.apereo.cas.dynamodb;


import Regions.US_EAST_1;
import lombok.val;
import org.apereo.cas.configuration.model.support.dynamodb.AbstractDynamoDbProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * This is {@link AmazonDynamoDbClientFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("DynamoDb")
public class AmazonDynamoDbClientFactoryTests {
    @Test
    public void verifyAction() {
        val factory = new AmazonDynamoDbClientFactory();
        val properties = new AbstractDynamoDbProperties() {
            private static final long serialVersionUID = -3599433486448467450L;
        };
        properties.setRegion(US_EAST_1.getName());
        val client = factory.createAmazonDynamoDb(properties);
        Assertions.assertNotNull(client);
    }
}

