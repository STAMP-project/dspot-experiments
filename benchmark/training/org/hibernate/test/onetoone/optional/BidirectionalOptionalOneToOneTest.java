/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.onetoone.optional;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class BidirectionalOptionalOneToOneTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.onetoone.optional.Parent a = new org.hibernate.test.onetoone.optional.Parent();
            a.id = 1L;
            org.hibernate.test.onetoone.optional.Child b = new org.hibernate.test.onetoone.optional.Child();
            b.id = 1L;
            a.setChild(b);
            b.setParent(a);
            entityManager.persist(a);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.onetoone.optional.Parent a = entityManager.find(.class, 1L);
            entityManager.remove(a);
        });
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        @Column(unique = true, nullable = false)
        private Long id;

        @OneToOne(optional = false, mappedBy = "parent", cascade = CascadeType.ALL)
        private BidirectionalOptionalOneToOneTest.Child child;

        public Long getId() {
            return id;
        }

        public BidirectionalOptionalOneToOneTest.Child getChild() {
            return child;
        }

        public void setChild(BidirectionalOptionalOneToOneTest.Child child) {
            this.child = child;
        }
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        @Column(unique = true, nullable = false)
        private Long id;

        @OneToOne(optional = false)
        @JoinColumn(nullable = false)
        private BidirectionalOptionalOneToOneTest.Parent parent;

        public Long getId() {
            return id;
        }

        public BidirectionalOptionalOneToOneTest.Parent getParent() {
            return parent;
        }

        public void setParent(BidirectionalOptionalOneToOneTest.Parent parent) {
            this.parent = parent;
        }
    }
}

