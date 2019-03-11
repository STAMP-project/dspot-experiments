/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mongodb;


import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class CollectionIdTest {
    private CollectionId id;

    @Test
    public void shouldParseStringWithThreeSegments() {
        assertParseable("a", "b", "c");
    }

    @Test
    public void shouldNotParseStringWithTwoSegments() {
        assertThat(CollectionId.parse("a.b")).isNull();
    }

    @Test
    public void shouldNotParseStringWithOneSegments() {
        assertThat(CollectionId.parse("a")).isNull();
    }
}

