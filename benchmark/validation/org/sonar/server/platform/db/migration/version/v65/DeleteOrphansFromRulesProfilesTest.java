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
package org.sonar.server.platform.db.migration.version.v65;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class DeleteOrphansFromRulesProfilesTest {
    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(DeleteOrphansFromRulesProfilesTest.class, "initial.sql");

    private DeleteOrphansFromRulesProfiles underTest = new DeleteOrphansFromRulesProfiles(db.database());

    @Test
    public void migration() throws SQLException {
        long rulesProfileId1 = insertRulesProfile("RP_UUID_1", "Sonar Way", "Java", 1000L, true);
        long rulesProfileId2 = insertRulesProfile("RP_UUID_2", "Sonar Way", "JavaScript", 1000L, false);
        insertRulesProfile("RP_UUID_3", "Sonar Way", "PL/SQL", 1000L, false);
        insertRulesProfile("RP_UUID_4", "Sonar Way", "Cobol", 1000L, false);
        insertRulesProfile("RP_UUID_5", "Sonar Way", "Cobol", 1000L, false);
        insertRulesProfile("RP_UUID_6", "Sonar Way", "Cobol", 1000L, true);
        insertOrgQProfile("ORG_QP_UUID_1", "ORG_UUID_1", "RP_UUID_1");
        insertOrgQProfile("ORG_QP_UUID_2", "ORG_UUID_1", "RP_UUID_2");
        long activeRuleId1 = insertActiveRule(rulesProfileId1, 1);
        long activeRuleId2 = insertActiveRule(rulesProfileId2, 1);
        insertActiveRule((-1), 1);
        insertActiveRule((-2), 1);
        long param1 = insertActiveRuleParameter(activeRuleId1, 1);
        long param2 = insertActiveRuleParameter(activeRuleId1, 2);
        long param3 = insertActiveRuleParameter(activeRuleId2, 2);
        insertActiveRuleParameter((-1), 1);
        insertActiveRuleParameter((-2), 1);
        insertQProfileChange("QPC_UUID1", "RP_UUID_1", "A");
        insertQProfileChange("QPC_UUID2", "RP_UUID_2", "B");
        insertQProfileChange("QPC_UUID3", "RP_UUID_3", "A");
        insertQProfileChange("QPC_UUID4", "RP_UUID_4", "A");
        underTest.execute();
        assertThat(selectRulesProfiles()).containsExactlyInAnyOrder("RP_UUID_1", "RP_UUID_2");
        assertThat(selectActiveRules()).containsExactlyInAnyOrder(activeRuleId1, activeRuleId2);
        assertThat(selectActiveRuleParameters()).containsExactlyInAnyOrder(param1, param2, param3);
        assertThat(selectQProfileChanges()).containsExactlyInAnyOrder("QPC_UUID1", "QPC_UUID2");
    }

    @Test
    public void delete_rules_profiles_without_reference_in_qprofiles() throws SQLException {
        insertRulesProfile("RP_UUID_1", "Sonar Way", "Java", 1000L, true);
        insertRulesProfile("RP_UUID_2", "Sonar Way", "JavaScript", 1000L, false);
        insertRulesProfile("RP_UUID_5", "Sonar Way", "Cobol", 1000L, false);
        insertRulesProfile("RP_UUID_6", "Sonar Way", "Cobol", 1000L, true);
        insertOrgQProfile("ORG_QP_UUID_1", "ORG_UUID_1", "RP_UUID_5");
        insertOrgQProfile("ORG_QP_UUID_2", "ORG_UUID_1", "RP_UUID_6");
        underTest.execute();
        assertThat(selectRulesProfiles()).containsExactlyInAnyOrder("RP_UUID_5", "RP_UUID_6");
    }

    @Test
    public void delete_active_rules_without_rules_profiles() throws SQLException {
        long rulesProfileId1 = insertRulesProfile("RP_UUID_1", "Sonar Way", "Java", 1000L, true);
        long rulesProfileId2 = insertRulesProfile("RP_UUID_2", "Sonar Way", "JavaScript", 1000L, false);
        insertOrgQProfile("ORG_QP_UUID_1", "ORG_UUID_1", "RP_UUID_1");
        insertOrgQProfile("ORG_QP_UUID_2", "ORG_UUID_1", "RP_UUID_2");
        long activeRule1 = insertActiveRule(rulesProfileId1, 1);
        long activeRule2 = insertActiveRule(rulesProfileId2, 1);
        insertActiveRule((-1), 1);
        insertActiveRule((-2), 1);
        underTest.execute();
        assertThat(selectActiveRules()).containsExactlyInAnyOrder(activeRule1, activeRule2);
    }

    @Test
    public void delete_active_rule_parameters_without_active_rules() throws SQLException {
        long rulesProfileId1 = insertRulesProfile("RP_UUID_1", "Sonar Way", "Java", 1000L, true);
        long rulesProfileId2 = insertRulesProfile("RP_UUID_2", "Sonar Way", "JavaScript", 1000L, false);
        insertOrgQProfile("ORG_QP_UUID_1", "ORG_UUID_1", "RP_UUID_1");
        insertOrgQProfile("ORG_QP_UUID_2", "ORG_UUID_1", "RP_UUID_2");
        long activeRuleId1 = insertActiveRule(rulesProfileId1, 1);
        long activeRuleId2 = insertActiveRule(rulesProfileId2, 1);
        long param1 = insertActiveRuleParameter(activeRuleId1, 1);
        long param2 = insertActiveRuleParameter(activeRuleId1, 2);
        long param3 = insertActiveRuleParameter(activeRuleId2, 2);
        insertActiveRuleParameter((-1), 1);
        insertActiveRuleParameter((-2), 1);
        underTest.execute();
        assertThat(selectActiveRuleParameters()).containsExactlyInAnyOrder(param1, param2, param3);
    }

    @Test
    public void delete_qprofile_changes_without_rules_profiles() throws SQLException {
        long rulesProfileId1 = insertRulesProfile("RP_UUID_1", "Sonar Way", "Java", 1000L, true);
        long rulesProfileId2 = insertRulesProfile("RP_UUID_2", "Sonar Way", "JavaScript", 1000L, false);
        insertOrgQProfile("ORG_QP_UUID_1", "ORG_UUID_1", "RP_UUID_1");
        insertOrgQProfile("ORG_QP_UUID_2", "ORG_UUID_1", "RP_UUID_2");
        insertQProfileChange("QPC_UUID1", "RP_UUID_1", "A");
        insertQProfileChange("QPC_UUID2", "RP_UUID_2", "B");
        insertQProfileChange("QPC_UUID3", "RP_UUID_3", "A");
        insertQProfileChange("QPC_UUID4", "RP_UUID_4", "A");
        underTest.execute();
        assertThat(selectQProfileChanges()).containsExactlyInAnyOrder("QPC_UUID1", "QPC_UUID2");
    }

    @Test
    public void reentrant_migration() throws SQLException {
        long rulesProfileId1 = insertRulesProfile("RP_UUID_1", "Sonar Way", "Java", 1000L, true);
        long rulesProfileId2 = insertRulesProfile("RP_UUID_2", "Sonar Way", "JavaScript", 1000L, false);
        insertRulesProfile("RP_UUID_3", "Sonar Way", "PL/SQL", 1000L, false);
        insertRulesProfile("RP_UUID_4", "Sonar Way", "Cobol", 1000L, false);
        insertRulesProfile("RP_UUID_5", "Sonar Way", "Cobol", 1000L, false);
        insertRulesProfile("RP_UUID_6", "Sonar Way", "Cobol", 1000L, true);
        insertOrgQProfile("ORG_QP_UUID_1", "ORG_UUID_1", "RP_UUID_1");
        insertOrgQProfile("ORG_QP_UUID_2", "ORG_UUID_1", "RP_UUID_2");
        long activeRuleId1 = insertActiveRule(rulesProfileId1, 1);
        long activeRuleId2 = insertActiveRule(rulesProfileId2, 1);
        insertActiveRule((-1), 1);
        insertActiveRule((-2), 1);
        insertActiveRuleParameter(activeRuleId1, 1);
        insertActiveRuleParameter(activeRuleId1, 2);
        insertActiveRuleParameter(activeRuleId2, 2);
        insertActiveRuleParameter((-1), 1);
        insertActiveRuleParameter((-2), 1);
        insertQProfileChange("QPC_UUID1", "RP_UUID_1", "A");
        insertQProfileChange("QPC_UUID2", "RP_UUID_2", "B");
        insertQProfileChange("QPC_UUID3", "RP_UUID_3", "A");
        insertQProfileChange("QPC_UUID4", "RP_UUID_4", "A");
        underTest.execute();
        underTest.execute();
        assertThat(countRows("rules_profiles")).isEqualTo(2);
        assertThat(countRows("active_rules")).isEqualTo(2);
        assertThat(countRows("active_rule_parameters")).isEqualTo(3);
        assertThat(countRows("qprofile_changes")).isEqualTo(2);
    }
}

