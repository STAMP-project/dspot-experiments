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
package org.graylog2.events;


import ClusterEventPeriodical.COLLECTION_NAME;
import WriteConcern.JOURNALED;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.graylog2.database.MongoConnection;
import org.graylog2.database.MongoConnectionRule;
import org.graylog2.plugin.system.NodeId;
import org.graylog2.shared.bindings.providers.ObjectMapperProvider;
import org.graylog2.system.debug.DebugEvent;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ClusterEventPeriodicalTest {
    @ClassRule
    public static final InMemoryMongoDb IN_MEMORY_MONGO_DB = newInMemoryMongoDbRule().build();

    private static final DateTime TIME = new DateTime(2015, 4, 1, 0, 0, DateTimeZone.UTC);

    @Rule
    public MongoConnectionRule mongoRule = MongoConnectionRule.build("test");

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Mock
    private NodeId nodeId;

    @Spy
    private EventBus serverEventBus;

    @Spy
    private ClusterEventBus clusterEventBus;

    private MongoConnection mongoConnection;

    private ClusterEventPeriodical clusterEventPeriodical;

    @Test
    public void clusterEventServiceRegistersItselfWithClusterEventBus() throws Exception {
        Mockito.verify(clusterEventBus, Mockito.times(1)).registerClusterEventSubscriber(clusterEventPeriodical);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void runHandlesInvalidPayloadsGracefully() throws Exception {
        DBObject event = new BasicDBObjectBuilder().add("timestamp", ClusterEventPeriodicalTest.TIME.getMillis()).add("producer", "TEST-PRODUCER").add("consumers", Collections.emptyList()).add("event_class", SimpleEvent.class.getCanonicalName()).add("payload", ImmutableMap.of("HAHA", "test")).get();
        @SuppressWarnings("deprecation")
        final DBCollection collection = mongoConnection.getDatabase().getCollection(COLLECTION_NAME);
        collection.save(event);
        assertThat(collection.count()).isEqualTo(1L);
        clusterEventPeriodical.run();
        assertThat(collection.count()).isEqualTo(1L);
        @SuppressWarnings("unchecked")
        final List<String> consumers = ((List<String>) (collection.findOne().get("consumers")));
        assertThat(consumers).containsExactly(nodeId.toString());
        Mockito.verify(serverEventBus, Mockito.never()).post(ArgumentMatchers.any());
        Mockito.verify(clusterEventBus, Mockito.never()).post(ArgumentMatchers.any());
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void serverEventBusDispatchesTypedEvents() throws Exception {
        final ClusterEventPeriodicalTest.SimpleEventHandler handler = new ClusterEventPeriodicalTest.SimpleEventHandler();
        serverEventBus.register(handler);
        DBObject event = new BasicDBObjectBuilder().add("timestamp", ClusterEventPeriodicalTest.TIME.getMillis()).add("producer", "TEST-PRODUCER").add("consumers", Collections.emptyList()).add("event_class", SimpleEvent.class.getCanonicalName()).add("payload", ImmutableMap.of("payload", "test")).get();
        @SuppressWarnings("deprecation")
        final DBCollection collection = mongoConnection.getDatabase().getCollection(COLLECTION_NAME);
        assertThat(collection.save(event).getN()).isEqualTo(1);
        assertThat(collection.count()).isEqualTo(1L);
        assertThat(handler.invocations).hasValue(0);
        clusterEventPeriodical.run();
        assertThat(handler.invocations).hasValue(1);
        assertThat(collection.count()).isEqualTo(1L);
        @SuppressWarnings("unchecked")
        final List<String> consumers = ((List<String>) (collection.findOne().get("consumers")));
        assertThat(consumers).containsExactly(nodeId.toString());
        Mockito.verify(serverEventBus, Mockito.times(1)).post(ArgumentMatchers.any(SimpleEvent.class));
        Mockito.verify(clusterEventBus, Mockito.never()).post(ArgumentMatchers.any());
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void runHandlesAutoValueCorrectly() throws Exception {
        final DebugEvent event = DebugEvent.create("Node ID", ClusterEventPeriodicalTest.TIME, "test");
        DBObject dbObject = new BasicDBObjectBuilder().add("timestamp", ClusterEventPeriodicalTest.TIME.getMillis()).add("producer", "TEST-PRODUCER").add("consumers", Collections.emptyList()).add("event_class", DebugEvent.class.getCanonicalName()).add("payload", objectMapper.convertValue(event, Map.class)).get();
        @SuppressWarnings("deprecation")
        final DBCollection collection = mongoConnection.getDatabase().getCollection(COLLECTION_NAME);
        collection.save(dbObject);
        assertThat(collection.count()).isEqualTo(1L);
        clusterEventPeriodical.run();
        assertThat(collection.count()).isEqualTo(1L);
        @SuppressWarnings("unchecked")
        final List<String> consumers = ((List<String>) (collection.findOne().get("consumers")));
        assertThat(consumers).containsExactly(nodeId.toString());
        Mockito.verify(serverEventBus, Mockito.times(1)).post(event);
        Mockito.verify(clusterEventBus, Mockito.never()).post(event);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testRun() throws Exception {
        DBObject event = new BasicDBObjectBuilder().add("timestamp", ClusterEventPeriodicalTest.TIME.getMillis()).add("producer", "TEST-PRODUCER").add("consumers", Collections.emptyList()).add("event_class", SimpleEvent.class.getCanonicalName()).add("payload", ImmutableMap.of("payload", "test")).get();
        @SuppressWarnings("deprecation")
        final DBCollection collection = mongoConnection.getDatabase().getCollection(COLLECTION_NAME);
        collection.save(event);
        assertThat(collection.count()).isEqualTo(1L);
        clusterEventPeriodical.run();
        assertThat(collection.count()).isEqualTo(1L);
        @SuppressWarnings("unchecked")
        final List<String> consumers = ((List<String>) (collection.findOne().get("consumers")));
        assertThat(consumers).containsExactly(nodeId.toString());
        Mockito.verify(serverEventBus, Mockito.times(1)).post(new SimpleEvent("test"));
        Mockito.verify(clusterEventBus, Mockito.never()).post(event);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testPublishClusterEvent() throws Exception {
        @SuppressWarnings("deprecation")
        DBCollection collection = mongoConnection.getDatabase().getCollection(COLLECTION_NAME);
        SimpleEvent event = new SimpleEvent("test");
        assertThat(collection.count()).isEqualTo(0L);
        clusterEventPeriodical.publishClusterEvent(event);
        Mockito.verify(clusterEventBus, Mockito.never()).post(ArgumentMatchers.any());
        assertThat(collection.count()).isEqualTo(1L);
        DBObject dbObject = collection.findOne();
        assertThat(((String) (dbObject.get("producer")))).isEqualTo(nodeId.toString());
        assertThat(((String) (dbObject.get("event_class")))).isEqualTo(SimpleEvent.class.getCanonicalName());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = ((Map<String, Object>) (dbObject.get("payload")));
        assertThat(payload).containsEntry("payload", "test");
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void publishClusterEventHandlesAutoValueCorrectly() throws Exception {
        @SuppressWarnings("deprecation")
        DBCollection collection = mongoConnection.getDatabase().getCollection(COLLECTION_NAME);
        DebugEvent event = DebugEvent.create("Node ID", "Test");
        assertThat(collection.count()).isEqualTo(0L);
        clusterEventPeriodical.publishClusterEvent(event);
        Mockito.verify(clusterEventBus, Mockito.never()).post(ArgumentMatchers.any());
        assertThat(collection.count()).isEqualTo(1L);
        DBObject dbObject = collection.findOne();
        assertThat(((String) (dbObject.get("producer")))).isEqualTo(nodeId.toString());
        assertThat(((String) (dbObject.get("event_class")))).isEqualTo(DebugEvent.class.getCanonicalName());
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void publishClusterEventSkipsDeadEvent() throws Exception {
        @SuppressWarnings("deprecation")
        DBCollection collection = mongoConnection.getDatabase().getCollection(COLLECTION_NAME);
        DeadEvent event = new DeadEvent(clusterEventBus, new SimpleEvent("test"));
        assertThat(collection.count()).isEqualTo(0L);
        clusterEventPeriodical.publishClusterEvent(event);
        Mockito.verify(clusterEventBus, Mockito.never()).post(ArgumentMatchers.any());
        assertThat(collection.count()).isEqualTo(0L);
    }

    @Test
    public void prepareCollectionCreatesIndexesOnExistingCollection() throws Exception {
        @SuppressWarnings("deprecation")
        DBCollection original = mongoConnection.getDatabase().getCollection(COLLECTION_NAME);
        original.dropIndexes();
        assertThat(original.getName()).isEqualTo(COLLECTION_NAME);
        assertThat(original.getIndexInfo()).hasSize(1);
        DBCollection collection = ClusterEventPeriodical.prepareCollection(mongoConnection);
        assertThat(collection.getName()).isEqualTo(COLLECTION_NAME);
        assertThat(collection.getIndexInfo()).hasSize(2);
        assertThat(collection.getWriteConcern()).isEqualTo(JOURNALED);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void prepareCollectionCreatesCollectionIfItDoesNotExist() throws Exception {
        @SuppressWarnings("deprecation")
        final DB database = mongoConnection.getDatabase();
        assertThat(database.collectionExists(COLLECTION_NAME)).isFalse();
        DBCollection collection = ClusterEventPeriodical.prepareCollection(mongoConnection);
        assertThat(collection.getName()).isEqualTo(COLLECTION_NAME);
        assertThat(collection.getIndexInfo()).hasSize(2);
        assertThat(collection.getWriteConcern()).isEqualTo(JOURNALED);
    }

    public static class SimpleEventHandler {
        final AtomicInteger invocations = new AtomicInteger();

        @Subscribe
        @SuppressWarnings("unused")
        public void handleSimpleEvent(SimpleEvent event) {
            invocations.incrementAndGet();
        }
    }
}

