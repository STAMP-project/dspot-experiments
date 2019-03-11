/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy.HHH_10708;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
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
public class UnexpectedDeleteTest2 extends BaseCoreFunctionalTestCase {
    private UnexpectedDeleteTest2.Bar myBar;

    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            s.refresh(myBar);
            Assert.assertFalse(myBar.foos.isEmpty());
            // The issue is that currently, for some unknown reason, foos are deleted on flush
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.HHH_10708.Bar bar = s.get(.class, myBar.id);
            Assert.assertFalse(bar.foos.isEmpty());
        });
    }

    // --- //
    @Entity
    @Table(name = "BAR")
    private static class Bar {
        @Id
        @GeneratedValue
        Long id;

        @ManyToMany(fetch = FetchType.LAZY, targetEntity = UnexpectedDeleteTest2.Foo.class)
        Set<UnexpectedDeleteTest2.Foo> foos = new HashSet<>();
    }

    @Entity
    @Table(name = "FOO")
    private static class Foo {
        @Id
        @GeneratedValue
        Long id;
    }
}

