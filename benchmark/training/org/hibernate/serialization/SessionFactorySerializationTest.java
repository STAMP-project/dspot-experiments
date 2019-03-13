/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.serialization;


import AvailableSettings.SESSION_FACTORY_NAME;
import AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI;
import SessionFactoryRegistry.INSTANCE;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.util.SerializationHelper;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.type.SerializationException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class SessionFactorySerializationTest extends BaseUnitTestCase {
    public static final String NAME = "mySF";

    @Test
    public void testNamedSessionFactorySerialization() throws Exception {
        Configuration cfg = new Configuration().setProperty(SESSION_FACTORY_NAME, SessionFactorySerializationTest.NAME).setProperty(SESSION_FACTORY_NAME_IS_JNDI, "false");// default is true

        SessionFactory factory = cfg.buildSessionFactory();
        // we need to do some tricking here so that Hibernate thinks the deserialization happens in a
        // different VM
        String uuid = getUuid();
        // deregister under this uuid...
        INSTANCE.removeSessionFactory(uuid, SessionFactorySerializationTest.NAME, false, null);
        // and then register under a different uuid...
        INSTANCE.addSessionFactory("some-other-uuid", SessionFactorySerializationTest.NAME, false, factory, null);
        SessionFactory factory2 = ((SessionFactory) (SerializationHelper.clone(factory)));
        Assert.assertSame(factory, factory2);
        INSTANCE.removeSessionFactory("some-other-uuid", SessionFactorySerializationTest.NAME, false, null);
        factory.close();
        Assert.assertFalse(INSTANCE.hasRegistrations());
    }

    @Test
    public void testUnNamedSessionFactorySerialization() throws Exception {
        // IMPL NOTE : this test is a control to testNamedSessionFactorySerialization
        // here, the test should fail based just on attempted uuid resolution
        Configuration cfg = new Configuration().setProperty(SESSION_FACTORY_NAME_IS_JNDI, "false");// default is true

        SessionFactory factory = cfg.buildSessionFactory();
        // we need to do some tricking here so that Hibernate thinks the deserialization happens in a
        // different VM
        String uuid = getUuid();
        // deregister under this uuid...
        INSTANCE.removeSessionFactory(uuid, null, false, null);
        // and then register under a different uuid...
        INSTANCE.addSessionFactory("some-other-uuid", null, false, factory, null);
        try {
            SerializationHelper.clone(factory);
            Assert.fail("Expecting an error");
        } catch (SerializationException expected) {
        }
        INSTANCE.removeSessionFactory("some-other-uuid", null, false, null);
        factory.close();
        Assert.assertFalse(INSTANCE.hasRegistrations());
    }
}

