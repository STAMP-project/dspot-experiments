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


import java.io.IOException;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.MessageException;
import org.sonar.scanner.WsTestUtil;
import org.sonar.scanner.bootstrap.ScannerWsClient;
import org.sonar.scanner.scan.ScanProperties;
import org.sonarqube.ws.client.HttpException;


public class DefaultQualityProfileLoaderTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ScannerWsClient wsClient = Mockito.mock(ScannerWsClient.class);

    private ScanProperties properties = Mockito.mock(ScanProperties.class);

    private DefaultQualityProfileLoader underTest = new DefaultQualityProfileLoader(properties, wsClient);

    @Test
    public void load_gets_profiles_for_specified_project_and_profile_name() throws IOException {
        WsTestUtil.mockStream(wsClient, "/api/qualityprofiles/search.protobuf?projectKey=foo", DefaultQualityProfileLoaderTest.createStreamOfProfiles("qp"));
        WsTestUtil.mockStream(wsClient, "/api/qualityprofiles/search.protobuf?profileName=bar", DefaultQualityProfileLoaderTest.createStreamOfProfiles("qp"));
        underTest.load("foo", "bar");
        verifyCalledPath("/api/qualityprofiles/search.protobuf?projectKey=foo");
        verifyCalledPath("/api/qualityprofiles/search.protobuf?profileName=bar");
    }

    @Test
    public void load_gets_all_profiles_for_specified_project() throws IOException {
        prepareCallWithResults();
        underTest.load("foo", null);
        verifyCalledPath("/api/qualityprofiles/search.protobuf?projectKey=foo");
    }

    @Test
    public void load_encodes_url_parameters() throws IOException {
        WsTestUtil.mockStream(wsClient, "/api/qualityprofiles/search.protobuf?projectKey=foo%232", DefaultQualityProfileLoaderTest.createStreamOfProfiles("qp"));
        WsTestUtil.mockStream(wsClient, "/api/qualityprofiles/search.protobuf?profileName=bar%232", DefaultQualityProfileLoaderTest.createStreamOfProfiles("qp"));
        underTest.load("foo#2", "bar#2");
        verifyCalledPath("/api/qualityprofiles/search.protobuf?projectKey=foo%232");
        verifyCalledPath("/api/qualityprofiles/search.protobuf?profileName=bar%232");
    }

    @Test
    public void load_sets_organization_parameter_if_defined_in_settings() throws IOException {
        Mockito.when(properties.organizationKey()).thenReturn(Optional.of("my-org"));
        prepareCallWithResults();
        underTest.load("foo", null);
        verifyCalledPath("/api/qualityprofiles/search.protobuf?projectKey=foo&organization=my-org");
    }

    @Test
    public void loadDefault_gets_profiles_with_specified_name() throws IOException {
        WsTestUtil.mockStream(wsClient, "/api/qualityprofiles/search.protobuf?defaults=true", DefaultQualityProfileLoaderTest.createStreamOfProfiles("qp"));
        WsTestUtil.mockStream(wsClient, "/api/qualityprofiles/search.protobuf?profileName=foo", DefaultQualityProfileLoaderTest.createStreamOfProfiles("qp"));
        underTest.loadDefault("foo");
        verifyCalledPath("/api/qualityprofiles/search.protobuf?defaults=true");
        verifyCalledPath("/api/qualityprofiles/search.protobuf?profileName=foo");
    }

    @Test
    public void loadDefault_gets_all_default_profiles() throws IOException {
        prepareCallWithResults();
        underTest.loadDefault(null);
        verifyCalledPath("/api/qualityprofiles/search.protobuf?defaults=true");
    }

    @Test
    public void loadDefault_sets_organization_parameter_if_defined_in_settings() throws IOException {
        Mockito.when(properties.organizationKey()).thenReturn(Optional.of("my-org"));
        WsTestUtil.mockStream(wsClient, "/api/qualityprofiles/search.protobuf?defaults=true&organization=my-org", DefaultQualityProfileLoaderTest.createStreamOfProfiles("qp"));
        WsTestUtil.mockStream(wsClient, "/api/qualityprofiles/search.protobuf?profileName=foo&organization=my-org", DefaultQualityProfileLoaderTest.createStreamOfProfiles("qp"));
        underTest.loadDefault("foo");
        verifyCalledPath("/api/qualityprofiles/search.protobuf?defaults=true&organization=my-org");
        verifyCalledPath("/api/qualityprofiles/search.protobuf?profileName=foo&organization=my-org");
    }

    @Test
    public void load_throws_MessageException_if_no_profiles_are_available_for_specified_project() throws IOException {
        prepareCallWithEmptyResults();
        exception.expect(MessageException.class);
        exception.expectMessage("No quality profiles");
        underTest.load("project", null);
        Mockito.verifyNoMoreInteractions(wsClient);
    }

    @Test
    public void load_throws_MessageException_if_organization_is_not_found() throws IOException {
        HttpException e = new HttpException("", 404, "{\"errors\":[{\"msg\":\"No organization with key \'myorg\'\"}]}");
        WsTestUtil.mockException(wsClient, e);
        exception.expect(MessageException.class);
        exception.expectMessage("Failed to load the quality profiles of project 'project': No organization with key 'myorg'");
        underTest.load("project", null);
        Mockito.verifyNoMoreInteractions(wsClient);
    }
}

