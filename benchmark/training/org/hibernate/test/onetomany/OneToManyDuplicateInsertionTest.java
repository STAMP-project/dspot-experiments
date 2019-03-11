/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.onetomany;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


public class OneToManyDuplicateInsertionTest extends BaseEntityManagerFunctionalTestCase {
    private int parentId;

    @Test
    @TestForIssue(jiraKey = "HHH-6776")
    public void testDuplicateInsertion() {
        // persist parent entity in a transaction
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            org.hibernate.test.onetomany.Parent parent = new org.hibernate.test.onetomany.Parent();
            em.persist(parent);
            parentId = parent.getId();
        });
        // relate and persist child entity in another transaction
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            org.hibernate.test.onetomany.Parent parent = em.find(.class, parentId);
            org.hibernate.test.onetomany.Child child = new org.hibernate.test.onetomany.Child();
            child.setParent(parent);
            parent.getChildren().add(child);
            em.persist(child);
            assertEquals(1, parent.getChildren().size());
        });
        // get the parent again
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            org.hibernate.test.onetomany.Parent parent = em.find(.class, parentId);
            assertEquals(1, parent.getChildren().size());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7404")
    public void testDuplicateInsertionWithCascadeAndMerge() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            org.hibernate.test.onetomany.ParentCascade p = new org.hibernate.test.onetomany.ParentCascade();
            // merge with 0 children
            p = em.merge(p);
            parentId = p.getId();
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            org.hibernate.test.onetomany.ParentCascade p = em.find(.class, parentId);
            final org.hibernate.test.onetomany.ChildCascade child = new org.hibernate.test.onetomany.ChildCascade();
            child.setParent(p);
            p.getChildren().add(child);
            em.merge(p);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            // again, load the Parent by id
            org.hibernate.test.onetomany.ParentCascade p = em.find(.class, parentId);
            // check that we have only 1 element in the list
            assertEquals(1, p.getChildren().size());
        });
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        @GeneratedValue
        private int id;

        @OneToMany(mappedBy = "parent")
        private List<OneToManyDuplicateInsertionTest.Child> children = new LinkedList<OneToManyDuplicateInsertionTest.Child>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<OneToManyDuplicateInsertionTest.Child> getChildren() {
            return children;
        }

        public void setChildren(List<OneToManyDuplicateInsertionTest.Child> children) {
            this.children = children;
        }
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        @GeneratedValue
        private int id;

        @ManyToOne
        private OneToManyDuplicateInsertionTest.Parent parent;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public OneToManyDuplicateInsertionTest.Parent getParent() {
            return parent;
        }

        public void setParent(OneToManyDuplicateInsertionTest.Parent parent) {
            this.parent = parent;
        }
    }

    @Entity(name = "ParentCascade")
    public static class ParentCascade {
        @Id
        @GeneratedValue
        private Integer id;

        @OneToMany(mappedBy = "parent", cascade = { CascadeType.ALL })
        private List<OneToManyDuplicateInsertionTest.ChildCascade> children = new ArrayList<OneToManyDuplicateInsertionTest.ChildCascade>();

        public Integer getId() {
            return id;
        }

        public List<OneToManyDuplicateInsertionTest.ChildCascade> getChildren() {
            return children;
        }

        public void setChildren(List<OneToManyDuplicateInsertionTest.ChildCascade> children) {
            this.children = children;
        }
    }

    @Entity(name = "ChildCascade")
    public static class ChildCascade {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToOne
        private OneToManyDuplicateInsertionTest.ParentCascade parent;

        public Integer getId() {
            return id;
        }

        public OneToManyDuplicateInsertionTest.ParentCascade getParent() {
            return parent;
        }

        public void setParent(OneToManyDuplicateInsertionTest.ParentCascade parent) {
            this.parent = parent;
        }
    }
}

