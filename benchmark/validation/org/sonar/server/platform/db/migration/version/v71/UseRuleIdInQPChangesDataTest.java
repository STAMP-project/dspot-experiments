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
package org.sonar.server.platform.db.migration.version.v71;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sonar.db.CoreDbTester;


@RunWith(DataProviderRunner.class)
public class UseRuleIdInQPChangesDataTest {
    public static final String RULE_ID_DATA_FIELD = "ruleId";

    public static final String RULE_KEY_DATA_FIELD = "ruleKey";

    @Rule
    public final CoreDbTester dbTester = CoreDbTester.createForSchema(UseRuleIdInQPChangesDataTest.class, "rules_and_qprofile_changes.sql");

    private UseRuleIdInQPChangesData underTest = new UseRuleIdInQPChangesData(dbTester.database());

    @Test
    public void no_effect_if_tables_are_empty() throws SQLException {
        underTest.execute();
    }

    @Test
    public void qpChange_without_ruleKey_is_unchanged() throws SQLException {
        String c1Data = insertQPChange("c1");
        String c2Data = insertQPChange("c2", "random", "data", "for", "value");
        underTest.execute();
        assertThat(reaQPChangeData("c1")).isEqualTo(c1Data);
        assertThat(reaQPChangeData("c2")).isEqualTo(c2Data);
    }

    @Test
    public void qpChange_with_ruleKey_of_other_case_is_unchanged() throws SQLException {
        int ruleId1 = insertRule("foo", "bar");
        String c1Data = insertQPChange("c1", "RULEKEY", "notARuleKey");
        String c2Data = insertQPChange("c2", "RULeKey", "foo:bar");
        underTest.execute();
        assertThat(reaQPChangeData("c1")).isEqualTo(c1Data);
        assertThat(reaQPChangeData("c2")).isEqualTo(c2Data);
    }

    @Test
    public void qpChange_with_ruleKey_of_existing_rule_is_replaced_with_ruleId() throws SQLException {
        int ruleId1 = insertRule("foo", "bar");
        int ruleId2 = insertRule("foo2", "bar");
        int ruleId3 = insertRule("foo", "bar2");
        insertQPChange("c1", UseRuleIdInQPChangesDataTest.RULE_KEY_DATA_FIELD, "foo:bar");
        insertQPChange("c2", "otherDataKey", "otherDataData", UseRuleIdInQPChangesDataTest.RULE_KEY_DATA_FIELD, "foo2:bar");
        insertQPChange("c3", UseRuleIdInQPChangesDataTest.RULE_KEY_DATA_FIELD, "foo:bar2", "otherDataKey2", "otherDataData2");
        underTest.execute();
        assertThat(reaQPChangeData("c1")).doesNotContain(UseRuleIdInQPChangesDataTest.RULE_KEY_DATA_FIELD).contains(("ruleId=" + ruleId1));
        assertThat(reaQPChangeData("c2")).contains("otherDataKey=otherDataData").doesNotContain(UseRuleIdInQPChangesDataTest.RULE_KEY_DATA_FIELD).contains(("ruleId=" + ruleId2));
        assertThat(reaQPChangeData("c3")).contains("otherDataKey2=otherDataData2").doesNotContain(UseRuleIdInQPChangesDataTest.RULE_KEY_DATA_FIELD).contains(("ruleId=" + ruleId3));
    }

    @Test
    public void qpChange_with_ruleId_is_unchanged() throws SQLException {
        int ruleId1 = insertRule("foo", "bar");
        String c1Data = insertQPChange("c1", UseRuleIdInQPChangesDataTest.RULE_ID_DATA_FIELD, "notAnIntButIgnored");
        String c2Data = insertQPChange("c2", UseRuleIdInQPChangesDataTest.RULE_ID_DATA_FIELD, String.valueOf(ruleId1));
        underTest.execute();
        assertThat(reaQPChangeData("c1")).isEqualTo(c1Data);
        assertThat(reaQPChangeData("c2")).isEqualTo(c2Data);
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        int ruleId1 = insertRule("foo", "bar");
        insertQPChange("c1", UseRuleIdInQPChangesDataTest.RULE_KEY_DATA_FIELD, "foo:bar");
        String c2Data = insertQPChange("c2", "RULEKEY", "notARuleKey");
        insertQPChange("c3", UseRuleIdInQPChangesDataTest.RULE_KEY_DATA_FIELD, "nonExistingRuleKey");
        underTest.execute();
        assertThat(reaQPChangeData("c1")).doesNotContain(UseRuleIdInQPChangesDataTest.RULE_KEY_DATA_FIELD).contains(("ruleId=" + ruleId1));
        assertThat(reaQPChangeData("c2")).isEqualTo(c2Data);
        assertThat(reaQPChangeData("c3")).isEmpty();
        underTest.execute();
        assertThat(reaQPChangeData("c1")).doesNotContain(UseRuleIdInQPChangesDataTest.RULE_KEY_DATA_FIELD).contains(("ruleId=" + ruleId1));
        assertThat(reaQPChangeData("c2")).isEqualTo(c2Data);
        assertThat(reaQPChangeData("c3")).isEmpty();
    }
}

