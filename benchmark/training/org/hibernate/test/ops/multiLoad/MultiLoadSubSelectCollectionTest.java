/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ops.multiLoad;


import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 * @author Gail Badner
 */
public class MultiLoadSubSelectCollectionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12740")
    public void testSubselect() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.test.ops.multiLoad.Parent> list = session.byMultipleIds(.class).multiLoad(ids(56));
            assertEquals(56, list.size());
            // None of the collections should be loaded yet
            for (org.hibernate.test.ops.multiLoad.Parent p : list) {
                assertFalse(Hibernate.isInitialized(list.get(0).children));
            }
            // When the first collection is loaded, the full batch of 50 collections
            // should be loaded.
            Hibernate.initialize(list.get(0).children);
            for (int i = 0; i < 50; i++) {
                assertTrue(Hibernate.isInitialized(list.get(i).children));
                assertEquals((i + 1), list.get(i).children.size());
            }
            // The collections for the 51st through 56th entities should still be uninitialized
            for (int i = 50; i < 56; i++) {
                assertFalse(Hibernate.isInitialized(list.get(i).children));
            }
            // When the 51st collection gets initialized, the remaining collections should
            // also be initialized.
            Hibernate.initialize(list.get(50).children);
            for (int i = 50; i < 56; i++) {
                assertTrue(Hibernate.isInitialized(list.get(i).children));
                assertEquals((i + 1), list.get(i).children.size());
            }
        });
    }

    @Entity(name = "Parent")
    @Table(name = "Parent")
    @BatchSize(size = 15)
    public static class Parent {
        Integer id;

        String text;

        private java.util.List<MultiLoadSubSelectCollectionTest.Child> children = new ArrayList<>();

        public Parent() {
        }

        public Parent(Integer id, String text) {
            this.id = id;
            this.text = text;
        }

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        @Fetch(FetchMode.SUBSELECT)
        public java.util.List<MultiLoadSubSelectCollectionTest.Child> getChildren() {
            return children;
        }

        public void setChildren(java.util.List<MultiLoadSubSelectCollectionTest.Child> children) {
            this.children = children;
        }
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private int id;

        @ManyToOne(fetch = FetchType.LAZY, optional = true)
        private MultiLoadSubSelectCollectionTest.Parent parent;

        public Child() {
        }

        public MultiLoadSubSelectCollectionTest.Parent getParent() {
            return parent;
        }

        public void setParent(MultiLoadSubSelectCollectionTest.Parent parent) {
            this.parent = parent;
        }

        public int getId() {
            return id;
        }
    }
}

