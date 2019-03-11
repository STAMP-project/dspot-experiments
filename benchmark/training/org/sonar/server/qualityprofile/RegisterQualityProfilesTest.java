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
package org.sonar.server.qualityprofile;


import LoggerLevel.INFO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.resources.Language;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.AlwaysIncreasingSystem2;
import org.sonar.api.utils.log.LogTester;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.qualityprofile.QualityProfileTesting;
import org.sonar.db.qualityprofile.RulesProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.server.language.LanguageTesting;
import org.sonar.server.tester.UserSessionRule;


public class RegisterQualityProfilesTest {
    private static final Language FOO_LANGUAGE = LanguageTesting.newLanguage("foo");

    private static final Language BAR_LANGUAGE = LanguageTesting.newLanguage("bar");

    private System2 system2 = new AlwaysIncreasingSystem2();

    @Rule
    public DbTester db = DbTester.create(system2);

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public BuiltInQProfileRepositoryRule builtInQProfileRepositoryRule = new BuiltInQProfileRepositoryRule();

    @Rule
    public LogTester logTester = new LogTester();

    private DbClient dbClient = db.getDbClient();

    private RegisterQualityProfilesTest.DummyBuiltInQProfileInsert insert = new RegisterQualityProfilesTest.DummyBuiltInQProfileInsert();

    private RegisterQualityProfilesTest.DummyBuiltInQProfileUpdate update = new RegisterQualityProfilesTest.DummyBuiltInQProfileUpdate();

    private RegisterQualityProfiles underTest = new RegisterQualityProfiles(builtInQProfileRepositoryRule, dbClient, insert, update, Mockito.mock(BuiltInQualityProfilesUpdateListener.class), system2);

    @Test
    public void start_fails_if_BuiltInQProfileRepository_has_not_been_initialized() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("initialize must be called first");
        underTest.start();
    }

    @Test
    public void persist_built_in_profiles_that_are_not_persisted_yet() {
        BuiltInQProfile builtInQProfile = builtInQProfileRepositoryRule.add(RegisterQualityProfilesTest.FOO_LANGUAGE, "Sonar way");
        builtInQProfileRepositoryRule.initialize();
        underTest.start();
        assertThat(insert.callLogs).containsExactly(builtInQProfile);
        assertThat(update.callLogs).isEmpty();
        assertThat(logTester.logs(INFO)).contains("Register profile foo/Sonar way");
    }

    @Test
    public void dont_persist_built_in_profiles_that_are_already_persisted() {
        String name = "doh";
        BuiltInQProfile persistedBuiltIn = builtInQProfileRepositoryRule.add(RegisterQualityProfilesTest.FOO_LANGUAGE, name, true);
        BuiltInQProfile nonPersistedBuiltIn = builtInQProfileRepositoryRule.add(RegisterQualityProfilesTest.BAR_LANGUAGE, name, true);
        builtInQProfileRepositoryRule.initialize();
        insertRulesProfile(persistedBuiltIn);
        underTest.start();
        assertThat(insert.callLogs).containsExactly(nonPersistedBuiltIn);
        assertThat(update.callLogs).containsExactly(persistedBuiltIn);
    }

    @Test
    public void rename_custom_outdated_profiles_if_same_name_than_built_in_profile() {
        OrganizationDto org1 = db.organizations().insert(( org) -> org.setKey("org1"));
        OrganizationDto org2 = db.organizations().insert(( org) -> org.setKey("org2"));
        QProfileDto outdatedProfileInOrg1 = db.qualityProfiles().insert(org1, ( p) -> p.setIsBuiltIn(false).setLanguage(FOO_LANGUAGE.getKey()).setName("Sonar way"));
        QProfileDto outdatedProfileInOrg2 = db.qualityProfiles().insert(org2, ( p) -> p.setIsBuiltIn(false).setLanguage(FOO_LANGUAGE.getKey()).setName("Sonar way"));
        builtInQProfileRepositoryRule.add(RegisterQualityProfilesTest.FOO_LANGUAGE, "Sonar way", false);
        builtInQProfileRepositoryRule.initialize();
        underTest.start();
        assertThat(selectPersistedName(outdatedProfileInOrg1)).isEqualTo("Sonar way (outdated copy)");
        assertThat(selectPersistedName(outdatedProfileInOrg2)).isEqualTo("Sonar way (outdated copy)");
        assertThat(logTester.logs(INFO)).contains("Rename Quality profiles [foo/Sonar way] to [Sonar way (outdated copy)] in 2?organizations");
    }

    @Test
    public void update_built_in_profile_if_it_already_exists() {
        RulesProfileDto ruleProfile = QualityProfileTesting.newRuleProfileDto(( rp) -> rp.setIsBuiltIn(true).setName("Sonar way").setLanguage(FOO_LANGUAGE.getKey()));
        db.getDbClient().qualityProfileDao().insert(db.getSession(), ruleProfile);
        db.commit();
        BuiltInQProfile builtIn = builtInQProfileRepositoryRule.add(RegisterQualityProfilesTest.FOO_LANGUAGE, ruleProfile.getName(), false);
        builtInQProfileRepositoryRule.initialize();
        underTest.start();
        assertThat(insert.callLogs).isEmpty();
        assertThat(update.callLogs).containsExactly(builtIn);
        assertThat(logTester.logs(INFO)).contains("Update profile foo/Sonar way");
    }

    @Test
    public void update_default_built_in_quality_profile() {
        String orgUuid = UuidFactoryFast.getInstance().create();
        RulesProfileDto ruleProfileWithoutRule = QualityProfileTesting.newRuleProfileDto(( rp) -> rp.setIsBuiltIn(true).setName("Sonar way").setLanguage(FOO_LANGUAGE.getKey()));
        RulesProfileDto ruleProfileWithOneRule = QualityProfileTesting.newRuleProfileDto(( rp) -> rp.setIsBuiltIn(true).setName("Sonar way 2").setLanguage(FOO_LANGUAGE.getKey()));
        QProfileDto qProfileWithoutRule = QualityProfileTesting.newQualityProfileDto().setIsBuiltIn(true).setLanguage(RegisterQualityProfilesTest.FOO_LANGUAGE.getKey()).setOrganizationUuid(orgUuid).setRulesProfileUuid(ruleProfileWithoutRule.getKee());
        QProfileDto qProfileWithOneRule = QualityProfileTesting.newQualityProfileDto().setIsBuiltIn(true).setLanguage(RegisterQualityProfilesTest.FOO_LANGUAGE.getKey()).setOrganizationUuid(orgUuid).setRulesProfileUuid(ruleProfileWithOneRule.getKee());
        db.qualityProfiles().insert(qProfileWithoutRule, qProfileWithOneRule);
        db.qualityProfiles().setAsDefault(qProfileWithoutRule);
        RuleDefinitionDto ruleDefinition = db.rules().insert();
        db.qualityProfiles().activateRule(qProfileWithOneRule, ruleDefinition);
        db.commit();
        builtInQProfileRepositoryRule.add(RegisterQualityProfilesTest.FOO_LANGUAGE, ruleProfileWithoutRule.getName(), true);
        builtInQProfileRepositoryRule.add(RegisterQualityProfilesTest.FOO_LANGUAGE, ruleProfileWithOneRule.getName(), false);
        builtInQProfileRepositoryRule.initialize();
        underTest.start();
        logTester.logs(INFO).contains(String.format("Default built-in quality profile for language [foo] has been updated from [%s] to [%s] since previous default does not have active rules.", qProfileWithoutRule.getName(), qProfileWithOneRule.getName()));
        assertThat(selectUuidOfDefaultProfile(RegisterQualityProfilesTest.FOO_LANGUAGE.getKey())).isPresent().get().isEqualTo(qProfileWithOneRule.getKee());
    }

    private static class DummyBuiltInQProfileInsert implements BuiltInQProfileInsert {
        private final List<BuiltInQProfile> callLogs = new ArrayList<>();

        @Override
        public void create(DbSession dbSession, DbSession batchDbSession, BuiltInQProfile builtIn) {
            callLogs.add(builtIn);
        }
    }

    private static class DummyBuiltInQProfileUpdate implements BuiltInQProfileUpdate {
        private final List<BuiltInQProfile> callLogs = new ArrayList<>();

        @Override
        public List<ActiveRuleChange> update(DbSession dbSession, BuiltInQProfile builtIn, RulesProfileDto ruleProfile) {
            callLogs.add(builtIn);
            return Collections.emptyList();
        }
    }
}

