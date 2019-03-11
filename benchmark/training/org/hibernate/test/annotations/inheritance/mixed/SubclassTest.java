/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.inheritance.mixed;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class SubclassTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testDefault() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        File doc = new Document("Enron Stuff To Shred", 1000);
        Folder folder = new Folder("Enron");
        s.persist(doc);
        s.persist(folder);
        try {
            tx.commit();
        } catch (SQLGrammarException e) {
            System.err.println(e.getSQLException().getNextException());
        }
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        List result = s.createCriteria(File.class).list();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        File f2 = ((File) (result.get(0)));
        checkClassType(f2, doc, folder);
        f2 = ((File) (result.get(1)));
        checkClassType(f2, doc, folder);
        s.delete(result.get(0));
        s.delete(result.get(1));
        tx.commit();
        s.close();
    }
}

