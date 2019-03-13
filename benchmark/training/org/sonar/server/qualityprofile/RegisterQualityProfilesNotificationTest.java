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


import com.google.common.collect.Multimap;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.log.LogTester;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.qualityprofile.RulesProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.server.language.LanguageTesting;
import org.sonar.server.qualityprofile.index.ActiveRuleIndexer;
import org.sonar.server.rule.index.RuleIndex;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.util.TypeValidations;


public class RegisterQualityProfilesNotificationTest {
    private static final Random RANDOM = new SecureRandom();

    private System2 system2 = Mockito.mock(System2.class);

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

    private TypeValidations typeValidations = Mockito.mock(TypeValidations.class);

    private ActiveRuleIndexer activeRuleIndexer = Mockito.mock(ActiveRuleIndexer.class);

    private BuiltInQProfileInsert builtInQProfileInsert = new BuiltInQProfileInsertImpl(dbClient, system2, UuidFactoryFast.getInstance(), typeValidations, activeRuleIndexer);

    private RuleActivator ruleActivator = new RuleActivator(system2, dbClient, typeValidations, userSessionRule);

    private QProfileRules qProfileRules = new QProfileRulesImpl(dbClient, ruleActivator, Mockito.mock(RuleIndex.class), activeRuleIndexer);

    private BuiltInQProfileUpdate builtInQProfileUpdate = new BuiltInQProfileUpdateImpl(dbClient, ruleActivator, activeRuleIndexer);

    private BuiltInQualityProfilesUpdateListener builtInQualityProfilesNotification = Mockito.mock(BuiltInQualityProfilesUpdateListener.class);

    private RegisterQualityProfiles underTest = new RegisterQualityProfiles(builtInQProfileRepositoryRule, dbClient, builtInQProfileInsert, builtInQProfileUpdate, builtInQualityProfilesNotification, system2);

    @Test
    public void do_not_send_notification_on_new_profile() {
        String language = RegisterQualityProfilesNotificationTest.newLanguageKey();
        builtInQProfileRepositoryRule.add(LanguageTesting.newLanguage(language), "Sonar way");
        builtInQProfileRepositoryRule.initialize();
        underTest.start();
        Mockito.verifyZeroInteractions(builtInQualityProfilesNotification);
    }

    @Test
    public void do_not_send_notification_when_profile_is_not_updated() {
        String language = RegisterQualityProfilesNotificationTest.newLanguageKey();
        RuleDefinitionDto dbRule = db.rules().insert(( r) -> r.setLanguage(language));
        RulesProfileDto dbProfile = insertBuiltInProfile(language);
        activateRuleInDb(dbProfile, dbRule, RulePriority.MAJOR);
        addPluginProfile(dbProfile, dbRule);
        builtInQProfileRepositoryRule.initialize();
        underTest.start();
        Mockito.verifyZeroInteractions(builtInQualityProfilesNotification);
    }

    @Test
    public void send_notification_when_a_new_rule_is_activated() {
        String language = RegisterQualityProfilesNotificationTest.newLanguageKey();
        RuleDefinitionDto existingRule = db.rules().insert(( r) -> r.setLanguage(language));
        RulesProfileDto dbProfile = insertBuiltInProfile(language);
        activateRuleInDb(dbProfile, existingRule, RulePriority.MAJOR);
        RuleDefinitionDto newRule = db.rules().insert(( r) -> r.setLanguage(language));
        addPluginProfile(dbProfile, existingRule, newRule);
        builtInQProfileRepositoryRule.initialize();
        underTest.start();
        ArgumentCaptor<Multimap> captor = ArgumentCaptor.forClass(Multimap.class);
        Mockito.verify(builtInQualityProfilesNotification).onChange(captor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        Multimap<QProfileName, ActiveRuleChange> updatedProfiles = captor.<Multimap<QProfileName, ActiveRuleChange>>getValue();
        assertThat(updatedProfiles.keySet()).extracting(QProfileName::getName, QProfileName::getLanguage).containsExactlyInAnyOrder(tuple(dbProfile.getName(), dbProfile.getLanguage()));
        assertThat(updatedProfiles.values()).extracting(( value) -> value.getActiveRule().getRuleId(), ActiveRuleChange::getType).containsExactlyInAnyOrder(tuple(newRule.getId(), Type.ACTIVATED));
    }

    @Test
    public void send_notification_when_a_rule_is_deactivated() {
        String language = RegisterQualityProfilesNotificationTest.newLanguageKey();
        RuleDefinitionDto existingRule = db.rules().insert(( r) -> r.setLanguage(language));
        RulesProfileDto dbProfile = insertBuiltInProfile(language);
        activateRuleInDb(dbProfile, existingRule, RulePriority.MAJOR);
        addPluginProfile(dbProfile);
        builtInQProfileRepositoryRule.initialize();
        underTest.start();
        ArgumentCaptor<Multimap> captor = ArgumentCaptor.forClass(Multimap.class);
        Mockito.verify(builtInQualityProfilesNotification).onChange(captor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        Multimap<QProfileName, ActiveRuleChange> updatedProfiles = captor.<Multimap<QProfileName, ActiveRuleChange>>getValue();
        assertThat(updatedProfiles.keySet()).extracting(QProfileName::getName, QProfileName::getLanguage).containsExactlyInAnyOrder(tuple(dbProfile.getName(), dbProfile.getLanguage()));
        assertThat(updatedProfiles.values()).extracting(( value) -> value.getActiveRule().getRuleId(), ActiveRuleChange::getType).containsExactlyInAnyOrder(tuple(existingRule.getId(), Type.DEACTIVATED));
    }

    @Test
    public void send_a_single_notification_when_multiple_rules_are_activated() {
        String language = RegisterQualityProfilesNotificationTest.newLanguageKey();
        RuleDefinitionDto existingRule1 = db.rules().insert(( r) -> r.setLanguage(language));
        RuleDefinitionDto newRule1 = db.rules().insert(( r) -> r.setLanguage(language));
        RulesProfileDto dbProfile1 = insertBuiltInProfile(language);
        activateRuleInDb(dbProfile1, existingRule1, RulePriority.MAJOR);
        addPluginProfile(dbProfile1, existingRule1, newRule1);
        RuleDefinitionDto existingRule2 = db.rules().insert(( r) -> r.setLanguage(language));
        RuleDefinitionDto newRule2 = db.rules().insert(( r) -> r.setLanguage(language));
        RulesProfileDto dbProfile2 = insertBuiltInProfile(language);
        activateRuleInDb(dbProfile2, existingRule2, RulePriority.MAJOR);
        addPluginProfile(dbProfile2, existingRule2, newRule2);
        builtInQProfileRepositoryRule.initialize();
        underTest.start();
        ArgumentCaptor<Multimap> captor = ArgumentCaptor.forClass(Multimap.class);
        Mockito.verify(builtInQualityProfilesNotification).onChange(captor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        Multimap<QProfileName, ActiveRuleChange> updatedProfiles = captor.<Multimap<QProfileName, ActiveRuleChange>>getValue();
        assertThat(updatedProfiles.keySet()).extracting(QProfileName::getName, QProfileName::getLanguage).containsExactlyInAnyOrder(tuple(dbProfile1.getName(), dbProfile1.getLanguage()), tuple(dbProfile2.getName(), dbProfile2.getLanguage()));
        assertThat(updatedProfiles.values()).extracting(( value) -> value.getActiveRule().getRuleId(), ActiveRuleChange::getType).containsExactlyInAnyOrder(tuple(newRule1.getId(), Type.ACTIVATED), tuple(newRule2.getId(), Type.ACTIVATED));
    }

    @Test
    public void notification_does_not_include_inherited_profiles_when_rule_is_added() {
        String language = RegisterQualityProfilesNotificationTest.newLanguageKey();
        RuleDefinitionDto newRule = db.rules().insert(( r) -> r.setLanguage(language));
        OrganizationDto organization = db.organizations().insert();
        QProfileDto builtInQProfileDto = insertProfile(organization, ( orgQProfile) -> orgQProfile.setIsBuiltIn(true).setLanguage(language));
        QProfileDto childQProfileDto = insertProfile(organization, ( orgQProfile) -> orgQProfile.setIsBuiltIn(false).setLanguage(language).setParentKee(builtInQProfileDto.getKee()));
        addPluginProfile(builtInQProfileDto, newRule);
        builtInQProfileRepositoryRule.initialize();
        underTest.start();
        ArgumentCaptor<Multimap> captor = ArgumentCaptor.forClass(Multimap.class);
        Mockito.verify(builtInQualityProfilesNotification).onChange(captor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        Multimap<QProfileName, ActiveRuleChange> updatedProfiles = captor.<Multimap<QProfileName, ActiveRuleChange>>getValue();
        assertThat(updatedProfiles.keySet()).extracting(QProfileName::getName, QProfileName::getLanguage).containsExactlyInAnyOrder(tuple(builtInQProfileDto.getName(), builtInQProfileDto.getLanguage()));
        assertThat(updatedProfiles.values()).extracting(( value) -> value.getActiveRule().getRuleId(), ActiveRuleChange::getType).containsExactlyInAnyOrder(tuple(newRule.getId(), Type.ACTIVATED));
    }

    @Test
    public void notification_does_not_include_inherited_profiled_when_rule_is_changed() {
        String language = RegisterQualityProfilesNotificationTest.newLanguageKey();
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage(language).setSeverity(Severity.MINOR));
        OrganizationDto organization = db.organizations().insert();
        QProfileDto builtInProfile = insertProfile(organization, ( orgQProfile) -> orgQProfile.setIsBuiltIn(true).setLanguage(language));
        db.qualityProfiles().activateRule(builtInProfile, rule, ( ar) -> ar.setSeverity(Severity.MINOR));
        QProfileDto childProfile = insertProfile(organization, ( orgQProfile) -> orgQProfile.setIsBuiltIn(false).setLanguage(language).setParentKee(builtInProfile.getKee()));
        db.qualityProfiles().activateRule(childProfile, rule, ( ar) -> ar.setInheritance(ActiveRuleDto.INHERITED).setSeverity(Severity.MINOR));
        addPluginProfile(builtInProfile, rule);
        builtInQProfileRepositoryRule.initialize();
        db.commit();
        underTest.start();
        ArgumentCaptor<Multimap> captor = ArgumentCaptor.forClass(Multimap.class);
        Mockito.verify(builtInQualityProfilesNotification).onChange(captor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        Multimap<QProfileName, ActiveRuleChange> updatedProfiles = captor.<Multimap<QProfileName, ActiveRuleChange>>getValue();
        assertThat(updatedProfiles.keySet()).extracting(QProfileName::getName, QProfileName::getLanguage).containsExactlyInAnyOrder(tuple(builtInProfile.getName(), builtInProfile.getLanguage()));
        assertThat(updatedProfiles.values()).extracting(( value) -> value.getActiveRule().getRuleId(), ActiveRuleChange::getType).containsExactlyInAnyOrder(tuple(rule.getId(), Type.UPDATED));
    }

    @Test
    public void notification_does_not_include_inherited_profiles_when_rule_is_deactivated() {
        String language = RegisterQualityProfilesNotificationTest.newLanguageKey();
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage(language).setSeverity(Severity.MINOR));
        OrganizationDto organization = db.organizations().insert();
        QProfileDto builtInQProfileDto = insertProfile(organization, ( orgQProfile) -> orgQProfile.setIsBuiltIn(true).setLanguage(language));
        db.qualityProfiles().activateRule(builtInQProfileDto, rule);
        QProfileDto childQProfileDto = insertProfile(organization, ( orgQProfile) -> orgQProfile.setIsBuiltIn(false).setLanguage(language).setParentKee(builtInQProfileDto.getKee()));
        qProfileRules.activateAndCommit(db.getSession(), childQProfileDto, Collections.singleton(RuleActivation.create(rule.getId())));
        db.commit();
        addPluginProfile(builtInQProfileDto);
        builtInQProfileRepositoryRule.initialize();
        underTest.start();
        ArgumentCaptor<Multimap> captor = ArgumentCaptor.forClass(Multimap.class);
        Mockito.verify(builtInQualityProfilesNotification).onChange(captor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        Multimap<QProfileName, ActiveRuleChange> updatedProfiles = captor.<Multimap<QProfileName, ActiveRuleChange>>getValue();
        assertThat(updatedProfiles.keySet()).extracting(QProfileName::getName, QProfileName::getLanguage).containsExactlyInAnyOrder(tuple(builtInQProfileDto.getName(), builtInQProfileDto.getLanguage()));
        assertThat(updatedProfiles.values()).extracting(( value) -> value.getActiveRule().getRuleId(), ActiveRuleChange::getType).containsExactlyInAnyOrder(tuple(rule.getId(), Type.DEACTIVATED));
    }

    @Test
    public void notification_contains_send_start_and_end_date() {
        String language = RegisterQualityProfilesNotificationTest.newLanguageKey();
        RuleDefinitionDto existingRule = db.rules().insert(( r) -> r.setLanguage(language));
        RulesProfileDto dbProfile = insertBuiltInProfile(language);
        activateRuleInDb(dbProfile, existingRule, RulePriority.MAJOR);
        RuleDefinitionDto newRule = db.rules().insert(( r) -> r.setLanguage(language));
        addPluginProfile(dbProfile, existingRule, newRule);
        builtInQProfileRepositoryRule.initialize();
        long startDate = RegisterQualityProfilesNotificationTest.RANDOM.nextInt(5000);
        long endDate = startDate + (RegisterQualityProfilesNotificationTest.RANDOM.nextInt(5000));
        Mockito.when(system2.now()).thenReturn(startDate, endDate);
        underTest.start();
        Mockito.verify(builtInQualityProfilesNotification).onChange(ArgumentMatchers.any(), ArgumentMatchers.eq(startDate), ArgumentMatchers.eq(endDate));
    }
}

