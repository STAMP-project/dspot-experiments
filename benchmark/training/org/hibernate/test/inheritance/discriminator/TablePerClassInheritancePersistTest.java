/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.inheritance.discriminator;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Davide D'Alto
 */
@TestForIssue(jiraKey = "HHH-12332")
public class TablePerClassInheritancePersistTest extends BaseCoreFunctionalTestCase {
    private final TablePerClassInheritancePersistTest.Man john = new TablePerClassInheritancePersistTest.Man("John", "Riding Roller Coasters");

    private final TablePerClassInheritancePersistTest.Woman jane = new TablePerClassInheritancePersistTest.Woman("Jane", "Hippotherapist");

    private final TablePerClassInheritancePersistTest.Child susan = new TablePerClassInheritancePersistTest.Child("Susan", "Super Mario retro Mushroom");

    private final TablePerClassInheritancePersistTest.Child mark = new TablePerClassInheritancePersistTest.Child("Mark", "Fidget Spinner");

    private final TablePerClassInheritancePersistTest.Family family = new TablePerClassInheritancePersistTest.Family("McCloud");

    private final List<TablePerClassInheritancePersistTest.Child> children = new ArrayList<>(Arrays.asList(susan, mark));

    private final List<TablePerClassInheritancePersistTest.Person> familyMembers = Arrays.asList(john, jane, susan, mark);

    @Test
    public void testPolymorphicAssociation() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session1) -> {
            org.hibernate.test.inheritance.discriminator.Family family = session1.createQuery("FROM Family f", .class).getSingleResult();
            List<org.hibernate.test.inheritance.discriminator.Person> members = family.getMembers();
            assertThat(members.size(), is(familyMembers.size()));
            for (org.hibernate.test.inheritance.discriminator.Person person : members) {
                if (person instanceof org.hibernate.test.inheritance.discriminator.Man) {
                    assertThat(((org.hibernate.test.inheritance.discriminator.Man) (person)).getHobby(), is(john.getHobby()));
                } else
                    if (person instanceof org.hibernate.test.inheritance.discriminator.Woman) {
                        assertThat(((org.hibernate.test.inheritance.discriminator.Woman) (person)).getJob(), is(jane.getJob()));
                    } else
                        if (person instanceof org.hibernate.test.inheritance.discriminator.Child) {
                            if (person.getName().equals("Susan")) {
                                assertThat(((org.hibernate.test.inheritance.discriminator.Child) (person)).getFavouriteToy(), is(susan.getFavouriteToy()));
                            } else {
                                assertThat(((org.hibernate.test.inheritance.discriminator.Child) (person)).getFavouriteToy(), is(mark.getFavouriteToy()));
                            }
                        } else {
                            fail(("Unexpected result: " + person));
                        }


            }
        });
    }

    @Entity(name = "Family")
    public static class Family {
        @Id
        private String name;

        @OneToMany(mappedBy = "familyName", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<TablePerClassInheritancePersistTest.Person> members = new ArrayList<>();

        public Family() {
        }

        public Family(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<TablePerClassInheritancePersistTest.Person> getMembers() {
            return members;
        }

        public void setMembers(List<TablePerClassInheritancePersistTest.Person> members) {
            this.members = members;
        }

        public void add(TablePerClassInheritancePersistTest.Person person) {
            person.setFamilyName(this);
            members.add(person);
        }

        @Override
        public String toString() {
            return ("Family [name=" + (name)) + "]";
        }
    }

    @Entity(name = "Person")
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    @DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
    public static class Person {
        @Id
        private String name;

        @ManyToOne
        private TablePerClassInheritancePersistTest.Family familyName;

        public Person() {
        }

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public TablePerClassInheritancePersistTest.Family getFamilyName() {
            return familyName;
        }

        public void setFamilyName(TablePerClassInheritancePersistTest.Family familyName) {
            this.familyName = familyName;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Entity(name = "Child")
    @DiscriminatorValue("CHILD")
    public static class Child extends TablePerClassInheritancePersistTest.Person {
        private String favouriteToy;

        @OneToOne
        private TablePerClassInheritancePersistTest.Woman mother;

        @OneToOne
        private TablePerClassInheritancePersistTest.Man father;

        public Child() {
        }

        public Child(String name, String favouriteToy) {
            super(name);
            this.favouriteToy = favouriteToy;
        }

        public String getFavouriteToy() {
            return favouriteToy;
        }

        public void setFavouriteToy(String favouriteToy) {
            this.favouriteToy = favouriteToy;
        }

        public TablePerClassInheritancePersistTest.Man getFather() {
            return father;
        }

        public void setFather(TablePerClassInheritancePersistTest.Man father) {
            this.father = father;
        }

        public TablePerClassInheritancePersistTest.Woman getMother() {
            return mother;
        }

        public void setMother(TablePerClassInheritancePersistTest.Woman mother) {
            this.mother = mother;
        }
    }

    @Entity(name = "Man")
    @DiscriminatorValue("MAN")
    public static class Man extends TablePerClassInheritancePersistTest.Person {
        private String hobby;

        @OneToOne
        private TablePerClassInheritancePersistTest.Woman wife;

        @OneToMany(mappedBy = "father")
        private List<TablePerClassInheritancePersistTest.Child> children = new ArrayList<>();

        public Man() {
        }

        public Man(String name, String hobby) {
            super(name);
            this.hobby = hobby;
        }

        public String getHobby() {
            return hobby;
        }

        public void setHobby(String hobby) {
            this.hobby = hobby;
        }

        public TablePerClassInheritancePersistTest.Woman getWife() {
            return wife;
        }

        public void setWife(TablePerClassInheritancePersistTest.Woman wife) {
            this.wife = wife;
        }

        public List<TablePerClassInheritancePersistTest.Child> getChildren() {
            return children;
        }

        public void setChildren(List<TablePerClassInheritancePersistTest.Child> children) {
            this.children = children;
        }
    }

    @Entity(name = "Woman")
    @DiscriminatorValue("WOMAN")
    public static class Woman extends TablePerClassInheritancePersistTest.Person {
        private String job;

        @OneToOne
        private TablePerClassInheritancePersistTest.Man husband;

        @OneToMany(mappedBy = "mother")
        private List<TablePerClassInheritancePersistTest.Child> children = new ArrayList<>();

        public Woman() {
        }

        public Woman(String name, String job) {
            super(name);
            this.job = job;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public TablePerClassInheritancePersistTest.Man getHusband() {
            return husband;
        }

        public void setHusband(TablePerClassInheritancePersistTest.Man husband) {
            this.husband = husband;
        }

        public List<TablePerClassInheritancePersistTest.Child> getChildren() {
            return children;
        }

        public void setChildren(List<TablePerClassInheritancePersistTest.Child> children) {
            this.children = children;
        }
    }
}

