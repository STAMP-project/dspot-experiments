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
package org.sonar.db.event;


import EventDto.CATEGORY_ALERT;
import EventDto.CATEGORY_PROFILE;
import EventDto.CATEGORY_VERSION;
import System2.INSTANCE;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.SnapshotTesting;


public class EventDaoTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private DbClient dbClient = dbTester.getDbClient();

    private DbSession dbSession = dbTester.getSession();

    private EventDao underTest = dbTester.getDbClient().eventDao();

    @Test
    public void select_by_uuid() {
        org.sonar.db.component.SnapshotDto analysis = dbTester.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(dbTester.organizations().insert()));
        dbTester.events().insertEvent(EventTesting.newEvent(analysis).setUuid("A1"));
        dbTester.events().insertEvent(EventTesting.newEvent(analysis).setUuid("A2"));
        dbTester.events().insertEvent(EventTesting.newEvent(analysis).setUuid("A3"));
        Optional<EventDto> result = underTest.selectByUuid(dbSession, "A2");
        assertThat(result).isPresent();
        assertThat(result.get().getUuid()).isEqualTo("A2");
    }

    @Test
    public void select_by_component_uuid() {
        prepareDbUnit(getClass(), "shared.xml");
        List<EventDto> dtos = underTest.selectByComponentUuid(dbTester.getSession(), "ABCD");
        assertThat(dtos).hasSize(3);
        dtos = underTest.selectByComponentUuid(dbTester.getSession(), "BCDE");
        assertThat(dtos).hasSize(1);
        EventDto dto = dtos.get(0);
        assertThat(dto.getId()).isEqualTo(4L);
        assertThat(dto.getUuid()).isEqualTo("E4");
        assertThat(dto.getAnalysisUuid()).isEqualTo("uuid_1");
        assertThat(dto.getComponentUuid()).isEqualTo("BCDE");
        assertThat(dto.getName()).isEqualTo("1.0");
        assertThat(dto.getCategory()).isEqualTo("Version");
        assertThat(dto.getDescription()).isEqualTo("Version 1.0");
        assertThat(dto.getData()).isEqualTo("some data");
        assertThat(dto.getDate()).isEqualTo(1413407091086L);
        assertThat(dto.getCreatedAt()).isEqualTo(1225630680000L);
    }

    @Test
    public void select_by_analysis_uuid() {
        ComponentDto project = ComponentTesting.newPrivateProjectDto(dbTester.getDefaultOrganization());
        org.sonar.db.component.SnapshotDto analysis = dbTester.components().insertProjectAndSnapshot(project);
        org.sonar.db.component.SnapshotDto otherAnalysis = dbClient.snapshotDao().insert(dbSession, SnapshotTesting.newAnalysis(project));
        dbTester.commit();
        dbTester.events().insertEvent(EventTesting.newEvent(analysis).setUuid("A1"));
        dbTester.events().insertEvent(EventTesting.newEvent(otherAnalysis).setUuid("O1"));
        dbTester.events().insertEvent(EventTesting.newEvent(analysis).setUuid("A2"));
        dbTester.events().insertEvent(EventTesting.newEvent(otherAnalysis).setUuid("O2"));
        dbTester.events().insertEvent(EventTesting.newEvent(analysis).setUuid("A3"));
        dbTester.events().insertEvent(EventTesting.newEvent(otherAnalysis).setUuid("O3"));
        List<EventDto> result = underTest.selectByAnalysisUuid(dbSession, analysis.getUuid());
        assertThat(result).hasSize(3);
        assertThat(result).extracting(EventDto::getUuid).containsOnly("A1", "A2", "A3");
    }

    @Test
    public void select_by_analysis_uuids() {
        ComponentDto project = dbTester.components().insertPrivateProject();
        org.sonar.db.component.SnapshotDto a1 = dbTester.components().insertSnapshot(SnapshotTesting.newAnalysis(project));
        org.sonar.db.component.SnapshotDto a2 = dbTester.components().insertSnapshot(SnapshotTesting.newAnalysis(project));
        org.sonar.db.component.SnapshotDto a42 = dbTester.components().insertSnapshot(SnapshotTesting.newAnalysis(project));
        dbTester.events().insertEvent(EventTesting.newEvent(SnapshotTesting.newAnalysis(project)));
        dbTester.events().insertEvent(EventTesting.newEvent(a1).setUuid("A11"));
        dbTester.events().insertEvent(EventTesting.newEvent(a1).setUuid("A12"));
        dbTester.events().insertEvent(EventTesting.newEvent(a1).setUuid("A13"));
        dbTester.events().insertEvent(EventTesting.newEvent(a2).setUuid("A21"));
        dbTester.events().insertEvent(EventTesting.newEvent(a2).setUuid("A22"));
        dbTester.events().insertEvent(EventTesting.newEvent(a2).setUuid("A23"));
        dbTester.events().insertEvent(EventTesting.newEvent(a42).setUuid("AO1"));
        dbTester.events().insertEvent(EventTesting.newEvent(a42).setUuid("AO2"));
        dbTester.events().insertEvent(EventTesting.newEvent(a42).setUuid("AO3"));
        List<EventDto> result = underTest.selectByAnalysisUuids(dbSession, Lists.newArrayList(a1.getUuid(), a2.getUuid()));
        assertThat(result).hasSize(6);
        assertThat(result).extracting(EventDto::getUuid).containsOnly("A11", "A12", "A13", "A21", "A22", "A23");
    }

    @Test
    public void return_different_categories() {
        prepareDbUnit(getClass(), "shared.xml");
        List<EventDto> dtos = underTest.selectByComponentUuid(dbTester.getSession(), "ABCD");
        assertThat(dtos).extracting("category").containsOnly(CATEGORY_ALERT, CATEGORY_PROFILE, CATEGORY_VERSION);
    }

    @Test
    public void insert() {
        prepareDbUnit(getClass(), "empty.xml");
        underTest.insert(dbTester.getSession(), new EventDto().setUuid("E1").setAnalysisUuid("uuid_1").setComponentUuid("ABCD").setName("1.0").setCategory(CATEGORY_VERSION).setDescription("Version 1.0").setData("some data").setDate(1413407091086L).setCreatedAt(1225630680000L));
        dbTester.getSession().commit();
        assertDbUnit(getClass(), "insert-result.xml", new String[]{ "id" }, "events");
    }

    @Test
    public void update_name_and_description() {
        org.sonar.db.component.SnapshotDto analysis = dbTester.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(dbTester.organizations().insert()));
        dbTester.events().insertEvent(EventTesting.newEvent(analysis).setUuid("E1"));
        underTest.update(dbSession, "E1", "New Name", "New Description");
        EventDto result = dbClient.eventDao().selectByUuid(dbSession, "E1").get();
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getDescription()).isEqualTo("New Description");
    }

    @Test
    public void delete_by_id() {
        prepareDbUnit(getClass(), "delete.xml");
        underTest.delete(dbTester.getSession(), 1L);
        dbTester.getSession().commit();
        assertThat(dbTester.countRowsOfTable("events")).isEqualTo(0);
    }

    @Test
    public void delete_by_uuid() {
        dbTester.events().insertEvent(EventTesting.newEvent(SnapshotTesting.newAnalysis(ComponentTesting.newPrivateProjectDto(dbTester.getDefaultOrganization()))).setUuid("E1"));
        underTest.delete(dbTester.getSession(), "E1");
        dbTester.commit();
        assertThat(dbTester.countRowsOfTable("events")).isEqualTo(0);
    }
}

