/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.manytomany;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-7940")
public class IndexColumnListTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    @Priority(10)
    public void initData() {
        // Revision 1 - Create indexed entries.
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.envers.test.integration.manytomany.Parent p = new org.hibernate.envers.test.integration.manytomany.Parent(1);
            p.addChild(new org.hibernate.envers.test.integration.manytomany.Child(1, "child1"));
            p.addChild(new org.hibernate.envers.test.integration.manytomany.Child(2, "child2"));
            entityManager.persist(p);
            p.getChildren().forEach(entityManager::persist);
        });
        // Revision 2 - remove an indexed entry, resetting positions.
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.manytomany.Parent p = entityManager.find(.class, 1);
            // should remove child with id 1
            p.removeChild(p.getChildren().get(0));
            entityManager.merge(p);
        });
        // Revision 3 - add new indexed entity to reset positions
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.manytomany.Parent p = entityManager.find(.class, 1);
            // add child with id 3
            final org.hibernate.envers.test.integration.manytomany.Child child = new org.hibernate.envers.test.integration.manytomany.Child(3, "child3");
            p.getChildren().add(0, child);
            child.getParents().add(p);
            entityManager.persist(child);
            entityManager.merge(p);
        });
        // Revision 4 - remove all children
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.manytomany.Parent p = entityManager.find(.class, 1);
            while (!(p.getChildren().isEmpty())) {
                org.hibernate.envers.test.integration.manytomany.Child child = p.getChildren().get(0);
                p.removeChild(child);
                entityManager.remove(child);
            } 
            entityManager.merge(p);
        });
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), getAuditReader().getRevisions(IndexColumnListTest.Parent.class, 1));
        Assert.assertEquals(Arrays.asList(1, 2), getAuditReader().getRevisions(IndexColumnListTest.Child.class, 1));
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), getAuditReader().getRevisions(IndexColumnListTest.Child.class, 2));
        Assert.assertEquals(Arrays.asList(3, 4), getAuditReader().getRevisions(IndexColumnListTest.Child.class, 3));
    }

    @Test
    public void testIndexedCollectionRev1() {
        final IndexColumnListTest.Parent p = getAuditReader().find(IndexColumnListTest.Parent.class, 1, 1);
        Assert.assertEquals(2, p.getChildren().size());
        Assert.assertEquals(new IndexColumnListTest.Child(1, "child1", p), p.getChildren().get(0));
        Assert.assertEquals(new IndexColumnListTest.Child(2, "child2", p), p.getChildren().get(1));
    }

    @Test
    public void testIndexedCollectionRev2() {
        final IndexColumnListTest.Parent p = getAuditReader().find(IndexColumnListTest.Parent.class, 1, 2);
        Assert.assertEquals(1, p.getChildren().size());
        Assert.assertEquals(new IndexColumnListTest.Child(2, "child2", p), p.getChildren().get(0));
    }

    @Test
    public void testIndexedCollectionRev3() {
        final IndexColumnListTest.Parent p = getAuditReader().find(IndexColumnListTest.Parent.class, 1, 3);
        Assert.assertEquals(2, p.getChildren().size());
        Assert.assertEquals(new IndexColumnListTest.Child(3, "child3", p), p.getChildren().get(0));
        Assert.assertEquals(new IndexColumnListTest.Child(2, "child2", p), p.getChildren().get(1));
    }

    @Test
    public void testIndexedCollectionRev4() {
        final IndexColumnListTest.Parent p = getAuditReader().find(IndexColumnListTest.Parent.class, 1, 4);
        Assert.assertEquals(0, p.getChildren().size());
    }

    @Audited
    @Entity(name = "Parent")
    public static class Parent {
        @Id
        private Integer id;

        @ManyToMany
        @OrderColumn(name = "`index`")
        private List<IndexColumnListTest.Child> children = new ArrayList<IndexColumnListTest.Child>();

        Parent() {
        }

        Parent(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public List<IndexColumnListTest.Child> getChildren() {
            return children;
        }

        public void setChildren(List<IndexColumnListTest.Child> children) {
            this.children = children;
        }

        public void addChild(IndexColumnListTest.Child child) {
            child.getParents().add(this);
            getChildren().add(child);
        }

        public void removeChild(IndexColumnListTest.Child child) {
            child.getParents().remove(this);
            getChildren().remove(child);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            IndexColumnListTest.Parent parent = ((IndexColumnListTest.Parent) (o));
            return (id) != null ? id.equals(parent.id) : (parent.id) == null;
        }

        @Override
        public int hashCode() {
            return (id) != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
            return (("Parent{" + "id=") + (id)) + '}';
        }
    }

    @Audited
    @Entity(name = "Child")
    public static class Child {
        @Id
        private Integer id;

        private String name;

        @ManyToMany(mappedBy = "children")
        private List<IndexColumnListTest.Parent> parents;

        Child() {
        }

        Child(Integer id, String name) {
            this(id, name, null);
        }

        Child(Integer id, String name, IndexColumnListTest.Parent... parents) {
            this.id = id;
            this.name = name;
            if ((parents != null) && ((parents.length) > 0)) {
                this.parents = new ArrayList<>(Arrays.asList(parents));
            } else {
                this.parents = new ArrayList<>();
            }
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<IndexColumnListTest.Parent> getParents() {
            return parents;
        }

        public void setParent(List<IndexColumnListTest.Parent> parent) {
            this.parents = parents;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            IndexColumnListTest.Child child = ((IndexColumnListTest.Child) (o));
            if ((id) != null ? !(id.equals(child.id)) : (child.id) != null) {
                return false;
            }
            if ((name) != null ? !(name.equals(child.name)) : (child.name) != null) {
                return false;
            }
            return (parents) != null ? parents.equals(child.parents) : (child.parents) == null;
        }

        @Override
        public int hashCode() {
            int result = ((id) != null) ? id.hashCode() : 0;
            result = (31 * result) + ((name) != null ? name.hashCode() : 0);
            result = (31 * result) + ((parents) != null ? parents.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return ((((((("Child{" + "id=") + (id)) + ", name='") + (name)) + '\'') + ", parents=") + (parents)) + '}';
        }
    }
}

