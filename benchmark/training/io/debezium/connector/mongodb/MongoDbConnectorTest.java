/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mongodb;


import MongoDbConnectorConfig.ALL_FIELDS;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class MongoDbConnectorTest {
    @Test
    public void shouldReturnConfigurationDefinition() {
        MongoDbConnectorTest.assertConfigDefIsValid(new MongoDbConnector(), ALL_FIELDS);
    }
}

