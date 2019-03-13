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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.graylog2.alarmcallbacks.AlarmCallbackConfiguration;
import org.graylog2.alarmcallbacks.AlarmCallbackConfigurationService;
import org.graylog2.alarmcallbacks.EmailAlarmCallback;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.rest.models.alarmcallbacks.requests.CreateAlarmCallbackRequest;
import org.graylog2.shared.users.UserService;
import org.graylog2.streams.StreamService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class V20161125142400_EmailAlarmCallbackMigrationTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private V20161125142400_EmailAlarmCallbackMigration emailAlarmCallbackMigrationPeriodical;

    @Mock
    private ClusterConfigService clusterConfigService;

    @Mock
    private StreamService streamService;

    @Mock
    private AlarmCallbackConfigurationService alarmCallbackConfigurationService;

    @Mock
    private EmailAlarmCallback emailAlarmCallback;

    @Mock
    private UserService userService;

    private static final String localAdminId = "local:adminMock";

    @Test
    public void doNotMigrateAnythingWithoutStreams() throws Exception {
        Mockito.when(this.streamService.loadAll()).thenReturn(Collections.emptyList());
        this.emailAlarmCallbackMigrationPeriodical.upgrade();
        Mockito.verify(this.alarmCallbackConfigurationService, Mockito.never()).create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        verifyMigrationCompletedWasPosted();
    }

    @Test
    public void doNotMigrateAnythingWithoutQualifyingStreams() throws Exception {
        final Stream stream1 = Mockito.mock(Stream.class);
        Mockito.when(stream1.getAlertReceivers()).thenReturn(Collections.emptyMap());
        final Stream stream2 = Mockito.mock(Stream.class);
        Mockito.when(stream2.getAlertReceivers()).thenReturn(ImmutableMap.of("users", Collections.emptyList(), "emails", Collections.emptyList()));
        Mockito.when(this.streamService.loadAll()).thenReturn(ImmutableList.of(stream1, stream2));
        this.emailAlarmCallbackMigrationPeriodical.upgrade();
        Mockito.verify(this.streamService, Mockito.never()).getAlertConditions(ArgumentMatchers.any());
        Mockito.verify(this.alarmCallbackConfigurationService, Mockito.never()).getForStream(ArgumentMatchers.any());
        Mockito.verify(this.alarmCallbackConfigurationService, Mockito.never()).create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        verifyMigrationCompletedWasPosted();
    }

    @Test
    public void doMigrateSingleQualifyingStream() throws Exception {
        final String matchingStreamId = "matchingStreamId";
        final Stream stream1 = Mockito.mock(Stream.class);
        Mockito.when(stream1.getAlertReceivers()).thenReturn(Collections.emptyMap());
        final Stream stream2 = Mockito.mock(Stream.class);
        Mockito.when(stream2.getAlertReceivers()).thenReturn(ImmutableMap.of("users", ImmutableList.of("foouser"), "emails", ImmutableList.of("foo@bar.com")));
        Mockito.when(stream2.getId()).thenReturn(matchingStreamId);
        Mockito.when(this.streamService.loadAll()).thenReturn(ImmutableList.of(stream1, stream2));
        final AlertCondition alertCondition = Mockito.mock(AlertCondition.class);
        Mockito.when(this.streamService.getAlertConditions(ArgumentMatchers.eq(stream2))).thenReturn(ImmutableList.of(alertCondition));
        final ConfigurationRequest configurationRequest = Mockito.mock(ConfigurationRequest.class);
        Mockito.when(emailAlarmCallback.getRequestedConfiguration()).thenReturn(configurationRequest);
        Mockito.when(configurationRequest.getFields()).thenReturn(Collections.emptyMap());
        final AlarmCallbackConfiguration newAlarmCallback = Mockito.mock(AlarmCallbackConfiguration.class);
        final String newAlarmCallbackId = "newAlarmCallbackId";
        Mockito.when(alarmCallbackConfigurationService.create(ArgumentMatchers.eq(matchingStreamId), ArgumentMatchers.any(CreateAlarmCallbackRequest.class), ArgumentMatchers.eq(V20161125142400_EmailAlarmCallbackMigrationTest.localAdminId))).thenReturn(newAlarmCallback);
        Mockito.when(alarmCallbackConfigurationService.save(ArgumentMatchers.eq(newAlarmCallback))).thenReturn(newAlarmCallbackId);
        this.emailAlarmCallbackMigrationPeriodical.upgrade();
        final ArgumentCaptor<String> streamIdCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<CreateAlarmCallbackRequest> createAlarmCallbackRequestCaptor = ArgumentCaptor.forClass(CreateAlarmCallbackRequest.class);
        final ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.alarmCallbackConfigurationService, Mockito.times(1)).create(streamIdCaptor.capture(), createAlarmCallbackRequestCaptor.capture(), userIdCaptor.capture());
        assertThat(streamIdCaptor.getValue()).isNotNull().isNotEmpty().isEqualTo(matchingStreamId);
        final CreateAlarmCallbackRequest createAlarmCallbackRequest = createAlarmCallbackRequestCaptor.getValue();
        assertThat(createAlarmCallbackRequest.type()).isEqualTo(EmailAlarmCallback.class.getCanonicalName());
        final ArgumentCaptor<AlarmCallbackConfiguration> alarmCallbackConfigurationCaptor = ArgumentCaptor.forClass(AlarmCallbackConfiguration.class);
        Mockito.verify(this.alarmCallbackConfigurationService, Mockito.times(1)).save(alarmCallbackConfigurationCaptor.capture());
        assertThat(alarmCallbackConfigurationCaptor.getValue()).isEqualTo(newAlarmCallback);
        verifyMigrationCompletedWasPosted(ImmutableMap.of(matchingStreamId, Optional.of(newAlarmCallbackId)));
    }

    @Test
    public void doMigrateMultipleQualifyingStreams() throws Exception {
        final String matchingStreamId1 = "matchingStreamId1";
        final String matchingStreamId2 = "matchingStreamId2";
        final Stream stream1 = Mockito.mock(Stream.class);
        Mockito.when(stream1.getAlertReceivers()).thenReturn(Collections.emptyMap());
        final Stream stream2 = Mockito.mock(Stream.class);
        Mockito.when(stream2.getAlertReceivers()).thenReturn(ImmutableMap.of("users", ImmutableList.of("foouser"), "emails", ImmutableList.of("foo@bar.com")));
        Mockito.when(stream2.getId()).thenReturn(matchingStreamId1);
        final Stream stream3 = Mockito.mock(Stream.class);
        Mockito.when(stream3.getAlertReceivers()).thenReturn(ImmutableMap.of("users", ImmutableList.of("foouser2")));
        Mockito.when(stream3.getId()).thenReturn(matchingStreamId2);
        Mockito.when(this.streamService.loadAll()).thenReturn(ImmutableList.of(stream1, stream2, stream3));
        final AlertCondition alertCondition1 = Mockito.mock(AlertCondition.class);
        final AlertCondition alertCondition2 = Mockito.mock(AlertCondition.class);
        Mockito.when(this.streamService.getAlertConditions(ArgumentMatchers.eq(stream2))).thenReturn(ImmutableList.of(alertCondition1));
        Mockito.when(this.streamService.getAlertConditions(ArgumentMatchers.eq(stream3))).thenReturn(ImmutableList.of(alertCondition2));
        final ConfigurationRequest configurationRequest = Mockito.mock(ConfigurationRequest.class);
        Mockito.when(emailAlarmCallback.getRequestedConfiguration()).thenReturn(configurationRequest);
        Mockito.when(configurationRequest.getFields()).thenReturn(Collections.emptyMap());
        final AlarmCallbackConfiguration newAlarmCallback1 = Mockito.mock(AlarmCallbackConfiguration.class);
        final String newAlarmCallbackId1 = "newAlarmCallbackId1";
        final AlarmCallbackConfiguration newAlarmCallback2 = Mockito.mock(AlarmCallbackConfiguration.class);
        final String newAlarmCallbackId2 = "newAlarmCallbackId2";
        Mockito.when(alarmCallbackConfigurationService.create(ArgumentMatchers.eq(matchingStreamId1), ArgumentMatchers.any(CreateAlarmCallbackRequest.class), ArgumentMatchers.eq(V20161125142400_EmailAlarmCallbackMigrationTest.localAdminId))).thenReturn(newAlarmCallback1);
        Mockito.when(alarmCallbackConfigurationService.create(ArgumentMatchers.eq(matchingStreamId2), ArgumentMatchers.any(CreateAlarmCallbackRequest.class), ArgumentMatchers.eq(V20161125142400_EmailAlarmCallbackMigrationTest.localAdminId))).thenReturn(newAlarmCallback2);
        Mockito.when(alarmCallbackConfigurationService.save(ArgumentMatchers.eq(newAlarmCallback1))).thenReturn(newAlarmCallbackId1);
        Mockito.when(alarmCallbackConfigurationService.save(ArgumentMatchers.eq(newAlarmCallback2))).thenReturn(newAlarmCallbackId2);
        this.emailAlarmCallbackMigrationPeriodical.upgrade();
        final ArgumentCaptor<String> streamIdCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<CreateAlarmCallbackRequest> createAlarmCallbackRequestCaptor = ArgumentCaptor.forClass(CreateAlarmCallbackRequest.class);
        final ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.alarmCallbackConfigurationService, Mockito.times(2)).create(streamIdCaptor.capture(), createAlarmCallbackRequestCaptor.capture(), userIdCaptor.capture());
        assertThat(streamIdCaptor.getAllValues()).isNotNull().isNotEmpty().contains(matchingStreamId1).contains(matchingStreamId2);
        createAlarmCallbackRequestCaptor.getAllValues().forEach(( createAlarmCallbackRequest) -> assertThat(createAlarmCallbackRequest.type()).isEqualTo(.class.getCanonicalName()));
        final ArgumentCaptor<AlarmCallbackConfiguration> alarmCallbackConfigurationCaptor = ArgumentCaptor.forClass(AlarmCallbackConfiguration.class);
        Mockito.verify(this.alarmCallbackConfigurationService, Mockito.times(2)).save(alarmCallbackConfigurationCaptor.capture());
        assertThat(alarmCallbackConfigurationCaptor.getAllValues()).isNotNull().isNotEmpty().hasSize(2).contains(newAlarmCallback1).contains(newAlarmCallback2);
        verifyMigrationCompletedWasPosted(ImmutableMap.of(matchingStreamId1, Optional.of(newAlarmCallbackId1), matchingStreamId2, Optional.of(newAlarmCallbackId2)));
    }

    @Test
    public void extractEmptyDefaultValuesFromEmptyEmailAlarmCallbackConfiguration() throws Exception {
        final ConfigurationRequest configurationRequest = Mockito.mock(ConfigurationRequest.class);
        Mockito.when(emailAlarmCallback.getRequestedConfiguration()).thenReturn(configurationRequest);
        final Map<String, Object> defaultConfig = this.emailAlarmCallbackMigrationPeriodical.getDefaultEmailAlarmCallbackConfig();
        assertThat(defaultConfig).isNotNull().isEmpty();
    }

    @Test
    public void extractDefaultValuesFromEmailAlarmCallbackConfiguration() throws Exception {
        final ConfigurationRequest configurationRequest = Mockito.mock(ConfigurationRequest.class);
        Mockito.when(emailAlarmCallback.getRequestedConfiguration()).thenReturn(configurationRequest);
        final ConfigurationField configurationField1 = Mockito.mock(ConfigurationField.class);
        Mockito.when(configurationField1.getDefaultValue()).thenReturn(42);
        final ConfigurationField configurationField2 = Mockito.mock(ConfigurationField.class);
        Mockito.when(configurationField2.getDefaultValue()).thenReturn("foobar");
        final ConfigurationField configurationField3 = Mockito.mock(ConfigurationField.class);
        Mockito.when(configurationField3.getDefaultValue()).thenReturn(true);
        final Map<String, ConfigurationField> configurationFields = ImmutableMap.of("field1", configurationField1, "field2", configurationField2, "field3", configurationField3);
        Mockito.when(configurationRequest.getFields()).thenReturn(configurationFields);
        final Map<String, Object> defaultConfig = this.emailAlarmCallbackMigrationPeriodical.getDefaultEmailAlarmCallbackConfig();
        assertThat(defaultConfig).isNotNull().isNotEmpty().hasSize(3).isEqualTo(ImmutableMap.of("field1", 42, "field2", "foobar", "field3", true));
    }
}

