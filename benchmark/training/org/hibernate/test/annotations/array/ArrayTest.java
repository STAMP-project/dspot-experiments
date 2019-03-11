/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.array;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

import static org.hibernate.test.annotations.array.Contest.Month.December;
import static org.hibernate.test.annotations.array.Contest.Month.January;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class ArrayTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToMany() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Competitor c1 = new Competitor();
        c1.setName("Renault");
        Competitor c2 = new Competitor();
        c2.setName("Ferrari");
        Contest contest = new Contest();
        contest.setResults(new Competitor[]{ c1, c2 });
        contest.setHeldIn(new Contest.Month[]{ January, December });
        s.persist(contest);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        contest = ((Contest) (s.get(Contest.class, contest.getId())));
        Assert.assertNotNull(contest);
        Assert.assertNotNull(contest.getResults());
        Assert.assertEquals(2, contest.getResults().length);
        Assert.assertEquals(c2.getName(), contest.getResults()[1].getName());
        Assert.assertEquals(2, contest.getHeldIn().length);
        Assert.assertEquals(January, contest.getHeldIn()[0]);
        tx.commit();
        s.close();
    }
}

