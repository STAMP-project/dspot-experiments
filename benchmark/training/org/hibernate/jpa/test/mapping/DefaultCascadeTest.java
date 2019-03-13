/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.mapping;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class DefaultCascadeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testCascadePersist() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.Parent parent = new org.hibernate.jpa.test.mapping.Parent();
            org.hibernate.jpa.test.mapping.Child child = new org.hibernate.jpa.test.mapping.Child();
            child.parent = parent;
            entityManager.persist(child);
        });
    }

    @Entity
    @Table(name = "Parent")
    public static class Parent {
        @Id
        @GeneratedValue
        private Integer id;
    }

    @Entity
    @Table(name = "Child")
    public static class Child {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToOne
        private DefaultCascadeTest.Parent parent;
    }
}

