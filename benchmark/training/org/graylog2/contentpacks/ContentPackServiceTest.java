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
package org.graylog2.contentpacks;


import MessageOutput.Factory;
import ModelTypes.OUTPUT_V1;
import ModelTypes.STREAM_V1;
import StreamImpl.FIELD_TITLE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.graylog2.alarmcallbacks.AlarmCallbackConfigurationService;
import org.graylog2.alerts.AlertService;
import org.graylog2.contentpacks.model.ContentPackInstallation;
import org.graylog2.contentpacks.model.ContentPackUninstallDetails;
import org.graylog2.contentpacks.model.ContentPackUninstallation;
import org.graylog2.contentpacks.model.ContentPackV1;
import org.graylog2.contentpacks.model.ModelId;
import org.graylog2.contentpacks.model.entities.EntityDescriptor;
import org.graylog2.contentpacks.model.entities.NativeEntityDescriptor;
import org.graylog2.database.NotFoundException;
import org.graylog2.grok.GrokPattern;
import org.graylog2.grok.GrokPatternService;
import org.graylog2.indexer.indexset.IndexSetService;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.outputs.MessageOutput;
import org.graylog2.plugin.streams.Output;
import org.graylog2.shared.bindings.providers.ObjectMapperProvider;
import org.graylog2.streams.OutputImpl;
import org.graylog2.streams.OutputService;
import org.graylog2.streams.StreamMock;
import org.graylog2.streams.StreamRuleService;
import org.graylog2.streams.StreamService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ContentPackServiceTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Mock
    private AlertService alertService;

    @Mock
    private AlarmCallbackConfigurationService alarmCallbackConfigurationService;

    @Mock
    private StreamService streamService;

    @Mock
    private StreamRuleService streamRuleService;

    @Mock
    private IndexSetService indexSetService;

    @Mock
    private OutputService outputService;

    @Mock
    private GrokPatternService patternService;

    @Mock
    private ContentPackInstallationPersistenceService contentPackInstallService;

    private ContentPackService contentPackService;

    private Set<PluginMetaData> pluginMetaData;

    private Map<String, Factory<? extends MessageOutput>> outputFactories;

    private ContentPackV1 contentPack;

    private ContentPackInstallation contentPackInstallation;

    private GrokPattern grokPattern;

    private ImmutableSet<NativeEntityDescriptor> nativeEntityDescriptors;

    @Test
    public void resolveEntitiesWithEmptyInput() {
        final Set<EntityDescriptor> resolvedEntities = contentPackService.resolveEntities(Collections.emptySet());
        assertThat(resolvedEntities).isEmpty();
    }

    @Test
    public void resolveEntitiesWithNoDependencies() throws NotFoundException {
        final StreamMock streamMock = new StreamMock(ImmutableMap.of("_id", "stream-1234", FIELD_TITLE, "Stream Title"));
        Mockito.when(streamService.load("stream-1234")).thenReturn(streamMock);
        final ImmutableSet<EntityDescriptor> unresolvedEntities = ImmutableSet.of(EntityDescriptor.create("stream-1234", STREAM_V1));
        final Set<EntityDescriptor> resolvedEntities = contentPackService.resolveEntities(unresolvedEntities);
        assertThat(resolvedEntities).containsOnly(EntityDescriptor.create("stream-1234", STREAM_V1));
    }

    @Test
    public void resolveEntitiesWithTransitiveDependencies() throws NotFoundException {
        final StreamMock streamMock = new StreamMock(ImmutableMap.of("_id", "stream-1234", FIELD_TITLE, "Stream Title")) {
            @Override
            public Set<Output> getOutputs() {
                return Collections.singleton(OutputImpl.create("output-1234", "Output Title", "org.example.outputs.SomeOutput", "admin", Collections.emptyMap(), new Date(0L), null));
            }
        };
        Mockito.when(streamService.load("stream-1234")).thenReturn(streamMock);
        final ImmutableSet<EntityDescriptor> unresolvedEntities = ImmutableSet.of(EntityDescriptor.create("stream-1234", STREAM_V1));
        final Set<EntityDescriptor> resolvedEntities = contentPackService.resolveEntities(unresolvedEntities);
        assertThat(resolvedEntities).containsOnly(EntityDescriptor.create("stream-1234", STREAM_V1), EntityDescriptor.create("output-1234", OUTPUT_V1));
    }

    @Test
    public void uninstallContentPack() throws NotFoundException {
        /* Test successful uninstall */
        Mockito.when(patternService.load("dead-beef1")).thenReturn(grokPattern);
        ContentPackUninstallation expectSuccess = ContentPackUninstallation.builder().skippedEntities(ImmutableSet.of()).failedEntities(ImmutableSet.of()).entities(nativeEntityDescriptors).build();
        ContentPackUninstallation resultSuccess = contentPackService.uninstallContentPack(contentPack, contentPackInstallation);
        assertThat(resultSuccess).isEqualTo(expectSuccess);
        /* Test skipped uninstall */
        Mockito.when(contentPackInstallService.countInstallationOfEntityById(ModelId.of("dead-beef1"))).thenReturn(((long) (2)));
        ContentPackUninstallation expectSkip = ContentPackUninstallation.builder().skippedEntities(nativeEntityDescriptors).failedEntities(ImmutableSet.of()).entities(ImmutableSet.of()).build();
        ContentPackUninstallation resultSkip = contentPackService.uninstallContentPack(contentPack, contentPackInstallation);
        assertThat(resultSkip).isEqualTo(expectSkip);
        /* Test skipped uninstall */
        Mockito.when(contentPackInstallService.countInstallationOfEntityById(ModelId.of("dead-beef1"))).thenReturn(((long) (1)));
        Mockito.when(contentPackInstallService.countInstallationOfEntityByIdAndFoundOnSystem(ModelId.of("dead-beef1"))).thenReturn(((long) (1)));
        ContentPackUninstallation expectSkip2 = ContentPackUninstallation.builder().skippedEntities(nativeEntityDescriptors).failedEntities(ImmutableSet.of()).entities(ImmutableSet.of()).build();
        ContentPackUninstallation resultSkip2 = contentPackService.uninstallContentPack(contentPack, contentPackInstallation);
        assertThat(resultSkip2).isEqualTo(expectSkip2);
        /* Test not found while uninstall */
        Mockito.when(contentPackInstallService.countInstallationOfEntityById(ModelId.of("dead-beef1"))).thenReturn(((long) (1)));
        Mockito.when(contentPackInstallService.countInstallationOfEntityByIdAndFoundOnSystem(ModelId.of("dead-beef1"))).thenReturn(((long) (0)));
        Mockito.when(patternService.load("dead-beef1")).thenThrow(new NotFoundException("Not found."));
        ContentPackUninstallation expectFailure = ContentPackUninstallation.builder().skippedEntities(ImmutableSet.of()).failedEntities(ImmutableSet.of()).entities(ImmutableSet.of()).build();
        ContentPackUninstallation resultFailure = contentPackService.uninstallContentPack(contentPack, contentPackInstallation);
        assertThat(resultFailure).isEqualTo(expectFailure);
    }

    @Test
    public void getUninstallDetails() throws NotFoundException {
        /* Test will be uninstalled */
        Mockito.when(contentPackInstallService.countInstallationOfEntityById(ModelId.of("dead-beef1"))).thenReturn(((long) (1)));
        ContentPackUninstallDetails expect = ContentPackUninstallDetails.create(nativeEntityDescriptors);
        ContentPackUninstallDetails result = contentPackService.getUninstallDetails(contentPack, contentPackInstallation);
        assertThat(result).isEqualTo(expect);
        /* Test nothing will be uninstalled */
        Mockito.when(contentPackInstallService.countInstallationOfEntityById(ModelId.of("dead-beef1"))).thenReturn(((long) (2)));
        ContentPackUninstallDetails expectNon = ContentPackUninstallDetails.create(ImmutableSet.of());
        ContentPackUninstallDetails resultNon = contentPackService.getUninstallDetails(contentPack, contentPackInstallation);
        assertThat(resultNon).isEqualTo(expectNon);
    }
}

