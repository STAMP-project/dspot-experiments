/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id: AggressiveReleaseTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
 */
package org.hibernate.test.connections;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.util.SerializationHelper;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.RequiresDialect;
import org.junit.Assert;
import org.junit.Test;


/**
 * Implementation of AggressiveReleaseTest.
 *
 * @author Steve Ebersole
 */
@RequiresDialect(H2Dialect.class)
public class AggressiveReleaseTest extends ConnectionManagementTestCase {
    // Some additional tests specifically for the aggressive-release functionality...
    @Test
    public void testSerializationOnAfterStatementAggressiveRelease() throws Throwable {
        prepare();
        try {
            Session s = getSessionUnderTest();
            Silly silly = new Silly("silly");
            s.save(silly);
            // this should cause the CM to obtain a connection, and then release it
            s.flush();
            // We should be able to serialize the session at this point...
            SerializationHelper.serialize(s);
            s.delete(silly);
            s.flush();
            release(s);
        } finally {
            done();
        }
    }

    @Test
    public void testSerializationFailsOnAfterStatementAggressiveReleaseWithOpenResources() throws Throwable {
        prepare();
        Session s = getSessionUnderTest();
        Silly silly = new Silly("silly");
        s.save(silly);
        // this should cause the CM to obtain a connection, and then release it
        s.flush();
        // both scroll() and iterate() cause batching to hold on
        // to resources, which should make aggressive-release not release
        // the connection (and thus cause serialization to fail)
        ScrollableResults sr = s.createQuery("from Silly").scroll();
        try {
            SerializationHelper.serialize(s);
            Assert.fail("Serialization allowed on connected session; or aggressive release released connection with open resources");
        } catch (IllegalStateException e) {
            // expected behavior
        }
        // getting the first row only because SybaseASE15Dialect throws NullPointerException
        // if data is not read before closing the ResultSet
        sr.next();
        // Closing the ScrollableResults does currently force batching to
        // aggressively release the connection
        sr.close();
        SerializationHelper.serialize(s);
        s.delete(silly);
        s.flush();
        release(s);
        done();
    }

    @Test
    public void testQueryIteration() throws Throwable {
        prepare();
        Session s = getSessionUnderTest();
        Silly silly = new Silly("silly");
        s.save(silly);
        s.flush();
        Iterator itr = s.createQuery("from Silly").iterate();
        Assert.assertTrue(itr.hasNext());
        Silly silly2 = ((Silly) (itr.next()));
        Assert.assertEquals(silly, silly2);
        Hibernate.close(itr);
        itr = s.createQuery("from Silly").iterate();
        Iterator itr2 = s.createQuery("from Silly where name = 'silly'").iterate();
        Assert.assertTrue(itr.hasNext());
        Assert.assertEquals(silly, itr.next());
        Assert.assertTrue(itr2.hasNext());
        Assert.assertEquals(silly, itr2.next());
        Hibernate.close(itr);
        Hibernate.close(itr2);
        s.delete(silly);
        s.flush();
        release(s);
        done();
    }

    @Test
    public void testQueryScrolling() throws Throwable {
        prepare();
        Session s = getSessionUnderTest();
        Silly silly = new Silly("silly");
        s.save(silly);
        s.flush();
        ScrollableResults sr = s.createQuery("from Silly").scroll();
        Assert.assertTrue(sr.next());
        Silly silly2 = ((Silly) (sr.get(0)));
        Assert.assertEquals(silly, silly2);
        sr.close();
        sr = s.createQuery("from Silly").scroll();
        ScrollableResults sr2 = s.createQuery("from Silly where name = 'silly'").scroll();
        Assert.assertTrue(sr.next());
        Assert.assertEquals(silly, sr.get(0));
        Assert.assertTrue(sr2.next());
        Assert.assertEquals(silly, sr2.get(0));
        sr.close();
        sr2.close();
        s.delete(silly);
        s.flush();
        release(s);
        done();
    }

    @Test
    public void testSuppliedConnection() throws Throwable {
        prepare();
        Connection originalConnection = sessionFactory().getServiceRegistry().getService(ConnectionProvider.class).getConnection();
        Session session = sessionFactory().withOptions().connection(originalConnection).openSession();
        Silly silly = new Silly("silly");
        session.save(silly);
        // this will cause the connection manager to cycle through the aggressive release logic;
        // it should not release the connection since we explicitly suplied it ourselves.
        session.flush();
        Assert.assertTrue(session.isConnected());
        session.delete(silly);
        session.flush();
        release(session);
        done();
        sessionFactory().getServiceRegistry().getService(ConnectionProvider.class).closeConnection(originalConnection);
    }

    @Test
    public void testConnectionMaintanenceDuringFlush() throws Throwable {
        final Statistics statistics = sessionFactory().getStatistics();
        prepare();
        Session s = getSessionUnderTest();
        List<Silly> entities = new ArrayList<Silly>();
        for (int i = 0; i < 10; i++) {
            Other other = new Other(("other-" + i));
            Silly silly = new Silly(("silly-" + i), other);
            entities.add(silly);
            s.save(silly);
        }
        s.flush();
        for (Silly silly : entities) {
            silly.setName(("new-" + (silly.getName())));
            silly.getOther().setName(("new-" + (silly.getOther().getName())));
        }
        long initialCount = statistics.getConnectCount();
        s.flush();
        Assert.assertEquals("connection not maintained through flush", (initialCount + 1), statistics.getConnectCount());
        s.createQuery("delete from Silly").executeUpdate();
        s.createQuery("delete from Other").executeUpdate();
        s.getTransaction().commit();
        release(s);
        done();
    }
}

