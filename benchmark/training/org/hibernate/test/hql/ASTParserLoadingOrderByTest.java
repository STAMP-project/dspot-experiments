/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests AST parser processing of ORDER BY clauses.
 *
 * @author Gail Badner
 */
public class ASTParserLoadingOrderByTest extends BaseCoreFunctionalTestCase {
    StateProvince stateProvince;

    private Zoo zoo1;

    private Zoo zoo2;

    private Zoo zoo3;

    private Zoo zoo4;

    Set<Zoo> zoosWithSameName;

    Set<Zoo> zoosWithSameAddress;

    Mammal zoo1Mammal1;

    Mammal zoo1Mammal2;

    Human zoo2Director1;

    Human zoo2Director2;

    @Test
    public void testOrderByOnJoinedSubclassPropertyWhoseColumnIsNotInDrivingTable() {
        // this is simply a syntax check
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.createQuery("from Human h order by h.bodyWeight").list();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testOrderByNoSelectAliasRef() {
        createData();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // ordered by name, address:
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        checkTestOrderByResults(s.createQuery("select name, address from Zoo order by name, address").list(), zoo2, zoo4, zoo3, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z.name, z.address from Zoo z order by z.name, z.address").list(), zoo2, zoo4, zoo3, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z2.name, z2.address from Zoo z2 where z2.name in ( select name from Zoo ) order by z2.name, z2.address").list(), zoo2, zoo4, zoo3, zoo1, null);
        // using ASC
        checkTestOrderByResults(s.createQuery("select name, address from Zoo order by name ASC, address ASC").list(), zoo2, zoo4, zoo3, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z.name, z.address from Zoo z order by z.name ASC, z.address ASC").list(), zoo2, zoo4, zoo3, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z2.name, z2.address from Zoo z2 where z2.name in ( select name from Zoo ) order by z2.name ASC, z2.address ASC").list(), zoo2, zoo4, zoo3, zoo1, null);
        // ordered by address, name:
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        checkTestOrderByResults(s.createQuery("select z.name, z.address from Zoo z order by z.address, z.name").list(), zoo3, zoo4, zoo2, zoo1, null);
        checkTestOrderByResults(s.createQuery("select name, address from Zoo order by address, name").list(), zoo3, zoo4, zoo2, zoo1, null);
        // ordered by address:
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // unordered:
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        checkTestOrderByResults(s.createQuery("select z.name, z.address from Zoo z order by z.address").list(), zoo3, zoo4, null, null, zoosWithSameAddress);
        checkTestOrderByResults(s.createQuery("select name, address from Zoo order by address").list(), zoo3, zoo4, null, null, zoosWithSameAddress);
        // ordered by name:
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // unordered:
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        checkTestOrderByResults(s.createQuery("select z.name, z.address from Zoo z order by z.name").list(), zoo2, zoo4, null, null, zoosWithSameName);
        checkTestOrderByResults(s.createQuery("select name, address from Zoo order by name").list(), zoo2, zoo4, null, null, zoosWithSameName);
        t.commit();
        s.close();
        cleanupData();
    }

    @Test
    @FailureExpected(jiraKey = "unknown")
    public void testOrderByComponentDescNoSelectAliasRef() {
        createData();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // ordered by address DESC, name DESC:
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        checkTestOrderByResults(s.createQuery("select z.name, z.address from Zoo z order by z.address DESC, z.name DESC").list(), zoo1, zoo2, zoo4, zoo3, null);
        checkTestOrderByResults(s.createQuery("select name, address from Zoo order by address DESC, name DESC").list(), zoo1, zoo2, zoo4, zoo3, null);
        t.commit();
        s.close();
        cleanupData();
    }

    @Test
    public void testOrderBySelectAliasRef() {
        createData();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // ordered by name, address:
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        checkTestOrderByResults(s.createQuery("select z2.name as zname, z2.address as zooAddress from Zoo z2 where z2.name in ( select name from Zoo ) order by zname, zooAddress").list(), zoo2, zoo4, zoo3, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z.name as name, z.address as address from Zoo z order by name, address").list(), zoo2, zoo4, zoo3, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z.name as zooName, z.address as zooAddress from Zoo z order by zooName, zooAddress").list(), zoo2, zoo4, zoo3, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z.name, z.address as name from Zoo z order by z.name, name").list(), zoo2, zoo4, zoo3, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z.name, z.address as name from Zoo z order by z.name, name").list(), zoo2, zoo4, zoo3, zoo1, null);
        // using ASC
        checkTestOrderByResults(s.createQuery("select z2.name as zname, z2.address as zooAddress from Zoo z2 where z2.name in ( select name from Zoo ) order by zname ASC, zooAddress ASC").list(), zoo2, zoo4, zoo3, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z.name as name, z.address as address from Zoo z order by name ASC, address ASC").list(), zoo2, zoo4, zoo3, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z.name as zooName, z.address as zooAddress from Zoo z order by zooName ASC, zooAddress ASC").list(), zoo2, zoo4, zoo3, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z.name, z.address as name from Zoo z order by z.name ASC, name ASC").list(), zoo2, zoo4, zoo3, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z.name, z.address as name from Zoo z order by z.name ASC, name ASC").list(), zoo2, zoo4, zoo3, zoo1, null);
        // ordered by address, name:
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        checkTestOrderByResults(s.createQuery("select z.name as address, z.address as name from Zoo z order by name, address").list(), zoo3, zoo4, zoo2, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z.name, z.address as name from Zoo z order by name, z.name").list(), zoo3, zoo4, zoo2, zoo1, null);
        // using ASC
        checkTestOrderByResults(s.createQuery("select z.name as address, z.address as name from Zoo z order by name ASC, address ASC").list(), zoo3, zoo4, zoo2, zoo1, null);
        checkTestOrderByResults(s.createQuery("select z.name, z.address as name from Zoo z order by name ASC, z.name ASC").list(), zoo3, zoo4, zoo2, zoo1, null);
        // ordered by address:
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // unordered:
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        checkTestOrderByResults(s.createQuery("select z.name as zooName, z.address as zooAddress from Zoo z order by zooAddress").list(), zoo3, zoo4, null, null, zoosWithSameAddress);
        checkTestOrderByResults(s.createQuery("select z.name as zooName, z.address as name from Zoo z order by name").list(), zoo3, zoo4, null, null, zoosWithSameAddress);
        // ordered by name:
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // unordered:
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        checkTestOrderByResults(s.createQuery("select z.name as zooName, z.address as zooAddress from Zoo z order by zooName").list(), zoo2, zoo4, null, null, zoosWithSameName);
        checkTestOrderByResults(s.createQuery("select z.name as address, z.address as name from Zoo z order by address").list(), zoo2, zoo4, null, null, zoosWithSameName);
        t.commit();
        s.close();
        cleanupData();
    }

    @Test
    @FailureExpected(jiraKey = "unknown")
    public void testOrderByComponentDescSelectAliasRefFailureExpected() {
        createData();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // ordered by address desc, name desc:
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        // using DESC
        checkTestOrderByResults(s.createQuery("select z.name as zooName, z.address as zooAddress from Zoo z order by zooAddress DESC, zooName DESC").list(), zoo1, zoo2, zoo4, zoo3, null);
        t.commit();
        s.close();
        cleanupData();
    }

    @Test
    public void testOrderByEntityWithFetchJoinedCollection() {
        createData();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // ordered by address desc, name desc:
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        // using DESC
        List list = s.createQuery("from Zoo z join fetch z.mammals").list();
        t.commit();
        s.close();
        cleanupData();
    }

    @Test
    public void testOrderBySelectNewArgAliasRef() {
        createData();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // ordered by name, address:
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        List list = s.createQuery("select new Zoo( z.name as zname, z.address as zaddress) from Zoo z order by zname, zaddress").list();
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(zoo2, list.get(0));
        Assert.assertEquals(zoo4, list.get(1));
        Assert.assertEquals(zoo3, list.get(2));
        Assert.assertEquals(zoo1, list.get(3));
        // ordered by address, name:
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        list = s.createQuery("select new Zoo( z.name as zname, z.address as zaddress) from Zoo z order by zaddress, zname").list();
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(zoo3, list.get(0));
        Assert.assertEquals(zoo4, list.get(1));
        Assert.assertEquals(zoo2, list.get(2));
        Assert.assertEquals(zoo1, list.get(3));
        t.commit();
        s.close();
        cleanupData();
    }

    @Test(timeout = (5 * 60) * 1000)
    public void testOrderBySelectNewMapArgAliasRef() {
        createData();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // ordered by name, address:
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        List list = s.createQuery("select new map( z.name as zname, z.address as zaddress ) from Zoo z left join z.mammals m order by zname, zaddress").list();
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(zoo2.getName(), ((Map) (list.get(0))).get("zname"));
        Assert.assertEquals(zoo2.getAddress(), ((Map) (list.get(0))).get("zaddress"));
        Assert.assertEquals(zoo4.getName(), ((Map) (list.get(1))).get("zname"));
        Assert.assertEquals(zoo4.getAddress(), ((Map) (list.get(1))).get("zaddress"));
        Assert.assertEquals(zoo3.getName(), ((Map) (list.get(2))).get("zname"));
        Assert.assertEquals(zoo3.getAddress(), ((Map) (list.get(2))).get("zaddress"));
        Assert.assertEquals(zoo1.getName(), ((Map) (list.get(3))).get("zname"));
        Assert.assertEquals(zoo1.getAddress(), ((Map) (list.get(3))).get("zaddress"));
        // ordered by address, name:
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        list = s.createQuery("select new map( z.name as zname, z.address as zaddress ) from Zoo z left join z.mammals m order by zaddress, zname").list();
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(zoo3.getName(), ((Map) (list.get(0))).get("zname"));
        Assert.assertEquals(zoo3.getAddress(), ((Map) (list.get(0))).get("zaddress"));
        Assert.assertEquals(zoo4.getName(), ((Map) (list.get(1))).get("zname"));
        Assert.assertEquals(zoo4.getAddress(), ((Map) (list.get(1))).get("zaddress"));
        Assert.assertEquals(zoo2.getName(), ((Map) (list.get(2))).get("zname"));
        Assert.assertEquals(zoo2.getAddress(), ((Map) (list.get(2))).get("zaddress"));
        Assert.assertEquals(zoo1.getName(), ((Map) (list.get(3))).get("zname"));
        Assert.assertEquals(zoo1.getAddress(), ((Map) (list.get(3))).get("zaddress"));
        t.commit();
        s.close();
        cleanupData();
    }

    @Test
    public void testOrderByAggregatedArgAliasRef() {
        createData();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // ordered by name, address:
        // zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
        // zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
        // zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
        // zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
        List list = s.createQuery("select z.name as zname, count(*) as cnt from Zoo z group by z.name order by cnt desc, zname").list();
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(zoo3.getName(), ((Object[]) (list.get(0)))[0]);
        Assert.assertEquals(Long.valueOf(2), ((Object[]) (list.get(0)))[1]);
        Assert.assertEquals(zoo2.getName(), ((Object[]) (list.get(1)))[0]);
        Assert.assertEquals(Long.valueOf(1), ((Object[]) (list.get(1)))[1]);
        Assert.assertEquals(zoo4.getName(), ((Object[]) (list.get(2)))[0]);
        Assert.assertEquals(Long.valueOf(1), ((Object[]) (list.get(2)))[1]);
        t.commit();
        s.close();
        cleanupData();
    }
}

