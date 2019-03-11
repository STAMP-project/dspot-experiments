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
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.testing.SkipForDialect;
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
@SkipForDialect(value = MySQL5Dialect.class, comment = "BLOB/TEXT column 'id' used in key specification without a key length")
@SkipForDialect(value = Oracle9iDialect.class, comment = "ORA-02329: column of datatype LOB cannot be unique or a primary key")
public class PrimitiveByteArrayIdTest extends BaseCoreFunctionalTestCase {
    /**
     * Removes two records from database.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-8999")
    public void testMultipleDeletions() {
        Session s = openSession();
        s.getTransaction().begin();
        Query query = s.createQuery("SELECT s FROM PrimitiveByteArrayIdTest$DemoEntity s");
        List results = query.list();
        s.delete(results.get(0));
        s.delete(results.get(1));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        query = s.createQuery("SELECT s FROM PrimitiveByteArrayIdTest$DemoEntity s");
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
        Query query = s.createQuery("SELECT s FROM PrimitiveByteArrayIdTest$DemoEntity s");
        List<PrimitiveByteArrayIdTest.DemoEntity> results = ((List<PrimitiveByteArrayIdTest.DemoEntity>) (query.list()));
        results.get(0).name = "Different 0";
        results.get(1).name = "Different 1";
        final String lastResultName = results.get(0).name;
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        query = s.createQuery("SELECT s FROM PrimitiveByteArrayIdTest$DemoEntity s");
        results = ((List<PrimitiveByteArrayIdTest.DemoEntity>) (query.list()));
        final Set<String> names = new HashSet<String>();
        for (PrimitiveByteArrayIdTest.DemoEntity entity : results) {
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
        public byte[] id;

        public String name;
    }
}

