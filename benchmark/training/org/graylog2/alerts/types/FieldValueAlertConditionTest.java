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
package org.graylog2.alerts.types;


import AlertCondition.CheckResult;
import FieldValueAlertCondition.CheckType;
import FieldValueAlertCondition.CheckType.MAX;
import FieldValueAlertCondition.ThresholdType.HIGHER;
import FieldValueAlertCondition.ThresholdType.LOWER;
import java.util.Map;
import org.graylog2.alerts.AlertConditionTest;
import org.graylog2.plugin.alarms.AlertCondition;
import org.junit.Assert;
import org.junit.Test;


public class FieldValueAlertConditionTest extends AlertConditionTest {
    private static final String alertConditionTitle = "Test Alert Condition";

    @Test
    public void testConstructor() throws Exception {
        Map<String, Object> parameters = getParametersMap(0, 0, HIGHER, MAX, 0, "response_time");
        final FieldValueAlertCondition fieldValueAlertCondition = getTestInstance(FieldValueAlertCondition.class, parameters, FieldValueAlertConditionTest.alertConditionTitle);
        Assert.assertNotNull(fieldValueAlertCondition);
        Assert.assertNotNull(fieldValueAlertCondition.getDescription());
    }

    @Test
    public void testRunCheckHigherPositive() throws Exception {
        for (FieldValueAlertCondition.CheckType checkType : CheckType.values()) {
            final double threshold = 50.0;
            final double higherThanThreshold = threshold + 10;
            final FieldValueAlertCondition fieldValueAlertCondition = getTestInstance(FieldValueAlertCondition.class, getParametersMap(0, 0, HIGHER, checkType, threshold, "response_time"), FieldValueAlertConditionTest.alertConditionTitle);
            fieldStatsShouldReturn(getFieldStatsResult(checkType, higherThanThreshold));
            AlertCondition.CheckResult result = fieldValueAlertCondition.runCheck();
            assertTriggered(fieldValueAlertCondition, result);
        }
    }

    @Test
    public void testRunCheckHigherNegative() throws Exception {
        for (FieldValueAlertCondition.CheckType checkType : CheckType.values()) {
            final double threshold = 50.0;
            final double lowerThanThreshold = threshold - 10;
            FieldValueAlertCondition fieldValueAlertCondition = getFieldValueAlertCondition(getParametersMap(0, 0, HIGHER, checkType, threshold, "response_time"), FieldValueAlertConditionTest.alertConditionTitle);
            fieldStatsShouldReturn(getFieldStatsResult(checkType, lowerThanThreshold));
            AlertCondition.CheckResult result = fieldValueAlertCondition.runCheck();
            assertNotTriggered(result);
        }
    }

    @Test
    public void testRunCheckLowerPositive() throws Exception {
        for (FieldValueAlertCondition.CheckType checkType : CheckType.values()) {
            final double threshold = 50.0;
            final double lowerThanThreshold = threshold - 10;
            FieldValueAlertCondition fieldValueAlertCondition = getFieldValueAlertCondition(getParametersMap(0, 0, LOWER, checkType, threshold, "response_time"), FieldValueAlertConditionTest.alertConditionTitle);
            fieldStatsShouldReturn(getFieldStatsResult(checkType, lowerThanThreshold));
            AlertCondition.CheckResult result = fieldValueAlertCondition.runCheck();
            assertTriggered(fieldValueAlertCondition, result);
        }
    }

    @Test
    public void testRunCheckLowerNegative() throws Exception {
        for (FieldValueAlertCondition.CheckType checkType : CheckType.values()) {
            final double threshold = 50.0;
            final double higherThanThreshold = threshold + 10;
            FieldValueAlertCondition fieldValueAlertCondition = getFieldValueAlertCondition(getParametersMap(0, 0, LOWER, checkType, threshold, "response_time"), FieldValueAlertConditionTest.alertConditionTitle);
            fieldStatsShouldReturn(getFieldStatsResult(checkType, higherThanThreshold));
            AlertCondition.CheckResult result = fieldValueAlertCondition.runCheck();
            assertNotTriggered(result);
        }
    }
}

