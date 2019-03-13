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
public class SingleTableInheritancePersistTest extends BaseCoreFunctionalTestCase {
    private final SingleTableInheritancePersistTest.Man john = new SingleTableInheritancePersistTest.Man("John", "Riding Roller Coasters");

    private final SingleTableInheritancePersistTest.Woman jane = new SingleTableInheritancePersistTest.Woman("Jane", "Hippotherapist");

    private final SingleTableInheritancePersistTest.Child susan = new SingleTableInheritancePersistTest.Child("Susan", "Super Mario retro Mushroom");

    private final SingleTableInheritancePersistTest.Child mark = new SingleTableInheritancePersistTest.Child("Mark", "Fidget Spinner");

    private final SingleTableInheritancePersistTest.Family family = new SingleTableInheritancePersistTest.Family("McCloud");

    private final List<SingleTableInheritancePersistTest.Child> children = new ArrayList<>(Arrays.asList(susan, mark));

    private final List<SingleTableInheritancePersistTest.Person> familyMembers = Arrays.asList(john, jane, susan, mark);

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
        private List<SingleTableInheritancePersistTest.Person> members = new ArrayList<>();

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

        public List<SingleTableInheritancePersistTest.Person> getMembers() {
            return members;
        }

        public void setMembers(List<SingleTableInheritancePersistTest.Person> members) {
            this.members = members;
        }

        public void add(SingleTableInheritancePersistTest.Person person) {
            person.setFamilyName(this);
            members.add(person);
        }

        @Override
        public String toString() {
            return ("Family [name=" + (name)) + "]";
        }
    }

    @Entity(name = "Person")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
    public static class Person {
        @Id
        private String name;

        @ManyToOne
        private SingleTableInheritancePersistTest.Family familyName;

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

        public SingleTableInheritancePersistTest.Family getFamilyName() {
            return familyName;
        }

        public void setFamilyName(SingleTableInheritancePersistTest.Family familyName) {
            this.familyName = familyName;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Entity(name = "Child")
    @DiscriminatorValue("CHILD")
    public static class Child extends SingleTableInheritancePersistTest.Person {
        private String favouriteToy;

        @OneToOne
        private SingleTableInheritancePersistTest.Woman mother;

        @OneToOne
        private SingleTableInheritancePersistTest.Man father;

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

        public SingleTableInheritancePersistTest.Man getFather() {
            return father;
        }

        public void setFather(SingleTableInheritancePersistTest.Man father) {
            this.father = father;
        }

        public SingleTableInheritancePersistTest.Woman getMother() {
            return mother;
        }

        public void setMother(SingleTableInheritancePersistTest.Woman mother) {
            this.mother = mother;
        }
    }

    @Entity(name = "Man")
    @DiscriminatorValue("MAN")
    public static class Man extends SingleTableInheritancePersistTest.Person {
        private String hobby;

        @OneToOne
        private SingleTableInheritancePersistTest.Woman wife;

        @OneToMany(mappedBy = "father")
        private List<SingleTableInheritancePersistTest.Child> children = new ArrayList<>();

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

        public SingleTableInheritancePersistTest.Woman getWife() {
            return wife;
        }

        public void setWife(SingleTableInheritancePersistTest.Woman wife) {
            this.wife = wife;
        }

        public List<SingleTableInheritancePersistTest.Child> getChildren() {
            return children;
        }

        public void setChildren(List<SingleTableInheritancePersistTest.Child> children) {
            this.children = children;
        }
    }

    @Entity(name = "Woman")
    @DiscriminatorValue("WOMAN")
    public static class Woman extends SingleTableInheritancePersistTest.Person {
        private String job;

        @OneToOne
        private SingleTableInheritancePersistTest.Man husband;

        @OneToMany(mappedBy = "mother")
        private List<SingleTableInheritancePersistTest.Child> children = new ArrayList<>();

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

        public SingleTableInheritancePersistTest.Man getHusband() {
            return husband;
        }

        public void setHusband(SingleTableInheritancePersistTest.Man husband) {
            this.husband = husband;
        }

        public List<SingleTableInheritancePersistTest.Child> getChildren() {
            return children;
        }

        public void setChildren(List<SingleTableInheritancePersistTest.Child> children) {
            this.children = children;
        }
    }
}

