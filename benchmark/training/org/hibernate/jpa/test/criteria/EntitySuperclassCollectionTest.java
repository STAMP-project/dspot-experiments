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
public class EntitySuperclassCollectionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10556")
    public void testPerson() {
        String address = "super-address";
        EntitySuperclassCollectionTest.PersonBase person = createPerson(new EntitySuperclassCollectionTest.Person(), address);
        assertAddress(person, address);
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

    @Entity(name = "PersonBase")
    public abstract static class PersonBase {
        @Id
        @GeneratedValue
        Integer id;

        @OneToMany(cascade = CascadeType.ALL)
        List<EntitySuperclassCollectionTest.Address> addresses = new ArrayList<EntitySuperclassCollectionTest.Address>();
    }

    @Entity(name = "Person")
    public static class Person extends EntitySuperclassCollectionTest.PersonBase {}
}

