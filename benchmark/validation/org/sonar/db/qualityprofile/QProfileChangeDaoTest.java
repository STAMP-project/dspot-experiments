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
package org.sonar.db.qualityprofile;


import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.AlwaysIncreasingSystem2;
import org.sonar.core.util.SequenceUuidFactory;
import org.sonar.core.util.UuidFactory;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;


public class QProfileChangeDaoTest {
    private System2 system2 = new AlwaysIncreasingSystem2();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(system2);

    private DbSession dbSession = db.getSession();

    private UuidFactory uuidFactory = new SequenceUuidFactory();

    private QProfileChangeDao underTest = new QProfileChangeDao(system2, uuidFactory);

    @Test
    public void insert() {
        QProfileChangeDto dto = insertChange("P1", "ACTIVATED", "marcel_uuid", "some_data");
        verifyInserted(dto);
    }

    /**
     * user_login and data can be null
     */
    @Test
    public void test_insert_with_null_fields() {
        QProfileChangeDto dto = insertChange("P1", "ACTIVATED", null, null);
        verifyInserted(dto);
    }

    @Test
    public void insert_throws_ISE_if_date_is_already_set() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Date of QProfileChangeDto must be set by DAO only. Got 123.");
        underTest.insert(dbSession, new QProfileChangeDto().setCreatedAt(123L));
    }

    @Test
    public void selectByQuery_returns_empty_list_if_profile_does_not_exist() {
        List<QProfileChangeDto> changes = underTest.selectByQuery(dbSession, new QProfileChangeQuery("P1"));
        assertThat(changes).isEmpty();
    }

    @Test
    public void selectByQuery_returns_changes_ordered_by_descending_date() {
        QProfileDto profile1 = db.qualityProfiles().insert(db.getDefaultOrganization());
        QProfileDto profile2 = db.qualityProfiles().insert(db.getDefaultOrganization());
        QProfileChangeDto change1OnP1 = insertChange(profile1, "ACTIVATED", null, null);
        QProfileChangeDto change2OnP1 = insertChange(profile1, "ACTIVATED", null, null);
        QProfileChangeDto changeOnP2 = insertChange(profile2, "ACTIVATED", null, null);
        List<QProfileChangeDto> changes = underTest.selectByQuery(dbSession, new QProfileChangeQuery(profile1.getKee()));
        assertThat(changes).extracting(QProfileChangeDto::getUuid).containsExactly(change2OnP1.getUuid(), change1OnP1.getUuid());
    }

    @Test
    public void selectByQuery_supports_pagination_of_changes() {
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization());
        QProfileChangeDto change1 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeDto change2 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeDto change3 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeDto change4 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeQuery query = new QProfileChangeQuery(profile.getKee());
        query.setOffset(2);
        query.setLimit(1);
        List<QProfileChangeDto> changes = underTest.selectByQuery(dbSession, query);
        assertThat(changes).extracting(QProfileChangeDto::getUuid).containsExactly(change2.getUuid());
    }

    @Test
    public void selectByQuery_returns_changes_after_given_date() {
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization());
        QProfileChangeDto change1 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeDto change2 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeDto change3 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeQuery query = new QProfileChangeQuery(profile.getKee());
        query.setFromIncluded(((change1.getCreatedAt()) + 1));
        assertThat(underTest.selectByQuery(dbSession, query)).extracting(QProfileChangeDto::getUuid).containsExactly(change3.getUuid(), change2.getUuid());
    }

    @Test
    public void selectByQuery_returns_changes_before_given_date() {
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization());
        QProfileChangeDto change1 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeDto change2 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeDto change3 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeQuery query = new QProfileChangeQuery(profile.getKee());
        query.setToExcluded(((change2.getCreatedAt()) + 1));
        assertThat(underTest.selectByQuery(dbSession, query)).extracting(QProfileChangeDto::getUuid).containsExactly(change2.getUuid(), change1.getUuid());
    }

    @Test
    public void selectByQuery_returns_changes_in_a_range_of_dates() {
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization());
        QProfileChangeDto change1 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeDto change2 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeDto change3 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeDto change4 = insertChange(profile, "ACTIVATED", null, null);
        QProfileChangeQuery query = new QProfileChangeQuery(profile.getKee());
        query.setFromIncluded(((change1.getCreatedAt()) + 1));
        query.setToExcluded(change4.getCreatedAt());
        assertThat(underTest.selectByQuery(dbSession, query)).extracting(QProfileChangeDto::getUuid).containsExactly(change3.getUuid(), change2.getUuid());
    }

    @Test
    public void test_selectByQuery_mapping() {
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization());
        QProfileChangeDto inserted = insertChange(profile, "ACTIVATED", "theLogin", "theData");
        List<QProfileChangeDto> result = underTest.selectByQuery(dbSession, new QProfileChangeQuery(profile.getKee()));
        assertThat(result).hasSize(1);
        QProfileChangeDto change = result.get(0);
        assertThat(change.getRulesProfileUuid()).isEqualTo(inserted.getRulesProfileUuid());
        assertThat(change.getUserUuid()).isEqualTo(inserted.getUserUuid());
        assertThat(change.getData()).isEqualTo(inserted.getData());
        assertThat(change.getChangeType()).isEqualTo(inserted.getChangeType());
        assertThat(change.getUuid()).isEqualTo(inserted.getUuid());
        assertThat(change.getCreatedAt()).isEqualTo(inserted.getCreatedAt());
    }

    @Test
    public void countByQuery() {
        QProfileDto profile1 = db.qualityProfiles().insert(db.getDefaultOrganization());
        QProfileDto profile2 = db.qualityProfiles().insert(db.getDefaultOrganization());
        long start = system2.now();
        insertChange(profile1, "ACTIVATED", null, null);
        insertChange(profile1, "ACTIVATED", null, null);
        insertChange(profile2, "ACTIVATED", null, null);
        long end = system2.now();
        assertThat(underTest.countByQuery(dbSession, new QProfileChangeQuery(profile1.getKee()))).isEqualTo(2);
        assertThat(underTest.countByQuery(dbSession, new QProfileChangeQuery(profile2.getKee()))).isEqualTo(1);
        assertThat(underTest.countByQuery(dbSession, new QProfileChangeQuery("does_not_exist"))).isEqualTo(0);
        QProfileChangeQuery query = new QProfileChangeQuery(profile1.getKee());
        query.setToExcluded(start);
        assertThat(underTest.countByQuery(dbSession, query)).isEqualTo(0);
        QProfileChangeQuery query2 = new QProfileChangeQuery(profile1.getKee());
        query2.setToExcluded(end);
        assertThat(underTest.countByQuery(dbSession, query2)).isEqualTo(2);
    }

    @Test
    public void deleteByRulesProfileUuids() {
        QProfileDto profile1 = db.qualityProfiles().insert(db.getDefaultOrganization());
        QProfileDto profile2 = db.qualityProfiles().insert(db.getDefaultOrganization());
        insertChange(profile1, "ACTIVATED", null, null);
        insertChange(profile1, "ACTIVATED", null, null);
        insertChange(profile2, "ACTIVATED", null, null);
        underTest.deleteByRulesProfileUuids(dbSession, Arrays.asList(profile1.getRulesProfileUuid()));
        assertThat(underTest.countByQuery(dbSession, new QProfileChangeQuery(profile1.getKee()))).isEqualTo(0);
        assertThat(underTest.countByQuery(dbSession, new QProfileChangeQuery(profile2.getKee()))).isEqualTo(1);
    }

    @Test
    public void deleteByProfileKeys_does_nothing_if_row_with_specified_key_does_not_exist() {
        QProfileDto profile1 = db.qualityProfiles().insert(db.getDefaultOrganization());
        insertChange(profile1.getRulesProfileUuid(), "ACTIVATED", null, null);
        underTest.deleteByRulesProfileUuids(dbSession, Arrays.asList("does not exist"));
        assertThat(underTest.countByQuery(dbSession, new QProfileChangeQuery(profile1.getKee()))).isEqualTo(1);
    }
}

