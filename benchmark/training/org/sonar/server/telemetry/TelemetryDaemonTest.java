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
package org.sonar.server.telemetry;


import EditionProvider.Edition.DEVELOPER;
import LicenseReader.License;
import LoggerLevel.DEBUG;
import LoggerLevel.INFO;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.api.utils.log.LogTester;
import org.sonar.core.platform.PlatformEditionProvider;
import org.sonar.core.platform.PluginInfo;
import org.sonar.core.platform.PluginRepository;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.metric.MetricDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.measure.index.ProjectMeasuresIndexer;
import org.sonar.server.property.InternalProperties;
import org.sonar.server.property.MapInternalProperties;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.user.index.UserIndexer;


public class TelemetryDaemonTest {
    private static final long ONE_HOUR = (60 * 60) * 1000L;

    private static final long ONE_DAY = 24 * (TelemetryDaemonTest.ONE_HOUR);

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create();

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public LogTester logger = new LogTester().setLevel(DEBUG);

    private TelemetryClient client = Mockito.mock(TelemetryClient.class);

    private InternalProperties internalProperties = Mockito.spy(new MapInternalProperties());

    private FakeServer server = new FakeServer();

    private PluginRepository pluginRepository = Mockito.mock(PluginRepository.class);

    private TestSystem2 system2 = new TestSystem2().setNow(System.currentTimeMillis());

    private MapSettings settings = new MapSettings();

    private ProjectMeasuresIndexer projectMeasuresIndexer = new ProjectMeasuresIndexer(db.getDbClient(), es.client());

    private UserIndexer userIndexer = new UserIndexer(db.getDbClient(), es.client());

    private PlatformEditionProvider editionProvider = Mockito.mock(PlatformEditionProvider.class);

    private final TelemetryDataLoader communityDataLoader = new TelemetryDataLoader(server, db.getDbClient(), pluginRepository, new org.sonar.server.user.index.UserIndex(es.client(), system2), new org.sonar.server.measure.index.ProjectMeasuresIndex(es.client(), null, system2), editionProvider, new org.sonar.server.organization.DefaultOrganizationProviderImpl(db.getDbClient()), null);

    private TelemetryDaemon communityUnderTest = new TelemetryDaemon(communityDataLoader, client, settings.asConfig(), internalProperties, system2);

    private final LicenseReader licenseReader = Mockito.mock(LicenseReader.class);

    private final TelemetryDataLoader commercialDataLoader = new TelemetryDataLoader(server, db.getDbClient(), pluginRepository, new org.sonar.server.user.index.UserIndex(es.client(), system2), new org.sonar.server.measure.index.ProjectMeasuresIndex(es.client(), null, system2), editionProvider, new org.sonar.server.organization.DefaultOrganizationProviderImpl(db.getDbClient()), licenseReader);

    private TelemetryDaemon commercialUnderTest = new TelemetryDaemon(commercialDataLoader, client, settings.asConfig(), internalProperties, system2);

    @Test
    public void send_telemetry_data() throws IOException {
        initTelemetrySettingsToDefaultValues();
        settings.setProperty("sonar.telemetry.frequencyInSeconds", "1");
        server.setId("AU-TpxcB-iU5OvuD2FL7");
        server.setVersion("7.5.4");
        List<PluginInfo> plugins = Arrays.asList(newPlugin("java", "4.12.0.11033"), newPlugin("scmgit", "1.2"), new PluginInfo("other"));
        Mockito.when(pluginRepository.getPluginInfos()).thenReturn(plugins);
        Mockito.when(editionProvider.get()).thenReturn(Optional.of(DEVELOPER));
        IntStream.range(0, 3).forEach(( i) -> db.users().insertUser());
        db.users().insertUser(( u) -> u.setActive(false));
        userIndexer.indexOnStartup(Collections.emptySet());
        MetricDto lines = db.measures().insertMetric(( m) -> m.setKey(LINES_KEY));
        MetricDto ncloc = db.measures().insertMetric(( m) -> m.setKey(NCLOC_KEY));
        MetricDto coverage = db.measures().insertMetric(( m) -> m.setKey(COVERAGE_KEY));
        MetricDto nclocDistrib = db.measures().insertMetric(( m) -> m.setKey(NCLOC_LANGUAGE_DISTRIBUTION_KEY));
        ComponentDto project1 = db.components().insertMainBranch(db.getDefaultOrganization());
        ComponentDto project1Branch = db.components().insertProjectBranch(project1);
        db.measures().insertLiveMeasure(project1, lines, ( m) -> m.setValue(200.0));
        db.measures().insertLiveMeasure(project1, ncloc, ( m) -> m.setValue(100.0));
        db.measures().insertLiveMeasure(project1, coverage, ( m) -> m.setValue(80.0));
        db.measures().insertLiveMeasure(project1, nclocDistrib, ( m) -> m.setValue(null).setData("java=200;js=50"));
        ComponentDto project2 = db.components().insertMainBranch(db.getDefaultOrganization());
        db.measures().insertLiveMeasure(project2, lines, ( m) -> m.setValue(300.0));
        db.measures().insertLiveMeasure(project2, ncloc, ( m) -> m.setValue(200.0));
        db.measures().insertLiveMeasure(project2, coverage, ( m) -> m.setValue(80.0));
        db.measures().insertLiveMeasure(project2, nclocDistrib, ( m) -> m.setValue(null).setData("java=300;kotlin=2500"));
        projectMeasuresIndexer.indexOnStartup(Collections.emptySet());
        communityUnderTest.start();
        ArgumentCaptor<String> jsonCaptor = captureJson();
        String json = jsonCaptor.getValue();
        assertJson(json).ignoreFields("database").isSimilarTo(getClass().getResource("telemetry-example.json"));
        assertJson(getClass().getResource("telemetry-example.json")).ignoreFields("database").isSimilarTo(json);
        assertDatabaseMetadata(json);
        assertThat(logger.logs(INFO)).contains("Sharing of SonarQube statistics is enabled.");
    }

    @Test
    public void take_biggest_long_living_branches() throws IOException {
        initTelemetrySettingsToDefaultValues();
        settings.setProperty("sonar.telemetry.frequencyInSeconds", "1");
        server.setId("AU-TpxcB-iU5OvuD2FL7").setVersion("7.5.4");
        MetricDto ncloc = db.measures().insertMetric(( m) -> m.setKey(NCLOC_KEY));
        ComponentDto project = db.components().insertMainBranch(db.getDefaultOrganization());
        ComponentDto longBranch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(LONG));
        ComponentDto shortBranch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(SHORT));
        db.measures().insertLiveMeasure(project, ncloc, ( m) -> m.setValue(10.0));
        db.measures().insertLiveMeasure(longBranch, ncloc, ( m) -> m.setValue(20.0));
        db.measures().insertLiveMeasure(shortBranch, ncloc, ( m) -> m.setValue(30.0));
        projectMeasuresIndexer.indexOnStartup(Collections.emptySet());
        communityUnderTest.start();
        ArgumentCaptor<String> jsonCaptor = captureJson();
        assertJson(jsonCaptor.getValue()).isSimilarTo(("{\n" + ("  \"ncloc\": 20\n" + "}\n")));
    }

    @Test
    public void send_data_via_client_at_startup_after_initial_delay() throws IOException {
        initTelemetrySettingsToDefaultValues();
        settings.setProperty("sonar.telemetry.frequencyInSeconds", "1");
        communityUnderTest.start();
        Mockito.verify(client, Mockito.timeout(2000).atLeastOnce()).upload(ArgumentMatchers.anyString());
    }

    @Test
    public void data_contains_no_license_type_on_community_edition() throws IOException {
        initTelemetrySettingsToDefaultValues();
        settings.setProperty("sonar.telemetry.frequencyInSeconds", "1");
        communityUnderTest.start();
        ArgumentCaptor<String> jsonCaptor = captureJson();
        assertThat(jsonCaptor.getValue()).doesNotContain("licenseType");
    }

    @Test
    public void data_contains_no_license_type_on_commercial_edition_if_no_license() throws IOException {
        initTelemetrySettingsToDefaultValues();
        settings.setProperty("sonar.telemetry.frequencyInSeconds", "1");
        Mockito.when(licenseReader.read()).thenReturn(Optional.empty());
        commercialUnderTest.start();
        ArgumentCaptor<String> jsonCaptor = captureJson();
        assertThat(jsonCaptor.getValue()).doesNotContain("licenseType");
    }

    @Test
    public void data_has_license_type_on_commercial_edition_if_no_license() throws IOException {
        String licenseType = randomAlphabetic(12);
        initTelemetrySettingsToDefaultValues();
        settings.setProperty("sonar.telemetry.frequencyInSeconds", "1");
        LicenseReader.License license = Mockito.mock(License.class);
        Mockito.when(license.getType()).thenReturn(licenseType);
        Mockito.when(licenseReader.read()).thenReturn(Optional.of(license));
        commercialUnderTest.start();
        ArgumentCaptor<String> jsonCaptor = captureJson();
        assertJson(jsonCaptor.getValue()).isSimilarTo((((("{\n" + "  \"licenseType\": \"") + licenseType) + "\"\n") + "}\n"));
    }

    @Test
    public void check_if_should_send_data_periodically() throws IOException {
        initTelemetrySettingsToDefaultValues();
        long now = system2.now();
        long sixDaysAgo = now - ((TelemetryDaemonTest.ONE_DAY) * 6L);
        long sevenDaysAgo = now - ((TelemetryDaemonTest.ONE_DAY) * 7L);
        internalProperties.write("telemetry.lastPing", String.valueOf(sixDaysAgo));
        settings.setProperty("sonar.telemetry.frequencyInSeconds", "1");
        communityUnderTest.start();
        Mockito.verify(client, Mockito.after(2000).never()).upload(ArgumentMatchers.anyString());
        internalProperties.write("telemetry.lastPing", String.valueOf(sevenDaysAgo));
        Mockito.verify(client, Mockito.timeout(2000).atLeastOnce()).upload(ArgumentMatchers.anyString());
    }

    @Test
    public void send_server_id_and_version() throws IOException {
        initTelemetrySettingsToDefaultValues();
        settings.setProperty("sonar.telemetry.frequencyInSeconds", "1");
        String id = randomAlphanumeric(40);
        String version = randomAlphanumeric(10);
        server.setId(id);
        server.setVersion(version);
        communityUnderTest.start();
        ArgumentCaptor<String> json = captureJson();
        assertThat(json.getValue()).contains(id, version);
    }

    @Test
    public void do_not_send_data_if_last_ping_earlier_than_one_week_ago() throws IOException {
        initTelemetrySettingsToDefaultValues();
        settings.setProperty("sonar.telemetry.frequencyInSeconds", "1");
        long now = system2.now();
        long sixDaysAgo = now - ((TelemetryDaemonTest.ONE_DAY) * 6L);
        internalProperties.write("telemetry.lastPing", String.valueOf(sixDaysAgo));
        communityUnderTest.start();
        Mockito.verify(client, Mockito.after(2000).never()).upload(ArgumentMatchers.anyString());
    }

    @Test
    public void send_data_if_last_ping_is_one_week_ago() throws IOException {
        initTelemetrySettingsToDefaultValues();
        settings.setProperty("sonar.telemetry.frequencyInSeconds", "1");
        long today = parseDate("2017-08-01").getTime();
        system2.setNow((today + (15 * (TelemetryDaemonTest.ONE_HOUR))));
        long sevenDaysAgo = today - ((TelemetryDaemonTest.ONE_DAY) * 7L);
        internalProperties.write("telemetry.lastPing", String.valueOf(sevenDaysAgo));
        Mockito.reset(internalProperties);
        communityUnderTest.start();
        Mockito.verify(internalProperties, Mockito.timeout(4000)).write("telemetry.lastPing", String.valueOf(today));
        Mockito.verify(client).upload(ArgumentMatchers.anyString());
    }

    @Test
    public void opt_out_sent_once() throws IOException {
        initTelemetrySettingsToDefaultValues();
        settings.setProperty("sonar.telemetry.frequencyInSeconds", "1");
        settings.setProperty("sonar.telemetry.enable", "false");
        communityUnderTest.start();
        communityUnderTest.start();
        Mockito.verify(client, Mockito.after(2000).never()).upload(ArgumentMatchers.anyString());
        Mockito.verify(client, Mockito.timeout(2000).times(1)).optOut(ArgumentMatchers.anyString());
        assertThat(logger.logs(INFO)).contains("Sharing of SonarQube statistics is disabled.");
    }
}

