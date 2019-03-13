/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.query;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the use of the {@link OrderBy} annotation on a one-to-many collection where
 * both sides of the association are audited.
 *
 * This mapping invokes the use of the OneAuditEntityQueryGenerator which we want to
 * verify orders the collection results properly.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12992")
public class OrderByOneAuditEntityTest extends BaseEnversJPAFunctionalTestCase {
    @Entity(name = "Parent")
    @Audited
    public static class Parent {
        @Id
        @GeneratedValue
        private Integer id;

        @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
        @OrderBy("index1, index2 desc")
        @AuditMappedBy(mappedBy = "parent", positionMappedBy = "index1")
        @Fetch(FetchMode.SELECT)
        @BatchSize(size = 100)
        private List<OrderByOneAuditEntityTest.Child> children = new ArrayList<>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public List<OrderByOneAuditEntityTest.Child> getChildren() {
            return children;
        }

        public void setChildren(List<OrderByOneAuditEntityTest.Child> children) {
            this.children = children;
        }
    }

    @Entity(name = "Child")
    @Audited
    public static class Child {
        @Id
        private Integer id;

        private Integer index1;

        private Integer index2;

        @ManyToOne
        private OrderByOneAuditEntityTest.Parent parent;

        public Child() {
        }

        public Child(Integer id, Integer index1) {
            this.id = id;
            this.index1 = index1;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getIndex1() {
            return index1;
        }

        public void setIndex1(Integer index1) {
            this.index1 = index1;
        }

        public Integer getIndex2() {
            return index2;
        }

        public void setIndex2(Integer index2) {
            this.index2 = index2;
        }

        public OrderByOneAuditEntityTest.Parent getParent() {
            return parent;
        }

        public void setParent(OrderByOneAuditEntityTest.Parent parent) {
            this.parent = parent;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            OrderByOneAuditEntityTest.Child child = ((OrderByOneAuditEntityTest.Child) (o));
            return (Objects.equals(id, child.id)) && (Objects.equals(index1, child.index1));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, index1);
        }

        @Override
        public String toString() {
            return (((("Child{" + "id=") + (id)) + ", index1=") + (index1)) + '}';
        }
    }

    private Integer parentId;

    @Test
    public void initData() {
        // Rev 1
        this.parentId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Parent parent = new org.hibernate.envers.test.integration.query.Parent();
            final org.hibernate.envers.test.integration.query.Child child1 = new org.hibernate.envers.test.integration.query.Child();
            child1.setId(1);
            child1.setIndex1(1);
            child1.setIndex2(1);
            child1.setParent(parent);
            parent.getChildren().add(child1);
            final org.hibernate.envers.test.integration.query.Child child2 = new org.hibernate.envers.test.integration.query.Child();
            child2.setId(2);
            child2.setIndex1(2);
            child2.setIndex2(2);
            child2.setParent(parent);
            parent.getChildren().add(child2);
            entityManager.persist(parent);
            return parent.getId();
        });
        // Rev 2
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Parent parent = entityManager.find(.class, parentId);
            final org.hibernate.envers.test.integration.query.Child child = new org.hibernate.envers.test.integration.query.Child();
            child.setId(3);
            child.setIndex1(3);
            child.setIndex2(3);
            child.setParent(parent);
            parent.getChildren().add(child);
            entityManager.merge(parent);
        });
        // Rev 3
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Parent parent = entityManager.find(.class, parentId);
            parent.getChildren().removeIf(( c) -> (c.getIndex1()) == 2);
            entityManager.merge(parent);
        });
        // Rev 4
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Parent parent = entityManager.find(.class, parentId);
            parent.getChildren().clear();
            entityManager.merge(parent);
        });
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), getAuditReader().getRevisions(OrderByOneAuditEntityTest.Parent.class, this.parentId));
        Assert.assertEquals(Arrays.asList(1, 4), getAuditReader().getRevisions(OrderByOneAuditEntityTest.Child.class, 1));
        Assert.assertEquals(Arrays.asList(1, 3), getAuditReader().getRevisions(OrderByOneAuditEntityTest.Child.class, 2));
        Assert.assertEquals(Arrays.asList(2, 4), getAuditReader().getRevisions(OrderByOneAuditEntityTest.Child.class, 3));
    }

    @Test
    public void testRevision1History() {
        final OrderByOneAuditEntityTest.Parent parent = getAuditReader().find(OrderByOneAuditEntityTest.Parent.class, this.parentId, 1);
        Assert.assertNotNull(parent);
        Assert.assertTrue((!(parent.getChildren().isEmpty())));
        Assert.assertEquals(2, parent.getChildren().size());
        Assert.assertEquals(Arrays.asList(new OrderByOneAuditEntityTest.Child(1, 1), new OrderByOneAuditEntityTest.Child(2, 2)), parent.getChildren());
    }

    @Test
    public void testRevision2History() {
        final OrderByOneAuditEntityTest.Parent parent = getAuditReader().find(OrderByOneAuditEntityTest.Parent.class, this.parentId, 2);
        Assert.assertNotNull(parent);
        Assert.assertTrue((!(parent.getChildren().isEmpty())));
        Assert.assertEquals(3, parent.getChildren().size());
        Assert.assertEquals(Arrays.asList(new OrderByOneAuditEntityTest.Child(1, 1), new OrderByOneAuditEntityTest.Child(2, 2), new OrderByOneAuditEntityTest.Child(3, 3)), parent.getChildren());
    }

    @Test
    public void testRevision3History() {
        final OrderByOneAuditEntityTest.Parent parent = getAuditReader().find(OrderByOneAuditEntityTest.Parent.class, this.parentId, 3);
        Assert.assertNotNull(parent);
        Assert.assertTrue((!(parent.getChildren().isEmpty())));
        Assert.assertEquals(2, parent.getChildren().size());
        Assert.assertEquals(Arrays.asList(new OrderByOneAuditEntityTest.Child(1, 1), new OrderByOneAuditEntityTest.Child(3, 3)), parent.getChildren());
    }

    @Test
    public void testRevision4History() {
        final OrderByOneAuditEntityTest.Parent parent = getAuditReader().find(OrderByOneAuditEntityTest.Parent.class, this.parentId, 4);
        Assert.assertNotNull(parent);
        Assert.assertTrue(parent.getChildren().isEmpty());
    }
}

