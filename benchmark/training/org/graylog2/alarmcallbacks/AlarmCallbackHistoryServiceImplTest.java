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
package org.graylog2.alarmcallbacks;


import com.lordofthejars.nosqlunit.annotation.CustomComparisonStrategy;
import com.lordofthejars.nosqlunit.annotation.IgnorePropertyValue;
import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoFlexibleComparisonStrategy;
import java.util.Date;
import java.util.List;
import org.graylog2.alerts.Alert;
import org.graylog2.database.MongoDBServiceTest;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.rest.models.alarmcallbacks.AlarmCallbackError;
import org.graylog2.rest.models.alarmcallbacks.AlarmCallbackSuccess;
import org.joda.time.DateTime;
import org.junit.Test;


@CustomComparisonStrategy(comparisonStrategy = MongoFlexibleComparisonStrategy.class)
public class AlarmCallbackHistoryServiceImplTest extends MongoDBServiceTest {
    private AlarmCallbackHistoryService alarmCallbackHistoryService;

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testGetForAlertIdShouldReturnEmptyListWhenCollectionIsEmpty() throws Exception {
        final String nonExistentAlertId = "nonexistent";
        final List<AlarmCallbackHistory> result = this.alarmCallbackHistoryService.getForAlertId(nonExistentAlertId);
        assertThat(result).isEmpty();
    }

    @Test
    @UsingDataSet
    public void testGetPerAlertIdShouldReturnPopulatedListForExistingAlert() throws Exception {
        final String existingAlertId = "55ae105afbeaf123a6ddfc1b";
        final List<AlarmCallbackHistory> result = this.alarmCallbackHistoryService.getForAlertId(existingAlertId);
        assertThat(result).isNotNull().isNotEmpty().hasSize(2);
        assertThat(result.get(0).result()).isInstanceOf(AlarmCallbackSuccess.class);
        assertThat(result.get(1).result()).isInstanceOf(AlarmCallbackError.class);
    }

    @Test
    public void testSuccess() throws Exception {
        final AlarmCallbackConfiguration alarmCallbackConfiguration = mockAlarmCallbackConfiguration(new Date());
        final Alert alert = mockAlert();
        final AlertCondition alertCondition = mockAlertCondition();
        final AlarmCallbackHistory alarmCallbackHistory = this.alarmCallbackHistoryService.success(alarmCallbackConfiguration, alert, alertCondition);
        verifyAlarmCallbackHistory(alarmCallbackHistory, alert, alertCondition);
        assertThat(alarmCallbackHistory.result()).isNotNull().isInstanceOf(AlarmCallbackSuccess.class);
        assertThat(alarmCallbackHistory.result().type()).isEqualTo("success");
    }

    @Test
    public void testError() throws Exception {
        final AlarmCallbackConfiguration alarmCallbackConfiguration = mockAlarmCallbackConfiguration(new Date());
        final Alert alert = mockAlert();
        final AlertCondition alertCondition = mockAlertCondition();
        final String errorMessage = "Dummy Error Message";
        final AlarmCallbackHistory alarmCallbackHistory = this.alarmCallbackHistoryService.error(alarmCallbackConfiguration, alert, alertCondition, errorMessage);
        verifyAlarmCallbackHistory(alarmCallbackHistory, alert, alertCondition);
        assertThat(alarmCallbackHistory.result()).isNotNull().isInstanceOf(AlarmCallbackError.class);
        assertThat(alarmCallbackHistory.result().type()).isEqualTo("error");
        final AlarmCallbackError result = ((AlarmCallbackError) (alarmCallbackHistory.result()));
        assertThat(result.error()).isEqualTo(errorMessage);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    @ShouldMatchDataSet
    @IgnorePropertyValue(properties = { "created_at", "_id" })
    public void testSaveForDummySuccess() throws Exception {
        final Date createdAt = DateTime.parse("2015-07-20T09:49:02.503Z").toDate();
        final AlarmCallbackConfiguration alarmCallbackConfiguration = mockAlarmCallbackConfiguration(createdAt);
        final Alert alert = mockAlert();
        final AlertCondition alertCondition = mockAlertCondition();
        final AlarmCallbackHistory success = this.alarmCallbackHistoryService.success(alarmCallbackConfiguration, alert, alertCondition);
        this.alarmCallbackHistoryService.save(success);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    @ShouldMatchDataSet
    @IgnorePropertyValue(properties = { "created_at", "_id" })
    public void testSaveForDummyError() throws Exception {
        final Date createdAt = DateTime.parse("2015-07-20T09:49:02.503Z").toDate();
        final AlarmCallbackConfiguration alarmCallbackConfiguration = mockAlarmCallbackConfiguration(createdAt);
        final Alert alert = mockAlert();
        final AlertCondition alertCondition = mockAlertCondition();
        final String errorMessage = "Dummy Error Message";
        final AlarmCallbackHistory error = this.alarmCallbackHistoryService.error(alarmCallbackConfiguration, alert, alertCondition, errorMessage);
        this.alarmCallbackHistoryService.save(error);
    }
}

