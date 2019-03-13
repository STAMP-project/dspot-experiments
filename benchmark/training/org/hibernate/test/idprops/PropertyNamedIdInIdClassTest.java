/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idprops;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class PropertyNamedIdInIdClassTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-13084")
    public void testHql() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            assertEquals(2, session.createQuery("from Person p where p.id = 0", .class).list().size());
            assertEquals(3L, session.createQuery("select count( p ) from Person p").uniqueResult());
        });
    }

    @Entity(name = "Person")
    @IdClass(PropertyNamedIdInIdClassTest.PersonId.class)
    public static class Person implements Serializable {
        @Id
        private String name;

        @Id
        private Integer id;

        public Person(String name, Integer id) {
            this();
            setName(name);
            setId(id);
        }

        public String getName() {
            return name;
        }

        public Integer getId() {
            return id;
        }

        protected Person() {
            // this form used by Hibernate
        }

        protected void setName(String name) {
            this.name = name;
        }

        protected void setId(Integer id) {
            this.id = id;
        }
    }

    public static class PersonId implements Serializable {
        private String name;

        private Integer id;

        public PersonId() {
        }

        public PersonId(String name, int id) {
            setName(name);
            setId(id);
        }

        public String getName() {
            return name;
        }

        public Integer getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            PropertyNamedIdInIdClassTest.PersonId personId = ((PropertyNamedIdInIdClassTest.PersonId) (o));
            if ((id) != (personId.id)) {
                return false;
            }
            return name.equals(personId.name);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = (31 * result) + (id);
            return result;
        }
    }
}

