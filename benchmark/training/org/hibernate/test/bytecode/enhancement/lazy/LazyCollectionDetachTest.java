/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy;


import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


@TestForIssue(jiraKey = "HHH-12260")
@RunWith(BytecodeEnhancerRunner.class)
public class LazyCollectionDetachTest extends BaseCoreFunctionalTestCase {
    private static final int CHILDREN_SIZE = 10;

    private Long parentID;

    @Test
    public void testDetach() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.Parent parent = s.find(.class, parentID);
            assertThat(parent, notNullValue());
            assertThat(parent, not(instanceOf(.class)));
            assertFalse(isPropertyInitialized(parent, "children"));
            checkDirtyTracking(parent);
            s.detach(parent);
            s.flush();
        });
    }

    @Test
    public void testDetachProxy() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.Parent parent = s.getReference(.class, parentID);
            checkDirtyTracking(parent);
            s.detach(parent);
            s.flush();
        });
    }

    @Test
    public void testRefresh() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.Parent parent = s.find(.class, parentID);
            assertThat(parent, notNullValue());
            assertThat(parent, not(instanceOf(.class)));
            assertFalse(isPropertyInitialized(parent, "children"));
            checkDirtyTracking(parent);
            s.refresh(parent);
            s.flush();
        });
    }

    @Entity
    @Table(name = "PARENT")
    private static class Parent {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long id;

        @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
        List<LazyCollectionDetachTest.Child> children;

        void setChildren(List<LazyCollectionDetachTest.Child> children) {
            this.children = children;
        }
    }

    @Entity
    @Table(name = "CHILD")
    private static class Child {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long id;

        @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        LazyCollectionDetachTest.Parent parent;

        Child() {
        }
    }
}

