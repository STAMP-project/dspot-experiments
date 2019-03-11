/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.connections;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.internal.util.SerializationHelper;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Common test cases relating to session management and how the sessions
 * manages its underlying jdbc connection across different config
 * scenarios.  The different config scenarios are controlled by the
 * individual test subclasses.
 * <p/>
 * In general, all the tests required are defined here in templated fashion.
 * Subclassed then override various hook methods specific to their given
 * scneario being tested.
 *
 * @author Steve Ebersole
 */
public abstract class ConnectionManagementTestCase extends BaseNonConfigCoreFunctionalTestCase {
    // tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Tests to validate that a session holding JDBC resources will not
     * be allowed to serialize.
     */
    @Test
    public final void testConnectedSerialization() throws Throwable {
        prepare();
        Session sessionUnderTest = getSessionUnderTest();
        // force the connection to be retained
        sessionUnderTest.createQuery("from Silly").scroll();
        try {
            SerializationHelper.serialize(sessionUnderTest);
            Assert.fail("Serialization of connected session allowed!");
        } catch (IllegalStateException e) {
            // expected behaviour
        } finally {
            release(sessionUnderTest);
            done();
        }
    }

    /**
     * Tests to validate that a session holding JDBC resources will not
     * be allowed to serialize.
     */
    @Test
    public final void testEnabledFilterSerialization() throws Throwable {
        prepare();
        Session sessionUnderTest = getSessionUnderTest();
        sessionUnderTest.enableFilter("nameIsNull");
        Assert.assertNotNull(sessionUnderTest.getEnabledFilter("nameIsNull"));
        disconnect(sessionUnderTest);
        Assert.assertNotNull(sessionUnderTest.getEnabledFilter("nameIsNull"));
        byte[] bytes = SerializationHelper.serialize(sessionUnderTest);
        checkSerializedState(sessionUnderTest);
        Assert.assertNotNull(sessionUnderTest.getEnabledFilter("nameIsNull"));
        reconnect(sessionUnderTest);
        Assert.assertNotNull(sessionUnderTest.getEnabledFilter("nameIsNull"));
        disconnect(sessionUnderTest);
        Assert.assertNotNull(sessionUnderTest.getEnabledFilter("nameIsNull"));
        Session s2 = ((Session) (SerializationHelper.deserialize(bytes)));
        checkDeserializedState(s2);
        Assert.assertNotNull(sessionUnderTest.getEnabledFilter("nameIsNull"));
        reconnect(s2);
        Assert.assertNotNull(sessionUnderTest.getEnabledFilter("nameIsNull"));
        disconnect(s2);
        Assert.assertNotNull(sessionUnderTest.getEnabledFilter("nameIsNull"));
        reconnect(s2);
        Assert.assertNotNull(sessionUnderTest.getEnabledFilter("nameIsNull"));
        release(sessionUnderTest);
        release(s2);
        done();
    }

    /**
     * Test that a session which has been manually disconnected will be allowed
     * to serialize.
     */
    @Test
    public final void testManualDisconnectedSerialization() throws Throwable {
        prepare();
        Session sessionUnderTest = getSessionUnderTest();
        disconnect(sessionUnderTest);
        SerializationHelper.serialize(sessionUnderTest);
        checkSerializedState(sessionUnderTest);
        release(sessionUnderTest);
        done();
    }

    /**
     * Test that the legacy manual disconnect()/reconnect() chain works as
     * expected in the given environment.
     */
    @Test
    public final void testManualDisconnectChain() throws Throwable {
        prepare();
        Session sessionUnderTest = getSessionUnderTest();
        disconnect(sessionUnderTest);
        byte[] bytes = SerializationHelper.serialize(sessionUnderTest);
        checkSerializedState(sessionUnderTest);
        Session s2 = ((Session) (SerializationHelper.deserialize(bytes)));
        checkDeserializedState(s2);
        reconnect(s2);
        disconnect(s2);
        reconnect(s2);
        release(sessionUnderTest);
        release(s2);
        done();
    }

    /**
     * Test that the legacy manual disconnect()/reconnect() chain works as
     * expected in the given environment.  Similar to {@link #testManualDisconnectChain}
     * expect that here we force the session to acquire and hold JDBC resources
     * prior to disconnecting.
     */
    @Test
    public final void testManualDisconnectWithOpenResources() throws Throwable {
        prepare();
        Session sessionUnderTest = getSessionUnderTest();
        Silly silly = new Silly("tester");
        sessionUnderTest.save(silly);
        sessionUnderTest.flush();
        sessionUnderTest.createQuery("from Silly").iterate();
        disconnect(sessionUnderTest);
        SerializationHelper.serialize(sessionUnderTest);
        checkSerializedState(sessionUnderTest);
        reconnect(sessionUnderTest);
        sessionUnderTest.createQuery("from Silly").scroll();
        disconnect(sessionUnderTest);
        SerializationHelper.serialize(sessionUnderTest);
        checkSerializedState(sessionUnderTest);
        reconnect(sessionUnderTest);
        sessionUnderTest.delete(silly);
        sessionUnderTest.flush();
        release(sessionUnderTest);
        done();
    }

    /**
     * Test that the basic session usage template works in all environment
     * scenarios.
     */
    @Test
    public void testBasicSessionUsage() throws Throwable {
        prepare();
        Session s = null;
        Transaction txn = null;
        try {
            s = getSessionUnderTest();
            txn = s.beginTransaction();
            s.createQuery("from Silly").list();
            txn.commit();
        } catch (Throwable t) {
            if (txn != null) {
                try {
                    txn.rollback();
                } catch (Throwable ignore) {
                }
            }
        } finally {
            if ((s != null) && (s.isOpen())) {
                try {
                    s.close();
                } catch (Throwable ignore) {
                }
            }
        }
        done();
    }

    /**
     * Test that session-closed protections work properly in all environments.
     */
    @Test
    public void testSessionClosedProtections() throws Throwable {
        prepare();
        Session s = getSessionUnderTest();
        release(s);
        done();
        Assert.assertFalse(s.isOpen());
        Assert.assertFalse(s.isConnected());
        Assert.assertNotNull(s.getStatistics());
        Assert.assertNotNull(s.toString());
        try {
            s.createQuery("from Silly").list();
            Assert.fail("allowed to create query on closed session");
        } catch (Throwable ignore) {
        }
        try {
            s.getTransaction();
            Assert.fail("allowed to access transaction on closed session");
        } catch (Throwable ignore) {
        }
        try {
            s.close();
            Assert.fail("allowed to close already closed session");
        } catch (Throwable ignore) {
        }
        try {
            s.isDirty();
            Assert.fail("allowed to check dirtiness of closed session");
        } catch (Throwable ignore) {
        }
    }
}

