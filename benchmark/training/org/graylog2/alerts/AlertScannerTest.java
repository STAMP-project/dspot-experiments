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
import java.util.Collections;
import java.util.Optional;
import org.graylog2.plugin.Tools;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.streams.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class AlertScannerTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private AlertScanner alertScanner;

    @Mock
    private AlertService alertService;

    @Mock
    private AlertNotificationsSender alertNotificationsSender;

    @Test
    public void testNoCheckWhileInGracePeriod() throws Exception {
        final AlertCondition alertCondition = Mockito.mock(AlertCondition.class);
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(alertService.inGracePeriod(ArgumentMatchers.eq(alertCondition))).thenReturn(true);
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isFalse();
        Mockito.verify(alertCondition, Mockito.never()).runCheck();
    }

    @Test
    public void testCheckWithNegativeResult() throws Exception {
        final AlertCondition alertCondition = Mockito.mock(AlertCondition.class);
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(alertService.inGracePeriod(ArgumentMatchers.eq(alertCondition))).thenReturn(false);
        Mockito.when(alertCondition.runCheck()).thenReturn(new AbstractAlertCondition.NegativeCheckResult());
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isFalse();
        Mockito.verify(alertCondition, Mockito.times(1)).runCheck();
    }

    @Test
    public void testCheckTriggersFirstAlert() throws Exception {
        final Stream stream = Mockito.mock(Stream.class);
        final AlertCondition alertCondition = Mockito.mock(AlertCondition.class);
        Mockito.when(alertService.inGracePeriod(ArgumentMatchers.eq(alertCondition))).thenReturn(false);
        final AlertCondition.CheckResult positiveCheckResult = new AbstractAlertCondition.CheckResult(true, alertCondition, "Mocked positive CheckResult", Tools.nowUTC(), Collections.emptyList());
        Mockito.when(alertCondition.runCheck()).thenReturn(positiveCheckResult);
        final Alert alert = Mockito.mock(Alert.class);
        Mockito.when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.empty());
        Mockito.when(alertService.factory(ArgumentMatchers.eq(positiveCheckResult))).thenReturn(alert);
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();
        Mockito.when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.of(alert));
        Mockito.when(alertService.isResolved(alert)).thenReturn(false);
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();
        Mockito.verify(alertCondition, Mockito.times(2)).runCheck();
        Mockito.verify(alertNotificationsSender, Mockito.times(1)).send(positiveCheckResult, stream, alert, alertCondition);
        Mockito.verify(alertService, Mockito.never()).resolveAlert(alert);
    }

    @Test
    public void testCheckTriggersAlertIfPreviousIsResolved() throws Exception {
        final Stream stream = Mockito.mock(Stream.class);
        final AlertCondition alertCondition = Mockito.mock(AlertCondition.class);
        Mockito.when(alertService.inGracePeriod(ArgumentMatchers.eq(alertCondition))).thenReturn(false);
        final AlertCondition.CheckResult positiveCheckResult = new AbstractAlertCondition.CheckResult(true, alertCondition, "Mocked positive CheckResult", Tools.nowUTC(), Collections.emptyList());
        Mockito.when(alertCondition.runCheck()).thenReturn(positiveCheckResult);
        final Alert alert = Mockito.mock(Alert.class);
        final Alert previousAlert = Mockito.mock(Alert.class);
        Mockito.when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.of(previousAlert));
        Mockito.when(alertService.isResolved(previousAlert)).thenReturn(true);
        Mockito.when(alertService.factory(ArgumentMatchers.eq(positiveCheckResult))).thenReturn(alert);
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();
        Mockito.verify(alertCondition, Mockito.times(1)).runCheck();
        Mockito.verify(alertNotificationsSender, Mockito.times(1)).send(positiveCheckResult, stream, alert, alertCondition);
        Mockito.verify(alertService, Mockito.never()).resolveAlert(alert);
    }

    @Test
    public void testCheckStatefulAlertNotifications() throws Exception {
        final Stream stream = Mockito.mock(Stream.class);
        final AlertCondition alertCondition = Mockito.mock(AlertCondition.class);
        Mockito.when(alertCondition.shouldRepeatNotifications()).thenReturn(false);
        Mockito.when(alertService.inGracePeriod(ArgumentMatchers.eq(alertCondition))).thenReturn(false);
        final AlertCondition.CheckResult positiveCheckResult = new AbstractAlertCondition.CheckResult(true, alertCondition, "Mocked positive CheckResult", Tools.nowUTC(), Collections.emptyList());
        Mockito.when(alertCondition.runCheck()).thenReturn(positiveCheckResult);
        final Alert alert = Mockito.mock(Alert.class);
        Mockito.when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.empty());
        Mockito.when(alertService.factory(ArgumentMatchers.eq(positiveCheckResult))).thenReturn(alert);
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();
        Mockito.when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.of(alert));
        Mockito.when(alertService.isResolved(alert)).thenReturn(false);
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();
        Mockito.verify(alertCondition, Mockito.times(2)).runCheck();
        Mockito.verify(alertNotificationsSender, Mockito.times(1)).send(positiveCheckResult, stream, alert, alertCondition);
        Mockito.verify(alertService, Mockito.never()).resolveAlert(alert);
    }

    @Test
    public void testCheckRepeatedAlertNotifications() throws Exception {
        final Stream stream = Mockito.mock(Stream.class);
        final AlertCondition alertCondition = Mockito.mock(AlertCondition.class);
        Mockito.when(alertCondition.shouldRepeatNotifications()).thenReturn(true);
        Mockito.when(alertService.inGracePeriod(ArgumentMatchers.eq(alertCondition))).thenReturn(false);
        final AlertCondition.CheckResult positiveCheckResult = new AbstractAlertCondition.CheckResult(true, alertCondition, "Mocked positive CheckResult", Tools.nowUTC(), Collections.emptyList());
        Mockito.when(alertCondition.runCheck()).thenReturn(positiveCheckResult);
        final Alert alert = Mockito.mock(Alert.class);
        Mockito.when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.empty());
        Mockito.when(alertService.factory(ArgumentMatchers.eq(positiveCheckResult))).thenReturn(alert);
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();
        Mockito.when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.of(alert));
        Mockito.when(alertService.isResolved(alert)).thenReturn(false);
        Mockito.when(alertService.shouldRepeatNotifications(alertCondition, alert)).thenReturn(true);
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();
        Mockito.verify(alertCondition, Mockito.times(2)).runCheck();
        Mockito.verify(alertNotificationsSender, Mockito.times(2)).send(positiveCheckResult, stream, alert, alertCondition);
        Mockito.verify(alertService, Mockito.never()).resolveAlert(alert);
    }

    @Test
    public void testAlertIsResolved() throws Exception {
        final AlertCondition alertCondition = Mockito.mock(AlertCondition.class);
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(alertService.inGracePeriod(ArgumentMatchers.eq(alertCondition))).thenReturn(false);
        final AlertCondition.CheckResult positiveCheckResult = new AbstractAlertCondition.CheckResult(true, alertCondition, "Mocked positive CheckResult", Tools.nowUTC(), Collections.emptyList());
        Mockito.when(alertCondition.runCheck()).thenReturn(positiveCheckResult);
        final Alert alert = Mockito.mock(Alert.class);
        Mockito.when(alertService.factory(ArgumentMatchers.eq(positiveCheckResult))).thenReturn(alert);
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();
        Mockito.verify(alertCondition, Mockito.times(1)).runCheck();
        Mockito.verify(alertService, Mockito.never()).resolveAlert(alert);
        Mockito.when(alertCondition.runCheck()).thenReturn(new AbstractAlertCondition.NegativeCheckResult());
        Mockito.when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.of(alert));
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isFalse();
        Mockito.verify(alertCondition, Mockito.times(2)).runCheck();
        Mockito.verify(alertService, Mockito.times(1)).resolveAlert(alert);
    }
}

