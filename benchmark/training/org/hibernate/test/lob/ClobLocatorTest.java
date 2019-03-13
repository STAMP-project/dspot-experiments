/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lob;


import DialectChecks.SupportsExpectedLobUsagePattern;
import DialectChecks.SupportsUnboundedLobLocatorMaterializationCheck;
import LockOptions.UPGRADE;
import org.hibernate.Session;
import org.hibernate.dialect.SybaseASE157Dialect;
import org.hibernate.dialect.TeradataDialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests lazy materialization of data mapped by
 * {@link org.hibernate.type.ClobType} as well as bounded and unbounded
 * materialization and mutation.
 *
 * @author Steve Ebersole
 */
@RequiresDialectFeature(value = SupportsExpectedLobUsagePattern.class, comment = "database/driver does not support expected LOB usage pattern")
public class ClobLocatorTest extends BaseCoreFunctionalTestCase {
    private static final int CLOB_SIZE = 10000;

    @Test
    @SkipForDialect(value = TeradataDialect.class, jiraKey = "HHH-6637", comment = "Teradata requires locator to be used in same session where it was created/retrieved")
    public void testBoundedClobLocatorAccess() throws Throwable {
        String original = ClobLocatorTest.buildString(ClobLocatorTest.CLOB_SIZE, 'x');
        String changed = ClobLocatorTest.buildString(ClobLocatorTest.CLOB_SIZE, 'y');
        String empty = "";
        Session s = openSession();
        s.beginTransaction();
        LobHolder entity = new LobHolder();
        entity.setClobLocator(s.getLobHelper().createClob(original));
        s.save(entity);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = s.get(LobHolder.class, entity.getId());
        Assert.assertEquals(ClobLocatorTest.CLOB_SIZE, entity.getClobLocator().length());
        Assert.assertEquals(original, ClobLocatorTest.extractData(entity.getClobLocator()));
        s.getTransaction().commit();
        s.close();
        // test mutation via setting the new clob data...
        if (getDialect().supportsLobValueChangePropogation()) {
            s = openSession();
            s.beginTransaction();
            entity = ((LobHolder) (s.byId(LobHolder.class).with(UPGRADE).load(entity.getId())));
            entity.getClobLocator().truncate(1);
            entity.getClobLocator().setString(1, changed);
            s.getTransaction().commit();
            s.close();
            s = openSession();
            s.beginTransaction();
            entity = ((LobHolder) (s.byId(LobHolder.class).with(UPGRADE).load(entity.getId())));
            Assert.assertNotNull(entity.getClobLocator());
            Assert.assertEquals(ClobLocatorTest.CLOB_SIZE, entity.getClobLocator().length());
            Assert.assertEquals(changed, ClobLocatorTest.extractData(entity.getClobLocator()));
            entity.getClobLocator().truncate(1);
            entity.getClobLocator().setString(1, original);
            s.getTransaction().commit();
            s.close();
        }
        // test mutation via supplying a new clob locator instance...
        s = openSession();
        s.beginTransaction();
        entity = ((LobHolder) (s.byId(LobHolder.class).with(UPGRADE).load(entity.getId())));
        Assert.assertNotNull(entity.getClobLocator());
        Assert.assertEquals(ClobLocatorTest.CLOB_SIZE, entity.getClobLocator().length());
        Assert.assertEquals(original, ClobLocatorTest.extractData(entity.getClobLocator()));
        entity.setClobLocator(s.getLobHelper().createClob(changed));
        s.getTransaction().commit();
        s.close();
        // test empty clob
        if (!((getDialect()) instanceof SybaseASE157Dialect)) {
            // Skip for Sybase. HHH-6425
            s = openSession();
            s.beginTransaction();
            entity = s.get(LobHolder.class, entity.getId());
            Assert.assertEquals(ClobLocatorTest.CLOB_SIZE, entity.getClobLocator().length());
            Assert.assertEquals(changed, ClobLocatorTest.extractData(entity.getClobLocator()));
            entity.setClobLocator(s.getLobHelper().createClob(empty));
            s.getTransaction().commit();
            s.close();
            s = openSession();
            s.beginTransaction();
            entity = s.get(LobHolder.class, entity.getId());
            if ((entity.getClobLocator()) != null) {
                Assert.assertEquals(empty.length(), entity.getClobLocator().length());
                Assert.assertEquals(empty, ClobLocatorTest.extractData(entity.getClobLocator()));
            }
            s.delete(entity);
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    @RequiresDialectFeature(value = SupportsUnboundedLobLocatorMaterializationCheck.class, comment = "database/driver does not support materializing a LOB locator outside the owning transaction")
    public void testUnboundedClobLocatorAccess() throws Throwable {
        // Note: unbounded mutation of the underlying lob data is completely
        // unsupported; most databases would not allow such a construct anyway.
        // Thus here we are only testing materialization...
        String original = ClobLocatorTest.buildString(ClobLocatorTest.CLOB_SIZE, 'x');
        Session s = openSession();
        s.beginTransaction();
        LobHolder entity = new LobHolder();
        entity.setClobLocator(s.getLobHelper().createClob(original));
        s.save(entity);
        s.getTransaction().commit();
        s.close();
        // load the entity with the clob locator, and close the session/transaction;
        // at that point it is unbounded...
        s = openSession();
        s.beginTransaction();
        entity = s.get(LobHolder.class, entity.getId());
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(ClobLocatorTest.CLOB_SIZE, entity.getClobLocator().length());
        Assert.assertEquals(original, ClobLocatorTest.extractData(entity.getClobLocator()));
        s = openSession();
        s.beginTransaction();
        s.delete(entity);
        s.getTransaction().commit();
        s.close();
    }
}

