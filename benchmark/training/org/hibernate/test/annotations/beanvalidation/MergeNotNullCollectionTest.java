/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.beanvalidation;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Ryan Emerson
 */
@TestForIssue(jiraKey = "HHH-9979")
public class MergeNotNullCollectionTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToManyNotNullCollection() {
        MergeNotNullCollectionTest.Parent parent = new MergeNotNullCollectionTest.Parent();
        parent.id = 1L;
        MergeNotNullCollectionTest.Child child = new MergeNotNullCollectionTest.Child();
        child.id = 1L;
        List<MergeNotNullCollectionTest.Child> children = new ArrayList<MergeNotNullCollectionTest.Child>();
        children.add(child);
        child.setParent(parent);
        parent.setChildren(children);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        parent = ((MergeNotNullCollectionTest.Parent) (s.merge(parent)));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.delete(parent);
        t.commit();
        s.close();
    }

    @Test(expected = ConstraintViolationException.class)
    public void testOneToManyNullCollection() {
        MergeNotNullCollectionTest.Parent parent = new MergeNotNullCollectionTest.Parent();
        parent.id = 1L;
        Session s = openSession();
        Transaction t = s.beginTransaction();
        parent = ((MergeNotNullCollectionTest.Parent) (s.merge(parent)));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.delete(parent);
        t.commit();
        s.close();
    }

    @Entity
    @Table(name = "PARENT")
    static class Parent {
        @Id
        private Long id;

        @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
        @NotNull
        private List<MergeNotNullCollectionTest.Child> children;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<MergeNotNullCollectionTest.Child> getChildren() {
            return children;
        }

        public void setChildren(List<MergeNotNullCollectionTest.Child> children) {
            this.children = children;
        }
    }

    @Entity
    @Table(name = "CHILD")
    static class Child {
        @Id
        private Long id;

        @ManyToOne
        private MergeNotNullCollectionTest.Parent parent;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public MergeNotNullCollectionTest.Parent getParent() {
            return parent;
        }

        public void setParent(MergeNotNullCollectionTest.Parent parent) {
            this.parent = parent;
        }
    }
}

