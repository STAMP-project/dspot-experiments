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


import RulePriority.CRITICAL;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.profiles.ProfileExporter;
import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.api.utils.internal.AlwaysIncreasingSystem2;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.tester.UserSessionRule;


public class QProfileExportersTest {
    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    private System2 system2 = new AlwaysIncreasingSystem2();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(system2);

    public DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private RuleFinder ruleFinder = new org.sonar.server.rule.DefaultRuleFinder(db.getDbClient(), defaultOrganizationProvider);

    private ProfileExporter[] exporters = new ProfileExporter[]{ new QProfileExportersTest.StandardExporter(), new QProfileExportersTest.XooExporter() };

    private ProfileImporter[] importers = new ProfileImporter[]{ new QProfileExportersTest.XooProfileImporter(), new QProfileExportersTest.XooProfileImporterWithMessages(), new QProfileExportersTest.XooProfileImporterWithError() };

    private RuleDefinitionDto rule;

    private QProfileRules qProfileRules = Mockito.mock(QProfileRules.class);

    private QProfileExporters underTest = new QProfileExporters(db.getDbClient(), ruleFinder, qProfileRules, exporters, importers);

    @Test
    public void exportersForLanguage() {
        assertThat(underTest.exportersForLanguage("xoo")).hasSize(2);
        assertThat(underTest.exportersForLanguage("java")).hasSize(1);
        assertThat(underTest.exportersForLanguage("java").get(0)).isInstanceOf(QProfileExportersTest.StandardExporter.class);
    }

    @Test
    public void mimeType() {
        assertThat(underTest.mimeType("xootool")).isEqualTo("plain/custom");
        // default mime type
        assertThat(underTest.mimeType("standard")).isEqualTo("text/plain");
    }

    @Test
    public void import_xml() {
        QProfileDto profile = createProfile();
        underTest.importXml(profile, "XooProfileImporter", IOUtils.toInputStream("<xml/>", StandardCharsets.UTF_8), db.getSession());
        ArgumentCaptor<QProfileDto> profileCapture = ArgumentCaptor.forClass(QProfileDto.class);
        Class<Collection<RuleActivation>> collectionClass = ((Class<Collection<RuleActivation>>) ((Class) (Collection.class)));
        ArgumentCaptor<Collection<RuleActivation>> activationCapture = ArgumentCaptor.forClass(collectionClass);
        Mockito.verify(qProfileRules).activateAndCommit(ArgumentMatchers.any(DbSession.class), profileCapture.capture(), activationCapture.capture());
        assertThat(profileCapture.getValue().getKee()).isEqualTo(profile.getKee());
        Collection<RuleActivation> activations = activationCapture.getValue();
        assertThat(activations).hasSize(1);
        RuleActivation activation = activations.iterator().next();
        assertThat(activation.getRuleId()).isEqualTo(rule.getId());
        assertThat(activation.getSeverity()).isEqualTo("CRITICAL");
    }

    @Test
    public void import_xml_return_messages() {
        QProfileDto profile = createProfile();
        QProfileResult result = underTest.importXml(profile, "XooProfileImporterWithMessages", IOUtils.toInputStream("<xml/>", StandardCharsets.UTF_8), db.getSession());
        assertThat(result.infos()).containsOnly("an info");
        assertThat(result.warnings()).containsOnly("a warning");
    }

    @Test
    public void fail_to_import_xml_when_error_in_importer() {
        try {
            underTest.importXml(QProfileTesting.newXooP1("org-123"), "XooProfileImporterWithError", IOUtils.toInputStream("<xml/>", StandardCharsets.UTF_8), db.getSession());
            Assert.fail();
        } catch (BadRequestException e) {
            assertThat(e).hasMessage("error!");
        }
    }

    @Test
    public void fail_to_import_xml_on_unknown_importer() {
        try {
            underTest.importXml(QProfileTesting.newXooP1("org-123"), "Unknown", IOUtils.toInputStream("<xml/>", StandardCharsets.UTF_8), db.getSession());
            Assert.fail();
        } catch (BadRequestException e) {
            assertThat(e).hasMessage("No such importer : Unknown");
        }
    }

    @Test
    public void export_empty_profile() {
        QProfileDto profile = createProfile();
        StringWriter writer = new StringWriter();
        underTest.export(db.getSession(), profile, "standard", writer);
        assertThat(writer.toString()).isEqualTo((("standard -> " + (profile.getName())) + " -> 0"));
        writer = new StringWriter();
        underTest.export(db.getSession(), profile, "xootool", writer);
        assertThat(writer.toString()).isEqualTo((("xoo -> " + (profile.getName())) + " -> 0"));
    }

    @Test
    public void export_profile() {
        QProfileDto profile = createProfile();
        db.qualityProfiles().activateRule(profile, rule);
        StringWriter writer = new StringWriter();
        underTest.export(db.getSession(), profile, "standard", writer);
        assertThat(writer.toString()).isEqualTo((("standard -> " + (profile.getName())) + " -> 1"));
        writer = new StringWriter();
        underTest.export(db.getSession(), profile, "xootool", writer);
        assertThat(writer.toString()).isEqualTo((("xoo -> " + (profile.getName())) + " -> 1"));
    }

    @Test
    public void export_throws_NotFoundException_if_exporter_does_not_exist() {
        QProfileDto profile = createProfile();
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Unknown quality profile exporter: does_not_exist");
        underTest.export(db.getSession(), profile, "does_not_exist", new StringWriter());
    }

    public static class XooExporter extends ProfileExporter {
        public XooExporter() {
            super("xootool", "Xoo Tool");
        }

        @Override
        public String[] getSupportedLanguages() {
            return new String[]{ "xoo" };
        }

        @Override
        public String getMimeType() {
            return "plain/custom";
        }

        @Override
        public void exportProfile(RulesProfile profile, Writer writer) {
            try {
                writer.write(((("xoo -> " + (profile.getName())) + " -> ") + (profile.getActiveRules().size())));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static class StandardExporter extends ProfileExporter {
        public StandardExporter() {
            super("standard", "Standard");
        }

        @Override
        public void exportProfile(RulesProfile profile, Writer writer) {
            try {
                writer.write(((("standard -> " + (profile.getName())) + " -> ") + (profile.getActiveRules().size())));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public class XooProfileImporter extends ProfileImporter {
        public XooProfileImporter() {
            super("XooProfileImporter", "Xoo Profile Importer");
        }

        @Override
        public String[] getSupportedLanguages() {
            return new String[]{ "xoo" };
        }

        @Override
        public RulesProfile importProfile(Reader reader, ValidationMessages messages) {
            RulesProfile rulesProfile = RulesProfile.create();
            rulesProfile.activateRule(org.sonar.api.rules.Rule.create(rule.getRepositoryKey(), rule.getRuleKey()), CRITICAL);
            return rulesProfile;
        }
    }

    public static class XooProfileImporterWithMessages extends ProfileImporter {
        public XooProfileImporterWithMessages() {
            super("XooProfileImporterWithMessages", "Xoo Profile Importer With Message");
        }

        @Override
        public String[] getSupportedLanguages() {
            return new String[]{  };
        }

        @Override
        public RulesProfile importProfile(Reader reader, ValidationMessages messages) {
            messages.addWarningText("a warning");
            messages.addInfoText("an info");
            return RulesProfile.create();
        }
    }

    public static class XooProfileImporterWithError extends ProfileImporter {
        public XooProfileImporterWithError() {
            super("XooProfileImporterWithError", "Xoo Profile Importer With Error");
        }

        @Override
        public RulesProfile importProfile(Reader reader, ValidationMessages messages) {
            messages.addErrorText("error!");
            return RulesProfile.create();
        }
    }
}

