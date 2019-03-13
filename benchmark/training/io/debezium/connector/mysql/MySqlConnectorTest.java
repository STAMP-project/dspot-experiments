/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import MySqlConnectorConfig.ALL_FIELDS;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class MySqlConnectorTest {
    @Test
    public void shouldReturnConfigurationDefinition() {
        MySqlConnectorTest.assertConfigDefIsValid(new MySqlConnector(), ALL_FIELDS);
    }
}

