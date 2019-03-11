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
package org.sonar.server.ui.ws;


import EditionProvider.Edition.COMMUNITY;
import EditionProvider.Edition.DEVELOPER;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.platform.Server;
import org.sonar.api.resources.ResourceType;
import org.sonar.api.resources.ResourceTypeTree;
import org.sonar.api.web.page.Page;
import org.sonar.core.platform.PlatformEditionProvider;
import org.sonar.db.DbClient;
import org.sonar.db.dialect.MySql;
import org.sonar.server.branch.BranchFeatureRule;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.organization.TestOrganizationFlags;
import org.sonar.server.platform.WebServer;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class GlobalActionTest {
    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private MapSettings settings = new MapSettings();

    private Server server = Mockito.mock(Server.class);

    private WebServer webServer = Mockito.mock(WebServer.class);

    private DbClient dbClient = Mockito.mock(DbClient.class, Mockito.RETURNS_DEEP_STUBS);

    private TestOrganizationFlags organizationFlags = TestOrganizationFlags.standalone();

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.fromUuid("foo");

    private BranchFeatureRule branchFeature = new BranchFeatureRule();

    private PlatformEditionProvider editionProvider = Mockito.mock(PlatformEditionProvider.class);

    private WsActionTester ws;

    @Test
    public void empty_call() {
        init();
        assertJson(call()).isSimilarTo(("{" + ((("  \"globalPages\": []," + "  \"settings\": {},") + "  \"qualifiers\": []") + "}")));
    }

    @Test
    public void return_qualifiers() {
        init(new Page[]{  }, new ResourceTypeTree[]{ ResourceTypeTree.builder().addType(ResourceType.builder("POL").build()).addType(ResourceType.builder("LOP").build()).addRelations("POL", "LOP").build(), ResourceTypeTree.builder().addType(ResourceType.builder("PAL").build()).addType(ResourceType.builder("LAP").build()).addRelations("PAL", "LAP").build() });
        assertJson(call()).isSimilarTo(("{" + ("  \"qualifiers\": [\"POL\", \"PAL\"]" + "}")));
    }

    @Test
    public void return_settings() {
        settings.setProperty("sonar.lf.logoUrl", "http://example.com/my-custom-logo.png");
        settings.setProperty("sonar.lf.logoWidthPx", 135);
        settings.setProperty("sonar.lf.gravatarServerUrl", "https://secure.gravatar.com/avatar/{EMAIL_MD5}.jpg?s={SIZE}&d=identicon");
        settings.setProperty("sonar.lf.enableGravatar", true);
        settings.setProperty("sonar.updatecenter.activate", false);
        settings.setProperty("sonar.technicalDebt.ratingGrid", "0.05,0.1,0.2,0.5");
        // This setting should be ignored as it's not needed
        settings.setProperty("sonar.defaultGroup", "sonar-users");
        init();
        assertJson(call()).isSimilarTo(("{" + (((((((("  \"settings\": {" + "    \"sonar.lf.logoUrl\": \"http://example.com/my-custom-logo.png\",") + "    \"sonar.lf.logoWidthPx\": \"135\",") + "    \"sonar.lf.gravatarServerUrl\": \"https://secure.gravatar.com/avatar/{EMAIL_MD5}.jpg?s={SIZE}&d=identicon\",") + "    \"sonar.lf.enableGravatar\": \"true\",") + "    \"sonar.updatecenter.activate\": \"false\",") + "    \"sonar.technicalDebt.ratingGrid\": \"0.05,0.1,0.2,0.5\"") + "  }") + "}")));
    }

    @Test
    public void return_prismic_setting_on_sonarcloud_only() {
        settings.setProperty("sonar.sonarcloud.enabled", true);
        settings.setProperty("sonar.prismic.accessToken", "secret");
        settings.setProperty("sonar.analytics.trackingId", "ga_id");
        init();
        assertJson(call()).isSimilarTo(("{" + (((("  \"settings\": {" + "    \"sonar.prismic.accessToken\": \"secret\"") + "    \"sonar.analytics.trackingId\": \"ga_id\"") + "  }") + "}")));
    }

    @Test
    public void return_deprecated_logo_settings() {
        init();
        settings.setProperty("sonar.lf.logoUrl", "http://example.com/my-custom-logo.png");
        settings.setProperty("sonar.lf.logoWidthPx", 135);
        assertJson(call()).isSimilarTo(("{" + (((((("  \"settings\": {" + "    \"sonar.lf.logoUrl\": \"http://example.com/my-custom-logo.png\",") + "    \"sonar.lf.logoWidthPx\": \"135\"") + "  },") + "  \"logoUrl\": \"http://example.com/my-custom-logo.png\",") + "  \"logoWidth\": \"135\"") + "}")));
    }

    @Test
    public void the_returned_global_pages_do_not_include_administration_pages() {
        init(createPages(), new ResourceTypeTree[]{  });
        assertJson(call()).isSimilarTo(("{" + (((((((((("  \"globalPages\": [" + "    {") + "      \"key\": \"another_plugin/page\",") + "      \"name\": \"My Another Page\"") + "    },") + "    {") + "      \"key\": \"my_plugin/page\",") + "      \"name\": \"My Plugin Page\"") + "    }") + "  ]") + "}")));
    }

    @Test
    public void return_sonarqube_version() {
        init();
        Mockito.when(server.getVersion()).thenReturn("6.2");
        assertJson(call()).isSimilarTo(("{" + ("  \"version\": \"6.2\"" + "}")));
    }

    @Test
    public void functional_version_when_4_digits() {
        init();
        Mockito.when(server.getVersion()).thenReturn("6.3.1.1234");
        String result = call();
        assertThat(result).contains("6.3.1 (build 1234)");
    }

    @Test
    public void functional_version_when_third_digit_is_0() {
        init();
        Mockito.when(server.getVersion()).thenReturn("6.3.0.1234");
        String result = call();
        assertThat(result).contains("6.3 (build 1234)");
    }

    @Test
    public void return_if_production_database_or_not() {
        init();
        Mockito.when(dbClient.getDatabase().getDialect()).thenReturn(new MySql());
        assertJson(call()).isSimilarTo(("{" + ("  \"productionDatabase\": true" + "}")));
    }

    @Test
    public void organization_support() {
        init();
        organizationFlags.setEnabled(true);
        assertJson(call()).isSimilarTo(("{" + (("  \"organizationsEnabled\": true," + "  \"defaultOrganization\": \"key_foo\"") + "}")));
    }

    @Test
    public void branch_support() {
        init();
        branchFeature.setEnabled(true);
        assertJson(call()).isSimilarTo("{\"branchesEnabled\":true}");
        branchFeature.setEnabled(false);
        assertJson(call()).isSimilarTo("{\"branchesEnabled\":false}");
    }

    @Test
    public void can_admin_on_global_level() {
        init();
        userSession.logIn().setRoot();
        assertJson(call()).isSimilarTo("{\"canAdmin\":true}");
    }

    @Test
    public void standalone_flag() {
        init();
        userSession.logIn().setRoot();
        Mockito.when(webServer.isStandalone()).thenReturn(true);
        assertJson(call()).isSimilarTo("{\"standalone\":true}");
    }

    @Test
    public void not_standalone_flag() {
        init();
        userSession.logIn().setRoot();
        Mockito.when(webServer.isStandalone()).thenReturn(false);
        assertJson(call()).isSimilarTo("{\"standalone\":false}");
    }

    @Test
    public void test_example_response() {
        settings.setProperty("sonar.lf.logoUrl", "http://example.com/my-custom-logo.png");
        settings.setProperty("sonar.lf.logoWidthPx", 135);
        settings.setProperty("sonar.lf.gravatarServerUrl", "http://some-server.tld/logo.png");
        settings.setProperty("sonar.lf.enableGravatar", true);
        settings.setProperty("sonar.updatecenter.activate", false);
        settings.setProperty("sonar.technicalDebt.ratingGrid", "0.05,0.1,0.2,0.5");
        init(createPages(), new ResourceTypeTree[]{ ResourceTypeTree.builder().addType(ResourceType.builder("POL").build()).addType(ResourceType.builder("LOP").build()).addRelations("POL", "LOP").build(), ResourceTypeTree.builder().addType(ResourceType.builder("PAL").build()).addType(ResourceType.builder("LAP").build()).addRelations("PAL", "LAP").build() });
        Mockito.when(server.getVersion()).thenReturn("6.2");
        Mockito.when(dbClient.getDatabase().getDialect()).thenReturn(new MySql());
        Mockito.when(webServer.isStandalone()).thenReturn(true);
        Mockito.when(editionProvider.get()).thenReturn(Optional.of(COMMUNITY));
        String result = call();
        assertJson(result).isSimilarTo(ws.getDef().responseExampleAsString());
    }

    @Test
    public void edition_is_not_returned_if_not_defined() {
        init();
        Mockito.when(editionProvider.get()).thenReturn(Optional.empty());
        String json = call();
        assertThat(json).doesNotContain("edition");
    }

    @Test
    public void edition_is_returned_if_defined() {
        init();
        Mockito.when(editionProvider.get()).thenReturn(Optional.of(DEVELOPER));
        String json = call();
        assertJson(json).isSimilarTo("{\"edition\":\"developer\"}");
    }
}

