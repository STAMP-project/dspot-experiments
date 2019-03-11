/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.task.projectanalysis.measure.qualitygatedetails;


import Condition.Operator.GREATER_THAN;
import Measure.Level;
import Metric.MetricType;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import org.junit.Test;
import org.sonar.ce.task.projectanalysis.qualitygate.Condition;
import org.sonar.test.JsonAssert;


public class QualityGateDetailsDataTest {
    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_if_Level_arg_is_null() {
        new QualityGateDetailsData(null, Collections.emptyList(), false);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_if_Iterable_arg_is_null() {
        new QualityGateDetailsData(Level.OK, null, false);
    }

    @Test
    public void verify_json_when_there_is_no_condition() {
        String actualJson = toJson();
        JsonAssert.assertJson(actualJson).isSimilarTo(("{" + (("\"level\":\"OK\"," + "\"conditions\":[]") + "}")));
    }

    @Test
    public void verify_json_for_each_type_of_condition() {
        String value = "actualValue";
        Condition condition = new Condition(new org.sonar.ce.task.projectanalysis.metric.MetricImpl(1, "key1", "name1", MetricType.STRING), GREATER_THAN.getDbValue(), "errorTh");
        ImmutableList<EvaluatedCondition> evaluatedConditions = ImmutableList.of(new EvaluatedCondition(condition, Level.OK, value), new EvaluatedCondition(condition, Level.ERROR, value));
        String actualJson = toJson();
        JsonAssert.assertJson(actualJson).isSimilarTo(("{" + ((((((((((((((((("\"level\":\"OK\"," + "\"conditions\":[") + "  {") + "    \"metric\":\"key1\",") + "    \"op\":\"GT\",") + "    \"error\":\"errorTh\",") + "    \"actual\":\"actualValue\",") + "    \"level\":\"OK\"") + "  },") + "  {") + "    \"metric\":\"key1\",") + "    \"op\":\"GT\",") + "    \"error\":\"errorTh\",") + "    \"actual\":\"actualValue\",") + "    \"level\":\"ERROR\"") + "  }") + "]") + "}")));
    }

    @Test
    public void verify_json_for_condition_on_leak_metric() {
        String value = "actualValue";
        Condition condition = new Condition(new org.sonar.ce.task.projectanalysis.metric.MetricImpl(1, "new_key1", "name1", MetricType.STRING), GREATER_THAN.getDbValue(), "errorTh");
        ImmutableList<EvaluatedCondition> evaluatedConditions = ImmutableList.of(new EvaluatedCondition(condition, Level.OK, value), new EvaluatedCondition(condition, Level.ERROR, value));
        String actualJson = toJson();
        JsonAssert.assertJson(actualJson).isSimilarTo(("{" + ((((((((((((((((((("\"level\":\"OK\"," + "\"conditions\":[") + "  {") + "    \"metric\":\"new_key1\",") + "    \"op\":\"GT\",") + "    \"error\":\"errorTh\",") + "    \"actual\":\"actualValue\",") + "    \"period\":1,") + "    \"level\":\"OK\"") + "  },") + "  {") + "    \"metric\":\"new_key1\",") + "    \"op\":\"GT\",") + "    \"error\":\"errorTh\",") + "    \"actual\":\"actualValue\",") + "    \"period\":1,") + "    \"level\":\"ERROR\"") + "  }") + "]") + "}")));
    }

    @Test
    public void verify_json_for_small_leak() {
        String actualJson = toJson();
        JsonAssert.assertJson(actualJson).isSimilarTo("{\"ignoredConditions\": false}");
        String actualJson2 = toJson();
        JsonAssert.assertJson(actualJson2).isSimilarTo("{\"ignoredConditions\": true}");
    }
}

