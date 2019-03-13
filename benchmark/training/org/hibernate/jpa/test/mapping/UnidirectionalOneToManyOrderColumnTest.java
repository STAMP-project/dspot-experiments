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
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-11587")
public class UnidirectionalOneToManyOrderColumnTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testRemovingAnElement() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.ParentData parent = new org.hibernate.jpa.test.mapping.ParentData();
            entityManager.persist(parent);
            String[] childrenStr = new String[]{ "One", "Two", "Three" };
            for (String str : childrenStr) {
                org.hibernate.jpa.test.mapping.ChildData child = new org.hibernate.jpa.test.mapping.ChildData(str);
                entityManager.persist(child);
                parent.getChildren().add(child);
            }
            entityManager.flush();
            List<org.hibernate.jpa.test.mapping.ChildData> children = parent.getChildren();
            children.remove(0);
        });
    }

    @Test
    public void testAddingAnElement() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.ParentData parent = new org.hibernate.jpa.test.mapping.ParentData();
            entityManager.persist(parent);
            String[] childrenStr = new String[]{ "One", "Two", "Three" };
            for (String str : childrenStr) {
                org.hibernate.jpa.test.mapping.ChildData child = new org.hibernate.jpa.test.mapping.ChildData(str);
                entityManager.persist(child);
                parent.getChildren().add(child);
            }
            entityManager.flush();
            List<org.hibernate.jpa.test.mapping.ChildData> children = parent.getChildren();
            children.add(1, new org.hibernate.jpa.test.mapping.ChildData("Another"));
        });
    }

    @Test
    public void testRemovingAndAddingAnElement() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.ParentData parent = new org.hibernate.jpa.test.mapping.ParentData();
            entityManager.persist(parent);
            String[] childrenStr = new String[]{ "One", "Two", "Three" };
            for (String str : childrenStr) {
                org.hibernate.jpa.test.mapping.ChildData child = new org.hibernate.jpa.test.mapping.ChildData(str);
                entityManager.persist(child);
                parent.getChildren().add(child);
            }
            entityManager.flush();
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
            assertEquals("Three", childIds.get((i++)));
        });
    }

    @Test
    public void testRemovingOneAndAddingTwoElements() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.ParentData parent = new org.hibernate.jpa.test.mapping.ParentData();
            entityManager.persist(parent);
            String[] childrenStr = new String[]{ "One", "Two", "Three" };
            for (String str : childrenStr) {
                org.hibernate.jpa.test.mapping.ChildData child = new org.hibernate.jpa.test.mapping.ChildData(str);
                entityManager.persist(child);
                parent.getChildren().add(child);
            }
            entityManager.flush();
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
            assertEquals("Another Another", childIds.get((i++)));
        });
    }

    @Entity(name = "ParentData")
    @Table(name = "PARENT")
    public static class ParentData {
        @Id
        @GeneratedValue
        long id;

        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
        @OrderColumn(name = "listOrder")
        private java.util.List<UnidirectionalOneToManyOrderColumnTest.ChildData> children = new ArrayList<>();

        public java.util.List<UnidirectionalOneToManyOrderColumnTest.ChildData> getChildren() {
            return children;
        }
    }

    @Entity(name = "ChildData")
    @Table(name = "CHILD")
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

