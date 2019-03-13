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
package org.sonar.server.platform.db.migration.version.v64;


import java.sql.SQLException;
import java.util.Date;
import javax.annotation.CheckForNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.CoreDbTester;
import org.sonar.server.platform.db.migration.version.v63.DefaultOrganizationUuidProviderImpl;


public class PopulateRulesMetadataTest {
    private static final String TABLE_RULES_METADATA = "rules_metadata";

    private static final String TABLE_RULES = "rules";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateRulesMetadataTest.class, "rules_and_rules_metadata_and_organization_and_internal_properties.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private System2 system2 = Mockito.mock(System2.class);

    private PopulateRulesMetadata underTest = new PopulateRulesMetadata(db.database(), new DefaultOrganizationUuidProviderImpl(), system2);

    @Test
    public void fails_with_ISE_when_no_default_organization_is_set() throws SQLException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default organization uuid is missing");
        underTest.execute();
    }

    @Test
    public void fails_with_ISE_when_default_organization_does_not_exist_in_table_ORGANIZATIONS() throws SQLException {
        db.defaultOrganization().insertInternalProperty("blabla");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default organization with uuid 'blabla' does not exist in table ORGANIZATIONS");
        underTest.execute();
    }

    @Test
    public void execute_has_no_effect_if_loaded_templates_table_is_empty() throws Exception {
        db.defaultOrganization().setupDefaultOrganization();
        underTest.execute();
        assertThat(db.countRowsOfTable(PopulateRulesMetadataTest.TABLE_RULES_METADATA)).isEqualTo(0);
    }

    @Test
    public void execute_has_no_effect_if_rules_is_empty() throws Exception {
        db.defaultOrganization().setupDefaultOrganization();
        underTest.execute();
        assertThat(db.countRowsOfTable(PopulateRulesMetadataTest.TABLE_RULES_METADATA)).isEqualTo(0);
        assertThat(db.countRowsOfTable(PopulateRulesMetadataTest.TABLE_RULES)).isEqualTo(0);
    }

    @Test
    public void execute_creates_row_with_data_from_table_rules_for_each_row_in_table_rules() throws SQLException {
        String defaultOrganizationUuid = db.defaultOrganization().setupDefaultOrganization();
        PopulateRulesMetadataTest.Metadata[] metadatas = new PopulateRulesMetadataTest.Metadata[]{ new PopulateRulesMetadataTest.Metadata(), new PopulateRulesMetadataTest.Metadata("foo"), new PopulateRulesMetadataTest.Metadata().withNote("bar"), new PopulateRulesMetadataTest.Metadata().withRemediation("doh"), new PopulateRulesMetadataTest.Metadata().withTags("taggeah!") };
        PopulateRulesMetadataTest.Dates noDates = new PopulateRulesMetadataTest.Dates();
        PopulateRulesMetadataTest.Dates dates = new PopulateRulesMetadataTest.Dates(122334);
        long now = 666666666L;
        PopulateRulesMetadataTest.Dates defaultDates = new PopulateRulesMetadataTest.Dates(now, now);
        long[] ruleIds = new long[]{ insertRule("r10", noDates, metadatas[0]), insertRule("r11", dates, metadatas[0]), insertRule("r20", noDates, metadatas[1]), insertRule("r21", dates, metadatas[1]), insertRule("r30", noDates, metadatas[2]), insertRule("r31", dates, metadatas[2]), insertRule("r40", noDates, metadatas[3]), insertRule("r41", dates, metadatas[3]), insertRule("r50", noDates, metadatas[4]), insertRule("r51", dates, metadatas[4]) };
        Mockito.when(system2.now()).thenReturn(now);
        underTest.execute();
        verifyRulesMetadata(ruleIds[0], defaultOrganizationUuid, defaultDates, metadatas[0]);
        verifyRulesMetadata(ruleIds[1], defaultOrganizationUuid, dates, metadatas[0]);
        verifyRulesMetadata(ruleIds[2], defaultOrganizationUuid, defaultDates, metadatas[1]);
        verifyRulesMetadata(ruleIds[3], defaultOrganizationUuid, dates, metadatas[1]);
        verifyRulesMetadata(ruleIds[4], defaultOrganizationUuid, defaultDates, metadatas[2]);
        verifyRulesMetadata(ruleIds[5], defaultOrganizationUuid, dates, metadatas[2]);
        verifyRulesMetadata(ruleIds[6], defaultOrganizationUuid, defaultDates, metadatas[3]);
        verifyRulesMetadata(ruleIds[7], defaultOrganizationUuid, dates, metadatas[3]);
        verifyRulesMetadata(ruleIds[8], defaultOrganizationUuid, defaultDates, metadatas[4]);
        verifyRulesMetadata(ruleIds[9], defaultOrganizationUuid, dates, metadatas[4]);
    }

    @Test
    public void execute_creates_does_not_update_rows_in_table_RULES_METADATA() throws SQLException {
        db.defaultOrganization().setupDefaultOrganization();
        long ruleId = insertRule("r1", new PopulateRulesMetadataTest.Dates(999), new PopulateRulesMetadataTest.Metadata("foo"));
        insertRuleMetadata(ruleId, "other org uuid", new PopulateRulesMetadataTest.Dates(1000));
        underTest.execute();
        verifyRulesMetadata(ruleId, "other org uuid", new PopulateRulesMetadataTest.Dates(1000), new PopulateRulesMetadataTest.Metadata());
    }

    @Test
    public void execute_is_reentrant() throws SQLException {
        db.defaultOrganization().setupDefaultOrganization();
        insertRule("r1", new PopulateRulesMetadataTest.Dates(999), new PopulateRulesMetadataTest.Metadata("foo"));
        insertRule("r2", new PopulateRulesMetadataTest.Dates(10888), new PopulateRulesMetadataTest.Metadata("bar"));
        underTest.execute();
        underTest.execute();
    }

    private static final class Metadata {
        private String noteData;

        private String noteUserLogin;

        private PopulateRulesMetadataTest.Dates noteDates;

        private String remediationFunction;

        private String remediationGapMult;

        private String remediationBaseEffort;

        private String tags;

        private Metadata() {
            this.noteData = null;
            this.noteUserLogin = null;
            this.noteDates = new PopulateRulesMetadataTest.Dates();
            this.remediationFunction = null;
            this.remediationGapMult = null;
            this.remediationBaseEffort = null;
            this.tags = null;
        }

        private Metadata(String seed) {
            withNote(seed);
            withRemediation(seed);
            this.tags = seed + "_tags";
        }

        private PopulateRulesMetadataTest.Metadata withNote(String seed) {
            this.noteData = seed + "_noteData";
            this.noteUserLogin = seed + "_noteUserLogin";
            this.noteDates = new PopulateRulesMetadataTest.Dates(seed.hashCode());
            return this;
        }

        private PopulateRulesMetadataTest.Metadata withTags(String tags) {
            this.tags = tags;
            return this;
        }

        private PopulateRulesMetadataTest.Metadata withRemediation(String seed) {
            this.remediationFunction = seed + "_Function";
            this.remediationGapMult = seed + "_GapMult";
            this.remediationBaseEffort = seed + "_BaseEffort";
            return this;
        }
    }

    private static final class Dates {
        private final Long createdAt;

        private final Long updatedAt;

        private Dates() {
            this.createdAt = null;
            this.updatedAt = null;
        }

        private Dates(long seed) {
            this.createdAt = seed + 5778765L;
            this.updatedAt = seed + 9111100L;
        }

        public Dates(long createdAt, long updatedAt) {
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        @CheckForNull
        public Long getCreatedAt() {
            return createdAt;
        }

        @CheckForNull
        public Date getCreatedAtDate() {
            return (createdAt) == null ? null : new Date(createdAt);
        }

        @CheckForNull
        public Long getUpdatedAt() {
            return updatedAt;
        }

        @CheckForNull
        public Date getUpdatedAtDate() {
            return (updatedAt) == null ? null : new Date(updatedAt);
        }
    }
}

