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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


@TestForIssue(jiraKey = "HHH-10708")
@RunWith(BytecodeEnhancerRunner.class)
public class UnexpectedDeleteTest1 extends BaseCoreFunctionalTestCase {
    private long fooId;

    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.HHH_10708.Foo foo = s.get(.class, fooId);
            // accessing the collection results in an exception
            foo.bars.size();
        });
    }

    // --- //
    @Entity
    @Table(name = "BAR")
    private static class Bar {
        @Id
        @GeneratedValue
        Long id;

        @ManyToOne
        @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
        UnexpectedDeleteTest1.Foo foo;
    }

    @Entity
    @Table(name = "FOO")
    private static class Foo {
        @Id
        @GeneratedValue
        Long id;

        @OneToMany(orphanRemoval = true, mappedBy = "foo", targetEntity = UnexpectedDeleteTest1.Bar.class)
        @Cascade(CascadeType.ALL)
        Set<UnexpectedDeleteTest1.Bar> bars = new HashSet<>();
    }
}

