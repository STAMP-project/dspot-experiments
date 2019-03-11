/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.connections;


import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test originally developed to verify and fix HHH-5550
 *
 * @author Steve Ebersole
 */
public class HibernateCreateBlobFailedCase extends BaseCoreFunctionalTestCase {
    @Test
    public void testLobCreation() throws SQLException {
        Session session = sessionFactory().getCurrentSession();
        session.beginTransaction();
        Blob blob = Hibernate.getLobCreator(session).createBlob(new byte[]{  });
        blob.free();
        Clob clob = Hibernate.getLobCreator(session).createClob("Steve");
        clob.free();
        session.getTransaction().commit();
        Assert.assertFalse(session.isOpen());
    }
}

