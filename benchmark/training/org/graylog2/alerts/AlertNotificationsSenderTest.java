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
package org.graylog2.alerts;


import AlertCondition.CheckResult;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import org.graylog2.alarmcallbacks.AlarmCallbackConfiguration;
import org.graylog2.alarmcallbacks.AlarmCallbackConfigurationService;
import org.graylog2.alarmcallbacks.AlarmCallbackFactory;
import org.graylog2.alarmcallbacks.AlarmCallbackHistoryService;
import org.graylog2.plugin.Tools;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.AlarmCallback;
import org.graylog2.plugin.streams.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class AlertNotificationsSenderTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private AlertNotificationsSender alertNotificationsSender;

    @Mock
    private AlarmCallbackConfigurationService alarmCallbackConfigurationService;

    @Mock
    private AlarmCallbackFactory alarmCallbackFactory;

    @Mock
    private AlarmCallbackHistoryService alarmCallbackHistoryService;

    @Test
    public void executeStreamWithNotifications() throws Exception {
        final Stream stream = Mockito.mock(Stream.class);
        final Alert alert = Mockito.mock(Alert.class);
        final AlertCondition alertCondition = Mockito.mock(AlertCondition.class);
        final AlertCondition.CheckResult positiveCheckResult = new AbstractAlertCondition.CheckResult(true, alertCondition, "Mocked positive CheckResult", Tools.nowUTC(), Collections.emptyList());
        final AlarmCallbackConfiguration alarmCallbackConfiguration = Mockito.mock(AlarmCallbackConfiguration.class);
        Mockito.when(alarmCallbackConfigurationService.getForStream(ArgumentMatchers.eq(stream))).thenReturn(ImmutableList.of(alarmCallbackConfiguration));
        final AlarmCallback alarmCallback = Mockito.mock(AlarmCallback.class);
        Mockito.when(alarmCallbackFactory.create(ArgumentMatchers.eq(alarmCallbackConfiguration))).thenReturn(alarmCallback);
        alertNotificationsSender.send(positiveCheckResult, stream, alert, alertCondition);
        final ArgumentCaptor<Stream> streamCaptor = ArgumentCaptor.forClass(Stream.class);
        final ArgumentCaptor<AlertCondition.CheckResult> checkResultCaptor = ArgumentCaptor.forClass(CheckResult.class);
        Mockito.verify(alarmCallback, Mockito.times(1)).call(streamCaptor.capture(), checkResultCaptor.capture());
        assertThat(streamCaptor.getValue()).isEqualTo(stream);
        assertThat(checkResultCaptor.getValue()).isEqualTo(positiveCheckResult);
        final ArgumentCaptor<AlarmCallbackConfiguration> alarmCallbackConfigurationCaptor = ArgumentCaptor.forClass(AlarmCallbackConfiguration.class);
        final ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        final ArgumentCaptor<AlertCondition> alertConditionCaptor = ArgumentCaptor.forClass(AlertCondition.class);
        Mockito.verify(alarmCallbackHistoryService, Mockito.times(1)).success(alarmCallbackConfigurationCaptor.capture(), alertCaptor.capture(), alertConditionCaptor.capture());
        assertThat(alarmCallbackConfigurationCaptor.getValue()).isEqualTo(alarmCallbackConfiguration);
        assertThat(alertCaptor.getValue()).isEqualTo(alert);
        assertThat(alertConditionCaptor.getValue()).isEqualTo(alertCondition);
    }
}

