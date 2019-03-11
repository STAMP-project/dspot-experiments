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


import ClusterEventCleanupPeriodical.DEFAULT_MAX_EVENT_AGE;
import ClusterEventPeriodical.COLLECTION_NAME;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import com.mongodb.DBCollection;
import org.graylog2.database.MongoConnection;
import org.graylog2.database.MongoConnectionRule;
import org.graylog2.shared.bindings.providers.ObjectMapperProvider;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static ClusterEventCleanupPeriodical.DEFAULT_MAX_EVENT_AGE;


public class ClusterEventCleanupPeriodicalTest {
    @ClassRule
    public static final InMemoryMongoDb IN_MEMORY_MONGO_DB = newInMemoryMongoDbRule().build();

    private static final DateTime TIME = new DateTime(2015, 4, 1, 0, 0, DateTimeZone.UTC);

    @Rule
    public MongoConnectionRule mongoRule = MongoConnectionRule.build("test");

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    private MongoConnection mongoConnection;

    private ClusterEventCleanupPeriodical clusterEventCleanupPeriodical;

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testDoRun() throws Exception {
        final DBCollection collection = mongoConnection.getDatabase().getCollection(COLLECTION_NAME);
        assertThat(insertEvent(collection, 0L)).isTrue();
        assertThat(insertEvent(collection, ClusterEventCleanupPeriodicalTest.TIME.getMillis())).isTrue();
        assertThat(insertEvent(collection, ClusterEventCleanupPeriodicalTest.TIME.minus(DEFAULT_MAX_EVENT_AGE).getMillis())).isTrue();
        assertThat(insertEvent(collection, ClusterEventCleanupPeriodicalTest.TIME.minus((2 * (DEFAULT_MAX_EVENT_AGE))).getMillis())).isTrue();
        assertThat(collection.count()).isEqualTo(4L);
        clusterEventCleanupPeriodical.run();
        assertThat(collection.count()).isEqualTo(2L);
    }
}

