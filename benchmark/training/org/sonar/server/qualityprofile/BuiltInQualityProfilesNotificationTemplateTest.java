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


import java.util.Date;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.platform.Server;
import org.sonar.plugins.emailnotifications.api.EmailMessage;
import org.sonar.server.qualityprofile.BuiltInQualityProfilesNotification.Profile;


public class BuiltInQualityProfilesNotificationTemplateTest {
    private Server server = Mockito.mock(Server.class);

    private BuiltInQualityProfilesNotificationTemplate underTest = new BuiltInQualityProfilesNotificationTemplate(server);

    @Test
    public void notification_contains_a_subject() {
        String profileName = BuiltInQualityProfilesNotificationTemplateTest.newProfileName();
        String languageKey = BuiltInQualityProfilesNotificationTemplateTest.newLanguageKey();
        String languageName = BuiltInQualityProfilesNotificationTemplateTest.newLanguageName();
        BuiltInQualityProfilesNotification notification = new BuiltInQualityProfilesNotification().addProfile(Profile.newBuilder().setProfileName(profileName).setLanguageKey(languageKey).setLanguageName(languageName).setNewRules(2).build());
        EmailMessage emailMessage = underTest.format(notification.serialize());
        assertThat(emailMessage.getSubject()).isEqualTo("Built-in quality profiles have been updated");
    }

    @Test
    public void notification_contains_count_of_new_rules() {
        String profileName = BuiltInQualityProfilesNotificationTemplateTest.newProfileName();
        String languageKey = BuiltInQualityProfilesNotificationTemplateTest.newLanguageKey();
        String languageName = BuiltInQualityProfilesNotificationTemplateTest.newLanguageName();
        BuiltInQualityProfilesNotification notification = new BuiltInQualityProfilesNotification().addProfile(Profile.newBuilder().setProfileName(profileName).setLanguageKey(languageKey).setLanguageName(languageName).setNewRules(2).build());
        EmailMessage emailMessage = underTest.format(notification.serialize());
        assertMessage(emailMessage, "\n 2 new rules\n");
    }

    @Test
    public void notification_contains_count_of_updated_rules() {
        String profileName = BuiltInQualityProfilesNotificationTemplateTest.newProfileName();
        String languageKey = BuiltInQualityProfilesNotificationTemplateTest.newLanguageKey();
        String languageName = BuiltInQualityProfilesNotificationTemplateTest.newLanguageName();
        BuiltInQualityProfilesNotification notification = new BuiltInQualityProfilesNotification().addProfile(Profile.newBuilder().setProfileName(profileName).setLanguageKey(languageKey).setLanguageName(languageName).setUpdatedRules(2).build());
        EmailMessage emailMessage = underTest.format(notification.serialize());
        assertMessage(emailMessage, "\n 2 rules have been updated\n");
    }

    @Test
    public void notification_contains_count_of_removed_rules() {
        String profileName = BuiltInQualityProfilesNotificationTemplateTest.newProfileName();
        String languageKey = BuiltInQualityProfilesNotificationTemplateTest.newLanguageKey();
        String languageName = BuiltInQualityProfilesNotificationTemplateTest.newLanguageName();
        BuiltInQualityProfilesNotification notification = new BuiltInQualityProfilesNotification().addProfile(Profile.newBuilder().setProfileName(profileName).setLanguageKey(languageKey).setLanguageName(languageName).setRemovedRules(2).build());
        EmailMessage emailMessage = underTest.format(notification.serialize());
        assertMessage(emailMessage, "\n 2 rules removed\n");
    }

    @Test
    public void notification_supports_grammar_for_single_rule_added_removed_or_updated() {
        String profileName = BuiltInQualityProfilesNotificationTemplateTest.newProfileName();
        String languageKey = BuiltInQualityProfilesNotificationTemplateTest.newLanguageKey();
        String languageName = BuiltInQualityProfilesNotificationTemplateTest.newLanguageName();
        BuiltInQualityProfilesNotification notification = new BuiltInQualityProfilesNotification().addProfile(Profile.newBuilder().setProfileName(profileName).setLanguageKey(languageKey).setLanguageName(languageName).setNewRules(1).setUpdatedRules(1).setRemovedRules(1).build());
        EmailMessage emailMessage = underTest.format(notification.serialize());
        assertThat(emailMessage.getMessage()).contains("\n 1 new rule\n").contains("\n 1 rule has been updated\n").contains("\n 1 rule removed\n");
    }

    @Test
    public void notification_contains_list_of_new_updated_and_removed_rules() {
        String profileName = BuiltInQualityProfilesNotificationTemplateTest.newProfileName();
        String languageKey = BuiltInQualityProfilesNotificationTemplateTest.newLanguageKey();
        String languageName = BuiltInQualityProfilesNotificationTemplateTest.newLanguageName();
        BuiltInQualityProfilesNotification notification = new BuiltInQualityProfilesNotification().addProfile(Profile.newBuilder().setProfileName(profileName).setLanguageKey(languageKey).setLanguageName(languageName).setNewRules(2).setUpdatedRules(3).setRemovedRules(4).build());
        EmailMessage emailMessage = underTest.format(notification.serialize());
        assertMessage(emailMessage, ("\n" + ((" 2 new rules\n" + " 3 rules have been updated\n") + " 4 rules removed\n")));
    }

    @Test
    public void notification_contains_many_profiles() {
        String profileName1 = "profile1_" + (randomAlphanumeric(20));
        String languageKey1 = "langkey1_" + (randomAlphanumeric(20));
        String languageName1 = "langName1_" + (randomAlphanumeric(20));
        String profileName2 = "profile2_" + (randomAlphanumeric(20));
        String languageKey2 = "langkey2_" + (randomAlphanumeric(20));
        String languageName2 = "langName2_" + (randomAlphanumeric(20));
        BuiltInQualityProfilesNotification notification = new BuiltInQualityProfilesNotification().addProfile(Profile.newBuilder().setProfileName(profileName1).setLanguageKey(languageKey1).setLanguageName(languageName1).setNewRules(2).build()).addProfile(Profile.newBuilder().setProfileName(profileName2).setLanguageKey(languageKey2).setLanguageName(languageName2).setNewRules(13).build());
        EmailMessage emailMessage = underTest.format(notification.serialize());
        assertThat(emailMessage.getMessage()).containsSubsequence("The following built-in profiles have been updated:\n", profileTitleText(profileName1, languageKey1, languageName1), " 2 new rules\n", profileTitleText(profileName2, languageKey2, languageName2), " 13 new rules\n", (("This is a good time to review your quality profiles and update them to benefit from the latest evolutions: " + (server.getPublicRootUrl())) + "/profiles"));
    }

    @Test
    public void notification_contains_profiles_sorted_by_language_then_by_profile_name() {
        String languageKey1 = "langkey1_" + (randomAlphanumeric(20));
        String languageName1 = "langName1_" + (randomAlphanumeric(20));
        String languageKey2 = "langKey2_" + (randomAlphanumeric(20));
        String languageName2 = "langName2_" + (randomAlphanumeric(20));
        String profileName1 = "profile1_" + (randomAlphanumeric(20));
        String profileName2 = "profile2_" + (randomAlphanumeric(20));
        String profileName3 = "profile3_" + (randomAlphanumeric(20));
        BuiltInQualityProfilesNotification notification = new BuiltInQualityProfilesNotification().addProfile(Profile.newBuilder().setProfileName(profileName3).setLanguageKey(languageKey2).setLanguageName(languageName2).build()).addProfile(Profile.newBuilder().setProfileName(profileName2).setLanguageKey(languageKey1).setLanguageName(languageName1).build()).addProfile(Profile.newBuilder().setProfileName(profileName1).setLanguageKey(languageKey2).setLanguageName(languageName2).build());
        EmailMessage emailMessage = underTest.format(notification.serialize());
        assertThat(emailMessage.getMessage()).containsSubsequence(((("\"" + profileName2) + "\" - ") + languageName1), ((("\"" + profileName1) + "\" - ") + languageName2), ((("\"" + profileName3) + "\" - ") + languageName2));
    }

    @Test
    public void notification_contains_encoded_profile_name() {
        BuiltInQualityProfilesNotification notification = new BuiltInQualityProfilesNotification().addProfile(Profile.newBuilder().setProfileName("Sonar Way").setLanguageKey("java").setLanguageName(BuiltInQualityProfilesNotificationTemplateTest.newLanguageName()).build());
        EmailMessage emailMessage = underTest.format(notification.serialize());
        assertThat(emailMessage.getMessage()).contains(((server.getPublicRootUrl()) + "/profiles/changelog?language=java&name=Sonar+Way"));
    }

    @Test
    public void notification_contains_from_and_to_date() {
        String profileName = BuiltInQualityProfilesNotificationTemplateTest.newProfileName();
        String languageKey = BuiltInQualityProfilesNotificationTemplateTest.newLanguageKey();
        String languageName = BuiltInQualityProfilesNotificationTemplateTest.newLanguageName();
        long startDate = 1000000000000L;
        long endDate = startDate + 1100000000000L;
        BuiltInQualityProfilesNotification notification = new BuiltInQualityProfilesNotification().addProfile(Profile.newBuilder().setProfileName(profileName).setLanguageKey(languageKey).setLanguageName(languageName).setStartDate(startDate).setEndDate(endDate).build());
        EmailMessage emailMessage = underTest.format(notification.serialize());
        assertMessage(emailMessage, profileTitleText(profileName, languageKey, languageName, formatDate(new Date(startDate)), formatDate(new Date(endDate))));
    }
}

