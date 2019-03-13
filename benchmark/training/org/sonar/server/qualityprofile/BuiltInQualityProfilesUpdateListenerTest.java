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


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.notifications.Notification;
import org.sonar.api.resources.Languages;
import org.sonar.server.notification.NotificationManager;


public class BuiltInQualityProfilesUpdateListenerTest {
    private NotificationManager notificationManager = Mockito.mock(NotificationManager.class);

    private MapSettings settings = new MapSettings();

    @Test
    public void add_profile_to_notification_for_added_rules() {
        enableNotificationInGlobalSettings();
        Multimap<QProfileName, ActiveRuleChange> profiles = ArrayListMultimap.create();
        Languages languages = new Languages();
        Tuple expectedTuple = addProfile(profiles, languages, Type.ACTIVATED);
        BuiltInQualityProfilesUpdateListener underTest = new BuiltInQualityProfilesUpdateListener(notificationManager, languages, settings.asConfig());
        underTest.onChange(profiles, 0, 1);
        ArgumentCaptor<Notification> notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);
        Mockito.verify(notificationManager).scheduleForSending(notificationArgumentCaptor.capture());
        Mockito.verifyNoMoreInteractions(notificationManager);
        assertThat(BuiltInQualityProfilesNotification.parse(notificationArgumentCaptor.getValue()).getProfiles()).extracting(Profile::getProfileName, Profile::getLanguageKey, Profile::getLanguageName, Profile::getNewRules).containsExactlyInAnyOrder(expectedTuple);
    }

    @Test
    public void add_profile_to_notification_for_updated_rules() {
        enableNotificationInGlobalSettings();
        Multimap<QProfileName, ActiveRuleChange> profiles = ArrayListMultimap.create();
        Languages languages = new Languages();
        Tuple expectedTuple = addProfile(profiles, languages, Type.UPDATED);
        BuiltInQualityProfilesUpdateListener underTest = new BuiltInQualityProfilesUpdateListener(notificationManager, languages, settings.asConfig());
        underTest.onChange(profiles, 0, 1);
        ArgumentCaptor<Notification> notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);
        Mockito.verify(notificationManager).scheduleForSending(notificationArgumentCaptor.capture());
        Mockito.verifyNoMoreInteractions(notificationManager);
        assertThat(BuiltInQualityProfilesNotification.parse(notificationArgumentCaptor.getValue()).getProfiles()).extracting(Profile::getProfileName, Profile::getLanguageKey, Profile::getLanguageName, Profile::getUpdatedRules).containsExactlyInAnyOrder(expectedTuple);
    }

    @Test
    public void add_profile_to_notification_for_removed_rules() {
        enableNotificationInGlobalSettings();
        Multimap<QProfileName, ActiveRuleChange> profiles = ArrayListMultimap.create();
        Languages languages = new Languages();
        Tuple expectedTuple = addProfile(profiles, languages, Type.DEACTIVATED);
        BuiltInQualityProfilesUpdateListener underTest = new BuiltInQualityProfilesUpdateListener(notificationManager, languages, settings.asConfig());
        underTest.onChange(profiles, 0, 1);
        ArgumentCaptor<Notification> notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);
        Mockito.verify(notificationManager).scheduleForSending(notificationArgumentCaptor.capture());
        Mockito.verifyNoMoreInteractions(notificationManager);
        assertThat(BuiltInQualityProfilesNotification.parse(notificationArgumentCaptor.getValue()).getProfiles()).extracting(Profile::getProfileName, Profile::getLanguageKey, Profile::getLanguageName, Profile::getRemovedRules).containsExactlyInAnyOrder(expectedTuple);
    }

    @Test
    public void add_multiple_profiles_to_notification() {
        enableNotificationInGlobalSettings();
        Multimap<QProfileName, ActiveRuleChange> profiles = ArrayListMultimap.create();
        Languages languages = new Languages();
        Tuple expectedTuple1 = addProfile(profiles, languages, Type.ACTIVATED);
        Tuple expectedTuple2 = addProfile(profiles, languages, Type.ACTIVATED);
        BuiltInQualityProfilesUpdateListener underTest = new BuiltInQualityProfilesUpdateListener(notificationManager, languages, settings.asConfig());
        underTest.onChange(profiles, 0, 1);
        ArgumentCaptor<Notification> notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);
        Mockito.verify(notificationManager).scheduleForSending(notificationArgumentCaptor.capture());
        Mockito.verifyNoMoreInteractions(notificationManager);
        assertThat(BuiltInQualityProfilesNotification.parse(notificationArgumentCaptor.getValue()).getProfiles()).extracting(Profile::getProfileName, Profile::getLanguageKey, Profile::getLanguageName, Profile::getNewRules).containsExactlyInAnyOrder(expectedTuple1, expectedTuple2);
    }

    @Test
    public void add_start_and_end_dates_to_notification() {
        enableNotificationInGlobalSettings();
        Multimap<QProfileName, ActiveRuleChange> profiles = ArrayListMultimap.create();
        Languages languages = new Languages();
        addProfile(profiles, languages, Type.ACTIVATED);
        long startDate = 10000000000L;
        long endDate = 15000000000L;
        BuiltInQualityProfilesUpdateListener underTest = new BuiltInQualityProfilesUpdateListener(notificationManager, languages, settings.asConfig());
        underTest.onChange(profiles, startDate, endDate);
        ArgumentCaptor<Notification> notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);
        Mockito.verify(notificationManager).scheduleForSending(notificationArgumentCaptor.capture());
        Mockito.verifyNoMoreInteractions(notificationManager);
        assertThat(BuiltInQualityProfilesNotification.parse(notificationArgumentCaptor.getValue()).getProfiles()).extracting(Profile::getStartDate, Profile::getEndDate).containsExactlyInAnyOrder(tuple(startDate, endDate));
    }

    @Test
    public void avoid_notification_if_configured_in_settings() {
        settings.setProperty(DISABLE_NOTIFICATION_ON_BUILT_IN_QPROFILES, true);
        Multimap<QProfileName, ActiveRuleChange> profiles = ArrayListMultimap.create();
        Languages languages = new Languages();
        addProfile(profiles, languages, Type.ACTIVATED);
        BuiltInQualityProfilesUpdateListener underTest = new BuiltInQualityProfilesUpdateListener(notificationManager, languages, settings.asConfig());
        underTest.onChange(profiles, 0, 1);
        Mockito.verifyZeroInteractions(notificationManager);
    }
}

