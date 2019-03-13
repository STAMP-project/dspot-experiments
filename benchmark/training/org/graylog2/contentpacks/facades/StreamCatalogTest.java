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
package org.graylog2.contentpacks.facades;


import ModelTypes.OUTPUT_V1;
import ModelTypes.STREAM_V1;
import MongoIndexSet.Factory;
import Stream.MatchingType.AND;
import StreamImpl.FIELD_DESCRIPTION;
import StreamImpl.FIELD_DISABLED;
import StreamImpl.FIELD_TITLE;
import StreamRuleImpl.FIELD_FIELD;
import StreamRuleImpl.FIELD_INVERTED;
import StreamRuleImpl.FIELD_STREAM_ID;
import StreamRuleImpl.FIELD_TYPE;
import StreamRuleImpl.FIELD_VALUE;
import StreamRuleType.EXACT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import java.util.Optional;
import java.util.Set;
import org.bson.types.ObjectId;
import org.graylog2.alarmcallbacks.AlarmCallbackConfigurationService;
import org.graylog2.alerts.AlertService;
import org.graylog2.contentpacks.EntityDescriptorIds;
import org.graylog2.contentpacks.model.ModelId;
import org.graylog2.contentpacks.model.entities.Entity;
import org.graylog2.contentpacks.model.entities.EntityDescriptor;
import org.graylog2.contentpacks.model.entities.EntityExcerpt;
import org.graylog2.contentpacks.model.entities.EntityV1;
import org.graylog2.contentpacks.model.entities.StreamEntity;
import org.graylog2.contentpacks.model.entities.references.ValueReference;
import org.graylog2.database.MongoConnectionRule;
import org.graylog2.indexer.indexset.IndexSetService;
import org.graylog2.notifications.NotificationService;
import org.graylog2.plugin.streams.Output;
import org.graylog2.plugin.streams.StreamRule;
import org.graylog2.shared.bindings.providers.ObjectMapperProvider;
import org.graylog2.streams.OutputService;
import org.graylog2.streams.StreamImpl;
import org.graylog2.streams.matchers.StreamRuleMock;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class StreamCatalogTest {
    @ClassRule
    public static final InMemoryMongoDb IN_MEMORY_MONGO_DB = newInMemoryMongoDbRule().build();

    @Rule
    public final MongoConnectionRule mongoRule = MongoConnectionRule.build("test");

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Mock
    private AlertService alertService;

    @Mock
    private OutputService outputService;

    @Mock
    private IndexSetService indexSetService;

    @Mock
    private Factory mongoIndexSetFactory;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AlarmCallbackConfigurationService alarmCallbackConfigurationService;

    private StreamFacade facade;

    @Test
    public void encode() {
        final ImmutableMap<String, Object> streamFields = ImmutableMap.of(FIELD_TITLE, "Stream Title", FIELD_DESCRIPTION, "Stream Description", FIELD_DISABLED, false);
        final ImmutableMap<String, Object> streamRuleFields = ImmutableMap.<String, Object>builder().put("_id", "1234567890").put(FIELD_TYPE, EXACT.getValue()).put(StreamRuleImpl.FIELD_DESCRIPTION, "description").put(FIELD_FIELD, "field").put(FIELD_VALUE, "value").put(FIELD_INVERTED, false).put(FIELD_STREAM_ID, "1234567890").build();
        final ImmutableList<StreamRule> streamRules = ImmutableList.of(new StreamRuleMock(streamRuleFields));
        final ImmutableSet<Output> outputs = ImmutableSet.of();
        final ObjectId streamId = new ObjectId();
        final StreamImpl stream = new StreamImpl(streamId, streamFields, streamRules, outputs, null);
        final EntityDescriptor descriptor = EntityDescriptor.create(stream.getId(), STREAM_V1);
        final EntityDescriptorIds entityDescriptorIds = EntityDescriptorIds.of(descriptor);
        final Entity entity = facade.exportNativeEntity(stream, entityDescriptorIds);
        assertThat(entity).isInstanceOf(EntityV1.class);
        assertThat(entity.id()).isEqualTo(ModelId.of(entityDescriptorIds.get(descriptor).orElse(null)));
        assertThat(entity.type()).isEqualTo(STREAM_V1);
        final EntityV1 entityV1 = ((EntityV1) (entity));
        final StreamEntity streamEntity = objectMapper.convertValue(entityV1.data(), StreamEntity.class);
        assertThat(streamEntity.title()).isEqualTo(ValueReference.of("Stream Title"));
        assertThat(streamEntity.description()).isEqualTo(ValueReference.of("Stream Description"));
        assertThat(streamEntity.disabled()).isEqualTo(ValueReference.of(false));
        assertThat(streamEntity.streamRules()).hasSize(1);
    }

    @Test
    public void createExcerpt() {
        final ImmutableMap<String, Object> fields = ImmutableMap.of(DashboardImpl.FIELD_TITLE, "Stream Title");
        final StreamImpl stream = new StreamImpl(fields);
        final EntityExcerpt excerpt = facade.createExcerpt(stream);
        assertThat(excerpt.id()).isEqualTo(ModelId.of(stream.getId()));
        assertThat(excerpt.type()).isEqualTo(STREAM_V1);
        assertThat(excerpt.title()).isEqualTo(stream.getTitle());
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/streams.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void listEntityExcerpts() {
        final EntityExcerpt expectedEntityExcerpt1 = EntityExcerpt.builder().id(ModelId.of("000000000000000000000001")).type(STREAM_V1).title("All messages").build();
        final EntityExcerpt expectedEntityExcerpt2 = EntityExcerpt.builder().id(ModelId.of("5adf23894b900a0fdb4e517d")).type(STREAM_V1).title("Test").build();
        final Set<EntityExcerpt> entityExcerpts = facade.listEntityExcerpts();
        assertThat(entityExcerpts).containsOnly(expectedEntityExcerpt1, expectedEntityExcerpt2);
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/streams.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void collectEntity() {
        final EntityDescriptor descriptor = EntityDescriptor.create("5adf23894b900a0fdb4e517d", STREAM_V1);
        final EntityDescriptor outputDescriptor = EntityDescriptor.create("5adf239e4b900a0fdb4e5197", OUTPUT_V1);
        final EntityDescriptorIds entityDescriptorIds = EntityDescriptorIds.of(descriptor, outputDescriptor);
        final Optional<Entity> collectedEntity = facade.exportEntity(descriptor, entityDescriptorIds);
        assertThat(collectedEntity).isPresent().containsInstanceOf(EntityV1.class);
        final EntityV1 entity = ((EntityV1) (collectedEntity.orElseThrow(AssertionError::new)));
        assertThat(entity.id()).isEqualTo(ModelId.of(entityDescriptorIds.get(descriptor).orElse(null)));
        assertThat(entity.type()).isEqualTo(STREAM_V1);
        final StreamEntity streamEntity = objectMapper.convertValue(entity.data(), StreamEntity.class);
        assertThat(streamEntity.title()).isEqualTo(ValueReference.of("Test"));
        assertThat(streamEntity.description()).isEqualTo(ValueReference.of("Description"));
        assertThat(streamEntity.matchingType()).isEqualTo(ValueReference.of(AND));
        assertThat(streamEntity.streamRules()).hasSize(7);
        assertThat(streamEntity.outputs()).containsExactly(ValueReference.of(entityDescriptorIds.get(outputDescriptor).orElse(null)));
    }
}

