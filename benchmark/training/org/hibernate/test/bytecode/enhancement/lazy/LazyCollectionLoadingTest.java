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
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Simple test for lazy collection handling in the new bytecode support.
 * Prior to HHH-10055 lazy collections were simply not handled.  The tests
 * initially added for HHH-10055 cover the more complicated case of handling
 * lazy collection initialization outside of a transaction; that is a bigger
 * fix, and I first want to get collection handling to work here in general.
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-10055")
@RunWith(BytecodeEnhancerRunner.class)
public class LazyCollectionLoadingTest extends BaseCoreFunctionalTestCase {
    private static final int CHILDREN_SIZE = 10;

    private Long parentID;

    private LazyCollectionLoadingTest.Parent parent;

    @Test
    public void testTransaction() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.Parent parent = s.load(.class, parentID);
            assertThat(parent, notNullValue());
            assertThat(parent, not(instanceOf(.class)));
            assertThat(parent, not(instanceOf(.class)));
            assertFalse(isPropertyInitialized(parent, "children"));
            checkDirtyTracking(parent);
            List children1 = parent.children;
            List children2 = parent.children;
            assertTrue(isPropertyInitialized(parent, "children"));
            checkDirtyTracking(parent);
            assertThat(children1, sameInstance(children2));
            assertThat(children1.size(), equalTo(CHILDREN_SIZE));
        });
    }

    @Test
    public void testNoTransaction() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            parent = s.load(.class, parentID);
            assertThat(parent, notNullValue());
            assertThat(parent, not(instanceOf(.class)));
            assertThat(parent, not(instanceOf(.class)));
            assertFalse(isPropertyInitialized(parent, "children"));
        });
        List children1 = parent.children;
        List children2 = parent.children;
        Assert.assertTrue(Hibernate.isPropertyInitialized(parent, "children"));
        checkDirtyTracking(parent);
        MatcherAssert.assertThat(children1, CoreMatchers.sameInstance(children2));
        MatcherAssert.assertThat(children1.size(), CoreMatchers.equalTo(LazyCollectionLoadingTest.CHILDREN_SIZE));
    }

    // --- //
    @Entity
    @Table(name = "PARENT")
    private static class Parent {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long id;

        @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        List<LazyCollectionLoadingTest.Child> children;

        void setChildren(List<LazyCollectionLoadingTest.Child> children) {
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
        LazyCollectionLoadingTest.Parent parent;

        String name;

        Child() {
        }

        Child(String name) {
            this.name = name;
        }
    }
}

