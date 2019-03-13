/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.mapping;


import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-1268")
public class UnidirectionalOneToManyIndexColumnTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testRemovingAChild() {
        int parentId = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.jpa.test.mapping.Parent parent = new org.hibernate.jpa.test.mapping.Parent();
            parent.getChildren().add(new org.hibernate.jpa.test.mapping.Child());
            parent.getChildren().add(new org.hibernate.jpa.test.mapping.Child());
            parent.getChildren().add(new org.hibernate.jpa.test.mapping.Child());
            session.persist(parent);
            return parent.getId();
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.jpa.test.mapping.Parent parent = session.find(.class, parentId);
            List<org.hibernate.jpa.test.mapping.Child> children = parent.getChildren();
            assertThat(children.size(), is(3));
            children.remove(0);
            session.persist(parent);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.jpa.test.mapping.Parent parent = session.find(.class, parentId);
            List<org.hibernate.jpa.test.mapping.Child> children = parent.getChildren();
            assertThat(children.size(), is(2));
        });
    }

    @Entity
    @Table(name = "PARENT")
    public static class Parent {
        @Id
        @GeneratedValue
        private int id;

        @OneToMany(targetEntity = UnidirectionalOneToManyIndexColumnTest.Child.class, cascade = CascadeType.ALL)
        @IndexColumn(name = "position")
        private java.util.List<UnidirectionalOneToManyIndexColumnTest.Child> children = new ArrayList<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public java.util.List<UnidirectionalOneToManyIndexColumnTest.Child> getChildren() {
            return children;
        }

        public void setChildren(java.util.List<UnidirectionalOneToManyIndexColumnTest.Child> children) {
            this.children = children;
        }
    }

    @Entity
    @Table(name = "CHILD")
    public static class Child {
        @Id
        @GeneratedValue
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

