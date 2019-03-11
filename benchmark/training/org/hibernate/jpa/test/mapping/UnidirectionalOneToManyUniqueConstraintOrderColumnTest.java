/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.mapping;


import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-1268")
public class UnidirectionalOneToManyUniqueConstraintOrderColumnTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @FailureExpected(jiraKey = "HHH-1268")
    public void testRemovingAnElement() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.ParentData parent = entityManager.find(.class, 1L);
            List<org.hibernate.jpa.test.mapping.ChildData> children = parent.getChildren();
            children.remove(0);
        });
    }

    @Test
    @FailureExpected(jiraKey = "HHH-1268")
    public void testAddingAnElement() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.ParentData parent = entityManager.find(.class, 1L);
            List<org.hibernate.jpa.test.mapping.ChildData> children = parent.getChildren();
            children.add(1, new org.hibernate.jpa.test.mapping.ChildData("Another"));
        });
    }

    @Test
    @FailureExpected(jiraKey = "HHH-1268")
    public void testRemovingAndAddingAnElement() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.ParentData parent = entityManager.find(.class, 1L);
            List<org.hibernate.jpa.test.mapping.ChildData> children = parent.getChildren();
            children.remove(0);
            children.add(1, new org.hibernate.jpa.test.mapping.ChildData("Another"));
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.ParentData parent = entityManager.find(.class, 1L);
            List<String> childIds = parent.getChildren().stream().map(org.hibernate.jpa.test.mapping.ChildData::toString).collect(Collectors.toList());
            int i = 0;
            assertEquals("Two", childIds.get((i++)));
            assertEquals("Another", childIds.get((i++)));
            assertEquals("Three", childIds.get(i));
        });
    }

    @Test
    @FailureExpected(jiraKey = "HHH-1268")
    public void testRemovingOneAndAddingTwoElements() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.ParentData parent = entityManager.find(.class, 1L);
            List<org.hibernate.jpa.test.mapping.ChildData> children = parent.getChildren();
            children.remove(0);
            children.add(1, new org.hibernate.jpa.test.mapping.ChildData("Another"));
            children.add(new org.hibernate.jpa.test.mapping.ChildData("Another Another"));
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.ParentData parent = entityManager.find(.class, 1L);
            List<String> childIds = parent.getChildren().stream().map(org.hibernate.jpa.test.mapping.ChildData::toString).collect(Collectors.toList());
            int i = 0;
            assertEquals("Two", childIds.get((i++)));
            assertEquals("Another", childIds.get((i++)));
            assertEquals("Three", childIds.get((i++)));
            assertEquals("Another Another", childIds.get(i));
        });
    }

    @Entity(name = "ParentData")
    public static class ParentData {
        @Id
        long id;

        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "parentId", nullable = false)
        @OrderColumn(name = "listOrder")
        private java.util.List<UnidirectionalOneToManyUniqueConstraintOrderColumnTest.ChildData> children = new ArrayList<>();

        public java.util.List<UnidirectionalOneToManyUniqueConstraintOrderColumnTest.ChildData> getChildren() {
            return children;
        }
    }

    @Entity(name = "ChildData")
    @Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "parentId", "listOrder" }) })
    public static class ChildData {
        @Id
        @GeneratedValue
        long id;

        String childId;

        public ChildData() {
        }

        public ChildData(String id) {
            childId = id;
        }

        @Override
        public String toString() {
            return childId;
        }
    }
}

