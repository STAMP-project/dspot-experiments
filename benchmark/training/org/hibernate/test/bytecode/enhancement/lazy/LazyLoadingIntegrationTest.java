/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Luis Barreiro
 */
@RunWith(BytecodeEnhancerRunner.class)
public class LazyLoadingIntegrationTest extends BaseCoreFunctionalTestCase {
    private static final int CHILDREN_SIZE = 10;

    private Long lastChildID;

    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.Child loadedChild = s.load(.class, lastChildID);
            checkDirtyTracking(loadedChild);
            loadedChild.name = "Barrabas";
            checkDirtyTracking(loadedChild, "name");
            org.hibernate.test.bytecode.enhancement.lazy.Parent loadedParent = loadedChild.parent;
            checkDirtyTracking(loadedChild, "name");
            checkDirtyTracking(loadedParent);
            List<org.hibernate.test.bytecode.enhancement.lazy.Child> loadedChildren = new ArrayList<>(loadedParent.children);
            loadedChildren.remove(0);
            loadedChildren.remove(loadedChild);
            loadedParent.setChildren(loadedChildren);
            Assert.assertNull(loadedChild.parent);
        });
    }

    // --- //
    @Entity
    @Table(name = "PARENT")
    private static class Parent {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long id;

        @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        java.util.List<LazyLoadingIntegrationTest.Child> children;

        void setChildren(java.util.List<LazyLoadingIntegrationTest.Child> children) {
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
        @LazyToOne(LazyToOneOption.NO_PROXY)
        LazyLoadingIntegrationTest.Parent parent;

        String name;

        Child() {
        }

        Child(String name) {
            this.name = name;
        }
    }
}

