/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Janario Oliveira
 * @author Gail Badner
 */
public class SuperclassCollectionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testPerson() {
        String address = "super-address";
        String localAddress = "local-address";
        SuperclassCollectionTest.PersonBaseBase person = createPerson(new SuperclassCollectionTest.Person(), address, localAddress);
        assertAddress(person, address, localAddress);
    }

    @Test
    public void testOtherSubclass() {
        String address = "other-super-address";
        String localAddress = "other-local-address";
        SuperclassCollectionTest.PersonBaseBase person = createPerson(new SuperclassCollectionTest.OtherSubclass(), address, localAddress);
        assertAddress(person, address, localAddress);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10556")
    public void testOtherPerson() {
        String address = "other-person-super-address";
        String localAddress = "other-person-local-address";
        SuperclassCollectionTest.PersonBaseBase person = createPerson(new SuperclassCollectionTest.OtherPerson(), address, localAddress);
        assertAddress(person, address, localAddress);
    }

    @Entity(name = "Address")
    public static class Address {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

        protected Address() {
        }

        public Address(String name) {
            this.name = name;
        }
    }

    @MappedSuperclass
    public abstract static class PersonBaseBase {
        @Id
        @GeneratedValue
        Integer id;

        @OneToMany(cascade = CascadeType.ALL)
        List<SuperclassCollectionTest.Address> addresses = new ArrayList<SuperclassCollectionTest.Address>();

        protected abstract List<SuperclassCollectionTest.Address> getLocalAddresses();
    }

    @MappedSuperclass
    public abstract static class PersonBase extends SuperclassCollectionTest.PersonBaseBase {}

    @Entity(name = "Person")
    public static class Person extends SuperclassCollectionTest.PersonBase {
        @OneToMany(cascade = CascadeType.ALL)
        @JoinTable(name = "person_localaddress")
        List<SuperclassCollectionTest.Address> localAddresses = new ArrayList<SuperclassCollectionTest.Address>();

        @Override
        public List<SuperclassCollectionTest.Address> getLocalAddresses() {
            return localAddresses;
        }
    }

    @MappedSuperclass
    public static class OtherPersonBase extends SuperclassCollectionTest.Person {}

    @Entity(name = "OtherPerson")
    public static class OtherPerson extends SuperclassCollectionTest.OtherPersonBase {}

    @Entity(name = "OtherSubclass")
    public static class OtherSubclass extends SuperclassCollectionTest.PersonBaseBase {
        @OneToMany(cascade = CascadeType.ALL)
        @JoinTable(name = "other_person_localaddress")
        List<SuperclassCollectionTest.Address> localAddresses = new ArrayList<SuperclassCollectionTest.Address>();

        @Override
        public List<SuperclassCollectionTest.Address> getLocalAddresses() {
            return localAddresses;
        }
    }
}

