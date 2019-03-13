/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mongodb;


import io.debezium.doc.FixFor;
import io.debezium.schema.TopicSelector;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class TopicSelectorTest {
    private TopicSelector<CollectionId> noPrefix;

    private TopicSelector<CollectionId> withPrefix;

    @Test
    public void shouldHandleCollectionIdWithDatabaseAndCollection() {
        assertTopic(noPrefix, dbAndCollection("db", "coll")).isEqualTo("db.coll");
        assertTopic(withPrefix, dbAndCollection("db", "coll")).isEqualTo("prefix.db.coll");
    }

    @Test
    @FixFor("DBZ-878")
    public void shouldHandleCollectionIdWithInvalidTopicNameChar() {
        assertTopic(noPrefix, dbAndCollection("db", "my@collection")).isEqualTo("db.my_collection");
        assertTopic(withPrefix, dbAndCollection("db", "my@collection")).isEqualTo("prefix.db.my_collection");
    }
}

