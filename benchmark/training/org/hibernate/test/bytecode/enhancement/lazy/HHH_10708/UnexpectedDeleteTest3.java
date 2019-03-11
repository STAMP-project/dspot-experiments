/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy.HHH_10708;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@TestForIssue(jiraKey = "HHH-10708")
@RunWith(BytecodeEnhancerRunner.class)
public class UnexpectedDeleteTest3 extends BaseCoreFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.HHH_10708.Parent parent = s.get(.class, 1L);
            org.hibernate.test.bytecode.enhancement.lazy.HHH_10708.Child child = new org.hibernate.test.bytecode.enhancement.lazy.HHH_10708.Child();
            child.setId(1L);
            s.save(child);
            parent.addChild(child);
            // We need to leave at least one attribute unfetchd
            // parent.getNames().size();
            s.save(parent);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.HHH_10708.Parent application = s.get(.class, 1L);
            Assert.assertEquals("Loaded Children collection has unexpected size", 2, application.getChildren().size());
        });
    }

    // --- //
    @Entity
    @Table(name = "CHILD")
    private static class Child {
        Long id;

        @Id
        @Column(name = "id", unique = true, nullable = false)
        Long getId() {
            return id;
        }

        void setId(Long id) {
            this.id = id;
        }
    }

    @Entity
    @Table(name = "PARENT")
    private static class Parent {
        Long id;

        Set<String> names;

        Set<UnexpectedDeleteTest3.Child> children;

        @Id
        @Column(name = "id", unique = true, nullable = false)
        Long getId() {
            return id;
        }

        void setId(Long id) {
            this.id = id;
        }

        @ElementCollection
        Set<String> getNames() {
            return Collections.unmodifiableSet(names);
        }

        void setNames(Set<String> secrets) {
            this.names = secrets;
        }

        @ManyToMany(fetch = FetchType.LAZY, targetEntity = UnexpectedDeleteTest3.Child.class)
        Set<UnexpectedDeleteTest3.Child> getChildren() {
            return Collections.unmodifiableSet(children);
        }

        void addChild(UnexpectedDeleteTest3.Child child) {
            if ((children) == null) {
                children = new HashSet<>();
            }
            children.add(child);
        }
    }
}

