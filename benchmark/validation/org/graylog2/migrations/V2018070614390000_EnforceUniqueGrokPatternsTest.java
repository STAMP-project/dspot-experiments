/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.migrations;


import com.google.common.eventbus.Subscribe;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bson.Document;
import org.graylog2.events.ClusterEventBus;
import org.graylog2.grok.GrokPatternsDeletedEvent;
import org.junit.Test;


public class V2018070614390000_EnforceUniqueGrokPatternsTest {
    private MongoCollection<Document> collection;

    private V2018070614390000_EnforceUniqueGrokPatterns migration;

    private ClusterEventBus clusterEventBus;

    private V2018070614390000_EnforceUniqueGrokPatternsTest.TestSubscriber subscriber;

    @Test
    public void upgradeAbortsIfIndexExists() {
        final IndexOptions indexOptions = new IndexOptions().name("idx_name_asc_unique").unique(true);
        collection.createIndex(Indexes.ascending("name"), indexOptions);
        migration.upgrade();
        assertThat(migration.isIndexCreated()).isFalse();
        assertThat(subscriber.events).isEmpty();
    }

    @Test
    public void upgradeRunsIfIndexDoesNotExist() {
        migration.upgrade();
        assertThat(migration.isIndexCreated()).isTrue();
        assertThat(subscriber.events).isEmpty();
    }

    @Test
    public void upgradeRemovesDuplicateGrokPatterns() {
        collection.insertMany(Arrays.asList(grokPattern("FOO", "[a-z]+"), grokPattern("BAR", "%{FOO}[0-9]+"), grokPattern("BAR", "[0-9]+")));
        migration.upgrade();
        assertThat(migration.isIndexCreated()).isTrue();
        assertThat(collection.find()).anySatisfy(( document) -> assertThat(document).containsEntry("name", "FOO").containsEntry("pattern", "[a-z]+")).anySatisfy(( document) -> assertThat(document).containsEntry("name", "BAR").containsEntry("pattern", "%{FOO}[0-9]+")).noneSatisfy(( document) -> assertThat(document).containsEntry("name", "BAR").containsEntry("pattern", "[0-9]+"));
        assertThat(subscriber.events).containsOnly(GrokPatternsDeletedEvent.create(Collections.singleton("BAR")));
    }

    @Test
    public void insertingDuplicateGrokPatternsIsNotPossibleAfterUpgrade() {
        collection.insertOne(grokPattern("FOO", "[a-z]+"));
        migration.upgrade();
        assertThatThrownBy(() -> collection.insertOne(grokPattern("FOO", "[a-z]+"))).isInstanceOf(DuplicateKeyException.class).hasMessageContaining("E11000 duplicate key error index: migration-test.grok_patterns.idx_name_asc_unique");
    }

    private static class TestSubscriber {
        public final List<GrokPatternsDeletedEvent> events = new CopyOnWriteArrayList<>();

        @Subscribe
        public void handleGrokPatternsChangedEvent(GrokPatternsDeletedEvent event) {
            events.add(event);
        }
    }
}

