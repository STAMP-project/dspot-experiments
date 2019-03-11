/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.sqlobject;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class TestFoldToObjectGraph {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withPlugin(new SqlObjectPlugin());

    private Handle handle;

    private Map<String, TestFoldToObjectGraph.Team> expected;

    @Test
    public void testSqlObjectApi() {
        TestFoldToObjectGraph.Dao dao = handle.attach(TestFoldToObjectGraph.Dao.class);
        assertThat(dao.findAllTeams()).isEqualTo(expected);
    }

    public interface Dao {
        @SqlQuery("select t.name as teamName, " + (((" t.mascot as mascot, " + " p.name as personName, ") + " p.role as role ") + "from team t inner join person p on (t.name = p.team)"))
        @RegisterBeanMapper(TestFoldToObjectGraph.TeamPersonJoinRow.class)
        Iterator<TestFoldToObjectGraph.TeamPersonJoinRow> findAllTeamsAndPeople();

        default Map<String, TestFoldToObjectGraph.Team> findAllTeams() {
            Iterator<TestFoldToObjectGraph.TeamPersonJoinRow> i = findAllTeamsAndPeople();
            Map<String, TestFoldToObjectGraph.Team> acc = new HashMap<>();
            while (i.hasNext()) {
                TestFoldToObjectGraph.TeamPersonJoinRow row = i.next();
                if (!(acc.containsKey(row.getTeamName()))) {
                    acc.put(row.getTeamName(), new TestFoldToObjectGraph.Team(row.getTeamName(), row.getMascot()));
                }
                acc.get(row.getTeamName()).getPeople().add(new TestFoldToObjectGraph.Person(row.getPersonName(), row.getRole()));
            } 
            return acc;
        }
    }

    public static class Team {
        private final String name;

        private final String mascot;

        private final Set<TestFoldToObjectGraph.Person> people = new LinkedHashSet<>();

        public Team(String name, String mascot) {
            this.name = name;
            this.mascot = mascot;
        }

        public String getName() {
            return name;
        }

        public String getMascot() {
            return mascot;
        }

        public Set<TestFoldToObjectGraph.Person> getPeople() {
            return this.people;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            TestFoldToObjectGraph.Team that = ((TestFoldToObjectGraph.Team) (o));
            return ((Objects.equals(this.mascot, that.mascot)) && (Objects.equals(this.name, that.name))) && (Objects.equals(this.people, that.people));
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, mascot, people);
        }
    }

    public static class Person {
        private final String name;

        private final String role;

        public Person(String name, String role) {
            this.name = name;
            this.role = role;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            TestFoldToObjectGraph.Person person = ((TestFoldToObjectGraph.Person) (o));
            return (name.equals(person.name)) && (role.equals(person.role));
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = (31 * result) + (role.hashCode());
            return result;
        }
    }

    public static class TeamPersonJoinRow {
        private String teamName;

        private String mascot;

        private String personName;

        private String role;

        public String getTeamName() {
            return teamName;
        }

        public String getMascot() {
            return mascot;
        }

        public String getPersonName() {
            return personName;
        }

        public String getRole() {
            return role;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public void setMascot(String mascot) {
            this.mascot = mascot;
        }

        public void setPersonName(String personName) {
            this.personName = personName;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}

