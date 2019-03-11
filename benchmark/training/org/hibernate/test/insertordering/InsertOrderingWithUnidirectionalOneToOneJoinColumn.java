/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.insertordering;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12569")
public class InsertOrderingWithUnidirectionalOneToOneJoinColumn extends BaseEntityManagerFunctionalTestCase {
    @Embeddable
    public static class PersonAddressId implements Serializable {
        @Column(name = "id")
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Entity(name = "Person")
    public static class Person {
        @EmbeddedId
        private InsertOrderingWithUnidirectionalOneToOneJoinColumn.PersonAddressId id;

        public InsertOrderingWithUnidirectionalOneToOneJoinColumn.PersonAddressId getId() {
            return id;
        }

        public void setId(InsertOrderingWithUnidirectionalOneToOneJoinColumn.PersonAddressId id) {
            this.id = id;
        }
    }

    @Entity(name = "Address")
    public static class Address {
        @EmbeddedId
        private InsertOrderingWithUnidirectionalOneToOneJoinColumn.PersonAddressId id;

        @OneToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
        private InsertOrderingWithUnidirectionalOneToOneJoinColumn.Person person;

        public InsertOrderingWithUnidirectionalOneToOneJoinColumn.PersonAddressId getId() {
            return id;
        }

        public void setId(InsertOrderingWithUnidirectionalOneToOneJoinColumn.PersonAddressId id) {
            this.id = id;
        }

        public InsertOrderingWithUnidirectionalOneToOneJoinColumn.Person getPerson() {
            return person;
        }

        public void setPerson(InsertOrderingWithUnidirectionalOneToOneJoinColumn.Person person) {
            this.person = person;
        }
    }

    @Test
    public void testBatchingWithEmbeddableId() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.test.insertordering.PersonAddressId id = new org.hibernate.test.insertordering.PersonAddressId();
            id.setId(1);
            org.hibernate.test.insertordering.Person person = new org.hibernate.test.insertordering.Person();
            person.setId(id);
            entityManager.persist(person);
            org.hibernate.test.insertordering.Address address = new org.hibernate.test.insertordering.Address();
            address.setId(id);
            address.setPerson(person);
            entityManager.persist(address);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Query query = entityManager.createQuery("FROM Person");
            assertEquals(1, query.getResultList().size());
        });
    }
}

