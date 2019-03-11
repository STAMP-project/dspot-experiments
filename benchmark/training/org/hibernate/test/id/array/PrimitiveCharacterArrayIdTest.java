/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id.array;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Piotr Krauzowicz <p.krauzowicz@visiona.pl>
 * @author Gail Badner
 */
public class PrimitiveCharacterArrayIdTest extends BaseCoreFunctionalTestCase {
    /**
     * Removes two records from database.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-8999")
    public void testMultipleDeletions() {
        Session s = openSession();
        s.getTransaction().begin();
        Query query = s.createQuery("SELECT s FROM PrimitiveCharacterArrayIdTest$DemoEntity s");
        List results = query.list();
        s.delete(results.get(0));
        s.delete(results.get(1));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        query = s.createQuery("SELECT s FROM PrimitiveCharacterArrayIdTest$DemoEntity s");
        Assert.assertEquals(1, query.list().size());
        s.getTransaction().commit();
        s.close();
    }

    /**
     * Updates two records from database.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-8999")
    public void testMultipleUpdates() {
        Session s = openSession();
        s.getTransaction().begin();
        Query query = s.createQuery("SELECT s FROM PrimitiveCharacterArrayIdTest$DemoEntity s");
        List<PrimitiveCharacterArrayIdTest.DemoEntity> results = ((List<PrimitiveCharacterArrayIdTest.DemoEntity>) (query.list()));
        results.get(0).name = "Different 0";
        results.get(1).name = "Different 1";
        final String lastResultName = results.get(0).name;
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        query = s.createQuery("SELECT s FROM PrimitiveCharacterArrayIdTest$DemoEntity s");
        results = ((List<PrimitiveCharacterArrayIdTest.DemoEntity>) (query.list()));
        final Set<String> names = new HashSet<String>();
        for (PrimitiveCharacterArrayIdTest.DemoEntity entity : results) {
            names.add(entity.name);
        }
        Assert.assertTrue(names.contains("Different 0"));
        Assert.assertTrue(names.contains("Different 1"));
        Assert.assertTrue(names.contains(lastResultName));
        s.getTransaction().commit();
        s.close();
    }

    @Entity
    @Table(name = "DemoEntity")
    public static class DemoEntity {
        @Id
        public char[] id;

        public String name;
    }
}

