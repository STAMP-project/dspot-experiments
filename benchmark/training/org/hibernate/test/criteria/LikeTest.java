/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.criteria;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


public class LikeTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testLike() {
        Session session = openSession();
        Transaction tx = session.beginTransaction();
        TestObject obj = new TestObject();
        String uniq = "uniq" + (System.currentTimeMillis());
        obj.setText((("XyZ " + uniq) + " blablabla"));
        session.save(obj);
        session.flush();
        tx.commit();
        session.close();
        String pattern = ("XyZ " + uniq) + "%";
        // retrieve object - case sensitive - works ok
        session = openSession();
        tx = session.beginTransaction();
        List objects = session.createCriteria(TestObject.class).add(Restrictions.like("text", pattern)).list();
        Assert.assertEquals(1, objects.size());
        session.clear();
        // retrieve object - case insensitive - works ok
        objects = session.createCriteria(TestObject.class).add(Restrictions.like("text", pattern).ignoreCase()).list();
        Assert.assertEquals(1, objects.size());
        session.clear();
        if (((!((getDialect()) instanceof MySQLDialect)) && (!((getDialect()) instanceof PostgreSQLDialect))) && (!((getDialect()) instanceof PostgreSQL81Dialect))) {
            // retrieve object - case insensitive via custom expression - works
            // ok
            objects = session.createCriteria(TestObject.class).add(StringExpression.stringExpression("text", pattern, true)).list();
            Assert.assertEquals(1, objects.size());
            session.clear();
            // retrieve object - case sensitive via custom expression - not
            // working
            objects = session.createCriteria(TestObject.class).add(StringExpression.stringExpression("text", pattern, false)).list();
            Assert.assertEquals(1, objects.size());
        }
        tx.rollback();
        session.close();
    }
}

