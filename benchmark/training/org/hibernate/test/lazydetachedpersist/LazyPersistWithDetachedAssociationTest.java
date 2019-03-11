/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lazydetachedpersist;


import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


public class LazyPersistWithDetachedAssociationTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-3846")
    public void testDetachedAssociationOnPersisting() {
        sessionFactory().getStatistics().clear();
        LazyPersistWithDetachedAssociationTest.Address loadedAddress = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // first load the address
            org.hibernate.test.lazydetachedpersist.Address _loadedAddress = session.load(.class, 1L);
            assertNotNull(_loadedAddress);
            return _loadedAddress;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.get(.class, 1L);
            org.hibernate.test.lazydetachedpersist.Person person = new org.hibernate.test.lazydetachedpersist.Person();
            person.setId(1L);
            person.setName("Johnny Depp");
            person.setAddress(loadedAddress);
            session.persist(person);
        });
    }

    @Entity
    @Table(name = "eg_sbt_address")
    public static class Address {
        private Long id;

        private String content;

        @Id
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @Basic
        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @Entity
    @Table(name = "eg_sbt_person")
    public static class Person {
        private Long id;

        private LazyPersistWithDetachedAssociationTest.Address address;

        private String name;

        @Id
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @ManyToOne(fetch = FetchType.LAZY, cascade = {  })
        public LazyPersistWithDetachedAssociationTest.Address getAddress() {
            return address;
        }

        public void setAddress(LazyPersistWithDetachedAssociationTest.Address address) {
            this.address = address;
        }

        @Basic
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

