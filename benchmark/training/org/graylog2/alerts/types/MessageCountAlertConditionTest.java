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
import MessageCountAlertCondition.ThresholdType;
import MessageCountAlertCondition.ThresholdType.MORE;
import java.util.Locale;
import java.util.Map;
import org.graylog2.alerts.AlertConditionTest;
import org.graylog2.plugin.alarms.AlertCondition;
import org.junit.Assert;
import org.junit.Test;


public class MessageCountAlertConditionTest extends AlertConditionTest {
    private final int threshold = 100;

    @Test
    public void testConstructor() throws Exception {
        final Map<String, Object> parameters = getParametersMap(0, 0, MORE, 0);
        final MessageCountAlertCondition messageCountAlertCondition = getMessageCountAlertCondition(parameters, AlertConditionTest.alertConditionTitle);
        Assert.assertNotNull(messageCountAlertCondition);
        Assert.assertNotNull(messageCountAlertCondition.getDescription());
        final String thresholdType = ((String) (messageCountAlertCondition.getParameters().get("threshold_type")));
        Assert.assertEquals(thresholdType, thresholdType.toUpperCase(Locale.ENGLISH));
    }

    /* Ensure MessageCountAlertCondition objects created before 2.2.0 and having a lowercase threshold_type,
    get converted to uppercase for consistency with new created alert conditions.
     */
    @Test
    public void testConstructorOldObjects() throws Exception {
        final Map<String, Object> parameters = getParametersMap(0, 0, MORE, 0);
        parameters.put("threshold_type", MORE.toString().toLowerCase(Locale.ENGLISH));
        final MessageCountAlertCondition messageCountAlertCondition = getMessageCountAlertCondition(parameters, AlertConditionTest.alertConditionTitle);
        final String thresholdType = ((String) (messageCountAlertCondition.getParameters().get("threshold_type")));
        Assert.assertEquals(thresholdType, thresholdType.toUpperCase(Locale.ENGLISH));
    }

    @Test
    public void testRunCheckMorePositive() throws Exception {
        final MessageCountAlertCondition.ThresholdType type = ThresholdType.MORE;
        final MessageCountAlertCondition messageCountAlertCondition = getConditionWithParameters(type, threshold);
        searchCountShouldReturn(((threshold) + 1));
        // AlertCondition was never triggered before
        final AlertCondition.CheckResult result = messageCountAlertCondition.runCheck();
        assertTriggered(messageCountAlertCondition, result);
    }

    @Test
    public void testRunCheckLessPositive() throws Exception {
        final MessageCountAlertCondition.ThresholdType type = ThresholdType.LESS;
        final MessageCountAlertCondition messageCountAlertCondition = getConditionWithParameters(type, threshold);
        searchCountShouldReturn(((threshold) - 1));
        final AlertCondition.CheckResult result = messageCountAlertCondition.runCheck();
        assertTriggered(messageCountAlertCondition, result);
    }

    @Test
    public void testRunCheckMoreNegative() throws Exception {
        final MessageCountAlertCondition.ThresholdType type = ThresholdType.MORE;
        final MessageCountAlertCondition messageCountAlertCondition = getConditionWithParameters(type, threshold);
        searchCountShouldReturn(threshold);
        final AlertCondition.CheckResult result = messageCountAlertCondition.runCheck();
        assertNotTriggered(result);
    }

    @Test
    public void testRunCheckLessNegative() throws Exception {
        final MessageCountAlertCondition.ThresholdType type = ThresholdType.LESS;
        final MessageCountAlertCondition messageCountAlertCondition = getConditionWithParameters(type, threshold);
        searchCountShouldReturn(threshold);
        final AlertCondition.CheckResult result = messageCountAlertCondition.runCheck();
        assertNotTriggered(result);
    }
}

