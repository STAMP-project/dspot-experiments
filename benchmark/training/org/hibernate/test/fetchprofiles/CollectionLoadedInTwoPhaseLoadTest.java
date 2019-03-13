/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.fetchprofiles;


import FetchProfile.FetchOverride;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import org.hibernate.LazyInitializationException;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12297")
public class CollectionLoadedInTwoPhaseLoadTest extends BaseCoreFunctionalTestCase {
    // NOTE
    // there are two fetch profiles because when I use only one the relation OrgUnit.people
    // is missing in the fetch profile.
    // It is missing because of logic in FetchProfile.addFetch(). Do not understand the implementation
    // of the method now, so the workaround is to use two fetch profiles.
    static final String FETCH_PROFILE_NAME = "fp1";

    static final String FETCH_PROFILE_NAME_2 = "fp2";

    private final String OU_1 = "ou_1";

    private final String OU_2 = "ou_2";

    private final String P_1 = "p_1";

    private final String P_2 = "p_2";

    @Test
    public void testIfEverythingIsLoaded() {
        createSampleData();
        sessionFactory().getStatistics().clear();
        try {
            CollectionLoadedInTwoPhaseLoadTest.OrgUnit ou1 = this.loadOrgUnitWithFetchProfile(OU_1);
            CollectionLoadedInTwoPhaseLoadTest.Person p1 = ou1.findPerson(P_1);
            CollectionLoadedInTwoPhaseLoadTest.OrgUnit ou2 = p1.findOrgUnit(OU_2);
            CollectionLoadedInTwoPhaseLoadTest.Person p2 = ou2.findPerson(P_2);
            @SuppressWarnings("unused")
            String email = p2.getEmail();
            Assert.assertEquals(4, sessionFactory().getStatistics().getEntityLoadCount());
        } catch (LazyInitializationException e) {
            Assert.fail("Everything should be initialized");
        }
    }

    @Entity(name = "OrgUnit")
    @FetchProfile(name = CollectionLoadedInTwoPhaseLoadTest.FETCH_PROFILE_NAME, fetchOverrides = { @FetchOverride(entity = CollectionLoadedInTwoPhaseLoadTest.OrgUnit.class, association = "people", mode = FetchMode.JOIN) })
    public static class OrgUnit {
        @Id
        private String name;

        private String description;

        @ManyToMany(fetch = FetchType.LAZY, mappedBy = "orgUnits", cascade = CascadeType.PERSIST)
        private List<CollectionLoadedInTwoPhaseLoadTest.Person> people = new ArrayList<>();

        public OrgUnit() {
        }

        public OrgUnit(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public CollectionLoadedInTwoPhaseLoadTest.Person findPerson(String personName) {
            if ((people) == null) {
                return null;
            }
            for (CollectionLoadedInTwoPhaseLoadTest.Person person : people) {
                if (person.getName().equals(personName))
                    return person;

            }
            return null;
        }

        public void addPerson(CollectionLoadedInTwoPhaseLoadTest.Person person) {
            if (people.contains(person))
                return;

            people.add(person);
            person.addOrgUnit(this);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<CollectionLoadedInTwoPhaseLoadTest.Person> getPeople() {
            return people;
        }

        public void setPeople(List<CollectionLoadedInTwoPhaseLoadTest.Person> people) {
            this.people = people;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CollectionLoadedInTwoPhaseLoadTest.OrgUnit group = ((CollectionLoadedInTwoPhaseLoadTest.OrgUnit) (o));
            return Objects.equals(name, group.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return (((((("OrgUnit{" + "name='") + (name)) + '\'') + ", description='") + (description)) + '\'') + '}';
        }
    }

    @Entity(name = "Person")
    @FetchProfile(name = CollectionLoadedInTwoPhaseLoadTest.FETCH_PROFILE_NAME_2, fetchOverrides = { @FetchOverride(entity = CollectionLoadedInTwoPhaseLoadTest.Person.class, association = "orgUnits", mode = FetchMode.JOIN) })
    public static class Person {
        @Id
        private String name;

        private String email;

        @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        private List<CollectionLoadedInTwoPhaseLoadTest.OrgUnit> orgUnits = new ArrayList<>();

        public Person() {
        }

        public Person(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public CollectionLoadedInTwoPhaseLoadTest.OrgUnit findOrgUnit(String orgUnitName) {
            if ((orgUnits) == null) {
                return null;
            }
            for (CollectionLoadedInTwoPhaseLoadTest.OrgUnit orgUnit : orgUnits) {
                if (orgUnit.getName().equals(orgUnitName))
                    return orgUnit;

            }
            return null;
        }

        public void addOrgUnit(CollectionLoadedInTwoPhaseLoadTest.OrgUnit orgUnit) {
            if (orgUnits.contains(orgUnit))
                return;

            orgUnits.add(orgUnit);
            orgUnit.addPerson(this);
        }

        public List<CollectionLoadedInTwoPhaseLoadTest.OrgUnit> getOrgUnits() {
            return orgUnits;
        }

        public void setOrgUnits(List<CollectionLoadedInTwoPhaseLoadTest.OrgUnit> orgUnits) {
            this.orgUnits = orgUnits;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CollectionLoadedInTwoPhaseLoadTest.Person person = ((CollectionLoadedInTwoPhaseLoadTest.Person) (o));
            return Objects.equals(name, person.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return (((((("Person{" + "name='") + (name)) + '\'') + ", email='") + (email)) + '\'') + '}';
        }
    }
}

