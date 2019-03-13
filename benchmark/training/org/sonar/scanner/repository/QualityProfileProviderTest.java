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
package org.sonar.scanner.repository;


import LoggerLevel.WARN;
import QualityProfiles.SONAR_PROFILE_PROP;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sonar.api.utils.log.LogTester;
import org.sonar.scanner.bootstrap.ProcessedScannerProperties;
import org.sonar.scanner.rule.QualityProfiles;
import org.sonarqube.ws.Qualityprofiles.SearchWsResponse.QualityProfile;


public class QualityProfileProviderTest {
    @Rule
    public LogTester logTester = new LogTester();

    private QualityProfilesProvider qualityProfileProvider;

    @Mock
    private QualityProfileLoader loader;

    @Mock
    private ProcessedScannerProperties props;

    @Mock
    private ProjectRepositories projectRepo;

    private List<QualityProfile> response;

    @Test
    public void testProvide() {
        Mockito.when(loader.load("project", null)).thenReturn(response);
        QualityProfiles qps = qualityProfileProvider.provide(loader, projectRepo, props);
        assertResponse(qps);
        Mockito.verify(loader).load("project", null);
        Mockito.verifyNoMoreInteractions(loader);
    }

    @Test
    public void testProjectDoesntExist() {
        Mockito.when(projectRepo.exists()).thenReturn(false);
        Mockito.when(loader.loadDefault(ArgumentMatchers.anyString())).thenReturn(response);
        Mockito.when(props.property(SONAR_PROFILE_PROP)).thenReturn("profile");
        QualityProfiles qps = qualityProfileProvider.provide(loader, projectRepo, props);
        assertResponse(qps);
        Mockito.verify(loader).loadDefault(ArgumentMatchers.anyString());
        Mockito.verifyNoMoreInteractions(loader);
    }

    @Test
    public void testProfileProp() {
        Mockito.when(loader.load(ArgumentMatchers.eq("project"), ArgumentMatchers.eq("custom"))).thenReturn(response);
        Mockito.when(props.property(SONAR_PROFILE_PROP)).thenReturn("custom");
        Mockito.when(props.properties()).thenReturn(ImmutableMap.of(SONAR_PROFILE_PROP, "custom"));
        QualityProfiles qps = qualityProfileProvider.provide(loader, projectRepo, props);
        assertResponse(qps);
        Mockito.verify(loader).load(ArgumentMatchers.eq("project"), ArgumentMatchers.eq("custom"));
        Mockito.verifyNoMoreInteractions(loader);
        assertThat(logTester.logs(WARN)).contains((("Ability to set quality profile from command line using '" + (QualityProfiles.SONAR_PROFILE_PROP)) + "' is deprecated and will be dropped in a future SonarQube version. Please configure quality profile used by your project on SonarQube server."));
    }

    @Test
    public void testProfilePropDefault() {
        Mockito.when(projectRepo.exists()).thenReturn(false);
        Mockito.when(loader.loadDefault(ArgumentMatchers.eq("custom"))).thenReturn(response);
        Mockito.when(props.property(SONAR_PROFILE_PROP)).thenReturn("custom");
        Mockito.when(props.properties()).thenReturn(ImmutableMap.of(SONAR_PROFILE_PROP, "custom"));
        QualityProfiles qps = qualityProfileProvider.provide(loader, projectRepo, props);
        assertResponse(qps);
        Mockito.verify(loader).loadDefault(ArgumentMatchers.eq("custom"));
        Mockito.verifyNoMoreInteractions(loader);
        assertThat(logTester.logs(WARN)).contains((("Ability to set quality profile from command line using '" + (QualityProfiles.SONAR_PROFILE_PROP)) + "' is deprecated and will be dropped in a future SonarQube version. Please configure quality profile used by your project on SonarQube server."));
    }
}

