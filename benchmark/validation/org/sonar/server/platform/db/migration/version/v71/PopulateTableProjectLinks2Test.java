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
package org.sonar.server.platform.db.migration.version.v71;


import java.sql.SQLException;
import java.util.stream.Collectors;
import org.assertj.core.groups.Tuple;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.core.util.SequenceUuidFactory;
import org.sonar.core.util.UuidFactory;
import org.sonar.db.CoreDbTester;


public class PopulateTableProjectLinks2Test {
    private static final long PAST = 5000000000L;

    private static final long NOW = 10000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateTableProjectLinks2Test.class, "project_links2.sql");

    private System2 system2 = new TestSystem2().setNow(PopulateTableProjectLinks2Test.NOW);

    private UuidFactory uuidFactory = new SequenceUuidFactory();

    private PopulateTableProjectLinks2 underTest = new PopulateTableProjectLinks2(db.database(), uuidFactory, system2);

    @Test
    public void copy_custom_links() throws SQLException {
        String project = insertProject();
        insertProjectLink("Name1", "custom1", "http://link1", project);
        insertProjectLink("Name2", "custom2", "http://link2", project);
        underTest.execute();
        assertProjectLinks2(tuple("Name1", "custom1", "http://link1", project, PopulateTableProjectLinks2Test.NOW, PopulateTableProjectLinks2Test.NOW), tuple("Name2", "custom2", "http://link2", project, PopulateTableProjectLinks2Test.NOW, PopulateTableProjectLinks2Test.NOW));
    }

    @Test
    public void remove_name_of_provided_links() throws SQLException {
        String project = insertProject();
        insertProjectLink("Home", "homepage", "http://homepage", project);
        insertProjectLink("CI", "ci", "http://ci", project);
        insertProjectLink("Jira", "issue", "http://issue", project);
        insertProjectLink("SCM", "scm", "http://scm", project);
        underTest.execute();
        assertProjectLinks2(tuple(null, "homepage", "http://homepage", project, PopulateTableProjectLinks2Test.NOW, PopulateTableProjectLinks2Test.NOW), tuple(null, "ci", "http://ci", project, PopulateTableProjectLinks2Test.NOW, PopulateTableProjectLinks2Test.NOW), tuple(null, "issue", "http://issue", project, PopulateTableProjectLinks2Test.NOW, PopulateTableProjectLinks2Test.NOW), tuple(null, "scm", "http://scm", project, PopulateTableProjectLinks2Test.NOW, PopulateTableProjectLinks2Test.NOW));
    }

    @Test
    public void copy_links_from_different_projects() throws SQLException {
        String project1 = insertProject();
        insertProjectLink("Name", "custom", "http://link1", project1);
        String project2 = insertProject();
        insertProjectLink("Name", "custom", "http://link2", project2);
        underTest.execute();
        assertProjectLinks2(tuple("Name", "custom", "http://link1", project1, PopulateTableProjectLinks2Test.NOW, PopulateTableProjectLinks2Test.NOW), tuple("Name", "custom", "http://link2", project2, PopulateTableProjectLinks2Test.NOW, PopulateTableProjectLinks2Test.NOW));
    }

    @Test
    public void do_not_copy_links_from_developer_connection_link() throws SQLException {
        insertProjectLink("Dev", "scm_dev", "http://link1", insertProject());
        underTest.execute();
        assertNoProjectLinks2();
    }

    @Test
    public void do_not_copy_links_from_components_that_are_not_projects() throws SQLException {
        insertProjectLink("Name", "custom", "http://link1", insertComponent("PRJ", "BRC"));
        insertProjectLink("Name", "custom", "http://link2", insertComponent("PRJ", "VW"));
        insertProjectLink("Name", "custom", "http://link1", insertComponent("DIR", "DIR"));
        insertProjectLink("Name", "custom", "http://link1", "UNKNOWN");
        underTest.execute();
        assertNoProjectLinks2();
    }

    @Test
    public void do_not_copy_already_copied_data() throws SQLException {
        String project = insertProject();
        insertProjectLink("Name", "custom", "http://link", project);
        insertProjectLink("Home", "homepage", "http://homepage", project);
        insertProjectLink2("UUID1", "Name", "custom", "http://link", project, PopulateTableProjectLinks2Test.PAST);
        insertProjectLink2("UUID2", null, "homepage", "http://homepage", project, PopulateTableProjectLinks2Test.PAST);
        underTest.execute();
        assertThat(db.select("SELECT UUID, NAME, LINK_TYPE, HREF, PROJECT_UUID, CREATED_AT FROM PROJECT_LINKS2").stream().map(( map) -> new Tuple(map.get("UUID"), map.get("NAME"), map.get("LINK_TYPE"), map.get("HREF"), map.get("PROJECT_UUID"), map.get("CREATED_AT"))).collect(Collectors.toList())).containsExactlyInAnyOrder(tuple("UUID1", "Name", "custom", "http://link", project, PopulateTableProjectLinks2Test.PAST), tuple("UUID2", null, "homepage", "http://homepage", project, PopulateTableProjectLinks2Test.PAST));
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        String project = insertProject();
        insertProjectLink("Name", "custom", "http://link", project);
        underTest.execute();
        underTest.execute();
        assertProjectLinks2(tuple("Name", "custom", "http://link", project, PopulateTableProjectLinks2Test.NOW, PopulateTableProjectLinks2Test.NOW));
    }

    @Test
    public void has_no_effect_if_table_is_empty() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable("project_links2")).isZero();
    }
}

