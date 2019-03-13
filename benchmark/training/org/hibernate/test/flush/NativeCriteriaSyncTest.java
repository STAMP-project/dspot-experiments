/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.flush;


import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.test.hql.SimpleEntityWithAssociation;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Etienne Miret
 */
public class NativeCriteriaSyncTest extends BaseCoreFunctionalTestCase {
    /**
     * Tests that the join table of a many-to-many relationship is properly flushed before making a related Criteria
     * query.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-3813")
    public void test() {
        final SimpleEntityWithAssociation e1 = new SimpleEntityWithAssociation("e1");
        final SimpleEntityWithAssociation e2 = new SimpleEntityWithAssociation("e2");
        e1.getManyToManyAssociatedEntities().add(e2);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.save(e1);
            final Criteria criteria = session.createCriteria(.class);
            criteria.createCriteria("manyToManyAssociatedEntities").add(Restrictions.eq("name", "e2"));
            assertEquals(1, criteria.list().size());
        });
    }
}

