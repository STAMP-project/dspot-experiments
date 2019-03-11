/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.criteria;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Thomas Reinhardt
 */
public class CriteriaAliasFetchTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-7842")
    public void testFetchWithAlias() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            assertOnlyOneSelect(session.createCriteria(.class, "c").setFetchMode("c.kittens", FetchMode.JOIN).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));
        });
    }

    @Test
    public void testFixForHHH7842DoesNotBreakOldBehavior() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            assertOnlyOneSelect(session.createCriteria(.class).setFetchMode("kittens", FetchMode.JOIN).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));
        });
    }

    @Entity(name = "Cat")
    public static class Cat {
        @Id
        public Integer catId;

        public String name;

        @OneToMany(mappedBy = "cat")
        public Set<CriteriaAliasFetchTest.Kitten> kittens = new HashSet<>();
    }

    @Entity(name = "Kitten")
    public static class Kitten {
        @Id
        public Integer kittenId;

        public String name;

        @ManyToOne(fetch = FetchType.LAZY)
        public CriteriaAliasFetchTest.Cat cat;
    }
}

