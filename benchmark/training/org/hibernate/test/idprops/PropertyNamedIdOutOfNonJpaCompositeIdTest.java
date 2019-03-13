/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idprops;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class PropertyNamedIdOutOfNonJpaCompositeIdTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testHql() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            assertEquals(1, session.createQuery("from Person p where p.id = 1", .class).list().size());
            assertEquals(3L, session.createQuery("select count( p ) from Person p").uniqueResult());
        });
    }

    @Entity(name = "Person")
    public static class Person implements Serializable {
        @Id
        private String name;

        @Id
        @Column(name = "ind")
        private int index;

        private Integer id;

        public Person(String name, int index) {
            this();
            setName(name);
            setIndex(index);
        }

        public Person(String name, int index, int id) {
            this(name, index);
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
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

        protected void setIndex(int index) {
            this.index = index;
        }

        protected void setId(Integer id) {
            this.id = id;
        }
    }

    public static class PersonId implements Serializable {
        private String name;

        private int index;

        public PersonId() {
        }

        public PersonId(String name, int index) {
            setName(name);
            setIndex(index);
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            PropertyNamedIdOutOfNonJpaCompositeIdTest.PersonId personId = ((PropertyNamedIdOutOfNonJpaCompositeIdTest.PersonId) (o));
            if ((index) != (personId.index)) {
                return false;
            }
            return name.equals(personId.name);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = (31 * result) + (index);
            return result;
        }
    }
}

