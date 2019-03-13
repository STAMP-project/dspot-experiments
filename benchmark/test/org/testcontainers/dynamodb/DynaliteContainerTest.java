package org.testcontainers.dynamodb;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.junit.Rule;
import org.junit.Test;


public class DynaliteContainerTest {
    @Rule
    public DynaliteContainer dynamoDB = new DynaliteContainer();

    @Test
    public void simpleTestWithManualClientCreation() {
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(dynamoDB.getEndpointConfiguration()).withCredentials(dynamoDB.getCredentials()).build();
        runTest(client);
    }

    @Test
    public void simpleTestWithProvidedClient() {
        final AmazonDynamoDB client = dynamoDB.getClient();
        runTest(client);
    }
}

